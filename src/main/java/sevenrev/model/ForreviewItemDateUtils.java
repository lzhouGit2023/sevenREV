package sevenrev.model;

import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ForreviewItemDateUtils {
    public static OrigItemDate getOrigItemDate(Integer forreviewItemParam, Date forreviewDateParam) {
        int index;
        boolean notFound;
        index = 0;
        notFound = true;
        OrigItemDate origItemDate = new OrigItemDate();
        origItemDate.setOrigItemID(-1);

        while (index < GlobalVariables.forreviewItemDates_MaxIndex && notFound) {
            if (DateTimeFunctions.removeHMS(GlobalVariables.forreviewItemDates[index].getForreviewDate())
                    .equals(DateTimeFunctions.removeHMS(forreviewDateParam)) && GlobalVariables.forreviewItemDates[index].getForreviewItemID().equals(forreviewItemParam)) {
                origItemDate.setOrigItemID(GlobalVariables.forreviewItemDates[index].getOrigItemID());
                origItemDate.setOrigDate(GlobalVariables.forreviewItemDates[index].getOrigDate());
                notFound = false;
            } else {
                index ++;
            }
        }
        return origItemDate;
    }

    public static MessageBoxText initForreviewItemDates() {
        MessageBoxText msgBoxText = new MessageBoxText();
        GlobalVariables.forreviewItemDates_MaxIndex = 0;
        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }
        try {
            List<Item> items = GlobalVariables.daoFactory.getItemDAO().getForreviewItems(GlobalVariables.userID, true);
            for(Item item: items) {
                if (GlobalVariables.forreviewItemDates[GlobalVariables.forreviewItemDates_MaxIndex]==null) {
                    GlobalVariables.forreviewItemDates[GlobalVariables.forreviewItemDates_MaxIndex] = new ForreviewItemDate();
                }
                GlobalVariables.forreviewItemDates[GlobalVariables.forreviewItemDates_MaxIndex].setForreviewItemID(item.getItem());
                GlobalVariables.forreviewItemDates[GlobalVariables.forreviewItemDates_MaxIndex].setForreviewDate(item.getCurdate());
                GlobalVariables.forreviewItemDates[GlobalVariables.forreviewItemDates_MaxIndex].setOrigItemID(item.getReviewitem());
                GlobalVariables.forreviewItemDates[GlobalVariables.forreviewItemDates_MaxIndex].setOrigDate(item.getReviewdate());
                GlobalVariables.forreviewItemDates_MaxIndex ++;
            }
        } catch (SQLException sqlEx) {
            msgBoxText.getTexts().add(sqlEx.getMessage());
        }
        return msgBoxText;
    }
}
