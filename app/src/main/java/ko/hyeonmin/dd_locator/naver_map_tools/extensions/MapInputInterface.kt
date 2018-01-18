package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.nhn.android.maps.maplib.NGeoPoint
import com.nhn.android.maps.overlay.NMapPOIdata
import com.nhn.android.maps.overlay.NMapPOIitem
import com.nhn.android.maps.overlay.NMapPathData
import com.nhn.android.maps.overlay.NMapPathLineStyle
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.naver_bldMap_tools.Asset
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.naver_map_tools.required.NMapPOIflagType
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Hyeonmin on 2017-08-09.
 */
class MapInputInterface {
    var mapActivity: MapActivity? = null

    var inputPoiData: NMapPOIdata? = null
    var inputPoiDataOverlay: NMapPOIdataOverlay? = null

    var selectedSpot: NMapPOIitem? = null
    var refinedSpot: NMapPOIitem? = null
    var refinedSpotAddresses: JSONArray? = null

    var selectedToRefinedPathData: NMapPathData? = null
    var pathOverlay: NMapPathDataOverlay? = null

    var selectedSpotBtn: Button? = null
    var refinedSpotBtn: Button? = null
    var address1: TextView? = null
    var address2: TextView? = null
    var inputInterface: ConstraintLayout? = null

    var relocatingAsset = false
    var relocatingAssetRl: ConstraintLayout? = null

