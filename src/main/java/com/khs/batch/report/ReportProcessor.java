package com.khs.batch.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;

/**
 * @author dpitt@keyholesoftware.com
 */
public class ReportProcessor implements
	ItemProcessor<List<Data>, List<String[]>> {


    public final static String EMPTY_REPORT = "empty-report";
    public final static String HEADER = "HEADER";
    public final static String DETAIL = "BODY";
    public final static String FOOTER = "FOOTER";
    public final static String SUB_TOTAL = "SUBTOTAL";
    public final static String TOTAL = "TOTAL";
    public final static String PAGE_BREAK = "PAGEBREAK";
    public final static String COL_HEADINGS = "COLHEADINGS";
    public final static String EOF = "EOF";
    public final static String ALIGN_DELIMITER = "~";
    public final static String PAGE_INDICATOR = "#";

    private List<Column> columns = new ArrayList<Column>();
    private List<ControlBreak> controlBreaks = new ArrayList<ControlBreak>();
    private List<Total> totals = new ArrayList<Total>();
    private int linesPerPage = 60;
    private ReportFactory factory = null;
    private List<String[]> output = new ArrayList<String[]>();
    private boolean started = false;
    private int currentLines = 0;
    private int currentPage = 1;
    // private int detailLines = 0;
    private boolean createBlankReport = true;
    private boolean newPage = false;

    public void addData(String id, Object value)
    {

        if (idExists(id))
        {
            Data data = new Data();
            data.setId(id);
            data.setValue(value);
        }
        else
        {

            throw new RuntimeException(
                    "Col id ("
                            + id
                            + ") for data element does not exist...must add column first...");
        }
    }

    public String applyPage(String value)
    {

        return value.replaceAll(PAGE_INDICATOR, "" + currentPage);
    }

    public void checkForGroupBreaks(String id, Object value, List<ControlBreak> groupBreaks)
    {
        for (Column c : columns)
        {
            if (c.getId().equals(id))
            {
                if (c.isGroup())
                {
                    // check to see if it's time for control break
                    ControlBreak cb = controlBreakCheck(id, value);
                    if (cb != null && cb.isTotal())
                    {
                        groupBreaks.add(cb);
                    }
                }
            }
        }
    }

    public Column columnForId(String id)
    {

        for (Column c : columns)
        {
            if (c.getId().equals(id))
            {
                return c;
            }
        }

        return null;
    }

    public ControlBreak controlBreakCheck(String id, Object value)
    {

        ControlBreak cb = controlBreakForId(id);
        if (cb == null)
        {
            cb = new ControlBreak();
            cb.setId(id);
            controlBreaks.add(cb);
        }

        if (cb.getValue() == null)
        {
            cb.setValue(value);
            cb.setPreviousValue(value);
            cb.setTotal(false);
        }
        else if (cb.getValue().equals(value))
        {
            cb.setTotal(false);
        }
        else if (!cb.getValue().equals(value))
        {
            cb.setTotal(true);
            cb.setValue(value);
        }
        return cb;
    }

    public ControlBreak controlBreakForId(String id)
    {

        for (ControlBreak c : controlBreaks)
        {
            if (c.getId().equals(id))
            {
                return c;
            }
        }

        return null;
    }

    public ReportFactory getFactory()
    {
        return factory;
    }

    public int getLinesPerPage()
    {
        return linesPerPage;
    }

    public boolean idExists(String id)
    {

        for (Column c : columns)
        {
            if (c.getId().equals(id))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the createBlankReport
     */
    public boolean isCreateBlankReport()
    {
        return createBlankReport;
    }

    public List<String[]> process(List<Data> data) throws Exception
    {

   
        output = new ArrayList<String[]>();

        // init first time only
        if (!started)
        {

            currentLines = 0;
            columns = factory.getColumns();
            output = new ArrayList<String[]>();
            started = true;
            newPage = true;
       
        }

        if (eof(data))
        {
            if (!createBlankReport && isEmptyReport())
            {
                reset();
                return null;
            }
            if (createBlankReport && isEmptyReport())
            {
                writeColumnHeaders();
            }

            writeSubtotals(controlBreaks);
            // make sure totals can
            // fit on last page.. if not,
            // add new page..
            pageBreakRequired(4);
            writeTotals();
            writePageFooters();
            writeEOF();

            // clear
            reset();
            return output;
        }

        // column headers
        if (currentLines == 0)
        {
            writeColumnHeaders();
        }

        // validate that number of columns match data elements
        if (columns.size() != data.size())
        {
            throw new RuntimeException("Error - number of columns defined in factory (" + columns.size() + " ) " + factory.getClass().getName() + "  does not match number of Data elements (" + data.size() + ")");
        }

        List<String> rowValues = new ArrayList<String>();
        List<ControlBreak> groupBreaks = new ArrayList<ControlBreak>();
        // check for new groups started
        for (Data d : data)
        {
            checkForGroupBreaks(d.getId(), d.getValue(), groupBreaks);
        }
        // subtotal
        if (!groupBreaks.isEmpty())
        {
            writeSubtotals(groupBreaks);
            pageBreakRequired(1);
        }

        for (Data d : data)
        {
            Column col = columnForId(d.getId());
            if (col == null)
            {
                throw new RuntimeException("Column id (" + d.getId() + ") not found for data...");
            }

            ControlBreak cb = controlBreakForId(col.getId());
            Object colValue = d.getValue();

            if (cb != null && colValue.equals(cb.getPreviousValue()) && !newPage)
            {
                rowValues.add(" ");
            }
            else
            {
                rowValues.add(formatType(col, col.format(d.getValue())));
            }

            // grand total
            if (col.isTotal())
            {
                Total total = findTotal(col.getId());
                total.add(new BigDecimal("" + d.getValue()));
            }

        }

        // accumulate sub totals
        for (Data d : data)
        {
            Column col = columnForId(d.getId());
            ControlBreak cb = controlBreakForId(d.getId());
            // update previous control break value
            if (cb != null)
            {
                cb.setPreviousValue(cb.getValue());
            }
            if (col == null)
            {
                throw new RuntimeException("Column id (" + d.getId() + ") not found for data...");
            }
            if (col.isTotal())
            {
                updateSubTotalValues(d, col);
            }
        }

        output.add(convertToArray(DETAIL, rowValues));

        if (!pageBreakRequired(3))
        {
            currentLines++;
            newPage = false;
        }
        return output;
    }

    public void reset()
    {
        started = false;
        currentPage = 1;
        // subTotals = new ArrayList<SubTotal>();
        totals = new ArrayList<Total>();
        controlBreaks = new ArrayList<ControlBreak>();
        newPage = false;
    }

    /**
     * @param createBlankReport
     *            the createBlankReport to set
     */
    public void setCreateBlankReport(boolean createBlankReport)
    {
        this.createBlankReport = createBlankReport;
    }

    public void setFactory(ReportFactory factory)
    {
        this.factory = factory;
    }

    public void setLinesPerPage(int linesPerPage)
    {
        this.linesPerPage = linesPerPage;
    }

    private void clearTotals()
    {
        for (Total t : totals)
        {
            t.clear();
        }
        return;
    }

    private boolean eof(List<Data> data)
    {
        return data == null || data.get(0) instanceof EOF;
    }

    private Total findTotal(String id)
    {
        Total total = null;
        for (Total t : totals)
        {
            if (t.getId().equals(id))
            {
                return t;
            }
        }
        total = new Total();
        total.setId(id);
        totals.add(total);
        return total;

    }

    private String formatType(Column col, String value)
    {

        return (col.isTotal() || (col.isNumber()) ? "N" : "S") + ALIGN_DELIMITER + value;
    }

    private boolean isEmptyReport()
    {
       /* if (this.stepExecution.getJobExecution().getExecutionContext().containsKey(EMPTY_REPORT))
        {
            return (Boolean) this.stepExecution.getExecutionContext().get(EMPTY_REPORT);
        }*/
        return false;
    }

    /**
     * Determine if page break is required for lines that are going to be output. return true if page break output
     * 
     * @param lines
     *            that are going to be output
     */
    private boolean pageBreakRequired(int lines)
    {
        boolean result = false;
        if (currentLines + lines > linesPerPage)
        {
            writePageFooters();
            writePageBreak();
            // clearControlBreakPreviousValue();
            currentPage++;
            currentLines = 0;
            writeColumnHeaders();
            result = true;
            newPage = true;
        }

        return result;
    }

    private boolean shouldTotal()
    {
        for (Column col : columns)
        {
            if (col.isTotal())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param d
     * @param col
     */
    private void updateSubTotalValues(Data d, Column col)
    {
        for (ControlBreak cb : controlBreaks)
        {
            SubTotal total = cb.findSubTotal(col.getId());
            total.add(new BigDecimal("" + d.getValue()));
        }
    }

    private void writeColumnHeaders()
    {

        String[] headers = factory.getHeader();
        for (String h : headers)
        {
            writeHeader(h);
            currentLines++;
        }
        writeHeader(" ");
        currentLines++;
        started = true;

        List<Column> cols = columns;
        List<String> titles = new ArrayList<String>();
        for (Column col : cols)
        {
            titles.add(formatType(col, col.getTitle()));
        }

        output.add(convertToArray(COL_HEADINGS, titles));
        currentLines++;

    }

    private void writePageFooters()
    {
        currentLines++;
        String[] footers = factory.getFooter();
        if (footers != null)
        {
            for (String h : footers)
            {
                writeFooter(h);
                currentLines++;
            }
        }

    }

    private void writeSubtotals(List<ControlBreak> groupBreaks)
    {

        List<ControlBreak> sortedGroups = null;
        if (groupBreaks.size() > 1)
        {
            sortedGroups = new ArrayList<ControlBreak>();
            sortedGroups.addAll(groupBreaks);
            Collections.reverse(sortedGroups);
        }
        for (ControlBreak groupBreak : sortedGroups != null ? sortedGroups : groupBreaks)
        {
            List<String> subtotals = new ArrayList<String>();
            boolean hasValue = false;
            if (shouldTotal())
            {
                for (Column col : columns)
                {
                    ControlBreak cb = controlBreakForId(col.getId());
                    if (col.isTotal())
                    {
                        subtotals.add(formatType(col, col.format(groupBreak.findSubTotal(col.getId()).getTotal())));
                        hasValue = true;
                    }
                    else if (newPage && cb != null)
                    {
                        subtotals.add(formatType(col, col.format(cb.getPreviousValue())));
                    }
                    else
                    {
                        subtotals.add(" ");
                    }
                }
            }
            if (hasValue || newPage)
            {
                int numLinesAdded = hasValue ? 3 : 2;
                output.add(convertToArray(SUB_TOTAL, subtotals));
                pageBreakRequired(numLinesAdded);
                currentLines = currentLines + numLinesAdded;
            }
            else
            {
                String[] blankLine = new String[columns.size() + 1];
                blankLine[0] = DETAIL;
                for (int i = 0; i < columns.size(); i++)
                {
                    blankLine[i + 1] = " ";
                }
                output.add(blankLine);
                currentLines++;
            }
            // clear subtotals
            groupBreak.clearSubTotals();
        }
    }

    private void writeTotals()
    {

        List<String> t = new ArrayList<String>();
        boolean hasTotalColumn = false;
        for (Column col : columns)
        {

            if (col.isTotal())
            {

                t.add(formatType(col, col.format(findTotal(col.getId()).getTotal())));
                hasTotalColumn = true;

            }
            else
            {

                t.add(" ");

            }

        }
        if (hasTotalColumn)
        {
            output.add(convertToArray(TOTAL, t));
        }
        // clear subtotals
        clearTotals();

    }
    
    
    
    

    String[] convertToArray(String type, List<String> list)
    {
        String[] a = new String[list.size() + 1];
        a[0] = type;
        for (int i = 0; i < list.size(); i++)
        {
            a[i + 1] = list.get(i);
        }
        return a;
    }

    void writeDetail(String value)
    {
        String[] a = new String[] { DETAIL, value };
        output.add(a);
    }

    void writeEOF()
    {
        String[] a = new String[] { EOF };
        output.add(a);
    }

    void writeFooter(String value)
    {
        String[] a = new String[] { FOOTER, value };
        output.add(a);
    }

    void writeHeader(String value)
    {
        String[] a = new String[] { HEADER, applyPage(value) };
        output.add(a);
    }

    void writePageBreak()
    {
        String[] a = new String[] { PAGE_BREAK };
        output.add(a);
    }
}
