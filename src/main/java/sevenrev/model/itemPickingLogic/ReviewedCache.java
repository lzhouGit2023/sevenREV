package sevenrev.model.itemPickingLogic;

//import android.content.Context;

import sevenrev.model.GlobalVariables;
import sevenrev.model.MessageBoxText;
import sevenrev.model.entities.Item;
import sevenrev.utilities.DateTimeFunctions;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static sevenrev.model.GlobalVariables.RSCacheItem;

/**
 * Created by roy on 2017-01-30.
 */
public class ReviewedCache {
    //In old MSAccess 7rev the following function only takes orig item to GlobalVariables.rscache
    //Now in Java, this function can take both forreview and non-forreview item
    public static MessageBoxText AddToReviewedCache(Date dateCache, Integer itemCache) {
        MessageBoxText msgBoxText = new MessageBoxText();
        try {
            List<Item> items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID, dateCache, itemCache);
            Item item = items.size() > 0 ? items.get(0) : null;
            if (item != null && item.getForreview()) {
                items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns03(GlobalVariables.userID,
                        item.getReviewdate(), item.getReviewitem());
                item = items.size() > 0 ? items.get(0) : null;
            }

            if (item != null) {
                GlobalVariables.rscache[RSCacheItem].setDate(item.getCurdate());
                GlobalVariables.rscache[RSCacheItem].setItem(item.getItem());
                GlobalVariables.rscache[RSCacheItem].setHaveItem(true);

                if (GlobalVariables.RSCacheItem >= 1500) {
                    msgBoxText.getTexts().add("Sorry, Record Cache is over 1500");
                } else {
                    GlobalVariables.RSCacheItem++;
                }
            }
        } catch (SQLException ex) {
            msgBoxText.getTexts().add("Error: " + ex.getMessage());
        }
        return msgBoxText;
    }

    //The following method based on whether the item is a forreview item
    // and will cache only the non-forreview item to rscache
    public static boolean InReviewedCache(boolean forreview, Date reviewDate, Integer reviewitem, Date Curdate, Integer itemID) {
        boolean flag1 = false;
        Date dateCache;
        Integer itemCache;
        int i1 = 0;
        if (!forreview) {
            dateCache = Curdate;
            itemCache = itemID;
        } else {
            dateCache = reviewDate;
            itemCache = reviewitem;
        }

        while ((i1 < 1500) && (GlobalVariables.rscache[i1].isHaveItem()) && (!flag1)) {
            if (DateTimeFunctions.removeHMS(GlobalVariables.rscache[i1].getDate())
                    .equals(DateTimeFunctions.removeHMS(dateCache))) {
                if (GlobalVariables.rscache[i1].getItem().equals(itemCache)) {
                    flag1 = true;
                }
            }
            i1 ++;
        }
        return flag1;
    }

    public static MessageBoxText initReviewCache() {
        MessageBoxText msgBoxText = new MessageBoxText();
        Date tmpDate;
        Integer whichReviewDay;
        whichReviewDay = 1;
        RSCacheItem = 0;
        if (GlobalVariables.daoFactory == null) {
            msgBoxText.getTexts().add("daoFactory is still null");
            return msgBoxText;
        }
        try {
            List<Item> items = GlobalVariables.daoFactory.getItemDAO().getItemsAllColumns01(GlobalVariables.userID, false, GlobalVariables.date1);
            if (items.size()>0) {
                for (Item item: items) {
                    AddToReviewedCache(item.getCurdate(), item.getItem());
                }
            }
            msgBoxText.getTexts().add("There are " + RSCacheItem + " items in cache");
        } catch (SQLException sqlExp) {
            msgBoxText.getTexts().add(sqlExp.getMessage());
        }
        return msgBoxText;
    }

    // ========================================================================
    //=========================================================================
    private class ReviewedItem {
        int itemId;
        Date itemDate;
    }

    //Context context;
    int ReviewedItemCache_MaxIndex;
    ReviewedItem[] reviewedItems;

    public ReviewedCache() {
            ReviewedItemCache_MaxIndex = 0;
            reviewedItems = new ReviewedItem[5000];
        }

    private static class ReviewedItemCacheHolder {
        private static final sevenrev.model.itemPickingLogic.ReviewedCache reviewedCache = new sevenrev.model.itemPickingLogic.ReviewedCache();
    };

    static sevenrev.model.itemPickingLogic.ReviewedCache getInstance() {
        return ReviewedItemCacheHolder.reviewedCache;
    };

    /*
    private void setApplicationContext(Context context) {
        this.context = context;
    }
    */

    /*
    public void initReviewedCache() {
        String path = SevenRevContext.DB_Path;
        DAOFactory daoFactory = new DAOFactory(this.context, path);
        ItemDAO itemDao = daoFactory.getItemDAO();
        itemDao.
    }
    */


}
