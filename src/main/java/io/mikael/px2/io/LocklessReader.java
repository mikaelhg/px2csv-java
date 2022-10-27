package io.mikael.px2.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Because of JVM lock elision regressions, we have an explicitly lock-free
 * simplified BufferedReader implementation.
 */
public final class LocklessReader {

    public static final int PAGE_SIZE = 4096;

    public static final char EOF = (char) -1;

    private final char[] readBuffer = new char[2 * PAGE_SIZE];

    private int readOffset = 0;

    private int readLength = 0;

    private final Reader backingReader;

    public LocklessReader(final Reader backingReader) {
        this.backingReader = backingReader;
    }

    public char read() throws IOException {
        if (-1 == readLength) {
            return EOF;
        } else if (readOffset < readLength) {
            return readBuffer[readOffset++];
        } else {
            readOffset = 0;
            readLength = backingReader.read(readBuffer);
            if (-1 != readLength) {
                return readBuffer[readOffset++];
            }
        }
        return EOF;
    }

    public void switchDecoder(final String charsetName) {
        System.err.printf("Should change decoder charset to %s, but can't yet.%n", charsetName);
    }

}
