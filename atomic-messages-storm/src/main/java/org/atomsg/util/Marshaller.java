package org.atomsg.util;

	// Begin imports

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import org.atomsg.exception.DataException;
import org.atomsg.bean.*;

import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

	// End imports
	
public class Marshaller {

	public static Message asMessage(Tuple tuple) {
		return new Message(
					tuple.getLong(0), 
					tuple.getLong(1), 
					tuple.getLong(2), 
					tuple.getInteger(3), 
					tuple.getInteger(4));	
	}

	public static Message asMessage(JSONObject json) throws DataException {
		try { json = json.getJSONObject("Message"); } 
		catch (Throwable t) {
			throw new DataException("Invalid JSON structure for Message constructor");
		}
		Message bean = new Message();
		if (json.has("messageGroup")) {
			try { bean.setMessageGroup(json.getLong("messageGroup")); } catch (Throwable t) {}
		}
		if (json.has("correlationID")) {
			try { bean.setCorrelationID(json.getLong("correlationID")); } catch (Throwable t) {}
		}
		if (json.has("createTime")) {
			try { bean.setCreateTime(json.getLong("createTime")); } catch (Throwable t) {}
		}
		if (json.has("fullValue")) {
			try { bean.setFullValue(json.getInt("fullValue")); } catch (Throwable t) {}
		}
		if (json.has("currentValue")) {
			try { bean.setCurrentValue(json.getInt("currentValue")); } catch (Throwable t) {}
		}
		return bean;
	}

	public static Values asValues(Message bean) {
		return new Values(bean.getMessageGroup(), bean.getCorrelationID(), bean.getCreateTime(), bean.getFullValue(), bean.getCurrentValue());
	}
	
	public static JSONObject asJson(Message bean) throws DataException {
	
		JSONObject json = new JSONObject();

		try { 
 			if (bean.getMessageGroup() != null) {
 				json.putOpt("messageGroup", bean.getMessageGroup());
 			}
 			if (bean.getCorrelationID() != null) {
 				json.putOpt("correlationID", bean.getCorrelationID());
 			}
 			if (bean.getCreateTime() != null) {
 				json.putOpt("createTime", bean.getCreateTime());
 			}
 			if (bean.getFullValue() != null) {
 				json.putOpt("fullValue", bean.getFullValue());
 			}
 			if (bean.getCurrentValue() != null) {
 				json.putOpt("currentValue", bean.getCurrentValue());
 			}
 		} catch (JSONException e) {
 			throw new DataException("JSON error when persisting Message to JSON",e);
 		}
 		
 		JSONObject result = new JSONObject();
		try { 		
			result.put("Message", json);
 		} catch (JSONException e) {
 			throw new DataException("JSON error when persisting Message to JSON",e);
 		}
		return result;
	}

	public static String asJsonString(Message bean) {
		try {
			return asJson(bean).toString();
		} catch (DataException e) {
			return "{ \"error\":\"+e.toString()+\"}";
		}
	}

	// Begin custom methods
	
	
	
	// End custom methods

}
