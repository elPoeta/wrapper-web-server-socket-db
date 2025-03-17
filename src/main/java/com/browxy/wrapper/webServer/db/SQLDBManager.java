package com.browxy.wrapper.webServer.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.browxy.wrapper.webServer.config.Config;

public class SQLDBManager {
	private static final Logger logger = LoggerFactory.getLogger(SQLDBManager.class);

	public static void startHSQLDatabaseServer(DatabaseInitializationCallback callback) {
		Config config = Config.getInstance();
		Thread dbThread = new Thread(() -> {
			try {
				logger.info("Starting HSQLDB Server...");

				HsqlProperties hsqlProperties = new HsqlProperties();
				hsqlProperties.setProperty("server.database.0", "file:" + config.getDataSourceFilePath());
				hsqlProperties.setProperty("server.dbname.0", config.getDataSourceDbName());
				hsqlProperties.setProperty("server.port", config.getDatasourceEmbeddedPort());
				hsqlProperties.setProperty("sql.case_sensitive", "true");

				Server server = new Server();
				server.setProperties(hsqlProperties);
				server.setLogWriter(null);
				server.setErrWriter(null);
				server.start();

				logger.info("HSQLDB Server started on port 9001");

				initializeDatabase(config);
				callback.onInitializationSuccess();

			} catch (Exception e) {
				logger.error("Error start hsql db server ", e);
				callback.onInitializationSuccess();
			}
		});

		dbThread.setDaemon(true);
		dbThread.start();
	}

	public static void initMYSQLDatabase(DatabaseInitializationCallback callback) {
		Config config = Config.getInstance();
		Thread dbThread = new Thread(() -> {
			int retries = 5;
			while (retries > 0) {
				try {
					logger.info("INIT MySQL DB...");

					initializeDatabase(config); 
					callback.onInitializationSuccess();
					return;
				} catch (Exception e) {
					logger.error("Error initializing MySQL DB. Retries left: {}", retries, e);
					retries--;
					if (retries == 0) {
						callback.onInitializationFailure(e);
					} else {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException interruptedEx) {
							Thread.currentThread().interrupt();
						}
					}
				}
			}
		});

		dbThread.setDaemon(true);
		dbThread.start();
	}

	private static void initializeDatabase(Config config) {
		try {
			DBManager dbManager = DBManager.getInstance(config.getDataSourceUserName(), config.getDataSourcePassword(),
					config.getDataSourceUrl(!config.isDatasourceEmbedded() ? "jdbc:mysql" : "jdbc:hsqldb", "UTF-8"),
					config.getDataSourceDbName());

			String scriptFilePath = config.getContainerBasePath() + File.separator + "metadata" + File.separator
					+ "init.sql";

			if (new File(scriptFilePath).exists()) {
				executeSqlScript(dbManager, scriptFilePath);
			}

			logger.info("Database initialized successfully.");
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize the database.");
		}
	}

	private static void executeSqlScript(DBManager dbManager, String scriptFilePath) throws IOException, SQLException {
		try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {
			StringBuilder sql = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty() && !line.startsWith("--")) {
					sql.append(line).append(" ");
					if (line.endsWith(";")) {
						dbManager.genericExecute("from_script", sql.toString(), new String[] {}, false);
						sql.setLength(0);
					}
				}
			}
		}
	}

}
