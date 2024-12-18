package sevenrev.dao;

import com.j256.ormlite.dao.Dao;
import sevenrev.model.entities.FutureItem;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public interface FutureItemDAO extends Dao<FutureItem, Integer> {

    //clear the cached data structure (lists)
    public void clearFutureItemCache();
    public void addToFutureItemCache(FutureItem futureItem);

    //Physically flushes the changes into future items table
    public void flushBothCaches() throws SQLException; // Will not clean up cache

    public void flushToBeUpdatedCache() throws SQLException; // Will not clean up cache

    //This method will populate data structure with data from future items table
    public void populateInternalDataWithFutureItemsfromDB(int userId) throws SQLException;

    public List<FutureItem> getFutureItemsToBeInsertedIntoItemsCache();

    public Iterator<FutureItem> getFutureItemsToBeInsertedIntoItemsFromDB(int userId) throws SQLException;

    //Physically delete items from future items table based on a cached list
    public void removeFutureItemsToBeInsertedIntoItems(Iterator<FutureItem> futureItems) throws SQLException; //will clear internal data

    public void removeFutureItemsToBeInsertedIntoItems02(List<FutureItem> futureItems) throws SQLException; //will clear internal data
    public void decreaseFutureItemCycles() throws SQLException; // will clear internal data
}
