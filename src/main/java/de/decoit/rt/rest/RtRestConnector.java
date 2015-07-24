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

import de.decoit.rt.RtConnector;
import de.decoit.rt.RtException;
import de.decoit.rt.model.RtQueue;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicketHistoryItem;
import de.decoit.rt.model.RtTicketHistoryItem.RtTicketHistoryItemType;
import de.decoit.rt.model.RtUser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * Implementation of the RtConnector interface to access the RT system using the REST API.
 * It searches for a rt.properties file at the root of the classpath to set the Java TrustStore and its password.
 * This is required for SSL connections, if not SSL should be used the file may be absent or empty. If the file
 * is absent a warning will be logged.<br>
 * Additionally this class will configure the log4j system using a log4j.properties file located at the root of the
 * classpath. If this file does not exist the logging system will not work properly.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestConnector implements RtConnector {
	private final Logger LOG;
	private RtRestClient client = null;


	/**
	 * Initialize the object.
	 * The constructor will configure the log4j system and set the system properties for the Java TrustStore and its password.
	 * Also it will create an instance of the RtRestClient class using the provided RT base URI and credentials.
	 *
	 * @param rtBaseUri Base URI of the RT installation, i.e. http://10.10.10.10/
	 */
	public RtRestConnector(String rtBaseUri) {
		// Configure log4j system
		if(Files.exists(Paths.get("log4j.properties"))) {
			PropertyConfigurator.configure("log4j.properties");
		}

		LOG = Logger.getLogger(RtRestConnector.class.getName());

		// Create the REST client
		this.client = new RtRestClient(rtBaseUri);
	}


	@Override
	public String login(String uname, String password) throws RtException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("SSL KeyStore: " + System.getProperty("javax.net.ssl.keyStore"));
			LOG.debug("SSL TrustStore: " + System.getProperty("javax.net.ssl.trustStore"));
		}

		String sessionId = client.login(uname, password);

		return sessionId;
	}


	@Override
	public void logout(String sessionId) throws RtException {
		client.logout(sessionId);
	}


	@Override
	public RtTicket getTicket(String sessionId, long id) throws RtException {
		StringBuilder idSb = new StringBuilder("ticket/");
		idSb.append(id);

		return client.ticketProperties(sessionId, idSb.toString());
	}


	@Override
	public List<RtTicket> searchTickets(String sessionId, String query) throws RtException {
		return searchTickets(sessionId, query, "-Created");
	}


	@Override
	public List<RtTicket> searchTickets(String sessionId, String query, String orderby) throws RtException {
		return client.searchTickets(sessionId, query, orderby);
	}


	@Override
	public long createTicket(String sessionId, RtTicket ticket) throws RtException {
		return client.createTicket(sessionId, RtRestTicketResponseParser.ticketToString(ticket, true, null));
	}


	@Override
	public boolean editTicket(String sessionId, RtTicket ticket) throws RtException {
		StringBuilder sb = new StringBuilder("ticket/");
		sb.append(ticket.getId());
		String ticketId = sb.toString();

		RtTicket oldTicket = client.ticketProperties(sessionId, ticketId);

		return client.editTicket(sessionId, ticketId, RtRestTicketResponseParser.ticketToString(ticket, false, oldTicket));
	}


	@Override
	public List<RtTicketHistoryItem> getTicketHistory(String sessionId, long ticketId) throws RtException {
		StringBuilder sb = new StringBuilder("ticket/");
		sb.append(ticketId);

		return client.getTicketHistory(sessionId, sb.toString());
	}


	@Override
	public boolean commentTicket(String sessionId, long ticketId, RtTicketHistoryItem item) throws RtException {
		if(item.getType() == RtTicketHistoryItemType.COMMENT) {
			StringBuilder sb = new StringBuilder("ticket/");
			sb.append(ticketId);

			return client.writeTicketHistoryItem(sessionId, sb.toString(), RtRestTicketHistoryResponseParser.historyItemToString(item, null, null));
		}
		else {
			throw new RtException("Unsupported history item type for comment action: " + item.getType());
		}
	}


	@Override
	public boolean answerTicket(String sessionId, long ticketId, RtTicketHistoryItem item) throws RtException {
		if(item.getType() == RtTicketHistoryItemType.CORRESPOND) {
			StringBuilder sb = new StringBuilder("ticket/");
			sb.append(ticketId);

			return client.writeTicketHistoryItem(sessionId, sb.toString(), RtRestTicketHistoryResponseParser.historyItemToString(item, null, null));
		}
		else {
			throw new RtException("Unsupported history item type for answer action: " + item.getType());
		}
	}


	@Override
	public RtQueue getQueue(String sessionId, long id) throws RtException {
		StringBuilder sb = new StringBuilder("queue/");
		sb.append(id);

		return client.queueProperties(sessionId, sb.toString());
	}


	@Override
	public RtQueue getQueueByName(String sessionId, String name) throws RtException {
		StringBuilder sb = new StringBuilder("queue/");
		sb.append(name);

		return client.queueProperties(sessionId, sb.toString());
	}


	@Override
	public Map<Long, String> listQueues(String sessionId) throws RtException {
		return client.listQueues(sessionId);
	}


	@Override
	public long createQueue(String sessionId, RtQueue queue) throws RtException {
		return client.createQueue(sessionId, RtRestQueueResponseParser.queueToString(queue, true));
	}


	@Override
	public RtUser getUser(String sessionId, String uname) throws RtException {
		StringBuilder sb = new StringBuilder("user/");
		sb.append(uname);
		String userId = sb.toString();

		return client.userProperties(sessionId, userId);
	}


	@Override
	public RtUser getUser(String sessionId, long uid) throws RtException {
		StringBuilder sb = new StringBuilder("user/");
		sb.append(uid);
		String userId = sb.toString();

		return client.userProperties(sessionId, userId);
	}


	@Override
	public List<RtUser> searchUsers(String sessionId, String query, String orderby) throws RtException {
		return client.searchUsers(sessionId, query, orderby);
	}


	@Override
	public boolean editUser(String sessionId, RtUser user) throws RtException {
		StringBuilder sb = new StringBuilder("user/");
		sb.append(user.getId());
		String userId = sb.toString();

		RtUser oldUser = client.userProperties(sessionId, userId);

		return client.editUser(sessionId, userId, RtRestUserResponseParser.userToString(user, false, oldUser));
	}
}
