package net.viperfish.ticketServer;

import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
	protected static int currentID;
	protected Socket sock;
	protected int id;
	protected boolean exit;
	public Client(Socket sock) {
		this.sock = sock;
		id = currentID;
		exit = false;
		currentID= currentID+1;
	}
	
	public boolean reportError(Error e) {
		try {
			sock.getOutputStream().write(("Error:" + e.toString() + ",").getBytes());
		} catch (IOException e1) {
			return false;
		}
		return true;
		
	}
	
	public synchronized boolean sendTicket(Ticket t) {
		try {
			sock.getOutputStream().write(("Ticket:" + t.toString() + ",").getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public synchronized boolean pushCurrent(int num) {
		
		try {
			sock.getOutputStream().write(("CurrentNum:" + num + ",").getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void dispose() {
		exit = true;
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	public int getID() {
		return id;
	}
	
	protected String trim(String src) {
		String result = src.trim();
		if(result.endsWith("\n")) {
			result = result.substring(0, result.length()-2);
		}
		if(result.endsWith("\r")) {
			result = result.substring(0, result.length()-2);
		}
		return result;
	}
	
	@Override
	public void run() {
		String request;
		String[] buffer;
		String action;
		byte[] income = new byte[2048];
		byte[] buf = null;
		int status = 0;
		while(!Thread.interrupted() && !exit) {
			try {
				status = sock.getInputStream().read(income);
			} catch (IOException e) {
				TicketHandler.unSubscribe(this.id);
				return;
			}
			if(status == -1) {
				TicketHandler.unSubscribe(this.id);
				return;
			}
			buf = new byte[status];
			for(int j = 0; j< status; j++) {
				buf[j] = income[j];
			}
			request = new String(buf);
			System.out.println("Request:" + request);
			buffer = request.split(":");
			if(buffer.length != 2) {
				continue;
			}
			else {
				action = buffer[0];
				buffer = buffer[1].split(";");
				if(action.equals("GetTicket")) {
					if(!sendTicket(TicketHandler.getTicket(this.id))) {
						System.out.println("id" + id);
						TicketHandler.unSubscribe(this.id);
					}
				}
				if(action.equals("Done")) {
					if(buffer.length != 1) {
						continue;
					}
					TicketHandler.done(buffer[0]);
				}
			}
			
		}
		
	}
}
