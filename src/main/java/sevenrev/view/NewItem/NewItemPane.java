package sevenrev.view.NewItem;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sevenrev.dao.FutureItemDAO;
import sevenrev.dao.ItemDAO;
import sevenrev.model.*;
import sevenrev.model.entities.Item;
import sevenrev.model.entities.User;
import sevenrev.model.itemPickingLogic.ReviewedCache;
import sevenrev.utilities.DateTimeFunctions;
import sevenrev.view.ReviewOldItems.ReviewOldItemsPane;
import sevenrev.view.ReviewOldItems.SearchItemsPane;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.ParseException;

//updated just for commit
public class NewItemPane extends GridPane {
    //Scene scene = null;
    //Created just for commit
    String justTest01;

    Stage stage;
    BorderPane parentPane;

    Label label = new Label("Input new item Mode");
    Label Qulabel = new Label("Question:");
    Label Anlabel = new Label("Answer:");
    TextArea questionText = new TextArea();
    TextArea answerText = new TextArea();

    Button button_AddToDatabase = new Button("Add this item to database");
    Button button_ReviewOldItems = new Button("Review old items");
    Button button_StartNewDay = new Button("Start new cycle");

    Button button_quImagePath = new Button("Add Question Image");
    Button button_AnImagePath = new Button("Add Answer Image");

    Button button_searchItems = new Button("Search items");

    Button fontSizeDecrease = new Button("-");
    Button fontSizeIncrease = new Button("+");

    StringBuilder quImagePathBuilder = new StringBuilder();
    StringBuilder AnImagePathBuilder = new StringBuilder();

    public NewItemPane(BorderPane parentPane) {
        //this.stage = stage;
        GlobalVariables.questionText = questionText;
        GlobalVariables.answerText = answerText;
        GlobalVariables.questionText.setPrefHeight(parentPane.getHeight() / 2);
        GlobalVariables.questionText.setPrefWidth(parentPane.getWidth() * 95 / 100);
        GlobalVariables.answerText.setPrefHeight(parentPane.getHeight() / 2);
        GlobalVariables.answerText.setPrefWidth(parentPane.getWidth() * 95 / 100);
        this.parentPane = parentPane;
        int rowIndex = 0;
        this.add(label, 1, rowIndex, 1, 1);

        rowIndex ++;
        this.add(Qulabel, 0, rowIndex, 1, 1);
        this.add(button_searchItems, 7, rowIndex, 1, 1);
        button_searchItems.setOnAction(new SearchItemsButton_EventHandler(this.parentPane));
        button_searchItems.setWrapText(true);

        rowIndex ++;
        this.add(questionText, 0, rowIndex, 3, GlobalVariables.textAresRowSpan);
        questionText.setWrapText(true);
        questionText.setStyle("-fx-font-size: "
                + String.valueOf(GlobalVariables.fontSize) + "em;");
        this.add(fontSizeDecrease, 3, rowIndex, 1, 1);
        this.add(fontSizeIncrease, 3, rowIndex+1, 1, 1);
        fontSizeDecrease.setOnAction(new FontSizeDecreaseButton_EventHandler());
        fontSizeIncrease.setOnAction(new FontSizeIncreaseButton_EventHandler());

        rowIndex ++;
        rowIndex+= GlobalVariables.textAresRowSpan;
        this.add(Anlabel, 0, rowIndex, 1, 1);

        rowIndex ++;
        this.add(answerText, 0, rowIndex, 3, GlobalVariables.textAresRowSpan);
        answerText.setWrapText(true);
        answerText.setStyle("-fx-font-size: "
                + String.valueOf(GlobalVariables.fontSize) + "em;");

        rowIndex+= GlobalVariables.textAresRowSpan;
        this.add(button_AddToDatabase, 0, rowIndex, 1, 1);
        this.add(button_ReviewOldItems, 1, rowIndex, 1, 1);
        this.add(button_StartNewDay, 2, rowIndex, 1, 1);
        button_AddToDatabase.setOnAction(new AddToDatabaseButton_EventHandler());
        //button_ReviewOldItems.setOnAction(new ReviewOldItemsButton_EventHandler(this.stage));
        button_ReviewOldItems.setOnAction(new ReviewOldItemsButton_EventHandler(this.parentPane));

        rowIndex ++;
        this.add(button_quImagePath, 0, rowIndex, 1, 1);
        this.add(button_AnImagePath, 2, rowIndex, 1, 1);
        //button_quImagePath.setOnAction(new SelectFileButton_EventHandler(this.stage, this.quImagePathBuilder));
        button_quImagePath.setOnAction(new SelectFileButton_EventHandler(this.quImagePathBuilder));

        //button_AnImagePath.setOnAction(new SelectFileButton_EventHandler(this.stage, this.AnImagePathBuilder));
        button_AnImagePath.setOnAction(new SelectFileButton_EventHandler(this.AnImagePathBuilder));
        try {
            button_StartNewDay.setOnAction(new StartNewDayButton_EventHandler(button_ReviewOldItems, button_StartNewDay,
                    GlobalVariables.daoFactory.getFutureItemDAO(), GlobalVariables.daoFactory.getItemDAO()));
        } catch (java.sql.SQLException ex) {
            //tbd
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "error setting StartNewDayButton_EventHandler: " + ex.getMessage());
            alert.showAndWait();
        }
        GlobalVariables.quImgFilename = "";
        GlobalVariables.anImgFilename = "";
        //Label4.Caption = M1.username
        if (GlobalVariables.reviewed && java.util.Calendar.getInstance().getTime().after( GlobalVariables.date1)) {
            //CommandButton2.Enabled = False //disable review old items button
            button_ReviewOldItems.setDisable(true);
            button_StartNewDay.setDisable(false);
            /*** the following is commented because cannot find variable date
            if (GlobalVariables.date1 = date) {
                CommandButton3.Enabled = False //CommandButton3 is start new day button
                button_StartNewDay.setDisable(true);
            }
             */
        } else {
            button_ReviewOldItems.setDisable(false);
            button_StartNewDay.setDisable(true);
        }
        questionText.setText("");
        answerText.setText("");

