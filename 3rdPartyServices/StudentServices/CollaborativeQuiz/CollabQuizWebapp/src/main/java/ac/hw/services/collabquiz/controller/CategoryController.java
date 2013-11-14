package ac.hw.services.collabquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import ac.hw.services.collabquiz.dao.ICategoryRepository;
import ac.hw.services.collabquiz.dao.impl.CategoryRepository;
import ac.hw.services.collabquiz.entities.Category;


import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller("categoryController")
@ManagedBean(name = "categoryController")
@ViewScoped
public class CategoryController extends BasePageController {


	private ICategoryRepository categoryRepository;

	private final List<Category> categories = new ArrayList<Category>();

	// Creating new category
	private Category newCategory = new Category();

	// Category selected using checkbox
	private Category[] selectedCategories;
	private Category selectedCategory;

	public CategoryController() {
		postConstruct();
	}


	public void postConstruct() {
		log.debug("postConstruct ()");
		categoryRepository = new CategoryRepository();

		try {
			List<Category> storedCategories = categoryRepository.list();
			categories.addAll(storedCategories);
		} catch (Exception ex) {
			log.error("Error loading categories from repository, none loaded", ex);
		}

		if (log.isDebugEnabled())
			log.debug("Loaded " + categories.size() + " categories from DB");
	}

	public void initCategory() {
		log.debug("initCategory()");
		newCategory = new Category();
	}

	public List<Category> getCategories() {
		return categories;
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

	public ICategoryRepository getCategoryRepository() {
		return categoryRepository;
	}

	public void setCategoryRepository(ICategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public Category getNewCategory() {
		return newCategory;
	}
	
	//method to include or exclude supers
	public Category[] filterSuper(int method) {
		List<Category> filterC = new ArrayList<Category>();
			for ( Category c : categories)
			{
				if(method==0)
				{
				if(c.getSuperCatID()==0)
				{
					filterC.add(c);
				}
				}
				else
				{
					if(c.getSuperCatID()>0)
					{
						filterC.add(c);
					}
				}
			}
			Category[] cats = new Category[filterC.size()];
			int i =0;
			for (Category c : filterC)
			{
				cats[i]=c;
				i++;
			}
			return cats;
	}

	public void addCategory() {
		log.debug("Inserting new category with name: " + newCategory.getName());
		categoryRepository.insert(newCategory);
		categories.add(newCategory);
		newCategory = new Category();
	}

	public void deleteCategory() {
		log.debug("Deleting selected categories: " + Arrays.toString(selectedCategories));
		for (Category current : selectedCategories) {
			log.debug("Deleting: " + current);
			categoryRepository.physicalDelete(current);
			categories.remove(current);
		}
	}
}
