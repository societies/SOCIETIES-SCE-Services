package org.societies.ext3p.nzone;  

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class DemoBean {
	public String getOutput() {
		return "It is " + new Date().toString();
	}
}
