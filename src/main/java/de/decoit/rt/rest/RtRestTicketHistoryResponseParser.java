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
import de.decoit.rt.model.RtTicketHistoryItem;
import de.decoit.rt.model.RtTicketHistoryItem.RtTicketHistoryItemType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


/**
 * This class provides methods to parse responses from ticket history requests to the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestTicketHistoryResponseParser extends RtRestResponseParser {
	private static final Logger LOG = Logger.getLogger(RtRestUserResponseParser.class);
	private static final SimpleDateFormat sdf;


	static {
		// Create a parser for the REST date format
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
	}


	/**
	 * Protected default constructor, this is a static only class
	 */
	protected RtRestTicketHistoryResponseParser() {
	}


	/**
	 * Parse the response string of a single ticket history item into a history item object.
	 *
	 * @param response The received response containing the item information
	 * @return A history item object
	 * @throws RtException if the response format is corrupt
	 */
	static RtTicketHistoryItem parseHistoryItem(String response) throws RtException {
		RtTicketHistoryItem historyItem = new RtTicketHistoryItem();

		StringBuilder contentSb = new StringBuilder();
		Pattern contentMultilinePattern = Pattern.compile("^\\s{9}(.*)$");

		HashMap<Long, String> attachmentMap = new HashMap<>();
		Pattern attachmentsMultilinePattern = Pattern.compile("^\\s{13}(\\d+): (.+?) \\((\\d+(\\.\\d+)?)[bk]\\)$");

		Pattern currentMultilinePattern = null;

		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			Matcher multilineMatcher = null;

			if (currentMultilinePattern != null) {
				multilineMatcher = currentMultilinePattern.matcher(line);
			}

			if (line.length() == 0) {
				// Blank line, ignore
			}
			else if (line.startsWith("RT/") || line.startsWith("#")) {
				// Message or status line, ignore
			}
			else if (multilineMatcher != null && multilineMatcher.matches()) {
				if (currentMultilinePattern != null && currentMultilinePattern.equals(contentMultilinePattern)) {
					contentSb.append("\n");
					contentSb.append(multilineMatcher.group(1));
				}
				else if (currentMultilinePattern != null && currentMultilinePattern.equals(attachmentsMultilinePattern)) {
					if (Double.parseDouble(multilineMatcher.group(3)) > 0.0) {
						attachmentMap.put(Long.valueOf(multilineMatcher.group(1)), multilineMatcher.group(2));
					}
				}
				else {
					throw new RtException("Unexpected multiline line found");
				}
			}
			else {
				if (currentMultilinePattern != null) {
					currentMultilinePattern = null;
				}

				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				switch (lineParts[0]) {
					case "id":
						historyItem.setId(Long.parseLong(lineParts[1]));
						break;
					case "Ticket":
						historyItem.setTicketId(Long.parseLong(lineParts[1]));
						break;
					case "TimeTaken":
						historyItem.setTimeTaken(Integer.parseInt(lineParts[1]));
						break;
					case "Type":
						historyItem.setType(RtTicketHistoryItemType.fromTypeText(lineParts[1]));
						break;
					case "Field":
						historyItem.setField(lineParts[1]);
						break;
					case "OldValue":
						historyItem.setOldValue(lineParts[1]);
						break;
					case "NewValue":
						historyItem.setNewValue(lineParts[1]);
						break;
					case "Data":
						historyItem.setData(lineParts[1]);
						break;
					case "Description":
						historyItem.setDescription(lineParts[1]);
						break;
					case "Content":
						currentMultilinePattern = contentMultilinePattern;
						contentSb.append(lineParts[1]);
						break;
					case "Creator":
						historyItem.setCreator(lineParts[1]);
						break;
					case "Created":
						try {
							historyItem.setCreated(parseDateString(lineParts[1]));
						}
						catch (ParseException ex) {
							historyItem.setCreated(null);
						}
						break;
					case "Attachments":
						currentMultilinePattern = attachmentsMultilinePattern;
						break;
					default:
					/*
					 * Ignore
					 */
				}
			}
		}

		historyItem.setContent(contentSb.toString());
		historyItem.setAttachments(attachmentMap);

		return historyItem;
	}


	/**
	 * Parse a list of ticket history items from a request response.
	 * The items will be ordered in the same way they are returned from the API.
	 *
	 * @param response The received response
	 * @return A list of history items
	 * @throws RtException if the response format is corrupt
	 */
	static List<RtTicketHistoryItem> parseHistoryItems(String response) throws RtException {
		ArrayList<RtTicketHistoryItem> itemList = new ArrayList<>();

		String[] responseParts = response.split(RtRestResponseParser.LIST_DELIMITER);

		for (String part : responseParts) {
			itemList.add(parseHistoryItem(part));
		}

		return itemList;
	}


	/**
	 * Parse the response of a comment ticket request and check for success message.
	 *
	 * @param response The received response
	 * @return true if the comment was written successful
	 * @throws RtException if the comment action failed
	 */
	static boolean parseTicketCommented(String response) throws RtException {
		String[] responseParts = response.split("\n");

		for (String part : responseParts) {
			if(part.equals("# Message recorded")) {
				return true;
			}
		}

		throw new RtException("Ticket comment failed");
	}


	/**
	 * Generate a content string for the RT REST API from an existing history item object.
	 * This only works for history items with the type COMMENT or CORRESPOND, for all other types null will be returned.
	 * Attachments are not supported.
	 *
	 * @param item The history item object
	 * @param cc   Optional CC receipient list for comment, may be null
	 * @param bcc  Optional BCC receipient list for comment, may be null
	 * @return Generated content string, null for invalid history item type
	 */
	static String historyItemToString(RtTicketHistoryItem item, String cc, String bcc) {
		if (item.getType() == RtTicketHistoryItemType.COMMENT || item.getType() == RtTicketHistoryItemType.CORRESPOND) {
			StringBuilder sb = new StringBuilder();

			// Ticket ID
			sb.append("id: ");
			sb.append(item.getTicketId());
			sb.append("\n");

			// Action
			sb.append("Action: ");
			sb.append(item.getType().toString().toLowerCase());
			sb.append("\n");

			// Text
			sb.append("Text: ");
			sb.append(item.getContent().replaceAll("\n", "\n "));
			sb.append("\n");

			// CC receipients
			if (cc != null) {
				sb.append("Cc: ");
				sb.append(cc);
				sb.append("\n");
			}

			// BCC receipients
			if (bcc != null) {
				sb.append("Bcc: ");
				sb.append(bcc);
				sb.append("\n");
			}

			// Time worked
			sb.append("TimeWorked: ");
			sb.append(item.getTimeTaken());
			sb.append("\n");

			return sb.toString();
		}
		else {
			LOG.debug("Wrong history item type for string creation: " + item.getType().toString());

			return null;
		}
	}


	/**
	 * Parse the date string returned from RT into a native Java Date object
	 *
	 * @param date Date string returned from RT
	 * @return A native Date object representing the provided date
	 *
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
		if (date != null) {
			return sdf.format(date);
		}
		else {
			return "";
		}
	}
}
