package sevenrev.dao.daoImplementation.SQLite;

/*
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
 */
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import sevenrev.dao.ItemDAO;
import sevenrev.model.GlobalVariables;
import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by roy on 2016-12-22.
 */
public class SQLiteItemDAO extends BaseDaoImpl<Item, Integer>
        implements ItemDAO {
    private String DBPath = null;
    //private Context context = null;
    private List<Item> RemoveItemCache = new LinkedList();

    public SQLiteItemDAO(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, Item.class);
    }

    /*
    public SQLiteItemDAO(Context context, String DBPath) {
        this.DBPath = DBPath;
        this.context = context;
    }

    public SQLiteItemDAO(String DBPath) {
        this.DBPath = DBPath;
    }
    */

    @Override
    public void insert(Item item) throws SQLException {

        /*
        SQLiteUtilities dbUtil = new SQLiteUtilities(this.context, this.DBPath, null,1);
        //SQLiteUtilities02 dbUtil = new SQLiteUtilities02(this.context, this.DBPath);
                SQLiteDatabase db = dbUtil.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("userid", item.getUserid());
        values.put("Curdate", DateTimeFunctions.DateToString(item.getCurdate()));
        values.put("forreview", item.getForreview());
        values.put("reviewdate", DateTimeFunctions.DateToString(item.getReviewdate()));
        if (item.getItem()==null) {
            values.put("item", getAvailableItemId(item.getUserid(), item.getCurdate()));
        } else {
            values.put("item", item.getItem());
        }

        values.put("reviewitem", item.getReviewitem());
        values.put("question", item.getQuestion());
        values.put("answer", item.getAnswer());
        values.put("reviewtime", item.getReviewtime());
        */
        if (item.getItem()==null) {
            item.setItem(getAvailableItemId(item.getUserid(), item.getCurdate()));
        }
        //returnedCode = db.insertWithOnConflict("items", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        //returnedCode = db.insert("users", null, values);
        this.create(item);
        //dbUtil.close();
    }

    @Override
    public int getAvailableItemId(int userid, Date curDate) throws SQLException {
        Iterator<Item> items = getItems02(userid, curDate);
        Integer max = 0;
        Item item = null;

        while (items.hasNext()) {
            item = items.next();
            max++;
        }
        return max + 1;
    };

    @Override
    public Item queryByKey(int userId, Date curDate, int item) throws SQLException {

        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userId);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        //qb.where().and().eq("item", item);
        whereClause.and().eq("Curdate", curDate);
        whereClause.and().eq("item", item);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        Item result = null;
        if (items.size()>0) {
            result = items.get(0);
        }

        return result;
    };

    @Override
    public void remove(Item item) throws SQLException {
        /*
        SQLiteUtilities dbUtil = new SQLiteUtilities(this.context, this.DBPath, null,1);
        SQLiteDatabase db = dbUtil.getWritableDatabase();
        String query = "delete FROM items where userid=" + item.getUserid() + " and date(Curdate)='" +
                DateTimeFunctions.DateToString(item.getCurdate()) + "' and item=" + item.getItem();
        db.execSQL(query);
        dbUtil.close();
        */
        this.delete(item);
    };

    /*
    private List<Item> getItemsfromCursor(Cursor cursor) {
        List<Item> items = new ArrayList<Item>();
        Item item = null;
        if (cursor.moveToFirst()) {
            do {
                item = new Item();
                item.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
                item.setForreview(cursor.getInt(cursor.getColumnIndex("forreview"))>0);
                //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    //String tmpDate;
                    //Date dateResult;
                    //dateResult = DateTimeFunctions.StringToDate(cursor.getString(cursor.getColumnIndex("Curdate")));
                    item.setCurdate(DateTimeFunctions.StringToDate(cursor.getString(cursor.getColumnIndex("Curdate"))));
                    //dateResult = DateTimeFunctions.StringToDate(cursor.getString(cursor.getColumnIndex("reviewdate")));
                    item.setReviewdate(DateTimeFunctions.StringToDate(cursor.getString(cursor.getColumnIndex("reviewdate"))));
                } catch (ParseException e) {
                    //Log.e(TAG, "Parsing ISO8601 datetime failed", e);
                }
                item.setItem(cursor.getInt(cursor.getColumnIndex("item")));
                item.setReviewitem(cursor.getInt(cursor.getColumnIndex("reviewitem")));

                int tmpInt;
                if ((tmpInt=cursor.getColumnIndex("question"))>=0) {
                    item.setQuestion(cursor.getString(tmpInt));
                }
                if ((tmpInt=cursor.getColumnIndex("answer"))>=0) {
                    item.setAnswer(cursor.getString(tmpInt));
                }

                if ((tmpInt=cursor.getColumnIndex("reviewtime"))>=0) {
                    item.setReviewtime(cursor.getInt(tmpInt));
                }


                items.add(item);
            } while (cursor.moveToNext());
        };
        return items;
    }
    */
    /*
    private List<Date> getCurDatesfromCursor(Cursor cursor) {
        List<Date> dates = new ArrayList<Date>();
        Item item = null;
        Date tmpDate = null;
        if (cursor.moveToFirst()) {
            do {
                //item = new Item();
                //item.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
                //item.setForreview(cursor.getInt(cursor.getColumnIndex("forreview"))>0);
                //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    tmpDate = DateTimeFunctions.StringToDate(cursor.getString(cursor.getColumnIndex("curdate")));
                } catch (ParseException e) {
                    //Log.e(TAG, "Parsing ISO8601 datetime failed", e);
                }
                dates.add(tmpDate);
            } while (cursor.moveToNext());
        };
        return dates;
    }
    */

    @Override
    public List<Item> getItems(int userId, Date curDate) throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userId);
        whereClause.and().eq("Curdate", curDate);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);
        //Need to determine the query
        //String query = "SELECT * FROM items where userid=" + userId +
        //        " and date(Curdate)='" + DateTimeFunctions.DateToString(curDate) + "'";
        return items;
    };

    public Iterator<Item> getItems02(int userId, Date curDate) throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userId);
        whereClause.and().eq("Curdate", curDate);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        PreparedQuery<Item> preparedQuery = qb.prepare();
        CloseableIterator<Item> iterator = this.iterator(preparedQuery);
        return iterator;
    }

    @Override
    public List<Item> getItemsAllColumns01(int userid, boolean forreview, Date reviewdate) throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("forreview", Integer.toString(forreview?1:0));
        //qb.where().and().eq("reviewdate", DateTimeFunctions.DateToString(reviewdate));
        whereClause.and().eq("forreview", forreview);
        //whereClause.and().eq("reviewdate", reviewdate);

        whereClause.and().ge("reviewdate", DateTimeFunctions.removeHMS(reviewdate));
        whereClause.and().lt("reviewdate", DateTimeFunctions.getNextDay(reviewdate));

        qb.orderBy("item", true);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        //String query = "select * from items where userid=? and forreview=? and date(reviewdate)=? order by item";
        //String[] args = {Integer.toString(userid), Integer.toString(forreview?1:0), DateTimeFunctions.DateToString(reviewdate)};

        return items;
    }
    @Override
    public List<Item> getItemsAllColumns02(int userid, Date curDate) throws SQLException {
        //Get non-forreview items
        QueryBuilder<Item, Integer> qb01 = this.queryBuilder();
        Where whereClause01 = qb01.where().eq("userid", userid);
        whereClause01.and().ge("Curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause01.and().lt("Curdate", DateTimeFunctions.getNextDay(curDate));
        whereClause01.and().eq("forreview", false);
        qb01.orderBy("reviewtime", true);
        qb01.orderBy("Curdate", false);
        qb01.orderBy("item", true);
        PreparedQuery<Item> preparedQuery01 = qb01.prepare();
        List<Item> items01 = this.query(preparedQuery01);

        //Get forreview items
        QueryBuilder<Item, Integer> qb02 = this.queryBuilder()
            .groupBy("userid")
            .groupBy("forreview")
            .groupBy("reviewitem")
            .groupBy("reviewdate");

        qb02.selectColumns("userid", "Curdate", "reviewtime", "forreview", "reviewitem", "reviewdate", "item");
        Where whereClause02 = qb02.where().eq("userid", userid);
        whereClause02.and().ge("Curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause02.and().lt("Curdate", DateTimeFunctions.getNextDay(curDate));
        whereClause02.and().eq("forreview", true);
        qb02.orderBy("reviewdate", false);
        qb02.orderBy("reviewitem", true);
        PreparedQuery<Item> preparedQuery02 = qb02.prepare();
        List<Item> items02 = this.query(preparedQuery02);

        items01.addAll(items02);
        return items01;
    }
    @Override

    public List<Item> getItemsAllColumns03(int userid, Date curDate, int itemID) throws SQLException{
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        //qb.where().and().eq("item", itemID);

        //whereClause.and().eq("Curdate", curDate);
        whereClause.and().ge("Curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("Curdate", DateTimeFunctions.getNextDay(curDate));

        whereClause.and().eq("item", itemID);

        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        //String query = "select * from items where userid=? and date(Curdate)=? and item=?";

        return items;
    }
    @Override
    public List<Date> getDates01(int userid, Date curDate)  throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder().distinct();
        qb.selectColumns("Curdate");
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().lt("Curdate", DateTimeFunctions.DateToString(curDate));
        whereClause.and().lt("Curdate", DateTimeFunctions.removeHMS(curDate));
        qb.orderBy("Curdate",false);

        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);
        List<Date> dates01 = new ArrayList<Date>();
        items.stream().forEach(element -> {dates01.add(element.getCurdate());});
        List<Date> dates = dates01.stream().map(DateTimeFunctions::removeHMS).distinct().collect(Collectors.toList());

        //String query = "select distinct date(curdate) as curdate from items where userid=? and date(Curdate)<? order by date(curdate) desc";
        //String[] args = {Integer.toString(userid),DateTimeFunctions.DateToString(curDate)};

        return dates;
    }
    @Override
    public List<Date> getDates02(int userid)  throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder().distinct();
        qb.selectColumns("Curdate");
        qb.where().eq("userid", userid);
        qb.orderBy("Curdate",false);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);
        List<Date> dates01 = new ArrayList<Date>();
        items.stream().forEach(element -> {dates01.add(element.getCurdate());});
        List<Date> dates = dates01.stream().map(DateTimeFunctions::removeHMS).distinct().collect(Collectors.toList());

        //String query = "select distinct date(curdate) as curdate from items where userid=? order by date(curdate) desc";

        return dates;
    }
    @Override
    public List<Item> getItemsAllColumns04(int userid, Date curDate, int itemID,
                 boolean forReview /* actually this is a constant "true" */, int reviewItem, Date reviewDate)
            throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        //qb.where().and().eq("item", itemID);
        //qb.where().and().eq("forreview", 1);
        //qb.where().and().eq("reviewitem", reviewItem);
        //qb.where().and().eq("reviewdate", DateTimeFunctions.DateToString(reviewDate));

        //whereClause.and().eq("Curdate", curDate);
        whereClause.and().ge("Curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("Curdate", DateTimeFunctions.getNextDay(curDate));

        whereClause.and().eq("item", itemID);
        whereClause.and().eq("forreview", forReview);
        whereClause.and().eq("reviewitem", reviewItem);

        //whereClause.and().eq("reviewdate", reviewDate);
        whereClause.and().ge("reviewdate", DateTimeFunctions.removeHMS(reviewDate));
        whereClause.and().lt("reviewdate", DateTimeFunctions.getNextDay(reviewDate));

        qb.orderBy("item",false);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        /*
        String query = "select userid, curdate, forreview, reviewdate,item, reviewitem " +
                "from items where userid=? and date(Curdate)=? and item=? and forreview=1 " +
                " and reviewitem=? and date(reviewdate)=? order by item desc";
         */

        return items;
    }
    @Override
    public List<Item> getItemsAllColumns041(int userid, Date curDate,
                                           boolean forReview /* actually this is a constant "true" */, int reviewItem, Date reviewDate)
            throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        //qb.where().and().eq("item", itemID);
        //qb.where().and().eq("forreview", 1);
        //qb.where().and().eq("reviewitem", reviewItem);
        //qb.where().and().eq("reviewdate", DateTimeFunctions.DateToString(reviewDate));

        //whereClause.and().eq("Curdate", curDate);
        whereClause.and().ge("Curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("Curdate", DateTimeFunctions.getNextDay(curDate));

        //whereClause.and().eq("item", itemID);
        whereClause.and().eq("forreview", forReview);
        whereClause.and().eq("reviewitem", reviewItem);

        //whereClause.and().eq("reviewdate", reviewDate);
        whereClause.and().ge("reviewdate", DateTimeFunctions.removeHMS(reviewDate));
        whereClause.and().lt("reviewdate", DateTimeFunctions.getNextDay(reviewDate));

        qb.orderBy("item",false);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        /*
        String query = "select userid, curdate, forreview, reviewdate,item, reviewitem " +
                "from items where userid=? and date(Curdate)=? and item=? and forreview=1 " +
                " and reviewitem=? and date(reviewdate)=? order by item desc";
         */

        return items;
    }
    @Override
    public List<Item> getItemsAllColumns05(int userid, Date curDate)  throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        //whereClause.and().eq("Curdate", curDate);
        whereClause.and().ge("Curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("Curdate", DateTimeFunctions.getNextDay(curDate));

        qb.orderBy("item",false);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        /*
        String query = "select userid, curdate, forreview, reviewdate,item, reviewitem " +
                "from items where userid=? and date(Curdate)=? order by item desc";
        */

        return items;
    }
    @Override
    public List<Item> getForreviewItems(int userid, boolean forreview)  throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("forreview", Integer.toString(forreview?1:0));
        whereClause.and().eq("forreview", forreview);
        qb.orderBy("curdate",false);
        qb.orderBy("item",true);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        /*
        String query = "select userid, forreview,reviewitem, reviewdate, item, curdate " +
                "from items where userid=? and forreview=? order by curdate desc, item asc";
         */
        return items;
    }

    @Override
    public int getItemCount(int userid, Date curDate)  throws SQLException {
        int result = 0;

        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        //qb.selectRaw("count(*)");
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("curdate", DateTimeFunctions.DateToString(curDate));
        //whereClause.and().eq("curdate", curDate);
        whereClause.and().ge("curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("curdate", DateTimeFunctions.getNextDay(curDate));

        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        //GenericRawResults results = this.queryRaw(qb.prepareStatementString());
        //String[] columns = (String[]) results.getFirstResult();
        //result = (Integer.parseInt(columns[0]));
        result = items.size();

        // String query = "select count(*) as count from items where userid=? and date(curdate)=?";
        return result;
    }
    @Override
    public int getOrigItemCount(int userid, Date curDate)  throws SQLException {
        int result = 0;
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        //qb.selectRaw("count(*)");
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("curdate", DateTimeFunctions.DateToString(curDate));
        //qb.where().and().eq("forreview", 0);

        //whereClause.and().eq("curdate", curDate);
        whereClause.and().ge("curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("curdate", DateTimeFunctions.getNextDay(curDate));

        whereClause.and().eq("forreview", false);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);

        //GenericRawResults results = this.queryRaw(qb.prepareStatementString());
        //String[] columns = (String[]) results.getFirstResult();
        //result = (Integer.parseInt(columns[0]));
        result = items.size();

        //String query = "select count(*) as count from items where forreview=0 and userid=? and date(curdate)=?";
        return result;
    }
    @Override
    public boolean doesRecordExist(int userid, Date curDate, Date reviewDate, int reviewItem)  throws SQLException {
        int result = 0;
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        //qb.selectRaw("count(*)");
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("curdate", DateTimeFunctions.DateToString(curDate));
        //qb.where().and().eq("reviewdate", DateTimeFunctions.DateToString(reviewDate));
        //qb.where().and().eq("forreview", 0);
        //qb.where().and().eq("reviewitem", reviewItem);

        //whereClause.and().eq("curdate", curDate);
        whereClause.and().ge("curdate", DateTimeFunctions.removeHMS(curDate));
        whereClause.and().lt("curdate", DateTimeFunctions.getNextDay(curDate));

        //whereClause.and().eq("reviewdate", reviewDate);
        whereClause.and().ge("reviewdate", DateTimeFunctions.removeHMS(reviewDate));
        whereClause.and().lt("reviewdate", DateTimeFunctions.getNextDay(reviewDate));

        whereClause.and().eq("forreview", false);
        whereClause.and().eq("reviewitem", reviewItem);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);


        //GenericRawResults results = this.queryRaw(qb.prepareStatementString());
        //String[] columns = (String[]) results.getFirstResult();
        //result = (Integer.parseInt(columns[0]));
        result = items.size();
        return (result>=1)?true:false;
    }

    @Override
    public List<Item> getOrigItemsAllColumns(int userid, Date startDate, Date endDate)  throws SQLException {
        QueryBuilder<Item, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("Curdate", DateTimeFunctions.DateToString(curDate));
        if (startDate!=null) {
            whereClause.and().ge("Curdate", startDate);
        }
        if (endDate!=null) {
            whereClause.and().le("Curdate", endDate);
        }
        whereClause.and().eq("forreview", false);
        qb.orderBy("curdate",true);
        qb.orderBy("item",true);
        PreparedQuery<Item> preparedQuery = qb.prepare();
        List<Item> items = this.query(preparedQuery);
        return items;
    }

    @Override
    public void insertFutureItems(Iterator<FutureItem> futureItems, Date curDate) throws SQLException {
        System.out.println("Starting to insert futureItems into items");
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        int itemId = 0;
        int counter = 0;
        int i = 0;
        FutureItem fItem;
        while (futureItems.hasNext()) {
            fItem = futureItems.next();
            if (counter<=0) {
                itemId = getAvailableItemId(fItem.getUserid(), curDate);
            }
            counter++;
            Item item = new Item();
            item.setItem(itemId++);
            item.setCurdate(curDate);
            item.copyFutureItem(fItem);
            this.create(item);
            if (i < (GlobalVariables.batchSize-1)) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
        }
        conn.commit(savePoint);
        System.out.println("Completed to insert futureItems into items, count is " + counter);
    }

    @Override
    public void insertFutureItems02(List<FutureItem> futureItems, Date curDate) throws SQLException {
        System.out.println("Inserting futureItems into items :"
                + futureItems.size());
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        int counter = 0;
        if (futureItems.size()>0) {
            int itemId = getAvailableItemId ( futureItems.get(0).getUserid(), curDate);
            int i = 0;
            for (FutureItem fItem : futureItems) {
                Item item = new Item();
                item.setItem(itemId++);
                item.setCurdate(curDate);
                item.copyFutureItem(fItem);
                this.create(item);
                if (i < GlobalVariables.batchSize) {
                    i++;
                } else {
                    conn.commit(savePoint);
                    i = 0;
                }
                counter++;
            }
        }
        conn.commit(savePoint);
        System.out.println("Completed to insert futureItems into items, count is " + counter);
    }

    @Override
    public void addToRemoveItemCache(Item item) {
        RemoveItemCache.add(item);
    }

    @Override
    public void flushAndClearRemoveItemCache() throws SQLException {
        System.out.println("Flushing RemoveItemCache, count:"
                + RemoveItemCache.size());
        DatabaseConnection conn = this.startThreadConnection();
        Savepoint savePoint = conn.setSavePoint(null);
        int i = 0;
        for (Item item: RemoveItemCache) {
            this.delete(item);
            if (i < GlobalVariables.batchSize) {
                i++;
            } else {
                conn.commit(savePoint);
                i = 0;
            }
        }
        conn.commit(savePoint);
        RemoveItemCache.clear();
    }
}
