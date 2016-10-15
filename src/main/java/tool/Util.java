package tool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
* Original version available from:
* http://www.coderanch.com/t/420958/open-source/Copying-sheet-excel-file-another
**/

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFEvaluationWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fss.util.AppException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import pojobase.oracle.OracleBase;

public class Util {

	public static String[] loadParamFromConfigFileToStringArray(String fileConfigName) throws Exception {
		String paramRecord;
		String[] v_paramRecords;
		BufferedReader configReader;
		File f;
		try {
			f = new File(fileConfigName);
			configReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			LinkedList<String> paramRecords = new LinkedList<String>();
			while ((paramRecord = configReader.readLine()) != null)
				paramRecords.add(paramRecord);
			v_paramRecords = new String[paramRecords.size()];
			paramRecords.toArray(v_paramRecords);
			return v_paramRecords;
		} catch (Exception e) {
			throw new AppException(e, e.getMessage());
		}
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * * Thay các biến ${p_} trong template bằng các giá trị tương ứng trong
	 * model. Thay các biến ${s[k,j]} bằng các giá trị tương ứng với recordSet
	 * trả về của câu lệnh sql[k] (j là cột tương ứng trong sql còn số dòng sẽ
	 * tự duyệt cho hết recordSet)
	 * 
	 * @param model
	 * @param sqlArray
	 *            *
	 * @param fileTemplate
	 * @param conn
	 * @param totRec
	 *            Tổng số bản ghi các câu lênh sql trả về
	 * @return
	 * @throws Exception
	 */
	public static Workbook exportDataExcel(String sqlParam, String[] sqlArray, File fileTemplate, Connection conn,
			long[] totRec) throws Exception {
		BufferedInputStream bi = new BufferedInputStream(new FileInputStream(fileTemplate));
		XSSFWorkbook workbook = new XSSFWorkbook(bi);
		try {
			for (int i = 0; i < totRec.length; i++)
				totRec[i] = 0;
			int sheetIndex = 0, sql_index = 0, col_index = 0, numberOfColumns, lastRowNum, rowNum;
			Integer[] sqlFirstRow = new Integer[sqlArray.length];
			String cellValue;
			String[] headerRow;
			ResultSetMetaData rsMetaData;
			XSSFSheet sheet = null;
			int sql_index_min = 0;
			int sql_index_max = 0;
			Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
			Row destRow;
			Row srcRow;
			Cell cell;
			boolean check = true;
			String[] record;
			String value;

			List<String[]> pojoList = new LinkedList<String[]>();
			Statement sttm = conn.createStatement();
			ResultSet rs;
			CellFormular formular;
			List<CellFormular> formularList;
			CellFormular[] formularArray;

			Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
			cfg.setEncoding(Locale.getDefault(), "UTF-8");
			cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
			Template t;
			Writer out;
			rs = sttm.executeQuery(sqlParam);
			rsMetaData = rs.getMetaData();
			Map<String, Object> model = new TreeMap<String, Object>();
			numberOfColumns = rsMetaData.getColumnCount();

			while (rs != null && rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					model.put("p_" + rsMetaData.getColumnName(i).toLowerCase(), rs.getString(i));
				}
			}

			while (check) {
				try {
					sheet = (XSSFSheet) workbook.getSheetAt(sheetIndex);
				} catch (Exception e) {
					check = false;
				}
				if (sheet != null) {
					sql_index = sql_index - 1;
					sql_index_min = sql_index + 1;
					lastRowNum = sheet.getLastRowNum();
					rowNum = sheet.getFirstRowNum();
					formularList = new LinkedList<CellFormular>();
					while (rowNum <= lastRowNum) {
						srcRow = sheet.getRow(rowNum);
						if (srcRow != null) {
							for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
								cell = srcRow.getCell(j);
								if (cell != null) {
									try {
										cellValue = srcRow.getCell(j).getStringCellValue().trim();
									} catch (Exception e) {
										cellValue = "";
									}

									if (cellValue.indexOf("${p_") >= 0) {
										t = new Template("templateName", new StringReader(cellValue), cfg);
										out = new StringWriter();
										t.process(model, out);
										value = out.toString();
										if (value != null && isNumeric(value)) {
											cell.setCellValue(Double.valueOf(value));
										} else
											cell.setCellValue(value);

									} else if (cellValue.startsWith("${s[") && cellValue.endsWith("]}")
											&& (cellValue.substring(cellValue.indexOf(",") + 1, cellValue.length() - 2)
													.equals("0"))) {
										sql_index_max = Integer
												.parseInt(cellValue.substring(4, cellValue.indexOf(",")));
										sqlFirstRow[sql_index_max] = rowNum;
									} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA
											&& !cell.getCellFormula().equals("")) {
										formular = new CellFormular();
										formular.setRow(rowNum);
										formular.setCol(j);
										formular.setFormula(cell.getCellFormula());
										formularList.add(formular);
									}
								}
							}
						}
						rowNum++;
					}
					formularArray = formularList.toArray(new CellFormular[formularList.size()]);
					for (sql_index = sql_index_min; sql_index <= sql_index_max; sql_index++) {
						rs = sttm.executeQuery(sqlArray[sql_index]);
						rsMetaData = rs.getMetaData();
						numberOfColumns = rsMetaData.getColumnCount();
						headerRow = new String[numberOfColumns];
						for (int i = 1; i <= numberOfColumns; i++) {
							headerRow[i - 1] = rsMetaData.getColumnName(i);
						}
						rowNum = sqlFirstRow[sql_index];

						pojoList = new LinkedList<String[]>();

						rsMetaData = rs.getMetaData();
						numberOfColumns = rsMetaData.getColumnCount();
						headerRow = new String[numberOfColumns];
						for (int i = 1; i <= numberOfColumns; i++) {
							headerRow[i - 1] = rsMetaData.getColumnName(i);
						}
						while (rs != null && rs.next()) {
							record = new String[numberOfColumns];
							for (int i = 1; i < numberOfColumns + 1; i++) {
								record[i - 1] = rs.getString(i);
							}
							pojoList.add(record);
						}

						int k = pojoList.size();
						for (String[] objs : pojoList) {
							k--;
							srcRow = sheet.getRow(rowNum);
							if (k > 0) {
								for (int i = 0; i < formularArray.length; i++) {
									if (formularArray[i].getLastRowRef(sheet) >= rowNum) {
										formularArray[i]
												.setFormula(getNewRefrenceFormula(sheet, formularArray[i].getFormula(),
														rowNum, formularArray[i].getRow(), formularArray[i].getCol(),
														formularArray[i].getRow() + 1, formularArray[i].getCol()));

										if (k > 1 && sql_index + 1 < sqlFirstRow.length
												&& sqlFirstRow[sql_index + 1] != null
												&& formularArray[i].getRow() >= sqlFirstRow[sql_index + 1] + 1
												&& formularArray[i].getFormula().indexOf("$") > 0) {
											formularArray[i]
													.setFormula(increaseStaticAddress(formularArray[i].getFormula()));

										}

									}
									if (formularArray[i].getRow() >= rowNum)
										formularArray[i].setRow(formularArray[i].getRow() + 1);

								}
								sheet.shiftRows(rowNum + 1, lastRowNum, 1, true, false);
								destRow = (XSSFRow) sheet.getRow(rowNum + 1);
								if (destRow == null)
									destRow = (XSSFRow) sheet.createRow(rowNum + 1);
								Util.copyRow(sheet, sheet, srcRow, destRow, styleMap);
								// Tăng địa chỉ của row đầu tiên tương ứng với
								// các sql sau lên 1
								for (int i = sql_index_min + 1; i <= sql_index_max; i++) {
									sqlFirstRow[i] = sqlFirstRow[i] + 1;
								}
							}

							lastRowNum++;
							totRec[sql_index]++;
							totRec[totRec.length - 1]++;
							for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
								cell = srcRow.getCell(j);
								if (cell != null) {
									try {
										cellValue = cell.getStringCellValue().trim();
									} catch (Exception e) {
										cellValue = "";
									}
									if (cellValue.startsWith("${s[") && cellValue.endsWith("]}")) {
										col_index = Integer.parseInt(cellValue.substring(cellValue.indexOf(",") + 1,
												cellValue.length() - 2));
										value = objs[col_index];

										if (value != null && isNumeric(value) && !headerRow[col_index].equals("ISDN")
												&& !headerRow[col_index].equals("SIM_SERIAL")
												&& !headerRow[col_index].equals("IMSI")
												&& !headerRow[col_index].equals("SERIAL")) {
											cell.setCellValue(Double.valueOf(value));
										} else
											cell.setCellValue(value);
									} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
										cellValue = cell.getCellFormula();
										cell.setCellFormula(cell.getCellFormula());
									}
								}
							}
							rowNum++;

						}
					}
					for (int i = 0; i < formularArray.length; i++) {
						sheet.getRow(formularArray[i].getRow()).getCell(formularArray[i].getCol())
								.setCellFormula(formularArray[i].getFormula());
					}

				}
				sheetIndex++;
			}

			// co cac cau lenh sql can xuat ma ko co template

			sql_index_max = sql_index_max + 1;

			while (sql_index_max < sqlArray.length) {
				String sheet_name = "";
				rs = sttm.executeQuery(sqlArray[sql_index_max]);
				rsMetaData = rs.getMetaData();
				numberOfColumns = rsMetaData.getColumnCount();
				// int[] columType = new int[numberOfColumns + 1];
				// get the column names; column indexes start from 1
				TreeMap<String, String> headerRowMap = new TreeMap<String, String>();
				for (int i = 1; i <= numberOfColumns; i++) {
					headerRowMap.put(String.format("%03d", i), rsMetaData.getColumnName(i));
				}

				value = "";
				int sheetRow = 0;
				String k;
				try {
					sheet_name = sqlArray[sql_index_max].substring(sqlArray[sql_index_max].indexOf("/***") + 4,
							sqlArray[sql_index_max].indexOf("***/")).replaceAll(" ", "");
				} catch (Exception e) {
					sheet_name = "data_sql" + sql_index_max;

				}

				XSSFSheet excelSheet = (XSSFSheet) workbook.createSheet(sheet_name);
				XSSFRow excelHeader = (XSSFRow) excelSheet.createRow(0);
				int location = 0;
				for (String k1 : headerRowMap.keySet()) {
					excelHeader.createCell(location).setCellValue(headerRowMap.get(k1));
					location++;
				}

				while (rs != null && rs.next()) {
					totRec[totRec.length - 1]++;
					totRec[sql_index_max]++;
					sheetRow++;
					XSSFRow excelRow = (XSSFRow) excelSheet.createRow(sheetRow);
					location = 0;
					for (int i = 1; i <= numberOfColumns; i++) {
						k = String.format("%03d", i);
						value = rs.getString(i);
						excelRow.createCell(location);
						if (value != null && Util.isNumeric(value) && !headerRowMap.get(k).equals("ISDN")
								&& !headerRowMap.get(k).equals("SIM_SERIAL") && !headerRowMap.get(k).equals("IMSI")
								&& !headerRowMap.get(k).equals("SERIAL")) {
							excelRow.getCell(location).setCellType(XSSFCell.CELL_TYPE_NUMERIC);
							excelRow.getCell(location).setCellValue(Double.valueOf(value));
						} else
							excelRow.getCell(location).setCellValue(value);
						location++;
					}
				}
				sql_index_max = sql_index_max + 1;
			}

			if (totRec[totRec.length - 1] > 0)
				return workbook;
			else
				return null;
		} finally {
			bi.close();
		}
	}

	/**
	 * @param newSheet
	 *            the sheet to create from the copy.
	 * @param sheet
	 *            the sheet to copy.
	 */
	public static void copySheets(Sheet newSheet, Sheet sheet) {
		copySheets(newSheet, sheet, true);
	}

	/**
	 * @param newSheet
	 *            the sheet to create from the copy.
	 * @param sheet
	 *            the sheet to copy.
	 * @param copyStyle
	 *            true copy the style.
	 */
	public static void copySheets(Sheet newSheet, Sheet sheet, boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, CellStyle> styleMap = (copyStyle) ? new HashMap<Integer, CellStyle>() : null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			Row srcRow = sheet.getRow(i);
			Row destRow = newSheet.createRow(i);
			if (srcRow != null) {
				copyRow(sheet, newSheet, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for (int i = 0; i <= maxColumnNum; i++) {
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
	}

	/**
	 * @param srcSheet
	 *            the sheet to copy.
	 * @param destSheet
	 *            the sheet to create.
	 * @param srcRow
	 *            the row to copy.
	 * @param destRow
	 *            the row to create.
	 * @param styleMap
	 *            -
	 */
	public static void copyRow(Sheet srcSheet, Sheet destSheet, Row srcRow, Row destRow,
			Map<Integer, CellStyle> styleMap) {
		// manage a list of merged zone in order to not insert two times a
		// merged zone
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();

		destRow.setHeight(srcRow.getHeight());
		// reckoning delta rows
		int deltaRows = destRow.getRowNum() - srcRow.getRowNum();
		// pour chaque row
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			Cell oldCell = srcRow.getCell(j); // ancienne cell
			Cell newCell = destRow.getCell(j); // new cell
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				// copy chaque cell
				copyCell(oldCell, newCell, styleMap);
				// copy les informations de fusion entre les cellules
				// System.out.println("row num: " + srcRow.getRowNum() + " ,
				// col: " + (short)oldCell.getColumnIndex());
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(),
						(short) oldCell.getColumnIndex());

				if (mergedRegion != null) {
					// System.out.println("Selected merged region: " +
					// mergedRegion.toString());
					CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow() + deltaRows,
							mergedRegion.getLastRow() + deltaRows, mergedRegion.getFirstColumn(),
							mergedRegion.getLastColumn());
					// System.out.println("New merged region: " +
					// newMergedRegion.toString());
					CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(newMergedRegion);
					if (isNewMergedRegion(wrapper, mergedRegions)) {
						mergedRegions.add(wrapper);
						try {
							destSheet.addMergedRegion(wrapper.range);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * @param oldCell
	 * @param newCell
	 * @param styleMap
	 */
	public static void copyCell(Cell oldCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
		if (styleMap != null) {
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {
				int stHashCode = oldCell.getCellStyle().hashCode();
				CellStyle newCellStyle = styleMap.get(stHashCode);
				if (newCellStyle == null) {
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		switch (oldCell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			newCell.setCellType(Cell.CELL_TYPE_BLANK);
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			copyFormula(oldCell.getSheet(), oldCell, newCell);
			break;
		default:
			break;
		}

	}

	/**
	 * Récupère les informations de fusion des cellules dans la sheet source
	 * pour les appliquer à la sheet destination... Récupère toutes les zones
	 * merged dans la sheet source et regarde pour chacune d'elle si elle se
	 * trouve dans la current row que nous traitons. Si oui, retourne l'objet
	 * CellRangeAddress.
	 * 
	 * @param sheet
	 *            the sheet containing the data.
	 * @param rowNum
	 *            the num of the row to copy.
	 * @param cellNum
	 *            the num of the cell to copy.
	 * @return the CellRangeAddress created.
	 */
	public static CellRangeAddress getMergedRegion(Sheet sheet, int rowNum, short cellNum) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum)) {
				return merged;
			}
		}
		return null;
	}

	/**
	 * Check that the merged region has been created in the destination sheet.
	 * 
	 * @param newMergedRegion
	 *            the merged region to copy or not in the destination sheet.
	 * @param mergedRegions
	 *            the list containing all the merged region.
	 * @return true if the merged region is already in the list or not.
	 */
	private static boolean isNewMergedRegion(CellRangeAddressWrapper newMergedRegion,
			Set<CellRangeAddressWrapper> mergedRegions) {
		return !mergedRegions.contains(newMergedRegion);
	}

	public static String increaseStaticAddress(String org_formula) {
		String str = org_formula;
		String formula = org_formula;
		str = str.replaceAll("[^0-9]+", " ");
		String[] rowArray = str.trim().split(" ");
		for (int i = 0; i < rowArray.length; i++) {
			formula = formula.replace("$" + rowArray[i], "$" + (Integer.parseInt(rowArray[i]) + 1));
		}
		// System.out.println("formula: " + formula);
		return formula;

	}

	public static String getNewRefrenceFormula(Sheet sheet, String org_formula, int move_row, int org_row,
			int org_column, int dest_row, int dest_column) {
		Cell org = sheet.getRow(org_row).getCell(org_column);
		// OracleBase.syslog(org_formula);
		if (org_formula == null || org_formula.equals("") || org == null || sheet == null
				|| org.getCellType() != Cell.CELL_TYPE_FORMULA)
			return "";
		if (org.isPartOfArrayFormulaGroup())
			return "org.isPartOfArrayFormulaGroup()";

		int shiftRows = dest_row - org_row;
		int shiftCols = dest_column - org_column;
		XSSFEvaluationWorkbook workbookWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) sheet.getWorkbook());

		String formula = org_formula;
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
				sheet.getWorkbook().getSheetIndex(sheet));
		for (Ptg ptg : ptgs) {
			if (ptg instanceof RefPtgBase) // base class for cell references
			{
				RefPtgBase ref = (RefPtgBase) ptg;
				if (ref.isColRelative())
					ref.setColumn(ref.getColumn() + shiftCols);
				if (ref.isRowRelative() && move_row <= ref.getRow())
					ref.setRow(ref.getRow() + shiftRows);
				OracleBase.syslog(String.valueOf(ref.getRow()));
			} else if (ptg instanceof AreaPtg) // base class for range
												// references
			{
				AreaPtg ref = (AreaPtg) ptg;
				// OracleBase.syslog(String.valueOf(ref.getFirstRow()));
				if (ref.isFirstColRelative())
					ref.setFirstColumn(ref.getFirstColumn() + shiftCols);
				if (ref.isLastColRelative())
					ref.setLastColumn(ref.getLastColumn() + shiftCols);
				if (ref.isFirstRowRelative() && move_row <= ref.getFirstRow())
					ref.setFirstRow(ref.getFirstRow() + shiftRows);
				if (ref.isLastRowRelative() && move_row <= ref.getLastRow())
					ref.setLastRow(ref.getLastRow() + shiftRows);
			}

		}

		formula = FormulaRenderer.toFormulaString(workbookWrapper, ptgs);
		return formula;
	}

	public static void copyFormula(Sheet sheet, Cell org, Cell dest) {
		if (org == null || dest == null || sheet == null || org.getCellType() != Cell.CELL_TYPE_FORMULA)
			return;
		if (org.isPartOfArrayFormulaGroup())
			return;
		String formula = org.getCellFormula();
		int shiftRows = dest.getRowIndex() - org.getRowIndex();
		int shiftCols = dest.getColumnIndex() - org.getColumnIndex();
		XSSFEvaluationWorkbook workbookWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) sheet.getWorkbook());
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
				sheet.getWorkbook().getSheetIndex(sheet));
		for (Ptg ptg : ptgs) {
			if (ptg instanceof RefPtgBase) // base class for cell references
			{
				RefPtgBase ref = (RefPtgBase) ptg;
				if (ref.isColRelative())
					ref.setColumn(ref.getColumn() + shiftCols);
				if (ref.isRowRelative())
					ref.setRow(ref.getRow() + shiftRows);
			} else if (ptg instanceof AreaPtg) // base class for range
												// references
			{
				AreaPtg ref = (AreaPtg) ptg;
				if (ref.isFirstColRelative())
					ref.setFirstColumn(ref.getFirstColumn() + shiftCols);
				if (ref.isLastColRelative())
					ref.setLastColumn(ref.getLastColumn() + shiftCols);
				if (ref.isFirstRowRelative())
					ref.setFirstRow(ref.getFirstRow() + shiftRows);
				if (ref.isLastRowRelative())
					ref.setLastRow(ref.getLastRow() + shiftRows);
			}
		}
		formula = FormulaRenderer.toFormulaString(workbookWrapper, ptgs);
		dest.setCellFormula(formula);
	}

}

