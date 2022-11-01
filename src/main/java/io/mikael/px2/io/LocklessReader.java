package io.mikael.px2.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Because of JVM lock elision regressions, we have an explicitly lock-free
 * simplified BufferedReader implementation.
 */
public final class LocklessReader {

    public static final int PAGE_SIZE = 4096;

    public static final char EOF = (char) -1;

    private final CharBuffer characters = CharBuffer.allocate(2 * PAGE_SIZE);

    private final ByteBuffer bytes = ByteBuffer.allocate(2 * PAGE_SIZE);

    private int readBytesLength = 0;

    private int charactersLength = 0;

    private final SeekableByteChannel channel;

    private Charset charset;

    private CharsetDecoder decoder;

    private boolean defaultCharset = true;

    public LocklessReader(final SeekableByteChannel channel, final Charset charset) {
        this.channel = channel;
        this.charset = charset;
        this.decoder = charset.newDecoder();
    }

    /**
     * This method will be called in a very tight parser loop.
     */
    public char read() throws IOException {
        if (characters.position() < charactersLength) {
            return characters.get();

        } else if (-1 == readBytesLength) {
            return EOF;

        } else {
            bytes.clear();
            readBytesLength = channel.read(bytes);
            bytes.flip();

            characters.clear();
            decoder.decode(bytes, characters, false);
            charactersLength = characters.position();
            characters.flip();

            if (-1 != readBytesLength) {
                return characters.get();
            }
        }
        return EOF;
    }

    public void switchDecoder(final String charsetName) {
        if (this.defaultCharset) {
            this.defaultCharset = false;
            this.charset = Charset.forName(charsetName.toUpperCase());
            this.decoder = charset.newDecoder();
            characters.clear();
            bytes.flip();
            bytes.position(0);
            final var result = decoder.decode(bytes, characters, false);
            charactersLength = characters.position();
            characters.flip();
        }
    }

}
