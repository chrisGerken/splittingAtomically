package org.atomsg.bolt;

import backtype.storm.tuple.Tuple;

import org.atomsg.bean.*;

public interface IMessageSplitterBolt {

	/*
	 *  Emits an instance of EarnedTagInfo to stream Tags
	 */
	public void emitToSplitMessages(Message message);
	
	/*
	 *  Emits an instance of EarnedTagInfo to stream Tags
	 */
	public void emitToSplitMessagesWithoutAnchor(Message message);

	/*
	 *  Emits an instance of EarnedTagInfo to stream Tags
	 */
	public void emitToAtomicMessages(Message message);
	
	/*
	 *  Emits an instance of EarnedTagInfo to stream Tags
	 */
	public void emitToAtomicMessagesWithoutAnchor(Message message);

    public void ack();

    public void fail();

	public int getTaskId();

}