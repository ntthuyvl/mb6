import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.GJChronology;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pojo.AppParam;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;

public class Test {
	static GregorianCalendar gcal;
	// @Autowired
	// protected InsuranceFeeBase insuranceFeeBase;
	static ApplicationContext ctx;

	public static void main(String[] args) throws ParseException {
		// System.out.println("0903457979".substring(0, 1));
		// System.out.println("0903457979".substring(1));
		// ctx = new
		// ClassPathXmlApplicationContext("classpath:META-INF/spring/integration-data.xml");
		// Test test = new Test();
		// test.importInsuranceFeeDef();
		// System.out.println("0168".substring(0, "0168".length() - 2));
		// System.out.println(AppParam.getVnNow().withDayOfMonth(1)
		// .withMillisOfDay(0));
		/*
		 * String subsql = ""; for (int i = 0; i < 3000 / 1000; i++) { subsql =
		 * subsql +
		 * "union SELECT * FROM TABLE (SELECT nameTABLE(:departmentlist" + i +
		 * ") FROM DUAL) "; } subsql = subsql.substring(6);
		 * System.out.println(subsql); System.out.println(1 / 1000);
		 */
		// System.out.println(new DateTime().dayOfMonth().withMaximumValue());
		// System.out.println(new DateTime().monthOfYear().get());

		Date to_date = new DateTime().plusDays(-(new DateTime().getDayOfMonth())).plusMonths(-1).dayOfMonth()
				.withMaximumValue().toDate();

		Date from_date = new DateTime(to_date).plusDays(1 - (new DateTime(to_date).getDayOfYear())).toDate();

		System.out.println(from_date);
		// System.out.println(
		// pojo.AppParam.YYYY_MM_FORMAT.format(new
		// DateTime(pojo.AppParam.YYYY_MM_FORMAT.parse("2015-12-01"))
		// .dayOfMonth().withMaximumValue().toDate()));

	}

}