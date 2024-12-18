package sevenrev.dao;

import com.j256.ormlite.dao.Dao;
import sevenrev.model.entities.Reviewlog;
import sevenrev.model.entities.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by roy on 2016-11-16.
 */
public interface UserDAO extends Dao<User, Integer>  {
    public void insert(User user) throws SQLException;
    public int getAvailableId() throws SQLException;
    public User queryByKey(int userid) throws SQLException;
    public void remove(int userid) throws SQLException;
    public List<User> getAllUserNameIDs() throws SQLException;
}
