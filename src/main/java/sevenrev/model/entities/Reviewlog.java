package sevenrev.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import sevenrev.dao.daoImplementation.SQLite.SQLiteReviewlogDAO;

import java.util.Date;

/**
 * Created by roy on 2017-11-11.
 */
@DatabaseTable(tableName = "reviewlog", daoClass= SQLiteReviewlogDAO.class)
@Getter
@Setter
public class Reviewlog {
    @DatabaseField(id = true)
    private Integer reviewlogID;
    @DatabaseField(uniqueCombo = true)
    private int userid;
    @DatabaseField(uniqueCombo = true)
    private int itemid;
    @DatabaseField(uniqueCombo = true)
    private Date itemdate;
    @DatabaseField(uniqueCombo = true)
    private Date reviewdate;
    @DatabaseField
    private int reviewtime;
    @DatabaseField
    private boolean remember;

    public void setUserid(int userId) {
        this.userid = userId;
    }
    public int getUserid() {
        return this.userid;
    }
    public void setItemid(int itemid) {
        this.itemid = itemid;
    }
    public int getItemid() {
        return this.itemid;
    }

    public void setItemdate(Date itemdate) {
        this.itemdate = itemdate;
    }
    public Date getItemdate() {
        return this.itemdate;
    }
    public void setReviewdate(Date reviewdate) {
        this.reviewdate = reviewdate;
    }
    public Date getReviewdate() {
        return this.reviewdate;
    }
    public void setReviewtime(int reviewtime) {
        this.reviewtime = reviewtime;
    }
    public int getReviewtime() {
        return this.reviewtime;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
    public boolean getRemember() {
        return this.remember;
    }
}
