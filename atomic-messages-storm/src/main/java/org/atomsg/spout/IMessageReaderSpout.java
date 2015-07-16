package org.atomsg.spout;

import org.atomsg.bean.*;

import java.util.Map;

public interface IMessageReaderSpout {

    public Map<String, Object> getComponentConfiguration();

    /*
     * Unreliably emit an instance of Message to stream OldMessages.  
     */
	public void emitToOldMessages(Message message);


    /*
     * Reliably emit an instance of Message to stream OldMessages.
     * The second parameter is to be used as a message ID for
     * notification of message ack or fail.  
     */
	public void emitToOldMessages(Message message, Message messageID);

	public int getTaskId();

}
