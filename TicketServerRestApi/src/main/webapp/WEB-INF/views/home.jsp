<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ticket Server Home</title>
</head>
<body>
	<h1>Ticket Server</h1>
	<hr>
	<h3>Current Ticket</h3>
	<hr>
	<p>Ticket ${currentTicket} owned by ${currentTicketOwner }</p>
	<hr>
	<h3>Ticket List</h3>
	<hr>
	<ul>${ticketList}
	</ul>
</body>
</html>