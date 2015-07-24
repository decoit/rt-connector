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
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestResponse {
	private final RtRestStatus rtStatus;
	private final String sessionId;
	private final String bodyText;


	RtRestResponse(RtRestStatus status, String sessionId, String bodyText) {
		this.rtStatus = status;
		this.sessionId = sessionId;
		this.bodyText = bodyText;
	}


	public RtRestStatus getRtStatus() {
		return this.rtStatus;
	}


	public String getSessionId() {
		return this.sessionId;
	}


	public String getBodyText() {
		return this.bodyText;
	}
}
