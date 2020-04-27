package com.aot.orders.topic;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;

public class OrderConsumer {
    private final static String TOPIC = "OrderTopic";
    private final static String BOOTSTRAP_SERVER = "kafka:9092";

    public static void main(String... args) throws Exception {
        runConsumer();
    }

    private static Consumer<Long, String> createConsumer() {
        final Properties settings = new Properties();
        settings.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        settings.put(ConsumerConfig.GROUP_ID_CONFIG, "OrderConsumerGroup");
        settings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        settings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        settings.put(ConsumerConfig.GROUP_ID_CONFIG, "OrderConsumer");
        settings.put(ConsumerConfig.CLIENT_ID_CONFIG, "com.aot.orders.topic.OrderStatusProducer");
        settings.put("enable.auto.commit", false);
        settings.put("session.timeout.ms", 30000);
        settings.put("heartbeat.interval.ms", 10000);
        settings.put("request.timeout.ms", 31000);
        /*settings.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        settings.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\codes\\programs\\Kafka\\ssl\\client.truststore.jks");
        settings.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "test1234");
        settings.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "C:\\codes\\programs\\Kafka\\ssl\\server.keystore.jks");
        settings.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "test1234");
        settings.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "test1234");*/
        // Create the consumer using props.
        final Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(settings);
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }

    static void runConsumer() throws InterruptedException {
        final Consumer<Long, String> consumer = createConsumer();

        final int giveUp = 100;   int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
            });

            consumer.commitSync();
        }
        consumer.close();
        System.out.println("DONE");
    }
}







