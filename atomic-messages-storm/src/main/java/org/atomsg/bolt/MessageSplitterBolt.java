package org.atomsg.bolt;

import org.atomsg.bean.*;
import org.atomsg.logic.*;
import org.atomsg.topology.AtomicMessagesTopology;
import org.atomsg.util.Marshaller;
import org.atomsg.util.IAtomicMessagesLogger;

import org.apache.log4j.Logger;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map; 

public class MessageSplitterBolt implements IRichBolt, IMessageSplitterBolt {

	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map<String, Object> config;
    private static final Logger log = Logger.getLogger(MessageSplitterBolt.class);
	private MessageSplitterBoltLogic logic = new MessageSplitterBoltLogic();
	private Tuple anchor = null;
	private int taskId;	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void prepare(Map config, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.config = config;
         try { taskId = context.getThisTaskId(); }
        catch (Throwable t) { taskId = 1; }
        logic.prepare(config,context,this);		
    }

	/*
	 *  Emits an instance of Message to stream SplitMessages
	 */
	public void emitToSplitMessages(Message message) {
		Values values = Marshaller.asValues(message);
		collector.emit("SplitMessages",anchor,values);
	}


	/*
	 *  Emits an instance of Message to stream SplitMessages
	 */
	public void emitToSplitMessagesWithoutAnchor(Message message) {
		Values values = Marshaller.asValues(message);
		collector.emit("SplitMessages",values);
	}

	/*
	 *  Emits an instance of Message to stream AtomicMessages
	 */
	public void emitToAtomicMessages(Message message) {
		Values values = Marshaller.asValues(message);
		collector.emit("AtomicMessages",anchor,values);
	}


	/*
	 *  Emits an instance of Message to stream AtomicMessages
	 */
	public void emitToAtomicMessagesWithoutAnchor(Message message) {
		Values values = Marshaller.asValues(message);
		collector.emit("AtomicMessages",values);
	}

    @Override
    public void execute(Tuple tuple) {
    	anchor = tuple;
	    try {
	    	if ("NewMessages".equals(tuple.getSourceStreamId())) {
	    		Message message = Marshaller.asMessage(tuple);
	            logic.readFromNewMessages(message, this);
	    	}
	    	if ("OldMessages".equals(tuple.getSourceStreamId())) {
	    		Message message = Marshaller.asMessage(tuple);
	            logic.readFromOldMessages(message, this);
	    	}
		} catch (Exception e) { 
			log.error("MessageSplitterBolt execute() error", e); 
	    }
	}
	
    @Override
    /*
    *  NOTE: This method is not guaranteed to get called! Do not depend on it!
    */
    public void cleanup() {
		logic.cleanup(this);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("SplitMessages", new Fields(Message.fields));
        declarer.declareStream("AtomicMessages", new Fields(Message.fields));
    }
    
    public void ack() {
    	collector.ack(anchor);
    }
    
    public void fail() {
    	collector.fail(anchor);
    }

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return config;
	}
	
	@Override
	public int getTaskId() {
		return taskId;
	}

}
