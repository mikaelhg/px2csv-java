package io.mikael.poc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Main {

    public static void main(final String[] args) throws Exception {
        final var pxFile = Paths.get(args[0]);
        try (var input = Files.newBufferedReader(pxFile, ISO_8859_1);
             var fileWriter = new FileWriter(args[1], ISO_8859_1);
             var output = new BufferedWriter(fileWriter))
        {
            final var writer = new StatCubeCsvWriter(output);
            final var parser = new Parser(writer);
            parser.parseHeader(input);
            parser.parseDataDense(input);
        }
    }

}
