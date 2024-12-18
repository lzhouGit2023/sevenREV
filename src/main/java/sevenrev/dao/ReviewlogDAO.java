package sevenrev.dao;

import com.j256.ormlite.dao.Dao;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.Reviewlog;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by roy on 2017-11-11.
 */
public interface ReviewlogDAO extends Dao<Reviewlog, Integer> {

    //returns true means at least 1 row is updated
    public boolean updateOrInsertIntoReviewlog(Date itemDate, int itemid, int userid,
                      Date reviewDate, int itemTime, boolean remember) throws SQLException;

    public Reviewlog queryReviewlog(int userid, Date itemDate, int itemid, Date reviewDate) throws SQLException;

    //??? this one has poor performance
    public List<Reviewlog> queryReviewlog_user(int userid) throws SQLException;

    public void remove(Reviewlog reviewlog) throws SQLException;

    public List<Date> getReviewDatesFromLog(int userid) throws SQLException;
}
