package io.mikael.poc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.mikael.poc.PxParser.DATA_VALUE_WIDTH;

public final class StatCubeCsvWriter implements StatCubeWriter {

    /* This is how you crash your program on unexpected inputs. */
    private static final int ARBITRARY_BUFFER_SIZE = 4096;

    private static final String DELIMITER = "\";\"";

    private final CharBuffer out = CharBuffer.allocate(ARBITRARY_BUFFER_SIZE);

    private final BufferedWriter bufferedWriter;

    public StatCubeCsvWriter(final BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    @Override
    public void writeHeading(List<String> stub, CartesianProduct headingFlattener) throws IOException {
        bufferedWriter.write("\"");
        bufferedWriter.write(String.join(DELIMITER, stub));
        bufferedWriter.write(DELIMITER);
        bufferedWriter.write(Arrays.stream(headingFlattener.all())
                .map((ss) -> String.join(" ", ss))
                .collect(Collectors.joining(DELIMITER)));
        bufferedWriter.write("\"\n");
    }

    @Override
    public void writeRow(final String[] stubs, final char[] buffer,
                         final int[] valueLengths, final int headingWidth) throws IOException
    {
        out.clear();
        out.put('"');
        out.put(String.join(DELIMITER, stubs));
        out.put('"');
        out.put(';');
        for (int i = 0; i < headingWidth; i++) {
            final var offset = i * DATA_VALUE_WIDTH;
            out.put(buffer, offset, valueLengths[i]);
            if (i < headingWidth - 1) {
                out.put(';');
            }
        }
        out.put('\n');
        bufferedWriter.write(out.array(), out.arrayOffset(), out.position());
    }

    @Override
    public void writeFooting() {

    }

}
