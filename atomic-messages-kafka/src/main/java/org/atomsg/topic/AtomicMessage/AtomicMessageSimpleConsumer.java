package org.atomsg.topic.AtomicMessage;

import java.nio.ByteBuffer;

import kafka.message.MessageAndOffset;
import kafka.utils.VerifiableProperties;

import org.atomsg.model.AtomicMessage;
import org.atomsg.serialization.LongCoder;
import org.atomsg.serialization.AtomicMessageCoder;
import org.atomsg.common.exception.PartitionConsumerException;
import org.atomsg.common.internal.AbstractSimpleConsumer;

/**
 * AtomicMessageSimpleConsumer provides an iterator-link access to the messages on a single partition 
 * of topic AtomicMessage.
 * <pre>
 * Usage:
 * 
 *     AtomicMessageSimpleConsumer consumer = new AtomicMessageSimpleConsumer("localhost", 9092, 2, "group-two");
 *     
 *     while (consumer.hasNext()) {
 *         AtomicMessage atomicMessage = consumer.next();
 *     }
 *     
 *     consumer.close();
 *</pre>     
 */
public class AtomicMessageSimpleConsumer extends AbstractSimpleConsumer {

	private LongCoder keyCoder = new LongCoder(new VerifiableProperties());
	private AtomicMessageCoder messageCoder = new AtomicMessageCoder(new VerifiableProperties());
	
	  /**
	   * Creates a simple consumer that reads from a single partition for topic AtomicMessage
	   * 
	   * @param csBrokerList A String containing a comma-separated list of hosts
	   *  (e.g. 'host1,host2').  The hosts have running Kafka brokers.
	   * @param groupId Client group name.  Used to manage last-read offsets for partitions.
	   */
	public AtomicMessageSimpleConsumer(String csBrokerList, int port, int partition, String groupName) {
		super(csBrokerList, port, AtomicMessageInfo.topicName, partition, groupName);
	}

	/**
	 * Returns the next message from the partition
	 * 
	 * @return AtomicMessage
	 */
	public AtomicMessage next() {

		ByteBuffer payload = super.nextMessage().message().payload();
        byte[] bytes = new byte[payload.limit()];
        payload.get(bytes);
        return messageCoder.fromBytes(bytes);
        
	}

    /**
	  * Returns the next meta-message from the partition.  The meta-message is able to answer the key, 
	  * message, topic, partition and offset for the message.
	  * 
	  * @return AtomicMessageMetaMessage
	  */
	public AtomicMessageMetaMessage nextMetaMessage() {

		ByteBuffer buf;
		byte[] bytes;
		MessageAndOffset mao = super.nextMessage();
		
		buf = mao.message().payload();
        bytes = new byte[buf.limit()];
        buf.get(bytes);
        AtomicMessage message = messageCoder.fromBytes(bytes);
		
		buf = mao.message().key();
        bytes = new byte[buf.limit()];
        buf.get(bytes);
        Long key = keyCoder.fromBytes(bytes);

		return new AtomicMessageMetaMessage(key, message, AtomicMessageInfo.topicName, partition, mao.offset());
        
	}
		
}
