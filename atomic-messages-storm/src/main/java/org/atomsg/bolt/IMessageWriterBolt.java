package org.atomsg.bolt;

import backtype.storm.tuple.Tuple;

import org.atomsg.bean.*;

public interface IMessageWriterBolt {

    public void ack();

    public void fail();

	public int getTaskId();

}