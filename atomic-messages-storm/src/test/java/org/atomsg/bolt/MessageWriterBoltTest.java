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

public class MessageWriterBoltTest  {
	
		// Begin declarations 

		// End declarations 

	@Test
	public void testSerialization() {
		
//		try {
//			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new MessageWriterBolt() );
//		} catch (Throwable t) {
//			fail("Class MessageWriterBoltLogic is not serializable");
//		}

	}

	@Test
	public void testReadFromSplitMessages() {

		// Begin testReadFromSplitMessages logic 

//		HashMap<String,Object> config = new HashMap<String,Object>();
//		MockedBoltOutputCollector collector = new MockedBoltOutputCollector();
//		
//		MockedTuple tuple = new MockedTuple("SplitMessages", Message.sample().asValues()); 
//		MessageWriterBolt bolt = new MessageWriterBolt();

//		bolt.prepare(config,null,new OutputCollector(collector));
//		bolt.execute(tuple);
//		bolt.cleanup()

			// Validate execution side effects by interrogating collector
					
		// End testReadFromSplitMessages logic 

	}

// Begin custom methods 


// End custom methods 

}
