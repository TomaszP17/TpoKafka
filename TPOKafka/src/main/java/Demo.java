import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

import javax.swing.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class Demo {
    public static void main(String[] args) {
        EmbeddedKafkaBroker embeddedKafkaBroker = new EmbeddedKafkaZKBroker(1)
                .kafkaPorts(9092);
        //localhost:9092

        embeddedKafkaBroker.afterPropertiesSet();
/*
        MessageProducer.send(new ProducerRecord<>("chat", "Hello World!"));

        System.out.println("Wyslalem");

        new MessageConsumer("chat", "Kinga");*/

        SwingUtilities.invokeLater(() -> new Chat("Kinga", "chat"));
        SwingUtilities.invokeLater(() -> new Chat("Jakub", "chat"));

    }
}

class MessageProducer{
    private static KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(
            Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()
            )
    );

    public static void send(ProducerRecord<String, String> producerRecord){
        kafkaProducer.send(producerRecord);
    }
}

class MessageConsumer{
    KafkaConsumer<String, String> kafkaConsumer;


    public MessageConsumer(String topic, String id) {
        kafkaConsumer = new KafkaConsumer<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, id,
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                ));

        kafkaConsumer.subscribe(Collections.singletonList(topic));
        kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(cr -> System.out.println(id + " " + cr.value()));
    }
}
