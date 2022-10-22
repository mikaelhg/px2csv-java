package io.mikael.px2;

import io.mikael.px2.algo.CartesianProduct;

import java.io.IOException;
import java.util.List;

public interface CubeWriter {

    void writeHeading(List<String> stub, CartesianProduct headingFlattener) throws IOException;

    void writeRow(String[] stubs, char[] buffer,
                  int[] valueLengths, int headingWidth) throws IOException;

    default void writeFooting() {
    }

}
