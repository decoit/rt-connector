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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;


/**
 * This class acts as super class for all other response parser classes.
 * It provides a method to check the RT status of a REST API response.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
class RtRestResponseParser {
	private static final Logger LOG = Logger.getLogger(RtRestResponseParser.class);
	protected static final Pattern STATUS_PATTERN = Pattern.compile("^RT/\\d+(\\.\\d+){1,2} (\\d{3}) (.+)$");
	protected static final String LIST_DELIMITER = "--\n";


	/**
	 * Protected default constructor, this is a static only class
	 */
	protected RtRestResponseParser() {
	}


	/**
	 * Parse a RT REST response and extract the status from it.
	 *
	 * @param response The response string from RT
	 *
	 * @return Status enum constant, null if no status could be parsed from the response
	 */
	static RtRestStatus parseStatus(String response) {
		// Split lines in response string
		String[] responseParts = response.split("\n");

		// First line in response is status string
		Matcher m = STATUS_PATTERN.matcher(responseParts[0]);

		if (m.matches()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Parsed status string: " + m.group(2) + " " + m.group(3));
			}

			return RtRestStatus.getFromStatusNumber(m.group(2));
		}
		else {
			return null;
		}
	}
}
