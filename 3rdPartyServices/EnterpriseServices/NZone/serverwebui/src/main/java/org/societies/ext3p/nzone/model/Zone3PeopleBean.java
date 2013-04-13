package org.societies.ext3p.nzone.model;

import javax.faces.bean.ApplicationScoped;

import org.springframework.stereotype.Controller;

@ApplicationScoped
@Controller(value = "zone3PeopleBean")
public class Zone3PeopleBean extends PeopleBean{

	public Zone3PeopleBean() {
		setZonenumber(3);
		setZoneimage("/images/zone3.png");
	}

}
                    