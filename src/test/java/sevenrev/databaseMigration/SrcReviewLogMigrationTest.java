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
import sevenrev.dao.ReviewlogDAO;
import sevenrev.dao.UserDAO;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.databaseMigration.entities.SrcItem;
import sevenrev.model.entities.Reviewlog;
import sevenrev.model.entities.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.Callable;

import static sevenrev.model.GlobalVariables.databaseURL;

@Ignore
public class SrcReviewLogMigrationTest {
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
            TableUtils.createTableIfNotExists(connectionSource_MigDest, Reviewlog.class);
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
    public void copyReviewLog() {
        //final ReviewlogDAO destReviewLogDao = null;
        Reviewlog reviewLog;
        try {
            final ReviewlogDAO destReviewLogDao = destDaoFactory.getReviewlogDAO();
            String query = "select userid, itemid, itemdate, reviewdate," +
                    " reviewtime, remember from reviewlog";
            GenericRawResults<Object[]> rawResults =
                    srcDatabaseDao.queryRaw(query,
                            new DataType[] { DataType.INTEGER, DataType.INTEGER,
                                    DataType.STRING,DataType.STRING,
                                    DataType.INTEGER, DataType.INTEGER});

            Iterator<Object[]> iterable = rawResults.iterator();

            //for (Object[] resultArray : rawResults.iterator()) {
            while (iterable.hasNext()) {
                TransactionManager.callInTransaction(destReviewLogDao.getConnectionSource(), new Callable() {
                    public Void call() throws Exception {
                        long start = System.currentTimeMillis();
                        int insertCount = 0;
                        while (iterable.hasNext() && insertCount<2000) {
                            Reviewlog reviewLog01 = new Reviewlog();
                            setReviewlog(iterable.next(), reviewLog01);
                            destReviewLogDao.create(reviewLog01);
                            ++insertCount;
                        }
                        return null;
                    }
                });
                //reviewLog = new Reviewlog();
                //setReviewlog(resultArray, reviewLog);
                //destReviewLogDao.create(reviewLog);
            }
            rawResults.close();
        } catch (SQLException ex) {
            Assert.fail("Exception " + ex.getMessage());
        } catch (IOException ex) {
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    private void setReviewlog(Object[] resultArray, Reviewlog reviewLog) {
        int index = 0;
        try {
            reviewLog.setUserid((Integer) resultArray[index++]);
            reviewLog.setItemid((Integer) resultArray[index++]);
            Date date01 = StringToDate((String) resultArray[index++]);
            reviewLog.setItemdate(date01);
            date01 = StringToDate((String) resultArray[index++]);
            reviewLog.setReviewdate(date01);
            reviewLog.setReviewtime((Integer) resultArray[index++]);
            boolean remember = (((Integer) resultArray[index++]) != 0);
            reviewLog.setRemember(remember);
        } catch (ParseException ex) {
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    private Date StringToDate(String dateStr) throws ParseException {
        Date result = null;
        DateFormat iso8601Format = null;
        if (dateStr.length()<=10) {
            iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        result = iso8601Format.parse(dateStr);
        return result;
    }

}
