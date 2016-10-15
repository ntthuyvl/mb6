package springweb.controllers;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pojomode.ExceptionMode;

/**
 * 
 * @author Nguyễn Thành Thủy
 */

@Controller
public class QssKhdt extends BaseController {
	

	@RequestMapping(value = "/khdt", method = RequestMethod.GET)
	public String getSale(ModelMap model, HttpServletRequest request) {
		return "/khdt/welcome";
	}

	@RequestMapping(value = "/khdt/khsxkd/donvi", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> getDonVi(@RequestParam String json, HttpServletResponse response,
			HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getDonVi" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getDonViKhSxkd(user_name, json);

		} catch (Exception e) {
			return getError(e);
		}

	}

	@RequestMapping(value = "/khdt/danhgiachiphi/DanhGiaChiPhiTrucTiep", method = RequestMethod.GET)
	public String getDanhGiaChiPhiTrucTiep(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getDanhGiaChiPhiTrucTiep",
					request);

			return "/khdt/danhgiachiphi/DanhGiaChiPhiTrucTiep";

		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);

		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/getCpgtDesList", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> getCpgtDesList(@RequestParam String json,
			HttpServletResponse response, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getCpgtDesList" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getCpgtDesList(user_name, json);

		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/getChiPhiGianTiep", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getChiPhiGianTiep(@RequestParam String json,
			HttpServletResponse response, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChiPhiGianTiep" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getChiPhiGianTiep(user_name, json);

		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/getChiPhiGianTiepArea", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getChiPhiGianTiepArea(@RequestParam String json,
			HttpServletResponse response, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChiPhiGianTiepArea" + json,
				request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getChiPhiGianTiepArea(user_name, json);

		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/update_cpgt", method = RequestMethod.GET)
	public String getUpdateCpgt(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getUpdateCpgt", request);
			if (!user_name.equals("anonymousUser")) {
				return "/khdt/danhgiachiphi/update_cpgt";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/updateChiPhiGianTiep", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateChiPhiGianTiep(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".updateChiPhiGianTiep" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.updateChiPhiGianTiep(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/danhGiaChiPhiGianTiep", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> danhGiaChiPhiGianTiep(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".danhGiaChiPhiGianTiep" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.danhGiaChiPhiGianTiep(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/update_cptt", method = RequestMethod.GET)
	public String getUpdateCptt(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getUpdateCptt", request);
			if (!user_name.equals("anonymousUser")) {
				return "/khdt/danhgiachiphi/update_cptt";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/update_cptt", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getChiPhiTrucTiep(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getChiPhiTrucTiep" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getChiPhiTrucTiep(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/updateChiPhiTrucTiep", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateChiPhiTrucTiep(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".updateChiPhiTrucTiep" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.updateChiPhiTrucTiep(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/danhgiachiphi/danhgiaChiPhiTrucTiep", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> danhgiaChiPhiTrucTiep(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".danhgiaChiPhiTrucTiep" + json,
				request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.danhgiaChiPhiTrucTiep(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/khsxkd/list", method = RequestMethod.GET)
	public String getKhsxkd(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getKhsxkd", request);
			if (!user_name.equals("anonymousUser")) {
				return "/khdt/khsxkd/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/khdt/khsxkd/listdetail", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> getKhsxkd(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getKhsxkd" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.getKhsxkd(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/khdt/khsxkd/listdetailupdate", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> updateKhsxkd(@RequestParam String json, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".updateKhsxkd" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");

			return msaleBase.updateKhsxkd(user_name, json);
		} catch (Exception e) {
			System.out.println(e.toString());
			return getError(e);
		}
	}

}
