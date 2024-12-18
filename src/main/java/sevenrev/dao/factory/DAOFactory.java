package sevenrev.dao.factory;

// import android.content.Context;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import sevenrev.dao.FutureItemDAO;
import sevenrev.dao.ItemDAO;
import sevenrev.dao.ReviewlogDAO;
import sevenrev.dao.UserDAO;
import sevenrev.dao.daoImplementation.SQLite.SQLiteFutureItemDAO;
import sevenrev.dao.daoImplementation.SQLite.SQLiteItemDAO;
import sevenrev.dao.daoImplementation.SQLite.SQLiteReviewlogDAO;
import sevenrev.dao.daoImplementation.SQLite.SQLiteUserDAO;

import java.sql.SQLException;

/**
 * Created by roy on 2016-11-16.
 */
public class DAOFactory {
    //private Connection dbConn;
    private String DBPath;
    //private Context context;
    private ConnectionSource connectionSource;
    UserDAO userDAO = null;
    ItemDAO itemDAO = null;
    ReviewlogDAO reviewlogDAO = null;
    FutureItemDAO futureItemDAO = null;

    public DAOFactory(String path) throws SQLException {
        this.DBPath = path;
        this.connectionSource =
                new JdbcConnectionSource(DBPath);
        //this.context = context;
    }
    public UserDAO getUserDAO() throws SQLException {
        if (userDAO==null) {
            userDAO = new SQLiteUserDAO(this.connectionSource);
        }
        //return new SQLiteUserDAO(SQLiteDatabase.openDatabase(this.DBPath, null, 0));
        return userDAO;
    }
    public ItemDAO getItemDAO() throws SQLException {
        if (itemDAO==null) {
            itemDAO = new SQLiteItemDAO(this.connectionSource);
        }
        //return new SQLiteUserDAO(SQLiteDatabase.openDatabase(this.DBPath, null, 0));
        return itemDAO;
    }

    public ReviewlogDAO getReviewlogDAO() throws SQLException {
        if (reviewlogDAO==null) {
            reviewlogDAO = new SQLiteReviewlogDAO(this.connectionSource);
        }
        //return new SQLiteUserDAO(SQLiteDatabase.openDatabase(this.DBPath, null, 0));
        return reviewlogDAO;
    }

    public FutureItemDAO getFutureItemDAO() throws SQLException {
        if (futureItemDAO==null) {
            futureItemDAO = new SQLiteFutureItemDAO(this.connectionSource);
        }
        return futureItemDAO;
    }
}
