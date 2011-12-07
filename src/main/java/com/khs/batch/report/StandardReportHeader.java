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

import java.util.Calendar;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author dpitt@keyholesoftware.com
 */
public class StandardReportHeader {

	public static String[] defaultHeader(String programName, String reportId, String headerLine1) {
		return defaultHeader(programName, reportId, headerLine1, "", "");
	}

	public static String[] defaultHeader(String programName, String reportId, String headerLine1, String headerLine2) {
		return defaultHeader(programName, reportId, headerLine1, headerLine2, "");
	}

	public static String[] defaultHeader(String programName, String reportId, String headerLine1, String headerLine2, String headerLine3) {
		Calendar cal = Calendar.getInstance();
		String date = DateFormatUtils.format(cal, ReportingDefaultConstants.DATE_FORMAT);
		String time = DateFormatUtils.format(cal, ReportingDefaultConstants.TIME_FORMAT);

		String[] header = new String[] { "DATE:" + date + "~" + headerLine1 + "~PAGE: #", "TIME:" + time + "~" + headerLine2 + "~RPT : " + reportId, "PRGM:" + programName + "~" + headerLine3 + "~"

		};
		return header;
	}

}
