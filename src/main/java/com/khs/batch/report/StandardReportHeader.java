/**
 * 
 */
package com.khs.batch.report;

import java.util.Calendar;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author dpitt@keyholesoftware.com
 */
public class StandardReportHeader
{

    public static String[] defaultHeader(String programName, String reportId, String headerLine1)
    {
        return defaultHeader(programName, reportId, headerLine1, "", "");
    }

    public static String[] defaultHeader(String programName, String reportId, String headerLine1, String headerLine2)
    {
        return defaultHeader(programName, reportId, headerLine1, headerLine2, "");
    }

    public static String[] defaultHeader(String programName, String reportId, String headerLine1, String headerLine2, String headerLine3)
    {
        Calendar cal = Calendar.getInstance();
        String date = DateFormatUtils.format(cal, ReportingDefaultConstants.DATE_FORMAT);
        String time = DateFormatUtils.format(cal, ReportingDefaultConstants.TIME_FORMAT);

        String[] header = new String[] {
                "DATE:" + date + "~" + headerLine1 + "~PAGE: #",
                "TIME:" + time + "~" + headerLine2 + "~RPT : " + reportId,
                "PRGM:" + programName + "~" + headerLine3 + "~"

        };
        return header;
    }

}
