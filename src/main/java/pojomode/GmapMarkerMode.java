package pojomode;

import pojo.GmapMarker;

@SuppressWarnings("serial")
public class GmapMarkerMode extends ExceptionMode implements java.io.Serializable {
	private GmapMarker pojo;

	public void setPojo(GmapMarker pojo) {
		this.pojo = pojo;
	}

	public GmapMarker getPojo() {
		return pojo;
	}

}