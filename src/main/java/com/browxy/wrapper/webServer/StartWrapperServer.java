package com.browxy.wrapper.webServer;

import java.io.File;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import com.browxy.wrapper.webServer.config.Config;
import com.browxy.wrapper.webServer.db.HSQLInitDB;
import com.browxy.wrapper.webServer.db.MySQLInitDB;
import com.browxy.wrapper.webServer.db.SQLDBManager;
import com.browxy.wrapper.webServer.servlets.DownloadAssetServlet;
import com.browxy.wrapper.webServer.servlets.FileReaderServlet;
import com.browxy.wrapper.webServer.servlets.FileUploadServlet;
import com.browxy.wrapper.webServer.servlets.GetAssetServlet;
import com.browxy.wrapper.webServer.servlets.GetSessionServlet;
import com.browxy.wrapper.webServer.servlets.AuthServlet;
import com.browxy.wrapper.webServer.servlets.CompilerServiceServlet;
import com.browxy.wrapper.webServer.servlets.SendStaticFileServlet;


public class StartWrapperServer {
	private static final Logger logger = LoggerFactory.getLogger(StartWrapperServer.class);

	public static void main(String[] args) throws Exception {
		Config config = Config.getInstance();
		if (config == null) {
			throw new RuntimeException("Server config not loaded...");
		}

		if(config.isDatasourceEmbedded()) {
		    SQLDBManager.startHSQLDatabaseServer(new HSQLInitDB());
		} else {
			SQLDBManager.initMYSQLDatabase(new MySQLInitDB());
		}
		
		String containerBasePath = config.getContainerBasePath();
		System.setProperty("java.class.path", containerBasePath + File.separator + "target/classes");

		Thread jettyThread = new Thread(() -> startJettyServer(config, containerBasePath));
		jettyThread.start();

		if(config.getCompilerContextService().equals("websocket")) {
			int webSocketPort = config.getSocketPort();
			Thread webSocketThread = new Thread(() -> startWebSocketServer(webSocketPort));
			webSocketThread.start();
			logger.info("Jetty server started at http://localhost:" + config.getSocketPort());
		}
	}

	private static void startJettyServer(Config config, String containerBasePath) {
		try {
			Server jettyServer = new Server(config.getServerPort());
			ServletContextHandler servletContextHandler = getServletHandler(config);

			WebSocketHandler wsHandler = new WebSocketHandler() {
				@Override
				public void configure(WebSocketServletFactory factory) {
					factory.register(WebSocketServerWrapper.class);
				}
			};

			HandlerList handlers = new HandlerList();
			handlers.addHandler(servletContextHandler);
			handlers.addHandler(wsHandler);

			jettyServer.setHandler(handlers);

			jettyServer.start();
		
			jettyServer.join();
		} catch (Exception e) {
			logger.error("Error starting Jetty server", e);
		}
	}

	private static void startWebSocketServer(int port) {
		try {
			WebSocketServerWrapper webSocketServer = new WebSocketServerWrapper(new InetSocketAddress(port));
			webSocketServer.start();
			logger.info("WebSocket server started at ws://localhost:" + port);
		} catch (Exception e) {
			logger.error("Error starting WebSocket server", e);
		}
	}

	private static ServletContextHandler getServletHandler(Config config) {
		String basePath = config.getContainerBasePath();
		ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContextHandler.getSessionHandler().setMaxInactiveInterval(60 * 60);
		servletContextHandler.addServlet(
				new ServletHolder(new SendStaticFileServlet(basePath + File.separator + config.getStaticDir(),
						config.getStaticFile(), config.getEntryPoint())),
				"/*");
		servletContextHandler.addServlet(new ServletHolder(new CompilerServiceServlet()),
				"/api/v1/compilerService");
		servletContextHandler.addServlet(new ServletHolder(new FileUploadServlet(config.getStorage())),
				"/api/v1/upload");
		servletContextHandler.addServlet(new ServletHolder(new GetAssetServlet(config.getContainerBasePath())),
				"/api/v1/getAsset");
		servletContextHandler.addServlet(new ServletHolder(new DownloadAssetServlet(config.getStorage())),
				"/api/v1/downloadAsset");
		servletContextHandler.addServlet(new ServletHolder(new FileReaderServlet(basePath)), "/api/v1/readFile");
		servletContextHandler.addServlet(new ServletHolder(new GetSessionServlet()), "/api/v1/getSession");
		servletContextHandler.addServlet(new ServletHolder(new AuthServlet()), "/api/v1/auth");
		servletContextHandler.setContextPath("/");
		return servletContextHandler;
	}

}
