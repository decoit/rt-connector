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

import java.util.HashMap;
import java.util.Map;


/**
 * POJO data structure to represent a RT queue.
 * It features fields for all attributes returned by the RT REST API.
 *
 * @author Thomas Rix (rix@decoit.de)
 */
public class RtQueue {
	private long id;
	private String name;
	private String description = "";
	private String correspondAddress = "";
	private String commentAddress = "";
	private int initialPriority = 0;
	private int finalPriority = 0;
	private int defaultDueIn = 0;
	private boolean disabled = false;
	private Map<String, String> customFields = new HashMap<>();


	public long getId() {
		return id;
	}


	/**
	 * Set an ID for this queue.
	 *
	 * @param id Queue ID, must be greater than 0
	 */
	public void setId(long id) {
		if(id > 0) {
			this.id = id;
		}
		else {
			throw new IllegalArgumentException("ID cannot be less or equal 0");
		}
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getCorrespondAddress() {
		return correspondAddress;
	}


	public void setCorrespondAddress(String correspondAddress) {
		this.correspondAddress = correspondAddress;
	}


	public String getCommentAddress() {
		return commentAddress;
	}


	public void setCommentAddress(String commentAddress) {
		this.commentAddress = commentAddress;
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


	public int getDefaultDueIn() {
		return defaultDueIn;
	}


	public void setDefaultDueIn(int defaultDueIn) {
		this.defaultDueIn = defaultDueIn;
	}


	public boolean isDisabled() {
		return disabled;
	}


	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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
}
