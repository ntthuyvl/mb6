package springweb.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import pojo.HoaHongThuCuoc;
import pojobase.interfaces.MsaleBase;
import pojomode.ExceptionMode;

/**
 * 
 * @author Nguyễn Thành Thủy
 */

@Controller
public class QssPayment extends BaseController {
	private Map<String, String> jsonDataMap = new TreeMap<String, String>();

	@RequestMapping(value = "/ttcp", method = RequestMethod.GET)
	public String getSale(ModelMap model, HttpServletRequest request) {
		return "/ttcp/welcome";
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/list/{param}", method = RequestMethod.GET)
	public String getAgent(@PathVariable("param") int param, ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getAgent", request);
			if (!user_name.equals("anonymousUser")) {
				int bill_cycle_id;
				Date month;
				DateTime to_day = new DateTime();
				if (to_day.dayOfMonth().get() >= 3 && to_day.dayOfMonth().get() < 13) {
					bill_cycle_id = 1;
					month = to_day.plusMonths(-1).toDate();
				} else {
					month = to_day.toDate();
					bill_cycle_id = 11;
					// if (to_day.dayOfMonth().get() >= 13 &&
					// to_day.dayOfMonth().get() < 23)
					// bill_cycle_id = 11;
					//
					// else
					// bill_cycle_id = 21;
				}
				model.addAttribute("bill_cycle_id", bill_cycle_id);
				model.addAttribute("month", month);
				model.addAttribute("param", param);
				return "/ttcp/hoahongthucuoc/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/listdetail", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getAgent(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getAgent" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getAgent(user_name, json, 0);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/isclosed", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> isclosed(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".isclosed" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.hhtc_isclosed(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/close", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> close(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".close" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.hhtc_close(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/tonghop", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> tongHop(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".tongHop" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.tongHop(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/listdetailupdate/{param}", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateColectionDetail(@PathVariable("param") int param,
			@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(
				this.getClass().getCanonicalName() + ".updateColectionDetail" + param + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			if (param == 0)
				return msaleBase.updateAgentKH(user_name, json);
			else if (param == 1)
				return msaleBase.updateAgentParam(user_name, json);
			else
				return msaleBase.updateVungThuCuoc(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/xuat_bien_ban", method = RequestMethod.GET)
	public ModelAndView xuat_bien_ban(@RequestParam String json, HttpServletRequest request) throws SQLException {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".xuat_bien_ban" + json, request);

		JSONObject jsonObject = new JSONObject(json);
		String month, bill_cycle_id;

		month = String.valueOf(jsonObject.get("month"));
		bill_cycle_id = String.valueOf(jsonObject.get("bill_cycle_id"));
		String mbftinh = String.valueOf(jsonObject.get("mbftinh"));
		Connection cnn = msaleBase.getConnection();
		Map<String, HoaHongThuCuoc> hoaHongThuCuocMap = msaleBase.getHoaHongThuCuocList(user_name, json);
		ModelAndView model = new ModelAndView("hoa_hong_thu_cuoc");
		model.getModel().put("cnn", cnn);
		model.getModel().put("file_name", "hhtc_" + mbftinh + "_" + month + "ky_" + bill_cycle_id + ".xlsx");
		model.getModel().put("month", month);
		model.getModel().put("bill_cycle_id", bill_cycle_id);

		model.getModel().put("hoaHongThuCuocMap", hoaHongThuCuocMap);
		return model;
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/chitietthucuoc", method = RequestMethod.GET)
	public ModelAndView chitietthucuoc(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".chitietthucuoc" + json, request);

		JSONObject jsonObject = new JSONObject(json);
		String id, item, end_date;
		id = String.valueOf(jsonObject.get("id"));
		item = String.valueOf(jsonObject.get("item"));
		end_date = String.valueOf(jsonObject.get("end_date"));
		List<Map<String, String>> dataList = msaleBase.ChiTietThuCuoc(user_name, json);
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", dataList);
		model.getModel().put("file_name", "chitiet_thucuoc" + "_" + item + "_" + end_date + "_" + id + ".xlsx");
		return model;
	}

	@RequestMapping(value = "/ttcp/hoahongthucuoc/reports", method = RequestMethod.GET)
	public ModelAndView testBirt(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".testBirt" + json, request);
		ModelAndView model = new ModelAndView("birtView");
		return model;
	}

}
