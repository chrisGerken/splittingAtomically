package org.atomsg.common.internal;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import kafka.api.PartitionFetchInfo;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.OffsetMetadataAndError;
import kafka.common.OffsetOutOfRangeException;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchRequest;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetCommitRequest;
import kafka.javaapi.OffsetFetchRequest;
import kafka.javaapi.OffsetFetchResponse;
import kafka.javaapi.OffsetRequest;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.TopicMetadataResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import kafka.network.RequestChannel.RequestLogger;

import org.atomsg.common.exception.*;

public class AbstractSimpleConsumer {
	
	// List of brokers managing the topic's partitions
	private List<String> replicaBrokers = new ArrayList<String>();
	
	// List of (at least some of the) brokers (host:port) in the Kafka cluster 
	private List<String> seedBrokers = new ArrayList<String>();

	// The topic from which to read
	protected String topic;
	
	// The partition of the topic from which to read
	protected int partition;
	
	// The name of the client group
	private String groupName;
	
	// How long to wait
	private int timeout = 10000;
	
	// Number of bytes to fetch
	private int fetchSize = 100000;
	
	// How many times to retry a fetch before failing
	private int fetchRetries = 5;
	
	private String clientName;
	
	// An arbitrary string of data to be stored with partition offset
	private String consumerMetadata = "";
	
	private SimpleConsumer simpleConsumer = null;
	
	private Broker leadBroker = null;
	
	private MessageAndOffset nextMessageAndOffset = null;
	
	// Offset of the first unread message (the next message to be read)
	// This value advances as messages are read in preparation for fast return to a client
	private Long firstUnread = null;
	
	// Offset of the last returned message
	// This value will only advance when a message is returned to a client and is used on close()
	private Long nextOffsetToReturn = null;
	
	// Iterator over messages retrieved by most recent fetch
	private Iterator<MessageAndOffset> messageSetIterator;
	
	// How long (milliseconds) to sleep if there are no messages in the partition
	private long emptyWait = 1000;
	
	// Force the consumer to start reading at the beginning of the partition
	private boolean startAtBeginning = false;
	
	// Force the consumer to start reading at the end of the partition
	private boolean startAtEnd = false;
	
	public AbstractSimpleConsumer(String csBrokerList, String topic, int partition, String groupName) {
		this.seedBrokers = parse(csBrokerList);
		this.topic = topic;
		this.partition = partition;
		this.groupName = groupName;
        clientName = "Client_" + topic + "_" + partition;
//        determineFirstUnread();
	}

	private List<String> parse(String csBrokerList) {
		List<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(csBrokerList, ", \t\n");
		while (st.hasMoreTokens()) {
			list.add(st.nextToken());
		}
		return list;
	}

	public boolean hasNext() {
		if (nextMessageAndOffset == null) {
			nextMessageAndOffset = nextMessage();
		}
		return nextMessageAndOffset != null;
	}
	
	protected MessageAndOffset nextMessage() {
		for (int attempt = 0; attempt < fetchRetries; attempt++) {
			try {
				MessageAndOffset mao = nextMessage0();
				return mao;
			} catch (PartitionConsumerException e) {
			}
		}
		return null;
	}
	
