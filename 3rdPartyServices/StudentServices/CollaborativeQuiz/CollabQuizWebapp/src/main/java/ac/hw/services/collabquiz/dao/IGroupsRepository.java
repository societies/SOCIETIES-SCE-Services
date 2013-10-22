package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.Groups;

public interface IGroupsRepository {
	
    /**
     * Will return ALL Categoriess
     */
    List<Groups> list();

    Groups getByID(int result);
    Groups getByName(String groupName);
    void deleteGroupByID(int groupID);
    void update(Groups group);

    void insert(Groups group);


    /**
     * Perform a physical delete (instead of a logical delete)
     */
    void physicalDelete(Groups group);

}
