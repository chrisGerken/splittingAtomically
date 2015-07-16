package org.atomsg.topic.AtomicMessage;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;


import org.atomsg.model.AtomicMessage;

/**
 * AtomicMessageProducer provides a strongly-typed API for writing to topic AtomicMessage.
 * <pre>
 * Usage:
 * 
 *     AtomicMessageProducer producer = new AtomicMessageProducer("localhost:9092");
 *     
 *     AtomicMessage message = ...
 *
 *     producer.send(message);
 *     
 *     producer.close();
 
 *</pre>     
 */
public class AtomicMessageProducer {
	
  private final Producer<Long, AtomicMessage> producer;
  private Long key = 0L;

  /**
   * Construct a AtomicMessageProducer to write messages to topic AtomicMessage.
   * 
   * @param csBrokerList A String containing a comma-separated list of hosts and ports (e.g. 'host1:9092,host2:9092')
   */
public AtomicMessageProducer(String csBrokerList) {
	  
	  Properties props = new Properties();
	  props.put("metadata.broker.list", csBrokerList);
	  props.put("producer.type", "sync");
	  props.put("serializer.class","org.atomsg.serialization.AtomicMessageCoder");
	  props.put("key.serializer.class","org.atomsg.serialization.LongCoder");
	  props.put("request.required.acks", "1");
	  producer = new Producer<Long, AtomicMessage>(new ProducerConfig(props));

  }
  
  public void send(AtomicMessage value) {
	  key++;
	  producer.send(new KeyedMessage<Long, AtomicMessage>(AtomicMessageInfo.topicName, key, value));
  }
  
  public void close() {
	  producer.close();
  }

}
