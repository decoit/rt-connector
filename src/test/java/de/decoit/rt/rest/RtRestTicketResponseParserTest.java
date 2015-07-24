/* 
 * Copyright (C) 2015 DECOIT GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.decoit.rt.rest;

import com.google.common.base.Joiner;
import de.decoit.rt.RtException;
import de.decoit.rt.model.RtTicket;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import junit.framework.TestCase;


/**
 * This test file contains test cases for the RtRestTicketResponseParser class
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestTicketResponseParserTest extends TestCase {
	public RtRestTicketResponseParserTest(String testName) {
		super(testName);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	/**
	 * Test of parseTicket method, of class RtRestTicketResponseParser.
	 */
	public void testParseTicket() {
		System.out.println("Testing parseTicket()");
		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: ticket/1\n" +
			"Queue: General\n" +
			"Owner: root\n" +
			"Creator: root\n" +
			"Subject: Testticket 1\n" +
			"Status: new\n" +
			"Priority: 0\n" +
			"InitialPriority: 0\n" +
			"FinalPriority: 0\n" +
			"Requestors: rix@decoit.de\n" +
			"Cc:\n" +
			"AdminCc:\n" +
			"Created: Wed Feb 26 15:59:56 2014\n" +
			"Starts: Nicht angegeben\n" +
			"Started: Nicht angegeben\n" +
			"Due: Nicht angegeben\n" +
			"Resolved: Nicht angegeben\n" +
			"Told: Nicht angegeben\n" +
			"LastUpdated: Wed Feb 26 16:00:57 2014\n" +
			"TimeEstimated: 0\n" +
			"TimeWorked: 0\n" +
			"TimeLeft: 0\n" +
			"CF.{Risk}: 10\n" +
			"CF-Incident: 101";

		try {
			RtTicket ticket = RtRestTicketResponseParser.parseTicket(response);
			Joiner commaJoiner = Joiner.on(",").skipNulls();

			assertEquals("Ticket ID mismatch", 1, ticket.getId());
			assertEquals("Queue mismatch", "General", ticket.getQueue());
			assertEquals("Owner mismatch", "root", ticket.getOwner());
			assertEquals("Creator mismatch", "root", ticket.getCreator());
			assertEquals("Subject mismatch", "Testticket 1", ticket.getSubject());
			assertEquals("Status mismatch", RtTicket.RtTicketStatus.NEW, ticket.getStatus());
			assertEquals("Priority mismatch", 0, ticket.getPriority());
			assertEquals("InitialPriority mismatch", 0, ticket.getInitialPriority());
			assertEquals("FinalPriority mismatch", 0, ticket.getFinalPriority());
			assertEquals("Requestors mismatch", "rix@decoit.de", commaJoiner.join(ticket.getRequestors()));
			assertEquals("CC mismatch", "", commaJoiner.join(ticket.getCc()));
			assertEquals("AdminCC mismatch", "", commaJoiner.join(ticket.getAdminCc()));
			assertEquals("Created mismatch", "Wed Feb 26 15:59:56 2014", RtRestTicketResponseParser.formatDateString(ticket.getCreated()));
			assertEquals("Starts mismatch", null, ticket.getStarts());
			assertEquals("Started mismatch", null, ticket.getStarted());
			assertEquals("Due mismatch", null, ticket.getDue());
			assertEquals("Resolved mismatch", null, ticket.getResolved());
			assertEquals("Told mismatch", null, ticket.getTold());
			assertEquals("LastUpdated mismatch", "Wed Feb 26 16:00:57 2014", RtRestTicketResponseParser.formatDateString(ticket.getLastUpdated()));
			assertEquals("TimeEstimated mismatch", 0, ticket.getTimeEstimated());
			assertEquals("TimeWorked mismatch", 0, ticket.getTimeWorked());
			assertEquals("TimeLeft mismatch", 0, ticket.getTimeLeft());
			assertEquals("CustomField Risk mismatch", "10", ticket.getCustomFields().get("Risk"));
			assertEquals("CustomField Incident mismatch", "101", ticket.getCustomFields().get("Incident"));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	/**
	 * Test of parseTickets method, of class RtRestTicketResponseParser.
	 */
	public void testParseTickets() {
		System.out.println("Testing parseTickets()");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: ticket/2\n" +
			"Queue: General\n" +
			"Owner: root\n" +
			"Creator: root\n" +
			"Subject: Testticket 2\n" +
			"Status: new\n" +
			"Priority: 0\n" +
			"InitialPriority: 0\n" +
			"FinalPriority: 0\n" +
			"Requestors: rix@decoit.de\n" +
			"Cc:\n" +
			"AdminCc:\n" +
			"Created: Wed Feb 26 10:02:53 2014\n" +
			"Starts: Nicht angegeben\n" +
			"Started: Nicht angegeben\n" +
			"Due: Nicht angegeben\n" +
			"Resolved: Nicht angegeben\n" +
			"Told: Nicht angegeben\n" +
			"LastUpdated: Wed Feb 26 10:03:53 2014\n" +
			"TimeEstimated: 0\n" +
			"TimeWorked: 0\n" +
			"TimeLeft: 0\n" +
			"\n" +
			"--\n" +
			"\n" +
			"id: ticket/1\n" +
			"Queue: General\n" +
			"Owner: root\n" +
			"Creator: root\n" +
			"Subject: Testticket 1\n" +
			"Status: new\n" +
			"Priority: 0\n" +
			"InitialPriority: 0\n" +
			"FinalPriority: 0\n" +
			"Requestors: rix@decoit.de\n" +
			"Cc:\n" +
			"AdminCc:\n" +
			"Created: Wed Feb 26 15:59:56 2014\n" +
			"Starts: Nicht angegeben\n" +
			"Started: Nicht angegeben\n" +
			"Due: Nicht angegeben\n" +
			"Resolved: Nicht angegeben\n" +
			"Told: Nicht angegeben\n" +
			"LastUpdated: Wed Feb 26 16:00:57 2014\n" +
			"TimeEstimated: 0\n" +
			"TimeWorked: 0\n" +
			"TimeLeft: 0";

		try {
			List<RtTicket> tickets = RtRestTicketResponseParser.parseTickets(response);
			Joiner commaJoiner = Joiner.on(",").skipNulls();

			// Test first ticket
			RtTicket ticket = tickets.get(0);

			assertEquals("Ticket ID mismatch", 2, ticket.getId());
			assertEquals("Queue mismatch", "General", ticket.getQueue());
			assertEquals("Owner mismatch", "root", ticket.getOwner());
			assertEquals("Creator mismatch", "root", ticket.getCreator());
			assertEquals("Subject mismatch", "Testticket 2", ticket.getSubject());
			assertEquals("Status mismatch", RtTicket.RtTicketStatus.NEW, ticket.getStatus());
			assertEquals("Priority mismatch", 0, ticket.getPriority());
			assertEquals("InitialPriority mismatch", 0, ticket.getInitialPriority());
			assertEquals("FinalPriority mismatch", 0, ticket.getFinalPriority());
			assertEquals("Requestors mismatch", "rix@decoit.de", commaJoiner.join(ticket.getRequestors()));
			assertEquals("CC mismatch", "", commaJoiner.join(ticket.getCc()));
			assertEquals("AdminCC mismatch", "", commaJoiner.join(ticket.getAdminCc()));
			assertEquals("Created mismatch", "Wed Feb 26 10:02:53 2014", RtRestTicketResponseParser.formatDateString(ticket.getCreated()));
			assertEquals("Starts mismatch", null, ticket.getStarts());
			assertEquals("Started mismatch", null, ticket.getStarted());
			assertEquals("Due mismatch", null, ticket.getDue());
			assertEquals("Resolved mismatch", null, ticket.getResolved());
			assertEquals("Told mismatch", null, ticket.getTold());
			assertEquals("LastUpdated mismatch", "Wed Feb 26 10:03:53 2014", RtRestTicketResponseParser.formatDateString(ticket.getLastUpdated()));
			assertEquals("TimeEstimated mismatch", 0, ticket.getTimeEstimated());
			assertEquals("TimeWorked mismatch", 0, ticket.getTimeWorked());
			assertEquals("TimeLeft mismatch", 0, ticket.getTimeLeft());

			// Test second ticket
			ticket = tickets.get(1);

			assertEquals("Ticket ID mismatch", 1, ticket.getId());
			assertEquals("Queue mismatch", "General", ticket.getQueue());
			assertEquals("Owner mismatch", "root", ticket.getOwner());
			assertEquals("Creator mismatch", "root", ticket.getCreator());
			assertEquals("Subject mismatch", "Testticket 1", ticket.getSubject());
			assertEquals("Status mismatch", RtTicket.RtTicketStatus.NEW, ticket.getStatus());
			assertEquals("Priority mismatch", 0, ticket.getPriority());
			assertEquals("InitialPriority mismatch", 0, ticket.getInitialPriority());
			assertEquals("FinalPriority mismatch", 0, ticket.getFinalPriority());
			assertEquals("Requestors mismatch", "rix@decoit.de", commaJoiner.join(ticket.getRequestors()));
			assertEquals("CC mismatch", "", commaJoiner.join(ticket.getCc()));
			assertEquals("AdminCC mismatch", "", commaJoiner.join(ticket.getAdminCc()));
			assertEquals("Created mismatch", "Wed Feb 26 15:59:56 2014", RtRestTicketResponseParser.formatDateString(ticket.getCreated()));
			assertEquals("Starts mismatch", null, ticket.getStarts());
			assertEquals("Started mismatch", null, ticket.getStarted());
			assertEquals("Due mismatch", null, ticket.getDue());
			assertEquals("Resolved mismatch", null, ticket.getResolved());
			assertEquals("Told mismatch", null, ticket.getTold());
			assertEquals("LastUpdated mismatch", "Wed Feb 26 16:00:57 2014", RtRestTicketResponseParser.formatDateString(ticket.getLastUpdated()));
			assertEquals("TimeEstimated mismatch", 0, ticket.getTimeEstimated());
			assertEquals("TimeWorked mismatch", 0, ticket.getTimeWorked());
			assertEquals("TimeLeft mismatch", 0, ticket.getTimeLeft());
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	/**
	 * Test of parseTicketCreated method, of class RtRestTicketResponseParser.
	 */
	public void testParseTicketCreatedSuccess() {
		System.out.println("Testing parseTicketCreated() with success response");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# Ticket 775 created.";

		try {
			assertEquals("Successful creation mismatch", 775, RtRestTicketResponseParser.parseTicketCreated(response));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	public void testParseTicketCreatedFailed() {
		System.out.println("Testing parseTicketCreated() with failure response");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# Required: id, Queue\n" +
			"\n" +
			"id: ticket/new\n" +
			"Queue: General\n" +
			"Requestor: root\n" +
			"Subject: \n" +
			"Cc:\n" +
			"AdminCc:\n" +
			"Owner: \n" +
			"Status: new\n" +
			"Priority: 0\n" +
			"InitialPriority: 0\n" +
			"FinalPriority: 0\n" +
			"TimeEstimated: 0\n" +
			"Starts: 2014-03-03 13:15:08\n" +
			"Due: 2014-03-03 13:15:08\n" +
			"Attachment: \n" +
			"Text: ";

		try {
			RtRestTicketResponseParser.parseTicketCreated(response);
			fail("Parsing incorrect create ticket response did not throw exception");
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}


	public void testParseTicketEditedSuccess() {
		System.out.println("Testing parseTicketEdited() with success response");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# Ticket 775 updated.";

		String response2 = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: ticket/7\n" +
			"Queue: General\n" +
			"Owner: Nobody\n" +
			"Creator: root\n" +
			"Subject: createTicket-Test 1394099474486\n" +
			"Status: new\n" +
			"Priority: -2\n" +
			"InitialPriority: 0\n" +
			"FinalPriority: 0\n" +
			"Requestors: osga@exodus-project.net\n" +
			"Cc:\n" +
			"AdminCc:\n" +
			"Created: Do 06. Mär 10:51:16 2014\n" +
			"Starts: Mo 10. Mär 08:00:00 2014\n" +
			"Started: Do 06. Mär 11:26:55 2014\n" +
			"Due: Fr 14. Mär 23:59:00 2014\n" +
			"Resolved: Nicht angegeben\n" +
			"Told: Do 06. Mär 08:00:00 2014\n" +
			"LastUpdated: Do 06. Mär 12:02:43 2014\n" +
			"TimeEstimated: -60 minutes\n" +
			"TimeWorked: 10 minutes\n" +
			"TimeLeft: -50 minutes";

		try {
			assertEquals("Successful edit mismatch", true, RtRestTicketResponseParser.parseTicketEdited(response));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}

		try {
			RtRestTicketResponseParser.parseTicketEdited(response2);
			fail("Parsing incorrect edit ticket response did not throw exception");
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}


	public void testParseTicketEditedFailed() {
		System.out.println("Testing parseTicketEdited() with failure response");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: ticket/7\n" +
			"Queue: General\n" +
			"Owner: Nobody\n" +
			"Creator: root\n" +
			"Subject: createTicket-Test 1394099474486\n" +
			"Status: new\n" +
			"Priority: -2\n" +
			"InitialPriority: 0\n" +
			"FinalPriority: 0\n" +
			"Requestors: osga@exodus-project.net\n" +
			"Cc:\n" +
			"AdminCc:\n" +
			"Created: Do 06. Mär 10:51:16 2014\n" +
			"Starts: Mo 10. Mär 08:00:00 2014\n" +
			"Started: Do 06. Mär 11:26:55 2014\n" +
			"Due: Fr 14. Mär 23:59:00 2014\n" +
			"Resolved: Nicht angegeben\n" +
			"Told: Do 06. Mär 08:00:00 2014\n" +
			"LastUpdated: Do 06. Mär 12:02:43 2014\n" +
			"TimeEstimated: -60 minutes\n" +
			"TimeWorked: 10 minutes\n" +
			"TimeLeft: -50 minutes";

		try {
			RtRestTicketResponseParser.parseTicketEdited(response);
			fail("Parsing incorrect edit ticket response did not throw exception");
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}
}
