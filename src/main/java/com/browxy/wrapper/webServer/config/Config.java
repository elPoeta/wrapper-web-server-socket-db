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
		return configValues.get("server.entryPoint");
	}

	public void setEntryPoint(String entryPoint) {
		configValues.put("server.entryPoint", entryPoint);
	}

	public String getStorage() {
		return configValues.get("server.storage");
	}

	public void setStorage(String storage) {
		configValues.put("server.storage", storage);
	}

	public String getDataSourceIp() {
		return configValues.get("datasource.ip");
	}

	public void setDataSourceIp(String ip) {
		configValues.put("datasource.ip", ip);
	}

	public int getDataSourcePort() {
		return Integer.parseInt(configValues.get("datasource.port"));
	}

	public void setDataSourcePort(int port) {
		configValues.put("datasource.port", String.valueOf(port));
	}

	public String getDataSourceDbName() {
		return configValues.get("datasource.dbname");
	}

	public void setDataSourceDbName(String dbname) {
		configValues.put("datasource.dbname", dbname);
	}

	public String getDataSourceUserName() {
		return configValues.get("datasource.username");
	}

	public void setDataSourceUserName(String username) {
		configValues.put("datasource.username", username);
	}

	public String getDataSourcePassword() {
		return configValues.get("datasource.password");
	}

	public void setDataSourcePassword(String password) {
		configValues.put("datasource.password", password);
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
		return Integer.valueOf(configValues.get("host.socket.port"));
	}

	public void setHostSocketPort(int port) {
		configValues.put("host.socket.port", String.valueOf(port));
	}

	public String getKeystorePath() {
		return configValues.get("socket.keystorePath");
	}

	public void setKeystorePath(String keystorePath) {
		configValues.put("socket.keystorePath", keystorePath);
	}

	public String getKeystorePassword() {
		return configValues.get("socket.keystorePassword");
	}

	public void setKeystorePassword(String keystorePassword) {
		configValues.put("socket.keystorePassword", keystorePassword);
	}

	public boolean isSecure() {
		return Boolean.valueOf(configValues.get("socket.isSecure"));
	}

	public void setIsSecure(boolean isSecure) {
		configValues.put("socket.isSecure", String.valueOf(isSecure));
	}

	public String getCompilerContextService() {
		return configValues.get("compiler.context");
	}

	public void setCompilerContextService(String compilerContext) {
		configValues.put("compiler.context", compilerContext);
	}

	public boolean isDatasourceEmbedded() {
		return Boolean.valueOf(configValues.get("datasource.embedded"));
	}

	public void setDatasourceEmbedded(boolean embedded) {
		configValues.put("datasource.embedded", String.valueOf(embedded));
	}

	public int getDatasourceEmbeddedPort() {
		return Integer.valueOf(configValues.get("datasource.embedded.port"));
	}

	public void setDatasourceEmbeddedPort(Integer port) {
		configValues.put("datasource.embedded.port", String.valueOf(port));
	}

	public String getProjectId() {
		return configValues.get("project.id");
	}

	public void setProjectId(String projectId) {
		configValues.put("project.id", projectId);
	}

	@Override
	public String toString() {
		return "Config [configValues=" + configValues + "]";
	}

}
