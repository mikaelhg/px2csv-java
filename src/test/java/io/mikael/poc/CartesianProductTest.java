package io.mikael.poc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class CartesianProductTest {

    @Test
    public void testCartesianProductAll() {
        final var data = new String[][]{
            {"a", "b", "c"},
            {"1", "2", "3"},
        };
        final var expected = new String[][]{
            {"a", "1"}, {"a", "2"}, {"a", "3"},
            {"b", "1"}, {"b", "2"}, {"b", "3"},
            {"c", "1"}, {"c", "2"}, {"c", "3"},
        };
        final var c = new CartesianProduct(data);
        final var result = c.all();
        Assertions.assertTrue(Arrays.equals(expected, result, Arrays::compare));
    }

}
