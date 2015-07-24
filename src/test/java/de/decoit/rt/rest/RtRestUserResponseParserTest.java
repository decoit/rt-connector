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
import static junit.framework.Assert.fail;
import junit.framework.TestCase;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestUserResponseParserTest extends TestCase {
	public RtRestUserResponseParserTest(String testName) {
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


	public void testParseUser() {
		System.out.println("Testing parseUser() with valid data");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: user/100\n" +
			"Password: ********\n" +
			"Name: Testbenutzer\n" +
			"EmailAddress: test@decoit.de\n" +
			"RealName: Test User\n" +
			"NickName: Testy\n" +
			"Gecos: testy\n" +
			"Organization: DECOIT GmbH\n" +
			"Address1: Fahrenheitstraße 9\n" +
			"Address2: Blubb?\n" +
			"City: Bremen\n" +
			"State: HB\n" +
			"Zip: 12345\n" +
			"Country: Deutschland\n" +
			"HomePhone: 123\n" +
			"WorkPhone: 456\n" +
			"MobilePhone: 789\n" +
			"PagerPhone: 0123\n" +
			"\n" +
			"ContactInfo: Dies\n" +
			"             ist\n" +
			"             eine\n" +
			"             Zusatzinfo\n" +
			"\n" +
			"Comments: Dies ist ein\n" +
			"          Kommentar\n" +
			"\n" +
			"Signature: Dies ist eine\n" +
			"           \n" +
			"           Signatur\n" +
			"\n" +
			"Lang: de\n" +
			"Privileged: 1\n" +
			"Disabled: 0\n" +
			"CF.{NewStyle}: 1\n" +
			"CF-OldStyle: 1";

		try {
			RtUser result = RtRestUserResponseParser.parseUser(response);

			assertEquals("User ID mismatch", 100, result.getId());
			assertEquals("User name mismatch", "Testbenutzer", result.getName());
			assertEquals("User email mismatch", "test@decoit.de", result.getEmailAddress());
			assertEquals("User real name mismatch", "Test User", result.getRealName());
			assertEquals("User nick name mismatch", "Testy", result.getNickName());
			assertEquals("User gecos mismatch", "testy", result.getGecos());
			assertEquals("User organization mismatch", "DECOIT GmbH", result.getOrganization());
			assertEquals("User address 1 mismatch", "Fahrenheitstraße 9", result.getAddress1());
			assertEquals("User address 2 mismatch", "Blubb?", result.getAddress2());
			assertEquals("User city mismatch", "Bremen", result.getCity());
			assertEquals("User state mismatch", "HB", result.getState());
			assertEquals("User zip mismatch", "12345", result.getZip());
			assertEquals("User country mismatch", "Deutschland", result.getCountry());
			assertEquals("User home phone mismatch", "123", result.getHomePhone());
			assertEquals("User work phone mismatch", "456", result.getWorkPhone());
			assertEquals("User mobile phone mismatch", "789", result.getMobilePhone());
			assertEquals("User pager phone mismatch", "0123", result.getPagerPhone());
			assertEquals("User contact info mismatch", "Dies\nist\neine\nZusatzinfo", result.getContactInfo());
			assertEquals("User comments mismatch", "Dies ist ein\nKommentar", result.getComments());
			assertEquals("User signature mismatch", "Dies ist eine\n\nSignatur", result.getSignature());
			assertEquals("User lang mismatch", "de", result.getLang());
			assertEquals("User privileged mismatch", true, result.isPrivileged());
			assertEquals("User disabled mismatch", false, result.isDisabled());
			assertEquals("New style custom field mismatch", "1", result.getCustomFields().get("NewStyle"));
			assertEquals("Old style custom field mismatch", "1", result.getCustomFields().get("OldStyle"));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	public void testParseUserInvalidId() {
		System.out.println("Testing parseUser() with invalid value for id");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: ticket/100\n" +
			"Password: ********\n" +
			"Name: Testbenutzer\n" +
			"EmailAddress: test@decoit.de\n" +
			"RealName: Test User\n" +
			"NickName: Testy\n" +
			"Gecos: testy\n" +
			"Organization: DECOIT GmbH\n" +
			"Address1: Fahrenheitstraße 9\n" +
			"Address2: Blubb?\n" +
			"City: Bremen\n" +
			"State: HB\n" +
			"Zip: 12345\n" +
			"Country: Deutschland\n" +
			"HomePhone: 123\n" +
			"WorkPhone: 456\n" +
			"MobilePhone: 789\n" +
			"PagerPhone: 0123\n" +
			"\n" +
			"ContactInfo: Dies\n" +
			"             ist\n" +
			"             eine\n" +
			"             Zusatzinfo\n" +
			"\n" +
			"Comments: Dies ist ein\n" +
			"          Kommentar\n" +
			"\n" +
			"Signature: Dies ist eine\n" +
			"           Signatur\n" +
			"\n" +
			"Lang: de\n" +
			"Privileged: 1\n" +
			"Disabled: 0";

		try {
			RtUser result = RtRestUserResponseParser.parseUser(response);

			fail("Invalid ID value not detected: " + result.getId());
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}


	public void testParseUserInvalidPrivileged() {
		System.out.println("Testing parseUser() with invalid value for privileged");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: user/100\n" +
			"Password: ********\n" +
			"Name: Testbenutzer\n" +
			"EmailAddress: test@decoit.de\n" +
			"RealName: Test User\n" +
			"NickName: Testy\n" +
			"Gecos: testy\n" +
			"Organization: DECOIT GmbH\n" +
			"Address1: Fahrenheitstraße 9\n" +
			"Address2: Blubb?\n" +
			"City: Bremen\n" +
			"State: HB\n" +
			"Zip: 12345\n" +
			"Country: Deutschland\n" +
			"HomePhone: 123\n" +
			"WorkPhone: 456\n" +
			"MobilePhone: 789\n" +
			"PagerPhone: 0123\n" +
			"\n" +
			"ContactInfo: Dies\n" +
			"             ist\n" +
			"             eine\n" +
			"             Zusatzinfo\n" +
			"\n" +
			"Comments: Dies ist ein\n" +
			"          Kommentar\n" +
			"\n" +
			"Signature: Dies ist eine\n" +
			"           Signatur\n" +
			"\n" +
			"Lang: de\n" +
			"Privileged: 5\n" +
			"Disabled: 0";

		try {
			RtUser result = RtRestUserResponseParser.parseUser(response);

			fail("Invalid privileged value not detected: " + result.isPrivileged());
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}


	public void testParseUserInvalidDisabled() {
		System.out.println("Testing parseUser() with invalid value for disabled");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"id: user/100\n" +
			"Password: ********\n" +
			"Name: Testbenutzer\n" +
			"EmailAddress: test@decoit.de\n" +
			"RealName: Test User\n" +
			"NickName: Testy\n" +
			"Gecos: testy\n" +
			"Organization: DECOIT GmbH\n" +
			"Address1: Fahrenheitstraße 9\n" +
			"Address2: Blubb?\n" +
			"City: Bremen\n" +
			"State: HB\n" +
			"Zip: 12345\n" +
			"Country: Deutschland\n" +
			"HomePhone: 123\n" +
			"WorkPhone: 456\n" +
			"MobilePhone: 789\n" +
			"PagerPhone: 0123\n" +
			"\n" +
			"ContactInfo: Dies\n" +
			"             ist\n" +
			"             eine\n" +
			"             Zusatzinfo\n" +
			"\n" +
			"Comments: Dies ist ein\n" +
			"          Kommentar\n" +
			"\n" +
			"Signature: Dies ist eine\n" +
			"           Signatur\n" +
			"\n" +
			"Lang: de\n" +
			"Privileged: 1\n" +
			"Disabled: -5";

		try {
			RtUser result = RtRestUserResponseParser.parseUser(response);

			fail("Invalid disabled value not detected: " + result.isDisabled());
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}
}
