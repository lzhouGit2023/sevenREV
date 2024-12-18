package sevenrev.admin;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Suite;
import sevenrev.dao.UserDAO;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.GlobalVariables;
import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;
import sevenrev.model.entities.User;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Ignore
public class Admin {
    @Test
    public void createDBnUser() {

        DAOFactory daoFactory;
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:" + GlobalVariables.databaseURL);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.dropTable(connectionSource, FutureItem.class, true);
            TableUtils.createTable(connectionSource, FutureItem.class);
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.createTable(connectionSource, Item.class);
            TableUtils.dropTable(connectionSource, Reviewlog.class, true);
            TableUtils.createTable(connectionSource, Reviewlog.class);
            daoFactory = new DAOFactory("jdbc:sqlite:" + GlobalVariables.databaseURL);

            UserDAO userDao = daoFactory.getUserDAO();
            User user = new User();
            user.setUserid(1);
            user.setCurdate(DateTimeFunctions.StringToDate("2022-11-16 00:00:00"));
            user.setName("ZhouJie_General");
            user.setTime1(1);
            user.setTime2(3);
            user.setTime3(7);
            user.setTime4(16);
            user.setTime5(36);
            user.setTime6(85);
            user.setTime7(189); //On 2022 11/22, recall that on 11/16 Bible study we studied Psalm 63, then also randomly turned to page 189 of new Testment of my Bible which is II Cor Chapter 7
            userDao.insert(user);
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            System.out.println("Exception " + ex.getMessage());
            Assert.fail("Exception " + ex.getMessage());
        } catch (ParseException ex) {
            System.out.println("Exception " + ex.getMessage());
            Assert.fail("Exception " + ex.getMessage());
        }
    }
}
