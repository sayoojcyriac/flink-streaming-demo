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

## Project Modules
![image](https://user-images.githubusercontent.com/32276029/137683785-535fc608-b99a-4ad8-977e-ddf7dec89193.png)
### datasource - WordStreamProducer

<ol>
  <li>Implements the word streaming source </li>
  <li>Requires a running instance of Kafka created with the topic - streaming.words.source </li>
  <li>Establishes kafka connection, reads corpora data, stream the data word by word to Kafka topic in the loacl Kafka cluster </li>
  <li>The Producer API from Kafka direct the token/ message to Kafka Server</li>
  <li>The words are streamed from dump files in data directory at root level</li>
  <li>Read dump files line by line, split to word tokens & then streamed word-by-word</li>
  <li>The words are streamed by default with a delay of 1 second</li>
</ol>

### processor - WordStreamProcessor
<ol>
  <li>Creates Flink StreamExecutionEnvironment - creates a local environment that will execute program on local machine</li>
  <li>Kafka topic data source has been specified [streaming.words.source] for the execution environment</li>
  <li>Hence the DataStream is initialized which shall go through different transformation and operations</li>
  <li>Establishes kafka connection, reads corpora data, stream the data word by word to Kafka topic in the loacl Kafka cluster </li>
</ol>

