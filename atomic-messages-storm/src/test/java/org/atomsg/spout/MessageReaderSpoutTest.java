package org.atomsg.spout;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.junit.Test;

import backtype.storm.spout.SpoutOutputCollector;

import org.atomsg.bean.*;
import org.atomsg.mock.*;

public class MessageReaderSpoutTest {
 
	@Test
	public void testSerialization() {
		
//		try {
//			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new MessageReaderSpout() );
//		} catch (Throwable t) {
//			fail("Class MessageReaderSpout is not serializable");
//		}

	}

	@Test
	public void testNextTuple() {
		
//		MessageReaderSpout spout = new MessageReaderSpout();

//		MockedSpoutOutputCollector collector = new MockedSpoutOutputCollector();
//		HashMap<String,Object> config = new HashMap<String, Object>();
		
//		spout.open(config,null,new SpoutOutputCollector(collector));
//		spout.nextTuple();
//		spout.close();
		
		// Validate side effects of nextTuple() call by interrogating collector
		
	}

}
