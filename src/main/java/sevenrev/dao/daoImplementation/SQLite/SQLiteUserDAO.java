package sevenrev.dao.daoImplementation.SQLite;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import sevenrev.dao.UserDAO;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;
import sevenrev.model.entities.User;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by roy on 2016-11-16.
 */
public class SQLiteUserDAO extends BaseDaoImpl<User, Integer>
        implements UserDAO {
    private String DBPath = null;
    //private Context context = null;

    public SQLiteUserDAO(ConnectionSource connectionSource)
            throws SQLException {
        super(connectionSource, User.class);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        //db.close();
    }
    @Override
    public void insert(User user) throws SQLException {
        if (user.getReviewdate() == null) {
            try {
                user.setReviewdate(DateTimeFunctions.StringToDate("1970-01-01"));
            } catch (ParseException ex) {
                System.out.println("Parse Exception occurred with 1970-01-01");
            }
        }
        if (user.getUserid()==0) {
            user.setUserid(getAvailableId());
        }

        this.create(user);
    }
    @Override
    public int getAvailableId() throws SQLException {
        int result = 1;
        QueryBuilder<User, Integer> qb = this.queryBuilder();
        qb.selectRaw("MAX(userid)");
        GenericRawResults results = this.queryRaw(qb.prepareStatementString());
        String[] columns = (String[]) results.getFirstResult();

        result = (Integer.parseInt(columns[0]!=null? columns[0] : "0" )) + 1;

        //String query = "SELECT max(userid) as userid FROM users";

        return result;
    }
    @Override
    public User queryByKey(int userid) throws SQLException {
        QueryBuilder<User, Integer> qb = this.queryBuilder();
        qb.where().eq("userid", userid);
        PreparedQuery<User> preparedQuery = qb.prepare();
        List<User> userEntries = this.query(preparedQuery);

        //String query = "SELECT * FROM users where userid=" + userid;
        return userEntries.get(0);
    }
    @Override
    public void remove(int userid) throws SQLException {
        User user = queryByKey(userid);
        this.delete(user);
    }
    @Override
    public List<User> getAllUserNameIDs() throws SQLException {
        QueryBuilder<User, Integer> qb = this.queryBuilder();
        PreparedQuery<User> preparedQuery = qb.prepare();
        List<User> userEntries = this.query(preparedQuery);

        //String query = "SELECT * FROM users";

        return userEntries;
    }

    /*
    private void populateUser(Cursor cursor, User user) {
        user.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
        user.setName(cursor.getString(cursor.getColumnIndex("name")));
        user.setReviewed(cursor.getInt(cursor.getColumnIndex("reviewed"))>0); //boolean value

        String Curdate, reviewdate;
        Date dateResult;
        Curdate = cursor.getString(cursor.getColumnIndex("Curdate"));
        reviewdate = cursor.getString(cursor.getColumnIndex("reviewdate"));
        //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            user.setCurdate(DateTimeFunctions.StringToDate(Curdate));
            user.setReviewdate(DateTimeFunctions.StringToDate(reviewdate));
        } catch (ParseException e) {
            Log.e("Error","Parsing ISO8601 datetime failed", e);
        }

        user.setReviewitem(cursor.getInt(cursor.getColumnIndex("reviewitem")));
        user.setTime1(cursor.getInt(cursor.getColumnIndex("time1")));
        user.setTime2(cursor.getInt(cursor.getColumnIndex("time2")));
        user.setTime3(cursor.getInt(cursor.getColumnIndex("time3")));
        user.setTime4(cursor.getInt(cursor.getColumnIndex("time4")));
        user.setTime5(cursor.getInt(cursor.getColumnIndex("time5")));
        user.setTime6(cursor.getInt(cursor.getColumnIndex("time6")));
        user.setTime7(cursor.getInt(cursor.getColumnIndex("time7")));
        user.setDisplaysetting(cursor.getInt(cursor.getColumnIndex("displaysetting"))>0); //boolean value
        user.setDebugdisplay(cursor.getInt(cursor.getColumnIndex("debugdisplay"))>0); //boolean value
    }
    */
}
