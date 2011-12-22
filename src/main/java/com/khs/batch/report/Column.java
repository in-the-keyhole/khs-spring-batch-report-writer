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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @author dpitt@keyholesoftware.com
 */

public class Column {

    public enum Alignment {LEFT, CENTER, RIGHT};
    
	public static Column New(String id, String title) {
		return new Column(id, title);
	}

	private String id;
	private String title;
	private boolean total;
	private boolean group;
	private boolean number;
	private boolean date;

	private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormat;
    
	public Column() {
		super();
	}

	public Column(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public Column date() {
        date = true;
        dateFormat = new SimpleDateFormat(ReportingDefaultConstants.DATE_FORMAT);
        
        return this;
    }
    
    public Column date(String formatPattern) {
        date = true;
        dateFormat = new SimpleDateFormat(formatPattern);
        
        return this;
    }
    
	public String format(Object objToFormat) {
	    if (isNumber())
        {
            return decimalFormat.format(objToFormat);
        }
        
        if (isDate())
        {
            return dateFormat.format(objToFormat);
        }
        
        return objToFormat.toString();
	}
	
	public Column group() {
        this.group = true;
        
        return this;
    }

	public Column numeric() {
        this.number = true;
        decimalFormat = new DecimalFormat(ReportingDefaultConstants.DECIMAL_FORMAT);
        
        return this;
    }
	
	public Column numeric(String formatPattern) {
        this.number = true;
        decimalFormat = new DecimalFormat(formatPattern);
        
        return this;
    }
	
	public Column total() {
        this.total = true;
        
        return numeric();
    }

	public Column total(String formatPattern) {
        this.total = true;
        
        return numeric(formatPattern);
    }
	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDate()
    {
        return date;
    }
	
	public boolean isGroup() {
		return group;
	}

	public boolean isNumber() {
		return number;
	}

	public boolean isTotal() {
		return total;
	}

}
