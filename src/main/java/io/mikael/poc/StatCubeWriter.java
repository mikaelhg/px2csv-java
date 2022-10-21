package io.mikael.poc;

import java.io.IOException;
import java.util.List;

public interface StatCubeWriter {
    void writeHeading(List<String> stub, CartesianProduct headingFlattener) throws IOException;

    void writeRow(String[] stubs, char[] buffer,
                  int[] valueLengths, int headingWidth) throws IOException;

    void writeFooting();
}
