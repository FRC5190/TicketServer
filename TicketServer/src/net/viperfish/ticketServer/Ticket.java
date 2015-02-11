package net.viperfish.ticketServer;

public class Ticket {
	protected String credential;
	protected int num;
	protected String src;
	public Ticket() {
		credential = new String();
	}
	
	public void setCredential(String credential) {
		this.credential = credential;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public String getCredential() {
		return credential;
	}
	
	public int getNum() {
		return num;
	}
	
	public String getSrc() {
		return src;
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	@Override
	public String toString() {
		return num + ";" + credential;
	}
} 
