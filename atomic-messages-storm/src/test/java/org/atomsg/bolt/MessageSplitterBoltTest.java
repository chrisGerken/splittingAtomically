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

public class MessageSplitterBoltTest  {
	
		// Begin declarations 

		// End declarations 

	@Test
	public void testSerialization() {
		
//		try {
//			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new MessageSplitterBolt() );
//		} catch (Throwable t) {
//			fail("Class MessageSplitterBoltLogic is not serializable");
//		}

	}

	@Test
	public void testReadFromNewMessages() {

		// Begin testReadFromNewMessages logic 

//		HashMap<String,Object> config = new HashMap<String,Object>();
//		MockedBoltOutputCollector collector = new MockedBoltOutputCollector();
//		
//		MockedTuple tuple = new MockedTuple("NewMessages", Message.sample().asValues()); 
//		MessageSplitterBolt bolt = new MessageSplitterBolt();

//		bolt.prepare(config,null,new OutputCollector(collector));
//		bolt.execute(tuple);
//		bolt.cleanup()

			// Validate execution side effects by interrogating collector
					
		// End testReadFromNewMessages logic 

	}

	@Test
	public void testReadFromOldMessages() {

		// Begin testReadFromOldMessages logic 

//		HashMap<String,Object> config = new HashMap<String,Object>();
//		MockedBoltOutputCollector collector = new MockedBoltOutputCollector();
//		
//		MockedTuple tuple = new MockedTuple("OldMessages", Message.sample().asValues()); 
//		MessageSplitterBolt bolt = new MessageSplitterBolt();

//		bolt.prepare(config,null,new OutputCollector(collector));
//		bolt.execute(tuple);
//		bolt.cleanup()

			// Validate execution side effects by interrogating collector
					
		// End testReadFromOldMessages logic 

	}

// Begin custom methods 


// End custom methods 

}
