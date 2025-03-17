package com.browxy.wrapper.webServer.db;

public interface DatabaseInitializationCallback {
	void onInitializationSuccess();

	void onInitializationFailure(Exception e);
}
