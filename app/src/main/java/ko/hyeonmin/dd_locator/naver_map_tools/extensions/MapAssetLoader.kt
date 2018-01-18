package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.utils.Secrets
import org.json.JSONArray

/**
 * Created by Hyeonmin on 2017-06-29.
 */

class MapAssetLoader {

    var mapActivity: MapActivity? = null
    var requestQ: RequestQueue? = null

    constructor(ma: MapActivity) {
        mapActivity = ma
        requestQ = ma.requestQ!!
    }

    fun loadIfNeeded(forced: Boolean) {
        if (forced || mapActivity!!.mapLimit!!.limitLeft == 0 || mapActivity!!.mapLimit!!.isOffLimit() || MapSingleton.assetsJA == null) {
            mapActivity!!.mapLimit!!.setLimits()

            if (mapActivity!!.mapController!!.zoomLevel > 10) {
                // 지도 줌이 11 이상일 때
                val assetListUrl = Secrets.apiUrl + "assetListV2"
                val assetRequest: StringRequest = object: StringRequest(Request.Method.GET, assetListUrl,
                        Response.Listener<String> {
                            mapActivity?.showingClusteredAssets = false
                            mapActivity?.mapAssetPoi?.showLoadedAssets(JSONArray(it))
                            if (mapActivity!!.showingClusteredAssets)
                                mapActivity?.mapClusterPoi?.showLoadedClusterNumbers(JSONArray(it), 1)
                            mapActivity?.hideLoader()
                        },
                        Response.ErrorListener {
                            println("ERROR")
                        }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params.put("bldType", mapActivity!!.assetTypes[MapSingleton.assetTypeIndex])
                        params.put("top", (mapActivity!!.mapLimit!!.limitTop.toDouble() / 1000000).toString())
                        params.put("bottom", (mapActivity!!.mapLimit!!.limitBottom.toDouble() / 1000000).toString())
                        params.put("left", (mapActivity!!.mapLimit!!.limitLeft.toDouble() / 1000000).toString())
                        params.put("right", (mapActivity!!.mapLimit!!.limitRight.toDouble() / 1000000).toString())
                        params.put("noName", false.toString())
                        params.put("noNumber", false.toString())
                        params.put("noGwan", false.toString())
                        params.put("fmlyMin", (-1).toString())
                        params.put("fmlyMax", (-1).toString())
                        params.put("mainPurps", "")
                        params.put("useaprDay", "")
                        return params
                    }
                }
                requestQ?.add(assetRequest)
                mapActivity?.showLoader()
            } else {
                // 지도 줌이 10 이하일 때
                if (!mapActivity!!.showBalloon) return
                val dongListUrl = Secrets.apiUrl + "assetDongsV2"
                val dongRequest: StringRequest = object: StringRequest(Request.Method.GET, dongListUrl,
                        Response.Listener<String> {
                            mapActivity?.mapClusterPoi?.showLoadedClusterNumbers(JSONArray(it), 0)
                            mapActivity?.hideLoader()
                        },
                        Response.ErrorListener {
                            println("ERROR")
                        }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params.put("bldType", mapActivity!!.assetTypes[MapSingleton.assetTypeIndex])
                        params.put("top", (mapActivity!!.mapLimit!!.limitTop.toDouble() / 1000000).toString())
                        params.put("bottom", (mapActivity!!.mapLimit!!.limitBottom.toDouble() / 1000000).toString())
                        params.put("left", (mapActivity!!.mapLimit!!.limitLeft.toDouble() / 1000000).toString())
                        params.put("right", (mapActivity!!.mapLimit!!.limitRight.toDouble() / 1000000).toString())
                        params.put("noName", false.toString())
                        params.put("noNumber", false.toString())
                        params.put("noGwan", false.toString())
                        params.put("fmlyMin", (-1).toString())
                        params.put("fmlyMax", (-1).toString())
                        params.put("mainPurps", "")
                        params.put("useaprDay", "")
                        return params
                    }
                }
                requestQ?.add(dongRequest)
                mapActivity?.showLoader()
            }
        }

        mapActivity!!.mapTrace!!.getTrace()
    }

}