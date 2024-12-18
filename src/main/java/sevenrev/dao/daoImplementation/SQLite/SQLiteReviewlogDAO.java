package sevenrev.dao.daoImplementation.SQLite;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import sevenrev.dao.ReviewlogDAO;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by roy on 2017-11-11.
 */
public class SQLiteReviewlogDAO extends BaseDaoImpl<Reviewlog, Integer>
        implements ReviewlogDAO {
    private String DBPath = null;
    //private Context context = null;

    public SQLiteReviewlogDAO(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, Reviewlog.class);
    }
    @Override
    public boolean updateOrInsertIntoReviewlog(Date itemDate, int itemid,
              int userid, Date reviewDate, int itemTime, boolean remember) throws SQLException {

        Reviewlog reviewlog = this.queryReviewlog(userid, itemDate, itemid, reviewDate);
        boolean createNewRecord = ( reviewlog==null? true : false);
        if (createNewRecord) {
            reviewlog = new Reviewlog();
        }
        reviewlog.setUserid(userid);
        reviewlog.setItemid(itemid);
        reviewlog.setItemdate(itemDate);
        reviewlog.setReviewdate(reviewDate);
        reviewlog.setReviewtime(itemTime);
        reviewlog.setRemember(remember);

        int updatedCount;
        if (createNewRecord) {
            updatedCount = this.create(reviewlog);
        } else {
            updatedCount = this.update(reviewlog);
        }

        return updatedCount>=1? true : false;
    }

    /*
    private List<Reviewlog> getReviewlogEntriesfromCursor(Cursor cursor) {
        List<Reviewlog> reviewlogEntries = new ArrayList<Reviewlog>();
        Reviewlog reviewlog = null;
        if (cursor.moveToFirst()) {
            do {
                reviewlog = new Reviewlog();
                reviewlog.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
                reviewlog.setItemid(cursor.getInt(cursor.getColumnIndex("itemid")));
                //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String itemdate, reviewdate;
                Date dateResult;
                itemdate = cursor.getString(cursor.getColumnIndex("itemdate"));
                reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"));
                try {
                    reviewlog.setItemdate(DateTimeFunctions.StringToDate(itemdate));
                    reviewlog.setReviewdate(DateTimeFunctions.StringToDate(reviewdate));
                } catch (ParseException e) {
                    Log.e("Error", "ParseException occurred", e);
                }
                reviewlog.setReviewtime(cursor.getInt(cursor.getColumnIndex("reviewtime")));
                reviewlog.setRemember(cursor.getInt(cursor.getColumnIndex("remember"))>0); //boolean value
                reviewlogEntries.add(reviewlog);
            } while (cursor.moveToNext());
        };
        return reviewlogEntries;
    }

    // This one is not used at 2019/05/21
    private void populateReviewlog(Cursor cursor, Reviewlog reviewlog) {
        reviewlog.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
        reviewlog.setItemid(cursor.getInt(cursor.getColumnIndex("itemid")));

        String itemdate, reviewdate;
        Date dateResult;
        itemdate = cursor.getString(cursor.getColumnIndex("itemdate"));
        reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"));
        //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            reviewlog.setItemdate(DateTimeFunctions.StringToDate(itemdate));
            reviewlog.setReviewdate(DateTimeFunctions.StringToDate(reviewdate));
        } catch (ParseException e) {
            Log.e("Error","Parsing ISO8601 datetime failed", e);
        }
        reviewlog.setReviewtime(cursor.getInt(cursor.getColumnIndex("reviewtime")));
        reviewlog.setRemember(cursor.getInt(cursor.getColumnIndex("remember"))>0); //boolean value
    }
    */
    @Override
    public Reviewlog queryReviewlog(int userid, Date itemDate, int itemid, Date reviewDate) throws SQLException {
        QueryBuilder<Reviewlog, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        //qb.where().and().eq("itemdate", DateTimeFunctions.DateToString(itemDate));
        //qb.where().and().eq("itemid", itemid);
        //qb.where().and().eq("reviewdate", DateTimeFunctions.DateToString(reviewDate));

        //whereClause.and().eq("itemdate", itemDate);
        whereClause.and().ge("itemdate", DateTimeFunctions.removeHMS(itemDate));
        whereClause.and().lt("itemdate", DateTimeFunctions.getNextDay(itemDate));

        whereClause.and().eq("itemid", itemid);
        //whereClause.and().eq("reviewdate", reviewDate);
        whereClause.and().ge("reviewdate", DateTimeFunctions.removeHMS(reviewDate));
        whereClause.and().lt("reviewdate", DateTimeFunctions.getNextDay(reviewDate));

        PreparedQuery<Reviewlog> preparedQuery = qb.prepare();
        List<Reviewlog> reviewlogEntries = this.query(preparedQuery);

        //String query = "select * " +
        //        "from reviewlog where userid=? and date(itemdate)=? and itemid=? and date(reviewdate)=? ";

        return reviewlogEntries.size()>0? reviewlogEntries.get(0) : null ;
    }

    //??? this one has poor performance
    @Override
    public List<Reviewlog> queryReviewlog_user(int userid) throws SQLException {
        QueryBuilder<Reviewlog, Integer> qb = this.queryBuilder();
        qb.where().eq("userid", userid);
        PreparedQuery<Reviewlog> preparedQuery = qb.prepare();
        List<Reviewlog> reviewlogEntries = this.query(preparedQuery);

        //String query = "select * " +
        //        "from reviewlog where userid=?";

        return reviewlogEntries;
    }
    @Override
    public void remove(Reviewlog reviewlog) throws SQLException {
        this.delete(reviewlog);
        //String query = "delete FROM reviewlog where userid=? and itemid=? and itemdate=? and reviewdate=?";
    }
    @Override
    public List<Date> getReviewDatesFromLog(int userid) throws SQLException {
        QueryBuilder<Reviewlog, Integer> qb = this.queryBuilder();
        Where whereClause = qb.where().eq("userid", userid);
        qb.orderBy("reviewdate",false);

        PreparedQuery<Reviewlog> preparedQuery = qb.prepare();
        List<Reviewlog> reviewlogEntries = this.query(preparedQuery);
        List<Date> dates01 = new ArrayList<Date>();
        reviewlogEntries.stream().forEach(element -> {dates01.add(element.getReviewdate());});
        List<Date> reviewDates = dates01.stream().map(DateTimeFunctions::removeHMS).distinct().collect(Collectors.toList());

        //return reviewDates.size()>0? reviewDates : null ;
        return reviewDates;
    }
}
