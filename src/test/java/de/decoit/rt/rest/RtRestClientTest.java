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
import de.decoit.rt.model.RtQueue;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicket.RtTicketStatus;
import de.decoit.rt.model.RtUser;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import junit.framework.TestCase;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestClientTest extends TestCase {
//	private final String baseUri;
//	private final String username;
//	private final String password;
//	private RtRestClient instance;
//	private String sessionId;
//
//
//	public RtRestClientTest(String testName) throws IOException {
//		super(testName);
//
//		Properties prop = new Properties();
//		prop.load(getClass().getClassLoader().getResourceAsStream("rt.properties"));
//
//		System.setProperty("javax.net.ssl.trustStore", prop.getProperty("ssl.truststore.file"));
//		System.setProperty("javax.net.ssl.trustStorePassword", prop.getProperty("ssl.truststore.pass"));
//
//		baseUri = prop.getProperty("rt.baseuri");
//		username = prop.getProperty("rt.username");
//		password = prop.getProperty("rt.password");
//	}
//
//
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//
//		instance = new RtRestClient(baseUri);
//		sessionId = instance.login(username, password);
//	}
//
//
//	@Override
//	protected void tearDown() throws Exception {
//		super.tearDown();
//
//		instance.logout(sessionId);
//	}
//
//
	public void testTicketProperties() {
//		System.out.println("Testing ticketProperties()");
//
//		try {
//			Joiner commaJoiner = Joiner.on(",").skipNulls();
//
//			RtTicket ticket = instance.ticketProperties(sessionId, "ticket/1");
//
//			assertEquals("Ticket ID mismatch", 1, ticket.getId());
//			assertEquals("Queue mismatch", "General", ticket.getQueue());
//			assertEquals("Owner mismatch", "root", ticket.getOwner());
//			assertEquals("Creator mismatch", "root", ticket.getCreator());
//			assertEquals("Subject mismatch", "Testticket 1", ticket.getSubject());
//			assertEquals("Status mismatch", RtTicketStatus.NEW, ticket.getStatus());
//			assertEquals("Priority mismatch", 0, ticket.getPriority());
//			assertEquals("InitialPriority mismatch", 0, ticket.getInitialPriority());
//			assertEquals("FinalPriority mismatch", 0, ticket.getFinalPriority());
//			assertEquals("Requestors mismatch", "rix@decoit.de", commaJoiner.join(ticket.getRequestors()));
//			assertEquals("CC mismatch", "", commaJoiner.join(ticket.getCc()));
//			assertEquals("AdminCC mismatch", "", commaJoiner.join(ticket.getAdminCc()));
//			assertEquals("Created mismatch", "Wed Feb 26 15:59:56 2014", RtRestTicketResponseParser.formatDateString(ticket.getCreated()));
//			assertEquals("Starts mismatch", null, ticket.getStarts());
//			assertEquals("Started mismatch", null, ticket.getStarted());
//			assertEquals("Due mismatch", null, ticket.getDue());
//			assertEquals("Resolved mismatch", null, ticket.getResolved());
//			assertEquals("Told mismatch", null, ticket.getTold());
//			assertEquals("LastUpdated mismatch", "Thu Mar 13 12:23:44 2014", RtRestTicketResponseParser.formatDateString(ticket.getLastUpdated()));
//			assertEquals("TimeEstimated mismatch", 0, ticket.getTimeEstimated());
//			assertEquals("TimeWorked mismatch", 0, ticket.getTimeWorked());
//			assertEquals("TimeLeft mismatch", 0, ticket.getTimeLeft());
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
	}
//
//
//	public void testTicketPropertiesAbsentId() {
//		System.out.println("Testing ticketProperties() with absent ticket ID");
//
//		try {
//			RtTicket ticket = instance.ticketProperties(sessionId, "ticket/0");
//
//			fail("Absent ticket ID not detected: " + ticket.getId());
//		}
//		catch(RtException ex) {
//			/* Ignore, this is what should happen here! */
//		}
//	}
//
//
//	public void testSearchTickets() {
//		System.out.println("Testing searchTickets()");
//
//		try {
//			List<RtTicket> list = instance.searchTickets(sessionId, "Queue='General' AND id < 3", "-id");
//
//			assertEquals("Wrong list size", 2, list.size());
//			assertEquals("First list item ticket ID mismatch", 2, list.get(0).getId());
//			assertEquals("Second list item ticket ID mismatch", 1, list.get(1).getId());
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testCreateTicket() {
//		System.out.println("Testing createTicket()");
//
//		String ticketString = "id: ticket/new\n" +
//				"Queue: General\n" +
//				"Requestors: root\n" +
//				"Subject: createTicket-Test " + System.currentTimeMillis() + "\n" +
//				"Starts: Wed Mar 10 08:00:00 2014\n" +
//				"Text: Dies ist\n" +
//				" ein multiline\n" +
//				" Text!\n" +
//				"CF.{Risk}: 10\n" +
//				"CF.{Incident}: 101";
//
//		try {
//			long newId = instance.createTicket(sessionId, ticketString);
//
//			assertTrue("New ID less or equal 0", newId > 0);
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testEditTicket() {
//		System.out.println("Testing editTicket()");
//
//		String newSubject = "editTicket-Test " + System.currentTimeMillis();
//
//		String ticketString = "Subject: " + newSubject + "\n" +
//				"Status: " + RtTicketStatus.NEW.toString();
//
//		try {
//			boolean result = instance.editTicket(sessionId, "ticket/6", ticketString);
//
//			assertEquals("Ticket edit failed", true, result);
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testWriteTicketHistoryItemComment() {
//		System.out.println("Testing writeTicketHistoryItem() with action comment");
//
//		String itemString = "id: 18\n" +
//				"Action: comment\n" +
//				"Text: Multiline Kommentartext\n" +
//				" " + System.currentTimeMillis() + "ms";
//
//		try {
//			boolean result = instance.writeTicketHistoryItem(sessionId, "ticket/18", itemString);
//
//			assertEquals("Ticket commenting failed", true, result);
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testWriteTicketHistoryItemCorrespond() {
//		System.out.println("Testing writeTicketHistoryItem() with action correspond");
//
//		String itemString = "id: 18\n" +
//				"Action: correspond\n" +
//				"Text: Multiline Antworttext\n" +
//				" " + System.currentTimeMillis() + "ms";
//
//		try {
//			boolean result = instance.writeTicketHistoryItem(sessionId, "ticket/18", itemString);
//
//			assertEquals("Ticket commenting failed", true, result);
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testWriteTicketHistoryItemInvalidAction() {
//		System.out.println("Testing writeTicketHistoryItem() with action correspond");
//
//		String itemString = "id: 18\n" +
//				"Action: set\n" +
//				"Text: Multiline Antworttext\n" +
//				" " + System.currentTimeMillis() + "ms";
//
//		try {
//			boolean result = instance.writeTicketHistoryItem(sessionId, "ticket/18", itemString);
//
//			fail("Invalid action not detected");
//		}
//		catch(RtException ex) {
//			/* Ignore, this should happen here */
//		}
//	}
//
//
//	public void testQueueProperties() {
//		System.out.println("Testing queueProperties() with existing queue ID");
//
//		try {
//			RtQueue queue = instance.queueProperties(sessionId, "queue/3");
//
//			assertEquals("Queue ID mismatch", 3, queue.getId());
//			assertEquals("Queue name mismatch", "Connector queueProperties testing", queue.getName());
//			assertEquals("Queue description mismatch", "Queue for testing the RT connector, DO NOT CHANGE!", queue.getDescription());
//			assertEquals("Queue correspond address mismatch", "", queue.getCorrespondAddress());
//			assertEquals("Queue comment address mismatch", "", queue.getCommentAddress());
//			assertEquals("Queue initial priority mismatch", 0, queue.getInitialPriority());
//			assertEquals("Queue final priority mismatch", 10, queue.getFinalPriority());
//			assertEquals("Queue default due in mismatch", 5, queue.getDefaultDueIn());
//			assertEquals("Queue disabled mismatch", false, queue.isDisabled());
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testQueuePropertiesAbsentId() {
//		System.out.println("Testing queueProperties() with absent queue ID");
//
//		try {
//			RtQueue queue = instance.queueProperties(sessionId, "queue/0");
//
//			fail("Absent queue ID not detected: " + queue.getId());
//		}
//		catch(RtException ex) {
//			/* Ignore, this is what should happen here! */
//		}
//	}
//
//
//	public void testListQueues() {
//		System.out.println("Testing listQueues()");
//
//		try {
//			Map<Long, String> queueMap = instance.listQueues(sessionId);
//
//			assertTrue("Queue map empty", queueMap.size() > 0);
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testCreateQueue() {
//		System.out.println("Testing createQueue()");
//
//		String queueString = "id: queue/new\n" +
//			"Name: createQueue-Test " + System.currentTimeMillis() + "\n" +
//			"Description: Created during createQueue() test of the RT REST connector\n" +
//			"CorrespondAddress: \n" +
//			"CommentAddress: \n" +
//			"InitialPriority: 0\n" +
//			"FinalPriority: 5\n" +
//			"DefaultDueIn: 10\n" +
//			"Disabled: 0";
//
//		try {
//			long newId = instance.createQueue(sessionId, queueString);
//
//			assertTrue("New ID less or equal 0", newId > 0);
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testUserProperties() {
//		System.out.println("Testing userProperties() with username");
//
//		try {
//			RtUser result = instance.userProperties(sessionId, "Testbenutzer");
//
//			assertEquals("User ID mismatch", 100, result.getId());
//			assertEquals("User name mismatch", "Testbenutzer", result.getName());
//			assertEquals("User email mismatch", "test@decoit.de", result.getEmailAddress());
//			assertEquals("User real name mismatch", "Test User", result.getRealName());
//			assertEquals("User nick name mismatch", "Testy", result.getNickName());
//			assertEquals("User gecos mismatch", "testy", result.getGecos());
//			assertEquals("User organization mismatch", "DECOIT GmbH", result.getOrganization());
//			assertEquals("User address 1 mismatch", "Fahrenheitstraße 9", result.getAddress1());
//			assertEquals("User address 2 mismatch", "Blubb?", result.getAddress2());
//			assertEquals("User city mismatch", "Bremen", result.getCity());
//			assertEquals("User state mismatch", "HB", result.getState());
//			assertEquals("User zip mismatch", "12345", result.getZip());
//			assertEquals("User country mismatch", "Deutschland", result.getCountry());
//			assertEquals("User home phone mismatch", "123", result.getHomePhone());
//			assertEquals("User work phone mismatch", "456", result.getWorkPhone());
//			assertEquals("User mobile phone mismatch", "789", result.getMobilePhone());
//			assertEquals("User pager phone mismatch", "0123", result.getPagerPhone());
//			assertEquals("User contact info mismatch", "Dies\nist\neine\nZusatzinfo", result.getContactInfo());
//			assertEquals("User comments mismatch", "Dies ist ein\nKommentar", result.getComments());
//			assertEquals("User signature mismatch", "Dies ist eine\nSignatur", result.getSignature());
//			assertEquals("User lang mismatch", "de", result.getLang());
//			assertEquals("User privileged mismatch", true, result.isPrivileged());
//			assertEquals("User disabled mismatch", false, result.isDisabled());
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	public void testUserPropertiesAbsentUsername() {
//		System.out.println("Testing userProperties() with absent username");
//
//		try {
//			RtUser result = instance.userProperties(sessionId, "Testbenutzer0815");
//
//			fail("Absent username not detected");
//		}
//		catch(RtException ex) {
//			/* Ignore, this is what should happen here! */
//		}
//	}
//
//
//	public void testUserPropertiesById() {
//		System.out.println("Testing userProperties() with user ID");
//
//		try {
//			RtUser result = instance.userProperties(sessionId, 100L);
//
//			assertEquals("User ID mismatch", 100, result.getId());
//			assertEquals("User name mismatch", "Testbenutzer", result.getName());
//			assertEquals("User email mismatch", "test@decoit.de", result.getEmailAddress());
//			assertEquals("User real name mismatch", "Test User", result.getRealName());
//			assertEquals("User nick name mismatch", "Testy", result.getNickName());
//			assertEquals("User gecos mismatch", "testy", result.getGecos());
//			assertEquals("User organization mismatch", "DECOIT GmbH", result.getOrganization());
//			assertEquals("User address 1 mismatch", "Fahrenheitstraße 9", result.getAddress1());
//			assertEquals("User address 2 mismatch", "Blubb?", result.getAddress2());
//			assertEquals("User city mismatch", "Bremen", result.getCity());
//			assertEquals("User state mismatch", "HB", result.getState());
//			assertEquals("User zip mismatch", "12345", result.getZip());
//			assertEquals("User country mismatch", "Deutschland", result.getCountry());
//			assertEquals("User home phone mismatch", "123", result.getHomePhone());
//			assertEquals("User work phone mismatch", "456", result.getWorkPhone());
//			assertEquals("User mobile phone mismatch", "789", result.getMobilePhone());
//			assertEquals("User pager phone mismatch", "0123", result.getPagerPhone());
//			assertEquals("User contact info mismatch", "Dies\nist\neine\nZusatzinfo", result.getContactInfo());
//			assertEquals("User comments mismatch", "Dies ist ein\nKommentar", result.getComments());
//			assertEquals("User signature mismatch", "Dies ist eine\nSignatur", result.getSignature());
//			assertEquals("User lang mismatch", "de", result.getLang());
//			assertEquals("User privileged mismatch", true, result.isPrivileged());
//			assertEquals("User disabled mismatch", false, result.isDisabled());
//		}
//		catch(RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	/**
//	 * Test of login method, of class RtRestClient.
//	 */
//	public void testLogin() {
//		System.out.println("Testing login()");
//
//		try {
//			String sId = instance.login(username, password);
//
//			assertNotNull("Login failed", sId);
//		}
//		catch (RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
//
//
//	/**
//	 * Test of logout method, of class RtRestClient.
//	 */
//	public void testLogout() {
//		System.out.println("Testing logout()");
//
//		try {
//			String sId = instance.login(username, password);
//			instance.logout(sId);
//		}
//		catch (RtException ex) {
//			fail(ex.getMessage());
//		}
//	}
}
