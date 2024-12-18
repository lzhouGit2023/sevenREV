package sevenrev.model;

import java.util.LinkedList;
import java.util.List;

public class MessageBoxText {
    private LinkedList<String> texts;
    public MessageBoxText () {
        texts = new LinkedList<>();
    }
    public List<String> getTexts() {
        return this.texts;
    }
}
