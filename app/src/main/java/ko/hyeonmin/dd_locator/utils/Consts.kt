package ko.hyeonmin.dd_locator.utils

object Consts {

    var bldTypeOpt = arrayOf(
            Pair("전체", "all"),
            Pair("아파트", "apt"),
            Pair("연립·다세대", "yd"),
            Pair("오피스텔", "ot"),
            Pair("단독", "dd"),
            Pair("다가구", "ONEROOM"),
            Pair("상업·업무용", "su"),
            Pair("다가구X상가", "ONEROOM_SU"),
            Pair("토지", "lnd"),
            Pair("분양·입주권", "bi"),
            Pair("모텔·펜션", "mp"),
            Pair("펜션1", "ps"),
            Pair("공장", "FACTORY"),
            Pair("공장2", "FACTORY2"),
            Pair("창고", "str")
)

    var bldTypeOptForFilter = arrayOf(
            Pair("전체", "all"),
            Pair("아파트", "apt"),
            Pair("연립·다세대", "yd"),
            Pair("오피스텔", "ot"),
            Pair("단독", "dd"),
            Pair("다가구", "ONEROOM"),
            Pair("상업·업무용", "su"),
            Pair("다가구X상가", "ONEROOM_SU"),
            Pair("다가구+상가", "OR_SU_BOTH"),
            Pair("토지", "lnd"),
            Pair("분양·입주권", "bi"),
            Pair("모텔·펜션", "mp"),
            Pair("펜션1", "ps"),
            Pair("공장", "FACTORY"),
            Pair("공장2", "FACTORY2"),
            Pair("창고", "str"),

//            이것 하나가 추가됨
            Pair("공장&창고", "ftrstr")
    )
}