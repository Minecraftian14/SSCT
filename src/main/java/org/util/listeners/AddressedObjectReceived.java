package org.util.listeners;

import org.ConnectionHandle;

public interface AddressedObjectReceived {
    void ObjectReceived(Object object, ConnectionHandle sender);
}
