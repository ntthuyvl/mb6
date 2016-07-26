package pojomode;

import pojo.MsaleReseller;

@SuppressWarnings("serial")
public class MsaleResellerMode extends ExceptionMode implements
		java.io.Serializable {
	private MsaleReseller pojo;

	public void setPojo(MsaleReseller pojo) {
		this.pojo = pojo;
	}

	public MsaleReseller getPojo() {
		return pojo;
	}

}