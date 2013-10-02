package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.UserGroups;

public interface IUserGroupsRepository {
	
    /**
     * Will return ALL Categoriess
     */
    List<UserGroups> list();

    UserGroups getByID(String userJid);
    List<UserGroups> getListByID(int groupId);

    void insert(UserGroups userGroups);
    
    List<String> listUsers(int groupId);

    void update(UserGroups userGroups);
    
    void deleteAll(String groupID);
    
    void deleteUser(String userID);

    /**
     * Perform a physical delete (instead of a logical delete)
     */
    void physicalDelete(UserGroups userGroups);

}
