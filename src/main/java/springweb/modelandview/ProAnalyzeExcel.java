package springweb.modelandview;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;

import pojo.AppParam;

public class ProAnalyzeExcel extends AbstractPOIExcelView {

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(@SuppressWarnings("rawtypes") Map model, Workbook workbook,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-disposition", "attachment; filename=" + model.get("file_name"));
		exportDataToSheet(workbook, (List<Map<String, String>>) model.get("list"));
	}

	public void exportDataToSheet(Workbook workbook, List<Map<String, String>> dataList) {
		// int record = 1;
		int location = 0;
		Map<String, String> item, headerRow;
		SXSSFSheet excelSheet = null;
		SXSSFRow excelHeader = null;
		headerRow = dataList.get(0);

		String value = "";
		int sheet = 0;
		int sheetRow = 0;
		for (int record = 1; record < dataList.size(); record++) {
			if (record % 65535 == 1) {
				sheet++;
				excelSheet = (SXSSFSheet) workbook.createSheet("data" + sheet);
				excelHeader = (SXSSFRow) excelSheet.createRow(0);
				location = 0;
				for (String k : headerRow.keySet()) {
					excelHeader.createCell(location)
							.setCellValue(headerRow.get(k).replaceAll("<td>", "").replaceAll("<th>", ""));
					location++;
				}
			}
			item = dataList.get(record);
			sheetRow = record % 65535;
			if (sheetRow == 0)
				sheetRow = 65535;
			SXSSFRow excelRow = (SXSSFRow) excelSheet.createRow(sheetRow);
			location = 0;
			for (String k : item.keySet()) {
				value = item.get(k).replaceAll("<td>", "");
				excelRow.createCell(location);
				if (value != null && AppParam.isNumeric(value)
						&& !headerRow.get(k).replaceAll("<th>", "").equals("ISDN")
						&& !headerRow.get(k).replaceAll("<th>", "").equals("SIM_SERIAL")
						&& !headerRow.get(k).replaceAll("<th>", "").equals("SERIAL")) {
					excelRow.getCell(location).setCellType(XSSFCell.CELL_TYPE_NUMERIC);
					excelRow.getCell(location).setCellValue(Double.valueOf(value));
				} else
					excelRow.getCell(location).setCellValue(value);
				// if (headerRow.get(k).replaceAll("<th>",
				// "").equals("SIM_SERIAL")) {
				// System.out.println(value);
				// }

				location++;
			}

		}

	}

	@Override
	protected Workbook createWorkbook() {
		// TODO Auto-generated method stub
		return new SXSSFWorkbook();

	}

}