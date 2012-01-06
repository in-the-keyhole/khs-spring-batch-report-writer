package com.khs.batch.report;

import static com.khs.batch.report.JobExecutionReportFactory.DATE;
import static com.khs.batch.report.JobExecutionReportFactory.EXECUTION_TIME;
import static com.khs.batch.report.JobExecutionReportFactory.JOB;
import static com.khs.batch.report.JobExecutionReportFactory.STEP;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

/*
 * Produce report for Spring Batch Job execution times.
 * Data is obtained by reading spring batch meta data schema
 * 
 */
@Configurable
@Component
public class JobExecutionReportReader extends ReportReader {

	@Autowired
	BasicDataSource dataSource;

	/*
	 * Results must be in report sort order
	 */
	@Override
	public List doQuery() {

		List<Object> rows = new ArrayList<Object>();

		// NOTE: List of List<DATA> must be in report sort order

		try {

			Connection conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			// Query batch job tables
			statement.execute("select * from batch_job_instance a,batch_job_execution b,batch_step_execution c where a.job_instance_id = b.job_instance_id and b.job_execution_id = c.job_execution_id order by a.job_name,b.start_time,c.step_name");

			ResultSet results = statement.getResultSet();

			while (!results.isLast()) {

				results.next();

				String name = results.getString("job_name");
				Timestamp startTime = results.getTimestamp("start_time");
				Timestamp endTime = results.getTimestamp("end_time");
				String step = results.getString("step_name");
				// endtime is null, then it should be this job, don't report
				if (endTime == null) {
					continue;
				}

				long time = endTime.getTime() - startTime.getTime();

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(startTime.getTime());

				List<Data> cols = new ArrayList<Data>();

				Data d = new Data();
				d.setId(JOB);
				d.setValue(name);

				cols.add(d);

				d = new Data();
				d.setId(STEP);
				d.setValue(step);

				cols.add(d);

				d = new Data();
				d.setId(DATE);
				d.setValue(cal.getTime());

				cols.add(d);

				d = new Data();
				d.setId(EXECUTION_TIME);
				d.setValue(time);
				cols.add(d);

				rows.add(cols);

			}

			conn.close();

		} catch (SQLException e) {

			throw new RuntimeException(e);

		}

		return rows;
	}

	@Override
	public List<Data> mapItem(Object o) {

		return (List<Data>) o;
	}

}
