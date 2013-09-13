package uk.ac.hw.services.collabquiz.controller;

import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

@Controller
@ManagedBean(name = "index")
public class IndexController extends BasePageController {
    private String color1;
    private String color2;

    public IndexController() {
        log.debug("IndexController ctor()");
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("postConstruct()");
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getColor2() {
        return color2;
    }
}
