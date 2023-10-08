package se.liu.ida.tdp024.account.util.logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.internals.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DestinationTopic;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Properties;

public class AccountLoggerKafka implements AccountLogger {


    private void sendKafkaHelperFunc(String topicName, String contextAndInfo) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        producer.send(new ProducerRecord<String, String>(topicName, contextAndInfo));
        producer.close();
    }

    @Override
    public void log(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void log(TodoLoggerLevel todoLoggerLevel, String context, String info) {
        this.sendKafkaHelperFunc("rest-requests", context + ": " + info);
    }
    @Override
    public void sendKafka(String kafkaTopic, TodoLoggerLevel todoLoggerLevel, String context, String info) {
        this.sendKafkaHelperFunc(kafkaTopic, context + ": " + info);
    }
}
