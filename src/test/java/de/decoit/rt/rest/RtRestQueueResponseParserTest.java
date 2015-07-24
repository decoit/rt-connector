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
import junit.framework.TestCase;


/**
 * This test file contains test cases for the RtRestQueueResponseParser class
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtRestQueueResponseParserTest extends TestCase {
	public RtRestQueueResponseParserTest(String testName) {
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
	 * Test the parseQueue method with the response for an enabled queue.
	 */
	public void testParseQueue() {
		System.out.println("Testing parseQueue() with enabled queue");

		// Test enabled queue with only valid data
		String response = "RT/4.2.3 200 Ok\n"
				+ "\n"
				+ "id: queue/1\n"
				+ "Name: General\n"
				+ "Description: The default queue\n"
				+ "CorrespondAddress: \n"
				+ "CommentAddress: \n"
				+ "InitialPriority: 0\n"
				+ "FinalPriority: 0\n"
				+ "DefaultDueIn: 10\n"
				+ "Disabled: 0\n"
				+ "CF.{NewStyle}: 1\n"
				+ "CF-OldStyle: 1";

		try {
			RtQueue queue = RtRestQueueResponseParser.parseQueue(response);

			assertEquals("Queue ID mismatch", 1, queue.getId());
			assertEquals("Name mismatch", "General", queue.getName());
			assertEquals("Description mismatch", "The default queue", queue.getDescription());
			assertEquals("CorrespondAddress mismatch", "", queue.getCorrespondAddress());
			assertEquals("CommentAddress mismatch", "", queue.getCommentAddress());
			assertEquals("InitialPriority mismatch", 0, queue.getInitialPriority());
			assertEquals("FinalPriority mismatch", 0, queue.getFinalPriority());
			assertEquals("DefaultDueIn mismatch", 10, queue.getDefaultDueIn());
			assertEquals("Disabled mismatch", false, queue.isDisabled());
			assertEquals("New style custom field mismatch", "1", queue.getCustomFields().get("NewStyle"));
			assertEquals("Old style custom field mismatch", "1", queue.getCustomFields().get("OldStyle"));
		}
		catch (RtException ex) {
			fail(ex.getMessage());
		}
	}


	/**
	 * Test the parseQueue method with the response for an disabled queue.
	 */
	public void testParseQueueDisabled() {
		System.out.println("Testing parseQueue() with disabled queue");

		// Test disabled queue with only valid data
		String response = "RT/4.2.3 200 Ok\n"
				+ "\n"
				+ "id: queue/1\n"
				+ "Name: General\n"
				+ "Description: The default queue\n"
				+ "CorrespondAddress: \n"
				+ "CommentAddress: \n"
				+ "InitialPriority: 0\n"
				+ "FinalPriority: 0\n"
				+ "DefaultDueIn: 10\n"
				+ "Disabled: 1";

		try {
			RtQueue queue = RtRestQueueResponseParser.parseQueue(response);

			assertEquals("Queue ID mismatch", 1, queue.getId());
			assertEquals("Name mismatch", "General", queue.getName());
			assertEquals("Description mismatch", "The default queue", queue.getDescription());
			assertEquals("CorrespondAddress mismatch", "", queue.getCorrespondAddress());
			assertEquals("CommentAddress mismatch", "", queue.getCommentAddress());
			assertEquals("InitialPriority mismatch", 0, queue.getInitialPriority());
			assertEquals("FinalPriority mismatch", 0, queue.getFinalPriority());
			assertEquals("DefaultDueIn mismatch", 10, queue.getDefaultDueIn());
			assertEquals("Disabled mismatch", true, queue.isDisabled());
		}
		catch (RtException ex) {
			fail(ex.getMessage());
		}
	}


	/**
	 * Test the parseQueue method with an invalid ID value inside the response.
	 */
	public void testParseQueueInvalidId() {
		System.out.println("Testing parseQueue() with invalid ID value");

		// Test queue with invalid queue ID string
		String response = "RT/4.2.3 200 Ok\n"
				+ "\n"
				+ "id: ticket/1\n"
				+ "Name: General\n"
				+ "Description: The default queue\n"
				+ "CorrespondAddress: \n"
				+ "CommentAddress: \n"
				+ "InitialPriority: 0\n"
				+ "FinalPriority: 0\n"
				+ "DefaultDueIn: 10\n"
				+ "Disabled: 0";

		try {
			RtQueue queue = RtRestQueueResponseParser.parseQueue(response);

			fail("Invalid queue ID string not recognized: " + queue.getId());
		}
		catch (RtException ex) {
			/*
			 * This should happen, test successful
			 */
		}
	}


	/**
	 * Test the parseQueue method with an invalid value for disabled inside the response.
	 */
	public void testParseQueueInvalidDisabled() {
		System.out.println("Testing parseQueue() with invalid disabled value");

		// Test queue with invalid queue ID string
		String response = "RT/4.2.3 200 Ok\n"
				+ "\n"
				+ "id: queue/1\n"
				+ "Name: General\n"
				+ "Description: The default queue\n"
				+ "CorrespondAddress: \n"
				+ "CommentAddress: \n"
				+ "InitialPriority: 0\n"
				+ "FinalPriority: 0\n"
				+ "DefaultDueIn: 10\n"
				+ "Disabled: 5";

		try {
			RtQueue queue = RtRestQueueResponseParser.parseQueue(response);

			fail("Invalid disabled value not recognized: " + queue.isDisabled());
		}
		catch (RtException ex) {
			/*
			 * This should happen, test successful
			 */
		}
	}


	/**
	 * Test the parseQueue method with the response for a list of 3 queues.
	 */
	public void testParseQueueList() {
		System.out.println("Testing parseQueueList()");

		String response = "RT/4.2.3 200 Ok\n"
				+ "\n"
				+ "1: General\n"
				+ "2: Internal\n"
				+ "3: SIEM";

		Map<Long, String> expResult = new HashMap<>();
		expResult.put(1L, "General");
		expResult.put(2L, "Internal");
		expResult.put(3L, "SIEM");

		Map<Long, String> result = RtRestQueueResponseParser.parseQueueList(response);

		assertEquals("Result maps do not match", expResult, result);
	}


	/**
	 * Test the parseQueue method with the response for an empty queue list.
	 */
	public void testParseQueueListEmptyResult() {
		System.out.println("Testing parseQueueList() with empty list");

		String response = "RT/4.2.3 200 Ok\n"
				+ "\n"
				+ "No matching results.";

		Map<Long, String> expResult = new HashMap<>();
		Map<Long, String> result = RtRestQueueResponseParser.parseQueueList(response);

		assertEquals("Result maps do not match", expResult, result);
	}


	public void testParseQueueCreated() {
		System.out.println("Testing parseQueueCreated() with success response");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# Queue 775 created.";

		try {
			assertEquals("Successful creation mismatch", 775, RtRestQueueResponseParser.parseQueueCreated(response));
		}
		catch(RtException ex) {
			fail(ex.getMessage());
		}
	}


	public void testParseQueueCreatedFailed() {
		System.out.println("Testing parseQueueCreated() with failure response");

		String response = "RT/4.2.3 200 Ok\n" +
			"\n" +
			"# Required: Name\n" +
			"\n" +
			"id: queue/new\n" +
			"Name: <queue name>\n" +
			"Description: \n" +
			"CorrespondAddress: \n" +
			"CommentAddress: \n" +
			"InitialPriority: \n" +
			"FinalPriority: \n" +
			"DefaultDueIn: \n" +
			"Disabled: ";

		try {
			RtRestQueueResponseParser.parseQueueCreated(response);
			fail("Parsing incorrect create queue response did not throw exception");
		}
		catch(RtException ex) {
			/* Ignore, this is what should happen here! */
		}
	}
}
