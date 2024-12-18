package sevenrev.model.ReviewOldItems;

import lombok.Getter;
import lombok.Setter;
import sevenrev.model.GlobalVariables;
import sevenrev.model.ReturnWithData;
import sevenrev.model.ReviewTime;
import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;
import sevenrev.model.itemPickingLogic.ReviewedCache;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by roy_normal on 6/6/2019.
 */

@Getter
@Setter
public class NavigateItems {
    DataForCommunication dataForCommunication;

    private List<Item> rs1;
    //private Item currentItem;
    private int currentItemIndex;

    private int flag;
    private String question;
    private String answer;
    private boolean checkanswer;

    public NavigateItems(DataForCommunication dataForCommunication) {
        this.dataForCommunication = dataForCommunication;
    }

    public boolean isRSNotEOF() /** means not EOF*/ {
        if ((rs1!=null) && (currentItemIndex < rs1.size())) {
            return true;
        } else {
            //??? do we need the following
            //dataForCommunication.getMessageBoxText().getTexts().add("Error: reached EOF of current resultset");
            return false;
        }
    }

    public boolean MoveToNext02(boolean initialMove) {
        boolean MoveToNext02 = true;
        try {

            checkanswer = true;
            int flag1 = 0;
            //M1.checkedItemCount = M1.check60:45edItemCount + 1
            GlobalVariables.checkedItemCount++;

            //if (rs1!=null && currentItemIndex<rs1.size())
            if (isRSNotEOF()) {
                currentItemIndex++;
            } else {
                rs1 = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns02(GlobalVariables.userID, GlobalVariables.reviewDate);
                currentItemIndex = 0;
                flag1 = 1;
            }

            //while (currentItemIndex >= rs1.size() && (flag1 == 0 || flag1 == 1))
            while ((!isRSNotEOF()) && (flag1 == 0 || flag1 == 1))
            {
                if (GlobalVariables.reviewtime <= GlobalVariables.steps) {
                    GlobalVariables.reviewtime = ReviewTime.nextReviewTime(GlobalVariables.reviewtime);
                    GlobalVariables.reviewDate = ReviewTime.getPrevDate(GlobalVariables.reviewtime);
                    rs1 = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns02(GlobalVariables.userID, GlobalVariables.reviewDate);
                    currentItemIndex = 0;
                    flag1 = 1;
                } else {
                    flag1 = 2; // in this case review is done
                }
            }

            if (flag1 == 2) {
                //' flag1=2 means review is done
                //'rs1.Close
                GlobalVariables.reviewed = true;
                //'disable some of the buttons
                dataForCommunication.setCommand5Enabled(false);
                dataForCommunication.setCommandButton4Enabled(false);
                dataForCommunication.setCommandButton3Enabled(false);
                dataForCommunication.setCommandButton1Enabled(false);

                //'display the number of items in cache
                //MsgBox "Review for current date is done, there are " & (M1.RSCacheItem) & " items reviewed"
                dataForCommunication.getMessageBoxText().getTexts().add("Review for current date is done, there are " + GlobalVariables.RSCacheItem + " items reviewed");
            } else {
                //' check whether whether the selected item satisfies the reviewtime
                ReturnWithData dataReturned = null;
                int scenario = 0;
                Item currentItem = rs1.get(currentItemIndex);
                if (ReviewedCache.InReviewedCache(currentItem.getForreview(), currentItem.getReviewdate(),
                        currentItem.getReviewitem(), currentItem.getCurdate(), currentItem.getItem())) {
                    MoveToNext02 = false;
                } else {
                    //reviewtimeOK = ReviewTime.reviewTimeSatisfied(currentItem);
                    dataReturned = ReviewTime.reviewTimeSatisfied(currentItem);
                    if (dataReturned.getMessageBoxText().getTexts().size() > 0) {
                        dataForCommunication.getMessageBoxText().getTexts().addAll(dataReturned.getMessageBoxText().getTexts());
                        return false;
                    }
                    scenario = (Integer) dataReturned.getScenario();
                    if (scenario == 0 || scenario == 2) {
                        if (flag1 == 1) {
                            //rs1.MoveFirst
                            currentItemIndex = 0;
                        }
                        // ' populate GUI fields
                        if (isRSNotEOF()) {
                            MoveToNext02 = cacheItemData(currentItem.getCurdate(), currentItem.getItem(), currentItem.getForreview());
                        }
                    } else {
                        if (scenario == 3) {
                            //tbd ??? do we need the following
                            //' M1.movedForreviewItemCount = M1.movedForreviewItemCount + 1
                            //' addToBeReviewedItem rs1.Fields("item"), rs1.Fields("curdate"), reviewtimeOK, rs1.Fields("forreview")

                            if (!initialMove)
                            {
                                Item toBeRemovedItem = dataReturned.getItem();
                                if (toBeRemovedItem.getForreview()) {
                                    GlobalVariables.daoFactory.getItemDAO().addToRemoveItemCache(toBeRemovedItem);
                                }
                                GlobalVariables.daoFactory.getFutureItemDAO().addToFutureItemCache(dataReturned.getFutureItem());
                                System.out.println("Added to future item cache");
                            }
                        } else {

                        }
                        GlobalVariables.skippedItemCount++;
                        MoveToNext02 = false;
                    }
                    //End If ' end of If (M1.InReviewedCache(...))
                }
            }
        } catch (SQLException ex) {
            dataForCommunication.getMessageBoxText().getTexts().add("Error : " + ex.getMessage());
        }
        return MoveToNext02;
    }

