package springweb.modelandview;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractPOIExcelViewThreeWorkbookTypes extends AbstractView {
	// https://jira.spring.io/browse/SPR-6898

	/** The content type for an Excel response */
	private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	private static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel";

	/**
	 * Default Constructor. Sets the content type of the view for excel files.
	 */
	public AbstractPOIExcelViewThreeWorkbookTypes() {
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	/**
	 * Renders the Excel view, given the specified model.
	 */
	@Override
	protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Workbook workbook = createWorkbook();

		if (workbook instanceof XSSFWorkbook || workbook instanceof SXSSFWorkbook) {
			setContentType(CONTENT_TYPE_XLSX);
		} else {
			setContentType(CONTENT_TYPE_XLS);
		}

		buildExcelDocument(model, workbook, request, response);

		// Set the content type.
		response.setContentType(getContentType());

		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		out.flush();
		workbook.write(out);
		out.flush();
		if (workbook instanceof SXSSFWorkbook) {
			((SXSSFWorkbook) workbook).dispose();
		}

		// Don't close the stream - we didn't open it, so let the container
		// handle it.
		// http://stackoverflow.com/questions/1829784/should-i-close-the-servlet-outputstream
	}

	/**
	 * Subclasses must implement this method to create an Excel Workbook.
	 * HSSFWorkbook, XSSFWorkbook and SXSSFWorkbook are all possible formats.
	 */
	protected abstract Workbook createWorkbook();

	/**
	 * Subclasses must implement this method to create an Excel HSSFWorkbook
	 * document, given the model.
	 * 
	 * @param model
	 *            the model Map
	 * @param workbook
	 *            the Excel workbook to complete
	 * @param request
	 *            in case we need locale etc. Shouldn't look at attributes.
	 * @param response
	 *            in case we need to set cookies. Shouldn't write to it.
	 */
	protected abstract void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

}
