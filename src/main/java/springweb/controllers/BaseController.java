package springweb.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import pojo.Account;
import pojobase.interfaces.MsaleBase;

public class BaseController {
	@Autowired
	protected MsaleBase msaleBase;
	protected Map<String, String> jsonDataMap = new TreeMap<String, String>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	List<Map<String, String>> getError(Exception e) {
		// TODO Auto-generated catch block
		List result = new ArrayList<Map<String, String>>();
		Map error = new HashMap<String, String>();
		error.put("error", e.toString());
		result.add(error);
		e.printStackTrace();
		return result;
	}

	protected Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public boolean getShowLoginLink() {
		for (GrantedAuthority authority : getAuthentication().getAuthorities()) {
			if (authority.getAuthority().equals("ROLE_USER")) {
				return false;
			}
		}
		return true;
	}

	public static Object getDepartment(HttpServletRequest request) {
		try {
			return request.getSession().getAttribute("department");
		} catch (Exception e) {
			return null;
		}
	}

	protected Account getRequestUser(HttpServletRequest request) {
		try {
			return (Account) request.getSession().getAttribute("user");
		} catch (Exception e) {
			return null;
		}
	}

	protected String returnLogin(HttpServletRequest request) {
		setSessionVariable(request, "destlink", request.getPathInfo());
		return "redirect:/login";
	}

	protected void setSessionVariable(HttpServletRequest request, String stringVariableName, Object variableValue) {
		request.getSession().setAttribute(stringVariableName, variableValue);
	}

	@ModelAttribute("user_logged_name")
	public String getUserLoginName(String function, HttpServletRequest request) {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			System.out.println(new Date().toString() + " " + principal.toString() + "@" + request.getRemoteAddr() + " "
					+ request.getHeader("User-Agent") + " " + function);
			// if (!principal.toString().equals("anonymousUser") ||
			// function.equals("springweb.controllers.QssCare.getStockIsdnDetail"))
			msaleBase.addLog(principal.toString() + "@" + request.getRemoteAddr() + " " + function);
			return principal.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@ModelAttribute("user_roles")
	public List<String> getUserRoles() {
		try {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetails) {
				List<String> list = new ArrayList<String>();
				for (GrantedAuthority item : ((UserDetails) principal).getAuthorities())
					list.add(item.getAuthority());
				return list;

			} else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, String> getInsuraceCo() {
		return null;
	}

}