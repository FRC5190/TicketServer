package net.viperfish.ticketClient;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.List;

public class TicketClient {
	private Window currentWindow;

	private ClientWorker w;
	private Thread worker;
	private Thread updater;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new TicketClient();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TicketClient() {
		currentWindow = new MainWindow(this);
		currentWindow.show();

		w = new ClientWorker();
		worker = new Thread(w);
		updater = new Thread(new Runnable() {

			@Override
			public void run() {
				List<Display> updates;
				while (true) {
					synchronized (w) {
						try {
							w.wait();
						} catch (InterruptedException e) {
							return;
						}
					}
					updates = w.getTask();
					currentWindow.updateDisplay(updates);
					updates.clear();
				}

			}

		});
	}

	public synchronized void setCurrentWindow(Window currentWindow) {
		this.currentWindow = currentWindow;
	}

	public void connect(String ip, String name) throws TicketException {
		try {
			w.connect(ip);
			if (name.length() != 0) {
				w.sendName(name);
			}
		} catch (IOException e) {
			throw new TicketException(e.getMessage(), e);
		}
		worker.start();
		updater.start();
	}

	public void getTicket() throws TicketException {
		try {
			w.getTicket();
		} catch (IOException e) {
			throw new TicketException(e.getMessage(), e);
		}
	}

	public void done() throws TicketException {
		try {
			w.getTicket();
		} catch (IOException e) {
			throw new TicketException(e.getMessage(), e);
		}
	}

}
