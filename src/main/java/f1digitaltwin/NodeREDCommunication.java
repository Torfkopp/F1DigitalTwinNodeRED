package f1digitaltwin;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;

/**
 * Class to handle the communication with NodeRED via MQTT
 */
public class NodeREDCommunication {

    private static final String serverURI = "tcp://localhost:1883";

    private static MqttClient client = null;
    private final Controller controller;

    /**
     * Constructor
     *
     * @param controller object
     */
    NodeREDCommunication(Controller controller) {
        this.controller = controller;
        init();
    }

    /**
     * Closes the connection
     */
    static void disconnect() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Connects to the broker and sets up the message receiving
     */
    void init() {
        try {
            connect();
            receive();
        } catch (MqttException me) {
            System.out.println("FAILED to initialise");
        }
    }

    /**
     * Sends a message
     *
     * @param msg The message to be sent
     */
    void send(String topic, String msg) {
        try {
            MqttMessage message = new MqttMessage(msg.getBytes(StandardCharsets.UTF_8));
            message.setQos(2);
            client.publish(topic, message);
            //System.out.println("Message sent");
        } catch (Exception me) {
            System.out.println("FAILED to send");
        }
    }

    /**
     * Connects to the broker
     *
     * @throws MqttException thrown
     */
    private void connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);

        client = new MqttClient(serverURI, "my_sender", new MemoryPersistence());

        client.connect(options);
        System.out.println("Connected");
    }

    /**
     * Handles a received message and forwards it the controller
     *
     * @param topic The message's topic
     * @param msg   The message's message
     */
    private void handleMessage(String topic, MqttMessage msg) {
        //System.out.println("Message received");
        controller.messageReceived(msg.toString());
    }

    /**
     * Sets up the message subscription
     *
     * @throws MqttException thrown
     */
    private void receive() throws MqttException {
        String[] topic = new String[]{"hello"};
        int[] qos = new int[]{2};

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                handleMessage(s, mqttMessage);
            }
        });

        client.subscribe(topic, qos);
    }
}