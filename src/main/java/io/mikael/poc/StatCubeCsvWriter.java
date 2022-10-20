package io.mikael.poc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.mikael.poc.Parser.DATA_VALUE_WIDTH;

public class StatCubeCsvWriter {

    private final BufferedWriter out;

    public StatCubeCsvWriter(final BufferedWriter out) {
        this.out = out;
    }

    public void writeHeading(List<String> stub, CartesianProduct headingFlattener) throws IOException {
        out.write("\"");
        out.write(String.join("\";\"", stub));
        out.write("\";\"");
        out.write(Arrays.stream(headingFlattener.all())
                .map((ss) -> String.join(" ", ss))
                .collect(Collectors.joining("\";\"")));
        out.write("\"\n");
    }

    public void writeRow(final String[] currentStubs, final char[] buffer,
                         final int[] valueLengths, final int headingWidth) throws IOException
    {
        out.write("\"");
        out.write(String.join("\";\"", currentStubs));
        out.write("\";");
        for (int i = 0; i < headingWidth; i++) {
            final var offset = i * DATA_VALUE_WIDTH;
            out.write(buffer, offset, valueLengths[i]);
            if (i < headingWidth - 1) {
                out.write(';');
            }
        }
        out.write('\n');
    }

    public void writeFooting() {

    }

}
