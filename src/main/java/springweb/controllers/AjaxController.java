package springweb.controllers;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.Mb6Program;
import pojo.SubReg;
import pojobase.interfaces.MsaleBase;

/**
 * 
 * @author Nguyễn Thành Thủy
 */

@Controller
public class AjaxController extends BaseController {
	public static SimpleDateFormat YYYY_MM_DD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat YYYYMMDD_FORMAT = new SimpleDateFormat("yyyyMMdd");
	public static SimpleDateFormat DD_MM_YYYY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	@Autowired
	protected MsaleBase msaleBase;

	static final String separator = ";";

	@RequestMapping(value = "/m6_programs", method = RequestMethod.POST)
	public @ResponseBody List<Mb6Program> getM6SalePrograms(HttpServletResponse response) {
		List<Mb6Program> pojoList = null;
		try {
			System.out.println("m6_programs");
			pojoList = msaleBase.getMb6Programs(0);

			// TODO Auto-generated catch block
			// logger.debug("Received request to show download page");
			return pojoList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return pojoList;
		}

	}

	@RequestMapping(value = "/m6_programaddsub", method = RequestMethod.POST)
	public @ResponseBody List<SubReg> addM6SaleProgramSubs(@RequestParam int pro_id, @RequestParam String month,
			@RequestParam String pre_isdn_list, @RequestParam String pos_isdn_list, HttpServletRequest request)
					throws Exception {
		String user_name = getUserLoginName("/m6_programaddsub(pro_id:" + pro_id + ", month:" + month
				+ ", pre_isdn_list:" + pre_isdn_list + ", pos_isdn_list:" + pos_isdn_list + ")", request);
		if (user_name == null)
			throw new Exception("Not logged");
		else {
			System.out.println("pro_id: " + String.valueOf(pro_id));
			System.out.println("month: " + month);
			// System.out.println("pre_isdn_list: " + pre_isdn_list);

			pre_isdn_list = pre_isdn_list.replaceAll("\n", separator).replaceAll(",", separator).replaceAll(" ", "");
			pos_isdn_list = pos_isdn_list.replaceAll("\n", separator).replaceAll(",", separator).replaceAll(" ", "");
			String[] preSubArray = pre_isdn_list.split(separator);

			String[] posSubArray = pos_isdn_list.split(separator);
			System.out.println("pos_isdn_list: " + pos_isdn_list);
			return msaleBase.regSubsToProgram(pro_id, 0, user_name, preSubArray, posSubArray, month);

		}

	}

}
