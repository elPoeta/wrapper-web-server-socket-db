package com.browxy.wrapper.socket.response;

import com.browxy.wrapper.socket.message.Message;
import com.browxy.wrapper.socket.message.MessageFactory;

public class ResponseHandler {
	private Message message;
	private ResponseMessage responseMessage;

	public ResponseHandler(String jsonMessage) {
		this.message = MessageFactory.createMessage(jsonMessage);
		this.responseMessage = ResponseFactory.createResponse(this.message);
	}

	public String getResponse() {
		return this.responseMessage.handleClientRequest();
	}
}
