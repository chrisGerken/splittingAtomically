package org.atomsg.spout;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.junit.Test;

import backtype.storm.spout.SpoutOutputCollector;

import org.atomsg.bean.*;
import org.atomsg.mock.*;

public class GeneratorSpoutTest {
 
	@Test
	public void testSerialization() {
		
//		try {
//			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new GeneratorSpout() );
//		} catch (Throwable t) {
//			fail("Class GeneratorSpout is not serializable");
//		}

	}

	@Test
	public void testNextTuple() {
		
//		GeneratorSpout spout = new GeneratorSpout();

//		MockedSpoutOutputCollector collector = new MockedSpoutOutputCollector();
//		HashMap<String,Object> config = new HashMap<String, Object>();
		
//		spout.open(config,null,new SpoutOutputCollector(collector));
//		spout.nextTuple();
//		spout.close();
		
		// Validate side effects of nextTuple() call by interrogating collector
		
	}

}
