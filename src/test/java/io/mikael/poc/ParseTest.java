package io.mikael.poc;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class ParseTest {

    static final String FILE_NAME = "/home/mikael/devel/hobbies/gpcaxis/data/statfin_akay_pxt_005.px";

    @Test
    public void parseTest() throws IOException {
        final var pxFile = Paths.get(FILE_NAME);
        try (var input = Files.newBufferedReader(pxFile, ISO_8859_1);
             var output = new BufferedWriter(new OutputStreamWriter(System.out))) {
            final var writer = new StatCubeCsvWriter(output);
            final var parser = new Parser(writer);
            parser.parseHeader(input);
            parser.parseDataDense(input);
            // System.out.println(parser.headers);
            // System.out.println(parser.headers.get(32));
        }
    }

}
