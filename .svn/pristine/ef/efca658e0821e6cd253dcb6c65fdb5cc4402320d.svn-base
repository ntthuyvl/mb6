package springweb.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ExceptionMessage extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String exceptionMessage = "Có lỗi ";

	public ExceptionMessage(String exceptionMessage) {
		this.setExceptionMessage(exceptionMessage);
	}

	public ExceptionMessage() {
		super();
	}

	public ExceptionMessage(Exception e) {
		super(e);
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

}
