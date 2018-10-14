package api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message implements Serializable {
//	#JsonProperty("message_id")
	int MessageID;
//	@JsonProperty("subject")
	String Subject;
//	@JsonProperty("message")
	String Message;	
	
	
	public Message(int MessageID, String Message, String Action) {
		this.MessageID = MessageID;
		this.Subject = Action;
		this.Message = Message;
	}
	
	public int getMessageID() {
		return MessageID;
	}
	public void setMessageID(int messageID) {
		MessageID = messageID;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	public String getSubject() {
		return Subject;
	}
	public void setSubject(String subject) {
		Subject = subject;
	}


}