	private MessageAndOffset nextMessage0() throws PartitionConsumerException {
		MessageAndOffset result  = null;
		
		if (nextMessageAndOffset != null) {
			result = nextMessageAndOffset;
			nextMessageAndOffset = null;
			nextOffsetToReturn = result.nextOffset();
			return result;
		}

		if ((messageSetIterator == null) || (!messageSetIterator.hasNext())) {
			messageSetIterator = fetch();
			if (!messageSetIterator.hasNext()) {
				messageSetIterator = null;
				try { Thread.sleep(emptyWait); } catch (Throwable t) {  }
				return null;
			}
		}
		
		MessageAndOffset messageAndOffset = messageSetIterator.next();
        long currentOffset = messageAndOffset.offset();
        if (currentOffset < firstUnread) {
            throw new OldMessageException("Found an old offset: " + currentOffset + " Expecting: " + firstUnread);
        }

        firstUnread = messageAndOffset.nextOffset();
		nextOffsetToReturn = messageAndOffset.nextOffset();
        return messageAndOffset;
		
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private Iterator<MessageAndOffset> fetch() throws PartitionConsumerException {

    	if (firstUnread == null) {
    		determineFirstUnread();
    	}
    	
    	Map<TopicAndPartition,PartitionFetchInfo> requestInfo = new HashMap<TopicAndPartition,PartitionFetchInfo>();
    	TopicAndPartition tap = new TopicAndPartition(topic,partition);
    	PartitionFetchInfo pfi = new PartitionFetchInfo(firstUnread, fetchSize);
    	requestInfo.put(tap,pfi);
        FetchRequest fetchRequest = new FetchRequest(0, clientName, timeout, 1, requestInfo);
        FetchResponse fetchResponse;
		try {
			fetchResponse = getSimpleConsumer().fetch(fetchRequest);
		} catch (Exception e) {
	        return new ByteBufferMessageSet(new ArrayList()).iterator();
		}
 
        if (fetchResponse.hasError()) {

            short code = fetchResponse.errorCode(topic, partition);
            if (code == ErrorMapping.OffsetOutOfRangeCode())  {
                // We asked for an invalid offset. For simple case ask for the last element to reset
                firstUnread = retrieveOffset();
                cleanConsumer();
                throw new OffsetOutOfRangeException();
            }

            cleanConsumer();
            throw new PartitionConsumerException("Error fetching data from the Broker:" + getLeadBroker() + " Reason: " + code);

        }
                
        ByteBufferMessageSet bbms = fetchResponse.messageSet(topic, partition);
        return bbms.iterator();
        
    }
    
    private SimpleConsumer getSimpleConsumer() throws PartitionConsumerException {
    	if (simpleConsumer==null) { 
    		simpleConsumer = new SimpleConsumer(getLeadBroker().host(), getLeadBroker().port(), timeout, 64 * 1024, clientName);
    	}
    	return simpleConsumer;
    }
    
    private void cleanConsumer() {
    	if (simpleConsumer!=null) { 
    		simpleConsumer.close();
    	}
    	simpleConsumer = null;
        // Force a search for a new lead broker
        leadBroker = null;
    }
    
    public void close() throws PartitionConsumerException {
    	cleanConsumer();
    	if (nextOffsetToReturn!=null) {
        	commitOffset(nextOffsetToReturn);
    	}
    }
    
    private Broker getLeadBroker() throws PartitionConsumerException {
    	if (leadBroker==null) {
    		leadBroker = findNewLeadBroker();
    	}
    	return leadBroker;
    }

	private void determineFirstUnread() {
		
		try {

			long earliest = requestEarliestAvailableOffset();
			long latest = requestLatestAvailableOffset();
			if (startAtBeginning) { 
				// Flag was set forcing us back to the beginning of the partition
				firstUnread = earliest;
			} else if (startAtEnd) {
				// Flag was set forcing us forward to the end (most recent message) of the partition
				firstUnread = latest + 1;
			} else {
				// Look to see if we previously committed an offset for this partition
				firstUnread = retrieveOffset();
				
				if (firstUnread < earliest) {
					firstUnread = earliest;
				}
				
				if (firstUnread > latest+1) {
					firstUnread = latest+1;
				}
			}
			
		} catch (PartitionConsumerException e) {
			
			// gotta start somewhere
			firstUnread = 0L; 
		}

		
	}

    /*
     * Commits the given offset as being the offset of the next message to be read. 
     */
	private void commitOffset(long offset) throws PartitionConsumerException {
    	
        Map<TopicAndPartition, OffsetMetadataAndError> requestInfo = new HashMap<TopicAndPartition, OffsetMetadataAndError>();
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        OffsetMetadataAndError omae = new OffsetMetadataAndError(offset, consumerMetadata, ErrorMapping.NoError());
        requestInfo.put(topicAndPartition, omae);
        
    	OffsetCommitRequest request = new OffsetCommitRequest(groupName, requestInfo, (short) 0, 0, clientName);
    	
    	getSimpleConsumer().commitOffsets(request);

	}

    /*
     * Retrieves the previously committed offset. If no offset has been committed, yet, a -1 is returned.
     */
	public long retrieveOffset() throws PartitionConsumerException {
    	
        List<TopicAndPartition> requestInfo = new ArrayList<TopicAndPartition>();
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        requestInfo.add(topicAndPartition);
        
    	OffsetFetchRequest request = new OffsetFetchRequest(groupName, requestInfo, (short) 0, 0, clientName);
    	
    	OffsetFetchResponse response = getSimpleConsumer().fetchOffsets(request);
    	Map<TopicAndPartition, OffsetMetadataAndError> offsets = response.offsets();
    	OffsetMetadataAndError omae = offsets.get(new TopicAndPartition(topic, partition));
    	
    	return omae.offset();

	}
    
	public long requestEarliestAvailableOffset() throws PartitionConsumerException {
		return requestOffset(-2);
	}
    
	public long requestLatestAvailableOffset() throws PartitionConsumerException {
		return requestOffset(-1);
	}
	
	/*
	 * Request the offset for the current partition:
	 *   time = -1 returns the latest offset
	 *   time = -2 returns the earliest available offset
	 */
    private long requestOffset(long time) throws PartitionConsumerException {
        
    	TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(time, 1));
        
        OffsetRequest request = new OffsetRequest(requestInfo, (short)0, clientName);
        OffsetResponse response = getSimpleConsumer().getOffsetsBefore(request);
 
        if (response.hasError()) {
        	throw new PartitionConsumerException("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }
    
    public TopicMetadata getMetaData() throws PartitionConsumerException {

        for (String seed : seedBrokers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(hostFrom(seed), portFrom(seed), timeout, 64 * 1024, "leaderLookup");
                List<String> topics = Collections.singletonList(topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                TopicMetadataResponse resp = consumer.send(req);
 
                List<TopicMetadata> metaData = resp.topicsMetadata();
                return metaData.get(0);
            } catch (Exception e) {
            	throw new PartitionConsumerException("Error communicating with Broker ('" + seed + "') to find meta data for topic ('"+topic+"')",e);
            } finally {
                if (consumer != null) consumer.close();
            }
        }

       	throw new PartitionConsumerException("Can't find metadata for topic ('"+topic+"') and partition ("+partition+")");

    }
 
    private int portFrom(String seed) {
    	int index = seed.indexOf(":");
    	if (index == -1) { return 9092; }
		try {
			return Integer.parseInt(seed.substring(index+1));
		} catch (NumberFormatException e) {
			
		}
		return 9092;
	}

	private String hostFrom(String seed) {
    	int index = seed.indexOf(":");
    	if (index == -1) { return seed; }
		return seed.substring(0,index);
	}

	private Broker findNewLeadBroker() throws PartitionConsumerException {

        for (String seed : seedBrokers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(hostFrom(seed), portFrom(seed), timeout, 64 * 1024, "leaderLookup");
                List<String> topics = Collections.singletonList(topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                TopicMetadataResponse resp = consumer.send(req);
 
                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        if (part.partitionId() == partition) {

                            replicaBrokers.clear();
                            for (kafka.cluster.Broker replica : part.replicas()) {
                                replicaBrokers.add(replica.host());
                            }

                            if (part.leader() == null) {
                            	throw new PartitionConsumerException("Can't find leader for topic ('"+topic+"') and partition ("+partition+")");
                            }
                            
                            return part.leader();
                        }
                    }
                }
            } catch (Exception e) {
            	throw new PartitionConsumerException("Error communicating with Broker ('" + seed + "') to find Leader for topic ('"+topic+"') and partition ("+partition+")",e);
            } finally {
                if (consumer != null) consumer.close();
            }
        }

       	throw new PartitionConsumerException("Can't find metadata for topic ('"+topic+"') and partition ("+partition+")");

    }

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void setTimeout(String timeout) {
		if (timeout==null) { return; }
		this.timeout = Integer.parseInt(timeout);
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	public void setFetchSize(String fetchSize) {
		if (fetchSize==null) { return; }
		this.fetchSize = Integer.parseInt(fetchSize);
	}

	public void setFetchRetries(int fetchRetries) {
		this.fetchRetries = fetchRetries;
	}

	public void setFetchRetries(String fetchRetries) {
		if (fetchRetries==null) { return; }
		this.fetchRetries = Integer.parseInt(fetchRetries);
	}

	public void setEmptyWait(long emptyWait) {
		this.emptyWait = emptyWait;
	}

	public void setEmptyWait(String emptyWait) {
		if (emptyWait==null) { return; }
		this.emptyWait = Long.parseLong(emptyWait);
	}

	public void setStartAtBeginning(boolean startAtBeginning) {
		this.startAtBeginning = startAtBeginning;
	}

	public void setStartAtBeginning(String startAtBeginning) {
		if (startAtBeginning==null) { return; }
		this.startAtBeginning = Boolean.parseBoolean(startAtBeginning);
	}

	public void setStartAtEnd(boolean startAtEnd) {
		this.startAtEnd = startAtEnd;
	}

	public void setStartAtEnd(String startAtEnd) {
		if (startAtEnd==null) { return; }
		this.startAtEnd = Boolean.parseBoolean(startAtEnd);
	}
  
}
