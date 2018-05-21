package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.nhn.android.maps.maplib.NGeoPoint
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.utils.Secrets
import org.json.JSONObject

/**
 * Created by Hyeonmin on 2017-06-28.
 */

class MapVolley(ma: MapActivity) {
    val mapActivity = ma
    var rq: RequestQueue = ma.requestQ!!

    fun coordsToAddress(point: NGeoPoint) {
        val coordsToAddressRequest: StringRequest = object: StringRequest(Request.Method.GET,
                "${Secrets.coordsToAddressUrl}${point.longitude},${point.latitude}",
                Response.Listener {
                    mapActivity.mapInputInterface?.refinedSpotAddresses = JSONObject(it).getJSONObject("result").getJSONArray("items")
                    mapActivity.mapInputInterface?.onSpotRefined()

                },
                Response.ErrorListener {
                    println("ERROR")
                }
                ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("X-Naver-Client-Id", Secrets.naverMapClientId)
                params.put("X-Naver-Client-Secret", Secrets.naverMapClientSecret)
                return params
            }
        }
        rq.add(coordsToAddressRequest)
    }

    fun getAnAsset(baseIdx: String, usage: Int) {
        var getAnAssetRequest: StringRequest = object : StringRequest(Request.Method.GET,
                "${Secrets.apiUrl}asset_S2/$baseIdx",
                Response.Listener {
                    when (usage) {
                        0 -> mapActivity.afterGetAnAsset(JSONObject(it))
                        1 -> mapActivity.mapAssetPoi?.copyToClipboard(JSONObject(it))
                    }
                },
                Response.ErrorListener {
                    println("ERROR")
                 }
                ) {
        }
        rq.add(getAnAssetRequest)
    }

    fun modifyLocation() {
        val modifyRequest: StringRequest = object: StringRequest(Request.Method.PUT,
                Secrets.apiUrl + "asset/relocate",
                Response.Listener {
                    mapActivity.mapInputInterface?.setRelocationingAsset(false)
                    mapActivity.mapInputInterface?.clearInputOverlays()
                    mapActivity.clearDataOverlays()
                    mapActivity.mapAssetLoader?.loadIfNeeded(true)
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("base_idx", MapSingleton.baseIdx)
                return params
            }
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            override fun getParams(): MutableMap<String, String> {
                var params: MutableMap<String, String> = HashMap<String, String>()

                params.put("bld_map_x", MapSingleton.asset!!.bldMapX)
                params.put("bld_map_y", MapSingleton.asset!!.bldMapY)
                params.put("plat_plc", MapSingleton.asset!!.platPlc)
                params.put("new_plat_lc", MapSingleton.asset!!.newPlatPlc)
                return params
            }
        }
        rq.add(modifyRequest)
    }
}