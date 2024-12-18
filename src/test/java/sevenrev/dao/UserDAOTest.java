package sevenrev.dao;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Suite;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.GlobalVariables;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.User;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
//Ignored because some test methods will destruct the database

@Ignore
@Suite.SuiteClasses(UserDAOTest.class)
public class UserDAOTest {
    private DAOFactory daoFactory;

    @Before
    public void setUp() {
        //mMockContext = RuntimeEnvironment.application;
        try {
            //String databaseURL;
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:" + GlobalVariables.databaseURL);
            //TableUtils.dropTable(connectionSource, User.class, true);
            //TableUtils.createTable(connectionSource, User.class);
            daoFactory = new DAOFactory("jdbc:sqlite:" + GlobalVariables.databaseURL);
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            System.out.println("Exception " + ex.getMessage());
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    @Test
    public void test_insert() {
        //deleteAllItems();
        UserDAO userDao = null;
        User user;
        try {
            userDao = daoFactory.getUserDAO();
            user = new User();
            user.setUserid(23);
            user.setCurdate(DateTimeFunctions.StringToDate("2021-01-02 00:00:00"));
            user.setName("Lisa_General");
            user.setTime1(1);
            user.setTime2(2);
            user.setTime3(3);
            user.setTime4(4);
            user.setTime5(5);
            user.setTime6(6);
            user.setTime7(7);
            userDao.insert(user);

            user = new User();
            user.setUserid(22);
            user.setCurdate(DateTimeFunctions.StringToDate("2021-01-01 00:00:00"));
            user.setName("Roy02");
            user.setTime1(1);
            user.setTime2(2);
            user.setTime3(3);
            user.setTime4(4);
            user.setTime5(5);
            user.setTime6(6);
            user.setTime7(7);
            userDao.insert(user);
            System.out.println("Users inserted");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        }
    }

    @Test
    //This method is used for configuring data for integration testing
    public void test_queryUser() {
        try {
            UserDAO userDao = null;
            User user;
            userDao = daoFactory.getUserDAO();
            List<User> users = userDao.getAllUserNameIDs();
            for (User user02 : users) {
                Date curDate = user02.getCurdate();
                Date reviewDate = user02.getReviewdate();
            }
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }


    }
}
