package sevenrev.model;

import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ReviewTime {
    public static Date getPrevDate(Integer whichtime) {
        int whichday, i1;
        whichday = 1;
        //getPrevDate = DateSerial(1000, 1, 1)
        Date result = new Date(0l);
        switch(whichtime) {
            case 1: //' get time1 days ago from current day
            whichday = GlobalVariables.time1;
            break;
            case 2: //'get time2 days ago from current day
            whichday = GlobalVariables.time2;
            break;
            //'getPrevDate = DateSerial(Year(M1.date1), Month(M1.date1), Day(M1.date1) - 2)
            case 3: //'get time3 days ago from current day
            whichday = GlobalVariables.time3;
            break;
            case 4:  //'get time4 days ago from current day
            whichday = GlobalVariables.time4;
            break;
            case 5:  //'get time5 days ago from current day
            whichday = GlobalVariables.time5;
            break;
            case 6:  //'get time6 days ago from current day
            whichday = GlobalVariables.time6;
            break;
            case 7:  //'get time7 days ago from current day
            whichday = GlobalVariables.time7;
        };
        if (GlobalVariables.daoFactory == null) {
            System.err.println("GlobalVariables.daoFactory is null");
            return null;
        }
        try {
            List<Date> dates = GlobalVariables.daoFactory.getItemDAO().getDates01(GlobalVariables.userID, GlobalVariables.date1);
            if (dates.size()>0) {
                i1 = 1;
                int index=0;
                while (index<dates.size() && i1<whichday) {
                    if (DateTimeFunctions.removeHMS(dates.get(index))
                            .equals(DateTimeFunctions.removeHMS(GlobalVariables.date1))
                            ||DateTimeFunctions.removeHMS(dates.get(index))
                            .after(DateTimeFunctions.removeHMS(GlobalVariables.date1))) {
                    } else {
                        i1 ++;
                    };
                    index ++;
                }
                if (index<dates.size()) {
                    result = dates.get(index);
                }
            }

        } catch (SQLException sqlEx) {
            System.err.println("Error occurred: " + sqlEx.getMessage());
            return null;
        }
        return result;
    }

    //This method updates the reviewTime field of the orig item
    public static Integer updateReviewTime(Integer userID, Integer itemID, Date itemDate, boolean canRemember) {
        int tmpItemID;
        Date tmpItemDate;
        int tmpReviewTime;
        Integer result = null;
        int updateRes = 0;

        if (GlobalVariables.daoFactory == null) {
            System.err.println("GlobalVariables.daoFactory is null");
            return null;
        }
        try {
            List<Item> items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(userID, itemDate, itemID);
            if (items.size()>0 ) {
                //rs3.MoveFirst
                //If rs3.Fields("forreview").Value = 1 Then
                if (items.get(0).getForreview()) {
                    tmpItemID = items.get(0).getReviewitem();
                    tmpItemDate = items.get(0).getReviewdate();
                    items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(userID, tmpItemDate, tmpItemID);
                } else {
                    tmpItemID = itemID;
                    tmpItemDate = items.get(0).getCurdate();
                }
                result = items.get(0).getReviewtime();
                tmpReviewTime = items.get(0).getReviewtime();
                //If(canRemember) And(tmpReviewTime <= M1.steps) Then
                if (canRemember && tmpReviewTime <= GlobalVariables.steps) {
                    tmpReviewTime ++ ;
                    String tmpStr;
                    tmpStr = DateTimeFunctions.DateToString(tmpItemDate);
                    //Dim RsRec As item7Rev
                    //RsRec = getItemFields(rs3)

                    //M1.conn1.Execute
                    //"update items set reviewtime=" & tmpReviewTime & ", reviewdate=#" & convDate(M1.date1) & "# where userid=" & M1.userID & " and item=" & tmpItemID & " and curdate=#" & convDate(tmpItemDate) & "# and reviewdate<>#" & convDate(M1.date1) & "#"
                    items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(userID, tmpItemDate, tmpItemID);
                    items.get(0).setReviewtime(tmpReviewTime);
                    items.get(0).setReviewdate(GlobalVariables.date1);
                    updateRes = GlobalVariables.daoFactory.getItemDAO().update(items.get(0));
                } else {
                    //M1.conn1.Execute
                    //"update items set reviewdate=#" & convDate(M1.date1) & "# where userid=" & M1.userID & " and item=" & tmpItemID & " and curdate=#" & convDate(tmpItemDate) & "# and reviewdate<>#" & convDate(M1.date1) & "#"
                    items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(userID, tmpItemDate, tmpItemID);
                    if (!(DateTimeFunctions.removeHMS(items.get(0).getReviewdate())
                            .equals(DateTimeFunctions.removeHMS(GlobalVariables.date1)))) {
                        //??? is there any concern with the enclosing if condition; why do we need it???
                        items.get(0).setReviewtime(tmpReviewTime);
                        items.get(0).setReviewdate(GlobalVariables.date1);
                        updateRes = GlobalVariables.daoFactory.getItemDAO().update(items.get(0));
                    }
                } //End If 'If (canRemember) Then

            } else {
                System.err.println("sth is wrong, cannot find the item");
                return null;
            }

        } catch (SQLException sqlEx) {
            System.err.println("Error occurred: " + sqlEx.getMessage());
            return null;
        }
        return result;
    }
    public static Integer nextReviewTime(Integer currentTime) {
        int nextTime;
        nextTime = currentTime + 1;
        if (GlobalVariables.skipTimes) {
            int index01;
            index01 = 0;
            while ((nextTime <= GlobalVariables.steps) && (GlobalVariables.toSkipTimes[nextTime] == 1)) {
                nextTime ++;
            }
        }
        return nextTime;
    }
    public static void initToSkipTimes() {
        if (GlobalVariables.skipTimes) {
            int index01 = 0;
            while(index01 <= GlobalVariables.steps) {
                GlobalVariables.toSkipTimes[index01] = 0;
                index01 ++;
            }
            GlobalVariables.toSkipTimes[3] = 1;// ' skip when time is 3
            GlobalVariables.toSkipTimes[5] = 1;// ' skip when time is 5
        }
    }

    public static Integer getDayCount(Date currentDate, Date lastReviewDate) {
        int index01;
        index01 = 0;
        boolean currentDate_Found;
        boolean lastReviewDate_Found;
        currentDate_Found = false;
        lastReviewDate_Found = false;
        int currentDate_Index;
        int lastReviewDate_Index;
        currentDate_Index = 0;
        lastReviewDate_Index = 0;
        if (lastReviewDate.before(GlobalVariables.reviewDates[GlobalVariables.reviewDates_index - 1].getReviewDate())) {
            lastReviewDate_Index = GlobalVariables.reviewDates_index;
            lastReviewDate_Found = true;
        }
        if (currentDate.after(GlobalVariables.reviewDates[0].getReviewDate())) {
            currentDate_Index = -1;
            currentDate_Found = true;
        }
        while ((index01 < GlobalVariables.reviewDates_index) && ((! currentDate_Found) || (! lastReviewDate_Found))) {
            if(! currentDate_Found) {
                if(DateTimeFunctions.removeHMS(GlobalVariables.reviewDates[index01].getReviewDate())
                        .equals(DateTimeFunctions.removeHMS(currentDate))) {
                    currentDate_Index = index01;
                    currentDate_Found = true;
                }
            }
            if(! lastReviewDate_Found) {
                if(!(GlobalVariables.reviewDates[index01].getReviewDate().after(lastReviewDate))) {
                    lastReviewDate_Index = index01;
                    lastReviewDate_Found = true;
                }
            }
            index01 ++;
        }
        return lastReviewDate_Index - currentDate_Index;
    }
    public static Integer getDaysInterval(int whichtime) {
        int result = 1;
        switch(whichtime) {
            case 1: //' get time1 days ago from current day
                result = GlobalVariables.time1;
                break;
            case 2: //'get time2 days ago from current day
                result = GlobalVariables.time2;
                break;
            case 3: //'get time3 days ago from current day
                result = GlobalVariables.time3;
                break;
            case 4:  //'get time4 days ago from current day
                result = GlobalVariables.time4;
                break;
            case 5:  //'get time5 days ago from current day
                result = GlobalVariables.time5;
                break;
            case 6:  //'get time6 days ago from current day
                result = GlobalVariables.time6;
                break;
            case 7:  //'get time7 days ago from current day
                result = GlobalVariables.time7;
        }
        return result;
    }

    //' this function is used to calculate whether the review times between the two dates satisfies the passed in review time
    //' the parms itemID and itemDate do not need to be the orig items
    //' returned values 0 means reviewtime is satisfied; 2 means item has been reviewed
    //' returned value 1 means item should be skipped
    public static ReturnWithData reviewTimeSatisfied(Item RsRec) {
        int itemReviewTime;
        int reviewDays;
        int orgItemID;
        Date orgItemDate;
        Item origItem = null;
        Date itemLastReviewDate;
        ReturnWithData resultData = new ReturnWithData();
        int result;
        int futureCycleCount = 0;
        FutureItem futureItem = null;
        //'-100 is the default value which is meaningless
        result = -100;

        if (GlobalVariables.daoFactory == null) {
            System.err.println("GlobalVariables.daoFactory is null");
            resultData.getMessageBoxText().getTexts().add("GlobalVariables.daoFactory is null");
            return resultData;
        }

        //Set rsMain = CreateObject("ADODB.Recordset")
        if (RsRec.getForreview()) {
            orgItemID = RsRec.getReviewitem();
            orgItemDate = RsRec.getReviewdate();
            List<Item> items = null;
            try {
                items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID, orgItemDate, orgItemID);
            } catch (SQLException sqlEx) {
                System.err.println(sqlEx.getMessage());
                resultData.getMessageBoxText().getTexts().add(sqlEx.getMessage());
            }
            if (items != null && items.size()>0) {
                origItem = items.get(0);
                itemReviewTime = origItem.getReviewtime();
                itemLastReviewDate = origItem.getReviewdate();
            } else {
                System.err.println("In reviewTimeSatisfied, sth. is wrong");
                resultData.getMessageBoxText().getTexts().add("In reviewTimeSatisfied, sth. is wrong");
                return resultData;
            }
        } else {
            origItem = RsRec;
            orgItemID = RsRec.getItem();
            orgItemDate = RsRec.getCurdate();
            itemReviewTime = RsRec.getReviewtime();
            itemLastReviewDate = RsRec.getReviewdate();
        }

        int expectedDaysInterval;

        if (itemReviewTime <= GlobalVariables.steps) {
            expectedDaysInterval = getDaysInterval(itemReviewTime);
            if (DateTimeFunctions.removeHMS(itemLastReviewDate)
                    .equals(DateTimeFunctions.removeHMS(GlobalVariables.date1))) {
                reviewDays = 0;
                result = 2;
                System.err.println("This item was reviewed on the same date?!");
                //No need to output the following
                //resultData.getMessageBoxText().getTexts().add("This item was reviewed on the same date?!");
            } else {
                reviewDays = getDayCount(GlobalVariables.date1, itemLastReviewDate);
            }
            //End If 'end of If (itemLastReviewDate = M1.date1) Then
            if (reviewDays >= expectedDaysInterval) {
                result = 0;
                if (GlobalVariables.skipTimes) {
                    if (GlobalVariables.toSkipTimes[itemReviewTime] == 1) {
                        //' this item should be skipped
                        result = 1;
                    }
                }
            } else if (result != 2) {
                //'In the following assignment, reviewTimeSatisfied must be a negative value
                result = 3;
                futureItem = new FutureItem();
                futureItem.setForreview(true);
                futureItem.setFutureCycles(expectedDaysInterval-reviewDays);
                futureItem.setUserid(GlobalVariables.userID);
                futureItem.setReviewitem(orgItemID);
                futureItem.setReviewdate(orgItemDate);
                futureItem.setReviewtime(origItem.getReviewtime());
                futureCycleCount = reviewDays - expectedDaysInterval;
            }
        } else {
            result = 1;
        }

        resultData.setScenario(new Integer(result));
        resultData.setItem(origItem);
        resultData.setFutureItem(futureItem);
        return resultData;
    }
}