    public boolean cacheItemData(Date itemDate, Integer itemID, boolean forreview) {

        boolean cacheItemData = true;
    //Set rsMain = CreateObject("ADODB.Recordset")
    //rsMain.Open "SELECT * FROM items where userid=" & M1.userID & " and curdate=#" & M1.convDate(itemDate) & "# and item=" & itemID, M1.conn1
//getItemsAllColumns03
        try {
            List<Item> rsMain = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID, itemDate, itemID);
            //if rsMain.EOF() Then
            if (rsMain.size() == 0) {
                cacheItemData = false;
                return cacheItemData;
            }
            Item rsMain_item = rsMain.get(0);
            //'check if it's a review item (forreview=1) of a some prev day or not
            //If rsMain.Fields("forreview").Value = 1 Then ' it's a review item
            if (rsMain_item.getForreview()) {
                //Set rs2 = CreateObject("ADODB.Recordset")
                //rs2.Open "SELECT question, answer, sizeq, sizea, quImage,quImageSize,answImage,answImageSize FROM items where userid=" & M1.userID & " and curdate=#" & M1.convDate(rsMain.Fields("reviewdate").Value) & "# and item=" & rsMain.Fields("reviewitem").Value, M1.conn1
                List<Item> rs2 = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID, rsMain_item.getReviewdate(), rsMain_item.getReviewitem());
                //if (Not rs2.EOF)
                if (rs2.size() > 0) {
                    //rs2.MoveFirst
                    Item rs2_item = rs2.get(0);
                    //If (rs2.Fields("sizeq").Value = 0) Then
                    if (rs2_item.getSizeq() == 0) {
                        //MsgBox
                        //"sth wrong, " & rsMain.Fields("reviewdate").Value & "item " & rsMain.Fields("reviewitem").Value & " should be orig, rsMain is " & rsMain.Fields("curdate").Value & " item " & rsMain.Fields("item").Value
                        dataForCommunication.getMessageBoxText().getTexts().add("Error: sth wrong, "
                                + rsMain_item.getReviewdate() + " item " + rsMain_item.getReviewitem() +
                                " should be orig, rsMain is " + rsMain_item.getCurdate() + " item " + rsMain_item.getItem());
                    }
                    //question = ReadField("question", rs2.Fields("sizeq").Value, rs2)
                    //answer = ReadField("answer", rs2.Fields("sizea").Value, rs2)
                    question = rs2_item.getQuestion();
                    answer = rs2_item.getAnswer();
                    GlobalVariables.quImageSize = rs2_item.getQuImageSize();
                    if (GlobalVariables.quImageSize > 0) {
                        //Text1.Height = 87
                        //Text1.Visible = False
                        //Text1.Visible = True
                        //M1.quImageBytes() = rs2("quImage").GetChunk(M1.quImageSize)
                        dataForCommunication.setText1Height(87);
                        dataForCommunication.setText1Visible(true);
                        GlobalVariables.quImageBytes = rs2_item.getQuImage();
                    } else {
                        //Text1.Height = 260
                        //Text1.Visible = False
                        //Text1.Visible = True
                        dataForCommunication.setText1Height(260);
                        dataForCommunication.setText1Visible(true);
                    }

                    GlobalVariables.answImageSize = rs2_item.getAnswImageSize();
                    if (GlobalVariables.answImageSize > 0) {
                        GlobalVariables.answImageBytes = rs2_item.getAnswImage();
                    }

                    GlobalVariables.reviewitem = rsMain_item.getItem();
                } else {
                    //'in this case, probably review time is larger
                    //MsgBox
                    //"missing orig data on " & rsMain("reviewdate") & ", item " & rsMain("reviewitem") & " curr item " & rsMain("curdate") & " " & rsMain("item")
                    //MsgBox
                    //"sql is " & "SELECT question, answer, sizeq, sizea, quImage,quImageSize,answImage,answImageSize FROM items where userid=" & M1.userID & " and curdate=#" & M1.convDate(rsMain.Fields("reviewdate").Value) & "# and item=" & rsMain.Fields("reviewitem").Value
                    dataForCommunication.getMessageBoxText().getTexts().add("missing orig data on " + rsMain_item.getReviewdate() +
                            ", item " + rsMain_item.getReviewitem() + " curr item " + rsMain_item.getCurdate() + " " + rsMain_item.getItem());
                    cacheItemData = false;
                }

            } else { //' of If rsMain.Fields("forreview").Value = 1 Then
                //question = ReadField("question", rsMain.Fields("sizeq").Value, rsMain)
                //answer = ReadField("answer", rsMain.Fields("sizea").Value, rsMain)
                question = rsMain_item.getQuestion();
                answer = rsMain_item.getAnswer();
                GlobalVariables.quImageSize = rsMain_item.getQuImageSize();
                if (GlobalVariables.quImageSize > 0) {
                    //Text1.Height = 87
                    //Text1.Visible = False
                    //Text1.Visible = True
                    //M1.quImageBytes() = rsMain("quImage").GetChunk(M1.quImageSize)
                    dataForCommunication.setText1Height(87);
                    dataForCommunication.setText1Visible(true);
                    GlobalVariables.quImageBytes = rsMain_item.getQuImage();
                } else {
                    //Text1.Height = 260
                    //Text1.Visible = False
                    //Text1.Visible = True
                    dataForCommunication.setText1Height(260);
                    dataForCommunication.setText1Visible(true);
                }

                GlobalVariables.answImageSize = rsMain_item.getAnswImageSize();
                if (GlobalVariables.answImageSize > 0) {
                    GlobalVariables.answImageBytes = rsMain_item.getAnswImage();
                }

                GlobalVariables.reviewitem = rsMain_item.getItem();
            }
        } catch (SQLException ex) {
            dataForCommunication.getMessageBoxText().getTexts().add("Error: " + ex.getMessage());
        }
        return cacheItemData;
    }

    //getItemFromResultSet() is used for
    public Item getItemFromResultSet(Iterator<Item> resultSet) {
        Item result = null;
        if (resultSet!=null) {
            if (resultSet.hasNext()) {
                result = resultSet.next();
            }
        }
        return result;
    }

    public Item getCurrentItem() {
        return rs1!=null && rs1.size()>currentItemIndex ? rs1.get(currentItemIndex) : null ;
    }

    //Search is case insensitive
    public Item getNextItemWithKeywords(List<String> keywords) {
        Item nextItem = null;
        currentItemIndex++;
        if (keywords!=null && keywords.size()>0) {
            Item tmpItem = null;
            int i;
            for (i = currentItemIndex ; i<rs1.size(); i++ ) {
                tmpItem = rs1.get(i);
                boolean allFound = true;
                String question = tmpItem.getQuestion().toLowerCase();
                String answer = tmpItem.getAnswer().toLowerCase();
                for (String keyword : keywords) {
                    if (!question.contains(keyword)
                            && !answer.contains(keyword)) {
                        allFound = false;
                        break;
                    }
                }
                if (allFound) {
                    nextItem = tmpItem;
                    break;
                }
            }
            currentItemIndex = i;
        } else if (rs1!=null && rs1.size()>currentItemIndex) {
            nextItem = rs1.get(currentItemIndex);
        }
        return nextItem;
    }

    //Method locateItemInRS1(..) sets currentItemIndex
    public void locateItemInRS1(Item item) {
        //Integer itemID
        if (item!=null) {
            while (currentItemIndex < rs1.size()) {
                if (item.getForreview()) {
                    if (item.getUserid()==rs1.get(currentItemIndex).getUserid()
                    && item.getReviewitem().equals(rs1.get(currentItemIndex).getReviewitem())
                    && item.getReviewdate().equals(rs1.get(currentItemIndex).getReviewdate())) {
                        break;
                    }
                } else {
                    if (item.getUserid()==rs1.get(currentItemIndex).getUserid()
                    && item.getCurdate().equals(rs1.get(currentItemIndex).getCurdate())
                    && item.getItem().equals(rs1.get(currentItemIndex).getItem())) {
                        break;
                    }
                }
                currentItemIndex++;
            }
        } else {
            //??? need to output error
        }
    }
}
