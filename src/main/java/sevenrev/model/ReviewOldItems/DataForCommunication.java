package sevenrev.model.ReviewOldItems;

import lombok.Getter;
import lombok.Setter;
import sevenrev.model.MessageBoxText;

@Getter
@Setter
public class DataForCommunication {
    private int Text1Height;
    private boolean Text1Visible = true;
    private MessageBoxText messageBoxText = new MessageBoxText();
    private boolean Command5Enabled = true; // check answer button
    private boolean CommandButton4Enabled = true; // can remember button
    private boolean CommandButton3Enabled = true; // I forgot button
    private boolean CommandButton1Enabled = true; // unused button
}
