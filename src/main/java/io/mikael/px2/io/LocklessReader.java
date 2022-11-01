package io.mikael.px2.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Because of JVM lock elision regressions, we have an explicitly lock-free
 * simplified BufferedReader implementation.
 *
 * Obviously not thread-safe. Theoretically if you pass an instance from one
 * thread to another, and especially if you operate the instance in a tight
 * loop, you need to make sure that you flush your L2/3 cache lines by caging
 * any access in happens-before barriers, such as synchronized. Read the JMM.
 */
public final class LocklessReader {

    public static final int PAGE_SIZE = 4096;

    public static final char EOF = (char) -1;

    private final CharBuffer characters = CharBuffer.allocate(2 * PAGE_SIZE);

    private final ByteBuffer bytes = ByteBuffer.allocateDirect(2 * PAGE_SIZE);

    private int readLength = 0;

    private final ReadableByteChannel channel;

    private CharsetDecoder decoder;

    private boolean isDefaultCodepage = true;

    public LocklessReader(final ReadableByteChannel channel, final Charset charset) {
        this.channel = channel;
        this.decoder = charset.newDecoder();
        this.characters.limit(0);
        bytes.flip();
    }

    /**
     * This method will be called in a very tight parser loop.
     */
    public char read() throws IOException {
        if (characters.hasRemaining()) {
            return characters.get();

        } else if (-1 == readLength) {
            return EOF;

        } else {
            bytes.compact();
            readLength = channel.read(bytes);
            bytes.flip();

            if (-1 != readLength) {
                characters.clear();
                final var result = decoder.decode(bytes, characters,
                        bytes.limit() != bytes.capacity());

                // bytes.compact(); // might contain partially read multibyte characters
                characters.flip();

                return characters.get();
            }
        }
        return EOF;
    }

    public void switchCharsetDecoder(final CharsetDecoder charsetDecoder) {
        if (this.isDefaultCodepage) {
            this.isDefaultCodepage = false;
            this.decoder = charsetDecoder;
            characters.clear();
            bytes.flip();
            bytes.position(0);
            decoder.decode(bytes, characters, bytes.limit() != bytes.capacity());
            characters.flip();
        }
    }

}
