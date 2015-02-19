package net.viperfish.springTicketServer;

public class Ticket {
	protected String owner;
	protected String num;
	protected String credential;

	public Ticket() {
		owner = new String();
		num = new String();
		credential = new String();
	}

	public Ticket(Ticket src) {
		this.owner = src.owner;
		this.num = src.num;
		this.credential = src.credential;
	}

	public Ticket(String owner, String num, String credential) {
		this.owner = owner;
		this.num = num;
		this.credential = credential;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

}
