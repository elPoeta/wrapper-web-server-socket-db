package com.browxy.wrapper.webServer.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLInitDB  implements DatabaseInitializationCallback {
	private static final Logger logger = LoggerFactory.getLogger(MySQLInitDB.class);
			
	@Override
	public void onInitializationSuccess() {
		logger.info("Database Mysql initialized successfully.");
	}

	@Override
	public void onInitializationFailure(Exception e) {
		logger.error("Failed to initialize Mysql database",e);

	}
}
