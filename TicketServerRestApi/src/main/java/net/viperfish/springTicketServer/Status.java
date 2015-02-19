package net.viperfish.springTicketServer;

public class Status {
	protected String description;

	public Status() {
		description = new String();
	}

	public Status(String status) {
		description = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
