package springweb.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.springframework.web.servlet.ModelAndView;

import pojo.Mb6Program;
import pojo.MsaleReseller;
import pojomode.ExceptionMode;
import pojomode.Mb6ProgramMode;
import pojomode.MsaleResellerMode;

/**
 * 
 * @author Nguyễn Thành Thủy
 */

@Controller
public class QssSale extends BaseController {
	private Map<String, String> jsonDataMap = new TreeMap<String, String>();

	@RequestMapping(value = "/sale/welcome", method = RequestMethod.GET)
	public String getSaleWelcome(ModelMap model, HttpServletRequest request) {
		return "/sale/welcome";
	}

	@RequestMapping(value = "/sale", method = RequestMethod.GET)
	public String getSale(ModelMap model, HttpServletRequest request) {
		return "/sale/welcome";
	}

	@RequestMapping(value = "/khdn", method = RequestMethod.GET)
	public String getKhdn(ModelMap model, HttpServletRequest request) {
		return "/khdn/welcome";
	}

	@RequestMapping(value = "/sale/msale/gmap", method = RequestMethod.GET)
	public String getGoogleMap(ModelMap model, HttpServletRequest request) {
		MsaleResellerMode pojoMode = new MsaleResellerMode();
		List<MsaleReseller> pojoList;
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getGoogleMap", request);

		if (!user_name.equals("anonymousUser")) {
			try {
				pojoList = msaleBase.getReselers(user_name);
				Collection<MsaleResellerMode> pojoModeList = new ArrayList<MsaleResellerMode>();
				if (pojoList != null && pojoList.size() > 0)
					for (MsaleReseller pojo : pojoList) {
						if (pojo.getGoogelAddress() != null && !pojo.getGoogelAddress().equals("")) {
							pojoMode = new MsaleResellerMode();
							pojoMode.setPojo(pojo);
							pojoModeList.add(pojoMode);
						}
					}
				pojoMode = new MsaleResellerMode();
				model.addAttribute("pojoModeList", pojoModeList);
				model.addAttribute("pojoMode", pojoMode);
				return "/sale/msale/gmap";
			} catch (Exception e) {
				return HomeController.getError(e, pojoMode, model);
			}
		} else
			return "redirect:/login";
	}

