package org.atomsg.logic;

	// Begin imports 

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.atomsg.bean.Message;
import org.atomsg.bolt.IMessageWriterBolt;
import org.atomsg.model.AtomicMessage;
import org.atomsg.topic.AtomicMessage.AtomicMessageProducer;

import backtype.storm.task.TopologyContext;

	// End imports 

public class MessageWriterBoltLogic implements Serializable {

	private static final long serialVersionUID = 1L;
		
		// Begin declarations 

	private AtomicMessageProducer producer;
    private static final Logger log = Logger.getLogger(MessageWriterBoltLogic.class);

		// End declarations 
		
    public void readFromSplitMessages(Message m, IMessageWriterBolt bolt) {

			// Begin readFromSplitMessages() logic 

    	AtomicMessage am = new AtomicMessage(m.getMessageGroup(), m.getCorrelationID(), m.getCreateTime(), m.getFullValue(), m.getCurrentValue());
    	producer.send(am);
    	
    	bolt.ack();

			// End readFromSplitMessages() logic 

    }

    public void prepare(Map conf, TopologyContext context, IMessageWriterBolt bolt) {

			// Begin prepare() logic 

    	String kafka = (String) conf.get("atomic.kafka");
    	producer = new AtomicMessageProducer(kafka);

			// End prepare() logic 

    }

	/*
    *  NOTE: This method is not guaranteed to get called! Do not depend on it!
	*/
    public void cleanup(IMessageWriterBolt bolt) {

			// Begin cleanup() logic 

    	producer.close();

			// End cleanup() logic 

    }

// Begin custom methods 

// End custom methods 

}
