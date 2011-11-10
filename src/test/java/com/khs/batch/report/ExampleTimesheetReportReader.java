package com.khs.batch.report;

import java.util.ArrayList;
import java.util.List;

import com.khs.batch.report.Data;
import com.khs.batch.report.ReportReader;

import static com.khs.batch.report.ExampleTimesheetReportFactory.*;
/*
 * Produce report data rows...they must be sorted in report grouping order
 * in reality a database or some kind of data source will be read
 * 
 */
public class ExampleTimesheetReportReader extends ReportReader {

	@Override
	public List doQuery() {

		// 200 test records 
		int loops = 200;
		int count = 0;

		List<Object> rows = new ArrayList<Object>();

		while (count < loops) {

			count++;
			List<Data> cols = new ArrayList<Data>();

			Data d = new Data();
			d.setId(WEEKEND);
			d.setValue("06/30/20");

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
