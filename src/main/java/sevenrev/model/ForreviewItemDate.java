package sevenrev.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ForreviewItemDate {
    Integer forreviewItemID;
    Date forreviewDate;
    Integer origItemID;
    Date origDate;
}
