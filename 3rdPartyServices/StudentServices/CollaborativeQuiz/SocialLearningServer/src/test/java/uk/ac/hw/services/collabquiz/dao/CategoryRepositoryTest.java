package uk.ac.hw.services.collabquiz.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import uk.ac.hw.services.collabquiz.dao.impl.CategoryRepository;
import uk.ac.hw.services.collabquiz.entities.Category;

import java.util.List;

public class CategoryRepositoryTest {
    private CategoryRepository categoryRepository;

    @Before
    public void setupTest() {
        categoryRepository = new CategoryRepository();
    }

    @Test
    public void insert_getByID_insert_getByID_list_physicalDelete_getByID_list() throws Exception {

        // Category test object 1 *****************************
        final Category input1 = new Category();

        input1.setName("Geography");
        input1.setCategoryID(1);

        // **************************************************

        // Category test object 2 *****************************
        final Category input2 = new Category();

        input1.setName("Sport");
        input1.setCategoryID(3);

        // **************************************************

        // insert input1
        categoryRepository.insert(input1);
        int id1 = input1.getCategoryID();
        Assert.assertTrue(-1 != id1);

        // getByID input1
        final Category returnedInput1 = categoryRepository.getByID(id1);
        Assert.assertNotNull(returnedInput1);
        compareItems(input1, returnedInput1);

        // insert input2
        categoryRepository.insert(input2);
        int id2 = input2.getCategoryID();
        Assert.assertTrue(-1 != id2);

        // getByID input2
        final Category returnedInput2 = categoryRepository.getByID(id2);
        Assert.assertNotNull(returnedInput2);
        compareItems(input2, returnedInput2);

        // list
        final List<Category> list1 = categoryRepository.list();
        Assert.assertNotNull(list1);
        Assert.assertEquals(2, list1.size());

        // delete
        categoryRepository.physicalDelete(input1);

        // get
        final Category returnedInput2_2 = categoryRepository.getByID(id2);
        Assert.assertNotNull(returnedInput2_2);
        compareItems(input2, returnedInput2_2);

        // list
        final List<Category> list2 = categoryRepository.list();
        Assert.assertNotNull(list2);
        Assert.assertEquals(1, list2.size());

    }

    @Test
    public void listCategories() {

        // Category test object 1 *****************************
        final Category input1 = new Category();

        input1.setName("Geography");
        input1.setCategoryID(1);
        // **************************************************

        // Category test object 2 *****************************
        final Category input2 = new Category();

        input2.setName("Film");
        input2.setCategoryID(2);
        // **************************************************

        // Category test object 3 *****************************
        final Category input3 = new Category();

        input3.setName("Sport");
        input3.setCategoryID(3);
        // **************************************************

        // Category test object 4 *****************************
        final Category input4 = new Category();

        input4.setName("History");
        input4.setCategoryID(4);
        // **************************************************

        List<Category> allItems = categoryRepository.list();
        Assert.assertNotNull(allItems);
        Assert.assertEquals(4, allItems.size());
    }

    private void compareItems(Category expectedInput, Category actualInput) {
        Assert.assertEquals(expectedInput.getCategoryID(), actualInput.getCategoryID());
//        Assert.assertEquals(expectedInput.getName(), actualInput.getName());
    }
}