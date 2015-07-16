package org.atomsg.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.atomsg.bean.*;
import org.atomsg.util.*;

import backtype.storm.task.IOutputCollector;
import backtype.storm.tuple.Tuple;

public class MockedBoltOutputCollector implements IOutputCollector {

	public boolean acked = false;
	public boolean failed = false;
	public Throwable error = null;

	public List<Message> emittedToSplitMessages = new ArrayList<Message>();
	public List<Message> emittedToAtomicMessages = new ArrayList<Message>();

	public List<Object> others = new ArrayList<Object>();
	
	public MockedBoltOutputCollector() {

	}

	@Override
	public List<Integer> emit(String streamId, Collection<Tuple> anchors, List<Object> tuple) {
		
		List<Integer> result = new ArrayList<Integer>();
		
		if (streamId == null) { return result; }
		
		if (streamId.equals("SplitMessages")) {
			emittedToSplitMessages.add(Marshaller.asMessage(new MockedTuple(streamId, tuple)));
			return result;
		}
		if (streamId.equals("AtomicMessages")) {
			emittedToAtomicMessages.add(Marshaller.asMessage(new MockedTuple(streamId, tuple)));
			return result;
		}
		
		others.add(tuple);
		return result;
	}

	@Override
	public void emitDirect(int taskId, String streamId, Collection<Tuple> anchors, List<Object> tuple) {

	}

	@Override
	public void ack(Tuple input) {
		acked = true;
	}

	@Override
	public void fail(Tuple input) {
		failed = true;
	}

	@Override
	public void reportError(Throwable error) {
		this.error = error;
	}

}
