package org.atomsg.logic;

	// Begin imports 

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.atomsg.bean.Message;
import org.atomsg.bolt.IMessageAggregatorBolt;

import backtype.storm.task.TopologyContext;

	// End imports 

public class MessageAggregatorBoltLogic implements Serializable {

	private static final long serialVersionUID = 1L;
		
		// Begin declarations 

    private static final Logger log = Logger.getLogger(MessageAggregatorBoltLogic.class);

    private HashMap<Long, Message> messages;

		// End declarations 
		
    public void readFromAtomicMessages(Message message, IMessageAggregatorBolt bolt) {

			// Begin readFromAtomicMessages() logic 

		Long key = message.getCorrelationID();
		Message sum = null;
		if (messages.containsKey(key)) {
			sum = messages.get(key);
			sum.setCurrentValue(sum.getCurrentValue() + message.getCurrentValue());
		} else {
			messages.put(key,  message);
			sum = message;
		}
		
		log.debug("c-id "+sum.getCorrelationID()+" has sum="+sum.getCurrentValue()+" of "+sum.getFullValue());
		if (sum.getCurrentValue().intValue() == sum.getFullValue().intValue()) {
			long dur = System.currentTimeMillis() - sum.getCreateTime();
			log.info("Corelation ID "+message.getCorrelationID()+" aggregated after "+dur+" ms; "+(messages.size()-1)+" left");
			messages.remove(key);
		}
		
		bolt.ack();

			// End readFromAtomicMessages() logic 

    }

    public void prepare(Map conf, TopologyContext context, IMessageAggregatorBolt bolt) {

			// Begin prepare() logic 

    	messages = new HashMap<Long, Message>();

			// End prepare() logic 

    }

	/*
    *  NOTE: This method is not guaranteed to get called! Do not depend on it!
	*/
    public void cleanup(IMessageAggregatorBolt bolt) {

			// Begin cleanup() logic 


			// End cleanup() logic 

    }

// Begin custom methods 

// End custom methods 

}
