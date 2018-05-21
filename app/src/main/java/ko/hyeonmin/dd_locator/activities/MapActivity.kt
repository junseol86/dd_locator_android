package ko.hyeonmin.dd_locator.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.nhn.android.maps.*
import com.nhn.android.maps.maplib.NGeoPoint
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay
import com.nhn.android.mapviewer.overlay.NMapOverlayManager
import com.nhn.android.mapviewer.overlay.NMapResourceProvider
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.naver_bldMap_tools.Asset
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.naver_map_tools.extensions.*
import ko.hyeonmin.dd_locator.naver_map_tools.required.MyMapResourceProvider
import ko.hyeonmin.dd_locator.naver_map_tools.required.MyOnMapStateChangeListener
import ko.hyeonmin.dd_locator.naver_map_tools.required.MyOnMapViewTouchListener
import ko.hyeonmin.dd_locator.utils.Caches
import ko.hyeonmin.dd_locator.utils.Secrets
import org.json.JSONObject
import java.lang.System.exit
import kotlin.concurrent.fixedRateTimer

/**
 * Created by junse on 2017-12-27.
 */
class MapActivity : NMapActivity() {


    var caches: Caches? = null
    var requestQ: RequestQueue? = null

    var spinner: ImageView? = null

    var mapVolley: MapVolley? = null
    var mapLimit: MapLimit? = null
    var mapAssetLoader: MapAssetLoader? = null
    var mapTrace: MapTrace? = null

    var mapView: NMapView? = null
    var resourceProvider: NMapResourceProvider? = null
    var overlayManager: NMapOverlayManager? = null
    var locationManager: NMapLocationManager? = null
    var compassManager: NMapCompassManager? = null
    var locationOverlay: NMapMyLocationOverlay? = null

    var mapAssetPoi: MapAssetPoi? = null
    var mapClusterPoi: MapClusterPoi? = null
    var mapInputInterface: MapInputInterface? = null

    var mapController: NMapController? = null

    var myLocation: NGeoPoint? = null
    var isMyLocationEnabled: Boolean = false

    var buttonPanel: RelativeLayout? = null

    var myLocBtn: ImageView? = null
    var rotateBtn: ImageView? = null
    var followMyLoc: Boolean = false

//    var typeBtns = arrayOfNulls<ImageView>(5)
//    val typeBtnIds: Array<Int> = arrayOf(R.id.typeBtnAll, R.id.typeBtnOne, R.id.typeBtnSg, R.id.typeBtnLnd, R.id.typeBtnAdm)
//    val typeBtnOns: Array<Int> = arrayOf(R.drawable.asset_type_all_on, R.drawable.asset_type_one_on, R.drawable.asset_type_sg_on, R.drawable.asset_type_lnd_on, R.drawable.asset_type_adm_on)
//    val typeBtnOffs: Array<Int> = arrayOf(R.drawable.asset_type_all_off, R.drawable.asset_type_one_off, R.drawable.asset_type_sg_off, R.drawable.asset_type_lnd_off, R.drawable.asset_type_adm_off)
//    var assetTypes: Array<String> = arrayOf("all", "one", "sg", "lnd", "hs")
//    var assetTypeNames: Array<String> = arrayOf("전체", "원룸", "상가", "토지", "주택")

    var showBalloonBtn: ImageView? = null
    var showBalloon: Boolean = true

    var mapFilter: MapFilter? = null
    var filterBtn: ImageView? = null

    var logoutBtn: ImageView? = null
    var zoomLevel: Int = 12

    // 줌이 높지만 매물이 너무 많아 클러스터로 보여주고 있는지 여부
    var showingClusteredAssets = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_map)

        caches = Caches(this)
        requestQ = Volley.newRequestQueue(this)
        resourceProvider = MyMapResourceProvider(this)

        spinner = findViewById(R.id.loadingSpinner)

        mapVolley = MapVolley(this)
        mapLimit = MapLimit(this)
        mapAssetLoader = MapAssetLoader(this)
        mapAssetPoi = MapAssetPoi(this)
        mapClusterPoi = MapClusterPoi(this)
        mapInputInterface = MapInputInterface(this)
        mapTrace = MapTrace(this)

        mapView = findViewById(R.id.mapView)
        val clientId = mapView?.setClientId(Secrets.naverMapClientId)

        buttonPanel = findViewById(R.id.button_panel)

        overlayManager = NMapOverlayManager(this, mapView, resourceProvider)

        locationManager = NMapLocationManager(this)
        setOnMyLocationChangeListener()
        compassManager = NMapCompassManager(this)
        compassManager?.enableCompass()

        locationOverlay = overlayManager?.createMyLocationOverlay(locationManager, compassManager)
        overlayManager?.addOverlay(locationOverlay)

        mapController = mapView?.mapController

        mapView?.isClickable = true
        mapView?.isEnabled = true
        mapView?.isFocusable = true
        mapView?.isFocusableInTouchMode = true
        mapView?.requestFocus()
        mapView?.setScalingFactor(2.5f, true)
        mapView?.setOnMapViewTouchEventListener(MyOnMapViewTouchListener)
        mapView?.setOnMapStateChangeListener(MyOnMapStateChangeListener)

        mapController?.zoomLevel = 12
        if (caches!!.longitude != "") {
            mapController?.mapCenter = NGeoPoint(caches!!.longitude.toDouble(), caches!!.latitude.toDouble())
        }

        myLocBtn = findViewById(R.id.myLocBtn)
        myLocBtn?.setOnClickListener {
            toggleMyLocation()
        }
        rotateBtn = findViewById(R.id.rotateBtn)
        rotateBtn?.setOnClickListener {
            toggleRotation(!(mapView?.isAutoRotateEnabled)!!)
        }

