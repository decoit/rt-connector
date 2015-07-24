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
import java.util.List;
import junit.framework.TestCase;


/**
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestTicketHistoryResponseParserTest extends TestCase {

	public RtRestTicketHistoryResponseParserTest(String testName) {
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
	 * Test of parseHistoryItem method, of class RtRestTicketHistoryResponseParser.
	 */
	public void testParseHistoryItem() {
		System.out.println("Testing parseHistoryItem()");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# 11/11 (id/210/total)\n" +
			"\n" +
			"id: 210\n" +
			"Ticket: 18\n" +
			"TimeTaken: 0\n" +
			"Type: Create\n" +
			"Field: \n" +
			"OldValue: \n" +
			"NewValue: \n" +
			"Data: \n" +
			"Description: Ticket created by root\n" +
			"\n" +
			"Content: Dies ist\n" +
			"         ein multiline\n" +
			"         Text!\n" +
			"\n" +
			"Creator: root\n" +
			"Created: 2014-03-13 11:11:07\n" +
			"\n" +
			"Attachments: \n" +
			"             28: untitled (28b)";

		try {
			RtTicketHistoryItem result = RtRestTicketHistoryResponseParser.parseHistoryItem(response);

			assertEquals("ID mismatch", 210, result.getId());
			assertEquals("Ticket mismatch", 18, result.getTicketId());
			assertEquals("Time taken mismatch", 0, result.getTimeTaken());
			assertEquals("Type mismatch", RtTicketHistoryItemType.CREATE, result.getType());
			assertEquals("Field mismatch", "", result.getField());
			assertEquals("Old value mismatch", "", result.getOldValue());
			assertEquals("New value mismatch", "", result.getNewValue());
			assertEquals("Data mismatch", "", result.getData());
			assertEquals("Description mismatch", "Ticket created by root", result.getDescription());
			assertEquals("Content mismatch", "Dies ist\nein multiline\nText!", result.getContent());
			assertEquals("Creator mismatch", "root", result.getCreator());
			assertEquals("Created mismatch", "2014-03-13 11:11:07", RtRestTicketHistoryResponseParser.formatDateString(result.getCreated()));
			assertEquals("Attachments size mismatch", 1, result.getAttachments().size());
			assertTrue("Attachment ID mismatch", result.getAttachments().containsKey(28L));
			assertTrue("Attachment content mismatch", result.getAttachments().get(28L).equals("untitled"));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	public void testParseHistoryItemWithoutAttachments() {
		System.out.println("Testing parseHistoryItem() without attachments");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# 11/11 (id/210/total)\n" +
			"\n" +
			"id: 210\n" +
			"Ticket: 18\n" +
			"TimeTaken: 0\n" +
			"Type: Create\n" +
			"Field: \n" +
			"OldValue: \n" +
			"NewValue: \n" +
			"Data: \n" +
			"Description: Ticket created by root\n" +
			"\n" +
			"Content: Dies ist\n" +
			"         ein multiline\n" +
			"         Text!\n" +
			"\n" +
			"Creator: root\n" +
			"Created: 2014-03-13 11:11:07\n" +
			"\n" +
			"Attachments: \n";

		try {
			RtTicketHistoryItem result = RtRestTicketHistoryResponseParser.parseHistoryItem(response);

			assertEquals("ID mismatch", 210, result.getId());
			assertEquals("Ticket mismatch", 18, result.getTicketId());
			assertEquals("Time taken mismatch", 0, result.getTimeTaken());
			assertEquals("Type mismatch", RtTicketHistoryItemType.CREATE, result.getType());
			assertEquals("Field mismatch", "", result.getField());
			assertEquals("Old value mismatch", "", result.getOldValue());
			assertEquals("New value mismatch", "", result.getNewValue());
			assertEquals("Data mismatch", "", result.getData());
			assertEquals("Description mismatch", "Ticket created by root", result.getDescription());
			assertEquals("Content mismatch", "Dies ist\nein multiline\nText!", result.getContent());
			assertEquals("Creator mismatch", "root", result.getCreator());
			assertEquals("Created mismatch", "2014-03-13 11:11:07", RtRestTicketHistoryResponseParser.formatDateString(result.getCreated()));
			assertEquals("Attachments size mismatch", 0, result.getAttachments().size());
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	public void testParseHistoryItemWithMultipleAttachments() {
		System.out.println("Testing parseHistoryItem() with multiple attachments");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# 11/11 (id/210/total)\n" +
			"\n" +
			"id: 210\n" +
			"Ticket: 18\n" +
			"TimeTaken: 0\n" +
			"Type: Create\n" +
			"Field: \n" +
			"OldValue: \n" +
			"NewValue: \n" +
			"Data: \n" +
			"Description: Ticket created by root\n" +
			"\n" +
			"Content: Dies ist\n" +
			"         ein multiline\n" +
			"         Text!\n" +
			"\n" +
			"Creator: root\n" +
			"Created: 2014-03-13 11:11:07\n" +
			"\n" +
			"Attachments: \n" +
			"             41: untitled (0b)\n" +
			"             42: untitled (209b)\n" +
			"             43: plaindin.bst (50.8k)";

		try {
			RtTicketHistoryItem result = RtRestTicketHistoryResponseParser.parseHistoryItem(response);

			assertEquals("ID mismatch", 210, result.getId());
			assertEquals("Ticket mismatch", 18, result.getTicketId());
			assertEquals("Time taken mismatch", 0, result.getTimeTaken());
			assertEquals("Type mismatch", RtTicketHistoryItemType.CREATE, result.getType());
			assertEquals("Field mismatch", "", result.getField());
			assertEquals("Old value mismatch", "", result.getOldValue());
			assertEquals("New value mismatch", "", result.getNewValue());
			assertEquals("Data mismatch", "", result.getData());
			assertEquals("Description mismatch", "Ticket created by root", result.getDescription());
			assertEquals("Content mismatch", "Dies ist\nein multiline\nText!", result.getContent());
			assertEquals("Creator mismatch", "root", result.getCreator());
			assertEquals("Created mismatch", "2014-03-13 11:11:07", RtRestTicketHistoryResponseParser.formatDateString(result.getCreated()));
			assertEquals("Attachments size mismatch", 2, result.getAttachments().size());
			assertFalse("Attachment ID 41 present", result.getAttachments().containsKey(41L));
			assertTrue("Attachment ID 42 not present", result.getAttachments().containsKey(42L));
			assertTrue("Attachment ID 43 not present", result.getAttachments().containsKey(43L));
			assertEquals("Attachment 42 content mismatch", "untitled", result.getAttachments().get(42L));
			assertEquals("Attachment 43 content mismatch", "plaindin.bst", result.getAttachments().get(43L));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	/**
	 * Test of parseHistoryItems method, of class RtRestTicketHistoryResponseParser.
	 */
	public void testParseHistoryItems() {
		System.out.println("Testing parseHistoryItems()");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# 11/11 (id/210/total)\n" +
			"\n" +
			"id: 210\n" +
			"Ticket: 18\n" +
			"TimeTaken: 0\n" +
			"Type: Create\n" +
			"Field: \n" +
			"OldValue: \n" +
			"NewValue: \n" +
			"Data: \n" +
			"Description: Ticket created by root\n" +
			"\n" +
			"Content: Dies ist\n" +
			"         ein multiline\n" +
			"         Text!\n" +
			"\n" +
			"Creator: root\n" +
			"Created: 2014-03-13 11:11:07\n" +
			"\n" +
			"Attachments: \n" +
			"             28: untitled (28b)\n" +
			"\n" +
			"\n" +
			"--\n" +
			"\n" +
			"# 11/11 (id/211/total)\n" +
			"\n" +
			"id: 211\n" +
			"Ticket: 18\n" +
			"TimeTaken: 0\n" +
			"Type: AddWatcher\n" +
			"Field: Requestor\n" +
			"OldValue: \n" +
			"NewValue: 12\n" +
			"Data: \n" +
			"Description: Requestor root (Enoch Root) added by root\n" +
			"Content: This transaction appears to have no content\n" +
			"Creator: root\n" +
			"Created: 2014-03-13 11:11:07\n" +
			"Attachments: \n" +
			"\n" +
			"--" +
			"\n" +
			"# 11/11 (id/231/total)\n" +
			"\n" +
			"id: 231\n" +
			"Ticket: 18\n" +
			"TimeTaken: 0\n" +
			"Type: Comment\n" +
			"Field: \n" +
			"OldValue: \n" +
			"NewValue: \n" +
			"Data: No Subject\n" +
			"Description: Comments added by root\n" +
			"\n" +
			"Content: Am Do 13. Mär 12:11:07 2014, root schrieb:\n" +
			"         \n" +
			"           Dies ist\n" +
			"           ein multiline\n" +
			"           Text!\n" +
			"         \n" +
			"         Attachment Test!\n" +
			"         \n" +
			"\n" +
			"\n" +
			"Creator: root\n" +
			"Created: 2014-03-13 12:46:50\n" +
			"\n" +
			"Attachments: \n" +
			"             41: untitled (0b)\n" +
			"             42: untitled (209b)\n" +
			"             43: plaindin.bst (50.8k)";

		try {
			List<RtTicketHistoryItem> result = RtRestTicketHistoryResponseParser.parseHistoryItems(response);

			assertEquals("List size mismatch", 3, result.size());
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}
}
