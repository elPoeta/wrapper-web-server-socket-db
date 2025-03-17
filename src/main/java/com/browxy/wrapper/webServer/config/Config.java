package com.browxy.wrapper.webServer.config;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Config {
	private static final Logger logger = LoggerFactory.getLogger(Config.class);

	private static Config instance = null;
	private static final Object lock = new Object();

	private Map<String, String> configValues;

	private Config() {
		Properties properties = getProperties();
		configValues = getEnvFileProperties(properties);

		configValues.put("server.port", properties.getProperty("server.port"));
		configValues.put("server.staticDir", properties.getProperty("server.staticDir"));
		configValues.put("server.staticFile", properties.getProperty("server.staticFile"));
		configValues.put("socket.port", properties.getProperty("socket.port"));
		configValues.put("server.storage", properties.getProperty("server.storage"));

		configValues.put("container.basePath", properties.getProperty("container.basePath"));
		configValues.put("container.mavenRepoPath", properties.getProperty("container.mavenRepoPath"));
		configValues.put("container.mavenSettingsPath", properties.getProperty("container.mavenSettingsPath"));

		configValues.put("datasource.filePath", properties.getProperty("datasource.filePath"));
		configValues.put("datasource.embedded.port", properties.getProperty("datasource.embedded.port"));

	}

	public static Config getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}

	private static Properties getProperties() {
		String resource = System.getProperty("dev") == null ? "resource.server.properties"
				: "resource.server.dev.properties";
		Properties properties = new Properties();

		try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(resource)) {
			if (inputStream != null) {
				properties.load(inputStream);

			} else {
				logger.error("Properties file not found!");
			}
		} catch (IOException e) {
			logger.error("error reading properties file", e);
		}
		return properties;
	}

	private static Map<String, String> getEnvFileProperties(Properties properties) {
		Map<String, String> env = System.getenv();
		Map<String, String> envVars = new HashMap<>();
		for (Map.Entry<String, String> entry : env.entrySet()) {
			envVars.put(entry.getKey(), entry.getValue());
		}
		return envVars;
	}

	public String get(String key) {
		return configValues.get(key);
	}

	public void set(String key, String value) {
		configValues.put(key, value);
	}

	public int getServerPort() {
		return Integer.parseInt(configValues.get("server.port"));
	}

	public void setServerPort(int port) {
		configValues.put("server.port", String.valueOf(port));
	}

	public String getStaticDir() {
		return configValues.get("server.staticDir");
	}

	public void setStaticDir(String staticDir) {
		configValues.put("server.staticDir", staticDir);
	}

	public String getStaticFile() {
		return configValues.get("server.staticFile");
	}

	public void setStaticFile(String staticFile) {
		configValues.put("server.staticFile", staticFile);
	}

	public String getEntryPoint() {
		return configValues.get("BXY_SERVER_ENTRY_POINT");
	}

	public void setEntryPoint(String entryPoint) {
		configValues.put("BXY_SERVER_ENTRY_POINT", entryPoint);
	}

	public String getStorage() {
		return configValues.get("server.storage");
	}

	public void setStorage(String storage) {
		configValues.put("server.storage", storage);
	}

	public String getDataSourceIp() {
		return configValues.get("BXY_DATA_SOURCE_IP");
	}

	public void setDataSourceIp(String ip) {
		configValues.put("BXY_DATA_SOURCE_IP", ip);
	}

	public int getDataSourcePort() {
		return Integer.parseInt(configValues.get("BXY_DATA_SOURCE_PORT"));
	}

	public void setDataSourcePort(int port) {
		configValues.put("BXY_DATA_SOURCE_PORT", String.valueOf(port));
	}

	public String getDataSourceDbName() {
		return configValues.get("BXY_DATA_SOURCE_DB_NAME");
	}

	public void setDataSourceDbName(String dbname) {
		configValues.put("BXY_DATA_SOURCE_DB_NAME", dbname);
	}

	public String getDataSourceUserName() {
		return configValues.get("BXY_DATA_SOURCE_USER");
	}

	public void setDataSourceUserName(String username) {
		configValues.put("BXY_DATA_SOURCE_USER", username);
	}

	public String getDataSourcePassword() {
		return configValues.get("BXY_DATA_SOURCE_PASSWORD");
	}

	public void setDataSourcePassword(String password) {
		configValues.put("BXY_DATA_SOURCE_PASSWORD", password);
	}

	public String getDataSourceFilePath() {
		return configValues.get("datasource.filePath");
	}

	public void setDataSourceFilePath(String filePath) {
		configValues.put("datasource.filePath", filePath);
	}

	public String getDataSourceUrl(String connector, String encoding) {
		return !this.isDatasourceEmbedded()
				? connector + "://" + getDataSourceIp() + ":" + getDataSourcePort() + "/" + getDataSourceDbName() + "?characterEncoding=" + encoding
				: connector + ":file:" + getDataSourceFilePath()
						+ ";shutdown=true;sql.names=false;hsqldb.applog=0;sql.enforce_strict_size=false";

	}

	public String getContainerBasePath() {
		return configValues.get("container.basePath");
	}

	public void setContainerBasePath(String containerBasePath) {
		configValues.put("container.basePath", containerBasePath);
	}

	public String getContainerMavenRepoPath() {
		return configValues.get("container.mavenRepoPath");
	}

	public void setContainerMavenRepoPath(String containerMavenRepoPath) {
		configValues.put("container.mavenRepoPath", containerMavenRepoPath);
	}

	public String getContainerMavenSettingsPath() {
		return configValues.get("container.mavenSettingsPath");
	}

	public void setContainerMavenSettingsPath(String containerMavenSettingsPath) {
		configValues.put("container.mavenSettingsPath", containerMavenSettingsPath);
	}

	public int getSocketPort() {
		return Integer.valueOf(configValues.get("socket.port"));
	}

	public void setSocketPort(int port) {
		configValues.put("socket.port", String.valueOf(port));
	}

	public int getHostSocketPort() {
		String portString = configValues.get("BXY_HOST_SOCKET_PORT");
		int defaultPort = 9191;
		
		if (portString == null || portString.isEmpty()) {
		   return defaultPort;
		}

		try {
			return Integer.valueOf(portString);
		} catch (NumberFormatException e) {
           return defaultPort;
		}		
		
	}

	public void setHostSocketPort(int port) {
		configValues.put("BXY_HOST_SOCKET_PORT", String.valueOf(port));
	}

	public String getKeystorePath() {
		return configValues.get("BXY_SOCKET_KEYSTORE_PATH");
	}

	public void setKeystorePath(String keystorePath) {
		configValues.put("BXY_SOCKET_KEYSTORE_PATH", keystorePath);
	}

	public String getKeystorePassword() {
		return configValues.get("BXY_SOCKET_KEYSTORE_PASSWORD");
	}

	public void setKeystorePassword(String keystorePassword) {
		configValues.put("BXY_SOCKET_KEYSTORE_PASSWORD", keystorePassword);
	}

	public boolean isSecure() {
		return Boolean.valueOf(configValues.get("BXY_SOCKET_IS_SECURE"));
	}

	public void setIsSecure(boolean isSecure) {
		configValues.put("BXY_SOCKET_IS_SECURE", String.valueOf(isSecure));
	}

	public String getCompilerContextService() {
		return configValues.get("BXY_COMPILER_CONTEXT");
	}

	public void setCompilerContextService(String compilerContext) {
		configValues.put("BXY_COMPILER_CONTEXT", compilerContext);
	}

	public boolean isDatasourceEmbedded() {
		return Boolean.valueOf(configValues.get("BXY_DATA_SOURCE_EMBEDDED"));
	}

	public void setDatasourceEmbedded(boolean embedded) {
		configValues.put("BXY_DATA_SOURCE_EMBEDDED", String.valueOf(embedded));
	}

	public int getDatasourceEmbeddedPort() {
		return Integer.valueOf(configValues.get("datasource.embedded.port"));
	}

	public void setDatasourceEmbeddedPort(Integer port) {
		configValues.put("datasource.embedded.port", String.valueOf(port));
	}

	public String getProjectId() {
		return configValues.get("BXY_PROJECT_ID");
	}

	public void setProjectId(String projectId) {
		configValues.put("BXY_PROJECT_ID", projectId);
	}

	@Override
	public String toString() {
		return "Config [configValues=" + configValues + "]";
	}

}
