package org.atomsg.bolt;

	// Begin imports 

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.junit.Test;

import backtype.storm.task.OutputCollector;

import org.atomsg.bean.*;
import org.atomsg.mock.*;

	// End imports 

public class MessageAggregatorBoltTest  {
	
		// Begin declarations 

		// End declarations 

	@Test
	public void testSerialization() {
		
//		try {
//			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new MessageAggregatorBolt() );
//		} catch (Throwable t) {
//			fail("Class MessageAggregatorBoltLogic is not serializable");
//		}

	}

	@Test
	public void testReadFromAtomicMessages() {

		// Begin testReadFromAtomicMessages logic 

//		HashMap<String,Object> config = new HashMap<String,Object>();
//		MockedBoltOutputCollector collector = new MockedBoltOutputCollector();
//		
//		MockedTuple tuple = new MockedTuple("AtomicMessages", Message.sample().asValues()); 
//		MessageAggregatorBolt bolt = new MessageAggregatorBolt();

//		bolt.prepare(config,null,new OutputCollector(collector));
//		bolt.execute(tuple);
//		bolt.cleanup()

			// Validate execution side effects by interrogating collector
					
		// End testReadFromAtomicMessages logic 

	}

// Begin custom methods 


// End custom methods 

}
