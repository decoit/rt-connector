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
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.decoit.rt.RtException;
import de.decoit.rt.model.RtTicket;
import de.decoit.rt.model.RtTicket.RtTicketStatus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * This class provides methods to parse responses from ticket requests to the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
class RtRestTicketResponseParser extends RtRestResponseParser {
	private static final Logger LOG = Logger.getLogger(RtRestTicketResponseParser.class);
	private static final SimpleDateFormat sdf;

	static {
		// Create a parser for the REST date format
		sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US);
	}


	/**
	 * Protected default constructor, this is a static only class
	 */
	protected RtRestTicketResponseParser() {
	}


	/**
	 * Create a RtTicket instance from the response of a RT REST request. This only works for ticket properties requests, other responses may return defect objects.
	 *
	 * @param response Response text received from the RT REST API
	 * @return A ticket object filled with the values from the response string
	 *
	 * @throws RtException If a line starting with "#" was found (means error for ticket properties requests)
	 */
	static RtTicket parseTicket(String response) throws RtException {
		RtTicket ticket = new RtTicket();

		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			if (StringUtils.isBlank(line)) {
				// Blank line, ignore
			}
			else if (line.startsWith("RT/")) {
				// Status line, ignore
			}
			else if (line.startsWith("#")) {
				// Message line, in case of ticket query means something went wrong, raise exception
				throw new RtException(line.substring(2));
			}
			else if (line.startsWith("CF.{")) {
				// New style custom field line, parse and add to custom fields of the ticket
				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				Pattern cfNamePattern = Pattern.compile("^CF\\.\\{(.+?)\\}$");
				Matcher cfNameMatcher = cfNamePattern.matcher(lineParts[0]);
				if(cfNameMatcher.matches()) {
					String cfName = cfNameMatcher.group(1);

					ticket.addCustomField(cfName, lineParts[1]);
				}
				else {
					LOG.warn("Invalid custom field line detected: " + line);
				}
			}
			else if (line.startsWith("CF-")) {
				// Old style custom field line, parse and add to custom fields of the ticket
				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				Pattern cfNamePattern = Pattern.compile("^CF-(.+?)$");
				Matcher cfNameMatcher = cfNamePattern.matcher(lineParts[0]);
				if(cfNameMatcher.matches()) {
					String cfName = cfNameMatcher.group(1);

					ticket.addCustomField(cfName, lineParts[1]);
				}
				else {
					LOG.warn("Invalid custom field line detected: " + line);
				}
			}
			else {
				Pattern idPattern = Pattern.compile("^ticket/(\\d+)$");
				Matcher idMatcher;
				Pattern timePattern = Pattern.compile("^(\\d+?) (minutes)?$");
				Matcher timeMatcher;

				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				switch(lineParts[0]) {
					case "id":
						idMatcher = idPattern.matcher(lineParts[1]);

						if(idMatcher.matches()) {
							ticket.setId(Long.parseLong(idMatcher.group(1)));
						}
						else {
							throw new RtException("Invalid ticket ID pattern: " + lineParts[1]);
						}
						break;
					case "Queue":
						ticket.setQueue(lineParts[1]);
						break;
					case "Owner":
						ticket.setOwner(lineParts[1]);
						break;
					case "Creator":
						ticket.setCreator(lineParts[1]);
						break;
					case "Subject":
						ticket.setSubject(lineParts[1]);
						break;
					case "Status":
						ticket.setStatus(RtTicketStatus.fromStatusText(lineParts[1]));
						break;
					case "Priority":
						ticket.setPriority(Integer.parseInt(lineParts[1]));
						break;
					case "InitialPriority":
						ticket.setInitialPriority(Integer.parseInt(lineParts[1]));
						break;
					case "FinalPriority":
						ticket.setFinalPriority(Integer.parseInt(lineParts[1]));
						break;
					case "Requestors":
						ticket.setRequestors(Lists.newArrayList(Splitter.on(",").trimResults().split(lineParts[1])));
						break;
					case "Cc":
						ticket.setCc(Lists.newArrayList(Splitter.on(",").trimResults().split(lineParts[1])));
						break;
					case "AdminCc":
						ticket.setAdminCc(Lists.newArrayList(Splitter.on(",").trimResults().split(lineParts[1])));
						break;
					case "Created":
						try {
							ticket.setCreated(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setCreated(null);
						}
						break;
					case "Starts":
						try {
							ticket.setStarts(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setStarts(null);
						}
						break;
					case "Started":
						try {
							ticket.setStarted(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setStarted(null);
						}
						break;
					case "Due":
						try {
							ticket.setDue(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setDue(null);
						}
						break;
					case "Resolved":
						try {
							ticket.setResolved(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setResolved(null);
						}
						break;
					case "Told":
						try {
							ticket.setTold(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setTold(null);
						}
						break;
					case "LastUpdated":
						try {
							ticket.setLastUpdated(parseDateString(lineParts[1]));
						}
						catch(ParseException ex) {
							ticket.setLastUpdated(null);
						}
						break;
					case "TimeEstimated":
						timeMatcher = timePattern.matcher(lineParts[1]);
						if(timeMatcher.matches()) {
							ticket.setTimeEstimated(Integer.parseInt(timeMatcher.group(1)));
						}
						else {
							ticket.setTimeEstimated(0);
						}
						break;
					case "TimeWorked":
						timeMatcher = timePattern.matcher(lineParts[1]);
						if(timeMatcher.matches()) {
							ticket.setTimeWorked(Integer.parseInt(timeMatcher.group(1)));
						}
						else {
							ticket.setTimeWorked(0);
						}
						break;
					case "TimeLeft":
						timeMatcher = timePattern.matcher(lineParts[1]);
						if(timeMatcher.matches()) {
							ticket.setTimeLeft(Integer.parseInt(timeMatcher.group(1)));
						}
						else {
							ticket.setTimeLeft(0);
						}
						break;
					default:
						/* Ignore */
				}
			}
		}

		return ticket;
	}


	/**
	 * Parse a list of tickets into a Java list.
	 * The reponse list must be in the format of a long format ticket search. It uses the parseTicket() method to
	 * create ticket objects from the list parts. Ordering is preserved and will be the same as in the response
	 * string.
	 *
	 * @param response Response text received from the RT REST API
	 * @return A list of tickets
	 * @throws RtException
	 */
	static List<RtTicket> parseTickets(String response) throws RtException {
		ArrayList<RtTicket> ticketList = new ArrayList<>();

		if(response.contains("No matching results.")) {
			// No results found, return empty list
			return ticketList;
		}

		String[] responseParts = response.split(RtRestResponseParser.LIST_DELIMITER);

		for (String part : responseParts) {
			ticketList.add(parseTicket(part));
		}

		return ticketList;
	}


	/**
	 * Parse a response text to determine if a ticket was created successfully and return the new ticket's ID.
	 * It checks the message line of the response (beginning with a #) for the success message.
	 *
	 * @param response Response text received from the RT REST API
	 * @return The ID of the created ticket
	 */
	static long parseTicketCreated(String response) throws RtException {
		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			// Line with status message, it is the only one that is of interest in this case
			if (line.startsWith("#")) {
				Pattern p = Pattern.compile("^# Ticket (\\d+?) created\\.$");
				Matcher m = p.matcher(line);

				if (m.matches()) {
					return Long.parseLong(m.group(1));
				}
			}
			else {
				// All other lines, ignore
			}
		}

		if(LOG.isDebugEnabled()) {
			LOG.debug("Ticket creation failed");
			LOG.debug(response);
		}
		throw new RtException("Ticket creation failed");
	}


	/**
	 * Parse the response of an update ticket request to decide if the update was successful.
	 *
	 * @param response Response text received from the RT REST API
	 * @return true if the ticket update was succcessful
	 * @throws RtException if the ticket update failed
	 */
	static boolean parseTicketEdited(String response) throws RtException {
		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			// Line with status message, it is the only one that is of interest in this case
			if (line.startsWith("#")) {
				Pattern p = Pattern.compile("^# Ticket \\d+? updated\\.$");
				Matcher m = p.matcher(line);

				if(m.matches()) {
					return true;
				}
			}
			else {
				// All other lines, ignore
			}
		}

		throw new RtException("Ticket edit failed");
	}


	/**
	 * Build a content string from a ticket object.
	 * This string contains all information set in the object and can be used to create a new ticket
	 * or update an existing one.
	 *
	 * @param ticket Ticket object to transform into a string
	 * @param newTicket Whether the string should be used for creation (true) or update (false)
	 * @param oldTicket In case of ticket update, the existing ticket must be provided here. In case of ticket creation this may be null.
	 * @return The generated content string
	 */
	static String ticketToString(RtTicket ticket, boolean newTicket, RtTicket oldTicket) throws RtException {
		if(newTicket) {
			return newTicketToString(ticket);
		}
		else if(!newTicket && oldTicket != null) {
			return editTicketToString(ticket, oldTicket);
		}
		else {
			throw new RtException("No existing ticket provided for ticket update");
		}
	}


	/**
	 * Parse the date string returned from RT into a native Java Date object
	 *
	 * @param date Date string returned from RT
	 * @return A native Date object representing the provided date
	 * @throws ParseException If the date string could not be parsed
	 */
	static Date parseDateString(String date) throws ParseException {
		return sdf.parse(date);
	}


	/**
	 * Format a Date object into the date string used by the specific implementation
	 *
	 * @param date Native Date object
	 * @return Date string required for the specific implementation
	 */
	static String formatDateString(Date date) {
		if(date != null) {
			return sdf.format(date);
		}
		else {
			return "";
		}
	}


	private static String newTicketToString(RtTicket ticket) {
		StringBuilder sb = new StringBuilder();
		Joiner commaJoiner = Joiner.on(",").skipNulls();

		// Ticket ID
		sb.append("id: ticket/new\n");

		// Ticket queue
		sb.append("Queue: ");
		sb.append(ticket.getQueue());
		sb.append("\n");

		// Owner
		sb.append("Owner: ");
		sb.append(ticket.getOwner());
		sb.append("\n");

		// Subject
		sb.append("Subject: ");
		sb.append(ticket.getSubject());
		sb.append("\n");

		// Priority
		sb.append("Priority: ");
		sb.append(ticket.getPriority());
		sb.append("\n");

		// Initial Priority
		sb.append("InitialPriority: ");
		sb.append(ticket.getInitialPriority());
		sb.append("\n");

		// Final Priority
		sb.append("FinalPriority: ");
		sb.append(ticket.getFinalPriority());
		sb.append("\n");

		// Requestors
		sb.append("Requestors: ");
		sb.append(commaJoiner.join(ticket.getRequestors()));
		sb.append("\n");

		// CC
		sb.append("Cc: ");
		sb.append(commaJoiner.join(ticket.getCc()));
		sb.append("\n");

		// AdminCC
		sb.append("AdminCc: ");
		sb.append(commaJoiner.join(ticket.getAdminCc()));
		sb.append("\n");

		// Starts
		sb.append("Starts: ");
		sb.append(formatDateString(ticket.getStarts()));
		sb.append("\n");

		// Due
		sb.append("Due: ");
		sb.append(formatDateString(ticket.getDue()));
		sb.append("\n");

		// Time estimated
		sb.append("TimeEstimated: ");
		sb.append(ticket.getTimeEstimated());
		sb.append("\n");

		// Time worked
		sb.append("TimeWorked: ");
		sb.append(ticket.getTimeWorked());
		sb.append("\n");

		// Time left
		sb.append("TimeLeft: ");
		sb.append(ticket.getTimeLeft());
		sb.append("\n");

		// Text
		if(ticket.getText() != null) {
			sb.append("Text: ");
			sb.append(ticket.getText().replaceAll("\n", "\n "));
			sb.append("\n");
		}

		if(ticket.getCustomFields() != null) {
			for(Entry<String, String> e : ticket.getCustomFields().entrySet()) {
				sb.append("CF.{");
				sb.append(e.getKey());
				sb.append("}: ");
				sb.append(e.getValue());
				sb.append("\n");
			}
		}

		return sb.toString();
	}


	private static String editTicketToString(RtTicket ticket, RtTicket oldTicket) {
		StringBuilder sb = new StringBuilder();
		Joiner commaJoiner = Joiner.on(",").skipNulls();

		// Ticket queue
		if(!oldTicket.getQueue().equals(ticket.getQueue())) {
			sb.append("Queue: ");
			sb.append(ticket.getQueue());
			sb.append("\n");
		}

		// Owner
		if(!oldTicket.getOwner().equals(ticket.getOwner())) {
			sb.append("Owner: ");
			sb.append(ticket.getOwner());
			sb.append("\n");
		}

		// Subject
		if(!oldTicket.getSubject().equals(ticket.getSubject())) {
			sb.append("Subject: ");
			sb.append(ticket.getSubject());
			sb.append("\n");
		}

		// Status
		if(oldTicket.getStatus() != ticket.getStatus()) {
			sb.append("Status: ");
			sb.append(ticket.getStatus());
			sb.append("\n");
		}

		// Priority
		if(oldTicket.getPriority() != ticket.getPriority()) {
			sb.append("Priority: ");
			sb.append(ticket.getPriority());
			sb.append("\n");
		}

		// Initial Priority
		if(oldTicket.getInitialPriority() != ticket.getInitialPriority()) {
			sb.append("InitialPriority: ");
			sb.append(ticket.getInitialPriority());
			sb.append("\n");
		}

		// Final Priority
		if(oldTicket.getFinalPriority() != ticket.getFinalPriority()) {
			sb.append("FinalPriority: ");
			sb.append(ticket.getFinalPriority());
			sb.append("\n");
		}

		// Requestors
		List<String> oldRequestorsList = oldTicket.getRequestors();
		List<String> newRequestorsList = ticket.getRequestors();
		boolean requestorsEqual = false;
		if(oldRequestorsList.size() == newRequestorsList.size()) {
			Collections.sort(oldRequestorsList);
			Collections.sort(newRequestorsList);

			if(oldRequestorsList.equals(newRequestorsList)) {
				requestorsEqual = true;
			}
		}
		if(!requestorsEqual) {
			sb.append("Requestors: ");
			sb.append(commaJoiner.join(ticket.getRequestors()));
			sb.append("\n");
		}

		// CC
		List<String> oldCcList = oldTicket.getCc();
		List<String> newCcList = ticket.getCc();
		boolean ccEqual = false;
		if(oldCcList.size() == newCcList.size()) {
			Collections.sort(oldCcList);
			Collections.sort(newCcList);

			if(oldCcList.equals(newCcList)) {
				ccEqual = true;
			}
		}
		if(!ccEqual) {
			sb.append("Cc: ");
			sb.append(commaJoiner.join(ticket.getCc()));
			sb.append("\n");
		}

		// AdminCC
		List<String> oldAdminCcList = oldTicket.getAdminCc();
		List<String> newAdminCcList = ticket.getAdminCc();
		boolean adminCcEqual = false;
		if(oldAdminCcList.size() == newAdminCcList.size()) {
			Collections.sort(oldAdminCcList);
			Collections.sort(newAdminCcList);

			if(oldAdminCcList.equals(newAdminCcList)) {
				adminCcEqual = true;
			}
		}
		if(!adminCcEqual) {
			sb.append("AdminCc: ");
			sb.append(commaJoiner.join(ticket.getAdminCc()));
			sb.append("\n");
		}

		// Starts
		if(ticket.getStarts() != null && !ticket.getStarts().equals(oldTicket.getStarts())) {
			sb.append("Starts: ");
			sb.append(formatDateString(ticket.getStarts()));
			sb.append("\n");
		}

		// Started
		if(ticket.getStarted() != null && !ticket.getStarted().equals(oldTicket.getStarted())) {
			sb.append("Started: ");
			sb.append(formatDateString(ticket.getStarted()));
			sb.append("\n");
		}

		// Due
		if(ticket.getDue() != null && !ticket.getDue().equals(oldTicket.getDue())) {
			sb.append("Due: ");
			sb.append(formatDateString(ticket.getDue()));
			sb.append("\n");
		}

		// Resolved
		if(ticket.getResolved() != null && !ticket.getResolved().equals(oldTicket.getResolved())) {
			sb.append("Resolved: ");
			sb.append(formatDateString(ticket.getResolved()));
			sb.append("\n");
		}

		// Told
		if(ticket.getTold() != null && !ticket.getTold().equals(oldTicket.getTold())) {
			sb.append("Told: ");
			sb.append(formatDateString(ticket.getTold()));
			sb.append("\n");
		}

		// Time estimated
		if(oldTicket.getTimeEstimated() != ticket.getTimeEstimated()) {
			sb.append("TimeEstimated: ");
			sb.append(ticket.getTimeEstimated());
			sb.append("\n");
		}

		// Time worked
		if(oldTicket.getTimeWorked() != ticket.getTimeWorked()) {
			sb.append("TimeWorked: ");
			sb.append(ticket.getTimeWorked());
			sb.append("\n");
		}

		// Time left
		if(oldTicket.getTimeLeft() != ticket.getTimeLeft()) {
			sb.append("TimeLeft: ");
			sb.append(ticket.getTimeLeft());
			sb.append("\n");
		}

		// Text
		if(ticket.getText() != null) {
			sb.append("Text: ");
			sb.append(ticket.getText().replaceAll("\n", "\n "));
			sb.append("\n");
		}

		if(ticket.getCustomFields() != null) {
			for(Entry<String, String> e : ticket.getCustomFields().entrySet()) {
				sb.append("CF.{");
				sb.append(e.getKey());
				sb.append("}: ");
				sb.append(e.getValue());
				sb.append("\n");
			}
		}

		return sb.toString();
	}
}
