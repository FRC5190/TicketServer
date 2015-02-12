package net.viperfish.ticketClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ClientProperties {
	public static final String PROPERTIES_DIR = System.getProperty("user.home")
			+ "/.ticketServer";
	public static final String PROPERTIES_FILE = "client.properties";

	private static ClientProperties instance;

	Properties properties;
	File propertiesFile;

	private ClientProperties() {
		File propertiesDir = new File(PROPERTIES_DIR);
		if (!propertiesDir.exists()) {
			if (!propertiesDir.mkdirs()) {
				throw new IllegalStateException(
						"Can't create properties directory " + PROPERTIES_DIR);
			}
		}
		propertiesFile = new File(propertiesDir, PROPERTIES_FILE);
		if (!propertiesFile.exists()) {
			try {
				propertiesFile.createNewFile();
			} catch (IOException e) {
				throw new IllegalStateException("Can't create properties file "
						+ propertiesFile.getPath(), e);
			}
		}

		properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertiesFile);
			properties.load(inputStream);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load properties file "
					+ propertiesFile.getPath(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static ClientProperties getInstance() {
		if (instance == null) {
			instance = new ClientProperties();
		}
		return instance;
	}

	public void put(String key, Object value) {
		properties.put(key, value.toString());
		save();
	}

	public int getInt(String key, int defaultInt) {
		String valueStr = properties.getOrDefault(key, new Integer(defaultInt))
				.toString();
		return Integer.parseInt(valueStr);
	}

	/**
	 * Get the string property. Returns null if property doesn't exist.
	 */
	public String getString(String key) {
		return properties.get(key).toString();
	}

	/**
	 * Get the string property. Returns the defaultString if property doesn't
	 * exist.
	 */

	public String getString(String key, String defaultString) {
		return properties.getOrDefault(key, defaultString).toString();
	}

	private void save() {
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(
					propertiesFile));
			properties.store(outputStream, null);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to save properties file "
					+ propertiesFile.getPath());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
