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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.util.StringUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author dpitt@keyholesoftware.com
 */
public class ReportPDFWriter implements ItemWriter<List<String[]>> {

	private final static String SINGLE_LINE = "-";
	private final static String DOUBLE_LINE = "=";
	private final static int CELLPADDING_TOP = 0;
	private final static int CELLPADDING_BOTTOM = 0;
	private final static int CELLPADDING_LEFT = 4;
	private final static int CELLPADDING_RIGHT = 4;

	private static String LEFT = "S";
	private static String RIGHT = "N";

	private void addMetaData(Document document) {
		document.addTitle(metaTitle);
		document.addSubject(metaSubject);
		document.addKeywords(metaKeywords);
		document.addAuthor(metaAuthor);
		document.addCreator(metaCreator);
	}

	private int[] colWidths;
	private String path = ReportingDefaultConstants.DEFAULT_PATH;
	private String name = ReportingDefaultConstants.FILE_NAME;
	private int fontSize = 8;
	private Document document = null;
	private PdfPTable detailTable = null;
	private PdfPTable footerTable = null;
	private Font font = null;
	private int width = ReportingDefaultConstants.DEFAULT_BODY_WIDTH_PERCENT;
	private int headingWidth = ReportingDefaultConstants.DEFAULT_HEADING_WIDTH_PERCENT;
	private String fileName = null;
	private String metaTitle = ReportingDefaultConstants.META_DATA_TITLE;
	private String metaSubject = ReportingDefaultConstants.META_DATA_SUBJECT;
	private String metaKeywords = ReportingDefaultConstants.META_DATA_KEYWORDS;
	private String metaAuthor = ReportingDefaultConstants.META_DATA_AUTHOR;
	private String metaCreator = ReportingDefaultConstants.META_DATA_CREATOR;

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaSubject() {
		return metaSubject;
	}

