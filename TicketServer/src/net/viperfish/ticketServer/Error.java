package net.viperfish.ticketServer;

public class Error {
	protected String message;
	protected int status;
	public Error(String m, int status) {
		message = m;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status + ";" + message;
	}
}
