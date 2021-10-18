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
https://github.com/sayoojcyriac/flink-streaming-demo/tree/main/src/main/java/com/flink/demo
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

### predictor - WordPredictor
<ol>
  <li>External service word prediction pattern is implemented</li>
  <li>WordPredictor service caches the bi-grams as the new word arrives</li>
  <li>The service predicts upto 5 next possible words by counting the bi-gram and deriving the underlying probability / occurrences</li>
  <li>Google Guava's Table API is used as this use case is similar to a map of maps</li>
  <li>Key1 and Key2 forms a bi-gram pair and the third entry value tracks the number of occurrences in the Table</li>
  <li>This is a very naive implementation of external prediction API as the model is cached in-memory</li>
  <li>The implementation needs to be changed for unbounded data stream. But, this maybe good enough for demostration purpose and proving the concept</li>
  <li>Hence during transformation of incoming data stream, the Predictor API is called with the input data & prints the predictions</li>
  <li>The Predictor data model grows and learns as the new data arrives</li>
</ol>

## Building Module
 mvn clean install
 
## Running the data streaming system
1. Create Kafka topic - streaming.words.source
2. After running zookeeper and Kafka like mentioned in the above section, execute the below command to create the Kafka topic
./bin/kafka-topics.sh \
    --zookeeper localhost:2181 \
    --create \
    --topic streaming.words.source \
    --partitions 1 \
    --replication-factor 1
 3. Make sure the topic is created, run the below command to list the topics
./bin/kafka-topics.sh --zookeeper localhost:2181 --list
![image](https://user-images.githubusercontent.com/32276029/137755406-7fdc4cda-fcf6-4615-93f4-b34f8caac39a.png)

4. Generate the fat Jar using maven command
mvn clean install

5. The application expects the data dump/ corpora in the designated directory, "data" at the root level of Jar
6. Add the dump files in the "data" directory
![image](https://user-images.githubusercontent.com/32276029/137756119-c4598a74-6f21-491a-9f1e-35267c269810.png)

7. Run the Jar - 
![image](https://user-images.githubusercontent.com/32276029/137756293-67003954-01aa-45fd-9d33-3ae6839f4317.png)

![image](https://user-images.githubusercontent.com/32276029/137756425-d68f8c69-ae4c-4029-ae4b-5a46d8706369.png)

## Results

1. You may see in real-time the corpora data gets streamed word-by-word, send to Kafka topic
2. The streamed words gets in to WordStreamProcessor DataStream
3. WordStreamProcessor transoforms the data, prints and count

![image](https://user-images.githubusercontent.com/32276029/137757356-a4a9a05b-63c6-4a1d-83d2-90c90393f176.png)
4. The number next to the word is the count

## Prediction

1. WordStreamProcessor during tranformation, invokes the external WordPredictor service for insights
2. The WordPredictor service logic is exmplained in the above section
3. The WordPredictor service retruns upto 5 next possible strings based on the learned bi-grams derived from its occurrences
![image](https://user-images.githubusercontent.com/32276029/137757870-3f39ce04-0f4e-418e-9319-0897b24755a9.png)






