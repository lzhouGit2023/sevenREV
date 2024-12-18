package sevenrev.view;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Category(TestClassA.class)
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.println("just test in test class 123");
    }
}