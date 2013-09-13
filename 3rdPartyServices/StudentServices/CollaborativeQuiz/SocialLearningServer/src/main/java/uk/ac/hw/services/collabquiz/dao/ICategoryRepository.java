package uk.ac.hw.services.collabquiz.dao;

import uk.ac.hw.services.collabquiz.entities.Category;

import java.util.List;

public interface ICategoryRepository {

    /**
     * Will return ALL Categoriess
     */
    List<Category> list();

    Category getByID(int CategoryId);

    void insert(Category Category);

    void update(Category Category);

    /**
     * Perform a physical delete (instead of a logical delete)
     */
    void physicalDelete(Category Category);

}