	@RequestMapping(value = "/*/program/update_sub_pro", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> programAddSub(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".programAddSub" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.programUpdateSub(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/*/program/pro_analyze", method = RequestMethod.GET)
	public ModelAndView getProAnalyze(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getProAnalyze" + json, request);
		List<Map<String, String>> list = msaleBase.getProgramSub(user_name, json);
		JSONObject jsonObject = new JSONObject(json);
		String pro_name, type, from_date, to_date;

		pro_name = String.valueOf(jsonObject.get("pro_name"));
		type = String.valueOf(jsonObject.get("type"));

		try {
			from_date = String.valueOf(jsonObject.get("from_date")).replaceAll("-", "");
			to_date = String.valueOf(jsonObject.get("to_date")).replaceAll("-", "");

		} catch (Exception e1) {
			// id = "-1"; type = "-1";
			to_date = "";
			from_date = "";
		}
		if (to_date.equals(""))
			to_date = AjaxController.YYYYMMDD_FORMAT.format(new DateTime().plusDays(-2).toDate());
		if (type.equals("0"))
			type = "tt";
		else
			type = "ts";

		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		model.getModel().put("file_name", pro_name + "_" + type + "_" + from_date + "_" + to_date + ".xlsx");
		return model;
	}

	@RequestMapping(value = "/*/program/listsub", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getProgramSub(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getProgramSub" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getProgramSub(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/*/program/update", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateProgram(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		// java.lang.reflect.Method method;
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".updateProgram" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.updateProgram(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/*/program/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getProgram(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getProgram" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.getProgram(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/{app_path}/program/list", method = RequestMethod.GET)
	public String getProgram(ModelMap model, @PathVariable("app_path") String app_path, HttpServletRequest request)
			throws SQLException {
		// TODO Auto-generated method stub
		Mb6ProgramMode pojoMode = new Mb6ProgramMode();
		// String restOfTheUrl = (String)
		// request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getProgram", request);
			if (!user_name.equals("anonymousUser")) {
				List<Mb6Program> pojoList = msaleBase.getMb6Programs(0);
				Collection<Mb6ProgramMode> pojoModeList = new ArrayList<Mb6ProgramMode>();
				if (pojoList != null && pojoList.size() > 0)
					for (Mb6Program pojo : pojoList) {
						pojoMode = new Mb6ProgramMode();
						pojoMode.setPojo(pojo);
						pojoModeList.add(pojoMode);
					}
				model.addAttribute("pojoModeList", pojoModeList);
				model.addAttribute("pojoMode", pojoMode);

				if (app_path.equals("care"))
					model.addAttribute("pro_type", 1);
				else if (app_path.equals("sale"))
					model.addAttribute("pro_type", 2);
				else if (app_path.equals("khdn"))
					model.addAttribute("pro_type", 3);

				if (app_path.equals("care"))
					model.addAttribute("app", 3);
				else if (app_path.equals("sale"))
					model.addAttribute("app", 2);
				else if (app_path.equals("khdn"))
					model.addAttribute("app", 1);

				model.addAttribute("app_path", app_path);

				return "/sale/program/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/sale/msale/list", method = RequestMethod.GET)
	public String getMsaleResellerList(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getMsaleResellerList", request);
			if (!user_name.equals("anonymousUser")) {
				// Date from_date = new DateTime().plusDays(-2).toDate();
				// Date to_date = new DateTime().plusDays(-1).toDate();
				// model.addAttribute("from_date", from_date);
				// model.addAttribute("to_date", to_date);
				return "/sale/msale/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/sale/msale/post", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getMsaleReseller(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getMsaleReseller", request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getMsaleReseller(user_name);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/sale/chatluongdonhang/list", method = RequestMethod.GET)
	public String getChatLuongDonHang(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChatLuongDonHang", request);
			if (!user_name.equals("anonymousUser")) {
				Date from_date = new DateTime().plusDays(-10).dayOfMonth().withMinimumValue().toDate();
				Date to_date = new DateTime().plusDays(-10).dayOfMonth().withMaximumValue().toDate();
				if (to_date.after(new DateTime().plusDays(-2).toDate()))
					to_date = new DateTime().plusDays(-2).toDate();
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				return "/sale/chatluongdonhang/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/{app_path}/thongtintrathuong/list", method = RequestMethod.GET)
	public String getThongTinTraThuong(@PathVariable("app_path") String app_path, ModelMap model,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getThongTinTraThuong", request);
			if (!user_name.equals("anonymousUser")) {
				Date from_date = new DateTime().plusDays(-15).toDate();
				Date to_date = new DateTime().toDate();
				model.addAttribute("app_path", app_path);
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);

				if (app_path.equals("care"))
					model.addAttribute("app", 3);
				else if (app_path.equals("sale"))
					model.addAttribute("app", 2);
				else if (app_path.equals("khdn"))
					model.addAttribute("app", 1);

				return "/sale/thongtintrathuong/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/sale/thongtintrathuong/trathuong", method = RequestMethod.GET)
	public ModelAndView getThongTinTraThuong(HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getThongTinTraThuong", request);
		List<Map<String, String>> list = msaleBase.getThongTinTraThuong(user_name,
				jsonDataMap.get(request.getSession().getId()));
		jsonDataMap.remove(request.getSession().getId());
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		model.getModel().put("file_name", "thongtintrathuong" + request.getSession().getId() + ".xlsx");
		return model;
	}

	@RequestMapping(value = "/sale/thongtintrathuong/trathuong", method = RequestMethod.POST)
	public @ResponseBody String getThongTinTraThuongPost(@RequestParam String json, HttpServletRequest request) {
		getUserLoginName(this.getClass().getCanonicalName() + ".getThongTinTraThuongPost" + json, request);
		jsonDataMap.put(request.getSession().getId(), json);
		return "OK";
	}

	@RequestMapping(value = "/sale/cell/list", method = RequestMethod.GET)
	public String chiTietCell(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".chiTietCell", request);
			if (!user_name.equals("anonymousUser")) {
				Date from_date = new DateTime().plusDays(-9).toDate();
				Date to_date = new DateTime().plusDays(-2).toDate();
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				return "/sale/cell/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/sale/cell/get_report", method = RequestMethod.GET)
	public ModelAndView chiTietCell(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".chiTietCell" + json, request);
		List<Map<String, String>> list = msaleBase.chiTietCell(user_name, json);
		JSONObject jsonObject = new JSONObject(json);
		String tu_ngay = String.valueOf(jsonObject.get("tu_ngay"));
		String den_ngay = String.valueOf(jsonObject.get("den_ngay"));
		String rptype = String.valueOf(jsonObject.get("rptype"));
		if (rptype.equals("0"))
			rptype = "ChiTiet";
		else
			rptype = "TongHop";

		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		model.getModel().put("file_name",
				"TongHop" + tu_ngay.replaceAll("-", "") + "_" + den_ngay.replaceAll("-", "") + ".xlsx");
		return model;

	}

	@RequestMapping(value = "/{app_path}/chatluongthuebao/list", method = RequestMethod.GET)
	public String getChatLuongThueBao(@PathVariable("app_path") String app_path, ModelMap model,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChatLuongThueBao", request);
			if (!user_name.equals("anonymousUser")) {
				Date from_date = new DateTime().plusDays(-10).dayOfMonth().withMinimumValue().toDate();
				Date to_date = new DateTime().plusDays(-10).dayOfMonth().withMaximumValue().toDate();
				if (to_date.after(new DateTime().plusDays(-2).toDate()))
					to_date = new DateTime().plusDays(-2).toDate();
				model.addAttribute("app_path", app_path);
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);

				if (app_path.equals("care"))
					model.addAttribute("app", 3);
				else if (app_path.equals("sale"))
					model.addAttribute("app", 2);
				else if (app_path.equals("khdn"))
					model.addAttribute("app", 1);

				return "/sale/chatluongthuebao/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/sale/chatluongdonhang/danhgia", method = RequestMethod.GET)
	public ModelAndView getChatLuongDonHang(HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChatLuongDonHang", request);
		List<Map<String, String>> list = msaleBase.getChatLuongDonHang(user_name,
				jsonDataMap.get(request.getSession().getId()));
		jsonDataMap.remove(request.getSession().getId());
		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		model.getModel().put("file_name", "chatluong_donhang" + request.getSession().getId() + ".xlsx");
		return model;
	}

	@RequestMapping(value = "/sale/chatluongdonhang/danhgia", method = RequestMethod.POST)
	public @ResponseBody String getChatLuongDonHangPost(@RequestParam String json, HttpServletRequest request) {
		getUserLoginName(this.getClass().getCanonicalName() + ".getChatLuongDonHangPost" + json, request);
		jsonDataMap.put(request.getSession().getId(), json);
		return "OK";
	}

	@RequestMapping(value = "/sale/msale/listdetail", method = RequestMethod.GET)
	public String getMsaleResellerDetailNewWin(ModelMap model, @RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getMsaleResellerDetailNewWin" + json,
				request);
		if (!user_name.equals("anonymousUser")) {
			try {
				JSONObject jsonObject = new JSONObject(json);
				String district = null, province = null, chanel = null;
				district = String.valueOf(jsonObject.get("district"));
				province = String.valueOf(jsonObject.get("province"));
				chanel = String.valueOf(jsonObject.get("chanel"));
				model.addAttribute("province", province);
				model.addAttribute("district", district);
				model.addAttribute("chanel", chanel);
				return "/sale/msale/listdetail";
			} catch (Exception e) {
				ExceptionMode pojoMode = new ExceptionMode();
				return HomeController.getError(e, pojoMode, model);
			}
		} else
			return "redirect:/login";
	}

	@RequestMapping(value = "/sale/msale/listdetailupdate", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateMsaleResellerDetail(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".updateMsaleResellerDetail" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.updateMsaleResellerDetail(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/district", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getDistrict(@RequestParam String province,
			HttpServletRequest request) throws Exception {
		String user_name = getUserLoginName(
				this.getClass().getCanonicalName() + ".getDistrict" + "@RequestParam province:" + province + ")",
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getDistrict(user_name, province);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/account", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getAccount(@RequestParam String json, HttpServletRequest request)
			throws Exception {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getAccount" + json + ")", request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getAccount(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/accountupdate", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateAccount(@RequestParam String json, HttpServletRequest request)
			throws Exception {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".updateAccount" + json + ")",
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.updateAccount(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/{app_id}/admin/list", method = RequestMethod.GET)
	public String getAdmin(@PathVariable("app_id") int app_id, ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getAdmin", request);
			if (!user_name.equals("anonymousUser")) {
				model.addAttribute("app_id", app_id);
				return "/admin/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	/*
	 * @RequestMapping(value = "/khdn/admin/list", method = RequestMethod.GET)
	 * public String getKhdnAdmin(ModelMap model, HttpServletRequest request) {
	 * // TODO Auto-generated method stub try { String user_name =
	 * getUserLoginName(this.getClass().getCanonicalName() + ".getKhdnAdmin",
	 * request); if (!user_name.equals("anonymousUser")) { return
	 * "/khdn/admin/list"; } else return "redirect:/login"; } catch (Exception
	 * e) { ExceptionMode pojoMode = new ExceptionMode(); return
	 * HomeController.getError(e, pojoMode, model); } }
	 */
	@RequestMapping(value = "/sale/trathuong/list", method = RequestMethod.GET)
	public String getTraThuongList(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTraThuong", request);
			if (!user_name.equals("anonymousUser")) {
				Date from_date = new DateTime().plusDays(-2).toDate();
				Date to_date = new DateTime().plusDays(-2).toDate();
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				return "/sale/trathuong/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/sale/trathuong/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getTraThuong(@RequestParam String json, HttpServletRequest request)
			throws Exception {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTraThuong @RequestParam" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.getTraThuong(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/sale/trathuong/listdetail", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getTraThuongDetail(@RequestParam String json,
			HttpServletRequest request) throws Exception {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTraThuongDetail" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.getTraThuongDetail(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdn/banhang/list", method = RequestMethod.GET)
	public String getKhdnActiveList(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getKhdnActiveList", request);

			if (!user_name.equals("anonymousUser")) {
				Date from_date = new DateTime().dayOfMonth().withMinimumValue().toDate();
				Date to_date = new DateTime().plusDays(-1).toDate();
				model.addAttribute("from_date", from_date);
				model.addAttribute("to_date", to_date);
				return "/khdn/banhang/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/khdn/banhang/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getKhdnActiveList(@RequestParam String from_date,
			@RequestParam String to_date, HttpServletResponse response, HttpServletRequest request) throws Exception {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getKhdnActiveList"
				+ "(@RequestParam from_date:" + from_date + ",@RequestParam to_date:" + to_date + ")", request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			Date dfrom_date, dto_date;
			if (from_date == null || from_date.equals("")) {
				dfrom_date = new DateTime().dayOfMonth().withMinimumValue().toDate();
				dto_date = new DateTime().plusDays(-1).toDate();
			} else {
				dfrom_date = AjaxController.YYYY_MM_DD_FORMAT.parse(from_date);
				dto_date = AjaxController.YYYY_MM_DD_FORMAT.parse(to_date);
			}

			return msaleBase.getKhdnNewActive(user_name, dfrom_date, dto_date);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdn/banhang/listdetail", method = RequestMethod.GET)
	public ModelAndView getKhdnActiveListDetail(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getKhdnActiveListDetail" + json,
				request);
		List<Map<String, String>> list = msaleBase.getKhdnNewActiveDetail(user_name, json);

		JSONObject jsonObject = new JSONObject(json);
		String from_date = String.valueOf(jsonObject.get("from_date")),
				to_date = String.valueOf(jsonObject.get("to_date")),
				province = String.valueOf(jsonObject.get("province")),
				sub_type = String.valueOf(jsonObject.get("sub_type"));

		ModelAndView model = new ModelAndView("ProAnalyzeExcel", "list", list);
		model.getModel().put("file_name",
				"KhdnNewActiveDetail" + "_" + sub_type + "_" + province + "_" + from_date + "_" + to_date + ".xlsx");
		return model;
	}

	@RequestMapping(value = "/sale/trathuong/bcth", method = RequestMethod.GET)
	public ModelAndView getTraThuongExcel(@RequestParam String json, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getTraThuongExcel" + json, request);

		JSONObject jsonObject = new JSONObject(json);
		String from_date, to_date;
		try {
			from_date = String.valueOf(jsonObject.get("from_date"));
			to_date = String.valueOf(jsonObject.get("to_date"));
		} catch (Exception e1) {
			// id = "-1"; type = "-1";
			to_date = "";
			from_date = "";
		}

		if (from_date.equals(""))
			from_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime().plusDays(-3).toDate());
		if (to_date.equals(""))
			to_date = AjaxController.YYYY_MM_DD_FORMAT.format(new DateTime().plusDays(-2).toDate());

		List<Map<String, String>> kichHoat = msaleBase.getTraThuongKichHoatTh(user_name, json),
				napThe = msaleBase.getTraThuongNapTheTh(user_name, json),
				psc = msaleBase.getTraThuongPscTh(user_name, json),
				combo = msaleBase.getTraThuongComboTh(from_date, to_date);

		ModelAndView model = new ModelAndView("TraThuongExcel");
		model.getModel().put("file_name",
				"trathuong_th_" + from_date.replaceAll("-", "") + "_" + to_date.replaceAll("-", "") + ".xlsx");

		model.getModel().put("from_date", from_date);
		model.getModel().put("to_date", to_date);

		model.getModel().put("kichHoat", kichHoat);
		model.getModel().put("napThe", napThe);
		model.getModel().put("psc", psc);
		model.getModel().put("combo", combo);
		return model;
	}
}
