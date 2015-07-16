package org.atomsg.topic.AtomicMessage;

import java.util.List;

import kafka.javaapi.PartitionMetadata;

public class AtomicMessageInfo {

	private String csBrokerList;
	private int port;
	
	public static final String topicName = "AtomicMessage01";
	
	public AtomicMessageInfo(String csBrokerList, int port) {
		this.csBrokerList = csBrokerList;
		this.port = port;
	}

	public void info() {
		try {
			AtomicMessageSimpleConsumer c = new AtomicMessageSimpleConsumer(csBrokerList, port, 0, "fred");
			List<PartitionMetadata> list = c.getMetaData().partitionsMetadata();
			for (PartitionMetadata pmd : list) {
				int part = pmd.partitionId();
				AtomicMessageSimpleConsumer cs = new AtomicMessageSimpleConsumer(csBrokerList, port, part, "ignore");
				long first = cs.requestEarliestAvailableOffset();
				long last = cs.requestLatestAvailableOffset();
				cs.close();
				System.out.println("["+part+"] "+first+" --> "+last+"; leader: "+pmd.leader().id());
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new AtomicMessageInfo("localhost",9092).info();
	}
}
