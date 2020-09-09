package org.util.data.adaptors;

import java.io.IOException;

public interface ReaderAdaptor {
    int read(byte[] b) throws IOException;
}
