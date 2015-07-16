package org.atomsg.topic.AtomicMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.atomsg.model.AtomicMessage;
import org.atomsg.serialization.LongCoder;
import org.atomsg.serialization.AtomicMessageCoder;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

/**
 * AtomicMessageHighLevelConsumer provides an iterator-like interface to be used to read from all
 * partitions in topic AtomicMessage.
 * <pre>
 * Usage:
 * 
 *     AtomicMessageHighLevelConsumer consumer = new AtomicMessageHighLevelConsumer("localhost:2181","group-one");
 *     
 *     while (consumer.hasNext()) {
 *         AtomicMessage org.atomsg.model.AtomicMessage = consumer.next();
 *     }
 *     
 *     consumer.close();
 *</pre>     
 */
public class AtomicMessageHighLevelConsumer {
	
  private ConsumerConnector consumer;
  private final String topic;
  private String zookeeperConnect;
  private String groupId;
  ConsumerIterator<Long, AtomicMessage> it = null;
  
  /**
   * Creates a high-level consumer that reads from all partitions for topic AtomicMessage
   * 
   * @param zookeeperConnect A String containing a comma-separated list of host/ports
   *  (e.g. 'host1:2181,host2:2181').  The hosts have running Zookeeper nodes.
   * @param groupId Client group name.  Used to manage last-read offsets for partitions.
   */
public AtomicMessageHighLevelConsumer(String zookeeperConnect, String groupId) {
	  topic = AtomicMessageInfo.topicName;
	  this.zookeeperConnect = zookeeperConnect;
	  this.groupId = groupId;
	  setup();
  }
  
  /**
   * Returns the next message from the topic.
   * 
   * @return AtomicMessage
   */
  public AtomicMessage next() {
	  return nextMetaMessage().getMessage();
  }
  
  /**
   * Returns the next meta-message from the topic.  The meta-message is able to answer the key, 
   * message, topic, partition and offset for the message.
   * 
   * @return AtomicMessageMetaMessage
   */
  public AtomicMessageMetaMessage nextMetaMessage() {
	  MessageAndMetadata<Long, AtomicMessage> meta = it.next();
	  return new AtomicMessageMetaMessage(meta.key(), meta.message(), meta.topic(), meta.partition(), meta.offset());
  }

  /**
   * Returns whether or not there is a message ready to be returned from the topic.
   * 
   * @return boolean
   */
  public boolean hasNext() {
	  try {
		return it.hasNext();
	  } catch (Exception e) { }
	  return false;
  }
  
  /*
   * Performs required housekeeping when this consumer is no longer needed.
   */
  public void close() {
	  it.allDone();
	  consumer.commitOffsets();
	  consumer.shutdown();
  }
  
  private void setup() {

	consumer = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig());
  
    // Request a single connection that gathers messages from all partitions 
	Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
    topicCountMap.put(topic, new Integer(1));
    
    // Construct the encoders/decoders for the message key and values
    Decoder<Long> keyDecoder = new LongCoder(new VerifiableProperties());
    Decoder<AtomicMessage> messageDecoder = new AtomicMessageCoder(new VerifiableProperties());

	// Configure and request the desired streams using the topic map, encoder and decoder
    Map<String, List<KafkaStream<Long, AtomicMessage>>> consumerMap = consumer.createMessageStreams(topicCountMap, keyDecoder, messageDecoder);

	// We only get back one stream
    KafkaStream<Long, AtomicMessage> stream =  consumerMap.get(topic).get(0);
    
    // Capture the stream's iterator
    it = stream.iterator();
  }

  private ConsumerConfig createConsumerConfig() {
	  Properties props = new Properties();
	  props.put("zookeeper.connect", zookeeperConnect);
	  props.put("group.id", groupId);
	  props.put("zookeeper.session.timeout.ms", "400");
	  props.put("zookeeper.sync.time.ms", "200");
	  props.put("auto.commit.enable", "true");
	  props.put("auto.commit.interval.ms", "1000");
	  props.put("socket.timeout.ms", "10000");
	  props.put("consumer.timeout.ms", "1000");
	  props.put("auto.offset.reset", "smallest");
	  return new ConsumerConfig(props);
  }

}