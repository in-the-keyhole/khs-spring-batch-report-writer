package com.khs.batch.report;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "/launch-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ReportTest {

	// Refer to ExampleTimeSheetReport Factory
	// to see how a Report is Defined

	// The ExampleTimesheetReportReader defines
	// Data for the report

	// refer to jobs-context.xml for Batch Job Configuration

	// PDF is generated to output path defined
	// in the batch.properties file

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("timesheetJob")
	private Job timesheetJob;

	@Autowired
	@Qualifier("jobTimingJob")
	private Job jobTimingJob;

	@Autowired
	BasicDataSource dataSource;

	@Test
	public void testTimesheetReportJob() throws Exception {
		jobLauncher.run(timesheetJob, new JobParameters());
		// PDF generated to resource file path specified in jobs-context.xml
		System.out.println("PDF REPORT Generated in " + System.getProperty("java.io.tmpdir"));
		System.out.println("See src/test/resources/jobs-context.xml to change PDF output directory");

	}

	@Test
	public void jobExecutionTimeReport() throws Exception {

		jobLauncher.run(jobTimingJob, new JobParameters());
		// PDF generated to resource file path specified in jobs-context.xml
		System.out.println("PDF REPORT Generated in " + System.getProperty("java.io.tmpdir"));
		System.out.println("See src/test/resources/jobs-context.xml to change PDF output directory");

	}

}
