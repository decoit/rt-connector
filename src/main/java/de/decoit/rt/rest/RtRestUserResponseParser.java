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
import de.decoit.rt.model.RtUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


/**
 * This class provides a number of static methods to parse responses from the RT REST API regarding operations on users.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestUserResponseParser extends RtRestResponseParser {
	private static final Logger LOG = Logger.getLogger(RtRestUserResponseParser.class);


	/**
	 * Protected default constructor, this is a static only class
	 */
	protected RtRestUserResponseParser() {
	}


	/**
	 * Parse the response of a user properties request into a user object.
	 *
	 * @param response Response text received from the RT REST API
	 * @return User object
	 * @throws RtException if the response contains invalid values or lines
	 */
	static RtUser parseUser(String response) throws RtException {
		RtUser user = new RtUser();

		StringBuilder contactInfoSb = new StringBuilder();
		StringBuilder commentsSb = new StringBuilder();
		StringBuilder signatureSb = new StringBuilder();

		StringBuilder currentMultilineSb = null;
		Pattern multilinePattern = Pattern.compile("^\\s+(.*)$");

		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			Matcher multilineMatcher = multilinePattern.matcher(line);

			if (line.length() == 0) {
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
				// New style custom field line, parse and add to custom fields of the user
				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				Pattern cfNamePattern = Pattern.compile("^CF\\.\\{(.+?)\\}$");
				Matcher cfNameMatcher = cfNamePattern.matcher(lineParts[0]);
				if(cfNameMatcher.matches()) {
					String cfName = cfNameMatcher.group(1);

					user.addCustomField(cfName, lineParts[1]);
				}
				else {
					LOG.warn("Invalid custom field line detected: " + line);
				}
			}
			else if (line.startsWith("CF-")) {
				// Old style custom field line, parse and add to custom fields of the user
				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				Pattern cfNamePattern = Pattern.compile("^CF-(.+?)$");
				Matcher cfNameMatcher = cfNamePattern.matcher(lineParts[0]);
				if(cfNameMatcher.matches()) {
					String cfName = cfNameMatcher.group(1);

					user.addCustomField(cfName, lineParts[1]);
				}
				else {
					LOG.warn("Invalid custom field line detected: " + line);
				}
			}
			else if (multilineMatcher.matches()) {
				if(currentMultilineSb != null) {
					currentMultilineSb.append("\n");
					currentMultilineSb.append(multilineMatcher.group(1));
				}
				else {
					throw new RtException("Unexpected multiline line found");
				}
			}
			else {
				if(currentMultilineSb != null) {
					currentMultilineSb = null;
				}

				Pattern idPattern = Pattern.compile("^user/(\\d+)$");
				Matcher idMatcher;

				String[] lineParts = line.split(":", 2);
				lineParts[1] = lineParts[1].trim();

				switch(lineParts[0]) {
					case "id":
						idMatcher = idPattern.matcher(lineParts[1]);

						if(idMatcher.matches()) {
							user.setId(Long.parseLong(idMatcher.group(1)));
						}
						else {
							throw new RtException("Invalid user ID pattern: " + lineParts[1]);
						}
						break;
					case "Name":
						user.setName(lineParts[1]);
						break;
					case "EmailAddress":
						user.setEmailAddress(lineParts[1]);
						break;
					case "RealName":
						user.setRealName(lineParts[1]);
						break;
					case "NickName":
						user.setNickName(lineParts[1]);
						break;
					case "Gecos":
						user.setGecos(lineParts[1]);
						break;
					case "Organization":
						user.setOrganization(lineParts[1]);
						break;
					case "Address1":
						user.setAddress1(lineParts[1]);
						break;
					case "Address2":
						user.setAddress2(lineParts[1]);
						break;
					case "City":
						user.setCity(lineParts[1]);
						break;
					case "State":
						user.setState(lineParts[1]);
						break;
					case "Zip":
						user.setZip(lineParts[1]);
						break;
					case "Country":
						user.setCountry(lineParts[1]);
						break;
					case "HomePhone":
						user.setHomePhone(lineParts[1]);
						break;
					case "WorkPhone":
						user.setWorkPhone(lineParts[1]);
						break;
					case "MobilePhone":
						user.setMobilePhone(lineParts[1]);
						break;
					case "PagerPhone":
						user.setPagerPhone(lineParts[1]);
						break;
					case "ContactInfo":
						currentMultilineSb = contactInfoSb;
						currentMultilineSb.append(lineParts[1]);
						break;
					case "Comments":
						currentMultilineSb = commentsSb;
						currentMultilineSb.append(lineParts[1]);
						break;
					case "Signature":
						currentMultilineSb = signatureSb;
						currentMultilineSb.append(lineParts[1]);
						break;
					case "Lang":
						user.setLang(lineParts[1]);
						break;
					case "Privileged":
						switch (lineParts[1]) {
							case "0":
								user.setPrivileged(false);
								break;
							case "1":
								user.setPrivileged(true);
								break;
							default:
								throw new RtException("Invalid user privileged status value: " + lineParts[1]);
						}
						break;
					case "Disabled":
						switch (lineParts[1]) {
							case "0":
								user.setDisabled(false);
								break;
							case "1":
								user.setDisabled(true);
								break;
							default:
								throw new RtException("Invalid user disabled status value: " + lineParts[1]);
						}
						break;
					default:
						/* Ignore */
				}
			}
		}

		// Insert multiline attributes into the object
		user.setContactInfo(contactInfoSb.toString());
		user.setComments(commentsSb.toString());
		user.setSignature(signatureSb.toString());

		return user;
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
	static List<RtUser> parseUsers(String response) throws RtException {
		ArrayList<RtUser> userList = new ArrayList<>();

		if(response.contains("No matching results.")) {
			// No results found, return empty list
			return userList;
		}

		String[] responseParts = response.split(RtRestResponseParser.LIST_DELIMITER);

		for (String part : responseParts) {
			userList.add(parseUser(part));
		}

		return userList;
	}


	/**
	 * Parse the response of an update user request to decide if the update was successful.
	 *
	 * @param response Response text received from the RT REST API
	 * @return true if the user update was succcessful
	 * @throws RtException if the user update failed
	 */
	static boolean parseUserEdited(String response) throws RtException {
		// Split lines in response string
		String[] responseParts = response.split("\n");

		for (String line : responseParts) {
			// Line with status message, it is the only one that is of interest in this case
			if (line.startsWith("#")) {
				Pattern p = Pattern.compile("^# User \\d+? updated\\.$");
				Matcher m = p.matcher(line);

				if(m.matches()) {
					return true;
				}
			}
			else {
				// All other lines, ignore
			}
		}

		throw new RtException("User edit failed");
	}


	/**
	 * Build a content string from a user object.
	 * This string contains all information set in the object and can be used to create a new user
	 * or update an existing one. Creating a new user is not yet implemented and will cause a
	 * UnsupportedOperationException to be thrown.
	 *
	 * @param user User object to transform into a string
	 * @param newUser Whether the string should be used for creation (true) or update (false)
	 * @param oldUser In case of user update, the existing user must be provided here. In case of user creation this may be null.
	 * @return The generated content string
	 */
	static String userToString(RtUser user, boolean newUser, RtUser oldUser) throws RtException {
		if(newUser) {
			throw new UnsupportedOperationException("Creating users is not yet implemented");
//			return newTicketToString(ticket);
		}
		else if(!newUser && oldUser != null) {
			return editUserToString(user, oldUser);
		}
		else {
			throw new RtException("No existing user provided for user update");
		}
	}


	private static String editUserToString(RtUser user, RtUser oldUser) {
		StringBuilder sb = new StringBuilder();

		// E-Mail address
		if(!oldUser.getEmailAddress().equals(user.getEmailAddress())) {
			sb.append("EmailAddress: ");
			sb.append(user.getEmailAddress());
			sb.append("\n");
		}

		// Real name
		if(!oldUser.getRealName().equals(user.getRealName())) {
			sb.append("RealName: ");
			sb.append(user.getRealName());
			sb.append("\n");
		}

		// Nick name
		if(!oldUser.getNickName().equals(user.getNickName())) {
			sb.append("NickName: ");
			sb.append(user.getNickName());
			sb.append("\n");
		}

		// Gecos
		if(!oldUser.getGecos().equals(user.getGecos())) {
			sb.append("Gecos: ");
			sb.append(user.getGecos());
			sb.append("\n");
		}

		// Organization
		if(!oldUser.getOrganization().equals(user.getOrganization())) {
			sb.append("Organization: ");
			sb.append(user.getOrganization());
			sb.append("\n");
		}

		// Address 1
		if(!oldUser.getAddress1().equals(user.getAddress1())) {
			sb.append("Address1: ");
			sb.append(user.getAddress1());
			sb.append("\n");
		}

		// Address 2
		if(!oldUser.getAddress2().equals(user.getAddress2())) {
			sb.append("Address2: ");
			sb.append(user.getAddress2());
			sb.append("\n");
		}

		// City
		if(!oldUser.getCity().equals(user.getCity())) {
			sb.append("City: ");
			sb.append(user.getCity());
			sb.append("\n");
		}

		// State
		if(!oldUser.getState().equals(user.getState())) {
			sb.append("State: ");
			sb.append(user.getState());
			sb.append("\n");
		}

		// Zip
		if(!oldUser.getZip().equals(user.getZip())) {
			sb.append("Zip: ");
			sb.append(user.getZip());
			sb.append("\n");
		}

		// Country
		if(!oldUser.getCountry().equals(user.getCountry())) {
			sb.append("Country: ");
			sb.append(user.getCountry());
			sb.append("\n");
		}

		// Home phone
		if(!oldUser.getHomePhone().equals(user.getHomePhone())) {
			sb.append("HomePhone: ");
			sb.append(user.getHomePhone());
			sb.append("\n");
		}

		// Work phone
		if(!oldUser.getWorkPhone().equals(user.getWorkPhone())) {
			sb.append("WorkPhone: ");
			sb.append(user.getWorkPhone());
			sb.append("\n");
		}

		// Mobile phone
		if(!oldUser.getMobilePhone().equals(user.getMobilePhone())) {
			sb.append("MobilePhone: ");
			sb.append(user.getMobilePhone());
			sb.append("\n");
		}

		// Pager phone
		if(!oldUser.getPagerPhone().equals(user.getPagerPhone())) {
			sb.append("PagerPhone: ");
			sb.append(user.getPagerPhone());
			sb.append("\n");
		}

		// Contact information
		if(!oldUser.getContactInfo().equals(user.getContactInfo())) {
			sb.append("ContactInfo: ");
			sb.append(user.getContactInfo());
			sb.append("\n");
		}

		// Comments
		if(!oldUser.getComments().equals(user.getComments())) {
			sb.append("Comments: ");
			sb.append(user.getComments());
			sb.append("\n");
		}

		// Signature
		if(!oldUser.getSignature().equals(user.getSignature())) {
			sb.append("Signature: ");
			sb.append(user.getSignature());
			sb.append("\n");
		}

		// Language
		if(!oldUser.getLang().equals(user.getLang())) {
			sb.append("Lang: ");
			sb.append(user.getLang());
			sb.append("\n");
		}

		// Privileged
		if(oldUser.isPrivileged() != user.isPrivileged()) {
			sb.append("Privileged: ");

			if(user.isPrivileged()) {
				sb.append("1");
			}
			else {
				sb.append("0");
			}

			sb.append("\n");
		}

		// Disabled
		if(oldUser.isDisabled()!= user.isDisabled()) {
			sb.append("Disabled: ");

			if(user.isDisabled()) {
				sb.append("1");
			}
			else {
				sb.append("0");
			}

			sb.append("\n");
		}

		// Custom fields
		if(user.getCustomFields() != null) {
			for(Map.Entry<String, String> e : user.getCustomFields().entrySet()) {
				sb.append("CF-");
				sb.append(e.getKey());
				sb.append(": ");
				sb.append(e.getValue());
				sb.append("\n");
			}
		}

		return sb.toString();
	}
}
