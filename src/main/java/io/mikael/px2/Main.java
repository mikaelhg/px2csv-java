package io.mikael.px2;

import io.mikael.px2.io.LocklessReader;
import io.mikael.px2.io.CubeCsvWriter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@Command(name = "px2csv")
class Main implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "Input file.")
    private File inputFile;

    @Parameters(index = "1", arity = "1", description = "Output file.")
    private File outputFile;

    @Option(names = {"-c", "--charset"}, arity = "0..1")
    private Charset charset = StandardCharsets.ISO_8859_1;

    @Option(names = "-n", arity = "0..1", description = "how many times to execute, for testing")
    private Integer iterations = 1;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    public static void main(final String[] args) throws Exception {
        final var exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < iterations; i++) {
            try (var input = Files.newByteChannel(inputFile.toPath());
                 var output = Files.newBufferedWriter(outputFile.toPath(), charset))
            {
                final var reader = new LocklessReader(input, charset);
                final var writer = new CubeCsvWriter(output);
                final var parser = new PxParser(reader, writer);
                parser.parseHeader();
                parser.parseData();
            }
        }
        return 0;
    }

}