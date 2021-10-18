# flink-streaming-demo

This project is an introduction to Apache Flink DataStream APIs. Demonstrates a simple streaming system building on streaming patterns with Apache Flink.
<ul>
  <li>Stream words one by one from a dump</li>
  <li>Prints the count when the new word arrives</li>
  <li>Prints out upto five most likely next words to be streamed by counting bi-grams and using then to derive probabilities</li>
</ul>

## Prerequisites
The following prerequisities are required in order to build and run the services:
<ol>
  <li>JDK 1.8</li>
  <li>Apache Flink</li>
  <li>Basic knowledge of Flink Streaming, DataStream API operations</li>
  <li>Apache Maven</li>
  <li>Kafka topics as input data source</li>
  <li>IDE for development, IntelliJ preferred</li>
</ol>

## Insalling Flink 
Please refer https://nightlies.apache.org/flink/flink-docs-release-1.14/docs/try-flink/local_installation/ for installation steps. <br />
The above steps install Flink, run Flink cluster in your local enviroment. <br />
This project uses Apache Flink 1.9.1. So please make sure you install Flink 1.9.1 in order to run the code in this module smoothly.

## Kafka
Kafka topic is used as the data stream source. 
<ol>
  <li>Download Kafka - http://kafka.apache.org/downloads </li>
  <li>Get Kafka version for Scala 2.12</li>
  <li>Extract the binary to your desired directory - tar -xvf kafka_2.***.tgz </li>
  <li>cd kafka_2.*** directory</li>
  <li>Edit Kafka configuration file to enable listener on default localhost:9092. Open config/server.properties, uncomment listeners, add/ update the  
      listeners=PLAINTEXT://localhost:9092. Save the modified server.properties.</li>
  <li>Start zookeeper service - ./bin/zookeeper-server-start.sh ./config/zookeeper.properties > ./logs/start_zk.log & </li>
  <li>Start kafka service - ./bin/kafka-server-start.sh ./config/server.properties > ./logs/start_kafka.log & </li>
  <li>Similar steps are described - https://dzone.com/articles/kafka-setup</li>
</ol>
