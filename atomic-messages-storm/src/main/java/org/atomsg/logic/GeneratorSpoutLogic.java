package org.atomsg.logic;

	// Begin imports 

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.atomsg.bean.Message;
import org.atomsg.spout.IGeneratorSpout;

import backtype.storm.task.TopologyContext;

	// End imports 

public class GeneratorSpoutLogic implements Serializable {

		// Begin declarations
		 
	private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(GeneratorSpoutLogic.class);

    private Long messageGroup;
    private Long currentId;
    private long nextEmit;
    private int maxMessages;
    private int messagesWritten = 1;
    private int full;
    
		// End declarations 

    public void nextTuple(final IGeneratorSpout spout) {

			// Begin nextTuple() logic 
			
        try {

        	if (nextEmit > System.currentTimeMillis()) {
        		return;
        	}
        	if (maxMessages < 1) {
        		return;
        	}

        	int value = full;
        	Message message = new Message(messageGroup, currentId, System.currentTimeMillis(), value, value);
        	spout.emitToNewMessages(message, message);
        	
        	messagesWritten++;
        	currentId++;
        	maxMessages--;
//        	nextEmit = System.currentTimeMillis() + 100L;
        	
        } catch (Exception e) {
       		log.error("GeneratorSpoutLogic nextTuple() error: "+ e.toString());
        }

			// End nextTuple() logic 

    }

    public void open(Map map, TopologyContext topologyContext, IGeneratorSpout spout) {

			// Begin open() logic 
 
    	messageGroup = (Long) map.get("atomic.message.group");
    	currentId = 0L;
    	nextEmit = 0L;
    	long l = (Long) map.get("atomic.max.messages");
    	maxMessages = (int) l;
    	l = (Long) map.get("atomic.full.value");
    	full = (int) l;
 
			// End open() logic 

    }

    public void close(IGeneratorSpout spout) {

			// Begin close() logic 


			// End close() logic 

    }

    public void activate(IGeneratorSpout spout) {

			// Begin activate() logic 


			// End activate() logic 

    }

    public void deactivate(IGeneratorSpout spout) {

			// Begin deactivate() logic 


			// End deactivate() logic 

    }

    public void ack(Object o, IGeneratorSpout spout) {

			// Begin ack() logic 


			// End ack() logic 

    }

    public void fail(Object o, IGeneratorSpout spout) {

			// Begin fail() logic 


			// End fail() logic 

    }

// Begin custom methods 

// End custom methods 

}
