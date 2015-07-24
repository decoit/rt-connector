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

import java.util.Date;
import java.util.Map;


/**
 * POJO data structure to represent a RT ticket history item.
 * It features fields for all attributes returned by the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtTicketHistoryItem {
	private long id;
	private long ticketId;
	private int timeTaken;
	private RtTicketHistoryItemType type;
	private String field;
	private String oldValue;
	private String newValue;
	private String data;
	private String description;
	private String content;
	private String creator;
	private Date created;
	private Map<Long, String> attachments;


	public long getId() {
		return id;
	}


	/**
	 * Set an ID for this history item.
	 *
	 * @param id Item ID, must be greater than 0
	 */
	public void setId(long id) {
		if(id > 0) {
			this.id = id;
		}
		else {
			throw new IllegalArgumentException("ID cannot be less or equal 0");
		}
	}


	public long getTicketId() {
		return ticketId;
	}


	/**
	 * Set a ticket ID for this history item.
	 *
	 * @param ticketId Ticket ID, must be greater than 0
	 */
	public void setTicketId(long ticketId) {
		if(ticketId > 0) {
			this.ticketId = ticketId;
		}
		else {
			throw new IllegalArgumentException("ID cannot be less or equal 0");
		}
	}


	public int getTimeTaken() {
		return timeTaken;
	}


	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}


	public RtTicketHistoryItemType getType() {
		return type;
	}


	public void setType(RtTicketHistoryItemType type) {
		this.type = type;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}


	public String getOldValue() {
		return oldValue;
	}


	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}


	public String getNewValue() {
		return newValue;
	}


	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}


	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getCreator() {
		return creator;
	}


	public void setCreator(String creator) {
		this.creator = creator;
	}


	public Date getCreated() {
		return created;
	}


	public void setCreated(Date created) {
		this.created = created;
	}


	public Map<Long, String> getAttachments() {
		return attachments;
	}


	public void setAttachments(Map<Long, String> attachments) {
		this.attachments = attachments;
	}


	/**
	 * A history item can have one of the types defined in this enum.
	 */
	public static enum RtTicketHistoryItemType {
		CREATE("Create"),
		CUSTOM_FIELD("CustumField"),
		EMAIL_RECORD("EmailRecord"),
		STATUS("Status"),
		COMMENT_EMAIL_RECORD("CommentEmailRecord"),
		CORRESPOND("Correspond"),
		COMMENT("Comment"),
		PRIORITY("Priority"),
		GIVE("Give"),
		STEAL("Steal"),
		TAKE("Take"),
		UNTAKE("Untake"),
		SET_WATCHER("SetWatcher"),
		ADD_WATCHER("AddWatcher"),
		DELETE_WATCHER("DeleteWatcher"),
		ADD_LINK("AddLink"),
		DELETE_LINK("DeleteLink"),
		ADD_REMINDER("AddReminder"),
		OPEN_REMINDER("OpenReminder"),
		RESOLVE_REMINDER("ResolveReminder"),
		SET("Set"),
		FORCE("Force"),
		SUBJECT("Subject"),
		TOLD("Told"),
		PURGE_TRANSACTION("PurgeTransaction"),
		SYSTEM_ERROR("SystemError");

		/**
		 * Camel-case representation of the status as used by the RT REST API.
		 * For example, 'PURGE_TRANSACTION' becomes 'PurgeTransaction'.
		 */
		private final String typeText;


		RtTicketHistoryItemType(String s) {
			typeText = s;
		}


		@Override
		public String toString() {
			return typeText;
		}


		/**
		 * Evaluate a type string and return the corresponding enum constant.
		 * Will raise an IllegalArgumentException if the type string cannot be parsed.
		 *
		 * @param s Type string
		 * @return The corresponding enum constant
		 */
		public static RtTicketHistoryItemType fromTypeText(String s) {
			switch (s) {
				case "Create":
					return RtTicketHistoryItemType.CREATE;
				case "CustomField":
					return RtTicketHistoryItemType.CUSTOM_FIELD;
				case "EmailRecord":
					return RtTicketHistoryItemType.EMAIL_RECORD;
				case "Status":
					return RtTicketHistoryItemType.STATUS;
				case "CommentEmailRecord":
					return RtTicketHistoryItemType.COMMENT_EMAIL_RECORD;
				case "Correspond":
					return RtTicketHistoryItemType.CORRESPOND;
				case "Comment":
					return RtTicketHistoryItemType.COMMENT;
				case "Priority":
					return RtTicketHistoryItemType.PRIORITY;
				case "Give":
					return RtTicketHistoryItemType.GIVE;
				case "Steal":
					return RtTicketHistoryItemType.STEAL;
				case "Take":
					return RtTicketHistoryItemType.TAKE;
				case "Untake":
					return RtTicketHistoryItemType.UNTAKE;
				case "SetWatcher":
					return RtTicketHistoryItemType.SET_WATCHER;
				case "AddWatcher":
					return RtTicketHistoryItemType.ADD_WATCHER;
				case "DeleteWatcher":
					return RtTicketHistoryItemType.DELETE_WATCHER;
				case "AddLink":
					return RtTicketHistoryItemType.ADD_LINK;
				case "DeleteLink":
					return RtTicketHistoryItemType.DELETE_LINK;
				case "AddReminder":
					return RtTicketHistoryItemType.ADD_REMINDER;
				case "OpenReminder":
					return RtTicketHistoryItemType.OPEN_REMINDER;
				case "ResolveReminder":
					return RtTicketHistoryItemType.RESOLVE_REMINDER;
				case "Set":
					return RtTicketHistoryItemType.SET;
				case "Force":
					return RtTicketHistoryItemType.FORCE;
				case "Subject":
					return RtTicketHistoryItemType.SUBJECT;
				case "Told":
					return RtTicketHistoryItemType.TOLD;
				case "PurgeTransaction":
					return RtTicketHistoryItemType.PURGE_TRANSACTION;
				default:
					throw new IllegalArgumentException("Unknown type text: " + s);
			}
		}
	}
}
