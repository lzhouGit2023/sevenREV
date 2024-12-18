package sevenrev.model;

import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewDate {
    public static MessageBoxText initReviewDates() {
        GlobalVariables.reviewDates_index = 0;
        MessageBoxText msgBoxText = new MessageBoxText();
        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is null");
            return msgBoxText;
        }

        List<Date> reviewDates = null;
        try {
            List<Date> dates01 = GlobalVariables.daoFactory.
                    getReviewlogDAO().getReviewDatesFromLog(GlobalVariables.userID)
                    .stream().collect(Collectors.toList());
            //make it in ascending order so that it can use Collections.sort(..)
            if (dates01!=null && dates01.size()>1) {
                Collections.reverse(dates01);
            }
            List<Date> dates02 = GlobalVariables.daoFactory.
                    getItemDAO().getDates02(GlobalVariables.userID)
                    .stream().collect(Collectors.toList());
            if (dates02!=null && dates02.size()>1) {
                Collections.reverse(dates02);
            }
            dates01.addAll(dates02);
            Collections.sort(dates01);
            reviewDates = dates01.stream().distinct().collect(Collectors.toList());
            Collections.reverse(reviewDates);
        } catch (SQLException ex) {
            msgBoxText.getTexts().add(ex.getMessage());
        }
        if (reviewDates!=null && reviewDates.size()>0) {
            for (Date tmpDate : reviewDates) {
                GlobalVariables.reviewDates[GlobalVariables.reviewDates_index] = new ItemReviewDate();
                GlobalVariables.reviewDates[GlobalVariables.reviewDates_index].setReviewDate(tmpDate);
                GlobalVariables.reviewDates[GlobalVariables.reviewDates_index].setIndex(GlobalVariables.reviewDates_index);
                GlobalVariables.reviewDates_index++;
            }
        }
        return msgBoxText;
    }

    public static Integer getReviewDateIndex(Date reviewDate) {
        int result = -1;

        int tmpIndex = 0;
        boolean found = false;

        while ((!found) && (tmpIndex < GlobalVariables.reviewDates_index)) {
            if(DateTimeFunctions.removeHMS(GlobalVariables.reviewDates[tmpIndex].getReviewDate())
                    .equals(DateTimeFunctions.removeHMS(reviewDate))) {
                found = true;
                result = tmpIndex;
            }
            tmpIndex ++;
        }
        return result;
    }
}