//        for (i in 0..4) {
//            typeBtns[i] = findViewById(typeBtnIds[i])
//            typeBtns[i]?.setOnClickListener {
//                if (i < 4) {
//                    MapSingleton.assetTypeIndex = i
//                    mapAssetLoader?.loadIfNeeded(true)
//                    Toast.makeText(this, "${assetTypeNames[i]} 매물", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this, "준비중인 기능입니다.", Toast.LENGTH_SHORT).show()
//                }
//                for (j in 0..4) {
//                    if (i == j)
//                        typeBtns[j]?.setImageResource(typeBtnOns[j])
//                    else
//                        typeBtns[j]?.setImageResource(typeBtnOffs[j])
//                }
//            }
//        }

        showBalloonBtn = findViewById(R.id.showBaloonBtn)
        showBalloonBtn?.setOnClickListener {
            showBalloon = !showBalloon
            showBalloonBtn?.setImageResource(if (showBalloon) R.drawable.balloon_on else R.drawable.balloon_off)
            clearDataOverlays()
            mapAssetLoader!!.loadIfNeeded(true)
        }

        mapFilter = MapFilter(this)
        filterBtn = findViewById(R.id.filterBtn)
        filterBtn?.setOnClickListener({
            mapFilter?.filterOnOff(true)
        })

        logoutBtn = findViewById(R.id.logoutBtn)
        logoutBtn?.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage("로그아웃하시겠습니까?")
                    .setPositiveButton("예") { _, _ ->
                        caches?.logout()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                    .setNegativeButton("아니오") { _, _ -> }
                    .show()
        }

        mapAssetLoader?.loadIfNeeded(forced = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            mapInputInterface?.clearInputOverlays()
            mapController?.mapCenter = NGeoPoint(MapSingleton.asset!!.bldMapX.toDouble(), MapSingleton.asset!!.bldMapY.toDouble())
            mapAssetLoader?.loadIfNeeded(true)
        }
    }

    override fun onBackPressed() {
        if (mapFilter!!.filterCl.visibility == View.VISIBLE) {
            mapFilter?.filterOnOff(false)
            return
        }

        if (mapInputInterface!!.isSpotButtonsOn() || mapInputInterface!!.relocatingAsset) {
            mapInputInterface?.onBackPressed()
            return
        }
        AlertDialog.Builder(this)
                .setTitle("종료하시겠습니까?")
                .setPositiveButton("예", {_, _ -> exit(0)})
                .setNegativeButton("아니오", {_, _ -> })
                .show()

//        super.onBackPressed()
    }

    fun goToBilUp() {
        startActivityForResult(Intent(this, BilUpActivity::class.java), 0)
    }

    private fun isMyLocationOn(): Boolean {
        return locationManager!!.isMyLocationEnabled
    }

    private fun isRotationOn(): Boolean {
        return mapView!!.isAutoRotateEnabled
    }

    private fun toggleMyLocation() {
        if (PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (Build.VERSION.SDK_INT > 22) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        } else {
            if (!isMyLocationOn()) {
                isMyLocationEnabled = locationManager?.enableMyLocation(true)!!
                if (!isMyLocationEnabled) {
                    Toast.makeText(this, "환경설정에서 '내 좌표'를 활성화하세요.",
                            Toast.LENGTH_LONG).show()
                    val goToSettings = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(goToSettings)
                } else {
                    getMyLocation()
                    myLocBtn?.setImageResource(R.drawable.my_loc_on)
                    mapTrace?.toggleTrace(true)
                    followMyLoc = true
                    val rotation: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                    myLocBtn?.startAnimation(rotation)
                    rotateBtn?.alpha = 1f
                    mapTrace?.traceBtn?.alpha = 1f
                }
            } else {
                if (followMyLoc) {
                    followMyLoc = false
                    myLocBtn?.setImageResource(R.drawable.my_loc_subon)
                } else {
                    locationManager?.disableMyLocation()
                    myLocBtn?.clearAnimation()
                    myLocBtn?.setImageResource(R.drawable.my_loc_off)
                    toggleRotation(false)
                    mapTrace?.toggleTrace(false)
                    rotateBtn?.alpha = 0.5f
                    mapTrace?.traceBtn?.alpha = 0.5f
                }
            }
        }
    }

    private fun toggleRotation(enabled: Boolean) {
        if (isMyLocationEnabled) {
            mapView?.setAutoRotateEnabled(enabled, true)
            rotateBtn?.setImageResource(if (isRotationOn()) R.drawable.rotate_on else R.drawable.rotate_off)
        }
    }

    private fun getMyLocation() {
        val handler = Handler()
        fixedRateTimer(name = "readMyLocation", initialDelay = 100, period = 100) {
            myLocation = locationManager?.myLocation
            if (myLocation != null) {
                mapController?.zoomLevel = 12
                handler.post {
                    myLocBtn?.clearAnimation()
                }
                this.cancel()
            }
        }
    }

    private fun setOnMyLocationChangeListener() {
        locationManager?.setOnLocationChangeListener(
                object : NMapLocationManager.OnLocationChangeListener {
                    override fun onLocationChanged(p0: NMapLocationManager?, p1: NGeoPoint?): Boolean {
                        if (followMyLoc) {
                            caches?.latitude = p1!!.latitude.toString()
                            caches?.longitude = p1.longitude.toString()
                            mapController?.mapCenter = p1
                        }
                        mapTrace?.uploadTrace(p1!!)
                        return true
                    }
                    override fun onLocationUpdateTimeout(p0: NMapLocationManager?) {
                    }
                    override fun onLocationUnavailableArea(p0: NMapLocationManager?, p1: NGeoPoint?) {
                    }
                }
        )
    }

    fun onTouchOnArea(event: MotionEvent?) {
        toggleRotation(false)
        mapInputInterface?.toggleSpotButtonsVisibility(View.GONE)

        if (mapController!!.zoomLevel <= 10) {
            mapController!!.mapCenter = mapView?.mapProjection?.fromPixels(event!!.x.toInt(), event.y.toInt())
            mapController!!.zoomLevel = 12
            return
        }

//        손가락으로 선택한 좌표
        val touchedPoint = mapView?.mapProjection?.fromPixels(event!!.x.toInt(), event.y.toInt())
        mapController?.mapCenter = touchedPoint
        mapController?.zoomLevel = 14

        mapInputInterface?.onTouchArea(touchedPoint!!)
//        해당 좌표에 대한 주소 정보 받기
        mapVolley?.coordsToAddress(touchedPoint!!)
    }

    fun afterGetAnAsset(assetJo: JSONObject) {
        MapSingleton.asset = Asset(
                assetJo["bld_idx"].toString(),
                assetJo["bld_type"].toString(),
                assetJo["plat_plc"].toString(),
                assetJo["new_plat_plc"].toString(),
                assetJo["main_purps"].toString(),
                assetJo["etc_purps"].toString(),
                assetJo["grnd_flr_cnt"].toString(),
                assetJo["useapr_day"].toString(),
                assetJo["fmly_cnt"].toString(),
                assetJo["bld_name"].toString(),
                assetJo["bld_fmly_cnt"].toString(),
                assetJo["bld_memo"].toString(),
                assetJo["bld_ipkey"].toString(),
                assetJo["bld_roomkey"].toString(),
                assetJo["bld_gwan"].toString(),
                assetJo["bld_tel_owner"].toString(),
                assetJo["bld_tel_gwan"].toString(),
                assetJo["bld_map_x"].toString(),
                assetJo["bld_map_y"].toString(),
                assetJo["bld_on_wall"].toString(),
                assetJo["bld_on_parked"].toString(),
                assetJo["work_requested"].toString(),
                assetJo["visited"].toString(),
                assetJo["factory_count"].toString(),
                assetJo["photo"].toString()
        )
        goToBilUp()
    }

    fun clearDataOverlays() {
        mapAssetPoi?.clearOverlay()
        mapClusterPoi?.clearOverlay()
    }

    fun showLoader() {
        runOnUiThread {
            spinner?.visibility = View.VISIBLE
            val rotation: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
            spinner?.startAnimation(rotation)
        }
    }

    fun hideLoader() {
        runOnUiThread {
            spinner?.visibility = View.GONE
            spinner?.clearAnimation()
        }
    }

}
