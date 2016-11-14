package springweb.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller is used to provide functionality for the login page.
 * 
 * @author Mularien
 */
@Controller
public class LoginLogoutController extends BaseController {

	@RequestMapping(method = RequestMethod.GET, value = "/login")
	public String home(HttpServletRequest request) {
		return "/login";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/login-error")
	public String loginError(ModelMap model) {
		model.addAttribute("errorDetails", "Đăng nhập lỗi");
		return "/login";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
	public String accessDenied(ModelMap model, HttpServletRequest request) {
		AccessDeniedException ex = (AccessDeniedException) request.getAttribute("SPRING_SECURITY_403_EXCEPTION");
		StringWriter sw = new StringWriter();
		model.addAttribute("errorDetails", ex.getMessage());
		ex.printStackTrace(new PrintWriter(sw));
		model.addAttribute("errorTrace", sw.toString());
		return "/accessdenied";
	}
}
