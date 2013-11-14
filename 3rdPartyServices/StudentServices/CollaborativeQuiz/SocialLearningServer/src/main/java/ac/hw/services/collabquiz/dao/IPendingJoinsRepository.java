package ac.hw.services.collabquiz.dao;

import java.util.List;

import ac.hw.services.collabquiz.entities.PendingJoins;

public interface IPendingJoinsRepository {
	
    /**
     * Will return ALL Categoriess
     */
    List<PendingJoins> list();

    List<PendingJoins> getByID(String userJid);
    void deleteGroup(String groupName);
    List<PendingJoins> getPlayersByGroup(String groupName);

    void insert(PendingJoins pendingJoins);

    /**
     * Perform a physical delete (instead of a logical delete)
     */
    void physicalDelete(PendingJoins pendingJoins);


}
