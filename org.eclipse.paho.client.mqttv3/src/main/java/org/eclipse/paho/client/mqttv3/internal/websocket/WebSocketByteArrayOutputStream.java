package org.eclipse.paho.client.mqttv3.internal.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class WebSocketByteArrayOutputStream extends ByteArrayOutputStream {

    private OutputStream mSocketOutputStream;

    public WebSocketByteArrayOutputStream(OutputStream socketOutputStream) {
        mSocketOutputStream = socketOutputStream;
    }

    /**
     * Overrides the flush method.
     * This allows us to encode the MQTT payload into a WebSocket
     *  Frame before passing it through to the real socket.
     */
    public void flush() throws IOException {
        final ByteBuffer byteBuffer;

        synchronized (this) {
            byteBuffer = ByteBuffer.wrap(toByteArray());
            reset();
        }

        WebSocketFrame frame = new WebSocketFrame((byte)0x02, true, byteBuffer.array());
        byte[] rawFrame = frame.encodeFrame();

        if (mSocketOutputStream != null) {
            mSocketOutputStream.write(rawFrame);
            mSocketOutputStream.flush();
        }
    }
}
