package org.societies.webapp.model;

public class MessageForm {

	private String userId;
	private String cisBox;
	private String style;
	private String msg;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCisBox() {
		return cisBox;
	}
	public void setCisBox(String cisBox) {
		this.cisBox = cisBox;
	}
}
