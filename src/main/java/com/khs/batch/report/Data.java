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

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author dpitt@keyholesoftware.com
 */
public class Data {

	public static Data convertToData(String id, Date d) {
		return Data.convertToData(id, d, ReportingDefaultConstants.DATE_FORMAT);
	}

	public static Data convertToData(String id, Date d, String format) {
		String effDate = d != null ? DateFormatUtils.format(d, format) : " ";
		return new Data(id, effDate);
	}

	public static Data convertToData(String id, Object o) {
		return new Data(id, o);
	}

	public static Data convertToData(String id, String s, Integer length) {
		if (length != null) {
			s = StringUtils.substring(s, 0, length);
		}
		return new Data(id, s);
	}

	private String id;

	private Object value;

	public Data() {
		super();
	}

	public Data(String id, Object value) {
		super();
		setId(id);
		if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0) {
			setValue(new BigDecimal("0.00"));
		} else {
			setValue(value);
		}

	}

	public String getId() {
		return id;
	}

	public Object getValue() {
		return value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
