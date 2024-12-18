package sevenrev.model.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import sevenrev.dao.daoImplementation.SQLite.SQLiteUserDAO;

import java.util.Date;

/**
 * Created by roy on 2016-11-15.
 */
@DatabaseTable(tableName = "users", daoClass= SQLiteUserDAO.class)
public class User {

    @DatabaseField()
    private String name;

    @DatabaseField(id = true)
    private int userid;
    @DatabaseField()
    private boolean reviewed; // false => not reviewed; true => reviewed
    @DatabaseField()
    private Date Curdate;
    @DatabaseField()
    private Date reviewdate;
    @DatabaseField()
    private int reviewitem;
    @DatabaseField()
    private int time1;
    @DatabaseField()
    private int time2;
    @DatabaseField()
    private int time3;
    @DatabaseField()
    private int time4;
    @DatabaseField()
    private int time5;
    @DatabaseField()
    private int time6;
    @DatabaseField()
    private int time7;
    @DatabaseField()
    private int reviewtime;
    @DatabaseField()
    private boolean displaysetting; //false or 0 => default ; true or 1 => only display current date
    @DatabaseField()
    private boolean debugdisplay; //false or 0 => not debug; true or 1 => debug

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public int getUserid() {
        return this.userid;
    }
    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
    public boolean getReviewed() {
        return this.reviewed;
    }
    public void setCurdate(Date Curdate) {
        this.Curdate = Curdate;
    }
    public Date getCurdate() {
        return this.Curdate;
    }
    public void setReviewdate(Date reviewdate) {
        this.reviewdate = reviewdate;
    }
    public Date getReviewdate() {
        return this.reviewdate;
    }
    public void setReviewitem(int reviewitem) {
        this.reviewitem = reviewitem;
    }
    public int getReviewitem() {
        return this.reviewitem;
    }
    public void setTime1(int time1) {
        this.time1 = time1;
    }
    public int getTime1() {
        return this.time1;
    }
    public void setTime2(int time2) {
        this.time2 = time2;
    }
    public int getTime2() {
        return this.time2;
    }

    public void setTime3(int time3) {
        this.time3 = time3;
    }
    public int getTime3() {
        return this.time3;
    }
    public void setTime4(int time4) {
        this.time4 = time4;
    }
    public int getTime4() {
        return this.time4;
    }
    public void setTime5(int time5) {
        this.time5 = time5;
    }
    public int getTime5() {
        return this.time5;
    }
    public void setTime6(int time6) {
        this.time6 = time6;
    }
    public int getTime6() {
        return this.time6;
    }
    public void setTime7(int time7) {
        this.time7 = time7;
    }
    public int getTime7() {
        return this.time7;
    }
    public void setReviewtime(int reviewtime) {
        this.reviewtime = reviewtime;
    }
    public int getReviewtime() {
        return this.reviewtime;
    }

    public void setDisplaysetting(boolean displaysetting) {
        this.displaysetting = displaysetting;
    }
    public boolean getDisplaysetting() {
        return this.displaysetting;
    }
    public void setDebugdisplay(boolean debugdisplay) {
        this.debugdisplay = debugdisplay;
    }
    public boolean getDebugdisplay() {
        return this.debugdisplay;
    }

    @Override
    public String toString() {
        return getName();
    }
}
