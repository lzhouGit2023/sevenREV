package sevenrev.view.initForm;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.*;
import sevenrev.model.entities.User;
import sevenrev.model.itemPickingLogic.ReviewedCache;
import sevenrev.view.ReviewOldItems.ReviewOldItemsPane;

import java.sql.SQLException;
import java.util.List;

import static sevenrev.model.GlobalVariables.quAnText;

// updated just for commit
public class InitForm extends Application {
    //Created just for commit
    String justTest01;
    //String databaseURL = null;
    private DAOFactory daoFactory;

    Stage primaryStage = null;
    //Group group = new Group();
    //FlowPane flowpane = new FlowPane();
    BorderPane borderPane = new BorderPane();
    GridPane gridPane = new GridPane();

    Scene scene = new Scene(borderPane, 800, 600);
    Label label = new Label("Please select user!");
    ComboBox comboBox = new ComboBox();
    Button button = new Button("Select this user");

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        borderPane.setStyle("-fx-padding: 22;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        borderPane.setCenter(gridPane);
        if (System.getProperty("databaseURL")!=null) {
            GlobalVariables.databaseURL = System.getProperty("databaseURL");
            GlobalVariables.daoFactory = new DAOFactory("jdbc:sqlite:" + GlobalVariables.databaseURL);
        } else {
            if (GlobalVariables.databaseURL!=null) {
                GlobalVariables.daoFactory = new DAOFactory("jdbc:sqlite:" + GlobalVariables.databaseURL);
            }
        }

        primaryStage.setTitle("7Rev");
        primaryStage.setScene(scene);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue()*95/100 > 550) {
                if (quAnText != null) {
                    quAnText.setPrefWidth(newVal.doubleValue() * 95 / 100);
                }
                if (GlobalVariables.questionText!=null) {
                    GlobalVariables.questionText.setPrefWidth(newVal.doubleValue() * 95 / 100);
                }
                if (GlobalVariables.answerText!=null) {
                    GlobalVariables.answerText.setPrefWidth(newVal.doubleValue() * 95 / 100);
                }
            }
        });
        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > 320) {
                if (quAnText != null) {
                    quAnText.setPrefHeight(newVal.doubleValue() / 2);
                }
                if (GlobalVariables.questionText!=null) {
                    GlobalVariables.questionText.setPrefHeight(newVal.doubleValue() / 2);
                }
                if (GlobalVariables.answerText!=null) {
                    GlobalVariables.answerText.setPrefHeight(newVal.doubleValue() / 2);
                }
            }
        });

        //group.getChildren().add(label);
        //gridPane.getChildren().add(label);
        gridPane.add(label, 0, 0, 1, 1);

        //group.getChildren().add(comboBox);
        //gridPane.getChildren().add(comboBox);
        gridPane.add(comboBox, 0, 1, 1, 1);

        //group.getChildren().add(button);
        //gridPane.getChildren().add(button);
        gridPane.add(button, 0, 2, 1, 1);
        button.setMaxSize(100, 200);
        button.setOnAction(new buttonEventHandler(borderPane));

        initFormComponents();

        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void initFormComponents() throws SQLException {
        try {
            ConnectionSource connectionSource =
                    new JdbcConnectionSource("jdbc:sqlite:" + GlobalVariables.databaseURL);
            daoFactory = new DAOFactory("jdbc:sqlite:" + GlobalVariables.databaseURL);
        } catch (SQLException ex) {
            System.out.println("Exception " + ex.getMessage());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Exception " + ex.getMessage());
            alert.showAndWait();
            throw new SQLException(ex.getMessage());
        }

        //Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Exception: test");
        //alert.showAndWait();
        /*
        Dim rs1 As ADODB.Recordset
        Dim name As String
        Dim itemID As Integer
        Dim i1 As Integer
        Dim strConn As String

        Set M1.conn1 = CreateObject("ADODB.Connection")

        strConn = "DSN=" & M1.dsn1

        strConn = "DSN=" & (M1.dsn1) & ";DRIVER={MS Access};UID=;PWD="
        M1.conn1.Open strConn
        Set rs1 = CreateObject("ADODB.Recordset")

        rs1.Open "SELECT name, userid FROM users order by userid asc", M1.conn1
        */

        List<User> users =daoFactory.getUserDAO().getAllUserNameIDs();
        comboBox.getItems().addAll(users);

        if (users==null || users.size()==0) {
            //CommandButton1.Enabled = False
            button.setDisable(true);
            //MsgBox "There is no user information in the database"
        } else {
            /*
            rs1.MoveFirst
            Combo1.Text = rs1.Fields("name").Value

            i1 = 0
            While Not rs1.EOF
                name = rs1.Fields("name").Value
                arr1(i1) = rs1.Fields("userid").Value
                Combo1.AddItem name, i1
                rs1.MoveNext
                i1 = i1 + 1
            Wend
            End If
            */
        }

        //m1.null7RevItem.item = -1
        GlobalVariables.skipTimes = false;
        if (GlobalVariables.skipTimes) {
            label.setText(label.getText() + "(Skip)");
        } else {
            label.setText(label.getText() + "(Not Skip)");
        }
    }

    class buttonEventHandler implements EventHandler<ActionEvent> {
        //Stage stage;
        BorderPane parentPane;

        buttonEventHandler(BorderPane parentPane) {
            this.parentPane = parentPane;
        }

        @Override
        public void handle(ActionEvent actionEvent) {
            System.out.println("GUIOutput: Clicked to select a user");
            // init array toSkipTimes()
            ReviewTime.initToSkipTimes();
            User user = (User) comboBox.getValue();
            primaryStage.setTitle("7Rev: " + user.getName());
            //Dim rs1 As ADODB.Recordset
            GlobalVariables.userID = user.getUserid();
            //M1.userID = arr1(Combo1.ListIndex)
            GlobalVariables.username = user.getName();
            //M1.username = Combo1.Text

            if (GlobalVariables.userID < 0) {
                GlobalVariables.userID = 0;
            }
            ReviewDate.initReviewDates();
            //The following is commented out
            // because GlobalVariables.forreviewItemDates is not really used
            //ForreviewItemDateUtils.initForreviewItemDates();
            GlobalVariables.checkedItemCount = 0;
            MoveReviewedItem.initToBeRemovedForreviewItemIDs();
            GlobalVariables.skippedItemCount = 0;
            GlobalVariables.movedForreviewItemCount = 0;

            GlobalVariables.reviewed = user.getReviewed();
            GlobalVariables.date1 = user.getCurdate();
            GlobalVariables.time1 = user.getTime1();
            GlobalVariables.time2 = user.getTime2();
            GlobalVariables.time3 = user.getTime3();
            GlobalVariables.time4 = user.getTime4();
            GlobalVariables.time5 = user.getTime5();
            GlobalVariables.time6 = user.getTime6();
            GlobalVariables.time7 = user.getTime7();
            GlobalVariables.displaysetting = user.getDisplaysetting();
            GlobalVariables.debugDisplay = user.getDebugdisplay();

            if (GlobalVariables.reviewed) {
                GlobalVariables.reviewDate = ReviewTime.getPrevDate(1);
                GlobalVariables.reviewtime = 1;
                GlobalVariables.reviewitem = 1;
            } else {
                GlobalVariables.reviewDate = user.getReviewdate();
                GlobalVariables.reviewtime = user.getReviewtime();
                GlobalVariables.reviewitem = user.getReviewitem();
            }

            int i1 = 0;
            while (i1 < 500) {
                GlobalVariables.rscache[i1] = new rsCache1();
                GlobalVariables.rscache[i1].setItem(0);
                GlobalVariables.rscache[i1].setHaveItem(false);
                i1++;
            }

            GlobalVariables.RSCacheItem = 0;

            ReviewedCache.initReviewCache();
            ReviewOldItemsPane reviewOldItemsPane = new ReviewOldItemsPane(this.parentPane);
            //scene.setRoot(reviewOldItemsPane);
            parentPane.setCenter(reviewOldItemsPane);
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            GlobalVariables.daoFactory.getItemDAO().flushAndClearRemoveItemCache();

            //GlobalVariables.daoFactory.getFutureItemDAO().populateInternalDataWithFutureItemsfromDB(GlobalVariables.userID);
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Err occurred when exiting: " + ex.getMessage());
            alert.showAndWait();
        }

        super.stop();
    }
}
