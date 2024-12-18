package sevenrev;

//import android.content.Context;
import org.junit.*;
import sevenrev.dao.ItemDAO;
import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

/**
 * Created by roy on 2017-09-02.
 * This super class includes common setUp(..) and teraDown(..)
 * and has the implementation of DB operations
 */
@Ignore
public class TestSuper {
    //private Context context;
    private String DBPath;

    /*
    TestSuper(Context context, String DBPath) {
        context = context;
        DBPath = DBPath;
    }
    */
    @Before
    void setUp() {
    }

    @After
    void tearDown() {

    }
}
