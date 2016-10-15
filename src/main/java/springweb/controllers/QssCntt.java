package springweb.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.lowagie.text.pdf.codec.Base64.InputStream;

import pojobase.oracle.MsaleOracleBase;
import pojobase.oracle.OracleBase;
import pojomode.ExceptionMode;
import tool.Util;

/**
 * 
 * @author Nguyễn Thành Thủy
 */

@Controller
public class QssCntt extends BaseController {

	@RequestMapping(value = "/cntt/auto_report/configpaths", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> getconfigpaths(@RequestParam String json,
			HttpServletResponse response, HttpServletRequest request) {
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".getconfigpaths" + json, request);
		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			Map<String, String> pojo;
			pojo = new TreeMap<String, String>();
			List<Map<String, String>> pojoList = new LinkedList<Map<String, String>>();
			// put header
			pojo.put("000", "<th>STT");
			pojo.put("001", "<th>KEY");
			pojo.put("002", "<th>PATH");
			pojoList.add(pojo);
			String[] pathArray = Util.loadParamFromConfigFileToStringArray("tntservice.cfg");
			if (pathArray != null && pathArray.length > 0) {
				for (int i = 0; i < pathArray.length; i++) {
					pojo = new TreeMap<String, String>();
					pojo.put("000", "<td>" + i);
					pojo.put("001", "<td>" + i);
					pojo.put("002", "<td>" + pathArray[i]);
					pojoList.add(pojo);
				}
			}
			return pojoList;

		} catch (Exception e) {
			return getError(e);
		}

	}

	@RequestMapping(value = "/cntt/auto_report/list", method = RequestMethod.GET)
	public String get_auto_report(ModelMap model, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".get_auto_report", request);
			if (!user_name.equals("anonymousUser")) {

				return "/cntt/auto_report/list";
			} else
				return "redirect:/login";
		} catch (Exception e) {
			ExceptionMode pojoMode = new ExceptionMode();
			return HomeController.getError(e, pojoMode, model);
		}
	}

	@RequestMapping(value = "/cntt/auto_report/list", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> get_auto_report(@RequestParam String json,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".get_auto_report" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			JSONObject jsonObject = new JSONObject(json);
			String configpath = String.valueOf(jsonObject.get("configpath"));
			request.setAttribute("configpath", configpath);
			OracleBase.syslog("request.getAttribute: " + (String) request.getAttribute("configpath"));
			return msaleBase.get_auto_report(user_name, json);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/cntt/auto_report/add_modify_rp", method = RequestMethod.POST)
	public @ResponseBody List<Map<String, String>> add_modify_rp(MultipartHttpServletRequest request) {

		String json = request.getParameter("json");
		// @RequestPart("json") String json,
		// @RequestPart("file") MultipartFile file
		// TODO Auto-generated method stub
		String user_name = getUserLoginName(this.getClass().getCanonicalName() + ".add_modify_rp" + json, request);

		try {
			if (user_name.equals("anonymousUser"))
				throw new Exception("Not logged");
			return msaleBase.add_modify_rp(user_name, json, request);
		} catch (Exception e) {
			return getError(e);
		}
	}

	@RequestMapping(value = "/cntt/download/template/{file_name}/", method = RequestMethod.GET)
	@ResponseBody
	public void getFile(@PathVariable("file_name") String fileName, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			File download_file = new File(msaleBase.getTemplateDir(), fileName);
			OracleBase.syslog(download_file.getAbsolutePath());
			// return new FileSystemResource(download_file);
			FileInputStream is = new FileInputStream(download_file);
			// response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + download_file.getName());
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("IOError writing file to output stream");
		}

	}

	@RequestMapping(value = "/*/download/**/", method = RequestMethod.GET)
	@ResponseBody
	public void getFile(HttpServletRequest request, HttpServletResponse response) {
		try {

			String[] path_array = request.getPathInfo().split("/");
			File download_file = MsaleOracleBase.download_dir;
			for (int i = 3; i < path_array.length; i++)
				download_file = new File(download_file, path_array[i]);
			OracleBase.syslog(download_file.getAbsolutePath());

			// return new FileSystemResource(download_file);
			FileInputStream is = new FileInputStream(download_file);
			// response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + download_file.getName());
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("IOError writing file to output stream");
		}

	}

}
