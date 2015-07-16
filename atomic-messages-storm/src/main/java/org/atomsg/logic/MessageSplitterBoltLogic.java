package org.atomsg.logic;

	// Begin imports 

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.atomsg.bean.Message;
import org.atomsg.bolt.IMessageSplitterBolt;

import backtype.storm.task.TopologyContext;

	// End imports 

public class MessageSplitterBoltLogic implements Serializable {

	private static final long serialVersionUID = 1L;
		
		// Begin declarations 

    private static final Logger log = Logger.getLogger(MessageSplitterBoltLogic.class);

		// End declarations 
		
    public void readFromNewMessages(Message message, IMessageSplitterBolt bolt) {

			// Begin readFromNewMessages() logic 

    	readMessage(message,bolt);

			// End readFromNewMessages() logic 

    }
		
    public void readFromOldMessages(Message message, IMessageSplitterBolt bolt) {

			// Begin readFromOldMessages() logic 

    	readMessage(message,bolt);

			// End readFromOldMessages() logic 

    }

    private void readMessage(Message m, IMessageSplitterBolt bolt) {
    	
    	if (m.getCurrentValue() < 2) {
    		bolt.emitToAtomicMessages(m);
    		bolt.ack();
    		return;
    	}

    	Message other = new Message(m.getMessageGroup(),m.getCorrelationID(), m.getCreateTime(), m.getFullValue(), m.getCurrentValue());
    	log.info("Splitting: "+m);
    	int value = m.getCurrentValue() / 2;
    	other.setCurrentValue(value);
    	bolt.emitToSplitMessages(other);
    	
    	value = m.getCurrentValue() - value;
    	m.setCurrentValue(value);
    	bolt.emitToSplitMessages(m);
    	
    	bolt.ack();
	}

	public void prepare(Map conf, TopologyContext context, IMessageSplitterBolt bolt) {

			// Begin prepare() logic 

			// End prepare() logic 

    }

	/*
    *  NOTE: This method is not guaranteed to get called! Do not depend on it!
	*/
    public void cleanup(IMessageSplitterBolt bolt) {

			// Begin cleanup() logic 


			// End cleanup() logic 

    }

// Begin custom methods 

// End custom methods 

}
