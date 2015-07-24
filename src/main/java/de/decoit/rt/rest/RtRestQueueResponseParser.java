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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * This class provides a number of static methods to parse responses from the RT REST API regarding operations on queues.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestQueueResponseParser extends RtRestResponseParser {
	private static final Logger LOG = Logger.getLogger(RtRestQueueResponseParser.class);


	/**
	 * Protected default constructor, this is a static only class
	 */
	protected RtRestQueueResponseParser() {
	}


	/**
	 * This parses a single queue object from the response of a RT REST API request.
	 * A response string in the format of the queue properties or long format queue search
	 * responses is required.
	 *
	 * @param response Response string containing queue information
	 * @return A queue object built from the information
	 * @throws RtException if the string contains invalid values
	 */
	static RtQueue parseQueue(String response) throws RtException {
		RtQueue queue = new RtQueue();
		Pattern idPattern = Pattern.compile("^queue/(\\d+)$");

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
				// New style custom field line, parse and add to custom fields of the queue
				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				Pattern cfNamePattern = Pattern.compile("^CF\\.\\{(.+?)\\}$");
				Matcher cfNameMatcher = cfNamePattern.matcher(lineParts[0]);
				if(cfNameMatcher.matches()) {
					String cfName = cfNameMatcher.group(1);

					queue.addCustomField(cfName, lineParts[1]);
				}
				else {
					LOG.warn("Invalid custom field line detected: " + line);
				}
			}
			else if (line.startsWith("CF-")) {
				// Old style custom field line, parse and add to custom fields of the queue
				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				Pattern cfNamePattern = Pattern.compile("^CF-(.+?)$");
				Matcher cfNameMatcher = cfNamePattern.matcher(lineParts[0]);
				if(cfNameMatcher.matches()) {
					String cfName = cfNameMatcher.group(1);

					queue.addCustomField(cfName, lineParts[1]);
				}
				else {
					LOG.warn("Invalid custom field line detected: " + line);
				}
			}
			else {
				Matcher idMatcher;

				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				switch (lineParts[0]) {
					case "id":
						idMatcher = idPattern.matcher(lineParts[1]);

						if (idMatcher.matches()) {
							queue.setId(Long.parseLong(idMatcher.group(1)));
						}
						else {
							throw new RtException("Invalid queue ID pattern: " + lineParts[1]);
						}
						break;
					case "Name":
						queue.setName(lineParts[1]);
						break;
					case "Description":
						queue.setDescription(lineParts[1]);
						break;
					case "CorrespondAddress":
						queue.setCorrespondAddress(lineParts[1]);
						break;
					case "CommentAddress":
						queue.setCommentAddress(lineParts[1]);
						break;
					case "InitialPriority":
						queue.setInitialPriority(Integer.parseInt(lineParts[1]));
						break;
					case "FinalPriority":
						queue.setFinalPriority(Integer.parseInt(lineParts[1]));
						break;
					case "DefaultDueIn":
						queue.setDefaultDueIn(Integer.parseInt(lineParts[1]));
						break;
					case "Disabled":
						switch (lineParts[1]) {
							case "0":
								queue.setDisabled(false);
								break;
							case "1":
								queue.setDisabled(true);
								break;
							default:
								throw new RtException("Invalid queue disabled status value: " + lineParts[1]);
						}
						break;
					default:
						// Ignore
				}
			}
		}

		return queue;
	}


	/**
	 * Parse a list of queues into a Java map.
	 * The map will contain queueId=&gt;queueName mappings. No information beyond that is collected.
	 * The method requires a simple list of queues as returned from the queue search request with default
	 * format.
	 *
	 * @param response Response string containing queue list
	 * @return A map with the described mappings
	 */
	static Map<Long, String> parseQueueList(String response) {
		HashMap<Long, String> queueMap = new HashMap<>();
		Pattern p = Pattern.compile("^(\\d+): (.+)$");

		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			Matcher m = p.matcher(line);

			if (m.matches()) {
				// List line, extract queue ID and name and put them into the map
				// All other lines are ignored
				Long id = Long.valueOf(m.group(1));
				String name = m.group(2);

				queueMap.put(id, name);
			}
		}

		return queueMap;
	}


	/**
	 * Parse the response of a create queue REST API request.
	 * The method will extract the ID of the new queue from the response and return it. If the
	 * creation failed, a RtException will be raised.
	 *
	 * @param response Response string received from the RT REST API
	 * @return The ID of the created queue
	 * @throws RtException if queue creation failed
	 */
	static long parseQueueCreated(String response) throws RtException {
		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			// Line with status message, it is the only one that is of interest in this case
			if (line.startsWith("#")) {
				Pattern p = Pattern.compile("^# Queue (\\d+?) created\\.$");
				Matcher m = p.matcher(line);

				if (m.matches()) {
					return Long.parseLong(m.group(1));
				}
			}
			else {
				// All other lines, ignore
			}
		}

		throw new RtException("Queue creation failed");
	}


	/**
	 * Build a content string from a queue object.
	 * This string contains all information set in the object and can be used to create a new queue
	 * or update an existing one.
	 *
	 * @param queue Queue object to transform into a string
	 * @param newQueue Whether the string should be used for creation (true) or update (false)
	 * @return The generated content string
	 */
	static String queueToString(RtQueue queue, boolean newQueue) {
		StringBuilder sb = new StringBuilder();

		// Ticket ID
		if (newQueue) {
			sb.append("id: queue/new\n");
		}

		// Queue name
		sb.append("Name: ");
		sb.append(queue.getName());
		sb.append("\n");

		// Queue description
		sb.append("Description: ");
		sb.append(queue.getDescription());
		sb.append("\n");

		// Queue correspond address
		sb.append("CorrespondAddress: ");
		sb.append(queue.getCorrespondAddress());
		sb.append("\n");

		// Queue comment address
		sb.append("CommentAddress: ");
		sb.append(queue.getCommentAddress());
		sb.append("\n");

		// Queue initial priority
		sb.append("InitialPriority: ");
		sb.append(queue.getInitialPriority());
		sb.append("\n");

		// Queue final priority
		sb.append("FinalPriority: ");
		sb.append(queue.getFinalPriority());
		sb.append("\n");

		// Queue default due in
		sb.append("DefaultDueIn: ");
		sb.append(queue.getDefaultDueIn());
		sb.append("\n");

		// Queue disabled
		sb.append("Disabled: ");
		if (queue.isDisabled()) {
			sb.append(1);
		}
		else {
			sb.append(0);
		}
		sb.append("\n");

		return sb.toString();
	}
}
