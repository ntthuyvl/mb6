/**
 * 
 */
package springweb.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pojo.Account;
import pojo.ExceptionMessage;
import pojobase.interfaces.AccountBase;
import pojomode.ExceptionMode;

/**
 * This controller is used to provide functionality for the home page.
 * 
 * @author Mularien
 * 
 */
@Controller
public class HomeController extends BaseController {
	@Autowired
	protected AccountBase accountBase;

	@RequestMapping(method = RequestMethod.GET, value = "/welcomeframe")
	public String welcomeframe() {
		return "welcomeframe";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/homebuff")
	public String homebuff(HttpSession session, HttpServletRequest request) {
		String username = getUserLoginName("/homebuff", request);
		if (username != null) {
			Account user = accountBase.get(username);
			session.setAttribute("user", user);

			// PjicoDepartment department = pjicoDepartmentBase
			// .getByUsename(username);
			// session.setAttribute("department", department);
			//
			// System.out.println("department when login:" +
			// department.getName());
			String linkto = (String) session.getAttribute("destlink");
			// System.out.println("homebuff");
			if (linkto != null)
				return "redirect:" + linkto;
			return "welcome";
		} else
			return "redirect:/login";

	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String home(HttpServletRequest request) {
		return "redirect:/homebuff";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/home/{link}/")
	public String homelink(@PathVariable String link) {
		return "redirect:http://" + link;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/ql")
	public String homeql() {
		return "redirect:/manreseller/insurancefee/list";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/ak_forum")
	public String getForum() {
		System.out.println("ak_forum");
		return "redirect:../forum";
	}

	public static String getError(Exception e, ExceptionMode pojoMode, ModelMap model) {
		e.printStackTrace();
		ExceptionMessage exception = new ExceptionMessage(e);
		pojoMode.setException(exception);
		model.addAttribute("pojoMode", pojoMode);
		return "/error";
	}

	public static String getNotFoundError(ExceptionMode pojoMode, ModelMap model, String resource_id) {
		ExceptionMessage exception = new ExceptionMessage();
		exception.setExceptionMessage("Không có: " + resource_id + " trong hệ thống");
		pojoMode.setException(exception);
		model.addAttribute("pojoMode", pojoMode);
		return "/error";
	}

	protected Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
