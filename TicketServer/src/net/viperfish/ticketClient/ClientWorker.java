package net.viperfish.ticketClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClientWorker implements Runnable {

	SSLSocket sock;
	SocketAddress server;
	protected String currentCredential;

	public ClientWorker() {
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
		sock.getOutputStream().write(("Done:" + currentCredential).getBytes());
	}

	@Override
	public void run() {
		byte[] buffer = new byte[2048];
		byte[] trimed = null;
		String response = new String();
		String[] part;
		String[] responses;
		String action;
		Display d = Display.getInstance();
		int status;
		while (!Thread.interrupted()) {
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
					currentCredential = part[1];
					d.put("ticket", part[0]);

				}
				if (action.equals("CurrentNum")) {
					if (part.length != 2) {
						continue;
					}
					d.put("currentTicket", part[0] + ", " + part[1]);
					synchronized (this) {
						notifyAll();
					}
				}
				if (action.equals("Error")) {
					d.put("error", part[1]);
					synchronized (this) {
						notifyAll();
					}
				}
				if (action.equals("ConfirmedDone")) {
					currentCredential = new String();
				}
			}
		}
	}

}
