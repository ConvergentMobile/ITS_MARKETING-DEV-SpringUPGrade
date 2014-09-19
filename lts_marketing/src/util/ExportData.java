package util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import reports_graphs.ReportData;

public class ExportData {
	private static Logger logger = Logger.getLogger(ExportData.class);

	public ExportData() {	
	}
	
	public String exportToXLS(Long userId, List<ReportData> rdList, List<String> colHeaders) throws Exception {
		String fileName = userId.toString() + "_" + Calendar.getInstance().getTimeInMillis() + ".xls";
		String outFile = PropertyUtil.load().getProperty("report_out_path") + fileName;
		File xlsFile = new File(outFile);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		
		int rowNum = 0;
		HSSFRow row = sheet.createRow(rowNum++);
		int colNum = 0;
		for (String colH : colHeaders) {
			HSSFCell cell = row.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(colH));
		}

		for (ReportData rd : rdList) {
			row = sheet.createRow(rowNum++);
			if (rd.getColumn1() != null) {
				HSSFCell cell = row.createCell(0);
		        cell.setCellValue(new HSSFRichTextString(rd.getColumn1()));
			}
			if (rd.getColumn2() != null) {
				HSSFCell cell = row.createCell(1);
		        cell.setCellValue(new HSSFRichTextString(rd.getColumn2()));
			}
			if (rd.getColumn3() != null) {
				HSSFCell cell = row.createCell(2);
		        cell.setCellValue(new HSSFRichTextString(rd.getColumn3()));
			}
			if (rd.getColumn4() != null) {
				HSSFCell cell = row.createCell(3);
		        cell.setCellValue(new HSSFRichTextString(rd.getColumn4()));
			}
			if (rd.getColumn5() != null) {
				HSSFCell cell = row.createCell(4);
		        cell.setCellValue(new HSSFRichTextString(rd.getColumn5()));
			}			
		}
		FileOutputStream fos = new FileOutputStream(xlsFile);
		workbook.write(fos);
		fos.close();
		
		return outFile;
	}
}
