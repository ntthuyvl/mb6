package springweb.modelandview;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import pojo.AppParam;
import pojo.ExcelSheetParam;
import pojo.HoaHongThuCuoc;
import pojo.HoaHongThuCuocTrongDs;
import pojobase.oracle.OracleBase;
import tool.Util;
import java.sql.ResultSet;

public class ExcelFromTemplate extends AbstractPOIExcelView {
	private XSSFWorkbook template = null;
	private String month, bill_cycle_id;
	private String template_file_name;
	// private SXSSFSheet templateSheet = null;
	ExcelSheetParam[] sheetArray = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(@SuppressWarnings("rawtypes") Map model, Workbook workbook,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-disposition", "attachment; filename=" + model.get("file_name"));

		File file = new File(
				System.getProperty("user.dir") + File.separator + "template" + File.separator + template_file_name);
		OracleBase.syslog("Final filepath : " + file.getAbsolutePath());
		// month = (String) model.get("month");
		// bill_cycle_id = (String) model.get("bill_cycle_id");
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		template = new XSSFWorkbook(bis);
		SXSSFSheet destSheet = (SXSSFSheet) workbook.createSheet();
		Util.copySheets(destSheet, template.getSheetAt(0), true);
		bis.close();

		// templateSheet = destSheet;

		Map<String, HoaHongThuCuoc> hoaHongThuCuocMap = (Map<String, HoaHongThuCuoc>) model.get("hoaHongThuCuocMap");

		for (int i = 0; i < sheetArray.length; i++) {
			// if (key.equals("1600788"))
			buildHoaHongThuCuocSheet(workbook, sheetArray[i]);
		}
		// workbook.removeSheetAt(0);
	}

	private String convertDate(String date_month_input) {
		if (date_month_input.length() == 10)// convert date
			return date_month_input.substring(8, 10) + "/" + date_month_input.substring(5, 7) + "/"
					+ date_month_input.substring(0, 4);
		if (date_month_input.length() == 7)// convert month
			return date_month_input.substring(5, 7) + "/" + date_month_input.substring(0, 4);
		else
			return "";
	}

