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

import java.util.List;

/**
 * @author dpitt@keyholesoftware.com
 */
public abstract class ReportFactory {

	protected Object jobParams;

	public abstract List<Column> getColumns();

	public abstract List<String> getFooter();

	public abstract List<String> getHeader();

	/**
	 * @return the jobParams
	 */
	public Object getJobParams() {
		return jobParams;
	}

	/**
	 * @param jobParams
	 *            the jobParams to set
	 */
	public void setJobParams(Object jobParams) {
		this.jobParams = jobParams;
	}

}
