package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.utils.Secrets
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Hyeonmin on 2017-06-29.
 */

class MapAssetLoader(val ma: MapActivity) {

    var requestQ: RequestQueue? = null

    init {
        requestQ = ma.requestQ!!
    }

    fun loadIfNeeded(forced: Boolean) {
        if (ma.mapFilter != null && ma.mapController!!.zoomLevel > 7 && (forced || ma.mapLimit!!.limitLeft == 0 || ma.mapLimit!!.isOffLimit() || MapSingleton.assetsJA == null)) {
            ma.mapLimit!!.setLimits()

            println("HOHOHOHOHOHO")
            println(ma.mapFilter!!.bldCtgr)
            println("HOHOHOHOHOHO")


            if (ma.mapController!!.zoomLevel > 11) {
                // 지도 줌이 11 이상일 때
                val assetListUrl = Secrets.apiUrl + "assetList_mobile_S2"
                val assetRequest: StringRequest = object: StringRequest(Request.Method.GET, assetListUrl,
                        Response.Listener<String> {
                            ma.showingClusteredAssets = false
                            println(it)
                            ma.mapAssetPoi?.showLoadedAssets(JSONArray(it))
                            if (ma.showingClusteredAssets)
                                ma.mapClusterPoi?.showLoadedClusterNumbers(JSONArray(it), 1)
                            ma.hideLoader()
                        },
                        Response.ErrorListener {
                            println("ERROR")
                            println(it)
                        }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()

                        params["bldCtgr"] = ma.mapFilter!!.bldCtgr
                        params["bldType"] = ma.mapFilter!!.bldType
                        params["top"] = (ma.mapLimit!!.limitTop.toDouble() / 1000000).toString()
                        params["bottom"] = (ma.mapLimit!!.limitBottom.toDouble() / 1000000).toString()
                        params["left"] = (ma.mapLimit!!.limitLeft.toDouble() / 1000000).toString()
                        params["right"] = (ma.mapLimit!!.limitRight.toDouble() / 1000000).toString()
                        params["hasName"] = ma.mapFilter!!.hasName.toString()
                        params["hasNumber"] = ma.mapFilter!!.hasNumber.toString()
                        params["hasGwan"] = ma.mapFilter!!.hasGwan.toString()
                        params["fmlyMin"] = ma.mapFilter!!.fmlyMin.toString()
                        params["fmlyMax"] = ma.mapFilter!!.fmlyMax.toString()
                        params["mainPurps"] = ma.mapFilter!!.mainPurps
                        params["useaprDay"] = ma.mapFilter!!.useaprDay
                        params["visited"] = ma.mapFilter!!.visited.toString()
                        params["factory_count"] = ma.mapFilter!!.factory_count.toString()
                        params["floor_min"] = if (ma.mapFilter!!.floorMinEt.text.toString().trim() == "") "0" else ma.mapFilter!!.floorMinEt.text.toString()
                        println(params)
                        return params
                    }
                }
                assetRequest.retryPolicy = DefaultRetryPolicy(600000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
                requestQ?.add(assetRequest)
                ma.showLoader()
            } else {
                // 지도 줌이 10 이하일 때
                if (!ma.showBalloon) return
                val dongListUrl = Secrets.apiUrl + "assetDongs_mobile_S2"
                val dongRequest: StringRequest = object: StringRequest(Request.Method.GET, dongListUrl,
                        Response.Listener<String> {
                            println(it)
                            ma.mapClusterPoi?.showLoadedClusterNumbers(JSONArray(it), 0)
                            ma.hideLoader()
                        },
                        Response.ErrorListener {
                            println("ERROR")
                            println(it)
                        }
                ) {
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["bldCtgr"] = ma.mapFilter!!.bldCtgr
                        params["bldType"] = ma.mapFilter!!.bldType
                        params["top"] = (ma.mapLimit!!.limitTop.toDouble() / 1000000).toString()
                        params["bottom"] = (ma.mapLimit!!.limitBottom.toDouble() / 1000000).toString()
                        params["left"] = (ma.mapLimit!!.limitLeft.toDouble() / 1000000).toString()
                        params["right"] = (ma.mapLimit!!.limitRight.toDouble() / 1000000).toString()
                        params["hasName"] = ma.mapFilter!!.hasName.toString()
                        params["hasNumber"] = ma.mapFilter!!.hasNumber.toString()
                        params["hasGwan"] = ma.mapFilter!!.hasGwan.toString()
                        params["fmlyMin"] = ma.mapFilter!!.fmlyMin.toString()
                        params["fmlyMax"] = ma.mapFilter!!.fmlyMax.toString()
                        params["mainPurps"] = ma.mapFilter!!.mainPurps
                        params["useaprDay"] = ma.mapFilter!!.useaprDay
                        params["visited"] = ma.mapFilter!!.visited.toString()
                        params["factory_count"] = ma.mapFilter!!.factory_count.toString()
                        params["floor_min"] = if (ma.mapFilter!!.floorMinEt.text.toString().trim() == "") "0" else ma.mapFilter!!.floorMinEt.text.toString()
                        println(params)
                        return params
                    }
                }
                requestQ?.add(dongRequest)
                ma.showLoader()
            }
        }

        ma.mapTrace!!.getTrace()
    }

}