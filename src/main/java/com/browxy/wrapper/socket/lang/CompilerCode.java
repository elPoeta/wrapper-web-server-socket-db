package com.browxy.wrapper.socket.lang;

import com.browxy.wrapper.socket.message.Message;

public interface CompilerCode {
	public CompilerResult compileUserCode(Message message);
}
