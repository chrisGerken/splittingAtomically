package org.atomsg.bolt;

import backtype.storm.tuple.Tuple;

import org.atomsg.bean.*;

public interface IMessageAggregatorBolt {

    public void ack();

    public void fail();

	public int getTaskId();

}