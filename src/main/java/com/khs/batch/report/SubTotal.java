package com.khs.batch.report;

import java.math.BigDecimal;

/**
 * @author dpitt@keyholesoftware.com
 */
public class SubTotal
{

    private String id;
    private BigDecimal total = new BigDecimal(0.0);

    public void add(BigDecimal bd)
    {
        setTotal(getTotal().add(bd));
    }

    public void clear()
    {
        setTotal(new BigDecimal(0.0));
    }

    public String getId()
    {
        return id;
    }

    public BigDecimal getTotal()
    {
        return total;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setTotal(BigDecimal total)
    {
        this.total = total;
    }

}
