package io.mikael.poc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Main {

    public static void main(String[] args) throws Exception {
        final var pxFile = Paths.get(args[1]);
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
