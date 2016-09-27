package springweb.modelandview;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;

//import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import pojo.AppParam;
import pojo.HoaHongThuCuoc;
import pojo.HoaHongThuCuocTrongDs;
import pojobase.oracle.OracleBase;
import tool.Util;

public class HoaHongThuCuocExcel2 extends AbstractPOIExcelView {
	private String month, bill_cycle_id;

	@SuppressWarnings("unchecked")
	@Override
	protected void buildExcelDocument(@SuppressWarnings("rawtypes") Map model, Workbook workbook,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Content-disposition", "attachment; filename=" + model.get("file_name"));

		month = (String) model.get("month");
		bill_cycle_id = (String) model.get("bill_cycle_id");
		// SXSSFSheet destSheet = (SXSSFSheet) workbook.createSheet("hoahong");
		// Util.copySheets(destSheet, template.getSheet("hoahong"), true);
		// templateSheet = destSheet;

		Map<String, HoaHongThuCuoc> hoaHongThuCuocMap = (Map<String, HoaHongThuCuoc>) model.get("hoaHongThuCuocMap");
		if (hoaHongThuCuocMap.size() > 0) {
			for (String key : hoaHongThuCuocMap.keySet()) {
				// if (key.equals("1600788"))
				buildHoaHongThuCuocSheet(workbook, hoaHongThuCuocMap.get(key));
				OracleBase.syslog("tao xong sheet " + key);
			}

			workbook.removeSheetAt(0);
		}
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

	private String getHandoverDate(String date_month_input) {
		if (date_month_input.length() == 10)// convert date
			return String.format("%02d", Integer.parseInt(date_month_input.substring(8, 10)) + 4) + "/"
					+ date_month_input.substring(5, 7) + "/" + date_month_input.substring(0, 4);
		else
			return "";
	}

	private void buildHoaHongThuCuocSheet(Workbook workbook, HoaHongThuCuoc hhtc) {

		// SXSSFSheet destSheet = (SXSSFSheet)
		// workbook.createSheet(hhtc.paramArray[HoaHongThuCuoc.COLLECTION_GROUP_ID]);
		// Util.copySheets(destSheet, templateSheet, true);

		XSSFSheet destSheet = (XSSFSheet) workbook.cloneSheet(0);
		workbook.setSheetName(workbook.getSheetIndex(destSheet), hhtc.paramArray[HoaHongThuCuoc.COLLECTION_GROUP_ID]);

		int row = -1, column = 1, row_tds = 0, row_nds = 0, row_nd = 0;
		String tmp_string;
		XSSFRow srcRow;

		for (int j = 0; j < 2; j++) {
			row = row + 1;

		}
		row = row + 1;

		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 1;
		srcRow.getCell(column).setCellValue("Chu kì " + bill_cycle_id + " tháng " + convertDate(month));

		row = row + 1;

		srcRow = (XSSFRow) destSheet.getRow(row);
		srcRow.getCell(column)
				.setCellValue("(Doanh thu thu được từ " + convertDate(hhtc.paramArray[HoaHongThuCuoc.STA_DATE])
						+ " đến hết ngày " + convertDate(hhtc.paramArray[HoaHongThuCuoc.END_DATE]) + ")");
		row = row + 1;
		srcRow = (XSSFRow) destSheet.getRow(row);
		srcRow.getCell(column).setCellValue(
				"Căn cứ Hợp đồng dịch vụ số …………...ngày  ../.../….; Căn cứ Biên bản bàn giao kế hoạch thu cước ngày "
						+ getHandoverDate(hhtc.paramArray[HoaHongThuCuoc.STA_DATE]));

		row = row + 1;

		srcRow = (XSSFRow) destSheet.getRow(row);

		srcRow.getCell(column).setCellValue("Hôm nay, ngày " + AppParam.DD_MM_YYYY_FORMAT.format(new Date())
				+ ", tại Công ty dịch vụ MobiFone KV" + OracleBase.ct + ", chúng tôi gồm:");

		row = row + 2;
		srcRow = (XSSFRow) destSheet.getRow(row);
		srcRow.getCell(column).setCellValue("2. Bên B: " + hhtc.paramArray[HoaHongThuCuoc.NAME]);

		row = row + 1;
		srcRow = (XSSFRow) destSheet.getRow(row);
		srcRow.getCell(column).setCellValue("Cùng thống nhất lập biên bản xác nhận kết quả thu cước tháng "
				+ convertDate(month) + ", cụ thể như sau:");

		row = row + 3;

		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 1;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.KH_90]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_90]));
		column = column + 3;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH1]));
		column = column + 1;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.KH_NO_DONG_N]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NO_DONG_N]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.KH_NO_DONG_N1]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NO_DONG_N1]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.KH_NO_DONG]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NO_DONG]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2]));

		row = row + 2;

		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 1;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.NO_DK]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_THU_TK]));
		column = column + 3;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TIEN_NOP_NH]));
		column = column + 6;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TIEN_DC_THANH_TOAN_CP]));
		column = column + 4;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.NO_CK]));

		row = row + 2;

		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 1;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.KH_TTTD]));
		column = column + 5;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_TTTD]));
		column = column + 4;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSTD]));

		row = row + 4;
		int k = hhtc.trongDs.size();
		double MUC_CHI = 0.99;
		Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
		long totComissionPay = 0;

		// I/ CHI PHÍ THU CƯỚC PHÁT SINH TRONG DANH SÁCH ĐƯỢC GIAO (1)
		if (hhtc.trongDs.size() > 0) {
			for (HoaHongThuCuocTrongDs trongDs : hhtc.trongDs) {
				srcRow = (XSSFRow) destSheet.getRow(row);
				column = 1;
				if (trongDs.paramArray[HoaHongThuCuocTrongDs.LOAI_TT].equals("EZ"))
					MUC_CHI = Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.MUC_CHI]) - 6.0;
				else
					MUC_CHI = Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.MUC_CHI]);

				srcRow.getCell(column).setCellValue(trongDs.paramArray[HoaHongThuCuocTrongDs.LOAI_TT]);
				column = column + 1;
				srcRow.getCell(column).setCellValue(trongDs.paramArray[HoaHongThuCuocTrongDs.NAME]);
				column = column + 2;
				srcRow.getCell(column)
						.setCellValue(Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.THU_DUOC]));
				column = column + 3;
				srcRow.getCell(column)
						.setCellValue(Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.DUOC_HUONG]));
				column = column + 3;
				srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSTD]));
				column = column + 1;
				srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH1]));
				column = column + 1;
				srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));
				column = column + 1;
				srcRow.getCell(column).setCellValue("Vùng " + trongDs.paramArray[HoaHongThuCuocTrongDs.VUNG]);
				column = column + 1;
				srcRow.getCell(column).setCellValue(MUC_CHI);
				column = column + 1;
				srcRow.getCell(column)
						.setCellValue(Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.TY_LE_THU30]));
				column = column + 1;
				srcRow.getCell(column)
						.setCellValue(Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.TY_LE_NO90]));
				column = column + 1;

				// srcRow.getCell(column)
				// .setCellValue(Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.DUOC_HUONG])
				// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSTD])
				// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH1])
				// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]) *
				// MUC_CHI / 100);

				totComissionPay = totComissionPay
						+ Math.round(Double.parseDouble(trongDs.paramArray[HoaHongThuCuocTrongDs.DUOC_HUONG])
								* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSTD])
								* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH1])
								* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]) * MUC_CHI / 100);

				srcRow.getCell(column).setCellFormula("ROUND(H" + (row + 1) + "*K" + (row + 1) + "*L" + (row + 1) + "*M"
						+ (row + 1) + "*O" + (row + 1) + "%,0)");

				k--;
				XSSFRow destRow;
				if (k > 0) {
					destSheet.shiftRows(row + 1, destSheet.getLastRowNum(), 1, true, false);
					destRow = (XSSFRow) destSheet.getRow(row + 1);
					if (destRow == null)
						destRow = (XSSFRow) destSheet.createRow(row + 1);
					Util.copyRow(destSheet, destSheet, srcRow, destSheet.getRow(row + 1), styleMap);
					destSheet.getRow(row + 1).setHeight(srcRow.getHeight());
				}
				row = row + 1;
			}
			// row = row + 1;
			srcRow = (XSSFRow) destSheet.getRow(row);
			column = 4;
			srcRow.getCell(column).setCellFormula("SUM(E20:G" + (row) + ")");
			column = column + 3;
			srcRow.getCell(column).setCellFormula("SUM(H20:J" + (row) + ")");
			column = column + 10;
			srcRow.getCell(column).setCellFormula("SUM(R20:T" + (row) + ")");
		} else
			row = row + 1;

		row_tds = row + 1;

		// II/ CHI PHÍ THU CƯỚC PHÁT SINH NGOÀI DANH SÁCH GIAO (2)

		row = row + 3;
		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 5;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NDS]));
		column = column + 4;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDS]) * 100);
		column = column + 2;

		// srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NDS])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDS]));
		totComissionPay = totComissionPay + Math.round(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NDS])
				* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDS]));

		srcRow.getCell(column).setCellFormula("ROUND(F" + (row + 1) + "*J" + (row + 1) + "%,0)");
		row_nds = row + 1;
		row = row + 4;
		// III/ CHI PHÍ THU CƯỚC NỢ ĐỌNG (3)
		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 1;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2]));
		column = column + 1;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));
		column = column + 3;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_NO_DONG_N]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN]));
		column = column + 1;
		// srcRow.getCell(column)
		// .setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_NO_DONG_N])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));
		srcRow.getCell(column)
				.setCellFormula("ROUND(B" + (row + 1) + "*C" + (row + 1) + "*F" + (row + 1) + "*H" + (row + 1) + ",0)");
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_NO_DONG_N1]));
		column = column + 2;
		srcRow.getCell(column).setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN1]));
		column = column + 1;
		// srcRow.getCell(column)
		// .setCellValue(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_NO_DONG_N1])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN1])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));
		srcRow.getCell(column)
				.setCellFormula("ROUND(B" + (row + 1) + "*C" + (row + 1) + "*K" + (row + 1) + "*M" + (row + 1) + ",0)");
		column = column + 2;

		// srcRow.getCell(column)
		// .setCellValue((Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NO_DONG_N])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN])
		// + Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TH_NO_DONG_N1])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN1]))
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
		// * Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));

		totComissionPay = totComissionPay + Math.round((Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_NO_DONG_N])
				* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN])
				+ Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.TM_NO_DONG_N1])
						* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.DG_NDN1]))
				* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HKH2])
				* Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.HSCL]));

		srcRow.getCell(column).setCellFormula("ROUND(I" + (row + 1) + "+N" + (row + 1) + ",0)");
		row_nd = row + 1;
		row = row + 2;

		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 7;
		// srcRow.getCell(column)
		// .setCellValue(Math.round(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_TRONG_DS])
		// + Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NGOAI_DS])
		// +
		// Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NODONG])));
		srcRow.getCell(column).setCellFormula("ROUND(R" + (row_tds) + "+L" + (row_nds) + "+P" + (row_nd) + ",0)");
		row = row + 1;

		srcRow = (XSSFRow) destSheet.getRow(row);
		column = 3;

		// srcRow.getCell(column)
		// .setCellValue(tool.ReadNumber
		// .numberToString(Math.round(Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_TRONG_DS])
		// + Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NGOAI_DS])
		// +
		// Double.parseDouble(hhtc.paramArray[HoaHongThuCuoc.CHIPHI_NODONG]))));

		srcRow.getCell(column).setCellValue(tool.ReadNumber.numberToString(totComissionPay));

	}

	@Override
	protected Workbook createWorkbook() {
		File file = new File(System.getProperty("user.dir") + File.separator + "template" + File.separator
				+ "hoa_hong_thu_cuoc" + OracleBase.ct + ".xlsx");
		System.out.println("Final filepath : " + file.getAbsolutePath());

		try {
			return new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}