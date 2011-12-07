/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.khs.batch.report;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dpitt@keyholesoftware.com
 */
public class ControlBreak {

	private String id;
	private Object value;
	private Object previousValue;
	private List<SubTotal> subTotals = new ArrayList<SubTotal>();
	private boolean total;

	public void clearSubTotals() {
		for (SubTotal t : subTotals) {
			t.clear();
		}
		return;
	}

	public SubTotal findSubTotal(String id) {
		SubTotal total = null;
		for (SubTotal t : subTotals) {
			if (t.getId().equals(id)) {
				return t;
			}
		}
		total = new SubTotal();
		total.setId(id);
		subTotals.add(total);
		return total;
	}

	public String getId() {
		return id;
	}

	public Object getPreviousValue() {
		return previousValue;
	}

	/**
	 * @return the subTotals
	 */
	public List<SubTotal> getSubTotals() {
		return subTotals;
	}

	public Object getValue() {
		return value;
	}

	public boolean isTotal() {
		return total;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPreviousValue(Object previousValue) {
		this.previousValue = previousValue;
	}

	/**
	 * @param subTotals
	 *            the subTotals to set
	 */
	public void setSubTotals(List<SubTotal> subTotals) {
		this.subTotals = subTotals;
	}

	public void setTotal(boolean total) {
		this.total = total;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
