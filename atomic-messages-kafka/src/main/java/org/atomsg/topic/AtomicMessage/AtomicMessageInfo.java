package org.atomsg.topic.AtomicMessage;

import java.util.ArrayList;
import java.util.List;

import org.atomsg.common.internal.PartitionInfo;

import kafka.javaapi.PartitionMetadata;

public class AtomicMessageInfo {

	private String csBrokerList;
	private int port;
	
	public static String topicName = "AtomicMessage01";
	
	public AtomicMessageInfo(String csBrokerList, int port) {
		this.csBrokerList = csBrokerList;
		this.port = port;
	}

	public boolean topicExists() {
		return partitionInfo().length > 0;
	}
	
	public PartitionInfo[] partitionInfo() {
		return partitionInfo("fred");
	}

	public PartitionInfo[] partitionInfo(String group) {
		ArrayList<PartitionInfo> info = new ArrayList<>();
		try {
			AtomicMessageSimpleConsumer c = new AtomicMessageSimpleConsumer(csBrokerList, port, 0, group);
			List<PartitionMetadata> list = c.getMetaData().partitionsMetadata();
			for (PartitionMetadata pmd : list) {
				int part = pmd.partitionId();
				AtomicMessageSimpleConsumer cs = new AtomicMessageSimpleConsumer(csBrokerList, port, part, "ignore");
				long first = cs.requestEarliestAvailableOffset();
				long last = cs.requestLatestAvailableOffset();
				long offset = cs.retrieveOffset();
				int leaderId = pmd.leader().id();
				cs.close();  
				info.add(new PartitionInfo(topicName, part, first, last, leaderId, group, offset));
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		PartitionInfo result[] = new PartitionInfo[info.size()];
		info.toArray(result);
		return result;
	}

	public void info() {
		PartitionInfo[] info = partitionInfo();
		for (PartitionInfo pi : info) {
			System.out.println("["+pi.getPartition()+"] "+pi.getFirstAvailable()+" --> "+pi.getLastAvaialable()+"; leader: "+pi.getLeaderId()+"; "+pi.getGroupId()+"@"+pi.getCommittedOffset());
		}
	}
	
	public static void main(String[] args) {
		AtomicMessageInfo.topicName = "fred01";
		AtomicMessageInfo info = new AtomicMessageInfo("172.16.152.132",6667);
		info.info();
		System.out.println("exists: "+info.topicExists());
	}
}