        //Set rs2 = CreateObject("ADODB.Recordset")
        //rs2.Open "SELECT count(*) FROM items where forreview=0 and userid=" & M1.userID & " and curdate=#" & M1.convDate(M1.date1) & "#", M1.conn1
        try {
            int itemCount = GlobalVariables.daoFactory.getItemDAO().getOrigItemCount(GlobalVariables.userID, GlobalVariables.date1);
            //Label3.Caption = rs2.Fields(0).Value & " new items for " & M1.convDate(M1.date1)
            label.setText("Input new item Mode: " + itemCount + " new items added for " + DateTimeFunctions.DateToString(GlobalVariables.date1));
        } catch (SQLException exp) {
            //??? tbd
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "error getting getOrigItemCount: " + exp.getMessage());
            alert.showAndWait();
        }
    }

    class AddToDatabaseButton_EventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: AddToDatabaseButton_EventHandler");
            try {
                Item item = null;
            /*
            Private Sub CommandButton1_Click() ' add it to database button

Dim rs2, rs3 As ADODB.Recordset
Dim item2 As Integer

Dim file_num As String
Dim file_length As Long
Dim BLOCK_SIZE As Long

Dim bytes() As Byte

Dim num_blocks As Long
Dim left_over As Long
Dim block_num As Long

BLOCK_SIZE = 5000
*/
//
// If Trim(Text1.Text) <> "" And Trim(Text2.Text) <> "" Then
                if (questionText.getText().trim().length() > 0 && answerText.getText().trim().length() > 0) {
                    if (questionText.getText().length() > 175 || answerText.getText().length() > 250) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Question part should not exceed 175 chars, answer part should not exceed 250 chars");
                        alert.showAndWait();
                    }
/*
                   Set rs2 = CreateObject("ADODB.Recordset")
   rs2.Open
    "SELECT userid, Curdate, forreview, reviewdate,item, reviewitem, question, answer,sizeq, sizea,quImage,quImageSize,answImage,answImageSize, reviewtime FROM items where userid=" & M1.userID & " and Curdate=#" & M1.convDate(M1.date1) & "# order by item desc", M1.conn1, adOpenKeyset, adLockOptimistic
   if (rs2.EOF) {
     item2 = 1
    } else {
     item2 = rs2.Fields("item").Value + 1
    }
*/
/*
rs2.AddNew
WriteField "question", rs2, Text1.Text
WriteField "answer", rs2, Text2.Text
rs2.Fields("userid").Value = M1.userID
rs2.Fields("curdate").Value = M1.convDate(M1.date1)
rs2.Fields("forreview").Value = 0
'rs2.Fields("reviewdate").Value = M1.convDate(M1.date1)
rs2.Fields("reviewdate").Value = "1980-1-1"
rs2.Fields("item").Value = item2
rs2.Fields("reviewitem").Value = item2
rs2.Fields("sizeq").Value = Len(Text1.Text)
rs2.Fields("sizea").Value = Len(Text2.Text)
rs2.Fields("reviewtime").Value = 1
*/
                    item = new Item();
                    item.setQuestion(questionText.getText());
                    item.setAnswer(answerText.getText());
                    item.setUserid(GlobalVariables.userID);
                    item.setCurdate(GlobalVariables.date1);
                    item.setForreview(false);
                    item.setReviewdate(DateTimeFunctions.StringToDate("1980-01-01 00:00:00"));
                    item.setReviewitem(GlobalVariables.daoFactory.getItemDAO().getAvailableItemId(GlobalVariables.userID, GlobalVariables.date1));
                    item.setSizeq(questionText.getText().length());
                    item.setSizea(answerText.getText().length());
                    item.setReviewtime(1);

                /*
   rs2("quImageSize") = 0
   
                 */
                    GlobalVariables.quImgFilename = quImagePathBuilder.toString();
                    quImagePathBuilder.setLength(0);
                    if (GlobalVariables.quImgFilename != null && !"".equals(GlobalVariables.quImgFilename)) {
                        File file = new File(GlobalVariables.quImgFilename);
                        byte[] fileContent = Files.readAllBytes(file.toPath());
/*
                           file_num = FreeFile
       Open M1.quImgFilename For Binary Access Read As file_num
       file_length = LOF(file_num)
*/
                        if (fileContent.length > 0) {
                            item.setQuImageSize(fileContent.length);
                            item.setQuImage(fileContent);
                        }
                    } // of If (M1.quImgFilename <> "") Then
                    GlobalVariables.anImgFilename = AnImagePathBuilder.toString();
                    AnImagePathBuilder.setLength(0);
                    if (GlobalVariables.anImgFilename != null && !"".equals(GlobalVariables.anImgFilename)) {
                        File file = new File(GlobalVariables.anImgFilename);
                        byte[] fileContent = Files.readAllBytes(file.toPath());
                        if (fileContent.length > 0) {
                            item.setAnswImageSize(fileContent.length);
                            item.setAnswImage(fileContent);
                        }
                    } // of If (M1.anImgFilename <> "") Then
                    questionText.setText("");
                    answerText.setText("");
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You can not leave question or answer part blank!");
                    alert.showAndWait();
                }
                GlobalVariables.daoFactory.getItemDAO().insert(item);
            /*
Set rs2 = CreateObject("ADODB.Recordset")
rs2.Open "SELECT count(*) FROM items where forreview=0 and userid=" & M1.userID & " and curdate=#" & M1.convDate(M1.date1) & "#", M1.conn1
Label3.Caption = rs2.Fields(0).Value & " new items for " & M1.convDate(M1.date1)
rs2.Close
*/

                int itemCount = GlobalVariables.daoFactory.getItemDAO().getOrigItemCount(GlobalVariables.userID, GlobalVariables.date1);
                label.setText("Input new item Mode: " + itemCount + " new items added for " + DateTimeFunctions.DateToString(GlobalVariables.date1));

                GlobalVariables.quImgFilename="";
                GlobalVariables.anImgFilename = "";
            } catch (SQLException exp) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error: " + exp.getMessage());
                alert.showAndWait();
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error: " + ex.getMessage());
                alert.showAndWait();
            } catch (ParseException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error: " + ex.getMessage());
                alert.showAndWait();
            }
            /*
Set rs2 = Nothing
M1.quImgFilename = ""
M1.anImgFilename = ""
End Sub
             */

        }
    }

    class ReviewOldItemsButton_EventHandler implements EventHandler<ActionEvent> {
        //Stage stage;
        BorderPane parentPane;
        ReviewOldItemsButton_EventHandler(BorderPane parentPane) {
            this.parentPane = parentPane;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: ReviewOldItemsButton_EventHandler");
            ReviewOldItemsPane reviewOldItemsPane = new ReviewOldItemsPane(this.parentPane);
            this.parentPane.setCenter(reviewOldItemsPane);
            //this.stage.getScene().setRoot(reviewOldItemsPane);
        }
    }

    class SelectFileButton_EventHandler implements EventHandler<ActionEvent> {
        StringBuilder fileName;
        //Stage stage;
        //BorderPane parentPane;
        SelectFileButton_EventHandler(StringBuilder fileName) {
            this.fileName = fileName;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: SelectFileButton_EventHandler");
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(stage);
            this.fileName.append(selectedFile.getAbsolutePath());
        }
    }

    class StartNewDayButton_EventHandler implements EventHandler<ActionEvent> {
        Button button_ReviewOldItems;
        Button button_StartNewDay;
        FutureItemDAO futureItemDAO;
        ItemDAO itemDAO;

        StartNewDayButton_EventHandler(Button button_ReviewOldItems, Button button_StartNewDay,
                                       FutureItemDAO futureItemDAO, ItemDAO itemDAO) {
            this.button_ReviewOldItems = button_ReviewOldItems;
            this.button_StartNewDay = button_StartNewDay;
            this.futureItemDAO = futureItemDAO;
            this.itemDAO = itemDAO;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: StartNewDayButton_EventHandler");
            try {
                GlobalVariables.daoFactory.getItemDAO().flushAndClearRemoveItemCache();
                //???
                GlobalVariables.daoFactory.getItemDAO().
                        insertFutureItems02(futureItemDAO.getFutureItemsToBeInsertedIntoItemsCache(),
                                GlobalVariables.date1);
                futureItemDAO.removeFutureItemsToBeInsertedIntoItems02(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsCache());
                GlobalVariables.daoFactory.getItemDAO().
                        insertFutureItems(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsFromDB(GlobalVariables.userID),
                                GlobalVariables.date1);
                GlobalVariables.daoFactory.getFutureItemDAO().removeFutureItemsToBeInsertedIntoItems(GlobalVariables.daoFactory.getFutureItemDAO().getFutureItemsToBeInsertedIntoItemsFromDB(GlobalVariables.userID));

                futureItemDAO.flushToBeUpdatedCache();
                futureItemDAO.clearFutureItemCache();

                GlobalVariables.date1 = DateTimeFunctions.removeHMS(java.util.Calendar.getInstance().getTime());

                futureItemDAO.decreaseFutureItemCycles();
                //futureItemDAO.populateInternalDataWithFutureItemsfromDB(GlobalVariables.userID);




                GlobalVariables.reviewitem = -1; //'meaning no item has been reviewed
                GlobalVariables.reviewed = false;
                GlobalVariables.reviewtime = 1;
                GlobalVariables.reviewDate = ReviewTime.getPrevDate(1);
                //CommandButton2.Enabled = True // review old item
                this.button_ReviewOldItems.setDisable(false);
                //CommandButton3.Enabled = False // start a new day
                this.button_StartNewDay.setDisable(true);

                //conn1.Execute "update users set curdate='" & M1.convDate(M1.date1) & "',  reviewdate='" & M1.convDate(M1.reviewDate) & "',  reviewed=" & 0 & " , reviewtime=" & M1.reviewtime & ", reviewitem=" & M1.reviewitem & "  where userid=" & M1.userID
                User user = GlobalVariables.daoFactory.getUserDAO().queryByKey(GlobalVariables.userID);
                user.setCurdate(GlobalVariables.date1);
                user.setReviewdate(GlobalVariables.reviewDate);
                user.setReviewed(GlobalVariables.reviewed);
                user.setReviewtime(GlobalVariables.reviewtime);
                user.setReviewitem(GlobalVariables.reviewitem);
                GlobalVariables.daoFactory.getUserDAO().update(user);
            /*
            Set rs2 = CreateObject("ADODB.Recordset")
            rs2.Open "SELECT count(*) FROM items where forreview=0 and userid=" & M1.userID & " and curdate=#" & M1.convDate(M1.date1) & "#", M1.conn1
            */
                int count = GlobalVariables.daoFactory.getItemDAO().getOrigItemCount(GlobalVariables.userID, GlobalVariables.date1);

                //Label3.Caption = rs2.Fields(0).Value & " new items for " & M1.convDate(M1.date1)
                label.setText("Input new item Mode: " + count + " new items added for " + DateTimeFunctions.DateToString(GlobalVariables.date1));
                //rs2.Close
                //Set rs2 = Nothing

                ReviewDate.initReviewDates();

                //The following are not really used in Java
                //ForreviewItemDateUtils.initForreviewItemDates();
                //MoveReviewedItem.initToBeRemovedForreviewItemIDs();

                ReviewedCache.initReviewCache();
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Error: " + ex.getMessage());
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
        public void handle(ActionEvent actionEvent) {
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

    class FontSizeDecreaseButton_EventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: FontSizeDecreaseButton_EventHandler");
            GlobalVariables.fontSize *= 0.85;
            questionText.setStyle("-fx-font-size: "
                    + String.valueOf(GlobalVariables.fontSize) + "em;");
            answerText.setStyle("-fx-font-size: "
                    + String.valueOf(GlobalVariables.fontSize) + "em;");
        }
    }


    class FontSizeIncreaseButton_EventHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: FontSizeIncreaseButton_EventHandler");
            GlobalVariables.fontSize *= 1.15;
            questionText.setStyle("-fx-font-size: "
                    + String.valueOf(GlobalVariables.fontSize) + "em;");
            answerText.setStyle("-fx-font-size: "
                    + String.valueOf(GlobalVariables.fontSize) + "em;");
        }
    }
}
