package sevenrev.model;

//import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.TextArea;
import sevenrev.dao.factory.DAOFactory;
import sevenrev.model.entities.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by roy_normal on 6/10/2019.
 */
public class GlobalVariables {
    static Integer toSkipTimes[] = new Integer[20];
    public static boolean skipTimes; //the below url string needs to be checked!!
    //public static String databaseURL="C:/Users/xiong.zhou/Documents/myApps/tmp/7rev_shawn.db";
    public static String databaseURL="C:/Users/leon/Downloads/7rev_Leon.db";
    public static final int steps = 7;

    public static rsCache1 rscache[] = new rsCache1[1500];
    public static Integer RSCacheItem;

    // daoFactory is used to replace (in VBA) conn1 As ADODB.Connection
    public static DAOFactory daoFactory;

    public static Integer userID;
    public static String username;
    public static Boolean reviewed;
    public static Date date1; // date for the current cycle
    public static Date reviewDate;
    public static Integer reviewitem, reviewtime;
    public static Integer time1, time2, time3, time4, time5, time6, time7;
    public static boolean displaysetting; //0, default; 1, only display current date
    public static boolean debugDisplay; //0, not debug; 1, debug
    public static String quImgFilename, anImgFilename;
    public static byte[] quImageBytes; // can be turned to an inputStream then to BufferedImage obj
    //public static Long quImageSize;
    public static int quImageSize;
    public static byte[] answImageBytes; // can be turned to an inputStream then to BufferedImage obj
    public static int answImageSize;

    public static double imageMaxWidth = 550;
    public static double imageMaxHeight = 400;
    public static int textAresRowSpan = 18;
    public static double textMaxHeight_Review_withImage = 300;
    public static double textMaxHeight_Review_withOutImage = 700;

    public static TextArea quAnText = null;
    public static TextArea questionText = null;
    public static TextArea answerText = null;
    //got from items and reviewLog tables
    static ItemReviewDate reviewDates[] = new ItemReviewDate[10000];
    public static int reviewDates_index = 0;
    public static ForreviewItemDate forreviewItemDates[] = new ForreviewItemDate[100000];
    public static int forreviewItemDates_MaxIndex;

    public static double fontSize=1.3;

    //it is used to check how many items were checked on that day (but not really tested/reviewed)
    public static Integer checkedItemCount;
    //public static Integer toBeRemovedForreviewItemIDs[] = new Integer[20000];
    public static List<Integer> toBeRemovedForreviewItemIDs = new ArrayList<>();
    public static Integer toBeRemovedForreviewItemIDs_max;
    public static Integer skippedItemCount;
    public static Integer movedForreviewItemCount;

    public static int batchSize = 5000;
}
