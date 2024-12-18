package sevenrev.dao;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.*;
import org.junit.runners.Suite;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.entities.FutureItem;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.List;

import static sevenrev.model.GlobalVariables.databaseURL;

@Ignore
@Suite.SuiteClasses(FutureItemDAOTest.class)
public class FutureItemDAOTest {
    private DAOFactory daoFactory;

    @Before
    public void setUp() {
        //mMockContext = RuntimeEnvironment.application;
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:" + databaseURL);
            //TableUtils.dropTable(connectionSource, FutureItem.class, true);
            TableUtils.createTableIfNotExists(connectionSource, FutureItem.class);
            //TableUtils.createTable(connectionSource, FutureItem.class);
            daoFactory = new DAOFactory("jdbc:sqlite:" + databaseURL);
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            System.out.println("Exception " + ex.getMessage());
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        try {
            FutureItemDAO futureItemDao = daoFactory.getFutureItemDAO();

            QueryBuilder<FutureItem, Integer> qb = futureItemDao.queryBuilder();
            int userId = 3;
            Where whereClause = qb.where().eq("userid", userId);
            PreparedQuery<FutureItem> preparedQuery = qb.prepare();
            List<FutureItem> futureItems = futureItemDao.query(preparedQuery);
            for (FutureItem futureItem : futureItems) {
                int i = 9;
            }
        } catch (SQLException ex) {
            Assert.fail("EX " + ex.getMessage());
        }
        daoFactory = null;
        System.out.println("tearDown is done");
    }

    @Test
    public void fake_test() {
       System.out.println("Test is done");
    }

    @Test
    public void test_addToFutureItemCache() {
        FutureItemDAO futureItemDao = null;

        try {
            futureItemDao = daoFactory.getFutureItemDAO();
            futureItemDao.clearFutureItemCache();

            FutureItem futureItem = new FutureItem();
            futureItem.setFutureCycles(3);
            futureItem.setUserid(3);
            futureItem.setForreview(true);
            futureItem.setReviewdate(DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            futureItemDao.addToFutureItemCache(futureItem);

            futureItem = new FutureItem();
            futureItem.setFutureCycles(2);
            futureItem.setUserid(3);
            futureItem.setForreview(true);
            futureItem.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            futureItemDao.addToFutureItemCache(futureItem);
        } catch (java.sql.SQLException ex) {
            Assert.fail("EX " + ex.getMessage());
        } catch (java.text.ParseException ex) {
            Assert.fail("EX " + ex.getMessage());
        }
    }

    @Test
    public void test_flushCache() {
        FutureItemDAO futureItemDao = null;
        try {
            futureItemDao = daoFactory.getFutureItemDAO();
            futureItemDao.clearFutureItemCache();

            int userId = 3;
            FutureItem futureItem = new FutureItem();
            futureItem.setFutureCycles(3);
            futureItem.setUserid(userId);
            futureItem.setForreview(true);
            futureItem.setReviewdate(DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            futureItemDao.addToFutureItemCache(futureItem);

            futureItem = new FutureItem();
            futureItem.setFutureCycles(2);
            futureItem.setUserid(userId);
            futureItem.setForreview(true);
            futureItem.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            futureItemDao.addToFutureItemCache(futureItem);
            futureItemDao.flushBothCaches();

            //checking the flushed futureItems in database
            QueryBuilder<FutureItem, Integer> qb = futureItemDao.queryBuilder();

            Where whereClause = qb.where().eq("userid", userId);
            PreparedQuery<FutureItem> preparedQuery = qb.prepare();
            List<FutureItem> futureItems = futureItemDao.query(preparedQuery);
            Assert.assertEquals(futureItems.size(),2);
        } catch (java.sql.SQLException ex) {
            Assert.fail("EX " + ex.getMessage());
        } catch (java.text.ParseException ex) {
            Assert.fail("EX " + ex.getMessage());
        }
    }

    @Test
    public void test_decreaseFutureItemCycles() {
        FutureItemDAO futureItemDao = null;
        ItemDAO itemDao = null;

        try {
            futureItemDao = daoFactory.getFutureItemDAO();
            futureItemDao.clearFutureItemCache();
            itemDao = daoFactory.getItemDAO();
            int userId = 3;

            //remove all of the rows that meet the criteria
            QueryBuilder<FutureItem, Integer> qb = futureItemDao.queryBuilder();
            Where whereClause = qb.where().eq("userid", userId);
            whereClause.and().eq("reviewdate",
                    DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            PreparedQuery<FutureItem> preparedQuery = qb.prepare();
            List<FutureItem> futureItems = futureItemDao.query(preparedQuery);
            for (FutureItem fItem: futureItems) {
                futureItemDao.delete(fItem);
            }

            FutureItem futureItem = new FutureItem();
            futureItem.setFutureCycles(3);
            futureItem.setUserid(userId);
            futureItem.setForreview(true);
            futureItem.setReviewdate(DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            futureItemDao.addToFutureItemCache(futureItem);

            futureItem = new FutureItem();
            futureItem.setFutureCycles(1);
            futureItem.setUserid(userId);
            futureItem.setForreview(true);
            futureItem.setReviewdate(DateTimeFunctions.StringToDate("2001-01-01 00:00:00"));
            futureItemDao.addToFutureItemCache(futureItem);

            futureItemDao.flushBothCaches();
            //futureItemDao.populateInternalDataWithFutureItemsfromDB(userId);
            futureItemDao.decreaseFutureItemCycles();
            futureItemDao.clearFutureItemCache();

            qb = futureItemDao.queryBuilder();

            whereClause = qb.where().eq("userid", userId);
            whereClause.and().eq("reviewdate",
                    DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            preparedQuery = qb.prepare();
            futureItems = futureItemDao.query(preparedQuery);

            //items = itemDao.getItemsAllColumns01(userId, true,
            //        DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            Assert.assertEquals(1, futureItems.size());
        } catch (java.sql.SQLException ex) {
            Assert.fail("EX " + ex.getMessage());
        } catch (java.text.ParseException ex) {
            Assert.fail("EX " + ex.getMessage());
        }
    }
}
