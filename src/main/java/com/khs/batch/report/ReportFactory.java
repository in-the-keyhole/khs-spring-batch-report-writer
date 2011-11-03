package com.khs.batch.report;

import java.util.List;

/**
 * @author dpitt@keyholesoftware.com
 */
public abstract class ReportFactory
{

    protected Object jobParams;

    public abstract List<Column> getColumns();

    public abstract String[] getFooter();

    public abstract String[] getHeader();

    /**
     * @return the jobParams
     */
    public Object getJobParams()
    {
        return jobParams;
    }

    /**
     * @param jobParams
     *            the jobParams to set
     */
    public void setJobParams(Object jobParams)
    {
        this.jobParams = jobParams;
    }

}
