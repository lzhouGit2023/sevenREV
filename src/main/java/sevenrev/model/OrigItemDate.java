package sevenrev.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrigItemDate {
    private Date origDate;
    private Integer origItemID; // -1 means not found
}
