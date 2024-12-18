package sevenrev.view.ReviewOldItems;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.text.ParseException;

public class SearchItemsPane extends GridPane {
    BorderPane parentPane;

    Text heading = new Text("Search Items");
    Label startDate = new Label("Input start date (yyyy-mm-dd):");
    TextField startDateInput = new TextField();
    HBox hb_StartDate = new HBox();

    Label EndDate = new Label("Input end date (yyyy-mm-dd):");
    TextField endDateInput = new TextField();
    HBox hb_EndDate = new HBox();

    Label keywords_label = new Label("Input key words to search (separated by ,):");
    TextField keywordsInput = new TextField();
    HBox hb_keywords = new HBox();

    Button button_submitSearch = new Button("Submit search");
    Button button_goBackToReviewItems = new Button("Go back to ReviewItems");

    public SearchItemsPane(BorderPane parentPane) {
        this.parentPane = parentPane;
        int rowIndex = 0;
        this.add(heading, 1, rowIndex, 1, 1);

        rowIndex ++;
        rowIndex ++;
        rowIndex ++;
        hb_StartDate.getChildren().addAll(startDate, startDateInput);
        hb_StartDate.setSpacing(10);
        this.add(hb_StartDate, 1, rowIndex, 1, 1);

        rowIndex ++;
        rowIndex ++;
        hb_EndDate.getChildren().addAll(EndDate, endDateInput);
        hb_EndDate.setSpacing(10);
        this.add(hb_EndDate, 1, rowIndex, 1, 1);

        rowIndex ++;
        rowIndex ++;
        hb_keywords.getChildren().addAll(keywords_label, keywordsInput);
        hb_keywords.setSpacing(10);
        this.add(hb_keywords, 1, rowIndex, 1, 1);

        rowIndex ++;
        rowIndex ++;
        this.add(button_submitSearch, 1, rowIndex, 1, 1);
        button_submitSearch.setOnAction(new SubmitSearchButton_EventHandler(this.parentPane, this.startDateInput,
                this.endDateInput, this.keywordsInput));
        this.add(button_goBackToReviewItems, 2, rowIndex, 1, 1);
        button_goBackToReviewItems.setOnAction(new GoBackToReviewItems_EventHandler());
    }

    class SubmitSearchButton_EventHandler implements EventHandler<ActionEvent> {
        BorderPane parentPane;
        TextField startDateInput;
        TextField endDateInput;
        TextField keywordsInput;

        String startDate;
        String endDate;
        String keywords;

        SubmitSearchButton_EventHandler(BorderPane borderPane, TextField startDateInput,
                                        TextField endDateInput, TextField keywordsInput) {
            this.parentPane = borderPane;
            this.startDateInput = startDateInput;
            this.endDateInput = endDateInput;
            this.keywordsInput = keywordsInput;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                ReviewSearchItemsPane reviewSearchItemsPane = new ReviewSearchItemsPane(this.parentPane, this.startDateInput.getText(),
                        this.endDateInput.getText(), this.keywordsInput.getText());
                parentPane.setCenter(reviewSearchItemsPane);
            } catch (ParseException| SQLException ex) {
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
