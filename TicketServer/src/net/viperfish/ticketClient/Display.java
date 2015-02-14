package net.viperfish.ticketClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Display {
	private Map<String, String> displayMap = Collections
			.synchronizedMap(new HashMap<String, String>());

	private static Display instance = new Display();

	private Display() {
	}

	public static Display getInstance() {
		return instance;
	}

	/**
	 * Returns null if key not found.
	 */
	public String get(String key) {
		return displayMap.get(key);
	}

	public String get(String key, String defaultString) {
		String value = displayMap.get(key);
		if (value == null) {
			return defaultString;
		}
		return value;
	}

	public void put(String key, String value) {
		displayMap.put(key, value);
	}

	public void remove(String key) {
		displayMap.remove(key);
	}
}
