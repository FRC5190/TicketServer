package net.viperfish.ticketServer;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketHandler {
	protected static int current;
	protected static Map<Integer, Client> s;
	protected static LinkedList<Ticket> t;
	protected static ExecutorService pool;
	protected static SecureRandom generator;
	protected static ServerSocket meta;
	static {
		s = new HashMap<Integer, Client>();
		t = new LinkedList<Ticket>();
		pool = Executors.newCachedThreadPool();
		generator = new SecureRandom();
		try {
			meta = new ServerSocket(8000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void start() {
		pool.execute(new Runnable() {

			@Override
			public void run() {
				Socket temp;
				while(!Thread.interrupted())
				{
					try {
						temp = meta.accept();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
					subscribe(new Client(temp));
				}
			}
			
		});
	}
	
	public synchronized static void subscribe(Client c) {
		s.put(c.getID(), c);
		pool.execute(c);
	}

	public synchronized static void unSubscribe(int id) {
		s.get(new Integer(id)).dispose();
		s.remove(new Integer(id));
		for(Ticket i : t) {
			if(i.getSrc() == id) {
				t.remove(i);
			}
		}
		for(Map.Entry<Integer, Client> i : s.entrySet()) {
			if(!t.isEmpty()) {
				i.getValue().pushCurrent(t.getFirst().getNum());
			}
		}
	}

	public synchronized static Ticket getTicket(int src) {
		Ticket newTicket = new Ticket();
		newTicket.setCredential(new BigInteger(130, generator).toString());
		newTicket.setNum(current);
		newTicket.setSrc(src);
		t.addLast(newTicket);
		System.out.println("Ticket Queue:" + t.size());
		if(t.size() ==1) {
			for(Map.Entry<Integer, Client> i : s.entrySet()) {
				if(!t.isEmpty()) {
					i.getValue().pushCurrent(t.getFirst().getNum());
				}
			}
		}
		current = current +1;
		return newTicket;
	}

	public synchronized static void done(String credential) {
		if(t.isEmpty()) {
			return;
		}
		System.out.println("Queue:" + t);
		credential.trim();
		String localHash;
		localHash = t.getFirst().getCredential();
		if(localHash.equals(credential)) {
			t.removeFirst();
			for(Map.Entry<Integer, Client> i : s.entrySet()) {
				if(!t.isEmpty()) {
					System.out.println("Pushing " + t.getFirst().getNum() );
					i.getValue().pushCurrent(t.getFirst().getNum());
				}
			}
		}
		else {
			return;
		}
	}
}
