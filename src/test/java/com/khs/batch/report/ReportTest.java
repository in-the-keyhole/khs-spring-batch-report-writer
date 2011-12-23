package com.khs.batch.report;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
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
	private Job job;

	@Test
	public void testLaunchJob() throws Exception {
		jobLauncher.run(job, new JobParameters());
		System.out.println("PDF REPORT Generated in " + System.getProperty("java.io.tmpdir"));

	}

}
