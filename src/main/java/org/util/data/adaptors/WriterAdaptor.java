package org.util.data.adaptors;

import java.io.IOException;

public interface WriterAdaptor {
    void write(byte b[]) throws IOException;
}
