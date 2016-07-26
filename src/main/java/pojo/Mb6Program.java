package pojo;

import java.util.Date;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */

@SuppressWarnings("serial")
public class Mb6Program implements java.io.Serializable {
	private int id;
	private int pro_type;
	private String pro_name;
	private Date sta_date;
	private Date end_date;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setPro_type(int pro_type) {
		this.pro_type = pro_type;
	}

	public int getPro_type() {
		return pro_type;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setSta_date(Date sta_date) {
		this.sta_date = sta_date;
	}

	public Date getSta_date() {
		return sta_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public Date getEnd_date() {
		return end_date;
	}
}
