package com.flink.demo.datasource;

import com.flink.demo.constants.KafkaConnectionConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.flink.demo.constants.KafkaConnectionConstants.*;

/***
 * Kafka streaming source.
 * Requires a running instance of Kafka created with the topic - streaming.words.source
 * Establishes kafka connection, reads corpora data, stream the data word by word to Kafka topic
 * in the Kafka cluster. The Producer API from Kafka direct the token/ message to Kafka Server.
 */
public class WordStreamProducer implements Runnable {

    private static final String DATA_DIR = "data";

    private final KafkaProducer<String, String> producer;

    public WordStreamProducer() {
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS, KafkaConnectionConstants.KAFKA_SERVER_URL);
        properties.put(CLIENT_ID, "DemoWordProducer");
        properties.put(KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(VALUE_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");

        // create KafkaProducer with the above properties
        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public void run() {
        try {
            final Set<String> dataSources = this.listFilesInDataDir();
            for (String dataSource : dataSources) {
                final LineIterator fileContents = FileUtils.lineIterator(new File(dataSource),
                        StandardCharsets.UTF_8.name());

                while (fileContents.hasNext()) {
                    // normalize and split the line
                    final String[] tokens = fileContents
                            .nextLine()
                            .toLowerCase()
                            .split("\\W+");

                    // stream word-by-word!
                    for (final String token : tokens) {
                        if (StringUtils.isNotEmpty(token)) {
                            // send event to the producer synchronously
                            this.producer.send(new ProducerRecord<>(DATA_SOURCE_TOPIC,
                                    token))
                                    .get();
                            Thread.sleep(1000);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("error occurred while streaming events to Kafka, error: " + ex.getMessage());
        }
    }

    private Set<String> listFilesInDataDir() {
        return Stream.of(Objects.requireNonNull(new File(DATA_DIR).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .collect(Collectors.toSet());
    }

}