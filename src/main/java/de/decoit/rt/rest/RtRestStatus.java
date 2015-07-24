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


/**
 * Enum to represent the status returned by the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public enum RtRestStatus {
	STATUS_200,		// OK
	STATUS_400,		// Bad request
	STATUS_401,		// Credentials required
	STATUS_409;		// Syntax Error


	/**
	 * Get the status enum constant that represents the provided status number
	 *
	 * @param number Status number string, extracted from the response
	 *
	 * @return Corresponding status enum constant
	 */
	static RtRestStatus getFromStatusNumber(String number) {
		switch (number) {
			case "200":
				return STATUS_200;
			case "400":
				return STATUS_400;
			case "401":
				return STATUS_401;
			case "409":
				return STATUS_409;
			default:
				return null;
		}
	}
}
