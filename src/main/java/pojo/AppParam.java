package pojo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

//import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;

public class AppParam {
	public static SimpleDateFormat DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static SimpleDateFormat YYYY_MM_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static int ALL = 0, ISNULL = -1;
	public static String STR_ALL = "0", STR_ISNULL = "-1";

	public static Calendar getVnCalendar() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT + 7"));
		return cal;
	}

	// public static Date getFirstDayOfLastWeek() {
	// Calendar cal = Calendar.getInstance();
	// cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
	// cal.setTime(DateUtils.truncate(cal.getTime(), Calendar.DATE));
	// cal.add(Calendar.DATE, -6);
	// return cal.getTime();
	// }

	public static DateTime getVnNow() {
		DateTimeZone zone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+7:00"));
		Chronology gregorianJuian = GJChronology.getInstance(zone);
		DateTime dt = new DateTime();
		// DateTime dtVN = dt.withChronology(gregorianJuian);
		return dt.withChronology(gregorianJuian);
	}

	// public static Date getLastDayOfWeek() {
	// Calendar cal = Calendar.getInstance();
	// cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
	// cal.setTime(DateUtils.truncate(cal.getTime(), Calendar.DATE));
	// cal.add(Calendar.DATE, +8);
	// return cal.getTime();
	// }

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}