class CellFormular implements Comparable<CellFormular> {
	private int row;
	private int col;
	private String formula;

	public int getLastRowRef(Sheet sheet) {

		XSSFEvaluationWorkbook workbookWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) sheet.getWorkbook());
		Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL,
				sheet.getWorkbook().getSheetIndex(sheet));
		int max_row = 0;
		for (Ptg ptg : ptgs) {
			if (ptg instanceof RefPtgBase) // base class for cell references
			{
				RefPtgBase ref = (RefPtgBase) ptg;
				max_row = ref.getRow();
			} else if (ptg instanceof AreaPtg) // base class for range
												// references
			{
				AreaPtg ref = (AreaPtg) ptg;
				// OracleBase.syslog(String.valueOf(ref.getFirstRow()));
				if (max_row < ref.getFirstRow())
					max_row = ref.getFirstRow();
				if (max_row < ref.getLastRow())
					max_row = ref.getLastRow();
			}
		}
		return max_row;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public int compareTo(CellFormular o) {
		// TODO Auto-generated method stub
		return formula.compareTo(o.getFormula());
	}

}

class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper> {

	public CellRangeAddress range;

	/**
	 * @param theRange
	 *            the CellRangeAddress object to wrap.
	 */
	public CellRangeAddressWrapper(CellRangeAddress theRange) {
		this.range = theRange;
	}

	/**
	 * @param o
	 *            the object to compare.
	 * @return -1 the current instance is prior to the object in parameter, 0:
	 *         equal, 1: after...
	 */
	public int compareTo(CellRangeAddressWrapper o) {

		if (range.getFirstColumn() < o.range.getFirstColumn() && range.getFirstRow() < o.range.getFirstRow()) {
			return -1;
		} else if (range.getFirstColumn() == o.range.getFirstColumn() && range.getFirstRow() == o.range.getFirstRow()) {
			return 0;
		} else {
			return 1;
		}

	}
}