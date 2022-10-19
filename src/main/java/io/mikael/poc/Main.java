package io.mikael.poc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Main {

    static final String FILE_NAME = "/home/mikael/devel/hobbies/gpcaxis/data/010_kats_tau_101.px";

    public static void main(String[] args) throws Exception {
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
