package io.mikael.px2;

import io.mikael.px2.io.LocklessReader;
import io.mikael.px2.io.CubeCsvWriter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(final String[] args) throws Exception {
        final var charset = Charset.forName(args[2]);
        final var iterations = Integer.parseInt(args[3]);
        for (int i = 0; i < iterations; i++) {
            try (var input = Files.newBufferedReader(Paths.get(args[0]), charset);
                 var output = Files.newBufferedWriter(Paths.get(args[1]), charset))
            {
                final var reader = new LocklessReader(input);
                final var writer = new CubeCsvWriter(output);
                final var parser = new PxParser(reader, writer);
                parser.parseHeader();
                parser.parseData();
            }
        }
    }

}
