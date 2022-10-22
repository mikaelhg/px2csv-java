package io.mikael.poc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Main {

    public static void main(final String[] args) throws Exception {
        try (var input = Files.newBufferedReader(Paths.get(args[0]), ISO_8859_1);
             var output = Files.newBufferedWriter(Paths.get(args[1]), ISO_8859_1))
        {
            final var writer = new StatCubeCsvWriter(output);
            final var parser = new PxParser(writer);
            parser.parseHeader(input);
            parser.parseDataDense(input);
        }
    }

}
