package io.mikael.px2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * Functional end to end test for the PX to CSV converter.
 */
public class ParseTest {

    @Test
    public void parseStatic() throws IOException {
        final var cl = this.getClass().getClassLoader();
        try (var px = cl.getResourceAsStream("test_1.px");
             var csv = cl.getResourceAsStream("test_1.csv");
             var input = new BufferedReader(new InputStreamReader(px));
             var stringWriter = new StringWriter();
             var output = new BufferedWriter(stringWriter))
        {
            final var writer = new StatCubeCsvWriter(output);
            final var parser = new PxParser(writer);
            parser.parseHeader(input);
            parser.parseDataDense(input);
            output.flush();
            final var result = stringWriter.toString();
            final var expected = new String(csv.readAllBytes(), ISO_8859_1);
            Assertions.assertEquals(expected, result);
        }
    }

}
