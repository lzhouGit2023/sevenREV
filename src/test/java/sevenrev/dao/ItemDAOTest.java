package sevenrev.dao;

import java.io.*;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.junit.*;
import org.junit.runners.Suite;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.entities.Item;
import sevenrev.view.BuildConfig;
import sevenrev.utilities.DateTimeFunctions;


import org.junit.runner.RunWith;

import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static sevenrev.model.GlobalVariables.databaseURL;

/**
 * Created by roy on 2017-09-06.
 */
//@RunWith(Suite.class)
@Ignore
@Suite.SuiteClasses(ItemDAOTest.class)
//@RunWith(RobolectricTestRunner.class)
//@RunWith(JUnitPlatform.class)
//@Config(constants = BuildConfig.class)
public class ItemDAOTest {
    //private ItemDAO itemDao;
    private DAOFactory daoFactory;

    //@Mock
    //private Context mMockContext;
    //@BeforeClass
    /*
    public void initThisClass() {
        // daoFactory = new DAOFactory(null, "C:\\MyApps\\7rev_sqlite_test\\7rev.db");
        daoFactory = new DAOFactory(mMockContext, "C:\\MyApps\\7rev_sqlite_test\\7rev.db");
    }
    */

    @Before
    public void setUp() {
        //mMockContext = RuntimeEnvironment.application;
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:" + databaseURL);
            //TableUtils.dropTable(connectionSource, Item.class, true);
            //TableUtils.createTable(connectionSource, Item.class);
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.createTable(connectionSource, Item.class);
            daoFactory = new DAOFactory("jdbc:sqlite:" + databaseURL);
            System.out.println("setUp is done");
        } catch (SQLException ex) {
            System.out.println("Exception " + ex.getMessage());
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    @After
    public void tearDown() {

        //Remove all of the lines
        try {
            ItemDAO itemDao = daoFactory.getItemDAO();

            List<Date> dates = itemDao.getDates02(3);
            for (Date date: dates) {
                List<Item> items = itemDao.getItems(3, DateTimeFunctions.StringToDate(DateTimeFunctions.DateToString(date)));
                for (Item item : items) {
                    //itemDao.remove(item);
                }
            }
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("EX " + ex.getMessage());
        }
        daoFactory = null;
        System.out.println("tearDown is done");
    }

    private void cleanUpTable() {

        //Remove all of the lines
        try {
            ItemDAO itemDao = daoFactory.getItemDAO();
            List<Date> dates = itemDao.getDates02(3);
            for (Date date: dates) {
                List<Item> items = itemDao.getItems(3, DateTimeFunctions.StringToDate(DateTimeFunctions.DateToString(date)));
                for (Item item : items) {
                    itemDao.remove(item);
                }
            }
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException is " + ex.getMessage());
        }
        System.out.println("CleanUpTable is done");
    }

    @Ignore
    @Test
    //This method can be used to make changes to database for integration testing
    public void queryItems() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            String query = "select userid, curdate, item, forreview, reviewdate, reviewitem," +
                    "   question, answer, sizeq, sizea, quImageSize, quImage," +
                    "   answImageSize, answImage, reviewtime from items where userid=3 and quImageSize>0 order by curdate asc, item asc";
            GenericRawResults<Object[]> rawResults =
                    itemDao.queryRaw(query,
                            new DataType[] { DataType.INTEGER, DataType.TIME_STAMP,DataType.INTEGER,DataType.INTEGER,
                                    DataType.TIME_STAMP, DataType.INTEGER, DataType.STRING, DataType.STRING,
                                    DataType.INTEGER,DataType.INTEGER,DataType.INTEGER, DataType.BYTE_ARRAY,
                                    DataType.INTEGER, DataType.BYTE_ARRAY,DataType.INTEGER});
            for (Object[] row : rawResults) {
                int i = 9;
            }
            List<Item> items = itemDao.getItemsAllColumns05(3,DateTimeFunctions.StringToDate("2018-08-12 00:00:00"));
            item = items.get(0);
        } catch (SQLException ex) {
            Assert.fail("Exception " + ex.getMessage());
        } catch (ParseException ex) {
            Assert.fail("Exception " + ex.getMessage());
        }
    }

    private void copyWithStreams(File aSourceFile, File aTargetFile) {
        InputStream inStream = null;
        OutputStream outStream = null;
        try{
            try {
                byte[] bucket = new byte[32*1024];
                inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
                outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile));
                int bytesRead = 0;
                while(bytesRead != -1){
                    bytesRead = inStream.read(bucket); //-1, 0, or more
                    if(bytesRead > 0){
                        outStream.write(bucket, 0, bytesRead);
                    }
                }
            }
            finally {
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            }
        }
        catch (FileNotFoundException ex){
            Assert.fail(ex.getMessage());
        }
        catch (IOException ex){
            Assert.fail(ex.getMessage());
        }
    }


    private void deleteAllItems() {

        //Remove all of the lines
        try {
            ItemDAO itemDao = daoFactory.getItemDAO();
            List<Item> items = itemDao.getItems(3, DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            for (Item item : items) {
                itemDao.remove(item);
            }
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException is " + ex.getMessage());
        }
    }
    @Test
    public void test_insert() {
        //deleteAllItems();
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setQuestion("OK");
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setItem(3);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            itemDao.insert(item);
            System.out.println("Items inserted");
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
        try {
            List<Item> items = itemDao.getItems(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            Assert.assertEquals("Item count is not 1 ?!", 1, items.size());
            Assert.assertEquals("Item Qusetion is not 'OK'?!", "OK", items.get(0).getQuestion());
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (Exception ex) {
            Assert.fail(ex.getStackTrace().toString());
        }
        System.out.println("test_insert is done");
    }

    @Test
    public void test_getItemsAllColumns01() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK01");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-05-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OKWrong02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Item> items = itemDao.getItemsAllColumns01(3,true,DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals("Item count is not 3?!", 3, items.size());
            HashSet<String> set01 = new HashSet<String>();
            set01.add("OK01"); set01.add("OK02"); ; set01.add("OK03");
            Assert.assertEquals("something wrong", true, set01.contains(items.get(0).getQuestion()) );
            Assert.assertEquals("something wrong", true, set01.contains(items.get(1).getQuestion()) );
            Assert.assertEquals("something wrong", true, set01.contains(items.get(2).getQuestion()) );
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

    @Test
    public void test_getItemsAllColumns02() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK01");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            item.setQuestion("OKWrong02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Item> items = itemDao.getItemsAllColumns02(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            Assert.assertEquals("Item count is not 3?!", 3, items.size());
            HashSet<String> set01 = new HashSet<String>();
            Assert.assertEquals("something wrong", "OK03", items.get(0).getQuestion() );
            Assert.assertEquals("something wrong", "OK01", items.get(1).getQuestion() );
            Assert.assertEquals("something wrong", "OK02", items.get(2).getQuestion() );
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

    //??? issue with this one
    @Test
    public void test_getItemsAllColumns03() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK01");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            item.setQuestion("OKWrong02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Item> items = itemDao.getItemsAllColumns03(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"),1);
            Assert.assertEquals("something wrong", "OK01", items.get(0).getQuestion() );
            items = itemDao.getItemsAllColumns03(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"),2);
            Assert.assertEquals("something wrong", "OK02", items.get(0).getQuestion() );
            items = itemDao.getItemsAllColumns03(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"),3);
            Assert.assertEquals("something wrong", "OK03", items.get(0).getQuestion() );
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }

    }

    @Test
    public void test_getDates01() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-03 00:00:00"));
            item.setQuestion("OK01");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-22 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-05-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-04-01 00:00:00"));
            item.setQuestion("OKWrong02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Date> dates = itemDao.getDates01(3,DateTimeFunctions.StringToDate("2017-03-01 00:00:00"));
            Assert.assertEquals("Item count is not 3?!", 3, dates.size());
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

    @Test
    public void test_getDates02() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-03 00:00:00"));
            item.setQuestion("OK01");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-22 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-05-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-04-01 00:00:00"));
            item.setQuestion("OKWrong02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Date> dates = itemDao.getDates02(3);
            Assert.assertEquals("Item count is not 5?!", 5, dates.size());
        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

    @Test
    public void test_getItemsAllColumns04() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setReviewitem(5);
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Item> items = itemDao.getItemsAllColumns04(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"), 1, true,
                    5,DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals("something wrong", 1, items.size() );

            items = itemDao.getItemsAllColumns04(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"), 2, true,
                    5,DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals("something wrong", 0, items.size() );

        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }

    }

    @Test
    public void test_getItemsAllColumns041() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setReviewitem(5);
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Item> items = itemDao.getItemsAllColumns041(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"), true, 5,
                    DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals("something wrong", 1, items.size() );

            items = itemDao.getItemsAllColumns041(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"), false,
                    5,DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            Assert.assertEquals("something wrong", 0, items.size() );

        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }

    }

    @Test
    public void test_getItemsAllColumns05() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setReviewitem(5);
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //adding a noise items
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            item.setQuestion("OKWrong01");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);



            //testing
            List<Item> items = itemDao.getItemsAllColumns05(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            Assert.assertEquals("something wrong", 2, items.size() );

            items = itemDao.getItemsAllColumns05(3,DateTimeFunctions.StringToDate("2017-02-01 00:00:00"));
            Assert.assertEquals("something wrong", 1, items.size() );

        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

    @Test
    public void test_getForreviewItems() {
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setReviewitem(5);
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //testing
            List<Item> items = itemDao.getForreviewItems(3, true);

            Assert.assertEquals("something wrong", new Long(1), new Long(items.size()));
            Assert.assertEquals("something wrong", "2017-01-01", DateTimeFunctions.DateToString(items.get(0).getCurdate()));

        } catch (java.text.ParseException ex) {
                Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail("SQLException " + ex.getMessage());
        }
    }

    @Test
    public void test_getItemCount() {
        //cleanUpTable();
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setReviewitem(5);
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-05 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            int cnt = itemDao.getItemCount(3, DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            Assert.assertEquals("something wrong", new Long(1), new Long(cnt));
            cnt = itemDao.getItemCount(3, DateTimeFunctions.StringToDate("2017-01-05 00:00:00"));
            Assert.assertEquals("something wrong", new Long(1), new Long(cnt));

        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void test_getOrigItemCount() {
        //cleanUpTable();
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setReviewitem(5);
            itemDao.insert(item);

            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);

            //testing
            int result = itemDao.getOrigItemCount(3,DateTimeFunctions.StringToDate("2017-01-01 00:00:00"));

            Assert.assertEquals("something wrong", new Long(1), new Long(result));
            //Assert.assertEquals("something wrong", "2017-01-01", DateTimeFunctions.DateToString(items.get(0).getCurdate()));

        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void test_doesRecordExist() {
        //cleanUpTable();
        ItemDAO itemDao = null;
        Item item;
        try {
            itemDao = daoFactory.getItemDAO();
            item = new Item();
            item.setUserid(3);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-05-01 00:00:00"));
            item.setQuestion("OK02");
            item.setForreview(true);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            //item.setReviewitem(5);
            item.setReviewitem(1);
            itemDao.insert(item);


            item = new Item();
            //item.setItem(5);
            item.setUserid(3);
            item.setReviewitem(1);
            item.setCurdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            item.setQuestion("OK03");
            item.setForreview(false);
            item.setReviewdate(DateTimeFunctions.StringToDate("2017-02-03 00:00:00"));
            itemDao.insert(item);


            //testing
            boolean result;

            //result = itemDao.doesRecordExist(3, DateTimeFunctions.StringToDate("2017-05-01 00:00:00"), DateTimeFunctions.StringToDate("2017-02-03 00:00:00"), 1);
            result = itemDao.doesRecordExist(3, DateTimeFunctions.StringToDate("2017-02-03 00:00:00"), DateTimeFunctions.StringToDate("2017-02-03 00:00:00"), 1);

            Assert.assertTrue(result);
            result = itemDao.doesRecordExist(3, DateTimeFunctions.StringToDate("2017-05-01 00:00:00"), DateTimeFunctions.StringToDate("2017-02-03 00:00:00"), 5+1);
            Assert.assertFalse(result);

        } catch (java.text.ParseException ex) {
            Assert.fail("Date string format issue, test failed");
        } catch (SQLException ex) {
            Assert.fail(ex.getMessage());
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
