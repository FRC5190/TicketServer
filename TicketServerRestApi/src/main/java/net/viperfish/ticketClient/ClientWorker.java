package net.viperfish.ticketClient;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedList;

import net.viperfish.springTicketServer.Ticket;
import net.viperfish.springTicketServer.TicketID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ClientWorker implements Runnable {

	protected LinkedList<Ticket> myTickets;
	protected String name;
	protected JsonGenerator generator;
	protected SecureRandom r;
	protected LinkedList<String> works;
	protected String baseURL;
	protected Object activeObjectLock;
	protected RestTemplate sender;

	public ClientWorker() {
		sender = new RestTemplate();
		myTickets = new LinkedList<Ticket>();
		name = new String();
		activeObjectLock = new Object();
		try {
			r = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
		}
		works = new LinkedList<String>();
		generator = new JsonGenerator();
	}

	public void connect(String url) throws IOException {
		baseURL = url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void getTicket() throws IOException {
		works.add("Get Ticket");
		synchronized (activeObjectLock) {
			activeObjectLock.notifyAll();
		}
	}

	public void done() throws IOException {
		if (myTickets.isEmpty()) {
			return;
		}
		works.add("Done");
		synchronized (activeObjectLock) {
			activeObjectLock.notifyAll();
		}
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			synchronized (activeObjectLock) {
				try {
					activeObjectLock.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
			for (String work : works) {
				if (work.equals("Get Ticket")) {
					Ticket generated = generateTicket();
					HttpEntity<String> request;
					HttpHeaders header = new HttpHeaders();
					header.setContentType(MediaType.APPLICATION_JSON);
					String ticketRequest = new String();
					try {
						ticketRequest = generator.toJson(generated);
					} catch (JsonGenerationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					request = new HttpEntity<String>(ticketRequest, header);
					String result = sender.postForObject(baseURL + "/tickets",
							request, String.class);
					TicketID id = null;
					try {
						id = generator.fromJson(TicketID.class, result);
					} catch (JsonParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					generated.setNum(Integer.toString(id.getId()));
					Display.getInstance().put("ticket",
							Integer.toString(id.getId()));
					myTickets.add(generated);
				}
				if (work.equals("Done")) {
					if (myTickets.isEmpty()) {
						continue;
					}
					ResponseEntity<String> result = sender.exchange(baseURL
							+ "/tickets/" + myTickets.getFirst().getNum()
							+ "?credential="
							+ myTickets.getFirst().getCredential(),
							HttpMethod.DELETE, new HttpEntity<String>(""),
							String.class);
					result.getStatusCode();
					if (!result.getStatusCode().equals(HttpStatus.OK)) {
						Display.getInstance().put("error",
								result.getStatusCode().getReasonPhrase());
					} else {
						myTickets.removeFirst();
					}
					if (myTickets.isEmpty()) {
						Display.getInstance().put("ticket", "---");
					} else {
						Display.getInstance().put("ticket",
								myTickets.getFirst().getNum());
					}
				}
				works.remove(work);
			}
			synchronized (this) {
				notifyAll();
			}
		}

	}

	protected Ticket generateTicket() {
		Ticket result = new Ticket();
		BigInteger i;
		i = new BigInteger(15, r);
		result.setCredential(i.toString());
		result.setOwner(name);
		return result;
	}

}
