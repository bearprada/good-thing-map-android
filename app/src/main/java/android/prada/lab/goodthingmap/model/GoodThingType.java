package android.prada.lab.goodthingmap.model;

/**
 * Created by prada on 17/08/2017.
 */

public enum GoodThingType {
    MAIN,
    SNACK,
    FRUIT,
    OTHER,
    TBI,
    NEAR;

    public String getName() {
        switch(this) {
            case MAIN:
                return "主食";
            case SNACK:
                return "小吃";
            case FRUIT:
                return "冰品/水果";
            case OTHER:
                return "其他";
            case TBI:
                return "大誌雜誌";
            case NEAR:
            default:
                return "綜合搜尋";
        }
    }

    public int getTypeId() {
        return ordinal() + 1;
    }
}