	private void buildHoaHongThuCuocSheet(Workbook workbook, ExcelSheetParam excelSheetParam) {
		SXSSFSheet sheet = (SXSSFSheet) workbook.cloneSheet(0);
		workbook.setSheetName(workbook.getSheetIndex(sheet), excelSheetParam.getSheetName());
		OracleBase.syslog("tao xong sheet " + excelSheetParam.getSheetName());
		int maxColumnNum = 0;
		int rowNum = sheet.getFirstRowNum();
		int curColNum = 0;
		int maxRowNum = sheet.getLastRowNum();
		String celVal;
		List<String[]> rs;
		Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
		XSSFRow destRow;
		Cell oldCell;
		while (rowNum <= maxRowNum) {
			Row srcRow = sheet.getRow(rowNum);
			for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
				oldCell = srcRow.getCell(j); // ancienne cell
				if (oldCell != null && oldCell.getStringCellValue().startsWith("${")
						&& oldCell.getStringCellValue().endsWith("}")) {
					celVal = oldCell.getStringCellValue();
					if (!celVal.startsWith("${rs")) {
						// là ô tham số đơn
						oldCell.setCellValue(
								excelSheetParam.getParamMap().get(celVal.substring(2, celVal.length() - 2)));

					} else {
						// là result set
						curColNum = j;
						rs = excelSheetParam.getRsMap().get(celVal.substring(4, celVal.indexOf("[")));
						for (String[] rc : rs) {
							sheet.shiftRows(rowNum + 1, sheet.getLastRowNum(), 1);
							destRow = (XSSFRow) sheet.getRow(rowNum + 1);
							if (destRow == null)
								destRow = (XSSFRow) sheet.createRow(rowNum + 1);
							Util.copyRow(sheet, sheet, srcRow, sheet.getRow(rowNum + 1), styleMap);
							sheet.getRow(rowNum + 1).setHeight(srcRow.getHeight());
							for (int k = j; k <= srcRow.getLastCellNum(); k++) {
								oldCell = srcRow.getCell(k); // ancienne cell
								celVal = oldCell.getStringCellValue();
								if (celVal.startsWith("${rs")) {
									// là ô tham số đơn
									oldCell.setCellValue(excelSheetParam.getParamMap()
											.get(celVal.substring(2, celVal.length() - 2)));

								}
							}

						}
					}

				}

			}

		}

	}

	private void buildHoaHongThuCuocSheet(Workbook workbook, HoaHongThuCuoc hhtc) {

		// SXSSFSheet destSheet = (SXSSFSheet)
		// workbook.createSheet(hhtc.paramArray[HoaHongThuCuoc.COLLECTION_GROUP_ID]);
		// Util.copySheets(destSheet, templateSheet, true);

		SXSSFSheet destSheet = (SXSSFSheet) workbook.cloneSheet(0);
		workbook.setSheetName(workbook.getSheetIndex(destSheet), hhtc.paramArray[HoaHongThuCuoc.COLLECTION_GROUP_ID]);
		OracleBase.syslog("tao xong sheet " + hhtc.paramArray[HoaHongThuCuoc.COLLECTION_GROUP_ID]);
		/*
		 * * int row = -1, column = 1; SXSSFRow srcRow;
		 * 
		 * for (int j = 0; j < 2; j++) { row = row + 1;
		 * 
		 * } row = row + 1;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 1;
		 * srcRow.getCell(column).setCellValue("Chu kì " + bill_cycle_id +
		 * " tháng " + convertDate(month));
		 * 
		 * row = row + 1;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); srcRow.getCell(column)
		 * .setCellValue("(Doanh thu thu được từ " +
		 * convertDate(hhtc.paramArray[HoaHongThuCuoc.STA_DATE]) +
		 * " đến hết ngày " +
		 * convertDate(hhtc.paramArray[HoaHongThuCuoc.END_DATE]) + ")");
		 * 
		 * row = row + 2;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row);
		 * srcRow.getCell(column).setCellValue("Hôm nay, ngày " +
		 * AppParam.DD_MM_YYYY_FORMAT.format(new Date()) +
		 * ", tại Công ty dịch vụ MobiFone KV6, chúng tôi gồm:");
		 * 
		 * row = row + 2;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row);
		 * srcRow.getCell(column).setCellValue("2. Bên B: " +
		 * hhtc.paramArray[HoaHongThuCuoc.NAME]);
		 * 
		 * row = row + 1; srcRow = (SXSSFRow) destSheet.getRow(row);
		 * srcRow.getCell(column).setCellValue(
		 * "Cùng thống nhất lập biên bản xác nhận kết quả thu cước tháng " +
		 * convertDate(month) + ", cụ thể như sau:");
		 * 
		 * row = row + 3;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.KH_90])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_90])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HKH1])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.KH_NO_DONG_N])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_NO_DONG_N])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.KH_NO_DONG_N1])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_NO_DONG_N1])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.KH_NO_DONG])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_NO_DONG])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HKH2]));
		 * 
		 * row = row + 2;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.NO_DK])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TM_THU_TK])); column = column + 3;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TIEN_NOP_NH])); column = column + 6;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TIEN_DC_THANH_TOAN_CP])); column = column +
		 * 4; srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.NO_CK]));
		 * 
		 * row = row + 2;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.KH_TTTD])); column = column + 5;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_TTTD])); column = column + 4;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HSTD]));
		 * 
		 * row = row + 4; int k = hhtc.trongDs.size(); double MUC_CHI = 0.99;
		 * Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
		 * for (HoaHongThuCuocTrongDs trongDs : hhtc.trongDs) { srcRow =
		 * (SXSSFRow) destSheet.getRow(row); column = 1; if
		 * (trongDs.paramArray[HoaHongThuCuocTrongDs.LOAI_TT].equals("EZ"))
		 * MUC_CHI =
		 * Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.MUC_CHI])
		 * - 6.0; else MUC_CHI =
		 * Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.MUC_CHI])
		 * ;
		 * 
		 * srcRow.getCell(column).setCellValue(trongDs.paramArray[
		 * HoaHongThuCuocTrongDs.LOAI_TT]); column = column + 1;
		 * srcRow.getCell(column).setCellValue(trongDs.paramArray[
		 * HoaHongThuCuocTrongDs.NAME]); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(trongDs.
		 * paramArray[HoaHongThuCuocTrongDs.THU_DUOC])); column = column + 3;
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(trongDs.paramArray[
		 * HoaHongThuCuocTrongDs.DUOC_HUONG])); column = column + 3;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HSTD])); column = column + 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HKH1])); column = column + 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HSCL])); column = column + 1;
		 * srcRow.getCell(column).setCellValue("Vùng " +
		 * trongDs.paramArray[HoaHongThuCuocTrongDs.VUNG]); column = column + 1;
		 * srcRow.getCell(column).setCellValue(MUC_CHI); column = column + 1;
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(trongDs.paramArray[
		 * HoaHongThuCuocTrongDs.TY_LE_THU30])); column = column + 1;
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(trongDs.paramArray[
		 * HoaHongThuCuocTrongDs.TY_LE_NO90])); column = column + 1;
		 * 
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(trongDs.paramArray[
		 * HoaHongThuCuocTrongDs.DUOC_HUONG])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSTD])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH1])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]) * MUC_CHI /
		 * 100); k--; SXSSFRow destRow; if (k > 0) { destSheet.shiftRows(row +
		 * 1, destSheet.getLastRowNum(), 1); destRow = (SXSSFRow)
		 * destSheet.getRow(row + 1); if (destRow == null) destRow = (SXSSFRow)
		 * destSheet.createRow(row + 1); Util.copyRow(destSheet, destSheet,
		 * srcRow, destSheet.getRow(row + 1), styleMap); destSheet.getRow(row +
		 * 1).setHeight(srcRow.getHeight()); } row = row + 1; } // row = row +
		 * 1; srcRow = (SXSSFRow) destSheet.getRow(row); column = 4;
		 * srcRow.getCell(column).setCellFormula("SUM(E20:G" + (row) + ")");
		 * column = column + 3;
		 * srcRow.getCell(column).setCellFormula("SUM(H20:J" + (row) + ")");
		 * column = column + 10;
		 * srcRow.getCell(column).setCellFormula("SUM(R20:T" + (row) + ")");
		 * 
		 * row = row + 3;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 5;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_NDS])); column = column + 4;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.DG_NDS])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TH_NDS])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDS]));
		 * 
		 * row = row + 4;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HKH2])); column = column + 1;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.HSCL])); column = column + 3;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TM_NO_DONG_N])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.DG_NDN])); column = column + 1;
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.
		 * TM_NO_DONG_N])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL])); column =
		 * column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.TM_NO_DONG_N1])); column = column + 2;
		 * srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.
		 * paramArray[HoaHongThuCuoc.DG_NDN1])); column = column + 1;
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.
		 * TM_NO_DONG_N1])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN1])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL])); column =
		 * column + 2; srcRow.getCell(column)
		 * .setCellValue((Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.
		 * TH_NO_DONG_N])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN]) +
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NO_DONG_N1])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN1]))
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));
		 * 
		 * row = row + 2;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 7;
		 * srcRow.getCell(column)
		 * .setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.
		 * CHIPHI_TRONG_DS]) +
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NGOAI_DS]) +
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NODONG]));
		 * 
		 * row = row + 1;
		 * 
		 * srcRow = (SXSSFRow) destSheet.getRow(row); column = 3;
		 * srcRow.getCell(column) .setCellValue(tool.ReadNumber
		 * .numberToString(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.
		 * CHIPHI_TRONG_DS]) +
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NGOAI_DS]) +
		 * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NODONG])));
		 */
	}

	@Override
	protected Workbook createWorkbook() {
		// TODO Auto-generated method stub
		return new SXSSFWorkbook();

	}
}