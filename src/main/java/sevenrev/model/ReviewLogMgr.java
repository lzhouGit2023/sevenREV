package sevenrev.model;

import java.sql.SQLException;
import java.util.Date;

public class ReviewLogMgr {
    //In VBA possible values for param remember: 1=>remember, 2=> forgot
    //now it is true and false
    public static MessageBoxText UpdateReviewLog(Date itemDate, Integer itemID, Integer userID, Date reviewDate, Integer reviewtime, boolean remember) {
        MessageBoxText msgBoxText = new MessageBoxText();
        Date tmpDate;
        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }
        try {
            GlobalVariables.daoFactory.getReviewlogDAO().updateOrInsertIntoReviewlog(itemDate, itemID, userID, reviewDate, reviewtime, remember);
        } catch (SQLException sqlExp) {
            msgBoxText.getTexts().add(sqlExp.getMessage());
        }
        return msgBoxText;
    }
}