    constructor(ma: MapActivity) {
        mapActivity = ma

        inputPoiData = NMapPOIdata(1000, mapActivity?.resourceProvider)

        selectedSpotBtn = mapActivity?.findViewById(R.id.selectedSpotBtn) as Button
        refinedSpotBtn = mapActivity?.findViewById(R.id.refinedSpotBtn) as Button
        address1 = mapActivity?.findViewById(R.id.address1) as TextView
        address2 = mapActivity?.findViewById(R.id.address2) as TextView
        inputInterface = mapActivity?.findViewById(R.id.inputInterface) as ConstraintLayout

        selectedSpotBtn?.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> selectedSpotBtn?.setBackgroundResource(R.drawable.all_rounded_dark_blue)
                MotionEvent.ACTION_UP ->  {
                    selectedSpotBtn?.setBackgroundResource(R.drawable.all_rounded_blue)
                    val addr1 = (refinedSpotAddresses!![0] as JSONObject)["address"] as String
                    val addr2 = (refinedSpotAddresses!![1] as JSONObject)["address"] as String
                    MapSingleton.asset = Asset(
                            selectedSpot!!.point.longitude.toString(),
                            selectedSpot!!.point.latitude.toString(),
                            addr1, addr2,
                            mapActivity!!.assetTypes[MapSingleton.assetTypeIndex]
                    )
                    if (relocatingAsset) {
                        mapActivity?.mapVolley?.modifyLocation()
                    } else {
                        mapActivity?.goToBilUp()
                    }
                }
            }
            false
        }
        refinedSpotBtn?.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> refinedSpotBtn?.setBackgroundResource(R.drawable.all_rounded_dark_red)
                MotionEvent.ACTION_UP ->  {
                    refinedSpotBtn?.setBackgroundResource(R.drawable.all_rounded_red)
                    val addr1 = (refinedSpotAddresses!![0] as JSONObject)["address"] as String
                    val addr2 = (refinedSpotAddresses!![1] as JSONObject)["address"] as String
                    MapSingleton.asset = Asset(
                            refinedSpot!!.point.longitude.toString(),
                            refinedSpot!!.point.latitude.toString(),
                            addr1, addr2,
                            mapActivity!!.assetTypes[MapSingleton.assetTypeIndex]
                    )
                    if (relocatingAsset) {
                        mapActivity?.mapVolley?.modifyLocation()
                    } else {
                        mapActivity?.goToBilUp()
                    }
                }
            }
            false
        }

        relocatingAssetRl = mapActivity?.findViewById(R.id.relocatingAssetSign) as ConstraintLayout
    }

    fun onSpotRefined() {
//        선택된 좌표이 속한 주소에 의해 재조정된 좌표
        if (refinedSpotAddresses!!.length() < 2) {
            inputPoiData?.endPOIdata()
            clearInputOverlays()
            return
        }
        val refinedSpotJO: JSONObject = (refinedSpotAddresses!![0] as JSONObject)["point"] as JSONObject
        val refinedPoint = NGeoPoint(refinedSpotJO["x"].toString().toDouble(), refinedSpotJO["y"].toString().toDouble())
        inputPoiData?.beginPOIdata(0)
        val marker = NMapPOIflagType.SPOT
        if (refinedSpot == null) {
            refinedSpot = inputPoiData?.addPOIitem(refinedPoint, "주소상 좌표", marker, null)
        } else {
            refinedSpot?.point = refinedPoint
        }
        inputPoiData?.endPOIdata()
        setInputPoiOverlay()

        address1?.text = ((refinedSpotAddresses!![0] as JSONObject)["address"]).toString()
        address2?.text = ((refinedSpotAddresses!![1] as JSONObject)["address"]).toString()

//        손가락으로 선택한 좌표에서 주소에 의해 재조정된 좌표으로 선 긋기
        selectedToRefinedPathData = NMapPathData(2)
        selectedToRefinedPathData?.initPathData()
        selectedToRefinedPathData?.addPathPoint(selectedSpot!!.point.longitude, selectedSpot!!.point.latitude, NMapPathLineStyle.TYPE_SOLID)
        selectedToRefinedPathData?.addPathPoint(refinedSpot!!.point.longitude, refinedSpot!!.point.latitude, 0)
        selectedToRefinedPathData?.endPathData()
        setPathOverlay()
        toggleSpotButtonsVisibility(View.VISIBLE)
    }


    fun setInputPoiOverlay() {
        if (inputPoiDataOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(inputPoiDataOverlay)
        inputPoiDataOverlay = mapActivity?.overlayManager?.createPOIdataOverlay(inputPoiData, null)
    }

    fun setPathOverlay() {
        if (pathOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(pathOverlay)
        pathOverlay = mapActivity?.overlayManager?.createPathDataOverlay(selectedToRefinedPathData)
    }

    fun clearInputOverlays() {
        if (inputPoiDataOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(inputPoiDataOverlay)
        if (pathOverlay != null)
            mapActivity?.overlayManager?.removeOverlay(pathOverlay)
        toggleSpotButtonsVisibility(View.GONE)
    }


    fun isSpotButtonsOn(): Boolean {
        return inputInterface?.visibility == View.VISIBLE
    }

    fun setRelocationingAsset(onOff: Boolean) {
        relocatingAsset = onOff
        relocatingAssetRl?.visibility = if (relocatingAsset) View.VISIBLE else View.GONE

        mapActivity?.buttonPanel?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE

        mapActivity?.logoutBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE
        mapActivity?.myLocBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE
        mapActivity?.rotateBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE

        mapActivity?.mapTrace?.traceBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE
        mapActivity?.mapTrace?.showTraceBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE
        mapActivity?.mapTrace?.selectDateBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE
        mapActivity?.showBalloonBtn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE

        for (btn in mapActivity!!.typeBtns) {
            btn?.visibility = if (relocatingAsset) View.GONE else View.VISIBLE
        }
    }

    fun toggleSpotButtonsVisibility(visibility: Int) {
        inputInterface?.visibility = visibility
    }

    fun onTouchArea(touchedPoint: NGeoPoint) {
        inputPoiData?.beginPOIdata(0)
        val marker = NMapPOIflagType.PIN
        if (selectedSpot == null) {
            selectedSpot = inputPoiData?.addPOIitem(touchedPoint, "선택한 좌표", marker, null)
        } else {
            selectedSpot?.point = touchedPoint
        }
        inputPoiData?.endPOIdata()
        setInputPoiOverlay()
    }

    fun onBackPressed() {
        clearInputOverlays()
        setRelocationingAsset(false)
    }
}