package sevenrev.model;

import lombok.Getter;
import lombok.Setter;
import sevenrev.model.entities.FutureItem;
import sevenrev.model.entities.Item;

@Setter
@Getter
public class ReturnWithData {
    MessageBoxText messageBoxText = new MessageBoxText();
    Item item;
    FutureItem futureItem;
    Object scenario = null;
}
