package org.atomsg.logic;

	// Begin imports 

import java.io.Serializable;
import java.util.Map;

import kafka.consumer.ConsumerTimeoutException;

import org.apache.log4j.Logger;
import org.atomsg.bean.Message;
import org.atomsg.model.AtomicMessage;
import org.atomsg.spout.IMessageReaderSpout;
import org.atomsg.topic.AtomicMessage.AtomicMessageHighLevelConsumer;

import backtype.storm.task.TopologyContext;

	// End imports 

public class MessageReaderSpoutLogic implements Serializable {

		// Begin declarations
		 
	private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(MessageReaderSpoutLogic.class);
    private boolean written = false;

    private AtomicMessageHighLevelConsumer consumer;
    private long messageGroup;

		// End declarations 

    public void nextTuple(final IMessageReaderSpout spout) {

			// Begin nextTuple() logic 
			
        try {

        	AtomicMessage msg = consumer.next();
        	if (msg != null) {
        		Message message = new Message(msg.getMessageGroup(), msg.getCorrelationID(), msg.getCreateTime(), msg.getFullValue(), msg.getCurrentValue());
        		if (messageGroup != msg.getMessageGroup()) {
               		log.info("MessageReaderSpoutLogic nextTuple() old message group.  Ignoring message");
               		return;
        		}
           		log.debug("MessageReaderSpoutLogic nextTuple() read "+message.toString());
        		spout.emitToOldMessages(message, message);
        	}
        	
        } catch (ConsumerTimeoutException e) {
       		log.debug("MessageReaderSpoutLogic nextTuple() found no data.");
        } catch (Exception e) {
       		log.error("MessageReaderSpoutLogic nextTuple() error: "+ e.toString());
        }

			// End nextTuple() logic 

    }

    public void open(Map map, TopologyContext topologyContext, IMessageReaderSpout spout) {

			// Begin open() logic 
 
    	messageGroup = (Long) map.get("atomic.message.group");
    	String zkConnect = (String) map.get("atomic.zookeeper");
    	consumer = new AtomicMessageHighLevelConsumer(zkConnect, "stormReader");
 
			// End open() logic 

    }

    public void close(IMessageReaderSpout spout) {

			// Begin close() logic 

    	consumer.close();

			// End close() logic 

    }

    public void activate(IMessageReaderSpout spout) {

			// Begin activate() logic 


			// End activate() logic 

    }

    public void deactivate(IMessageReaderSpout spout) {

			// Begin deactivate() logic 


			// End deactivate() logic 

    }

    public void ack(Object o, IMessageReaderSpout spout) {

			// Begin ack() logic 


			// End ack() logic 

    }

    public void fail(Object o, IMessageReaderSpout spout) {

			// Begin fail() logic 


			// End fail() logic 

    }

// Begin custom methods 

// End custom methods 

}
