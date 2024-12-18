package sevenrev.databaseMigration.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import sevenrev.dao.daoImplementation.SQLite.SQLiteItemDAO;

import java.util.Date;

/**
 * Created by roy on 2016-12-01.
 */
@DatabaseTable(tableName = "items", daoClass= SQLiteItemDAO.class)
@Getter
@Setter
public class SrcItem {
    //private int item;
    @DatabaseField(id = true)
    private String itemkey;
    public void setItemkey(String sth) {
        itemkey = sth;
    }
    public String getItemkey() {
        return userid + "_" + Curdate + "_" + item;
    }

    @DatabaseField(uniqueCombo = true)
    private Integer item = null;

    @DatabaseField(uniqueCombo = true)
    private int userid;

    @DatabaseField(uniqueCombo = true)
    private Date Curdate;

    @DatabaseField()
    private int forreview;
    @DatabaseField()
    private Date reviewdate;
    @DatabaseField()
    //private int reviewitem;
    private Integer reviewitem = null;
    @DatabaseField()
    private String question;
    @DatabaseField()
    private String answer;
    @DatabaseField()
    private int sizeq; //do we really need it?
    @DatabaseField()
    private int sizea; //do we really need it?
    @DatabaseField()
    private int quImageSize; //do we really need it?

    @DatabaseField(columnDefinition = "LONGBLOB",
            dataType = DataType.BYTE_ARRAY)
    private byte[] quImage;

    @DatabaseField()
    private int answImageSize; //do we really need it?

    @DatabaseField(columnDefinition = "LONGBLOB",
            dataType = DataType.BYTE_ARRAY)
    private byte[] answImage;

    @DatabaseField()
    private int reviewtime;

    public void setItem(int item) {
        this.item = item;
    }
    public Integer getItem() {
        return this.item;
    }
    public void setUserid(int userId) {
        this.userid = userId;
    }
    public int getUserid() {
        return this.userid;
    }
    public void setCurdate(Date curdate) {
        this.Curdate = curdate;
    }
    public Date getCurdate() {
        return this.Curdate;
    }
    public void setForreview(int forreview) {
        this.forreview = forreview;
    }
    public int getForreview() {
        return this.forreview;
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
    public Integer getReviewitem() {
        return this.reviewitem;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getQuestion() {
        return this.question;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public String getAnswer() {
        return this.answer;
    }
    public void setReviewtime(int reviewtime) {
        this.reviewtime = reviewtime;
    }
    public int getReviewtime() {
        return this.reviewtime;
    }
}
