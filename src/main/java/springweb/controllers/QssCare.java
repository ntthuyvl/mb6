package springweb.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import pojomode.ExceptionMode;

/**
 * 
 * @author Nguyễn Thành Thủy
 */

@Controller
public class QssCare extends BaseController {

	@RequestMapping(value = "/care", method = RequestMethod.GET)
	public String getCare(ModelMap model, HttpServletRequest request) {
		return "/care/welcome";
	}

	@RequestMapping(value = "/care/lncc_roimang/actlist", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> actlist(@RequestParam String json, HttpServletResponse response,
			HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".actlist" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getLnccActList(user_name, json);

		} catch (Exception e) {
			return getError(e);
		}

	}

	@RequestMapping(value = "/care/lncc_roimang/resultdetailnw", method = RequestMethod.GET)
	public ModelAndView resultdetailnw(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".resultdetailnw" + json, request);
		JSONObject jsonObject = new JSONObject(json);
		String from_date = String.valueOf(jsonObject.get("from_date")),
				to_date = String.valueOf(jsonObject.get("to_date")), area = String.valueOf(jsonObject.get("area")),
				column = String.valueOf(jsonObject.get("column"));
		String province_clause = "";

		if (area != null && !area.equals(""))
			province_clause = area;
		else
			province_clause = "MB6";

