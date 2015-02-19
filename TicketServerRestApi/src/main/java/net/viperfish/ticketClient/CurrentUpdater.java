package net.viperfish.ticketClient;

import java.util.concurrent.TimeUnit;

import net.viperfish.springTicketServer.Ticket;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CurrentUpdater implements Runnable {
	protected RestTemplate sender;
	protected JsonGenerator g;
	protected String baseUrl;
	protected Object myLock;

	public CurrentUpdater(String url, Object lock) {
		sender = new RestTemplate();
		g = new JsonGenerator();
		baseUrl = url;
		myLock = lock;
	}

	@Override
	public void run() {
		String result = new String();
		while (!Thread.interrupted()) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e1) {
				return;
			}
			result = sender.getForObject(baseUrl + "/tickets/current",
					String.class);
			try {
				Ticket current = g.fromJson(Ticket.class, result);
				Display.getInstance().put("currentTicket", current.getNum());
				synchronized (myLock) {
					myLock.notifyAll();
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
