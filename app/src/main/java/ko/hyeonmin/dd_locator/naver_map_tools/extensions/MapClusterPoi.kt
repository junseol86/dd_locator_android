package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import com.nhn.android.maps.maplib.NGeoPoint
import com.nhn.android.maps.overlay.NMapPOIdata
import com.nhn.android.maps.overlay.NMapPOIitem
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.naver_map_tools.required.NMapPOIflagType
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Hyeonmin on 2017-08-09.
 */
class MapClusterPoi {
    var mapActivity: MapActivity? = null

    var clusterPoiData: NMapPOIdata? = null
    var clusterPoiDataOverlay: NMapPOIdataOverlay? = null

    constructor(ma: MapActivity) {
        mapActivity = ma

        clusterPoiData = NMapPOIdata(1000, mapActivity!!.resourceProvider!!)
    }

    fun showLoadedClusterNumbers(cluster: JSONArray, mode: Int) {
        val baloonText = if (mode == 0) "dong_name" else "asset_count"
        mapActivity?.clearDataOverlays()
        MapSingleton.clusterJA = cluster
        clusterPoiData?.beginPOIdata(0)
        clusterPoiData?.removeAllPOIdata()
        for (i in 0 until MapSingleton.clusterJA!!.length()) {
            val clusterJO: JSONObject = MapSingleton.clusterJA!![i] as JSONObject

            if (mode == 1) {
                if (clusterJO["markOrCluster"] == 0) {
                    break
                }
                if (clusterJO["asset_count"] == 0)
                    continue
            }

            val spot = NGeoPoint(clusterJO["bld_map_x"].toString().toDouble(), clusterJO["bld_map_y"].toString().toDouble())
            val count = clusterJO["asset_count"].toString().toInt()
            var marker: Int = NMapPOIflagType.CLUSTER_1
            if (count > 999) marker = NMapPOIflagType.CLUSTER_999
            else if (count >= 900) marker = NMapPOIflagType.CLUSTER_900
            else if (count >= 800) marker = NMapPOIflagType.CLUSTER_800
            else if (count >= 700) marker = NMapPOIflagType.CLUSTER_700
            else if (count >= 600) marker = NMapPOIflagType.CLUSTER_600
            else if (count >= 500) marker = NMapPOIflagType.CLUSTER_500
            else if (count >= 400) marker = NMapPOIflagType.CLUSTER_400
            else if (count >= 300) marker = NMapPOIflagType.CLUSTER_300
            else if (count >= 200) marker = NMapPOIflagType.CLUSTER_200
            else if (count >= 150) marker = NMapPOIflagType.CLUSTER_150
            else if (count >= 100) marker = NMapPOIflagType.CLUSTER_100
            else if (count >= 90) marker = NMapPOIflagType.CLUSTER_90
            else if (count >= 80) marker = NMapPOIflagType.CLUSTER_80
            else if (count >= 70) marker = NMapPOIflagType.CLUSTER_70
            else if (count >= 60) marker = NMapPOIflagType.CLUSTER_60
            else if (count >= 50) marker = NMapPOIflagType.CLUSTER_50
            else if (count >= 40) marker = NMapPOIflagType.CLUSTER_40
            else if (count >= 30) marker = NMapPOIflagType.CLUSTER_30
            else if (count >= 20) marker = NMapPOIflagType.CLUSTER_20
            else if (count >= 10) marker = NMapPOIflagType.CLUSTER_10
            clusterPoiData?.addPOIitem(spot, clusterJO[baloonText].toString(), marker, null)
        }
        clusterPoiData?.endPOIdata()
        setClusterPoiOverlay()
    }

    fun setClusterPoiOverlay() {
        if (clusterPoiDataOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(clusterPoiDataOverlay)
        clusterPoiDataOverlay = mapActivity?.overlayManager?.createPOIdataOverlay(clusterPoiData, null)
        clusterPoiDataOverlay?.onStateChangeListener = object: NMapPOIdataOverlay.OnStateChangeListener {
            override fun onFocusChanged(p0: NMapPOIdataOverlay?, p1: NMapPOIitem?) {
            }
            override fun onCalloutClick(p0: NMapPOIdataOverlay?, p1: NMapPOIitem?) {
                mapActivity?.mapController?.mapCenter = p1!!.point
                mapActivity?.mapController?.zoomLevel = 12
            }
        }
    }

    fun clearOverlay() {
        if (clusterPoiDataOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(clusterPoiDataOverlay)
    }
}