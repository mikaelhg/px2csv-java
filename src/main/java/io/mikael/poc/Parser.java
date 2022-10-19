package io.mikael.poc;

import io.mikael.poc.dto.HeaderParseState;
import io.mikael.poc.dto.PxHeaderRow;
import io.mikael.poc.dto.RowAccumulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

public class Parser {

    protected final StatCubeCsvWriter writer;

    protected final HeaderParseState hps = new HeaderParseState();

    protected RowAccumulator row = new RowAccumulator();

    protected List<PxHeaderRow> headers = new ArrayList<>();

    public List<String> header(
            final String keyword, final String language, final List<String> subkeys)
    {
        return headers.stream()
                .filter((h) -> Objects.equals(keyword, h.keyword())
                        && Objects.equals(language, h.language())
                        && Objects.deepEquals(subkeys, h.subkeys()))
                .findFirst().orElseThrow().values();
    }

    private List<String> valueHeader(final String subkey) {
        return this.header("VALUES", null, singletonList(subkey));
    }

    public Parser(final StatCubeCsvWriter writer) {
        this.writer = writer;
    }

    public void parseHeader(final BufferedReader input) throws IOException {
        int i = -1;
        while ((i = input.read()) != -1) {
            final char c = (char) i;
            final var inQuotes = this.hps.quotes % 2 == 1;
            final var inParenthesis = this.hps.parenthesisOpen > this.hps.parenthesisClose;
            final var inKey = this.hps.semicolons == this.hps.equals;
            final var inLanguage = inKey && this.hps.squareBracketOpen > this.hps.squareBracketClose;
            final var inSubkey = inKey && inParenthesis;

            if (c == '"') {
                this.hps.quotes += 1;

            } else if ((c == '\n' || c == '\r') && inQuotes) {
                throw new RuntimeException("there can't be newlines inside quoted strings");

            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                continue;
                
            } else if (c == '[' && inKey && !inQuotes) {
                this.hps.squareBracketOpen += 1;

            } else if (c == ']' && inKey && !inQuotes) {
                this.hps.squareBracketClose += 1;

            } else if (c == '(' && inKey && !inQuotes) {
                this.hps.parenthesisOpen += 1;

            } else if (c == '(' && !inKey && !inQuotes) {
                // TLIST opening quote
                this.hps.parenthesisOpen += 1;
                this.row.value.append(c);

            } else if (c == ')' && inKey && !inQuotes) {
                this.hps.parenthesisClose += 1;
                this.row.subkeys.add(this.row.subkey.toString());
                this.row.subkey = new StringBuilder();

            } else if (c == ')' && !inKey && !inQuotes) {
                // TLIST closing quote
                this.hps.parenthesisClose += 1;
                this.row.value.append(c);

            } else if (c == ',' && inSubkey && !inQuotes) {
                this.row.subkeys.add(this.row.subkey.toString());
                this.row.subkey = new StringBuilder();

            } else if (c == ',' && !inKey && !inQuotes && !inParenthesis) {
                this.row.values.add(this.row.value.toString());
                this.row.value = new StringBuilder();

            } else if (c == '=' && !inKey && !inQuotes) {
                throw new RuntimeException("found a second equals sign without a matching semicolon, unexpected keyword terminator");

            } else if (c == '=' && inKey && !inQuotes) {
                if (this.row.keyword.toString().equals("DATA")) {
                    return;
                }
                this.hps.equals += 1;

            } else if (c == ';' && inKey && !inQuotes) {
                throw new RuntimeException("found a semicolon without a matching equals sign, value terminator without keyword terminator");

            } else if (c == ';' && !inKey && !inQuotes) {
                if (this.row.value.length() > 0) {
                    this.row.values.add(this.row.value.toString());
                }
                this.hps.semicolons += 1;
                this.headers.add(this.row.toPxHeaderRow());
                this.row = new RowAccumulator();
                continue;

            } else if (inSubkey) {
                this.row.subkey.append(c);
                
            } else if (inLanguage) {
                this.row.language.append(c);

            } else if (inKey) {
                this.row.keyword.append(c);

            } else {
                this.row.value.append(c);

            }
        }
    }

    public void parseDataDense(final BufferedReader input) {
    }

}
