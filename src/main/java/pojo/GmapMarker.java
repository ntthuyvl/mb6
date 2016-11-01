package pojo;

/**
 * 
 * @author Nguyễn Thành Thủy
 * 
 */

@SuppressWarnings("serial")
public class GmapMarker implements java.io.Serializable {

	private String googelAddress; // kinh_do,vi_do
	private String new_member;
	private String description;

	public void setGoogelAddress(String googelAddress) {
		this.googelAddress = googelAddress;
	}

	public String getGoogelAddress() {
		return googelAddress;
	}

	public String getNew_member() {
		return new_member;
	}

	public void setNew_member(String new_member) {
		this.new_member = new_member;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
