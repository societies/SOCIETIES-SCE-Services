package org.societies.ext3p.nzone.model;

import javax.faces.bean.ApplicationScoped;

import org.springframework.stereotype.Controller;

@ApplicationScoped
@Controller(value = "mainPeopleBean")
public class MainPeopleBean extends PeopleBean{

	public MainPeopleBean() {
		setCurrentMainView(true);
	}

}
                    