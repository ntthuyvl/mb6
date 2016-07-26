package springweb.controllers;

import net.sf.jasperreports.engine.JRDataSource;
import org.apache.log4j.Logger;
import org.krams.tutorial.dao.SalesDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles and retrieves download request
 */
@Controller
@RequestMapping("/main")
public class MainController {

	protected static Logger logger = Logger.getLogger("controller");

	/**
	 * Handles and retrieves the download page
	 * 
	 * @return the name of the JSP page
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getDownloadPage() {
		logger.debug("Received request to show download page");

		// Do your work here. Whatever you like
		// i.e call a custom service to do your business
		// Prepare a model to be used by the JSP page

		// This will resolve to /WEB-INF/jsp/downloadpage.jsp
		return "downloadpage";
	}

	/**
	 * Handles multi-format report requests
	 * 
	 * @param type
	 *            the format of the report, i.e pdf
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ModelAndView doSalesMultiReport(@RequestParam("type") String type,
			ModelAndView modelAndView, ModelMap model) {
		logger.debug("Received request to download multi report");

		// Retrieve our data from a custom data provider
		// Our data comes from a DAO layer
		SalesDAO dataprovider = new SalesDAO();

		// Assign the datasource to an instance of JRDataSource
		// JRDataSource is the datasource that Jasper understands
		// This is basically a wrapper to Java's collection classes
		JRDataSource datasource = dataprovider.getDataSource();

		// In order to use Spring's built-in Jasper support,
		// We are required to pass our datasource as a map parameter

		// Add our datasource parameter
		model.addAttribute("datasource", datasource);
		// Add the report format
		model.addAttribute("format", type);

		// multiReport is the View of our application
		// This is declared inside the /WEB-INF/jasper-views.xml
		modelAndView = new ModelAndView("multiReport", model);

		// Return the View and the Model combined
		return modelAndView;
	}
}
