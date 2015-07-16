package org.atomsg.spout;

import org.atomsg.bean.*;

import java.util.Map;

public interface IGeneratorSpout {

    public Map<String, Object> getComponentConfiguration();

    /*
     * Unreliably emit an instance of Message to stream NewMessages.  
     */
	public void emitToNewMessages(Message message);


    /*
     * Reliably emit an instance of Message to stream NewMessages.
     * The second parameter is to be used as a message ID for
     * notification of message ack or fail.  
     */
	public void emitToNewMessages(Message message, Message messageID);

	public int getTaskId();

}
