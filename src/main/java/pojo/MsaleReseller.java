package pojo;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */

@SuppressWarnings("serial")
public class MsaleReseller implements java.io.Serializable {
	private String id;
	private String ten_cua_hang;
	private String dia_chi;
	private String so_ez_nhan_tien;
	private String googelAddress; // kinh_do,vi_do
	private String new_member;

	public void setGoogelAddress(String googelAddress) {
		this.googelAddress = googelAddress;
	}

	public String getGoogelAddress() {
		return googelAddress;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setTen_cua_hang(String ten_cua_hang) {
		this.ten_cua_hang = ten_cua_hang;
	}

	public String getTen_cua_hang() {
		return ten_cua_hang;
	}

	public void setDia_chi(String dia_chi) {
		this.dia_chi = dia_chi;
	}

	public String getDia_chi() {
		return dia_chi;
	}

	public void setSo_ez_nhan_tien(String so_ez_nhan_tien) {
		this.so_ez_nhan_tien = so_ez_nhan_tien;
	}

	public String getSo_ez_nhan_tien() {
		return so_ez_nhan_tien;
	}

	public String getNew_member() {
		return new_member;
	}

	public void setNew_member(String new_member) {
		this.new_member = new_member;
	}

}
