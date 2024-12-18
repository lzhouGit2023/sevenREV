package sevenrev.model;

import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MoveReviewedItem {
    //This will be decommed
    public MessageBoxText addToBeReviewedItem(Integer itemID, Date itemDate, Integer missingReviewTimes, boolean forreview) {
        MessageBoxText msgBoxText = new MessageBoxText();
        Integer orgItemID;
        Date orgItemDate;

        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }

        if (forreview) {
            OrigItemDate tmpOrigItemDate;
            tmpOrigItemDate = ForreviewItemDateUtils.getOrigItemDate(itemID, itemDate);
            orgItemID = tmpOrigItemDate.getOrigItemID();
            orgItemDate = tmpOrigItemDate.getOrigDate();
        } else {
            orgItemID = itemID;
            orgItemDate = itemDate;
        }

        Date tempDate;
        if (GlobalVariables.reviewtime == 6) {
            tempDate = GlobalVariables.reviewDate;
        } else {
            tempDate = ReviewTime.getPrevDate(6);
        }

        Date dateForAddedItem;
        Integer reviewDateIndex;
        reviewDateIndex = ReviewDate.getReviewDateIndex(GlobalVariables.reviewDate);

        if (reviewDateIndex > 0) {
            //get the date which is the previous date of current review date
            dateForAddedItem = GlobalVariables.reviewDates[reviewDateIndex - 1].getReviewDate();
        } else {
            dateForAddedItem = GlobalVariables.date1;
        }

        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }
        try {
            List<Item> items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns041(GlobalVariables.userID, dateForAddedItem, forreview, orgItemID, orgItemDate);
            if (items.size()==0) {
                //add a review item for date dateForAddedItem
                items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns05(GlobalVariables.userID,dateForAddedItem);
                Integer nextItemID;
                if (items.size()==0) {
                    nextItemID = 1;
                } else {
                    nextItemID = items.get(0).getItem() + 1;
                }

                Item newItem = new Item();
                newItem.setUserid(GlobalVariables.userID);
                newItem.setCurdate(dateForAddedItem);
                newItem.setForreview(true);
                newItem.setItem(nextItemID);
                newItem.setReviewdate(orgItemDate);
                newItem.setReviewitem(orgItemID);

                if(GlobalVariables.debugDisplay == true /* 1 */) {
                    msgBoxText.getTexts().add("Though not reviewed, orig item ( " + orgItemDate + ", " + orgItemID + ") is being added to date " + DateTimeFunctions.DateToString(dateForAddedItem));
                }
                GlobalVariables.daoFactory.getItemDAO().insert(newItem);

                //add to an array so that later the original forreview item will be removed
                if(forreview) {
                    //GlobalVariables.toBeRemovedForreviewItemIDs[GlobalVariables.toBeRemovedForreviewItemIDs_max] = itemID;
                    GlobalVariables.toBeRemovedForreviewItemIDs.add(itemID);
                    GlobalVariables.toBeRemovedForreviewItemIDs_max ++;
                }
            } else {
                //??? do we need to check the review time of the orig one
            }
        } catch (SQLException sqlEx) {
            msgBoxText.getTexts().add(sqlEx.getMessage());
        }

        return msgBoxText;
    }

    public MessageBoxText addToBeReviewedItem02(Item item) {
    //params: Integer itemID, Date itemDate, Integer missingReviewTimes, boolean forreview
        Integer itemID = item.getItem();
        Date itemDate = item.getCurdate();
        boolean forreview = item.getForreview();

        MessageBoxText msgBoxText = new MessageBoxText();
        Integer orgItemID;
        Date orgItemDate;

        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }

        if (forreview) {
            OrigItemDate tmpOrigItemDate;
            tmpOrigItemDate = ForreviewItemDateUtils.getOrigItemDate(itemID, itemDate);
            orgItemID = tmpOrigItemDate.getOrigItemID();
            orgItemDate = tmpOrigItemDate.getOrigDate();
        } else {
            orgItemID = itemID;
            orgItemDate = itemDate;
        }

        Date tempDate;
        if (GlobalVariables.reviewtime == 6) {
            tempDate = GlobalVariables.reviewDate;
        } else {
            tempDate = ReviewTime.getPrevDate(6);
        }

        Date dateForAddedItem;
        Integer reviewDateIndex;
        reviewDateIndex = ReviewDate.getReviewDateIndex(GlobalVariables.reviewDate);

        if (reviewDateIndex > 0) {
            //get the date which is the previous date of current review date
            dateForAddedItem = GlobalVariables.reviewDates[reviewDateIndex - 1].getReviewDate();
        } else {
            dateForAddedItem = GlobalVariables.date1;
        }

        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }
        try {
            List<Item> items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns041(GlobalVariables.userID, dateForAddedItem, forreview, orgItemID, orgItemDate);
            if (items.size()==0) {
                //add a review item for date dateForAddedItem
                items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns05(GlobalVariables.userID,dateForAddedItem);
                Integer nextItemID;
                if (items.size()==0) {
                    nextItemID = 1;
                } else {
                    nextItemID = items.get(0).getItem() + 1;
                }

                Item newItem = new Item();
                newItem.setUserid(GlobalVariables.userID);
                newItem.setCurdate(dateForAddedItem);
                newItem.setForreview(true);
                newItem.setItem(nextItemID);
                newItem.setReviewdate(orgItemDate);
                newItem.setReviewitem(orgItemID);

                if(GlobalVariables.debugDisplay == true /* 1 */) {
                    msgBoxText.getTexts().add("Though not reviewed, orig item ( " + orgItemDate + ", " + orgItemID + ") is being added to date " + DateTimeFunctions.DateToString(dateForAddedItem));
                }
                GlobalVariables.daoFactory.getItemDAO().insert(newItem);

                //add to an array so that later the original forreview item will be removed
                if(forreview) {
                    //GlobalVariables.toBeRemovedForreviewItemIDs[GlobalVariables.toBeRemovedForreviewItemIDs_max] = item.getItemkey();
                    GlobalVariables.toBeRemovedForreviewItemIDs.add(item.getItemkey());
                    GlobalVariables.toBeRemovedForreviewItemIDs_max ++;
                }
            } else {
                //??? do we need to check the review time of the orig one
            }
        } catch (SQLException sqlEx) {
            msgBoxText.getTexts().add(sqlEx.getMessage());
        }

        return msgBoxText;
    }

    public static void initToBeRemovedForreviewItemIDs() {
        GlobalVariables.toBeRemovedForreviewItemIDs.clear();
        GlobalVariables.toBeRemovedForreviewItemIDs_max = 0;
    }

    public static MessageBoxText removeToBeRemovedForreviewItemIds() {
        String tmpStr;
        Integer tmpIndex;

        MessageBoxText msgBoxText = new MessageBoxText();
        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }

        if(GlobalVariables.toBeRemovedForreviewItemIDs_max > 0) {
            tmpIndex = 0;
            /* ??? to be worked on

            while (tmpIndex < GlobalVariables.toBeRemovedForreviewItemIDs_max) {
                If tmpIndex >0 Then
                        tmpStr = tmpStr & "," & toBeRemovedForreviewItemIDs(tmpIndex)
                Else
                        tmpStr = toBeRemovedForreviewItemIDs(tmpIndex)
                End If
                tmpIndex = tmpIndex + 1
            }
            */

            //??? here the functionality is not implemented
            //GlobalVariables.daoFactory.getItemDAO().deleteIds()
            //M1.conn1.Execute
            //"delete from items where userid=" & M1.userID & " and curdate=#" & M1.convDate(M1.reviewDate) & "# and forreview=1 and item in (" & tmpStr & ")"
            //toBeRemovedForreviewItemIDs_max = 0;
            //??? need to also clean up the array


        }
        //???
        return null;
    }

    public static MessageBoxText removeToBeRemovedForreviewItemIds02() {
        String tmpStr;
        Integer tmpIndex;

        MessageBoxText msgBoxText = new MessageBoxText();
        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }

        if(GlobalVariables.toBeRemovedForreviewItemIDs_max > 0) {
            /*
            tmpIndex = 0;
            List<Integer> itemKeys = new LinkedList<>();
            while (tmpIndex < GlobalVariables.toBeRemovedForreviewItemIDs_max) {
                itemKeys.add(GlobalVariables.toBeRemovedForreviewItemIDs[tmpIndex]);
                tmpIndex ++;
            }
            */

            try {
                GlobalVariables.daoFactory.getItemDAO().deleteIds(GlobalVariables.toBeRemovedForreviewItemIDs);
            } catch (SQLException sqlEx) {
                msgBoxText.getTexts().add(sqlEx.getMessage());
            }
            GlobalVariables.toBeRemovedForreviewItemIDs.clear();
            GlobalVariables.toBeRemovedForreviewItemIDs_max = 0;
        }
        return msgBoxText;
    }
}
