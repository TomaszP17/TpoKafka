import org.apache.kafka.clients.producer.ProducerRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;

public class Chat extends JFrame{
    private JTextArea chatView;
    private JPanel mainPanel;
    private JButton sendButton;
    private JTextField message;
    private JButton loginButton;
    private JTextField loginField;
    private JList list1;
    private JTextField textField1;

    //private final MessageProducer messageProducer = new MessageProducer();
    private final MessageConsumer messageConsumer;

    public Chat(String id, String topic) throws HeadlessException {


        messageConsumer = new MessageConsumer(topic, id);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.add(mainPanel);
        this.setVisible(true);
        this.setTitle(id);
        this.pack();

        Executors.newSingleThreadExecutor().submit( () -> {
            while(true) {
                messageConsumer.kafkaConsumer.poll(Duration.of(1, ChronoUnit.SECONDS)).forEach(m -> {
                    System.out.println(m);
                    chatView.append(m.value() + System.lineSeparator());
                });
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageProducer.send(new ProducerRecord<>(topic, LocalDateTime.now() + " - " + id + " " + message.getText()));
            }
        });
    }
}
