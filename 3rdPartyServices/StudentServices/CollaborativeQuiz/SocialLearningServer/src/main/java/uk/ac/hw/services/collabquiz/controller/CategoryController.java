package uk.ac.hw.services.collabquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uk.ac.hw.services.collabquiz.dao.ICategoryRepository;
import uk.ac.hw.services.collabquiz.entities.Category;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.context.RequestContext;

@Controller("categoryController")
@ManagedBean(name = "categoryController")
@ViewScoped
public class CategoryController extends BasePageController {

    @Autowired
    private ICategoryRepository categoryRepository;

    private List<Category> categories;

    // Creating new category
    private Category newCategory = new Category();

    private Category selectedCategory = new Category();

    // Category selected using checkbox
    private Category[] selectedCategories;

    public CategoryController() {
        log.debug("CategoryController ctor()");

//        try {
//            String driverClass = "com.mysql.jdbc.Driver";
//            Class.forName(driverClass);
//            log.debug("Successfully loaded class " + driverClass);
//        } catch (ClassNotFoundException e) {
//            log.error("Error loading mysql class", e);
//        }
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("postConstruct ()");

        try {
            categories = categoryRepository.list();
        } catch (Exception ex) {
            log.error("Error loading categories from repository, none loaded", ex);
            categories = new ArrayList<Category>();
        }

        if (log.isDebugEnabled())
            log.debug("Loaded " + categories.size() + " categories from DB");
    }

    public void initCategory() {
        RequestContext.getCurrentInstance().reset("form:newCategoryDlg");
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ICategoryRepository getCategoryRepository() {
        return categoryRepository;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Category[] getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(Category[] selectedCategories) {
        this.selectedCategories = selectedCategories;
    }


    public void setCategoryRepository(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category getNewCategory() {
        return newCategory;
    }

    public void setNewCategory(Category newCategory) {
        this.newCategory = newCategory;
    }

    public void addCategory() {
        categoryRepository.insert(newCategory);
        categories.add(newCategory);
        newCategory = new Category(); //Safe longterm solution?
    }

    public void deleteCategory() {
        for (Category current : selectedCategories) {
            log.debug("deleteing: " + current +" OUT OF: " + selectedCategories.toString());
            categoryRepository.physicalDelete(current);
            categories.remove(current);
        }
    }
}
