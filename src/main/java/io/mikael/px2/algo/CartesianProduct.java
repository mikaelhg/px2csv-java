package io.mikael.px2.algo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CartesianProduct implements Iterator<String[]> {

    public int cursor = 0;

    public int[] counters;

    public final int length;

    public final int permutationCount;

    public final int[] lengths;

    public final String[][] lists;

    public static CartesianProduct of(final List<List<String>> input) {
        final var lists = new String[input.size()][];
        for (int i = 0; i < lists.length; i++) {
            final var ll = input.get(i);
            lists[i] = new String[ll.size()];
            ll.toArray(lists[i]);
        }
        return new CartesianProduct(lists);
    }

    public CartesianProduct(final String[][] input) {
        this.lists = input;
        this.length = input.length;
        this.counters = new int[length];
        this.lengths = new int[length];
        for (int i = 0; i < length; i++) {
            lengths[i] = lists[i].length;
        }
        this.permutationCount = Arrays.stream(lengths).reduce(1, (a, b) -> a * b);
    }

    @Override
    public boolean hasNext() {
        return this.cursor < this.permutationCount;
    }

    @Override
    public String[] next() {
        final var ret = new String[length];
        for (int i = 0; i < length; i++) {
            ret[i] = lists[i][counters[i]];
        }
        this.step();
        return ret;
    }

    private boolean step() {
        for (int i = length - 1; i >= 0; i--) {
            if (counters[i] < lengths[i] - 1) {
                counters[i] += 1;
                this.cursor += 1;
                return true;
            } else {
                counters[i] = 0;
            }
        }
        return false;
    }

    public String[][] all() {
        final var result = new String[this.permutationCount][];
        for (int i = 0; i < this.permutationCount; i++) {
            result[i] = this.next();
        }
        return result;
    }

}
