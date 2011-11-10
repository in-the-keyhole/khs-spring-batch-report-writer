package com.khs.batch.report;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author dpitt@keyholesoftware.com
 */
public class Data
{

    public static Data convertToData(String id, Date d)
    {
        return Data.convertToData(id, d, ReportingDefaultConstants.DATE_FORMAT);
    }

    public static Data convertToData(String id, Date d, String format)
    {
        String effDate = d != null ? DateFormatUtils.format(d, format) : " ";
        return new Data(id, effDate);
    }

    public static Data convertToData(String id, Object o)
    {
        return new Data(id, o);
    }

    public static Data convertToData(String id, String s, Integer length)
    {
        if (length != null)
        {
            s = StringUtils.substring(s, 0, length);
        }
        return new Data(id, s);
    }

    private String id;

    private Object value;

    public Data()
    {
        super();
    }

    public Data(String id, Object value)
    {
        super();
        setId(id);
        if (value instanceof BigDecimal && ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0)
        {
            setValue(new BigDecimal("0.00"));
        }
        else
        {
            setValue(value);
        }

    }

    public String getId()
    {
        return id;
    }

    public Object getValue()
    {
        return value;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

}
