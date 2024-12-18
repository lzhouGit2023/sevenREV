package sevenrev.view.ReviewOldItems;

//import com.sun.javafx.scene.control.skin.VirtualFlow;
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
import javafx.scene.text.Text;
import sevenrev.model.GlobalVariables;
import sevenrev.model.ReviewOldItems.DataForCommunication;
import sevenrev.model.ReviewOldItems.NavigateItems;
import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewSearchItemsPane extends GridPane {
    BorderPane parentPane;

    String startDateText;
    Date startDate;
    String endDateText;
    Date endDate;
    String keywordsText;
    List<String> keywords = new ArrayList<>();

    Text heading = new Text(GlobalVariables.username + ": Review search items");
    Text itemDetails = new Text("");
    Label qu_An_label = new Label("Question");

    //Label label2 = new Label("");
    TextArea quAnText = new TextArea();
    ImageView imageView = new ImageView();
    Button button_CheckAnswer = new Button("Check answer");
    Button button_moveToNext = new Button("Go to next item");
    Button button_update = new Button("Update the item");
    Button button_goBackToReviewItems = new Button("Go back to ReviewItems");

    DataForCommunication dataForCommunication = new DataForCommunication();
    NavigateItems navigateItems;

    static String ERROR_NO_ROWS_FOUND = "There are no such items";
    static String ERROR_NO_MORE_ITEMS_FOUND = "No more items or reached the end of search list";

    public ReviewSearchItemsPane(BorderPane parentPane, String startDateText,
                                 String endDateText, String keywordsText) throws ParseException, SQLException {
        this.parentPane = parentPane;
        button_CheckAnswer.setOnAction(new CheckAnswerButton_EventHandler());
        button_moveToNext.setOnAction(new MoveToNext_EventHandler());
        button_update.setOnAction(new Update_EventHandler());
        button_goBackToReviewItems.setOnAction(new GoBackToReviewItems_EventHandler());
        if (startDateText.trim().length()>0) {
            this.startDate = DateTimeFunctions.StringToDate(startDateText);
        }
        if (endDateText.trim().length()>0) {
            this.endDate = DateTimeFunctions.StringToDate(endDateText);
        }
        if (keywordsText!=null && !"".equals(keywordsText.trim()))  {
            Collections.addAll(this.keywords, keywordsText.trim().toLowerCase().split(","));
            keywords = keywords.stream().map( keyword -> keyword.trim()).collect(Collectors.toList());
        }
        List<Item> items = GlobalVariables.daoFactory.getItemDAO().getOrigItemsAllColumns(GlobalVariables.userID, startDate, endDate);
        if (items.size()==0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, ERROR_NO_ROWS_FOUND);
            alert.showAndWait();
            //SearchItemsPane searchItemsPane = new SearchItemsPane(this.parentPane);
            //parentPane.setCenter(searchItemsPane);
        }
        this.navigateItems = new NavigateItems(this.dataForCommunication);
        navigateItems.setRs1(items);
        //set CurrentItemIndex to -1 sp that after incremented by 1, it starts from 0
        navigateItems.setCurrentItemIndex(-1);
        navigateItems.setCheckanswer(false);

        Item item = navigateItems.getNextItemWithKeywords(keywords);
        if (item!=null) {
            itemDetails.setText("item date: " + item.getCurdate()
                    + ", item no: " + item.getItem() + ", review time: " + item.getReviewtime());
            quAnText.setWrapText(true);
            displayCurrentItem();
        } else {
            button_CheckAnswer.setDisable(true);
            button_moveToNext.setDisable(true);
            button_update.setDisable(true);
            layoutWithoutImage();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, ERROR_NO_ROWS_FOUND);
            alert.showAndWait();
        }
    }

    private void displayCurrentItem() {
        Item item = navigateItems.getCurrentItem();
        if (item == null) {
            return;
        }
        boolean checkAnswer = navigateItems.isCheckanswer();
        itemDetails.setText("item date: " + item.getCurdate()
                + ", item no: " + item.getItem() + ", review time: " + item.getReviewtime());
        if (!checkAnswer) {
            qu_An_label.setText("Question");
            button_CheckAnswer.setText("Check answer");
            quAnText.setText(item.getQuestion());
            if (item.getQuImageSize() > 0) {
                quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                InputStream targetStream = new ByteArrayInputStream(item.getQuImage());
                Image image = new Image(targetStream);
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
                quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withOutImage);
                layoutWithoutImage();
            }
        } else {
            qu_An_label.setText("Answer");
            button_CheckAnswer.setText("Check Question");
            quAnText.setText(item.getAnswer());
            if (item.getAnswImageSize() > 0) {//Then
                quAnText.setMaxHeight(GlobalVariables.textMaxHeight_Review_withImage);
                quAnText.setVisible(true);
                InputStream targetStream = new ByteArrayInputStream(item.getAnswImage());
                Image image = new Image(targetStream);
                if (imageView==null) {
                    imageView = new ImageView(image);
                } else {
                    imageView.setImage(image);
                }
                imageView.setFitHeight(GlobalVariables.imageMaxHeight);
                imageView.setDisable(false);
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
        }
    }

    private void layoutWithoutImage() {
        this.getChildren().clear();
        int rowNum = 0;
        this.add(heading, 0, rowNum, 7, 1);
        rowNum++;
        rowNum++;
        this.add(itemDetails, 0, rowNum, 7, 1);
        rowNum++;
        this.add(qu_An_label, 0, rowNum, 7, 1);

        rowNum++;
        this.add(quAnText, 0, rowNum, 7, 8);

        rowNum+=8;
        this.add(button_CheckAnswer, 1, rowNum, 1, 1);
        this.add(button_moveToNext, 3, rowNum, 1, 1);
        this.add(button_update, 5, rowNum, 1, 1);
        this.add(button_goBackToReviewItems, 7, rowNum, 1, 1);
    }

    private void layoutWithImage() {
        this.getChildren().clear();
        int rowNum = 0;
        this.add(heading, 0, rowNum, 7, 1);
        rowNum++;
        rowNum++;
        this.add(itemDetails, 0, rowNum, 7, 1);
        rowNum++;
        this.add(qu_An_label, 0, rowNum, 7, 1);

        rowNum++;
        this.add(quAnText, 0, rowNum, 7, 8);

        rowNum+=8;
        this.add(imageView, 0, rowNum, 3, 8);

        rowNum+=8;
        this.add(button_CheckAnswer, 1, rowNum, 1, 1);
        this.add(button_moveToNext, 3, rowNum, 1, 1);
        this.add(button_update, 5, rowNum, 1, 1);
        this.add(button_goBackToReviewItems, 7, rowNum, 1, 1);
    }

    class CheckAnswerButton_EventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            navigateItems.setCheckanswer(!navigateItems.isCheckanswer());
            displayCurrentItem();
        }
    }

    class MoveToNext_EventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            navigateItems.setCheckanswer(false);
            navigateItems.getNextItemWithKeywords(keywords);
            if (navigateItems.getCurrentItem()==null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, ERROR_NO_MORE_ITEMS_FOUND);
                alert.showAndWait();
                SearchItemsPane searchItemsPane = new SearchItemsPane(parentPane);
                parentPane.setCenter(searchItemsPane);
            } else {
                displayCurrentItem();
            }
        }
    }

    class Update_EventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                Item item = navigateItems.getCurrentItem();
                if (item != null) {
                    if (!navigateItems.isCheckanswer()) {
                        item.setQuestion(quAnText.getText());
                    } else {
                        item.setAnswer(quAnText.getText());
                    }
                    GlobalVariables.daoFactory.getItemDAO().update(item);
                    displayCurrentItem();
                }
            } catch (SQLException ex) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    class GoBackToReviewItems_EventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            ReviewOldItemsPane reviewOldItemsPane = new ReviewOldItemsPane(parentPane);
            parentPane.setCenter(reviewOldItemsPane);
        }
    }
}
