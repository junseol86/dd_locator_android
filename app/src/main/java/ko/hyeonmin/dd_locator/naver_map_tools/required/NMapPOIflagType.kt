package ko.hyeonmin.dd_locator.naver_map_tools.required

/**
 * Created by Hyeonmin on 2017-06-27.
 */

/**
 * Wrapper class representing POI flag types on map view.

 * @author kyjkim
 */
object NMapPOIflagType {
    val UNKNOWN = 0x0000

    // Single POI icons
    private val SINGLE_POI_BASE = 0x0100

    // Spot, Pin icons
    val SPOT = SINGLE_POI_BASE + 1
    val PIN = SPOT + 1
    val ASSET_JY_WY_GY_SY = SPOT + 2
    val ASSET_JY_WY_GY_SN = SPOT + 3
    val ASSET_JY_WY_GN_SY = SPOT + 4
    val ASSET_JY_WY_GN_SN = SPOT + 5
    val ASSET_JY_WN_GY_SY = SPOT + 6
    val ASSET_JY_WN_GY_SN = SPOT + 7
    val ASSET_JY_WN_GN_SY = SPOT + 8
    val ASSET_JY_WN_GN_SN = SPOT + 9
    val ASSET_JN_WY_GY_SY = SPOT + 10
    val ASSET_JN_WY_GY_SN = SPOT + 11
    val ASSET_JN_WY_GN_SY = SPOT + 12
    val ASSET_JN_WY_GN_SN = SPOT + 13
    val ASSET_JN_WN_GY_SY = SPOT + 14
    val ASSET_JN_WN_GY_SN = SPOT + 15
    val ASSET_JN_WN_GN_SY = SPOT + 16
    val ASSET_JN_WN_GN_SN = SPOT + 17

    val FTR_V0_P0_T0 = SPOT + 18
    val FTR_V0_P0_T1 = SPOT + 19
    val FTR_V0_P0_T2 = SPOT + 20
    val FTR_V0_P1_T0 = SPOT + 21
    val FTR_V0_P1_T1 = SPOT + 22
    val FTR_V0_P1_T2 = SPOT + 23
    val FTR_V1_P0_T0 = SPOT + 24
    val FTR_V1_P0_T1 = SPOT + 25
    val FTR_V1_P0_T2 = SPOT + 26
    val FTR_V1_P1_T0 = SPOT + 27
    val FTR_V1_P1_T1 = SPOT + 28
    val FTR_V1_P1_T2 = SPOT + 29

    val CLUSTER_START = SPOT + 100
    val CLUSTER_1 = CLUSTER_START + 1
    val CLUSTER_10 = CLUSTER_START + 2
    val CLUSTER_20 = CLUSTER_START + 3
    val CLUSTER_30 = CLUSTER_START + 4
    val CLUSTER_40 = CLUSTER_START + 5
    val CLUSTER_50 = CLUSTER_START + 6
    val CLUSTER_60 = CLUSTER_START + 7
    val CLUSTER_70 = CLUSTER_START + 8
    val CLUSTER_80 = CLUSTER_START + 9
    val CLUSTER_90 = CLUSTER_START + 10
    val CLUSTER_100 = CLUSTER_START + 11
    val CLUSTER_150 = CLUSTER_START + 12
    val CLUSTER_200 = CLUSTER_START + 13
    val CLUSTER_300 = CLUSTER_START + 14
    val CLUSTER_400 = CLUSTER_START + 15
    val CLUSTER_500 = CLUSTER_START + 16
    val CLUSTER_600 = CLUSTER_START + 17
    val CLUSTER_700 = CLUSTER_START + 18
    val CLUSTER_800 = CLUSTER_START + 19
    val CLUSTER_900 = CLUSTER_START + 20
    val CLUSTER_999 = CLUSTER_START + 21

    // Direction POI icons: From, To
    private val DIRECTION_POI_BASE = 0x0200
    val FROM = DIRECTION_POI_BASE + 1
    val TO = FROM + 1

    // end of single marker icon
    val SINGLE_MARKER_END = 0x04FF

    // Direction Number icons
    private val MAX_NUMBER_COUNT = 1000
    val NUMBER_BASE = 0x1000 // set NUMBER_BASE + 1 for '1' number
    val NUMBER_END = NUMBER_BASE + MAX_NUMBER_COUNT

    // Custom POI icons
    private val MAX_CUSTOM_COUNT = 1000
    val CUSTOM_BASE = NUMBER_END
    val CUSTOM_END = CUSTOM_BASE + MAX_CUSTOM_COUNT

    // Clickable callout에 보여지는 화살표
    val CLICKABLE_ARROW = CUSTOM_END + 1

    fun isBoundsCentered(markerId: Int): Boolean {
        var boundsCentered = false

        when (markerId) {
            else -> if (markerId >= NUMBER_BASE && markerId < NUMBER_END) {
                boundsCentered = true
            }
        }

        return boundsCentered
    }

    fun getMarkerId(poiFlagType: Int, iconIndex: Int): Int {
        val markerId = poiFlagType + iconIndex

        return markerId
    }

    fun getPOIflagType(markerId: Int): Int {
        var poiFlagType = UNKNOWN

        // Alphabet POI icons
        if (markerId in NUMBER_BASE..(NUMBER_END - 1)) { // Direction Number icons
            poiFlagType = NUMBER_BASE
        } else if (markerId in CUSTOM_BASE..(CUSTOM_END - 1)) { // Custom POI icons
            poiFlagType = CUSTOM_BASE
        } else if (markerId > SINGLE_POI_BASE) {
            poiFlagType = markerId
        }

        return poiFlagType
    }

    fun getPOIflagIconIndex(markerId: Int): Int {
        var iconIndex = 0

        if (markerId in NUMBER_BASE..(NUMBER_END - 1)) { // Direction Number icons
            iconIndex = markerId - (NUMBER_BASE + 1)
        } else if (markerId in CUSTOM_BASE..(CUSTOM_END - 1)) { // Custom POI icons
            iconIndex = markerId - (CUSTOM_BASE + 1)
        } else if (markerId > SINGLE_POI_BASE) {
            iconIndex = 0
        }

        return iconIndex
    }
}