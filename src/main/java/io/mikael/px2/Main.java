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

import static picocli.CommandLine.Help.Visibility.ALWAYS;

@Command(name = "px2csv", sortOptions = false)
class Main implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", description = "Input file.")
    private File inputFile;

    @Parameters(index = "1", arity = "1", description = "Output file.")
    private File outputFile;

    @Option(names = {"-ic", "--input-charset"}, arity = "0..1", paramLabel = "CHARSET",
            defaultValue = "ISO-8859-1", showDefaultValue = ALWAYS, order = 1)
    private Charset inputCharset = StandardCharsets.ISO_8859_1;

    @Option(names = {"-oc", "--output-charset"}, arity = "0..1", paramLabel = "CHARSET",
            defaultValue = "ISO-8859-1", showDefaultValue = ALWAYS, order = 2)
    private Charset outputCharset = StandardCharsets.ISO_8859_1;

    @Option(names = "-n", arity = "0..1", description = "how many times to execute, for testing", order = 3)
    private Integer iterations = 1;

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message", order = 4)
    private boolean helpRequested = false;

    public static void main(final String[] args) throws Exception {
        final var exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        for (int i = 0; i < iterations; i++) {
            try (var input = Files.newByteChannel(inputFile.toPath());
                 var output = Files.newBufferedWriter(outputFile.toPath(), outputCharset))
            {
                final var reader = new LocklessReader(input, inputCharset);
                final var writer = new CubeCsvWriter(output);
                final var parser = new PxParser(reader, writer);
                parser.parseHeader();
                parser.parseData();
            }
        }
        return 0;
    }

}