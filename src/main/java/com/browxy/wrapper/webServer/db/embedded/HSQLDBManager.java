package com.browxy.wrapper.webServer.db.embedded;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;

import com.browxy.wrapper.webServer.config.Config;

public class HSQLDBManager {

    public static void startDatabaseServer() {
    	Config config = Config.getInstance();
        Thread dbThread = new Thread(() -> {
            try {
                System.out.println("Starting HSQLDB Server...");
                
                HsqlProperties hsqlProperties = new HsqlProperties();
                hsqlProperties.setProperty("server.database.0", "file:"+ config.getDataSourceFilePath());
                hsqlProperties.setProperty("server.dbname.0", config.getDataSourceDbName());
                hsqlProperties.setProperty("server.port", config.getDatasourceEmbeddedPort());

                Server server = new Server();
                server.setProperties(hsqlProperties);
                server.setLogWriter(null); 
                server.setErrWriter(null); 
                server.start();

                System.out.println("HSQLDB Server started on port 9001");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to start HSQLDB Server.");
            }
        });

        dbThread.setDaemon(true);
        dbThread.start();
    }
}
