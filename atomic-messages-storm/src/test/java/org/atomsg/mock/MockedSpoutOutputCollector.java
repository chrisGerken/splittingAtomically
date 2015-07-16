package org.atomsg.mock;

import java.util.ArrayList;
import java.util.List;

import org.atomsg.bean.*;
import org.atomsg.util.*;

import backtype.storm.spout.ISpoutOutputCollector;

public class MockedSpoutOutputCollector implements ISpoutOutputCollector {
	
	public List<Message> emittedToNewMessages = new ArrayList<Message>();
	public List<Message> idsForNewMessages = new ArrayList<Message>();
	public List<Message> emittedToOldMessages = new ArrayList<Message>();
	public List<Message> idsForOldMessages = new ArrayList<Message>();

	public List<Object> others = new ArrayList<Object>();

	public MockedSpoutOutputCollector() {
	}

	@Override
	public List<Integer> emit(String streamId, List<Object> tuple, Object messageId) {
		
		List<Integer> result = new ArrayList<Integer>();
		
		if (streamId == null) { return result; }
		
		if (streamId.equals("NewMessages")) {
			emittedToNewMessages.add(Marshaller.asMessage(new MockedTuple(streamId, tuple)));
			if (messageId == null) {
				messageId = Message.sample();
			}
			idsForNewMessages.add((Message)messageId);
			return result;
		}
		if (streamId.equals("OldMessages")) {
			emittedToOldMessages.add(Marshaller.asMessage(new MockedTuple(streamId, tuple)));
			if (messageId == null) {
				messageId = Message.sample();
			}
			idsForOldMessages.add((Message)messageId);
			return result;
		}
		
		others.add(tuple);
		return result;
		
	}

	@Override
	public void emitDirect(int taskId, String streamId, List<Object> tuple,
			Object messageId) {  
	}

	@Override
	public void reportError(Throwable error) {
	}

}