	public void setMetaSubject(String metaSubject) {
		this.metaSubject = metaSubject;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public String getMetaAuthor() {
		return metaAuthor;
	}

	public void setMetaAuthor(String metaAuthor) {
		this.metaAuthor = metaAuthor;
	}

	public String getMetaCreator() {
		return metaCreator;
	}

	public void setMetaCreator(String metaCreator) {
		this.metaCreator = metaCreator;
	}

	/**
	 * @return the colWidths
	 */
	public int[] getColWidths() {
		return colWidths;
	}

	/**
	 * @return the fontSize
	 */
	public int getFontSize() {
		return fontSize;
	}

	public int getHeadingWidth() {
		return headingWidth;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public int getWidth() {
		return width;
	}

	public String[] removeDirective(String[] elements) {

		String[] results = new String[elements.length - 1];
		for (int i = 0; i < results.length; i++) {
			results[i] = elements[i + 1];
		}
		return results;

	}

	/**
	 * @param colWidths
	 *            the colWidths to set
	 */
	public void setColWidths(int[] colWidths) {
		this.colWidths = colWidths;
	}

	/**
	 * @param fontSize
	 *            the fontSize to set
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public void setHeadingWidth(int headingWidth) {
		this.headingWidth = headingWidth;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void write(List<? extends List<String[]>> line) {
		// TODO Auto-generated method stub

		for (List<String[]> l : line) {

			for (String[] elements : l) {
				try {

					if (document == null) {
						init();
					}

					String directive = elements[0];
					String[] reportData = removeDirective(elements);
					if (directive.equals(ReportProcessor.HEADER)) {
						createHeading(reportData[0]);
					}

					if (directive.equals(ReportProcessor.COL_HEADINGS)) {
						createColHeadings(reportData);
					}

					if (directive.equals(ReportProcessor.DETAIL)) {
						addDetail(reportData);
					}

					if (directive.equals(ReportProcessor.SUB_TOTAL)) {
						addSubtotal(reportData);
					}

					if (directive.equals(ReportProcessor.TOTAL)) {
						addTotal(reportData);
					}

					if (directive.equals(ReportProcessor.FOOTER)) {
						createFootings(reportData[0]);
					}

					if (directive.equals(ReportProcessor.PAGE_BREAK)) {
						newPage();
					}

					if (elements[0].equals(ReportProcessor.EOF)) {
						endFile();
						return;
					}

				} catch (DocumentException e) {
					e.printStackTrace();
					document.close();
					detailTable = null;
					footerTable = null;
					document = null;
					throw new RuntimeException(e.getMessage());
				}

			}
		}

	}

	private void addDetail(String[] data) throws DocumentException {

		for (String value : data) {
			PdfPCell c1 = new PdfPCell(new Phrase(value(value), font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(value));
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

	}

	private void addSubtotal(String[] data) throws DocumentException {

		// add dashed line...
		for (String value : data) {

			String v = null;
			String rawValue = value(value);
			if (StringUtils.hasText(rawValue)) {
				v = line(rawValue, SINGLE_LINE);
			}

			PdfPCell c1 = new PdfPCell(new Phrase(v, font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(value));
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

		// add totals....
		for (String value : data) {
			PdfPCell c1 = new PdfPCell(new Phrase(value(value), font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(value));
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

		// add blank line....
		for (String value : data) {
			PdfPCell c1 = new PdfPCell(new Phrase(" ", font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

	}

	private void addTotal(String[] data) throws DocumentException {

		// add dashed line...
		for (String value : data) {

			String v = null;
			String rawValue = value(value);
			if (StringUtils.hasText(rawValue)) {
				v = line(rawValue, SINGLE_LINE);
			}

			PdfPCell c1 = new PdfPCell(new Phrase(v, font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(value));
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

		// add totals....
		for (String value : data) {
			PdfPCell c1 = new PdfPCell(new Phrase(value(value), font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(value));
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

		// add blank line....

		for (String value : data) {

			String v = null;
			String rawValue = value(value);
			if (StringUtils.hasText(rawValue)) {
				v = line(rawValue, DOUBLE_LINE);
			}

			PdfPCell c1 = new PdfPCell(new Phrase(v, font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(value));
			setCellPadding(c1);
			detailTable.addCell(c1);
		}

	}

	private int alignment(String value) {

		String[] e = parse(value);
		if (e[0] != null) {

			if (e[0].equals(LEFT)) {
				return Element.ALIGN_LEFT;
			}
			if (e[0].equals(RIGHT)) {
				return Element.ALIGN_RIGHT;
			}

		}

		return Element.ALIGN_CENTER;

	}

	private void calculateRelativeSizes(PdfPTable table) throws DocumentException {
		float total = 0;
		int numCols = colWidths.length;
		for (int k = 0; k < numCols; ++k) {
			total += colWidths[k];
		}
		if (total > document.getPageSize().getWidth()) {
			throw new DocumentException("Table size is greater than page width.");
		}
		float[] relativeWidths = new float[colWidths.length];
		for (int k = 0; k < numCols; ++k) {
			relativeWidths[k] = 100 * colWidths[k] / total;
		}
		table.setWidths(relativeWidths);
		float widthPercentage = total / document.getPageSize().getWidth() * 100;
		table.setWidthPercentage(widthPercentage);

	}

	private void createColHeadings(String[] cols) throws DocumentException {

		createTable(cols.length);
		for (String col : cols) {
			PdfPCell c1 = new PdfPCell(new Phrase(value(col), font));
			c1.setBorder(Rectangle.NO_BORDER);
			c1.setHorizontalAlignment(alignment(col));
			setCellPadding(c1);
			c1.setPaddingBottom(5);
			detailTable.addCell(c1);
		}

	}

	private void createFootings(String footing) throws DocumentException {
		String[] cols = footing.split(ReportProcessor.ALIGN_DELIMITER, 3);
		float[] colSizes = new float[] { 25, 50, 25 };

		footerTable = new PdfPTable(colSizes);
		footerTable.setWidthPercentage(getHeadingWidth());

		int count = 0;
		for (String c : cols) {
			PdfPCell c1 = new PdfPCell(new Phrase(value(c), font));

			c1.setBorder(Rectangle.NO_BORDER);
			int alignment = Element.ALIGN_CENTER;
			if (count == 0) {
				alignment = Element.ALIGN_LEFT;
			} else if (count == 2) {
				alignment = Element.ALIGN_RIGHT;
			}
			c1.setHorizontalAlignment(alignment);
			setCellPadding(c1);
			footerTable.addCell(c1);
			count++;
		}
	}

	private void createHeading(String heading) throws DocumentException {

		String[] cols = heading.split(ReportProcessor.ALIGN_DELIMITER, 3);
		PdfPTable table = new PdfPTable(cols.length);
		table.setWidthPercentage(getHeadingWidth());

		int count = 0;
		for (String c : cols) {
			PdfPCell c1 = new PdfPCell(new Phrase(value(c), font));

			c1.setBorder(Rectangle.NO_BORDER);
			int alignment = Element.ALIGN_CENTER;
			if (count == 0) {
				alignment = Element.ALIGN_LEFT;
			} else if (count == 2) {
				alignment = Element.ALIGN_RIGHT;
			}
			c1.setHorizontalAlignment(alignment);
			setCellPadding(c1);
			table.addCell(c1);
			count++;
		}
		document.add(table);

	}

	private void createTable(int cols) throws DocumentException {
		if (colWidths != null) {
			detailTable = createTableWithAbsoluteWidths();
		} else {
			detailTable = new PdfPTable(cols);
			detailTable.setWidthPercentage(getWidth());
		}
		detailTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

	}

	private PdfPTable createTableWithAbsoluteWidths() throws DocumentException {
		PdfPTable table = new PdfPTable(colWidths.length);
		calculateRelativeSizes(table);
		return table;
	}

	/**
	 * @throws DocumentException
	 */
	private void endFile() throws DocumentException {
		document.add(detailTable);
		if (footerTable != null) {
			document.add(footerTable);
		}
		document.close();
		detailTable = null;
		footerTable = null;
		document = null;

	}

	private String fileName() {
		Calendar cal = Calendar.getInstance();
		String dateTime = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE) + "-" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE);
		String extension = ".pdf";
		this.fileName = name + "-" + dateTime + extension;
		return this.fileName;
	}

