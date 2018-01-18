package ko.hyeonmin.dd_locator.naver_map_tools

import ko.hyeonmin.dd_locator.naver_bldMap_tools.Asset
import org.json.JSONArray

/**
 * Created by Hyeonmin on 2017-07-03.
 */
object MapSingleton {
    var assetsJA: JSONArray? = null
    var clusterJA: JSONArray? = null
    var assetTypeIndex: Int = 1
    var baseIdx: String = ""

    var asset: Asset? = null
    var noPhotoFilter = "0"
}