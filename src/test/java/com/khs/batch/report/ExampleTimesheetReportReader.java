package com.khs.batch.report;

import static com.khs.batch.report.ExampleTimesheetReportFactory.DEPARTMENT;
import static com.khs.batch.report.ExampleTimesheetReportFactory.EMPLOYEE;
import static com.khs.batch.report.ExampleTimesheetReportFactory.HOURS;
import static com.khs.batch.report.ExampleTimesheetReportFactory.WEEKEND;

import java.util.ArrayList;
import java.util.List;

/*
 * Produce report data rows...they must be sorted in report grouping order
 * in reality a database or some kind of data source will be read
 * 
 */
public class ExampleTimesheetReportReader extends ReportReader {

	/*
	 * Results must be in report sort order
	 */
	@Override
	public List doQuery() {

		// 200 test records
		int loops = 200;
		int count = 0;

		List<Object> rows = new ArrayList<Object>();

		// NOTE: List of List<DATA> must be in report sort order

		while (count < loops) {

			count++;
			List<Data> cols = new ArrayList<Data>();

			Data d = new Data();
			d.setId(WEEKEND);
			d.setValue("06/30/2011");

			cols.add(d);

			d = new Data();
			d.setId(DEPARTMENT);
			d.setValue(count > 25 ? "Information Technology" : "Accounting");

			cols.add(d);

			d = new Data();
			d.setId(EMPLOYEE);
			d.setValue(count % 2 == 0 ? "Doe,Jane" : "Squidlow,Clifford");

			cols.add(d);

			d = new Data();
			d.setId(HOURS);
			d.setValue(count);

			cols.add(d);
			rows.add(cols);

		}
		return rows;
	}

	@Override
	public List<Data> mapItem(Object o) {

		return (List<Data>) o;
	}

}
