package sevenrev.view.ReviewOldItems;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import sevenrev.model.GlobalVariables;
import sevenrev.model.ReviewLogMgr;
import sevenrev.model.ReviewOldItems.DataForCommunication;
import sevenrev.model.ReviewOldItems.NavigateItems;
import sevenrev.model.ReviewTime;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.User;
import sevenrev.model.itemPickingLogic.ReviewedCache;
import sevenrev.utilities.DateTimeFunctions;
import sevenrev.view.NewItem.NewItemPane;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static sevenrev.model.GlobalVariables.userID;

//Updated just for commit
public class ReviewOldItemsPane extends GridPane {
    static String button_highLighted = "-fx-background-color: #FF0000";
    static String button_orange = "-fx-background-color: #FFA500";
    //Created just for commit
    String justTest01;

    //Stage stage;
    BorderPane parentPane;
    //Scene scene = null;

    Label label = new Label(GlobalVariables.username + ": Review old items Mode");

    Label label1 = new Label("Question");
    Label label2 = new Label("");
    TextArea quAnText = new TextArea();
    ImageView imageView = new ImageView();
    Button button_CheckAnswer = new Button("Check answer");
    Button button_CanRemember = new Button("Yes I remember");
    Button button_IForgot = new Button("No I forgot");
    Button button_AddNewItems = new Button("Add new items");
    Button button_searchItems = new Button("Search items");
    Button button_updateCurrentItem = new Button("Save changes to current Item");

    GridPane fontSizePane = new GridPane();
    Label fontSizeLabel = new Label("Font Size");
    Button fontSizeDecrease = new Button("-");
    Button fontSizeIncrease = new Button("+");

    DataForCommunication dataForCommunication = new DataForCommunication();
    NavigateItems navigateItems;

    String questionCurrentItem;
    String answerCurrentItem;
    boolean quAnTextChanged;

    public ReviewOldItemsPane(BorderPane parentPane) {
        //this.stage = stage;
        try {
            GlobalVariables.daoFactory.getFutureItemDAO().clearFutureItemCache();
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, ex.getMessage());
            alert.showAndWait();
        }
        GlobalVariables.quAnText = quAnText;
        GlobalVariables.quAnText.setPrefHeight(parentPane.getHeight() / 2);
        GlobalVariables.quAnText.setPrefWidth(parentPane.getWidth() * 95 / 100);

        this.parentPane = parentPane;
        this.navigateItems = new NavigateItems(this.dataForCommunication);

        button_AddNewItems.setOnAction(new AddNewItemsButton_EventHandler(this.parentPane));
        button_AddNewItems.setWrapText(true);
        button_CheckAnswer.setOnAction(new CheckAnswerButton_EventHandler());
        button_CheckAnswer.setWrapText(true);
        button_IForgot.setOnAction(new IForgotButton_EventHandler());
        button_IForgot.setWrapText(true);
        button_IForgot.setStyle(button_orange);
        button_CanRemember.setOnAction(new CanRememberButton_EventHandler());
        button_CanRemember.setWrapText(true);
        button_searchItems.setOnAction(new SearchItemsButton_EventHandler(this.parentPane));
        button_searchItems.setWrapText(true);
        button_updateCurrentItem.setOnAction(new UpdateCurrentItemButton_EventHandler());
        button_updateCurrentItem.setWrapText(true);
        fontSizeDecrease.setOnAction(new FontSizeDecreaseButton_EventHandler());
        fontSizeIncrease.setOnAction(new FontSizeIncreaseButton_EventHandler());
        fontSizePane.add(fontSizeLabel, 0,0, 2, 1);
        fontSizePane.add(fontSizeDecrease,0,1,1, 1);
        fontSizePane.add(fontSizeIncrease, 1, 1, 1, 1);

        String name = null;
        int itemID;

        int i1;
        int flag1;
        boolean MoveToNext_flag;

        navigateItems.setCheckanswer(true);
        label1.setText("Question");
        //Text1.Text = ""
        quAnText.setText("");
        quAnText.setWrapText(true);
        quAnText.setStyle("-fx-font-size: "
            + String.valueOf(GlobalVariables.fontSize) + "em;");

