package org.atomsg.topic.AtomicMessage;

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import kafka.utils.VerifiableProperties;

import org.junit.Test;

import org.atomsg.model.AtomicMessage;

public class AtomicMessageTopicTest {

	@Test
	public void testSerialization() {
		
		// Begin custom serialization test logic 

		try {
			new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(new AtomicMessage() );
		} catch (Throwable t) {
			fail("AtomicMesage is not serializable");
		}

		// End custom serialization test logic

	}

}
