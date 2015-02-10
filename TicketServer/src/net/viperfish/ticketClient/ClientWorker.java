package net.viperfish.ticketClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;

public class ClientWorker implements Runnable {

	Socket sock;
	SocketAddress server;
	LinkedList<Display> toRepresent;
	protected String currentCredential;
	public ClientWorker() {
		toRepresent = new LinkedList<Display>();
	}
	
	public void connect(String ip) throws IOException {
		sock = new Socket();
		server = new InetSocketAddress(ip, 8000);
		try {
			sock.connect(server, 20000);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void getTicket() throws IOException {
		sock.getOutputStream().write(("GetTicket:GetTicket").getBytes());
	}
	
	public void done() throws IOException {
		sock.getOutputStream().write(("Done:" + currentCredential).getBytes());
		currentCredential = new String();
	}

	public List<Display> getTask() {
		return toRepresent;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[2048];
		byte[] trimed = null;
		String response = new String();
		String[] part;
		String[] responses;
		String action;
		Display d;
		int status;
		while(!Thread.interrupted()) {
			d = new Display();
			try {
				status = sock.getInputStream().read(buffer);
			} catch (IOException e) {
				return;
			}
			System.out.println("Get Response:" + new String(buffer));
			if(status == -1) {
				return;
			}
			trimed = new byte[status];
			for(int i =0; i< status; i++) {
				trimed[i] = buffer[i];
			}
			response = new String(trimed);
			responses = response.split(",");
			for(String i : responses) {
				System.out.println("i:" + i);
				part = i.split(":");
				if(part.length != 2) {
					continue;
				}
				action = part[0];
				part = part[1].split(";");
				if(action.equals("Ticket")) {
					currentCredential = part[1];
					d.setContent(part[0]);
					d.setLocation("NumberBank");
					synchronized(this) {
						toRepresent.add(d);

						notifyAll();
					}
				} if(action.equals("CurrentNum")) {
					d.setContent(part[0]);
					d.setLocation("UpdateNumberBank");
					synchronized(this) {
						toRepresent.add(d);

						notifyAll();
					}
				} if(action.equals("Error")) {
					d.setLocation("Pop Up");
					d.setContent(part[1]);
					synchronized(this) {
						toRepresent.add(d);

						notifyAll();
					}
				}
			}
		}
	}

}
