package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
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
class MapAssetPoi {
    var mapActivity: MapActivity? = null

    var assetsPoiData: NMapPOIdata? = null
    var assetsPoiDataOverlay: NMapPOIdataOverlay? = null

    constructor(ma: MapActivity) {
        mapActivity = ma

        assetsPoiData = NMapPOIdata(1000, mapActivity!!.resourceProvider)
    }

    //    보이는 지역의 매물 다운로드 받은 것을 보여주기
    fun showLoadedAssets(assets: JSONArray) {
        mapActivity?.clearDataOverlays()
        MapSingleton.assetsJA = assets
        assetsPoiData?.beginPOIdata(0)
        assetsPoiData?.removeAllPOIdata()

        for (i in 0 until MapSingleton.assetsJA!!.length()) {
            val assetsJO: JSONObject = MapSingleton.assetsJA!![i] as JSONObject

            // 매물이 많아 클러스터가 된 경우 루프를 멈춘다
            if (assetsJO["markOrCluster"] == 1) {
                mapActivity?.showingClusteredAssets = true
                break
            }

            val spot = NGeoPoint(assetsJO["bld_map_x"].toString().toDouble(), assetsJO["bld_map_y"].toString().toDouble())
            var marker: Int
            var markerString = assetsJO["bld_name"].toString()

            if (assetsJO["work_requested"].toString().trim() == "") {
                marker = if (assetsJO["bld_name"].toString() == "(자동입력)") NMapPOIflagType.ASSET_JN_WN_GN_SN
                else if (assetsJO["bld_tel_owner"].toString().trim() != "") NMapPOIflagType.ASSET_JY_WN_GY_SN else NMapPOIflagType.ASSET_JN_WN_GY_SN
            } else {
                marker = if (assetsJO["bld_name"].toString() == "(자동입력)") NMapPOIflagType.ASSET_JN_WY_GN_SN
                else if (assetsJO["bld_tel_owner"].toString().trim() != "") NMapPOIflagType.ASSET_JY_WY_GY_SN else NMapPOIflagType.ASSET_JN_WY_GY_SN
            }
            val ownerTel = if (assetsJO["bld_tel_owner"].toString().trim() == "") "없음" else assetsJO["bld_tel_owner"].toString()
            assetsPoiData?.addPOIitem(spot, markerString, marker, "${assetsJO["bld_idx"]}§$ownerTel§${assetsJO["bld_ipkey"]}§${assetsJO["bld_roomkey"]}")
        }
        assetsPoiData?.endPOIdata()
        setAssetsPoiOverlay()
    }

    private fun setAssetsPoiOverlay() {
        if (assetsPoiDataOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(assetsPoiDataOverlay)
        assetsPoiDataOverlay = mapActivity?.overlayManager?.createPOIdataOverlay(assetsPoiData, null)
        assetsPoiDataOverlay?.onStateChangeListener = object : NMapPOIdataOverlay.OnStateChangeListener {
            override fun onFocusChanged(p0: NMapPOIdataOverlay?, p1: NMapPOIitem?) {
            }
            override fun onCalloutClick(p0: NMapPOIdataOverlay?, p1: NMapPOIitem?) {
//                풍선을 클릭했을 때
                showPoiDialog(p1!!)
            }
        }
    }

    fun showPoiDialog(p1: NMapPOIitem) {
        val builder = AlertDialog.Builder(mapActivity)
        builder.setTitle("무엇을 하시겠습니까?")
                .setItems(arrayOf("정보조회",
                        "현관비번: ${p1.tag.toString().split('§')[2]}",
                        "호실비번: ${p1.tag.toString().split('§')[3]}",
                        "주인번호: ${p1.tag.toString().split('§')[1]}",
                        "좌표수정",
                        "건물명과 주소 복사",
                        "취소"), { _, i ->
                    if (i in 0..2) {
                        val tag: String = p1!!.tag as String
                        MapSingleton.baseIdx = tag.split('§')[0]
                        mapActivity?.mapVolley?.getAnAsset(MapSingleton.baseIdx, 0)
                    } else if (i == 3) {
                        val phoneNo = p1.tag.toString().split('§')[1]
                        if (phoneNo != "없음") {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:" + phoneNo.replace("-", ""))
                            mapActivity?.startActivity(intent)
                        }
                    } else if (i == 4) {
                        mapActivity?.mapInputInterface?.toggleSpotButtonsVisibility(View.GONE)
                        val tag: String = p1!!.tag as String
                        MapSingleton.baseIdx = tag.split('§')[0]
                        mapActivity?.mapInputInterface?.setRelocationingAsset(true)
                    } else if (i == 5) {
                        val tag: String = p1!!.tag as String
                        MapSingleton.baseIdx = tag.split('§')[0]
                        mapActivity?.mapVolley?.getAnAsset(MapSingleton.baseIdx, 1)
                    }
                }).show()
    }

    fun copyToClipboard(jo: JSONObject) {
        val toastText = "${jo["bld_name"]} : ${jo["plat_plc"]}"
        val clipBoard: ClipboardManager = mapActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("BuildingNameAndAddress", toastText)
        clipBoard.primaryClip = clipData

        Toast.makeText(mapActivity!!, "복사되었습니다 → $toastText", Toast.LENGTH_LONG).show()
    }

    fun clearOverlay() {
        if (assetsPoiDataOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(assetsPoiDataOverlay)
    }
}