package io.mikael.px2;

import io.mikael.px2.io.LocklessReader;
import io.mikael.px2.io.CubeCsvWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Functional end to end test for the PX to CSV converter.
 */
public class ParseTest {

    @Test
    public void parseStatic() throws Exception {
        final var cl = this.getClass().getClassLoader();
        try (var input = Files.newByteChannel(Paths.get(cl.getResource("test_1.px").toURI()));
             var csv = cl.getResourceAsStream("test_1.csv");
             var stringWriter = new StringWriter();
             var output = new BufferedWriter(stringWriter))
        {
            final var reader = new LocklessReader(input, ISO_8859_1);
            final var writer = new CubeCsvWriter(output);
            final var parser = new PxParser(reader, writer);
            parser.parseHeader();
            parser.parseData();
            output.flush();
            final var result = stringWriter.toString();
            final var expected = new String(csv.readAllBytes(), ISO_8859_1);
            Assertions.assertEquals(expected, result);
        }
    }

}
