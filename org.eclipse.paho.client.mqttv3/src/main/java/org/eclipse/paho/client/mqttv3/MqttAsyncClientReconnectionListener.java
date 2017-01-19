package org.eclipse.paho.client.mqttv3;

import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;

public class MqttAsyncClientReconnectionListener implements IMqttActionListener {

    private static final String CLASS_NAME = MqttAsyncClientReconnectionListener.class.getName();
    private static final Logger log = LoggerFactory.getLogger(LoggerFactory.MQTT_CLIENT_MSG_CAT, CLASS_NAME);

    private MqttAsyncClient mClient;

    public MqttAsyncClientReconnectionListener(MqttAsyncClient client) {
        mClient = client;
    }

    public void onSuccess(IMqttToken asyncActionToken) {
        //@Trace 501=Automatic Reconnect Successful: {0}
        log.fine(CLASS_NAME, "onSuccess", "501", new Object[]{asyncActionToken.getClient().getClientId()});
        mClient.comms.setRestingState(false);
        mClient.stopReconnectCycle();
    }

    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        //@Trace 502=Automatic Reconnect failed, rescheduling: {0}
        log.fine(CLASS_NAME, "onFailure", "502", new Object[]{asyncActionToken.getClient().getClientId()});

        if (mClient.reconnectDelay < 128000) {
            mClient.reconnectDelay *= 2;
        }

        mClient.rescheduleReconnectCycle(mClient.reconnectDelay);
    }
}
