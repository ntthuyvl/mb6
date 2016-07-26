package springweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**  

 * @author Nabeel Ali Memon  

 */

/**
 * 
 * This annotation makes it a Spring MVC controller.
 * 
 * No need to extend any abstract controller like old Spring way
 */

/*
 * *
 * 
 * Spring MVC 3 supports RESTful URL's out of the box.
 * 
 * Every request on 'integration' resource will be handled by this controller
 */
@Controller
public class AdmBaseController {

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// binder.setValidator(new ActorModeValidator());
	}

	@RequestMapping(value = "/admbase", method = RequestMethod.GET)
	public String getCreateForm(Model model) {
		return "admbase/create";
	}

	@RequestMapping(value = "/admbase/menu", method = RequestMethod.GET)
	public String getMenuForm(Model model) {
		return "admbase/menu";
	}

	@RequestMapping(value = "/admbase/detail", method = RequestMethod.GET)
	public String getDetailForm(Model model) {
		return "admbase/detail";
	}

}
