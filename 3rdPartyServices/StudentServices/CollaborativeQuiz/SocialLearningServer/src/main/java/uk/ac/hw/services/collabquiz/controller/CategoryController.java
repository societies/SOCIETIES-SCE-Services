package uk.ac.hw.services.collabquiz.controller;

import org.springframework.stereotype.Controller;
import uk.ac.hw.services.collabquiz.dao.impl.CategoryRepository;
import uk.ac.hw.services.collabquiz.dao.ICategoryRepository;
import uk.ac.hw.services.collabquiz.entities.Category;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import java.util.List;

@Controller
@ManagedBean(name = "categoryController")
public class CategoryController extends BasePageController {
    private ICategoryRepository categoryRepository;

    private List<Category> categories;

    public CategoryController() {
        log.debug("CategoryController ctor()");
        categoryRepository = new CategoryRepository();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("postConstruct()");

        categories = categoryRepository.list();
    }

    public List<Category> getCategories() {
        return categories;
    }

}
