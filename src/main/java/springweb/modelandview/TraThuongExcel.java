package springweb.modelandview;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.joda.time.DateTime;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import pojo.AppParam;
import springweb.controllers.AjaxController;
import tool.Util;

public class TraThuongExcel extends AbstractExcelView {
	private HSSFWorkbook template = null;
	private String from_date = null, to_date = null;

	@Override
	protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Content-disposition", "attachment; filename=" + model.get("file_name"));
		File file = new File(
				System.getProperty("user.dir") + File.separator + "template" + File.separator + "trathuong.xls");
		System.out.println("Final filepath : " + file.getAbsolutePath());

		from_date = (String) model.get("from_date");
		to_date = (String) model.get("to_date");

		// System.out.println("from_date : " + from_date + ", to_date : " +
		// to_date);

		from_date = from_date.substring(8, 10) + "/" + from_date.substring(5, 7) + "/" + from_date.substring(0, 4);
		to_date = to_date.substring(8, 10) + "/" + to_date.substring(5, 7) + "/" + to_date.substring(0, 4);
		template = new HSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
		buildKichHoatSheet(model, workbook);
		buildNapTheSheet(model, workbook);
		buildPscSheet(model, workbook);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void buildKichHoatSheet(Map model, HSSFWorkbook workbook) {
		HSSFSheet sheet = template.getSheet("kichHoat");
		HSSFSheet kichHoatSheet = workbook.createSheet("kichHoat");

		Util.copySheets(kichHoatSheet, sheet);

		kichHoatSheet.getRow(1).getCell(0).setCellValue("BHM kích hoạt từ " + from_date + " đến " + to_date);

		kichHoatSheet.getRow(3).getCell(0)
				.setCellValue("Xuất ngày " + AjaxController.DD_MM_YYYY_FORMAT.format(new DateTime().toDate()));
		List<Map<String, String>> kichHoat = (List<Map<String, String>>) model.get("kichHoat");
		HSSFRow srcRow = sheet.getRow(7);
		int location = 0;
		Map<String, String> item;
		String value;
		Map<Integer, CellStyle> styleMap = (true) ? new HashMap<Integer, CellStyle>() : null;
		int maxColumnNum = 0;
		HSSFRow destRow;
		for (int record = 1; record < kichHoat.size(); record++) {
			item = kichHoat.get(record);
			location = 0;
			for (String k : item.keySet()) {
				value = item.get(k).replaceAll("<td>", "");
				// destRow.createCell(location);
				// destRow.getCell(location).setCellType(srcRow.getCell(location).getCellType());
				if (AppParam.isNumeric(value) && value.length() < 11)
					srcRow.getCell(location).setCellValue(Double.valueOf(value));
				else
					srcRow.getCell(location).setCellValue(value);
				// destRow.getCell(location).setCellType(srcRow.getCell(location).getCellType());

				location++;
			}
			kichHoatSheet.shiftRows(record + 6, kichHoatSheet.getLastRowNum(), 1);
			destRow = kichHoatSheet.getRow(record + 6);
			Util.copyRow(sheet, kichHoatSheet, srcRow, destRow, styleMap);
		}

		kichHoatSheet.getRow(kichHoat.size() + 7).getCell(5).setCellFormula("SUM(F8:F" + (kichHoat.size() + 6) + ")");
		kichHoatSheet.getRow(kichHoat.size() + 7).getCell(6).setCellFormula("SUM(G8:G" + (kichHoat.size() + 6) + ")");
		kichHoatSheet.getRow(kichHoat.size() + 7).getCell(9).setCellFormula("SUM(J8:J" + (kichHoat.size() + 6) + ")");
		kichHoatSheet.shiftRows(kichHoat.size() + 7, kichHoatSheet.getLastRowNum(), -1);
		// kichHoatSheet.removeRow(kichHoatSheet.getRow(kichHoat.size() + 6));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void buildNapTheSheet(Map model, HSSFWorkbook workbook) {
		HSSFSheet sheet = template.getSheet("napThe");
		HSSFSheet reportSheet = workbook.createSheet("napThe");

		Util.copySheets(reportSheet, sheet);

		reportSheet.getRow(1).getCell(0).setCellValue("Nạp thẻ lần đầu từ " + from_date + " đến " + to_date);

		reportSheet.getRow(3).getCell(0)
				.setCellValue("Xuất ngày " + AjaxController.DD_MM_YYYY_FORMAT.format(new DateTime().toDate()));
		List<Map<String, String>> itemList = (List<Map<String, String>>) model.get("napThe");
		HSSFRow srcRow = sheet.getRow(6);
		int location = 0;
		Map<String, String> item;
		String value;
		Map<Integer, CellStyle> styleMap = (true) ? new HashMap<Integer, CellStyle>() : null;
		int maxColumnNum = 0;
		HSSFRow destRow;
		for (int record = 1; record < itemList.size(); record++) {
			item = itemList.get(record);
			location = 0;
			for (String k : item.keySet()) {
				value = item.get(k).replaceAll("<td>", "");
				// destRow.createCell(location);
				// destRow.getCell(location).setCellType(srcRow.getCell(location).getCellType());
				if (AppParam.isNumeric(value) && value.length() < 11)
					srcRow.getCell(location).setCellValue(Double.valueOf(value));
				else
					srcRow.getCell(location).setCellValue(value);
				// destRow.getCell(location).setCellType(srcRow.getCell(location).getCellType());

				location++;
			}
			reportSheet.shiftRows(record + 5, reportSheet.getLastRowNum(), 1);
			destRow = reportSheet.getRow(record + 5);
			Util.copyRow(sheet, reportSheet, srcRow, destRow, styleMap);
		}

		reportSheet.getRow(itemList.size() + 6).getCell(5).setCellFormula("SUM(F7:F" + (itemList.size() + 5) + ")");
		reportSheet.getRow(itemList.size() + 6).getCell(6).setCellFormula("SUM(G7:G" + (itemList.size() + 5) + ")");
		reportSheet.shiftRows(itemList.size() + 6, reportSheet.getLastRowNum(), -1);
		// kichHoatSheet.removeRow(kichHoatSheet.getRow(kichHoat.size() + 6));
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private void buildPscSheet(Map model, HSSFWorkbook workbook) {
		HSSFSheet sheet = template.getSheet("psc");
		HSSFSheet reportSheet = workbook.createSheet("psc");

		Util.copySheets(reportSheet, sheet);

		reportSheet.getRow(1).getCell(0)
				.setCellValue("Khuyến khích PSC ngày thứ 90 của TB từ " + from_date + " đến " + to_date);

		reportSheet.getRow(3).getCell(0)
				.setCellValue("Xuất ngày " + AjaxController.DD_MM_YYYY_FORMAT.format(new DateTime().toDate()));
		List<Map<String, String>> itemList = (List<Map<String, String>>) model.get("psc");
		HSSFRow srcRow = sheet.getRow(6);
		int location = 0;
		Map<String, String> item;
		String value;
		Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
		int maxColumnNum = 0;
		HSSFRow destRow;
		for (int record = 1; record < itemList.size(); record++) {
			item = itemList.get(record);
			location = 0;
			for (String k : item.keySet()) {
				value = item.get(k).replaceAll("<td>", "");
				// destRow.createCell(location);
				// destRow.getCell(location).setCellType(srcRow.getCell(location).getCellType());
				if (AppParam.isNumeric(value) && value.length() < 11)
					srcRow.getCell(location).setCellValue(Double.valueOf(value));
				else
					srcRow.getCell(location).setCellValue(value);
				// destRow.getCell(location).setCellType(srcRow.getCell(location).getCellType());

				location++;
			}
			reportSheet.shiftRows(record + 5, reportSheet.getLastRowNum(), 1);
			destRow = reportSheet.getRow(record + 5);
			Util.copyRow(sheet, reportSheet, srcRow, destRow, styleMap);
		}

		reportSheet.getRow(itemList.size() + 6).getCell(5).setCellFormula("SUM(F7:F" + (itemList.size() + 5) + ")");
		reportSheet.getRow(itemList.size() + 6).getCell(6).setCellFormula("SUM(G7:G" + (itemList.size() + 5) + ")");
		reportSheet.shiftRows(itemList.size() + 6, reportSheet.getLastRowNum(), -1);
		// kichHoatSheet.removeRow(kichHoatSheet.getRow(kichHoat.size() + 6));
	}
}