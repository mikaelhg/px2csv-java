package io.mikael.poc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class ParseTest {

    static final String FILE_NAME = "/home/mikael/devel/hobbies/gpcaxis/data/010_kats_tau_101.px";

    @Test
    public void parseTest() throws IOException {
        final var pxFile = Paths.get(FILE_NAME);
        final var writer = new StatCubeCsvWriter();
        final var parser = new Parser(writer);
        try (var input = Files.newBufferedReader(pxFile, ISO_8859_1)) {
            parser.parseHeader(input);
            parser.parseDataDense(input);
        }
        System.out.println(parser.headers);
        System.out.println(parser.headers.get(32));
    }

}
