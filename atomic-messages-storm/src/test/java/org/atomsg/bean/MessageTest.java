package org.atomsg.bean;

// Begin imports 

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.atomsg.util.Marshaller;
import org.junit.Test;

// End imports 

public class MessageTest {

	@Test
	public void testJsonification() {
		try {
			Marshaller.asMessage(Marshaller.asJson(Message.sample()));
		} catch (Exception e) {
			fail("Failed JSONification");
		}
	}
	
	@Test
	public void testTupilization() {
		Marshaller.asValues(Message.sample());
	}

	@Test
	public void testSerialization() {
		
		try {

			serDeser(Message.sample());
			serDeser(new Message());
			
		} catch (Throwable t) {
			fail("Object not serializable: "+t);
		}

	}

	private Message serDeser(Message obj) throws Throwable {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		oos.close();
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		return (Message) ois.readObject();
	}
	
	// Begin custom methods
	
	
	// End custom methods

	
}
