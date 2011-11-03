package com.khs.batch.report;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author dpitt@keyholesoftware.com
 */
public class Column
{

    public static Column New(String id, String title)
    {
        return new Column(id, title, false, false, false);
    }

    public static Column New(String id, String title, boolean group, boolean total, boolean numeric)
    {
        return new Column(id, title, group, total, numeric);
    }

    public static Column NewGroup(String id, String title)
    {
        return new Column(id, title, true, false, false);
    }

    public static Column NewNumeric(String id, String title)
    {
        return new Column(id, title, false, false, true);
    }

    public static Column NewTotal(String id, String title)
    {
        return new Column(id, title, false, true, true);
    }

    private String id;
    private String title;
    private boolean total;
    private boolean group;
    private boolean number;
    private String format;
    private int precision = 2;

    public Column()
    {
        super();
    }

    public Column(String id, String title, boolean group, boolean total, boolean numeric)
    {
        super();
        setId(id);
        setTitle(title);
        setGroup(group);
        setTotal(total);
        setNumber(numeric);
    }

    public String format(Object o)
    {

        if (isTotal() || isNumber())
        {
            BigDecimal amount = new BigDecimal("" + o);
            NumberFormat n = NumberFormat.getNumberInstance();
            n.setMinimumFractionDigits(precision);
            n.setMaximumFractionDigits(precision);
            double damount = amount.doubleValue();
            return n.format(damount);
        }

        return "" + o;
    }

    public String getFormat()
    {
        return format;
    }

    public String getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean isGroup()
    {
        return group;
    }

    public boolean isNumber()
    {
        return number;
    }

    public boolean isTotal()
    {
        return total;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public void setGroup(boolean group)
    {
        this.group = group;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setNumber(boolean number)
    {
        this.number = number;
    }

    public Column setPrecision(int precision)
    {
        this.precision = precision;
        return this;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setTotal(boolean total)
    {
        this.total = total;
    }

}
