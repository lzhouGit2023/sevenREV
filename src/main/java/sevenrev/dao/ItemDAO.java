package sevenrev.dao;

import com.j256.ormlite.dao.Dao;
import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by roy on 2016-12-21.
 */
public interface ItemDAO extends Dao<Item, Integer> {
    public void insert(Item item) throws SQLException;
    public int getAvailableItemId(int userid, Date curDate) throws SQLException;
    public Item queryByKey(int userId, Date curDate, int item) throws SQLException;
    public void remove(Item item) throws SQLException;
    public List<Item> getItems(int userId, Date curDate) throws SQLException;
    public Iterator<Item> getItems02(int userId, Date curDate) throws SQLException;
    public List<Item> getItemsAllColumns01(int userid, boolean forreview, Date reviewdate) throws SQLException;
    // method getItemsAllColumns02(int userid, Date curDate) the items returned should be distinct based on
    // its original item's user id, item id and date
    public List<Item> getItemsAllColumns02(int userid, Date curDate) throws SQLException;
    public List<Item> getItemsAllColumns03(int userid, Date curDate, int itemID) throws SQLException;
    public List<Date> getDates01(int userid, Date curDate) throws SQLException;
    public List<Date> getDates02(int userid) throws SQLException;
    public List<Item> getItemsAllColumns04(int userid, Date curDate, int itemID,
                                           boolean forReview /* actually this is a constant "true" */,
                                           int reviewItem, Date reviewDate) throws SQLException;
    public List<Item> getItemsAllColumns041(int userid, Date curDate, boolean forReview /* actually this is a constant "true" */,
                                           int reviewItem, Date reviewDate) throws SQLException;
    public List<Item> getItemsAllColumns05(int userid, Date curDate) throws SQLException;
    public List<Item> getForreviewItems(int userid, boolean forreview) throws SQLException;
    public int getItemCount(int userid, Date curDate) throws SQLException;
    public int getOrigItemCount(int userid, Date curDate) throws SQLException;
    public boolean doesRecordExist(int userid, Date curDate, Date reviewDate, int reviewItem) throws SQLException;
    public List<Item> getOrigItemsAllColumns(int userid, Date startDate, Date endDate) throws SQLException;

    //When clicking Start new cycle, insert future items to Items table
    public void insertFutureItems(Iterator<FutureItem> futureItems, Date curDate) throws SQLException;
    //When clicking Start new cycle, insert future items to Items table
    public void insertFutureItems02(List<FutureItem> futureItems, Date curDate) throws SQLException;
    public void addToRemoveItemCache(Item item);
    public void flushAndClearRemoveItemCache() throws SQLException; // Physically delete to be removed records from Items table
}
