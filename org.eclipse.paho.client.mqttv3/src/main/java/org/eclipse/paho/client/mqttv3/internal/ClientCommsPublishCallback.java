package org.eclipse.paho.client.mqttv3.internal;

import org.eclipse.paho.client.mqttv3.BufferedMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

public class ClientCommsPublishCallback implements IDisconnectedBufferCallback {

    private static final String CLASS_NAME = ClientCommsPublishCallback.class.getName();
    private static final Logger log = LoggerFactory.getLogger(LoggerFactory.MQTT_CLIENT_MSG_CAT, CLASS_NAME);

    private ClientComms mClientComms;
    private ClientState mClientState;

    public ClientCommsPublishCallback(ClientComms clientComms, ClientState clientState) {
        mClientComms = clientComms;
        mClientState = clientState;
    }

    public void publishBufferedMessage(BufferedMessage bufferedMessage) throws MqttException {
        if (mClientComms.isConnected()) {
            while(mClientState.getActualInFlight() >= (mClientState.getMaxInFlight()-1)){
                // We need to Yield to the other threads to allow the in flight messages to clear
                Thread.yield();
            }
            //@TRACE 510=Publising Buffered message message={0}
            log.fine(CLASS_NAME, "publishBufferedMessage", "510", new Object[] {bufferedMessage.getMessage().getKey()});
            mClientComms.internalSend(bufferedMessage.getMessage(), bufferedMessage.getToken());
            // Delete from persistence if in there
            mClientState.unPersistBufferedMessage(bufferedMessage.getMessage());
        } else {
            //@TRACE 208=failed: not connected
            log.fine(CLASS_NAME, "publishBufferedMessage", "208");
            throw ExceptionHelper.createMqttException(MqttException.REASON_CODE_CLIENT_NOT_CONNECTED);
        }
    }
}