		List<Map<String, String>> list = msaleBase.lncc_result_detail(user_name, json);
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		model.getModel().put("file_name",
				column + "_" + province_clause + from_date.replace("-", "") + "_" + to_date.replace("-", "") + ".xlsx");
		// model.getModel().put("file_name", json.replaceAll("{",
		// "").replaceAll("}", "").replaceAll(" ", "")
		// .replaceAll("\"", "").replaceAll(",", "").replaceAll(":",
		// "").replaceAll("-", "") + ".xls");
		return model;
	}

	@RequestMapping(value = "/care/lncc_roimang/ketqua", method = RequestMethod.GET)
	public String lncc_ketqua(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".lncc_ketqua", request);
			if (!user_name.equals("anonymousUser")) {
				Date to_date = new DateTime().toDate();
				Date from_date = new DateTime().plusDays(-7).toDate();
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				return "/care/lncc_roimang/ketqua";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/care/lncc_roimang/ketqua", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> lncc_ketqua(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".lncc_ketqua" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.lncc_ketqua(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/lncc_roimang/list", method = RequestMethod.GET)
	public String getlncc_roimang(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getlncc_roimang", request);
			if (!user_name.equals("anonymousUser")) {
				return "/care/lncc_roimang/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/care/lncc_roimang/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getlncc_roimang(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getlncc_roimang" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.lncc_roimang(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/lncc_roimang/sub_care_history", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> sub_care_history(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".sub_care_history" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.sub_care_history(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/lncc_roimang/add_care_history", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> add_care_history(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".add_care_history" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.add_care_history(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/mobitv_roimang/ketqua", method = RequestMethod.GET)
	public String mobitv_ketqua(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".mobitv_ketqua", request);
			if (!user_name.equals("anonymousUser")) {
				Date to_date = new DateTime().toDate();
				Date from_date = new DateTime().plusDays(-7).toDate();
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				return "/care/mobitv_roimang/ketqua";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/care/mobitv_roimang/ketqua", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> mobitv_ketqua(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".lncc_ketqua" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.mobitv_ketqua(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/mobitv/add_care_history", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> add_mobitv_care_history(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".add_mobitv_care_history" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.add_mobitv_care_history(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/mobitv/sub_care_history", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> mobitv_care_history(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".mobitv_care_history" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.mobitv_care_history(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/mobitv/list", method = RequestMethod.GET)
	public String getmobitv_roimang(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getmobitv_roimang", request);
			if (!user_name.equals("anonymousUser")) {
				return "/care/mobitv/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/care/mobitv/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getmobitv_roimang(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getmobitv_roimang" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.mobitv_roimang(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/mobitv/import", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> importFromUploadFile(MultipartHttpServletRequest request) {

		String json = request.getParameter("json");
		// @RequestPart("json") String json,
		// @RequestPart("file") MultipartFile file
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".importFromUploadFile" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.importFromUploadFile(user_name, json, request);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/hsrm/list", method = RequestMethod.GET)
	public String getHsrmList(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getHsrmList", request);
			if (!user_name.equals("anonymousUser")) {
				model.put("vlr3k3dLatday", msaleBase.getVlr3k3dLatday());
				return "/care/hsrm/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/care/hsrm/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getCareHsrm(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getCareHsrm" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.CareHsrm(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/hsrm/no3k3d", method = RequestMethod.GET)
	public ModelAndView getCareHsrmNo3k3d(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getCareHsrmNo3k3d" + json, request);
		List<Map<String, String>> no3k3d = msaleBase.getNo3k3d(user_name, json);
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", no3k3d);
		model.getModel().put("file_name", "No3k3d.xlsx");
		return model;
	}

	@RequestMapping(value = "/*/tttb/listdetailnw", method = RequestMethod.GET)
	public ModelAndView getTinhTrangThueBaoDetail(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTinhTrangThueBaoDetail" + json,
				request);
		JSONObject jsonObject = new JSONObject(json);
		String from_date = String.valueOf(jsonObject.get("from_date")),
				to_date = String.valueOf(jsonObject.get("to_date")), area = String.valueOf(jsonObject.get("area"));
		String app = String.valueOf(jsonObject.get("app"));
		String type = String.valueOf(jsonObject.get("type"));
		String file_name;
		String province_clause = "";
		if (area != null && !area.equals(""))
			province_clause = area;
		else
			province_clause = "MB6";

		if (app.equals("1")) {
			String sub_type = String.valueOf(jsonObject.get("sub_type"));
			file_name = "cttbkhdn_" + type + "_" + sub_type + "_" + province_clause + "_" + from_date.replace("-", "")
					+ "_" + to_date.replace("-", "") + ".xlsx";
		} else {
			String act_status = String.valueOf(jsonObject.get("act_status"));
			String act_status_clause = "";
			if (act_status.equals("10"))
				act_status_clause = "_1C_OTHER";
			else if (act_status.equals("20"))
				act_status_clause = "_2C_OTHER";
			else if (act_status.equals("01"))
				act_status_clause = "_1C_DEPT";
			else if (act_status.equals("02"))
				act_status_clause = "_2C_DEPT";

			file_name = "cttb_" + type + "_" + province_clause + act_status_clause + from_date.replace("-", "") + "_"
					+ to_date.replace("-", "") + ".xlsx";
		}

		List<Map<String, String>> list = msaleBase.getTinhTrangThueBaoDetail(user_name, json);
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		;
		model.getModel().put("file_name", file_name);

		return model;
	}

	@RequestMapping(value = "/{app_path}/tttb/list", method = RequestMethod.GET)
	public String getTinhTrangThueBao(@PathVariable("app_path") String app_path, ModelMap model,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTinhTrangThueBao", request);
			if (!user_name.equals("anonymousUser")) {
				Date to_date = new DateTime().plusDays(-(new DateTime().getDayOfMonth())).plusMonths(-1).dayOfMonth()
						.withMaximumValue().toDate();
				Date from_date = new DateTime(to_date).plusDays(1 - (new DateTime(to_date).getDayOfYear())).toDate();
				int app = 3;
				if (app_path.equals("care"))
					app = 3;
				else if (app_path.equals("sale"))
					app = 2;
				else if (app_path.equals("khdn"))
					app = 1;
				if (app == 1) {
					from_date = new DateTime().dayOfMonth().withMinimumValue().toDate();
					to_date = new DateTime().plusDays(-1).toDate();
				}

				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				model.addAttribute("app", app);

				return "/care/tttb/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/*/tttb/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getTinhTrangThueBao(@RequestParam String json,
			HttpServletRequest request) throws Exception {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTinhTrangThueBao" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.getTinhTrangThueBao(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/chanmo/list", method = RequestMethod.GET)
	public String getChanMoMonthly(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChanMoMonthly", request);
			if (!user_name.equals("anonymousUser")) {
				Date act_month = new DateTime().dayOfMonth().withMinimumValue().toDate();
				model.addAttribute("act_month", act_month);
				return "/care/chanmo/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/care/chanmo/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getChanMoMonthly(@RequestParam String json,
			HttpServletRequest request) throws Exception {
		String user_name = getUserLoginName(
				this.getClass().getCanonicalName() + ".getChanMoMonthly @RequestParam" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.getChanMoMonthly(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/care/chanmo/chitiet", method = RequestMethod.GET)
	public ModelAndView getChanMoMonthlyDetail(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChanMoMonthlyDetail" + json,
				request);
		List<Map<String, String>> dataList = msaleBase.getChanMoMonthlyDetail(user_name, json);
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", dataList);
		model.getModel().put("file_name", "ChiTetChanMo.xlsx");
		return model;
	}

	@RequestMapping(value = "/care/khoso/donle", method = RequestMethod.GET)
	public String getStockIsdnDetail(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			getUserLoginName(this.getClass().getCanonicalName() + ".getStockIsdnDetail", request);
			return "/care/khoso/donle";

		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);

		}
	}

	@RequestMapping(value = "/care/khoso/donle", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getStockIsdnDetail(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getStockIsdnDetail" + json, request);
		try {

			return msaleBase.getStockIsdnDetail(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

}
