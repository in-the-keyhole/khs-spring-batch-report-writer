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

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * @author dpitt@keyholesoftware.com
 */
public abstract class ReportReader<T> implements ItemReader<List<? extends Data>> {

	private StepExecution stepExecution;
	private boolean eof = false;
	private List<T> queryResults = null;

	public abstract List<T> doQuery();

	public List<Data> doRead() {
		if (queryResults == null) {
			queryResults = doQuery();
			if (queryResults.isEmpty()) {
				this.stepExecution.getExecutionContext().put(ReportingDefaultConstants.EMPTY_REPORT, true);
				return null;
			}
			this.stepExecution.getExecutionContext().put(ReportingDefaultConstants.EMPTY_REPORT, false);
		}
		while (!queryResults.isEmpty()) {
			List<Data> result = mapItem(queryResults.get(0));
			queryResults.remove(0);
			return result;
		}
		return null;
	}

	/**
	 * @return the jobParams
	 */
	public Object getJobParams() {
		return stepExecution.getJobExecution().getExecutionContext().get("jobParams");
	}

	/**
	 * @return the queryResults
	 */
	public List<T> getQueryResults() {
		return queryResults;
	}

	/**
	 * @return the stepExecution
	 */
	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public abstract List<Data> mapItem(T o);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemReader#read()
	 */
	public List<? extends Data> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (eof) {
			eof = false;
			queryResults = null;
			return null;
		}

		List<Data> results = doRead();
		if (results == null) {
			eof = true;
			return eof();
		}
		return results;
	}

	@BeforeStep
	public void retrieveInterstepData(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	/**
	 * @param queryResults
	 *            the queryResults to set
	 */
	public void setQueryResults(List<T> queryResults) {
		this.queryResults = queryResults;
	}

	/**
	 * @param stepExecution
	 *            the stepExecution to set
	 */
	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public static List<Data> eof() {
		List<Data> eof = new ArrayList<Data>();
		eof.add(new EOF());
		return eof;
	}

}
