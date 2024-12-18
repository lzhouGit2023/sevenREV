package sevenrev.databaseMigration;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sevenrev.dao.ItemDAO;
import sevenrev.dao.UserDAO;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.databaseMigration.entities.SrcItem;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static sevenrev.model.GlobalVariables.databaseURL;
@Ignore
public class SrcUserMigrationTest {
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
            TableUtils.createTableIfNotExists(connectionSource_MigDest, User.class);
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
    public void copyUsers() {
        UserDAO destUserDao = null;
        User user;
        try {
            destUserDao = destDaoFactory.getUserDAO();
            String query = "select userid, name, curdate, reviewed, reviewdate, reviewitem, reviewtime," +
                    "   time1, time2, time3, time4, time5, time6, time7," +
                    "   displaysetting, debugdisplay from users";
            GenericRawResults<Object[]> rawResults =
                    srcDatabaseDao.queryRaw(query,
                            new DataType[] { DataType.INTEGER, DataType.STRING, DataType.STRING,
                                    DataType.INTEGER,DataType.STRING,
                                    DataType.INTEGER, DataType.INTEGER,
                                    DataType.INTEGER, DataType.INTEGER,DataType.INTEGER,DataType.INTEGER,DataType.INTEGER, DataType.INTEGER,DataType.INTEGER,
                                    DataType.INTEGER, DataType.INTEGER});
            for (Object[] resultArray : rawResults) {
                int index = 0;
                user = new User();
                user.setUserid((Integer) resultArray[index++]);

                user.setName((String)resultArray[index++]);

                Date curDate = StringToDate((String)resultArray[index++]);
                user.setCurdate(curDate);
                boolean reviewed = (((Integer) resultArray[index++])!=0);
                user.setReviewed(reviewed);
                Date reviewDate = StringToDate((String)resultArray[index++]);
                user.setReviewdate(reviewDate);
                user.setReviewitem((Integer) resultArray[index++]);
                user.setReviewtime((Integer) resultArray[index++]);

                user.setTime1((Integer) resultArray[index++]);
                user.setTime2((Integer) resultArray[index++]);
                user.setTime3((Integer) resultArray[index++]);
                user.setTime4((Integer) resultArray[index++]);
                user.setTime5((Integer) resultArray[index++]);
                user.setTime6((Integer) resultArray[index++]);
                user.setTime7((Integer) resultArray[index++]);

                boolean displaysetting = (((Integer) resultArray[index++])!=0);
                user.setDisplaysetting(displaysetting);
                boolean debugdisplay = (((Integer) resultArray[index++])!=0);
                user.setDebugdisplay(debugdisplay);

                destUserDao.insert(user);
            }
            rawResults.close();
        } catch (SQLException ex) {
            Assert.fail("Exception " + ex.getMessage());
        } catch (IOException ex) {
            Assert.fail("Exception " + ex.getMessage());
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
