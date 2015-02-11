package net.viperfish.ticketClient;

public class Display {
	protected String location;
	protected String content;
	protected String attribute;

	public Display() {
		location = new String();
		content = new String();
	}

	public void setLocation(String loc) {
		location = loc;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getLocation() {
		return location;
	}

	public void setAttr(String attr) {
		attribute = attr;
	}

	public String getAttribute() {
		return attribute;
	}
}