	private void init() {

		try {

			// COURIER is monospaced font
			font = new Font(Font.COURIER, fontSize, Font.NORMAL);
			document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);

			PdfWriter.getInstance(document, new FileOutputStream(new File(new URI(path + fileName()))));
			document.open();
			addMetaData(document);
		} catch (Exception e) {
			document = null;
			throw new RuntimeException("Error creating batch report " + this.path + this.fileName + "-" + e);
		}

	}

	private String line(String value, String type) {

		String result = "";
		for (int i = 0; i < value.length(); i++) {
			result += type;
		}

		return result;
	}

	private void newPage() throws DocumentException {

		document.add(detailTable);
		if (footerTable != null) {
			document.add(footerTable);
		}
		detailTable = null;
		footerTable = null;
		document.newPage();

	}

	private String[] parse(String value) {

		String[] results = new String[2];
		String[] elements = value.split(ReportProcessor.ALIGN_DELIMITER);
		if (elements.length == 2) {
			results[0] = elements[0];
			results[1] = elements[1];

		} else {

			results[0] = null;
			results[1] = value;
		}

		return results;
	}

	/**
	 * @param c1
	 */
	private void setCellPadding(PdfPCell c1) {
		c1.setPaddingLeft(CELLPADDING_LEFT);
		c1.setPaddingTop(CELLPADDING_TOP);
		c1.setPaddingRight(CELLPADDING_RIGHT);
		c1.setPaddingBottom(CELLPADDING_BOTTOM);
	}

	private String value(String value) {
		String[] result = parse(value);
		return result[1];
	}

}
