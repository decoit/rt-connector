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

import de.decoit.rt.RtException;
import de.decoit.rt.model.RtQueue;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicketHistoryItem;
import de.decoit.rt.model.RtUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


/**
 * This class manages the connection to the RT REST API.
 * It allows both HTTP and HTTPS connections to the API, deciding on the format of the RT base URI provided.
 * A URI prefixed with http:// will use a normal connection while a https:// prefix will initiate a SSL secured
 * connection. The server's SSL certificate must be available using a Java TrustStore.<br>
 * The session ID returned by RT as cookie data is stored for further use. The class implements the AutoCloseable
 * interface to make sure the session is logged out when the object is destroyed.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
class RtRestClient {
	private final Logger LOG = Logger.getLogger(RtRestClient.class);
	private final String URI_RT_REST_LOGIN = "REST/1.0/user/{username}";
	private final String URI_RT_REST_LOGOUT = "REST/1.0/logout";
	private final String URI_RT_REST_TICKET_PROPERTIES = "REST/1.0/{ticket-id}/show";
	private final String URI_RT_REST_TICKET_SEARCH = "REST/1.0/search/ticket?query={query}&orderby={order-by}&format=l";
	private final String URI_RT_REST_TICKET_CREATE = "REST/1.0/ticket/new";
	private final String URI_RT_REST_TICKET_EDIT = "REST/1.0/{ticket-id}/edit";
	private final String URI_RT_REST_TICKET_HISTORY = "REST/1.0/{ticket-id}/history?format=l";
	private final String URI_RT_REST_TICKET_COMMENT = "REST/1.0/{ticket-id}/comment";
	private final String URI_RT_REST_QUEUE_PROPERTIES = "REST/1.0/{queue-id}/show";
	private final String URI_RT_REST_QUEUE_LIST = "REST/1.0/search/queue?query=";
	private final String URI_RT_REST_QUEUE_CREATE = "REST/1.0/queue/new";
	private final String URI_RT_REST_USER_PROPERTIES = "REST/1.0/{user-id}/show";
	private final String URI_RT_REST_USER_SEARCH = "REST/1.0/search/user?query={query}&orderby={order-by}&format=l";
	private final String URI_RT_REST_USER_EDIT = "REST/1.0/{user-id}/edit";
	private final RestTemplate REST_TEMPLATE = new RestTemplate();

	/**
	 * This is required to make sure we always get US format for timestamps and english translation for status texts.
	 */
	private final String HTTP_HEADERS_ACCEPT_LANGUAGE = "en-US;q=0.8,en;q=0.6";

	private String baseUri;


	/**
	 * Constructor.
	 * Initialize object with required parameters. The RT base URI must be prefixed with http:// for normal connections
	 * or https:// for SSL secured connections. All other protocol prefixes will raise an exception. The URI may be provided
	 * as IP address or host name.
	 *
	 * @param rtBaseUri Base URI of the RT installation, i.e. http://10.10.10.10/
	 */
	RtRestClient(String rtBaseUri) {
		Pattern p = Pattern.compile("^http(s)?://.+/?$");
		Matcher m = p.matcher(rtBaseUri);

		// Validate RT base URI
		if (m.matches()) {
			if (rtBaseUri.endsWith("/")) {
				this.baseUri = rtBaseUri;
			}
			else {
				// Append missing "/" to the URI
				StringBuilder sb = new StringBuilder(rtBaseUri);
				sb.append("/");

				this.baseUri = sb.toString();
			}
		}
		else {
			throw new IllegalArgumentException("Provided base URI was not valid");
		}
	}


	/**
	 * Login to the RT REST API and store the session ID for further use.
	 * Any previous session will be logged out before opening a new session.
	 *
	 * @param uname Username used for login
	 * @param password Password for username
	 * @return RT session ID
	 *
	 * @throws RtException
	 */
	String login(String uname, String password) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_LOGIN);

		// Define POST parameters (username and password for authentication)
		LinkedMultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
		postParams.add("user", uname);
		postParams.add("pass", password);

		// Define variables to replace placeholders in URI string
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("username", uname);

		RtRestResponse response = getResponse(null, uriSb.toString(), postParams, uriParams);

		// If the request was processed successfully (this refers to RT's status, not the HTTP status!), read the session ID from the cookie data
		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("RT session established: " + response.getSessionId());
			}

			return response.getSessionId();
		}
		else {
			throw new RtException("Login failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Logout from the RT REST API, will destroy the session ID on the server.
	 *
	 * @throws RtException if logout request was not successful
	 */
	void logout(String sessionId) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_LOGOUT);

		RtRestResponse response = getResponse(sessionId, uriSb.toString());

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("RT session destroyed: " + sessionId);
			}
		}
		else {
			throw new RtException("Logout failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Perform a ticket properties query on the RT REST API.
	 * This will request information about a specific ticket and parse it into a RtTicket object.
	 *
	 * @param ticketId ID of the requested ticket, must be provided as 'ticket/[id]'
	 * @return Ticket object filled with the received values
	 *
	 * @throws RtException If the response contains a line starting with "#" (means error for ticket properties requests)
	 */
	RtTicket ticketProperties(String sessionId, String ticketId) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_TICKET_PROPERTIES);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("ticket-id", ticketId);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestTicketResponseParser.parseTicket(response.getBodyText());
		}
		else {
			throw new RtException("Ticket properties request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Search for tickets matching the provided query string and ordering them using the provided custom ordering.
	 *
	 * @param query   Search query, using the language generated by the RT query builder
	 * @param orderby Field and direction for ordering the results, using the language generated by the RT query builder
	 * @return A list of tickets matching the query
	 *
	 * @throws RtException if the request to the API failed
	 */
	List<RtTicket> searchTickets(String sessionId, String query, String orderby) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_TICKET_SEARCH);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("query", query);
		uriParams.put("order-by", orderby);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestTicketResponseParser.parseTickets(response.getBodyText());
		}
		else {
			throw new RtException("Search tickets request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Create a ticket from the provided information.
	 * The content variable must contain a line-by-line listing including key-value pairs for all keys defined below.
	 * Multiline texts must have all lines except the first prefixed with a space character. Required fields are not allowed to be empty.<br>
	 * <br>
	 * id: Ticket ID, must be set to 'ticket/new' (required)<br>
	 * Queue: Name of the queue the ticket is filed into (required)<br>
	 * Requestor: E-Mail address of the requestor (required)<br>
	 * Subject: The ticket's subject (required)<br>
	 * Cc: Comma separated list of CC receipients<br>
	 * AdminCc: Comma separated list of administrator CC receipients<br>
	 * Owner: Owner of the ticket<br>
	 * Status: Ticket status<br>
	 * Priority: Ticket priority<br>
	 * InitialPriority: Initial ticket priority<br>
	 * FinalPriority: Final ticket priority<br>
	 * TimeEstimated: Estimated time required to work on this ticket<br>
	 * Starts: Date and time the ticket starts (YYYY-MM-DD HH:MM:SS)<br>
	 * Due: Date and time the ticket has to be completed (YYYY-MM-DD HH:MM:SS)<br>
	 * Text: Ticket content text, multiline texts need their second and following lines prefixed with a space
	 *
	 * @param content Content string, fulfilling the above requirements
	 * @return true if the ticket was created successfully, false otherwise
	 *
	 * @throws RtException
	 */
	long createTicket(String sessionId, String content) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_TICKET_CREATE);

		// Define POST parameters
		LinkedMultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
		postParams.add("content", content);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), postParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestTicketResponseParser.parseTicketCreated(response.getBodyText());
		}
		else {
			throw new RtException("Create ticket request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Update an existing ticket with new information.
	 * This updates only the information directly attached to the ticket. It does not add comments or similar additional information.
	 *
	 * @param ticketId ID of the ticket to edit, must be provided as 'ticket/[id]'
	 * @param content  Content string, fulfilling the above requirements
	 * @return Ticket object filled with the updated values
	 *
	 * @throws RtException
	 */
	boolean editTicket(String sessionId, String ticketId, String content) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_TICKET_EDIT);

		// Define POST parameters
		LinkedMultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
		postParams.add("content", content);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("ticket-id", ticketId);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), postParams, uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200 || response.getRtStatus() == RtRestStatus.STATUS_409) {
			return RtRestTicketResponseParser.parseTicketEdited(response.getBodyText());
		}
		else {
			throw new RtException("Edit ticket request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Request the history of a specific ticket from the RT REST API.
	 *
	 * @param ticketId ID of the ticket whose history will be returned
	 * @return A list of history items, ordered the same way as in the API response
	 * @throws RtException
	 */
	List<RtTicketHistoryItem> getTicketHistory(String sessionId, String ticketId) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_TICKET_HISTORY);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("ticket-id", ticketId);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestTicketHistoryResponseParser.parseHistoryItems(response.getBodyText());
		}
		else {
			throw new RtException("Ticket history request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Post a comment or answer to an existing ticket using the RT REST API.
	 * The content string may contain the attributes shown below. The action attribute of the content
	 * string must be set to 'comment' or 'correspond', other values will result in a failed request (RT ERROR CODE 400).
	 * Adding attachments to the comment/answer is not supported.<br>
	 * <br>
	 * id: ID of the ticket to post to<br>
	 * Action: 'comment' or 'correspond' (answer), other values will make the request fail<br>
	 * Text: The text of the comment, multiline texts need their second and following lines prefixed with a space<br>
	 * Cc: CC receipients<br>
	 * Bcc: BCC receipients<br>
	 * TimeWorked: Number of minutes worked on the ticket (will be added to TimeWorked on the ticket)
	 *
	 * @param ticketId ID of the ticket which will be posted to
	 * @param content Content string
	 * @return true if the comment was posted successfully
	 * @throws RtException
	 */
	boolean writeTicketHistoryItem(String sessionId, String ticketId, String content) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_TICKET_COMMENT);

		// Define POST parameters
		LinkedMultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
		postParams.add("content", content);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("ticket-id", ticketId);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), postParams, uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestTicketHistoryResponseParser.parseTicketCommented(response.getBodyText());
		}
		else {
			throw new RtException("Edit ticket request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Perform a queue properties query on the RT REST API.
	 * This will request information about a specific queue and parse it into a RtQueue object.
	 *
	 * @param queueId ID of the requested queue, must be provided as 'queue/[id]'
	 * @return Queue object filled with the received values
	 *
	 * @throws RtException If the response contains a line starting with "#" (means error for queue properties requests)
	 */
	RtQueue queueProperties(String sessionId, String queueId) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_QUEUE_PROPERTIES);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("queue-id", queueId);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestQueueResponseParser.parseQueue(response.getBodyText());
		}
		else {
			throw new RtException("Queue properties request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * List all queues by searching for them via the RT REST API.
	 * This returns a map containing the mappings queueId=&gt;queueName.
	 *
	 * @return A map with the described mappings
	 * @throws RtException if the request to the API failed
	 */
	Map<Long, String> listQueues(String sessionId) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_QUEUE_LIST);

		RtRestResponse response = getResponse(sessionId, uriSb.toString());

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestQueueResponseParser.parseQueueList(response.getBodyText());
		}
		else {
			throw new RtException("Search queues request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Create a queue from the provided information.
	 * The content variable must contain a line-by-line listing including key-value pairs for all keys defined below.<br>
	 * <br>
	 * id: Queue ID, must be set to 'queue/new' (required)<br>
	 * Name: Name of the new queue (required)<br>
	 * Description: Description text of the queue<br>
	 * CorrespondAddress: E-Mail address used for answering on this queue<br>
	 * CommentAddress: E-Mail address used for commenting on this queue<br>
	 * InitialPriority: Initial priority for tickets in this queue<br>
	 * FinalPriority: Final priority for tickets in this queue<br>
	 * DefaultDueIn: Default time to complete tickets in this queue in days<br>
	 * Disabled: Whether the queue is disabled or not: 0 for false, 1 for true
	 *
	 * @param content Content string, fulfilling the above requirements
	 * @return true if the queue was created successfully, false otherwise
	 *
	 * @throws RtException
	 */
	long createQueue(String sessionId, String content) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_QUEUE_CREATE);

		// Define POST parameters
		LinkedMultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
		postParams.add("content", content);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), postParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestQueueResponseParser.parseQueueCreated(response.getBodyText());
		}
		else {
			throw new RtException("Create queue request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Request information about a RT user from the RT REST API.
	 * The returned information does not contain the user's password.
	 * User ID must be provided as either 'user/[uid]' or 'user/[uname]'.
	 *
	 * @param uid User ID of the user
	 * @return A user object containing the information
	 * @throws RtException
	 */
	RtUser userProperties(String sessionId, String uid) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_USER_PROPERTIES);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("user-id", String.valueOf(uid));

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestUserResponseParser.parseUser(response.getBodyText());
		}
		else {
			throw new RtException("User properties request (ID) failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	List<RtUser> searchUsers(String sessionId, String query, String orderby) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_USER_SEARCH);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("query", query);
		uriParams.put("order-by", orderby);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200) {
			return RtRestUserResponseParser.parseUsers(response.getBodyText());
		}
		else {
			throw new RtException("Search tickets request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Update an existing user with new information.
	 * This updates only the information directly attached to the user.
	 *
	 * @param userId ID of the user to edit, must be provided as 'user/[id]'
	 * @param content  Content string, fulfilling the above requirements
	 * @return User object filled with the updated values
	 *
	 * @throws RtException
	 */
	boolean editUser(String sessionId, String userId, String content) throws RtException {
		StringBuilder uriSb = new StringBuilder(this.baseUri);
		uriSb.append(this.URI_RT_REST_USER_EDIT);

		// Define POST parameters
		LinkedMultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
		postParams.add("content", content);

		// Define URI variables
		HashMap<String, String> uriParams = new HashMap<>();
		uriParams.put("user-id", userId);

		RtRestResponse response = getResponse(sessionId, uriSb.toString(), postParams, uriParams);

		if (response.getRtStatus() == RtRestStatus.STATUS_200 || response.getRtStatus() == RtRestStatus.STATUS_409) {
			return RtRestUserResponseParser.parseUserEdited(response.getBodyText());
		}
		else {
			throw new RtException("Edit user request failed with RT REST status: " + response.getRtStatus().toString());
		}
	}


	/**
	 * Perform a call to the REST API with no POST data and no URI variables.
	 *
	 * @param sessionId	RT session ID to use for the request
	 * @param uri URI of the REST API endpoint
	 * @return Response object with session ID and body text
	 *
	 * @throws RtException
	 */
	private RtRestResponse getResponse(String sessionId, String uri) throws RtException {
		return getResponse(sessionId, uri, new LinkedMultiValueMap<String, String>(), new HashMap<String, String>());
	}


	/**
	 * Perform a call to the REST API with POST data and no URI variables.
	 *
	 * @param sessionId	RT session ID to use for the request, may be null if credentials are provided as POST data
	 * @param uri        URI of the REST API endpoint
	 * @param postParams Map of POST data
	 * @return Response object with session ID and body text
	 *
	 * @throws RtException
	 */
	private RtRestResponse getResponse(String sessionId, String uri, MultiValueMap<String, String> postParams) throws RtException {
		return getResponse(sessionId, uri, postParams, new HashMap<String, String>());
	}


	/**
	 * Perform a call to the REST API with URI variables and no POST data.
	 *
	 * @param sessionId	RT session ID to use for the request
	 * @param uri       URI of the REST API endpoint
	 * @param uriParams Map of URI variables
	 * @return Response object with session ID and body text
	 *
	 * @throws RtException
	 */
	private RtRestResponse getResponse(String sessionId, String uri, Map<String, String> uriParams) throws RtException {
		return getResponse(sessionId, uri, new LinkedMultiValueMap<String, String>(), uriParams);
	}


	/**
	 * Perform a call to the REST API with POST data and URI variables.
	 *
	 * @param sessionId	RT session ID to use for the request, may be null if credentials are provided as POST data
	 * @param uri        URI of the REST API endpoint
	 * @param postParams Map of POST data
	 * @param uriParams  Map of URI variables
	 * @return Response object with session ID and body text
	 *
	 * @throws RtException
	 */
	private RtRestResponse getResponse(String sessionId, String uri, MultiValueMap<String, String> postParams, Map<String, String> uriParams) throws RtException {
		HttpHeaders requestHeaders = new HttpHeaders();

		// Ensure we get US/English date format from the REST API
		requestHeaders.add("Accept-Language", this.HTTP_HEADERS_ACCEPT_LANGUAGE);

		if(sessionId != null) {
			// Add session ID as cookie data if provided
			requestHeaders.add("Cookie", sessionId);
		}

		HttpEntity requestEntity = new HttpEntity(postParams, requestHeaders);
		ResponseEntity<String> responseEntity = this.REST_TEMPLATE.exchange(uri, HttpMethod.POST, requestEntity, String.class, uriParams);

		// Check if the request was successful
		if (responseEntity.getStatusCode() == HttpStatus.OK) {
			HttpHeaders responseHeaders = responseEntity.getHeaders();
			String[] cookieHeader = responseHeaders.get("Set-Cookie").get(0).split(";");
			String rtSessionId = cookieHeader[0];

			RtRestStatus rtStatus = RtRestResponseParser.parseStatus(responseEntity.getBody());

			if(LOG.isDebugEnabled() && rtStatus != RtRestStatus.STATUS_200) {
				LOG.debug("Received error response from RT REST:");
				LOG.debug(responseEntity.getBody());
			}

			RtRestResponse rtRes = new RtRestResponse(rtStatus, rtSessionId, responseEntity.getBody());

			return rtRes;
		}
		else {
			throw new RtException("REST API call failed with HTTP status code: " + responseEntity.getStatusCode().toString());
		}
	}
}
