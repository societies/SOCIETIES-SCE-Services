package org.societies.ext3p.nzone.model;

import javax.faces.bean.ApplicationScoped;

import org.springframework.stereotype.Controller;

@ApplicationScoped
@Controller(value = "zone2PeopleBean")
public class Zone2PeopleBean extends PeopleBean{

	public Zone2PeopleBean() {
		setZonenumber(2);
		setZoneimage("/images/zone2.png");
	}

}
                    