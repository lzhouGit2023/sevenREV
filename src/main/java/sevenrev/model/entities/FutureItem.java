package sevenrev.model.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;
import sevenrev.dao.daoImplementation.SQLite.SQLiteFutureItemDAO;
import sevenrev.dao.daoImplementation.SQLite.SQLiteItemDAO;

import java.util.Date;

/**
 * Created by roy on 2016-12-01.
 */
@DatabaseTable(tableName = "futureitems", daoClass= SQLiteFutureItemDAO.class)
@Getter
@Setter
public class FutureItem {
    @DatabaseField(id = true)
    private Integer itemkey;

    //@DatabaseField(uniqueCombo = true)
    //private Integer item = null;

    //@DatabaseField(uniqueCombo = true)
    @DatabaseField()
    private int userid;

    //@DatabaseField(uniqueCombo = true)
    @DatabaseField()
    private Integer futureCycles;;

    @DatabaseField()
    private boolean forreview;
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

    public void setUserid(int userId) {
        this.userid = userId;
    }
    public int getUserid() {
        return this.userid;
    }
    public void setFutureCycles(Integer futureCycles) {
        this.futureCycles = futureCycles;
    }
    public Integer getFutureCycles() {
        return this.futureCycles;
    }
    public void setForreview(boolean forreview) {
        this.forreview = forreview;
    }
    public boolean getForreview() {
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
