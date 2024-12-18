package sevenrev.dao;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.*;
import org.junit.runners.Suite;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;
import sevenrev.view.BuildConfig;
import sevenrev.utilities.DateTimeFunctions;


import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.List;

//@RunWith(Suite.class)
//@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class)
@Ignore
@Suite.SuiteClasses(ReviewlogDAO.class)
public class ReviewlogDAOTest {
    //private ItemDAO itemDao;
    private DAOFactory daoFactory;

    //@Mock
    //private Context mMockContext;

    @Before
    public void setUp() {
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:/home/roy/MyApps/tmp/7rev.db");
            //TableUtils.dropTable(connectionSource, ReviewlogDAO.class, true);
            TableUtils.dropTable(connectionSource, Reviewlog.class, true);
            TableUtils.createTable(connectionSource, Reviewlog.class);
            daoFactory = new DAOFactory("jdbc:sqlite:/home/roy/MyApps/tmp/7rev.db");
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @After
    public void tearDown() {
        ReviewlogDAO reviewlogDAO = null;

        //Remove all of the lines
        try {
            reviewlogDAO = daoFactory.getReviewlogDAO();
            List<Reviewlog> list = reviewlogDAO.queryReviewlog_user(3);
            for (Reviewlog reviewlogEntry: list) {
                //reviewlogDAO.remove(reviewlogEntry);
            }
        } catch (Exception ex) {
            Assert.fail("Parse failure");
        }
        daoFactory = null;
        System.out.println("tearDown is done");
    }

    private void cleanUpTable() {
        ReviewlogDAO reviewlogDAO = null;

        //Remove all of the lines
        try {
            reviewlogDAO = daoFactory.getReviewlogDAO();
            List<Reviewlog> list = reviewlogDAO.queryReviewlog_user(3);
            for (Reviewlog reviewlogEntry: list) {
                reviewlogDAO.remove(reviewlogEntry);
            }
        } catch (Exception ex) {
            Assert.fail("Parse failure");
        }
        System.out.println("tearDown is done");
    }

    @Ignore
    @Test
    public void test_queryReviewlog() {
        cleanUpTable();
        ReviewlogDAO reviewlogDAO = null;
        try {
            reviewlogDAO = daoFactory.getReviewlogDAO();
            //    public boolean insertIntoReviewlog(Date itemDate, int itemid, int userid, Date reviewDate, int itemTime, boolean remember)
            boolean flag01 = reviewlogDAO.updateOrInsertIntoReviewlog(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"), 2, 3, DateTimeFunctions.StringToDate("2017-01-02 00:00:00"), 3, true);
            Reviewlog reviewlog = reviewlogDAO.queryReviewlog(3,DateTimeFunctions.StringToDate("2017-01-05 00:00:00"),2,DateTimeFunctions.StringToDate("2017-01-02 00:00:00") );
            Assert.assertNotNull(reviewlog);
            Assert.assertEquals(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"),reviewlog.getItemdate());
            Assert.assertNotEquals(DateTimeFunctions.StringToDate("2017-01-03 00:00:00"),reviewlog.getItemdate());
            Assert.assertEquals(DateTimeFunctions.StringToDate("2017-01-02 00:00:00"),reviewlog.getReviewdate());
            System.out.println("All completed successfully");
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().toString());
        }
        int i=0;
    }

    //This can be used in integration testing
    @Ignore
    @Test
    public void test_queryReviewlog02() {
        ReviewlogDAO reviewlogDAO = null;
        try {
            reviewlogDAO = daoFactory.getReviewlogDAO();
            //    public boolean insertIntoReviewlog(Date itemDate, int itemid, int userid, Date reviewDate, int itemTime, boolean remember)
            //boolean flag01 = reviewlogDAO.updateOrInsertIntoReviewlog(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"), 2, 3, DateTimeFunctions.StringToDate("2017-01-02 00:00:00"), 3, true);
            Reviewlog reviewlog = reviewlogDAO.queryReviewlog(3,DateTimeFunctions.StringToDate("2021-01-02 00:00:00"),2,DateTimeFunctions.StringToDate("2017-01-02 00:00:00") );
            Assert.assertNotNull(reviewlog);
            Assert.assertEquals(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"),reviewlog.getItemdate());
            Assert.assertNotEquals(DateTimeFunctions.StringToDate("2017-01-03 00:00:00"),reviewlog.getItemdate());
            Assert.assertEquals(DateTimeFunctions.StringToDate("2017-01-02 00:00:00"),reviewlog.getReviewdate());
            System.out.println("All completed successfully");
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().toString());
        }
        int i=0;
    }
}
