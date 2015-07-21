package org.atomsg.common.internal;

public class PartitionInfo {
	
	private String 	topic;
	private int 	partition;
	private long 	firstAvailable;
	private long 	lastAvaialable;
	private String 	groupId;
	private long 	committedOffset;  
	private int 	leaderId;
	
	public PartitionInfo(String topic, int partition, long firstAvailable,
			long lastAvaialable, int leaderId, String groupId,
			long committedOffset) {
		super();
		this.topic = topic;
		this.partition = partition;
		this.firstAvailable = firstAvailable;
		this.lastAvaialable = lastAvaialable;
		this.leaderId = leaderId;
		this.groupId = groupId;
		this.committedOffset = committedOffset;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public int getPartition() {
		return partition;
	}
	
	public void setPartition(int partition) {
		this.partition = partition;
	}
	
	public long getFirstAvailable() {
		return firstAvailable;
	}
	
	public void setFirstAvailable(long firstAvailable) {
		this.firstAvailable = firstAvailable;
	}
	
	public long getLastAvaialable() {
		return lastAvaialable;
	}
	
	public void setLastAvaialable(long lastAvaialable) {
		this.lastAvaialable = lastAvaialable;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public long getCommittedOffset() {
		return committedOffset;
	}
	
	public void setCommittedOffset(long committedOffset) {
		this.committedOffset = committedOffset;
	}
	
	public int getLeaderId() {
		return leaderId;
	}

	public void setLeaderId(int leaderId) {
		this.leaderId = leaderId;
	}


}
