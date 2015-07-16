package org.atomsg.bean;

// Begin imports 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;

// End imports 

public class Message implements Serializable, Comparable<Message> {

	private Long _messageGroup;
	private Long _correlationID;
	private Long _createTime;
	private Integer _fullValue;
	private Integer _currentValue;
	
	public static String[] fields = {"messageGroup", "correlationID", "createTime", "fullValue", "currentValue"};

	public Message() {

	}

	public Message(Long _messageGroup, Long _correlationID, Long _createTime, Integer _fullValue, Integer _currentValue) {	
		this._messageGroup = _messageGroup;
		this._correlationID = _correlationID;
		this._createTime = _createTime;
		this._fullValue = _fullValue;
		this._currentValue = _currentValue;
	}

	public Long getMessageGroup() { 
		return _messageGroup;
	}
	
	public void setMessageGroup(Long value) {
		this._messageGroup = value;
	}

	public Long getCorrelationID() { 
		return _correlationID;
	}
	
	public void setCorrelationID(Long value) {
		this._correlationID = value;
	}

	public Long getCreateTime() { 
		return _createTime;
	}
	
	public void setCreateTime(Long value) {
		this._createTime = value;
	}

	public Integer getFullValue() { 
		return _fullValue;
	}
	
	public void setFullValue(Integer value) {
		this._fullValue = value;
	}

	public Integer getCurrentValue() { 
		return _currentValue;
	}
	
	public void setCurrentValue(Integer value) {
		this._currentValue = value;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		if (_messageGroup == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_messageGroup);
		}

		if (_correlationID == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_correlationID);
		}

		if (_createTime == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeLong(_createTime);
		}

		if (_fullValue == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_fullValue);
		}

		if (_currentValue == null) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			out.writeInt(_currentValue);
		}

	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		if (in.readBoolean()) {
			_messageGroup = in.readLong();
		} else {
			_messageGroup = null;
		}

		if (in.readBoolean()) {
			_correlationID = in.readLong();
		} else {
			_correlationID = null;
		}

		if (in.readBoolean()) {
			_createTime = in.readLong();
		} else {
			_createTime = null;
		}

		if (in.readBoolean()) {
			_fullValue = in.readInt();
		} else {
			_fullValue = null;
		}

		if (in.readBoolean()) {
			_currentValue = in.readInt();
		} else {
			_currentValue = null;
		}

	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("Message [messageGroup = " + _messageGroup + "; correlationID = " + _correlationID + "; createTime = " + _createTime + "; fullValue = " + _fullValue + "; currentValue = " + _currentValue + "]");
		return sb.toString();
		
	}
	
	@Override
	public int hashCode() {

		// Begin hashCode logic 

		return super.hashCode();

		// End hashCode logic 

	}

	@Override
	public boolean equals(Object obj) {

		// Begin equals logic 

		if (obj instanceof Message) {
			Message other = (Message) obj;
		
		}

		return super.equals(obj);

		// End equals logic 

	}

	public int compareTo(Message o) {

		// Begin compare logic 


		// return -1 if this < that
		//         0 if this = that
		//         1 if this > that

		if (o instanceof Message) {
			Message other = (Message) o;
		
		}
	
		return 0;

		// End compare logic 

	}

	public static Message sample() {
	
		return new Message(1L, 1L, 1L, 0, 0);
	
	}

// Begin custom methods 


// End custom methods 
	
}