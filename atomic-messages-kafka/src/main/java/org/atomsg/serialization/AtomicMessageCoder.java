package org.atomsg.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import kafka.serializer.Decoder;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

import org.atomsg.model.AtomicMessage;

public class AtomicMessageCoder implements Decoder<AtomicMessage> , Encoder<AtomicMessage> {

	public AtomicMessageCoder(VerifiableProperties vp) {
		
	}
	
	@Override
	public AtomicMessage fromBytes(byte[] b) {
		try {
			return (AtomicMessage) new ObjectInputStream(new ByteArrayInputStream(b)).readObject();
		} catch (Exception e) {
			return null;
		}
	}
	

	@Override
	public byte[] toBytes(AtomicMessage obj) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();
			byte[] b = baos.toByteArray();
			return b;
		} catch (IOException e) {
			return new byte[0];
		}
	}

}
