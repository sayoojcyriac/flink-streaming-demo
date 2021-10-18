package com.flink.demo.processor;

import com.flink.demo.datasource.WordStreamProducer;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

import static com.flink.demo.constants.KafkaConnectionConstants.*;

public class WordStreamProcessor {

    public static void main(String[] args) {
        try {
            // setup streaming execution environment
            final StreamExecutionEnvironment streamEnv
                    = StreamExecutionEnvironment.getExecutionEnvironment();

            // connection properties of Kafka Cluster
            final Properties properties = new Properties();
            properties.setProperty(BOOTSTRAP_SERVERS, KAFKA_SERVER_URL);

            // init Kafka consumer on Flink
            FlinkKafkaConsumer<String> kafkaConsumer =
                    new FlinkKafkaConsumer<>
                            (DATA_SOURCE_TOPIC, new SimpleStringSchema(), properties);

            kafkaConsumer.setStartFromLatest();

            // create word data stream
            final DataStream<String> wordDataStream = streamEnv.addSource(kafkaConsumer);

            DataStream<Tuple2<String, Integer>> wordCount =
                    wordDataStream.map(new MapFunction<String, Tuple2<String, Integer>>() {
                                @Override
                                public Tuple2<String, Integer> map(String word) {
                                    return new Tuple2<>(word, 1);
                                }
                            })
                            .keyBy(0)
                            .sum(1);
            wordCount.print();

            // start event generator
            startWordStreamProducer();

            // execute the streaming pipeline
            streamEnv.execute("flink streaming words processor!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void startWordStreamProducer() {
        System.out.println("staring Kafka word data stream...");
        final Thread thread = new Thread(new WordStreamProducer());
        thread.start();
    }

}