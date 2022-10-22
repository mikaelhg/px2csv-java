package io.mikael.px2.dto;

import java.util.ArrayList;
import java.util.List;

public final class RowAccumulator {

    public StringBuilder keyword;

    public StringBuilder language;

    public StringBuilder subkey;

    public List<String> subkeys;

    public StringBuilder value;

    public List<String> values;

    public RowAccumulator() {
        this.keyword = new StringBuilder();
        this.language = new StringBuilder();
        this.subkey = new StringBuilder();
        this.subkeys = new ArrayList<>();
        this.value = new StringBuilder();
        this.values = new ArrayList<>();
    }

    public PxHeaderRow toPxHeaderRow() {
        return new PxHeaderRow(keyword.toString(), language.toString(), subkeys, values);
    }

}
