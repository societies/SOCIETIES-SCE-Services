package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.Cis;

public interface ICisRepository {
	
    /**
     * Will return ALL Categoriess
     */
    List<Cis> list();

    Cis getByName(String cisName);
    //void deleteGroupByID(int groupID);
    void update(Cis cis);

    void insert(Cis cis);


    /**
     * Perform a physical delete (instead of a logical delete)
     */
    void physicalDelete(Cis cis);

}
