package io.mikael.px2.dto;

import java.util.List;

public record PxHeaderRow(String keyword, String language, List<String> subkeys, List<String> values) {

}
