package sevenrev.databaseMigration;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Suite;
import sevenrev.dao.ItemDAO;
import sevenrev.dao.ItemDAOTest;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.databaseMigration.entities.SrcItem;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;
import sevenrev.utilities.DateTimeFunctions;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import static sevenrev.model.GlobalVariables.databaseURL;

@Ignore
@Suite.SuiteClasses(ItemDAOTest.class)
public class SrcItemMigrationTest {
    private DAOFactory destDaoFactory;
    Dao<SrcItem, String> srcDatabaseDao;

    @Before
    public void setUp() {
        //mMockContext = RuntimeEnvironment.application;
        try {
            ConnectionSource connectionSource_MigSrc =
                    new JdbcConnectionSource("jdbc:sqlite:/home/roy/MyApps/tmp/leon7rev_2nd");
            srcDatabaseDao
                    = DaoManager.createDao(connectionSource_MigSrc, SrcItem.class);

            ConnectionSource connectionSource_MigDest =
                    new JdbcConnectionSource("jdbc:sqlite:" + databaseURL);
            TableUtils.createTableIfNotExists(connectionSource_MigDest, Item.class);
            //TableUtils.dropTable(connectionSource_MigDest, Item.class, true);
            //TableUtils.createTable(connectionSource_MigDest, Item.class);
            destDaoFactory = new DAOFactory("jdbc:sqlite:" + databaseURL);
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            System.out.println("Exception " + ex.getMessage());
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    @Test
    //This method can be used to migrate
    public void copyItems() {
        //ItemDAO destItemDao = null;
        Item item;
        try {
            ItemDAO destItemDao = destDaoFactory.getItemDAO();
            String query = "select userid, curdate, item, forreview, reviewdate, reviewitem," +
                    "   question, answer, sizeq, sizea, quImageSize, quImage," +
                    "   answImageSize, answImage, reviewtime from items order by curdate asc, item asc";
            GenericRawResults<Object[]> rawResults =
                    srcDatabaseDao.queryRaw(query,
                            new DataType[] { DataType.INTEGER, DataType.STRING,DataType.INTEGER,DataType.INTEGER,
                                    DataType.STRING, DataType.INTEGER, DataType.STRING, DataType.STRING,
                                    DataType.INTEGER,DataType.INTEGER,DataType.INTEGER, DataType.BYTE_ARRAY,
                                    DataType.INTEGER, DataType.BYTE_ARRAY,DataType.INTEGER});
            Iterator<Object[]> iterable = rawResults.iterator();
            //for (Object[] resultArray : rawResults) {
            while (iterable.hasNext()) {

                TransactionManager.callInTransaction(destItemDao.getConnectionSource(), new Callable() {
                    public Void call() {
                        long start = System.currentTimeMillis();
                        int insertCount = 0;
                        while (iterable.hasNext() && insertCount<2000) {
                            Item item01 = new Item();
                            try {
                                setItem(iterable.next(), item01);
                                //destItemDao.insert(item01);
                                destItemDao.create(item01);
                            } catch (Exception ex) {
                                Assert.fail("Exception " + ex.getMessage());
                            }
                            ++insertCount;
                        }
                        return null;
                    }
                });

                //destItemDao.insert(item);
            }
            rawResults.close();
        } catch (SQLException ex) {
            Assert.fail("Exception " + ex.getMessage());
        } catch (IOException ex) {
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    private void setItem(Object[] resultArray, Item item) {
        int index = 0;
        try {
            item.setUserid((Integer) resultArray[index++]);
            Date curDate = StringToDate((String) resultArray[index++]);
            item.setCurdate(curDate);
            item.setItem((Integer) resultArray[index++]);
            boolean forReview = (((Integer) resultArray[index++]) != 0);
            item.setForreview(forReview);
            Date reviewdate = StringToDate((String) resultArray[index++]);
            item.setReviewdate(reviewdate);
            item.setReviewitem((Integer) resultArray[index++]);
            item.setQuestion((String) resultArray[index++]);
            item.setAnswer((String) resultArray[index++]);
            item.setSizeq((Integer) resultArray[index++]);
            item.setSizea((Integer) resultArray[index++]);
            item.setQuImageSize((Integer) resultArray[index++]);
            byte[] bytes = (byte[]) resultArray[index++];
            item.setQuImage(bytes);
            item.setAnswImageSize((Integer) resultArray[index++]);
            bytes = (byte[]) resultArray[index++];
            item.setAnswImage(bytes);
            item.setReviewtime((Integer) resultArray[index++]);
        } catch (ParseException ex) {
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    private Date StringToDate(String dateStr) throws ParseException {
        Date result = null;
        DateFormat iso8601Format = null;
        if (dateStr!=null) {
            if (dateStr.length() <= 10) {
                iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
            } else {
                iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
        } else {
            dateStr="1990-01-01";
            iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        }
        result = iso8601Format.parse(dateStr);
        return result;
    }
}
