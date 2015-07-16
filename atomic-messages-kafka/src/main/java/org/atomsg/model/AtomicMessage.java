package org.atomsg.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class AtomicMessage implements Serializable {

	private static final long serialVersionUID = -8793393146096621502L;

	private Long _messageGroup;
	private Long _correlationID;
	private Long _createTime;
	private Integer _fullValue;
	private Integer _currentValue;
	
	public static String[] fields = {"messageGroup", "correlationID", "createTime", "fullValue", "currentValue"};

	public AtomicMessage() {

	}

	public AtomicMessage(Long _messageGroup, Long _correlationID, Long _createTime, Integer _fullValue, Integer _currentValue) {	
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
		sb.append("AtomicMessage [messageGroup = " + _messageGroup + "; correlationID = " + _correlationID + "; createTime = " + _createTime + "; fullValue = " + _fullValue + "; currentValue = " + _currentValue + "]");
		return sb.toString();
		
	}

	public static AtomicMessage sample() {
	
		return new AtomicMessage(1L, 1L, 1L, 0, 0);
	
	}

// Begin custom methods 


// End custom methods 
	
}