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

import junit.framework.TestCase;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestResponseParserTest extends TestCase {

	public RtRestResponseParserTest(String testName) {
		super(testName);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}


	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	/**
	 * Test of parseStatus method, of class RtRestResponseParser.
	 */
	public void testParseStatus() {
		System.out.println("Testing parseStatus()");
		String response1 = "RT/4.2.3 200 Ok";
		String response2 = "RT/4.2.3 401 Credentials required";

		RtRestStatus expResult1 = RtRestStatus.STATUS_200;
		RtRestStatus expResult2 = RtRestStatus.STATUS_401;

		RtRestStatus result1 = RtRestResponseParser.parseStatus(response1);
		assertEquals("Status 200 not recognized", expResult1, result1);

		RtRestStatus result2 = RtRestResponseParser.parseStatus(response2);
		assertEquals("Status 401 not recognized", expResult2, result2);
	}
}
