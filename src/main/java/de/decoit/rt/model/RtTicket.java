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
package de.decoit.rt.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * POJO data structure to represent a RT ticket.
 * It features fields for all attributes returned by the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtTicket {
	private long id;
	private String queue;
	private String owner = "";
	private String creator = "";
	private String subject = "";
	private RtTicketStatus status;
	private int priority = 0;
	private int initialPriority = 0;
	private int finalPriority = 0;
	private List<String> requestors = new ArrayList<>();
	private List<String> cc = new ArrayList<>();
	private List<String> adminCc = new ArrayList<>();
	private Date created;
	private Date starts;
	private Date started;
	private Date due;
	private Date resolved;
	private Date told;
	private Date lastUpdated;
	private int timeEstimated = 0;
	private int timeWorked = 0;
	private int timeLeft = 0;
	private String text;
	private Map<String, String> customFields = new HashMap<>();


	public long getId() {
		return id;
	}


	/**
	 * Set an ID for this ticket.
	 *
	 * @param id Ticket ID, must be greater than 0
	 */
	public void setId(long id) {
		if(id > 0) {
			this.id = id;
		}
		else {
			throw new IllegalArgumentException("ID cannot be less or equal 0");
		}
	}


	public String getQueue() {
		return queue;
	}


	public void setQueue(String queue) {
		this.queue = queue;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getCreator() {
		return creator;
	}


	public void setCreator(String creator) {
		this.creator = creator;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public RtTicketStatus getStatus() {
		return status;
	}


	public void setStatus(RtTicketStatus status) {
		this.status = status;
	}


	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	public int getInitialPriority() {
		return initialPriority;
	}


	public void setInitialPriority(int initialPriority) {
		this.initialPriority = initialPriority;
	}


	public int getFinalPriority() {
		return finalPriority;
	}


	public void setFinalPriority(int finalPriority) {
		this.finalPriority = finalPriority;
	}


	public List<String> getRequestors() {
		return requestors;
	}


	public void setRequestors(List<String> requestors) {
		this.requestors = requestors;
	}


	public List<String> getCc() {
		return cc;
	}


	public void setCc(List<String> cc) {
		this.cc = cc;
	}


	public List<String> getAdminCc() {
		return adminCc;
	}


	public void setAdminCc(List<String> adminCc) {
		this.adminCc = adminCc;
	}


	public Date getCreated() {
		return created;
	}


	public void setCreated(Date created) {
		this.created = created;
	}


	public Date getStarts() {
		return starts;
	}


	public void setStarts(Date starts) {
		this.starts = starts;
	}


	public Date getStarted() {
		return started;
	}


	public void setStarted(Date started) {
		this.started = started;
	}


	public Date getDue() {
		return due;
	}


	public void setDue(Date due) {
		this.due = due;
	}


	public Date getResolved() {
		return resolved;
	}


	public void setResolved(Date resolved) {
		this.resolved = resolved;
	}


	public Date getTold() {
		return told;
	}


	public void setTold(Date told) {
		this.told = told;
	}


	public Date getLastUpdated() {
		return lastUpdated;
	}


	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}


	public int getTimeEstimated() {
		return timeEstimated;
	}


	public void setTimeEstimated(int timeEstimated) {
		this.timeEstimated = timeEstimated;
	}


	public int getTimeWorked() {
		return timeWorked;
	}


	public void setTimeWorked(int timeWorked) {
		this.timeWorked = timeWorked;
	}


	public int getTimeLeft() {
		return timeLeft;
	}


	public void setTimeLeft(int timeLeft) {
		this.timeLeft = timeLeft;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public Map<String, String> getCustomFields() {
		return customFields;
	}


	public void setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
	}


	public void addCustomField(String name, String value) {
		this.customFields.put(name, value);
	}


	/**
	 * A ticket can have one of the status defined in this enum.
	 * Custom status defined in RT by the administrator are currently not supported.
	 */
	public static enum RtTicketStatus {
		NEW("new"),
		OPEN("open"),
		STALLED("stalled"),
		REJECTED("rejected"),
		RESOLVED("resolved"),
		DELETED("deleted");


		/**
		 * Lower-case letter representation of the status.
		 * For example, 'NEW' becomes 'new'.
		 */
		private final String statusText;


		RtTicketStatus(String s) {
			statusText = s;
		}


		@Override
		public String toString() {
			return statusText;
		}


		/**
		 * Evaluate a status string and return the corresponding enum constant.
		 * Will raise an IllegalArgumentException if the status string cannot be parsed.
		 *
		 * @param s Status string
		 * @return The corresponding enum constant
		 */
		public static RtTicketStatus fromStatusText(String s) {
			switch (s) {
				case "new":
					return RtTicketStatus.NEW;
				case "open":
					return RtTicketStatus.OPEN;
				case "stalled":
					return RtTicketStatus.STALLED;
				case "rejected":
					return RtTicketStatus.REJECTED;
				case "resolved":
					return RtTicketStatus.RESOLVED;
				case "deleted":
					return RtTicketStatus.DELETED;
				default:
					throw new IllegalArgumentException("Unknown status text: " + s);
			}
		}
	}
}
