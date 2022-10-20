package io.mikael.poc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    public void writeRow(String[] currentStubs, CharBuffer[] values,
                         int[] valueLengths, int headingWidth) throws IOException
    {
        out.write("\"");
        out.write(String.join("\";\"", currentStubs));
        out.write("\";");
        for (int i = 0; i < headingWidth; i++) {
            out.write(values[i].array(), 0, valueLengths[i]);
            if (i < headingWidth - 1) {
                out.write(';');
            }
        }
        out.write('\n');
    }

    public void writeFooting() {

    }

}
