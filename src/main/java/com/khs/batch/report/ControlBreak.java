package com.khs.batch.report;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dpitt@keyholesoftware.com
 */
public class ControlBreak
{

    private String id;
    private Object value;
    private Object previousValue;
    private List<SubTotal> subTotals = new ArrayList<SubTotal>();
    private boolean total;

    public void clearSubTotals()
    {
        for (SubTotal t : subTotals)
        {
            t.clear();
        }
        return;
    }

    public SubTotal findSubTotal(String id)
    {
        SubTotal total = null;
        for (SubTotal t : subTotals)
        {
            if (t.getId().equals(id))
            {
                return t;
            }
        }
        total = new SubTotal();
        total.setId(id);
        subTotals.add(total);
        return total;
    }

    public String getId()
    {
        return id;
    }

    public Object getPreviousValue()
    {
        return previousValue;
    }

    /**
     * @return the subTotals
     */
    public List<SubTotal> getSubTotals()
    {
        return subTotals;
    }

    public Object getValue()
    {
        return value;
    }

    public boolean isTotal()
    {
        return total;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setPreviousValue(Object previousValue)
    {
        this.previousValue = previousValue;
    }

    /**
     * @param subTotals
     *            the subTotals to set
     */
    public void setSubTotals(List<SubTotal> subTotals)
    {
        this.subTotals = subTotals;
    }

    public void setTotal(boolean total)
    {
        this.total = total;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

}
