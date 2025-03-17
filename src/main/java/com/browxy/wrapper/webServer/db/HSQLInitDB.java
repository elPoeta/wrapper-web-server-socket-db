package com.browxy.wrapper.webServer.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSQLInitDB implements DatabaseInitializationCallback {
	private static final Logger logger = LoggerFactory.getLogger(HSQLInitDB.class);
			
	@Override
	public void onInitializationSuccess() {
		logger.info("Database hsql initialized successfully.");
	}

	@Override
	public void onInitializationFailure(Exception e) {
		logger.error("Failed to initialize hsql database",e);

	}

}
