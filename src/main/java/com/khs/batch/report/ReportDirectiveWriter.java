package com.khs.batch.report;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.util.StringUtils;

/**
 * @author dpitt@keyholesoftware.com
 */
public class ReportDirectiveWriter implements ItemWriter<List<String[]>>{

	public void write(List<? extends List<String[]>> rows) throws Exception {


		for (List<String[]> row : rows) {
			
			for (String[] r : row) {
				 
				  System.out.print(StringUtils.arrayToCommaDelimitedString(r));
				  //for (String c : r) {
				 //	  System.out.print(c);
				 // }
				  
				  System.out.println("");
				
			}
			
			
		}
		
		
	}

	

}
