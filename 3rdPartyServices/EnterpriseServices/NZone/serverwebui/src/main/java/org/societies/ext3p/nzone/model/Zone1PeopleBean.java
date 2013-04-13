package org.societies.ext3p.nzone.model;

import javax.faces.bean.ApplicationScoped;

import org.springframework.stereotype.Controller;

@ApplicationScoped
@Controller(value = "zone1PeopleBean")
public class Zone1PeopleBean extends PeopleBean{

	public Zone1PeopleBean() {
		setZonenumber(1);
		setZoneimage("/images/zone1.png");
	}

}
                    