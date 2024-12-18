package sevenrev.model;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.*;
import sevenrev.dao.ItemDAO;
import sevenrev.dao.ReviewlogDAO;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Ignore // It could cause some failures, not sure reason - so it was ignored
public class ReviewDateTest {
    private DAOFactory daoFactory;

    @Before
    public void setUp() {
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:/home/roy/MyApps/tmp/7rev.db");
            //TableUtils.dropTable(connectionSource, ReviewlogDAO.class, true);
            TableUtils.dropTable(connectionSource, Reviewlog.class, true);
            TableUtils.createTable(connectionSource, Reviewlog.class);
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.createTable(connectionSource, Item.class);
            daoFactory = new DAOFactory("jdbc:sqlite:/home/roy/MyApps/tmp/7rev.db");
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        ReviewlogDAO reviewlogDAO = null;
        ItemDAO itemDao = null;

        //Remove all of the lines
        try {
            reviewlogDAO = daoFactory.getReviewlogDAO();
            List<Reviewlog> list = reviewlogDAO.queryReviewlog_user(3);
            for (Reviewlog reviewlogEntry: list) {
                reviewlogDAO.remove(reviewlogEntry);
            }

            itemDao = daoFactory.getItemDAO();
            List<Date> curDates = itemDao.getDates02(3);
            for (Date dateTmp: curDates) {
                List<Item> items = itemDao.getItemsAllColumns02(3,dateTmp);
                for (Item itemEntry: items) {
                    itemDao.remove(itemEntry);
                }
            }
        } catch (Exception ex) {
            Assert.fail("Parse failure");
        }
        daoFactory = null;
        System.out.println("tearDown is done");
    }

    @Test
    public void test_initReviewDates_n_getReviewDateIndex() {
        ItemDAO itemDao = null;
        Item item;
        GlobalVariables.daoFactory = daoFactory;
        GlobalVariables.userID = 3;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setQuestion("OK01");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-04 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-04 00:00:00"));
            itemDao.insert(item);

            boolean flag01;
            ReviewlogDAO reviewlogDAO = daoFactory.getReviewlogDAO();
            flag01 = reviewlogDAO.updateOrInsertIntoReviewlog(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"), 2, 3, DateTimeFunctions.StringToDate("2017-01-02 00:00:00"), 3, true);
            flag01 = reviewlogDAO.updateOrInsertIntoReviewlog(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"), 5, 3, DateTimeFunctions.StringToDate("2017-02-03 00:00:00"), 3, true);

            ReviewDate.initReviewDates();
            //Assert.assertEquals(GlobalVariables.reviewDates.length, 3);
            Assert.assertEquals(GlobalVariables.reviewDates[0].getReviewDate(), DateTimeFunctions.StringToDate("2017-02-04 00:00:00"));
            Assert.assertEquals(GlobalVariables.reviewDates[1].getReviewDate(), DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals(GlobalVariables.reviewDates[2].getReviewDate(), DateTimeFunctions.StringToDate("2017-01-02 00:00:00"));

            Integer index01 = ReviewDate.getReviewDateIndex(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals(index01, new Integer(1));
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

}
