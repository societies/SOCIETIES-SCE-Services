package org.societies.ext3p.nzone.model;

import java.util.List;

import javax.faces.bean.ApplicationScoped;

import org.societies.api.ext3p.nzone.client.INZoneClient;
import org.societies.api.ext3p.nzone.model.UserPreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@SuppressWarnings("serial")
@ApplicationScoped
@Controller(value = "mainPeopleBean")
public class MainPeopleBean extends PeopleBean{

	public MainPeopleBean() {
		setCurrentMainView(true);
	}

}
                    