package net.viperfish.ticketServer;

import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
	protected Socket sock;
	protected String name;
	protected boolean exit;

	public Client(Socket sock) {
		this.sock = sock;
		name = new String();
		exit = false;
	}

	public boolean reportError(Error e) {
		try {
			sock.getOutputStream().write(
					("Error:" + e.toString() + ",").getBytes());
		} catch (IOException e1) {
			return false;
		}
		return true;

	}

	public synchronized boolean sendTicket(Ticket t) {
		try {
			sock.getOutputStream().write(
					("Ticket:" + t.toString() + ",").getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public synchronized boolean confirmDone() {
		try {
			sock.getOutputStream().write("ConfirmedDone:Confirm".getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public synchronized boolean pushCurrent(Ticket current) {

		try {
			sock.getOutputStream()
					.write(("CurrentNum:" + current.getNum() + ";"
							+ current.getSrc() + ",").getBytes());
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public synchronized void dispose() {
		exit = true;
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}

	public synchronized String getUsername() {
		return name;
	}

	protected String trim(String src) {
		String result = src.trim();
		if (result.endsWith("\n")) {
			result = result.substring(0, result.length() - 2);
		}
		if (result.endsWith("\r")) {
			result = result.substring(0, result.length() - 2);
		}
		return result;
	}

	@Override
	public void run() {
		String request;
		String[] buffer;
		String action;
		boolean errorInformed = false;
		byte[] income = new byte[2048];
		byte[] buf = null;
		int status = 0;
		while (!Thread.interrupted() && !exit) {
			try {
				status = sock.getInputStream().read(income);
			} catch (IOException e) {
				TicketHandler.unSubscribe(name);
				return;
			}
			if (status == -1) {
				TicketHandler.unSubscribe(name);
				return;
			}
			buf = new byte[status];
			for (int j = 0; j < status; j++) {
				buf[j] = income[j];
			}
			request = new String(buf);
			System.out.println("Request:" + request);
			buffer = request.split(":");
			if (buffer.length != 2) {
				continue;
			} else {
				action = buffer[0];
				buffer = buffer[1].split(";");
				if (name.length() == 0 && !action.equals("Name")) {
					if (!errorInformed) {
						this.reportError(new Error("User Name Not Set", 002));
						errorInformed = true;
						continue;
					} else {
						continue;
					}
				}
				if (action.equals("GetTicket")) {
					if (!sendTicket(TicketHandler.getTicket(name))) {
						System.out.println("id" + name);
						TicketHandler.unSubscribe(name);
					}
				}
				if (action.equals("Done")) {
					if (buffer.length != 1) {
						continue;
					}
					TicketHandler.done(buffer[0]);
				}
				if (action.equals("Name")) {
					if (buffer.length != 1) {
						continue;
					}
					this.name = buffer[0];
					TicketHandler.subscribe(this);
				}
			}

		}

	}
}
