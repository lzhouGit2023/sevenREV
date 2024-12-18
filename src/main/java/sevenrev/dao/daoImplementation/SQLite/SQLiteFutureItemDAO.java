package sevenrev.dao.daoImplementation.SQLite;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import sevenrev.dao.FutureItemDAO;
import sevenrev.dao.ItemDAO;
import sevenrev.model.GlobalVariables;
import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SQLiteFutureItemDAO extends BaseDaoImpl<FutureItem, Integer>
        implements FutureItemDAO {
    private String DBPath = null;

    //private List<FutureItem> futureItemsCache = new LinkedList();
    private List<FutureItem> futureItemsToBeInsertedIntoItems = new LinkedList();
    private List<FutureItem> futureItemsToBeUpdatedWithinFutureItems = new LinkedList();

    public SQLiteFutureItemDAO(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, FutureItem.class);
    }

    @Override
    public void clearFutureItemCache() {
        futureItemsToBeInsertedIntoItems.clear();
        futureItemsToBeUpdatedWithinFutureItems.clear();
    }

    @Override
    public void addToFutureItemCache(FutureItem futureItem) {
        if (futureItem.getFutureCycles()>=1) {
            futureItemsToBeUpdatedWithinFutureItems.add(futureItem);
        } else {
            futureItemsToBeInsertedIntoItems.add(futureItem);
        }
        //futureItemsCache.add(futureItem);
    }

    @Override
    public void flushBothCaches() throws SQLException {
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        System.out.println("Flushing futureitems from futureItemsToBeInsertedIntoItems "
                + futureItemsToBeInsertedIntoItems.size());
        int i = 0;
        for (FutureItem futureItem : futureItemsToBeInsertedIntoItems) {
            this.createOrUpdate(futureItem);
            if (i < GlobalVariables.batchSize) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
        }
        conn.commit(savePoint);

        System.out.println("Flushing futureitems from futureItemsToBeUpdatedWithinFutureItems "
                + futureItemsToBeUpdatedWithinFutureItems.size());
        i = 0;
        for (FutureItem futureItem : futureItemsToBeUpdatedWithinFutureItems) {
            this.createOrUpdate(futureItem);
            if (i < GlobalVariables.batchSize) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
        }
        conn.commit(savePoint);
    }

    @Override
    public void flushToBeUpdatedCache() throws SQLException {
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        int i = 0;
        System.out.println("Flushing futureitems from futureItemsToBeUpdatedWithinFutureItems "
                + futureItemsToBeUpdatedWithinFutureItems.size());
        for (FutureItem futureItem : futureItemsToBeUpdatedWithinFutureItems) {
            this.createOrUpdate(futureItem);
            if (i < GlobalVariables.batchSize) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
        }
        conn.commit(savePoint);
    }

    @Override
    public void populateInternalDataWithFutureItemsfromDB(int userId)
            throws SQLException {
        System.out.println("Calling populateInternalDataWithFutureItemsfromDB");
        QueryBuilder<FutureItem, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userId);
        whereClause.and().le("futureCycles", 1);
        PreparedQuery<FutureItem> preparedQuery = qb.prepare();
        List<FutureItem> futureItems = this.query(preparedQuery);
        System.out.println("In populateInternalDataWithFutureItemsfromDB, there are " + futureItems.size() + " items with cycles <= 1");
        Integer cycles = 0;
        for (FutureItem fItem: futureItems) {
            cycles = fItem.getFutureCycles();
            if (cycles<=1) {
                futureItemsToBeInsertedIntoItems.add(fItem);
            } /* else {
                futureItemsToBeUpdatedWithinFutureItems.add(fItem);
            } */
        }
    }

    @Override
    public List<FutureItem> getFutureItemsToBeInsertedIntoItemsCache() {
        return this.futureItemsToBeInsertedIntoItems;
    }

    @Override
    public Iterator<FutureItem> getFutureItemsToBeInsertedIntoItemsFromDB(int userId) throws SQLException {
        QueryBuilder<FutureItem, Integer> qb = this.queryBuilder().distinct();
        qb.selectColumns("userid", "reviewdate", "reviewitem");
        Where whereClause = qb.where().eq("userid", userId);
        whereClause.and().le("futureCycles", 1);
        PreparedQuery<FutureItem> preparedQuery = qb.prepare();
        CloseableIterator<FutureItem> iterator =  this.iterator(preparedQuery);

        return iterator;
    }

    @Override
    public void removeFutureItemsToBeInsertedIntoItems(Iterator<FutureItem> futureItems) throws SQLException {
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        int counter = 0;
        int i = 0;
        FutureItem fItem;
        while (futureItems.hasNext()) {
            fItem = futureItems.next();
            this.delete(fItem);
            if (i < (GlobalVariables.batchSize-1)) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
            counter++;
        }
        conn.commit(savePoint);
        System.out.println("Completed to remove futureItems, count is " + counter);
    }

    @Override
    public void removeFutureItemsToBeInsertedIntoItems02(List<FutureItem> futureItems) throws SQLException {
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        int counter = 0;
        int i = 0;
        for (FutureItem fItem : futureItemsToBeInsertedIntoItems) {
            this.delete(fItem);
            if (i < GlobalVariables.batchSize) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
            counter++;
        }
        conn.commit(savePoint);
        System.out.println("Completed to remove futureItems, count is " + counter);
    }

    @Override
    public void decreaseFutureItemCycles() throws SQLException {
        DatabaseConnection conn = this.startThreadConnection();
        UpdateBuilder<FutureItem, Integer> ub = this.updateBuilder();
        ub.updateColumnExpression("futureCycles", "futureCycles - 1");
        ub.where().eq("userid", GlobalVariables.userID);
        int resultCount = ub.update();
        System.out.println("decreased cycles of " + resultCount + " rows in future item table");

        System.out.println("Updating decreased future item cycles :"
                + futureItemsToBeUpdatedWithinFutureItems.size());

        Savepoint savePoint = conn.setSavePoint(null);
        int i = 0;
        for (FutureItem fItem: futureItemsToBeUpdatedWithinFutureItems) {
            fItem.setFutureCycles(fItem.getFutureCycles() - 1);
            this.update(fItem);
            if (i < GlobalVariables.batchSize) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
        }
        conn.commit(savePoint);
        futureItemsToBeUpdatedWithinFutureItems.clear();
    }
}
