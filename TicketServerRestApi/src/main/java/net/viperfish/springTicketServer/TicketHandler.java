package net.viperfish.springTicketServer;

import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class TicketHandler {
	protected SecureRandom r;
	protected int currentMax;
	protected ConcurrentSkipListMap<String, Ticket> t;

	public TicketHandler() {
		try {
			r = SecureRandom.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			r = new SecureRandom();
		}
		t = new ConcurrentSkipListMap<String, Ticket>();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String htmlMain(Model m) {
		m.addAttribute("currentTicket", getCurrent().getNum());
		m.addAttribute("currentTicketOwner", getCurrent().getOwner());
		String htmlListItem = new String();
		String htmlListBlock = new String();
		for (Map.Entry<String, Ticket> i : t.entrySet()) {
			htmlListItem = "<li>Ticket Number:" + i.getValue().getNum()
					+ " Owner:" + i.getValue().getOwner() + "</li>";
			htmlListBlock += htmlListItem;
		}

		m.addAttribute("ticketList", htmlListBlock);
		return "home";
	}

	@RequestMapping(value = "tickets", method = RequestMethod.POST)
	public @ResponseBody TicketID getTicket(@RequestBody Ticket toAdd) {
		toAdd.setNum(Integer.toString(currentMax));
		t.put(toAdd.getNum(), toAdd);
		TicketID id = new TicketID();
		id.setId(currentMax);
		currentMax += 1;
		return id;
	}

	@RequestMapping(value = "tickets/{id}", method = RequestMethod.DELETE)
	public @ResponseBody Status done(@PathVariable("id") String id,
			HttpServletRequest resq, HttpServletResponse response) {
		response.setStatus(HttpURLConnection.HTTP_OK);
		Ticket toDelete = t.get(id);
		if (toDelete == null) {
			response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
			return new Status("ticket does not exist");
		}
		String credential = resq.getParameter("credential");
		if (credential == null) {
			response.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
			return new Status("fail to authenticate");
		}
		if (toDelete.getCredential().equals(credential)) {
			t.remove(id);
			return new Status("success");
		} else {
			response.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
			return new Status("fail to authenticate");
		}
	}

	@RequestMapping(value = "tickets/current", method = RequestMethod.GET)
	public @ResponseBody Ticket getCurrent() {
		if (t.firstEntry() == null) {
			return new Ticket();
		}
		Ticket currentTicket = new Ticket(t.firstEntry().getValue());
		currentTicket.setCredential("");
		return currentTicket;
	}

}
