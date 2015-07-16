package org.atomsg.spout;

import org.atomsg.bean.*;
import org.atomsg.logic.*;
import org.atomsg.util.*;

import org.apache.log4j.Logger;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;  

import org.atomsg.topology.AtomicMessagesTopology;  

public class MessageReaderSpout implements IRichSpout, IMessageReaderSpout {

	private static final long serialVersionUID = 1L;
	private static Map<String, Object> config = null;
    private static ThreadLocal<SpoutOutputCollector> collector = new ThreadLocal<SpoutOutputCollector>();
    private volatile static boolean activated = false; 
	private MessageReaderSpoutLogic logic = new MessageReaderSpoutLogic();
	private int taskId;	

    private static final Logger log = Logger.getLogger(MessageReaderSpout.class);

    @Override
    public void nextTuple() {

        try {

			logic.nextTuple(this);

        } catch (Exception e) {
            log.error("MessageReaderSpout nextTuple() error: " + e.toString());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void open(Map config, TopologyContext topologyContext, SpoutOutputCollector collector) {
    	AtomicMessagesLogger.getInstance().configure(config);
        MessageReaderSpout.collector.set(collector);
         try { taskId = topologyContext.getThisTaskId(); }
        catch (Throwable t) { taskId = 1; }
        logic.open(config,topologyContext,this);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("OldMessages", new Fields(Message.fields));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return config;
    }

    /*
     * Unreliably emit an instance of Message to stream OldMessages.  
     */
	public void emitToOldMessages(Message message) {
		Values values = Marshaller.asValues(message);
		collector.get().emit("OldMessages",values);
	}

    /*
     * Reliably emit an instance of Message to stream OldMessages.
     * The second parameter is to be used as a message ID for
     * notification of message ack or fail.  
     */
	public void emitToOldMessages(Message message, Message messageID) {
		Values values = Marshaller.asValues(message);
		collector.get().emit("OldMessages",values,messageID);
	}

    @Override
    public void close() {
        activated = false;
        logic.close(this);
    }

    @Override
    public void ack(Object o) {
        logic.ack(o,this);
    }

    @Override
    public void fail(Object o) {
        logic.fail(o,this);
    }


    @Override
    public void activate() {
        if (!activated) {
            activated = true;
	        logic.activate(this);
        }
    }

    @Override
    public void deactivate() {
        activated = false;
        logic.deactivate(this);
    }
	
	@Override
	public int getTaskId() {
		return taskId;
	}

}
