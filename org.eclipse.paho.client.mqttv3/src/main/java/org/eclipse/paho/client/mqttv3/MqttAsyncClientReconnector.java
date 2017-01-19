package org.eclipse.paho.client.mqttv3;

public class MqttAsyncClientReconnector implements MqttCallbackExtended {

    private MqttAsyncClient mClient;
    private boolean mAutomaticReconnect;

    public MqttAsyncClientReconnector(MqttAsyncClient client, boolean automaticReconnect) {
        mClient = client;
        mAutomaticReconnect = automaticReconnect;
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public void connectComplete(boolean reconnect, String serverURI) {
    }

    public void connectionLost(Throwable cause) {
        if (mAutomaticReconnect) {
            // Automatic reconnect is set so make sure comms is in resting state
            mClient.comms.setRestingState(true);
            mClient.setReconnecting();
            mClient.startReconnectCycle();
        }
    }
}
