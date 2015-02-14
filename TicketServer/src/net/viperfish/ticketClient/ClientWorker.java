package net.viperfish.ticketClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.viperfish.ticketServer.Ticket;

public class ClientWorker implements Runnable {

	SSLSocket sock;
	SocketAddress server;
	protected String currentCredential;
	protected LinkedList<Ticket> myTickets;

	public ClientWorker() {
		myTickets = new LinkedList<Ticket>();
	}

	public void connect(String ip) throws IOException {
		server = new InetSocketAddress(ip, 8000);
		sock = (SSLSocket) SSLSocketFactory.getDefault().createSocket();
		sock.setEnabledCipherSuites(sock.getSupportedCipherSuites());
		sock.setEnabledProtocols(sock.getEnabledProtocols());
		try {
			sock.connect(server);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void sendName(String name) throws IOException {
		sock.getOutputStream().write(("Name:" + name).getBytes());
	}

	public void getTicket() throws IOException {
		sock.getOutputStream().write(("GetTicket:GetTicket").getBytes());
	}

	public void done() throws IOException {
		if (myTickets.isEmpty()) {
			return;
		}
		sock.getOutputStream().write(
				("Done:" + myTickets.getFirst().getCredential()).getBytes());
	}

	@Override
	public void run() {
		byte[] buffer = new byte[2048];
		byte[] trimed = null;
		String response = new String();
		String[] part;
		String[] responses;
		String action;
		Ticket temp;
		Display d = Display.getInstance();
		int status;
		while (!Thread.interrupted()) {
			temp = new Ticket();
			try {
				status = sock.getInputStream().read(buffer);
			} catch (IOException e) {
				return;
			}
			System.out.println("Get Response:" + new String(buffer));
			if (status == -1) {
				return;
			}
			trimed = new byte[status];
			for (int i = 0; i < status; i++) {
				trimed[i] = buffer[i];
			}
			response = new String(trimed);
			responses = response.split(",");
			for (String i : responses) {
				System.out.println("i:" + i);
				part = i.split(":");
				if (part.length != 2) {
					continue;
				}
				action = part[0];
				part = part[1].split(";");
				if (action.equals("Ticket")) {
					temp.setCredential(part[1]);
					temp.setNum(Integer.parseInt(part[0]));
					myTickets.add(temp);
					d.put("ticket", Integer.toString(temp.getNum()));

				}
				if (action.equals("CurrentNum")) {
					if (part.length != 2) {
						continue;
					}
					d.put("currentTicket", part[0] + ", " + part[1]);
				}
				if (action.equals("Error")) {
					d.put("error", part[1]);
				}
				if (action.equals("ConfirmedDone")) {
					if (myTickets.isEmpty()) {
						d.put("ticket", "---");
					} else {
						d.put("ticket",
								Integer.toString(myTickets.getFirst().getNum()));
						myTickets.removeFirst();
						if (myTickets.isEmpty()) {
							d.put("ticket", "---");
						}
					}
					d.put("currentTicket", "---");
				}
			}
			synchronized (this) {
				notifyAll();
			}
		}
	}

}