        if (quAnText.textProperty()!=null) {
            quAnText.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                    if (navigateItems.isCheckanswer()) {
                        if (questionCurrentItem!=null) {
                            if (!questionCurrentItem.equals(quAnText.getText())) {
                                button_updateCurrentItem.setStyle(button_highLighted);
                                quAnTextChanged = true;
                            }
                        }
                    } else {
                        if (answerCurrentItem!=null) {
                            if (!answerCurrentItem.equals(quAnText.getText())) {
                                button_updateCurrentItem.setStyle(button_highLighted);
                                quAnTextChanged = true;
                            }
                        }
                    }
                }
            });
        }
        label2.setText("");
        label2.setWrapText(true);
        imageView.setFitHeight(GlobalVariables.imageMaxHeight);
        imageView.setFitWidth(GlobalVariables.imageMaxWidth);

        //flag = 0
        navigateItems.setFlag(0);

/*
Set rs1 = CreateObject("ADODB.Recordset")
*/
        try {
            if (GlobalVariables.reviewed) {// Then 'review is done
                layoutWithoutImage();
                //Command5.Enabled = False //check answer button
                //CommandButton4.Enabled = False // Can remember button
                //CommandButton3.Enabled = False // I forgot button
                //CommandButton1.Enabled = False // this button is not used
                //CommandButton2.Enabled = True // add new item button
                button_CheckAnswer.setDisable(true);
                button_CanRemember.setDisable(true);
                button_IForgot.setDisable(true);
                button_AddNewItems.setDisable(false);
                //flag = 1
                navigateItems.setFlag(1);
            } else {

                if (GlobalVariables.daoFactory.getItemDAO().queryByKey(GlobalVariables.userID, GlobalVariables.reviewDate,
                        GlobalVariables.reviewitem)==null) {
                    boolean initialMov = true;
                    do {
                        MoveToNext_flag = navigateItems.MoveToNext02(initialMov);
                        initialMov = initialMov ? false : false;
                        //display msgs in navigateItems
                        for (String msgStr : this.dataForCommunication.getMessageBoxText().getTexts()) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, msgStr);
                            alert.showAndWait();
                        }
                        if (!navigateItems.isRSNotEOF()) {
                            //Exit Do
                            break;
                        }
                    } while (!MoveToNext_flag);
                } else {
                    navigateItems.setRs1(GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns02(GlobalVariables.userID, GlobalVariables.reviewDate));
                    navigateItems.setCurrentItemIndex(0);
                    List<Item> tmpItems = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID, GlobalVariables.reviewDate, GlobalVariables.reviewitem);
                    if (tmpItems==null || tmpItems.size()<=0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "There is no such item by the item ID");
                        alert.showAndWait();
                    }
                    navigateItems.locateItemInRS1(tmpItems!=null && tmpItems.size()>0 ? tmpItems.get(0) : null);

                    Item origItem = navigateItems.getCurrentItem();
                    if (origItem.getForreview()) {
                        origItem = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(userID,
                                origItem.getReviewdate(), origItem.getReviewitem()).get(0);
                    }
                    navigateItems.setQuestion(origItem.getQuestion());
                    navigateItems.setAnswer(origItem.getAnswer());
                    GlobalVariables.quImageSize = origItem.getQuImageSize();
                    GlobalVariables.answImageSize = origItem.getAnswImageSize();
                    GlobalVariables.quImageBytes = origItem.getQuImage();
                    GlobalVariables.answImageBytes = origItem.getAnswImage();
                    navigateItems.setCheckanswer(true);
                }

                //Set button status
                button_CheckAnswer.setDisable(!navigateItems.getDataForCommunication().isCommand5Enabled());
                button_CanRemember.setDisable(!navigateItems.getDataForCommunication().isCommandButton4Enabled());
                button_IForgot.setDisable(!navigateItems.getDataForCommunication().isCommandButton3Enabled());

                //Text1.Text = question
                quAnText.setText(navigateItems.getQuestion());
                button_updateCurrentItem.setStyle(null);
                questionCurrentItem = navigateItems.getQuestion();
                answerCurrentItem = navigateItems.getAnswer();

                if (GlobalVariables.quImageSize > 0) {
                    //Text1.Height = 87
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                    //imageDisplay.Picture = PictureFromBits(M1.quImageBytes())
                    //imageDisplay.Visible = False
                    //imageDisplay.Visible = True
                    InputStream targetStream = new ByteArrayInputStream(GlobalVariables.quImageBytes);
                    Image image = new Image(targetStream);
                    //imageView = new ImageView(image);
                    imageView.setImage(image);
                    imageView.setFitHeight(GlobalVariables.imageMaxHeight);
                    imageView.setDisable(false);
                    imageView.setVisible(true);
                    layoutWithImage();
                } else {
                    if (imageView!=null) {
                        imageView.setDisable(true);
                        imageView.setVisible(false);
                    }
                    //Text1.Height = 260
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withOutImage);
                    layoutWithoutImage();
                }

                if (GlobalVariables.reviewed) {// Then
                    //flag = 1
                    navigateItems.setFlag(1);
                } else {
                    //flag = 0
                    navigateItems.setFlag(0);
                    if (!GlobalVariables.displaysetting) {//Then
                        Item rs1CurrentItem = navigateItems.getRs1().get(navigateItems.getCurrentItemIndex());
                        //If (rs1.Fields("forreview").Value = 0) Then
                        if (!(rs1CurrentItem.getForreview())) {
                            // Label2.Caption = "Orig item date is " & M1.convDate(rs1.Fields("curdate").Value) & " item no is " & rs1.Fields("item").Value & "; reviewtime: " & rs1.Fields("reviewtime").Value
                            label2.setText("("+ GlobalVariables.reviewtime + ") Orig item date is " + DateTimeFunctions.DateToString(rs1CurrentItem.getCurdate()) + " item no is " +
                                    rs1CurrentItem.getItem() + "; reviewtime: " + rs1CurrentItem.getReviewtime());
                        } else {
                            //Dim currentItem As item7Rev
                            //currentItem = getForreviewItemFields(rs1, M1.conn1)
                            Item currentItem = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(userID,
                                    rs1CurrentItem.getReviewdate(), rs1CurrentItem.getReviewitem()).get(0);
                            //Label2.Caption = "Forreview item date " & M1.convDate(rs1.Fields("curdate").Value) & " item " & (rs1.Fields("item").Value) & ", orig date is " & M1.convDate(rs1.Fields("reviewdate").Value) & " item no is " & rs1.Fields("reviewitem").Value & "; reviewtime: " & currentItem.reviewtime
                            label2.setText("("+ GlobalVariables.reviewtime + ") Forreview item date " + DateTimeFunctions.DateToString(rs1CurrentItem.getCurdate()) + " item " + rs1CurrentItem.getItem() +
                                    " ,Orig item date is " + DateTimeFunctions.DateToString(rs1CurrentItem.getReviewdate()) + " item no is " +
                                    rs1CurrentItem.getReviewitem() + "; reviewtime: " + currentItem.getReviewtime());
                        }
                    } else {// Then
                        //Label2.Caption = "Current date is " & M1.date1
                        label2.setText("("+ GlobalVariables.reviewtime + ") Current date is " + DateTimeFunctions.DateToString(GlobalVariables.date1));
                    }
                } //End If 'end of If M1.reviewed Then

            }//End If ' of If M1.reviewed Then

            label.setText(GlobalVariables.username + ": Review old items Mode - " + GlobalVariables.RSCacheItem + " items reviewed");
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, ex.getMessage());
            alert.showAndWait();
        }
        quAnTextChanged = false;
        //??? tbd Beep


    }

    private void layoutWithoutImage() {
        this.getChildren().clear();
        int rowNum = 0;
        this.add(label, 0, rowNum, 7, 1);
        rowNum++;
        this.add(label2, 0, rowNum, 7, 1);
        this.add(button_searchItems, 7, rowNum, 1, 1);
        rowNum++;
        this.add(label1, 0, rowNum, 7, 1);

        rowNum++;
        this.add(quAnText, 0, rowNum, 7, 8);
        this.add(fontSizePane, 7, rowNum, 1, 1);

        rowNum+=8;
        this.add(button_CheckAnswer, 1, rowNum, 1, 1);
        this.add(button_CanRemember, 3, rowNum, 1, 1);

        rowNum++;
        this.add(button_AddNewItems, 2, rowNum, 1, 1);
        this.add(button_IForgot, 4, rowNum, 1, 1);
        this.add(button_updateCurrentItem, 7, rowNum, 1, 1);
    }

    private void layoutWithImage() {
        this.getChildren().clear();
        int rowNum = 0;
        this.add(label, 0, rowNum, 3, 1);
        rowNum++;
        this.add(label2, 0, rowNum, 3, 1);
        this.add(button_searchItems, 7, rowNum, 1, 1);
        rowNum++;
        this.add(label1, 0, rowNum, 3, 1);

        rowNum++;
        this.add(quAnText, 0, rowNum, 7, 8);
        this.add(fontSizePane, 7, rowNum, 1, 1);

        rowNum+=8;
        this.add(imageView, 0, rowNum, 3, 8);

        rowNum+=8;
        this.add(button_CheckAnswer, 0, rowNum, 1, 1);
        this.add(button_CanRemember, 2, rowNum, 1, 1);

        rowNum++;
        this.add(button_AddNewItems, 1, rowNum, 1, 1);
        this.add(button_IForgot, 3, rowNum, 1, 1);
        this.add(button_updateCurrentItem, 7, rowNum, 1, 1);
    }

    class AddNewItemsButton_EventHandler implements EventHandler<ActionEvent> {
        //Stage stage;
        BorderPane parentPane;
        AddNewItemsButton_EventHandler(BorderPane borderPane) {
            this.parentPane = borderPane;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: AddNewItemsButton_EventHandler");
            try {
                GlobalVariables.daoFactory.getItemDAO().flushAndClearRemoveItemCache();

                /*
                GlobalVariables.daoFactory.getItemDAO().
                        insertFutureItems02(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsCache(),
                                GlobalVariables.date1);
                GlobalVariables.daoFactory.getFutureItemDAO().removeFutureItemsToBeInsertedIntoItems02(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsCache());
                GlobalVariables.daoFactory.getItemDAO().
                        insertFutureItems(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsFromDB(GlobalVariables.userID),
                                GlobalVariables.date1);
                GlobalVariables.daoFactory.getFutureItemDAO().removeFutureItemsToBeInsertedIntoItems(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsFromDB(GlobalVariables.userID));
                */

                //???
                GlobalVariables.daoFactory.getFutureItemDAO().flushBothCaches();
                GlobalVariables.daoFactory.getFutureItemDAO().clearFutureItemCache();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Err occurred when going to Add New Items: " + ex.getMessage());
                alert.showAndWait();
            }
            NewItemPane newItemPane = new NewItemPane(this.parentPane);
            //stage.getScene().setRoot(newItemPane);
            parentPane.setCenter(newItemPane);
        }
    }

    class CheckAnswerButton_EventHandler implements EventHandler<ActionEvent> {
        //Stage stage;
        //BorderPane parentPane;
        CheckAnswerButton_EventHandler() {
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: CheckAnswerButton_EventHandler");
            //boolean tmp_quAnTextChanged = quAnTextChanged;
            if (navigateItems.isCheckanswer()) {//Then
                navigateItems.setCheckanswer(false);
                //Text1.Text = answer
                questionCurrentItem = quAnText.getText();
                quAnText.setText(answerCurrentItem);
                if (GlobalVariables.answImageSize > 0) {//Then
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                    quAnText.setVisible(true);
                    InputStream targetStream = new ByteArrayInputStream(GlobalVariables.answImageBytes);
                    Image image = new Image(targetStream);
                    if (imageView==null) {
                        imageView = new ImageView(image);
                    } else {
                        imageView.setImage(image);
                    }
                    imageView.setFitHeight(GlobalVariables.imageMaxHeight);
                    imageView.setDisable(false);
                    //imageView.setVisible(false);
                    imageView.setVisible(true);
                    layoutWithImage();
                } else {
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withOutImage);
                    quAnText.setVisible(true);
                    if (imageView!=null) {
                        imageView.setImage(null);
                        imageView.setFitHeight(0);
                        imageView.setDisable(true);
                        imageView.setVisible(false);
                    }
                    layoutWithoutImage();
                }

                //Label1.Caption = "Answer"
                label1.setText("Answer");
                //Command5.Caption = "Check Question"
                button_CheckAnswer.setText("Check Question");
                //checkanswer = False
            } else {
                navigateItems.setCheckanswer(true);
                answerCurrentItem = quAnText.getText();
                quAnText.setText(questionCurrentItem);
                if (GlobalVariables.quImageSize > 0) {
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                    quAnText.setVisible(true);
                    InputStream targetStream = new ByteArrayInputStream(GlobalVariables.quImageBytes);
                    Image image = new Image(targetStream);
                    if (imageView==null) {
                        imageView = new ImageView(image);
                    } else {
                        imageView.setImage(image);
                    }
                    //imageView = new ImageView(image);
                    imageView.setFitHeight(GlobalVariables.imageMaxHeight);
                    imageView.setDisable(false);
                    //imageView.setVisible(false);
                    imageView.setVisible(true);
                    layoutWithImage();
                } else {
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withOutImage);
                    quAnText.setVisible(true);
                    if (imageView!=null) {
                        imageView.setImage(null);
                        imageView.setFitHeight(0);
                        imageView.setDisable(true);
                        imageView.setVisible(false);
                    }
                    layoutWithoutImage();
                }
                label1.setText("Question");
                button_CheckAnswer.setText("Check Answer");
            }
            //restore quAnTextChanged because it was changed when quAnText was set
            //quAnTextChanged = tmp_quAnTextChanged;
            //button_updateCurrentItem.setStyle(quAnTextChanged? button_highLighted : null);
        }
    }

    class IForgotButton_EventHandler implements EventHandler<ActionEvent> {
        //Stage stage;
        //BorderPane parentPane;
        IForgotButton_EventHandler() {
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: IForgotButton_EventHandler");
            try {
                int i1;
                List<Item> rs2 = null;
                int item2;
                boolean CommandButton3_Click_flag;

                label1.setText("Question");
                quAnTextChanged = false;

                Date origDate;
                int origItem;
                Item rs1Item = navigateItems.getRs1().get(navigateItems.getCurrentItemIndex());
                if (!rs1Item.getForreview()) {
                    origDate = rs1Item.getCurdate();
                    origItem = rs1Item.getItem();
                } else {
                    origDate = rs1Item.getReviewdate();
                    origItem = rs1Item.getReviewitem();
                }

                int rtime;
                rtime = ReviewTime.updateReviewTime(userID, origItem, origDate, false);

                ReviewLogMgr.UpdateReviewLog(origDate, origItem, GlobalVariables.userID, GlobalVariables.date1, rtime, false);
                boolean recordExist = GlobalVariables.daoFactory.getItemDAO().doesRecordExist(GlobalVariables.userID, GlobalVariables.date1, origDate, origItem);
                if (!recordExist) {
                    item2 = GlobalVariables.daoFactory.getItemDAO().getAvailableItemId(GlobalVariables.userID, GlobalVariables.date1);
                    Item rs2Item = new Item();
                    rs2Item.setUserid(GlobalVariables.userID);
                    rs2Item.setCurdate(GlobalVariables.date1);
                    rs2Item.setForreview(true);
                    rs2Item.setItem(item2);
                    if (!rs1Item.getForreview()) {
                        rs2Item.setReviewdate(rs1Item.getCurdate());
                        rs2Item.setReviewitem(rs1Item.getItem());
                        //ReviewTime of the orig item can change
                        rs2Item.setReviewtime(rs1Item.getReviewtime());
                    } else {
                        rs2Item.setReviewdate(rs1Item.getReviewdate());
                        rs2Item.setReviewitem(rs1Item.getReviewitem());
                        //ReviewTime is set randomly to 4
                        rs2Item.setReviewtime(rs1Item.getReviewtime()>0?
                                rs1Item.getReviewtime() : 4);
                    }
                    GlobalVariables.daoFactory.getItemDAO().update(rs2Item);
                }

                ReviewedCache.AddToReviewedCache(rs1Item.getCurdate(), rs1Item.getItem());
                do {
                    CommandButton3_Click_flag = navigateItems.MoveToNext02(false);
                    if (!navigateItems.isRSNotEOF()) {
                        break;
                    }
                    //'Loop While (M1.InReviewedCache(rs1.Fields("forreview").Value, (rs1.Fields("reviewdate").Value), (rs1.Fields("reviewitem").Value), rs1.Fields("curdate").Value, rs1.Fields("item").Value)) Or (Not CommandButton3_Click_flag)
                } while (!CommandButton3_Click_flag);

                //Set button status
                button_CheckAnswer.setDisable(!navigateItems.getDataForCommunication().isCommand5Enabled());
                button_CanRemember.setDisable(!navigateItems.getDataForCommunication().isCommandButton4Enabled());
                button_IForgot.setDisable(!navigateItems.getDataForCommunication().isCommandButton3Enabled());

                quAnText.setText(navigateItems.getQuestion());
                button_updateCurrentItem.setStyle(null);
                questionCurrentItem = navigateItems.getQuestion();
                answerCurrentItem = navigateItems.getAnswer();

                if (GlobalVariables.quImageSize > 0) {//Then
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                    InputStream targetStream = new ByteArrayInputStream(GlobalVariables.quImageBytes);
                    Image image = new Image(targetStream);
                    imageView.setImage(image);
                    imageView.setFitHeight(GlobalVariables.imageMaxHeight);
                    imageView.setDisable(false);
                    imageView.setVisible(true);
                    layoutWithImage();
                } else {
                    //Text1.Height = 260
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withOutImage);
                    if (imageView != null) {
                        //imageDisplay.Visible = False
                        imageView.setFitHeight(0);
                        imageView.setDisable(true);
                        imageView.setVisible(false);
                    }
                    layoutWithoutImage();
                }

                if (GlobalVariables.reviewed) {//Then
                    //flag = 1
                    navigateItems.setFlag(1);
                } else {
                    //flag = 0
                    navigateItems.setFlag(0);
                    if (!GlobalVariables.displaysetting) {
                        Item rs1CurrentItem = navigateItems.getRs1().get(navigateItems.getCurrentItemIndex());
                        if (!(rs1CurrentItem.getForreview())) {
                            label2.setText("("+ GlobalVariables.reviewtime + ") Orig item date is " + DateTimeFunctions.DateToString(rs1CurrentItem.getCurdate()) + " item no is " +
                                    rs1CurrentItem.getItem() + "; reviewtime: " + rs1CurrentItem.getReviewtime());
                        } else {
                            Item currentItem = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID,
                                    rs1CurrentItem.getReviewdate(), rs1CurrentItem.getReviewitem()).get(0);
                            label2.setText("("+ GlobalVariables.reviewtime + ") Forreview item date " + DateTimeFunctions.DateToString(rs1CurrentItem.getCurdate()) + " item " + rs1CurrentItem.getItem() +
                                    " ,Orig item date is " + DateTimeFunctions.DateToString(rs1CurrentItem.getReviewdate()) + " item no is " +
                                    rs1CurrentItem.getReviewitem() + "; reviewtime: " + currentItem.getReviewtime());
                        }
                    } else {
                        label2.setText("("+ GlobalVariables.reviewtime + ") Current date is " + DateTimeFunctions.DateToString(GlobalVariables.date1));
                    }
                }
                User user = GlobalVariables.daoFactory.getUserDAO().queryByKey(GlobalVariables.userID);
                user.setReviewdate(GlobalVariables.reviewDate);
                //user.setReviewed(navigateItems.getFlag());
                user.setReviewed(GlobalVariables.reviewed);
                user.setReviewtime(GlobalVariables.reviewtime);
                user.setReviewitem(GlobalVariables.reviewitem);
                GlobalVariables.daoFactory.getUserDAO().update(user);

                label.setText(GlobalVariables.username + ": Review old items Mode - " + GlobalVariables.RSCacheItem + " items reviewed");
                //??? Beep
            } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error : " + ex.getMessage());
            alert.showAndWait();
            }
        }
    }

    class CanRememberButton_EventHandler implements EventHandler<ActionEvent> {
        //Stage stage;
        //BorderPane parentPane;
        CanRememberButton_EventHandler() {
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: CanRememberButton_EventHandler");
            try {
                int i1;
                List<Item> rs2;
                boolean CommandButton4_Click_flag;

                //Label1.Caption = "Question"
                label1.setText("Question");
                quAnTextChanged = false;
                //'update the reviewtime of the original item (instead of forreview item)
                int rtime;
                Item rs1Item = navigateItems.getRs1().get(navigateItems.getCurrentItemIndex());
                rtime = ReviewTime.updateReviewTime(GlobalVariables.userID, rs1Item.getItem(), rs1Item.getCurdate(), true);

                //'added to the reviewed item cache and update review log
                if (!rs1Item.getForreview()) {//Then
                    ReviewedCache.AddToReviewedCache(rs1Item.getCurdate(), rs1Item.getItem());
                    ReviewLogMgr.UpdateReviewLog(rs1Item.getCurdate(), rs1Item.getItem(), GlobalVariables.userID, GlobalVariables.date1, rtime, true);
                } else {
                    ReviewedCache.AddToReviewedCache(rs1Item.getReviewdate(), rs1Item.getReviewitem());
                    ReviewLogMgr.UpdateReviewLog(rs1Item.getReviewdate(), rs1Item.getReviewitem(), GlobalVariables.userID, GlobalVariables.date1, rtime, true);
                }

                //'move to next record the same day
                do {
                    CommandButton4_Click_flag = navigateItems.MoveToNext02(false);
                    if (!navigateItems.isRSNotEOF()) {// Then
                        //' M1.removeToBeRemovedForreviewItemIDs
                        break;
                    }
                } while (!CommandButton4_Click_flag);

                //Set button status
                button_CheckAnswer.setDisable(!navigateItems.getDataForCommunication().isCommand5Enabled());
                button_CanRemember.setDisable(!navigateItems.getDataForCommunication().isCommandButton4Enabled());
                button_IForgot.setDisable(!navigateItems.getDataForCommunication().isCommandButton3Enabled());

                //Text1.Text = question
                quAnText.setText(navigateItems.getQuestion());
                button_updateCurrentItem.setStyle(null);
                questionCurrentItem = navigateItems.getQuestion();
                answerCurrentItem = navigateItems.getAnswer();

                if (GlobalVariables.quImageSize > 0) {//Then
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                    InputStream targetStream = new ByteArrayInputStream(GlobalVariables.quImageBytes);
                    Image image = new Image(targetStream);
                    imageView.setImage(image);
                    imageView.setFitHeight(GlobalVariables.imageMaxHeight);
                    imageView.setDisable(false);
                    imageView.setVisible(true);
                    layoutWithImage();

                } else {
                    //Text1.Height = 260
                    quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withOutImage);
                    if (imageView != null) {
                        //imageDisplay.Visible = False
                        imageView.setFitHeight(0);
                        imageView.setDisable(true);
                        imageView.setVisible(false);
                    }
                    layoutWithoutImage();
                }

                if (GlobalVariables.reviewed) {
                    navigateItems.setFlag(1);
                } else {
                    navigateItems.setFlag(0);
                    if (!GlobalVariables.displaysetting) {
                        Item rs1CurrentItem = navigateItems.getRs1().get(navigateItems.getCurrentItemIndex());
                        if (!(rs1CurrentItem.getForreview())) {
                            label2.setText("("+ GlobalVariables.reviewtime + ") Orig item date is " + DateTimeFunctions.DateToString(rs1CurrentItem.getCurdate()) + " item no is " +
                                    rs1CurrentItem.getItem() + "; reviewtime: " + rs1CurrentItem.getReviewtime());
                        } else {
                            Item currentItem = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID,
                                    rs1CurrentItem.getReviewdate(), rs1CurrentItem.getReviewitem()).get(0);
                            label2.setText("("+ GlobalVariables.reviewtime + ") Forreview item date " + DateTimeFunctions.DateToString(rs1CurrentItem.getCurdate()) + " item " + rs1CurrentItem.getItem() +
                                    " ,Orig item date is " + DateTimeFunctions.DateToString(rs1CurrentItem.getReviewdate()) + " item no is " +
                                    rs1CurrentItem.getReviewitem() + "; reviewtime: " + currentItem.getReviewtime());
                        }
                    } else {
                        label2.setText("("+ GlobalVariables.reviewtime + ") Current date is " + DateTimeFunctions.DateToString(GlobalVariables.date1));
                    }
                }

                //'update certain user info in users table, eg. item field
                //conn1.Execute "update users set reviewdate=#" & M1.convDate(M1.reviewDate) & "#, reviewed=" & flag & " , reviewtime=" & M1.reviewtime & ", reviewitem=" & M1.reviewitem & "  where userid=" & M1.userID
                User user = GlobalVariables.daoFactory.getUserDAO().queryByKey(GlobalVariables.userID);
                user.setReviewdate(GlobalVariables.reviewDate);
                user.setReviewed(GlobalVariables.reviewed);
                user.setReviewtime(GlobalVariables.reviewtime);
                user.setReviewitem(GlobalVariables.reviewitem);
                GlobalVariables.daoFactory.getUserDAO().update(user);

                label.setText(GlobalVariables.username + ": Review old items Mode - " + GlobalVariables.RSCacheItem + " items reviewed");
                //??? Beep
            } catch(SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error : " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    class SearchItemsButton_EventHandler implements EventHandler<ActionEvent> {
        BorderPane parentPane;
        SearchItemsButton_EventHandler(BorderPane borderPane) {
            this.parentPane = borderPane;
        }

        @Override
        public void handle(ActionEvent actionEveFnt) {
            System.out.println("GUIOutput: SearchItemsButton_EventHandler");
            try {
                GlobalVariables.daoFactory.getItemDAO().flushAndClearRemoveItemCache();

                /*
                GlobalVariables.daoFactory.getItemDAO().
                        insertFutureItems02(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsCache(),
                                GlobalVariables.date1);
                GlobalVariables.daoFactory.getFutureItemDAO().removeFutureItemsToBeInsertedIntoItems02(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsCache());
                GlobalVariables.daoFactory.getItemDAO().
                        insertFutureItems(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsFromDB(GlobalVariables.userID),
                                GlobalVariables.date1);
                GlobalVariables.daoFactory.getFutureItemDAO().removeFutureItemsToBeInsertedIntoItems(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsFromDB(GlobalVariables.userID));
                */

                //???
                GlobalVariables.daoFactory.getFutureItemDAO().flushBothCaches();
                GlobalVariables.daoFactory.getFutureItemDAO().clearFutureItemCache();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Err occurred when going to Search Items: " + ex.getMessage());
                alert.showAndWait();
            }
            SearchItemsPane searchItemsPane = new SearchItemsPane(this.parentPane);
            parentPane.setCenter(searchItemsPane);
        }
    }

    class UpdateCurrentItemButton_EventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: UpdateCurrentItemButton_EventHandler");
            Item currentItem = navigateItems.getCurrentItem();
            if (navigateItems.isCheckanswer()) {
                currentItem.setQuestion(quAnText.getText());
                currentItem.setAnswer(answerCurrentItem);
            } else {
                currentItem.setQuestion(questionCurrentItem);
                currentItem.setAnswer(quAnText.getText());
            }
            try {
                GlobalVariables.daoFactory.getItemDAO().update(currentItem);
                button_updateCurrentItem.setStyle(null);
                quAnTextChanged = false;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Update to database is done");
                alert.showAndWait();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error : " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    public class FontSizeDecreaseButton_EventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: FontSizeDecreaseButton_EventHandler");
            GlobalVariables.fontSize *= 0.85;
            quAnText.setStyle("-fx-font-size: "
                    + String.valueOf(GlobalVariables.fontSize) + "em;");
        }
    }

    public class FontSizeIncreaseButton_EventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: FontSizeIncreaseButton_EventHandler");
            GlobalVariables.fontSize *= 1.15;
            quAnText.setStyle("-fx-font-size: "
                    + String.valueOf(GlobalVariables.fontSize) + "em;");
        }
    }
}
