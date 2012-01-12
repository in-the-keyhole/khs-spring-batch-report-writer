package com.khs.batch.report;

import java.util.Arrays;
import java.util.List;

public class JobExecutionReportFactory extends ReportFactory {

	// column ids
	public final static String JOB = "Job";
	public final static String DATE = "Date";
	public final static String STEP = "Step";
	public final static String EXECUTION_TIME = "Execution Time (ms)";

	@Override
	// Report Header List
	// Each element in list is a header line
	// ~ is used to justify header text LEFT,CENTER,RIGHT
	public List<String> getHeader() {
		return Arrays.asList("Example Report", "~Job Execution Times~Page #");
	}

	@Override
	// Report Columns Column definitions options...
	// Column.New(<col id>,<col value>).group()
	// Column.New(<col id>,<col value>).total()
	public List<Column> getColumns() {
		return Arrays.asList(Column.New(JOB, JOB), Column.New(STEP, "STEP").group(), Column.New(DATE, DATE).date(), Column.New(EXECUTION_TIME, EXECUTION_TIME).total());
	}

	@Override
	// Report Footer List
	// Each element in list is a header line
	// ~ is used to justify header text LEFT,CENTER,RIGHT
	public List<String> getFooter() {
		return Arrays.asList("~Footer for ");
	}

}
