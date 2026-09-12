package android.prada.lab.goodthingmap.model

enum class GoodThingType {
    MAIN,
    SNACK,
    FRUIT,
    OTHER,
    TBI,
    NEAR;

    fun getName(): String = when (this) {
            MAIN -> "主食"
            SNACK -> "小吃"
            FRUIT -> "冰品/水果"
            OTHER -> "其他"
            TBI -> "大誌雜誌"
            NEAR -> "綜合搜尋"
        }

    val typeId: Int
        get() = ordinal + 1
}
