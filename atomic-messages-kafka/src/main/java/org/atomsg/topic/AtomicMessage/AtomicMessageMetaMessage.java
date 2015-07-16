package org.atomsg.topic.AtomicMessage;


import org.atomsg.model.AtomicMessage;

/**
 * AtomicMessageMetaMessage provides all available information about a message from the AtomicMessage topic.
 * 
 * Information includes the message key, the topic (AtomicMessage), the message's partition and offset
 * and the message itself.
 */
public class AtomicMessageMetaMessage {

	private Long	key;
	private	AtomicMessage		message;
	private String			topic;
	private int				partition;
	private long			offset;
	
	public AtomicMessageMetaMessage(Long key, AtomicMessage message, String topic, int partition, long offset) {
		this.key		= key;
		this.message	= message;
		this.topic		= topic;
		this.partition 	= partition;
		this.offset 	= offset;
	}

	/**
	 * Answers the message key
	 * 
	 * @return Long
	 */
	public Long getKey() {
		return key;
	}

	/**
	 * Answers the message
	 * 
	 * @return AtomicMessage
	 */
	public AtomicMessage getMessage() {
		return message;
	}

	/**
	 * Answers the message's topic
	 * 
	 * @return String
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * Answers the message's partition
	 * 
	 * @return int
	 */
	public int getPartition() {
		return partition;
	}

	/**
	 * Answers the message's offset in the partition
	 * 
	 * @return long
	 */
	public long getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		return "topic=" + topic + ", partition=" + partition
				+ ", offset=" + offset + ", key=" + key.toString() + ", message="
				+ message.toString() + "]";
	}

}
