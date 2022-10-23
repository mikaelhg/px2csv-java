package io.mikael.px2;

import io.mikael.px2.algo.CartesianProduct;
import io.mikael.px2.dto.PxParserState;
import io.mikael.px2.dto.PxHeaderRow;
import io.mikael.px2.dto.RowAccumulator;
import io.mikael.px2.io.LocklessReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.mikael.px2.io.LocklessReader.EOF;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public final class PxParser {

    /* Maximum width, in characters, the string representation of a decimal number can be. */
    public static final int DATA_VALUE_WIDTH = 128;

    private final LocklessReader reader;

    private final CubeWriter writer;

    private final PxParserState state = new PxParserState();

    private RowAccumulator row = new RowAccumulator();

    private final List<PxHeaderRow> headers = new ArrayList<>();

    public PxParser(final LocklessReader reader, final CubeWriter writer) {
        this.writer = writer;
        this.reader = reader;
    }

    private List<String> header(
            final String keyword, final String language, final List<String> subkeys) {
        return headers.stream()
                .filter((h) -> Objects.equals(keyword, h.keyword())
                        && Objects.equals(language, h.language())
                        && Objects.deepEquals(subkeys, h.subkeys()))
                .findFirst().orElseThrow().values();
    }

    private List<String> valueHeader(final String subkey) {
        return this.header("VALUES", "", singletonList(subkey));
    }

    public void parseHeader() throws IOException {
        for (char c = reader.read(); c != EOF; c = reader.read()) {
            final var inQuotes = this.state.quotes % 2 == 1;
            final var inParenthesis = this.state.parenthesisOpen > this.state.parenthesisClose;
            final var inKey = this.state.semicolons == this.state.equals;
            final var inLanguage = inKey && this.state.squareBracketOpen > this.state.squareBracketClose;
            final var inSubkey = inKey && inParenthesis;

            if (c == '"') {
                this.state.quotes += 1;

            } else if ((c == '\n' || c == '\r') && inQuotes) {
                throw new RuntimeException("there can't be newlines inside quoted strings");

            } else if ((c == '\n' || c == '\r') && !inQuotes) {
                continue;

            } else if (c == '[' && inKey && !inQuotes) {
                this.state.squareBracketOpen += 1;

            } else if (c == ']' && inKey && !inQuotes) {
                this.state.squareBracketClose += 1;

            } else if (c == '(' && inKey && !inQuotes) {
                this.state.parenthesisOpen += 1;

            } else if (c == '(' && !inKey && !inQuotes) {
                // TLIST opening quote
                this.state.parenthesisOpen += 1;
                this.row.value.append(c);

            } else if (c == ')' && inKey && !inQuotes) {
                this.state.parenthesisClose += 1;
                this.row.subkeys.add(this.row.subkey.toString());
                this.row.subkey = new StringBuilder();

            } else if (c == ')' && !inKey && !inQuotes) {
                // TLIST closing quote
                this.state.parenthesisClose += 1;
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
                this.state.equals += 1;

            } else if (c == ';' && inKey && !inQuotes) {
                throw new RuntimeException("found a semicolon without a matching equals sign, value terminator without keyword terminator");

            } else if (c == ';' && !inKey && !inQuotes) {
                if (this.row.value.length() > 0) {
                    this.row.values.add(this.row.value.toString());
                }
                this.state.semicolons += 1;
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

    public record DenseStub(List<String> stub, CartesianProduct stubFlattener, int stubWidth) {
    }

    public DenseStub denseStub() {
        final var stub = this.header("STUB", "", emptyList());
        final var stubValues = stub.stream().map(this::valueHeader).toList();
        return new DenseStub(stub, CartesianProduct.of(stubValues), stub.size());
    }

    public CartesianProduct denseHeading() {
        final var heading = this.header("HEADING", "", emptyList());
        final var headingValues = heading.stream().map(this::valueHeader).toList();
        return CartesianProduct.of(headingValues);
    }

    private void parseDataDense() throws IOException {
        final var ds = this.denseStub();
        final var headingFlattener = this.denseHeading();
        final var headingWidth = headingFlattener.permutationCount;

        this.writer.writeHeading(ds.stub, headingFlattener);

        final var buffer = new char[headingWidth * DATA_VALUE_WIDTH];
        final var valueLengths = new int[headingWidth];
        int bufLength = 0;
        int currentValue = 0;

        for (char c = reader.read(); c != EOF; c = reader.read()) {
            final var base = DATA_VALUE_WIDTH * currentValue;
            if (c == '"') {
                continue;

            } else if (c == ' ' || c == '\n' || c == '\r' || c == ';') {
                if (bufLength > 0) {
                    valueLengths[currentValue] = bufLength;
                    bufLength = 0;
                    currentValue += 1;
                }
                if (currentValue == headingWidth) {
                    currentValue = 0;
                    final var currentStubs = ds.stubFlattener.next();
                    this.writer.writeRow(currentStubs, buffer, valueLengths, headingWidth);
                }

            } else {
                buffer[base + bufLength] = c;
                bufLength += 1;

            }
        }

        this.writer.writeFooting();
    }

    private void parseDataSparse() throws IOException {
        throw new RuntimeException("SPARSE PX DATA (KEYS) READING NOT IMPLEMENTED!!!");
    }

    public void parseData() throws IOException {
        if (headers.stream().anyMatch((h) -> "KEYS".equals(h.keyword()))) {
            this.parseDataSparse();
        } else {
            this.parseDataDense();
        }
    }

}