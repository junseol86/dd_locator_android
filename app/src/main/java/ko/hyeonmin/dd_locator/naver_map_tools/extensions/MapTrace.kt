package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.ImageView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.nhn.android.maps.maplib.NGeoPoint
import com.nhn.android.maps.overlay.NMapPathData
import com.nhn.android.maps.overlay.NMapPathLineStyle
import com.nhn.android.mapviewer.overlay.NMapPathDataOverlay
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.utils.Secrets
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by Hyeonmin on 2017-08-07.
 */

class MapTrace {
    val MARK_DISTANCE = 0.00025
    val LINK_DISTANCE = 0.002

    var mapActivity: MapActivity? = null
    var requestQ: RequestQueue? = null

    var traceBtn: ImageView? = null
    var showTraceBtn: ImageView? = null
    var selectDateBtn: ImageView? = null
    var traceOn: Boolean = false
    var traceShowing: Boolean = true

    var longitude: Double = 0.0
    var latitude: Double = 0.0

    val traceList: ArrayList<Trace> = ArrayList()
    var tracePrevCount: Int = 0
    private val traceOverlayList: ArrayList<NMapPathDataOverlay> = ArrayList()
    private val tracePathDataList: ArrayList<NMapPathData> = ArrayList()

    var traceCurrentOverlay: NMapPathDataOverlay? = null
    var traceCurrentPathData: NMapPathData? = null

    var yearFrom: Int = 0
    var monthFrom: Int = 0
    var dayFrom: Int = 0
    var yearTo: Int = 0
    var monthTo: Int = 0
    var dayTo: Int = 0

    constructor(ma: MapActivity) {
        mapActivity = ma
        requestQ = ma.requestQ!!

        setDateToToday()
        setDateFrom(0)

        traceBtn = mapActivity?.findViewById(R.id.traceBtn) as ImageView
        traceBtn?.setOnClickListener {
            toggleTrace(!traceOn)
        }

        showTraceBtn = mapActivity?.findViewById(R.id.showTraceBtn) as ImageView
        showTraceBtn?.setOnClickListener {
            toggleShowTrace()
        }

        selectDateBtn = mapActivity?.findViewById(R.id.selectDateBtn) as ImageView
        selectDateBtn?.setOnClickListener {
            selectDate()
        }
    }

    class Trace(val traceIdx: Int, val userId: String, val longitude: Double, val latitude: Double, val datetime: String)

    fun uploadTrace(point: NGeoPoint) {
        if (!traceOn) {
            return
        }

        if (longitude == 0.0 || distanceCheck(longitude, latitude, point.longitude, point.latitude) > MARK_DISTANCE) {
            longitude = point.longitude
            latitude = point.latitude

            val insertRequest: StringRequest = object: StringRequest(Request.Method.POST,
                    Secrets.apiUrl + "trace/insert",
                    Response.Listener {
                        drawAfterUpload(it)
                    },
                    Response.ErrorListener {
                        println("ERROR")
                    }
            ) {
                override fun getBodyContentType(): String {
                    return "application/x-www-form-urlencoded; charset=UTF-8"
                }

                override fun getParams(): MutableMap<String, String> {
                    var params: MutableMap<String, String> = HashMap<String, String>()
                    params.put("trace_idx", "AUTO_INC")
                    params.put("user_id", mapActivity!!.caches!!.id)
                    params.put("longitude", longitude.toString())
                    params.put("latitude", latitude.toString())
                    params.put("datetime", "DEFAULT")
                    return params
                }
            }
            requestQ?.add(insertRequest)
        }
    }

    fun drawAfterUpload(idx: String) {
        val traceUrl = Secrets.apiUrl + "trace/single"
        val traceRequest: StringRequest = object: StringRequest(Request.Method.GET, traceUrl,
                Response.Listener<String> {
                    val jo = JSONObject(it)
                    val trace = Trace(
                            jo["trace_idx"].toString().toInt(), jo["user_id"].toString(),
                            jo["longitude"].toString().toDouble(), jo["latitude"].toString().toDouble(),
                            jo["datetime"].toString()
                    )
                    traceList.add(trace)
                    drawAdditionalTrace()
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("trace_idx", idx)
                return params
            }
        }
        requestQ?.add(traceRequest)

    }

    fun getTrace() {
        for (h in traceOverlayList) {
            mapActivity?.overlayManager?.removeOverlay(h)
        }
        tracePathDataList.removeAll(tracePathDataList)
        traceOverlayList.removeAll(traceOverlayList)
        if (traceCurrentOverlay != null) {
            mapActivity?.overlayManager?.removeOverlay(traceCurrentOverlay)
        }

        if (!traceShowing) {
            return
        }

        if (mapActivity!!.mapController!!.zoomLevel > 0 ) {
            val traceListUrl = Secrets.apiUrl + "trace"
            val traceRequest: StringRequest = object: StringRequest(Request.Method.GET, traceListUrl,
                    Response.Listener<String> {
                        var ja = JSONArray(it)
                        traceList.removeAll(traceList)
                        for (i in 0..ja.length() - 1) {
                            val jo:JSONObject = ja[i] as JSONObject
                            traceList.add(Trace(jo["trace_idx"].toString().toInt(), jo["user_id"].toString(),
                                    jo["longitude"].toString().toDouble(), jo["latitude"].toString().toDouble(),
                                    jo["datetime"].toString()
                                    ))
                        }
                        tracePrevCount = traceList.size
                        drawTrace()
                    },
                    Response.ErrorListener {
                        println("ERROR")
                    }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("user_id", mapActivity!!.caches!!.id)
                    params.put("top", (mapActivity!!.mapLimit!!.limitTop.toDouble() / 1000000).toString())
                    params.put("bottom", (mapActivity!!.mapLimit!!.limitBottom.toDouble() / 1000000).toString())
                    params.put("left", (mapActivity!!.mapLimit!!.limitLeft.toDouble() / 1000000).toString())
                    params.put("right", (mapActivity!!.mapLimit!!.limitRight.toDouble() / 1000000).toString())
                    params.put("date_from", "$yearFrom-${monthFrom + 1}-$dayFrom")
                    params.put("date_to", "$yearTo-${monthTo + 1}-$dayTo 23:59:59")

                    return params
                }
            }
            requestQ?.add(traceRequest)
        } else {

        }
    }

    fun toggleTrace(enabled: Boolean) {
        if (mapActivity!!.isMyLocationEnabled) {
            traceOn = enabled
            traceBtn?.setImageResource(if (traceOn) R.drawable.trace_on else R.drawable.trace_off)
        }
    }

    private fun toggleShowTrace() {
        traceShowing = !traceShowing
        showTraceBtn?.setImageResource(if (traceShowing) R.drawable.show_trace_on else R.drawable.show_trace_off)
        selectDateBtn?.alpha = if (traceShowing) 1.0f else 0.3f
        mapActivity?.mapAssetLoader?.loadIfNeeded(true)
    }

    private fun selectDate() {
        if (traceShowing) {
            var builder = AlertDialog.Builder(mapActivity)
            builder.setTitle("어느 기간 내의 트레이스를 보시겠습니까?")
                    .setItems(arrayOf("한달", "일주일", "어제부터", "오늘만", "전체", "직접 지정"), { _, i ->
                        if (i == 5) {
                            selectDateFromPicker()
                        } else {
                            setDateToToday()
                            setDateFrom(i)
                            mapActivity?.mapAssetLoader?.loadIfNeeded(true)
                        }
                    })
                    .show()
        }
    }
    
    private fun selectDateFromPicker() {
        DatePickerDialog(mapActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            yearFrom = year
            monthFrom = month
            dayFrom = day
            selectDateToPicker()
        }, yearFrom, monthFrom, dayFrom).show()
    }

    private fun selectDateToPicker() {
        DatePickerDialog(mapActivity, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            yearTo = year
            monthTo = month
            dayTo = day
            mapActivity?.mapAssetLoader?.loadIfNeeded(true)
        }, yearTo, monthTo, dayTo).show()
    }

    private fun setDateToToday() {
        val today = Calendar.getInstance()
        yearTo = today.get(Calendar.YEAR)
        monthTo = today.get(Calendar.MONTH)
        dayTo = today.get(Calendar.DATE)
    }

    private fun setDateFrom(i: Int) {
        var fromDate = Calendar.getInstance()
        when (i) {
            0 -> fromDate.add(Calendar.MONTH, -1)
            1 -> fromDate.add(Calendar.DATE, -7)
            2 -> fromDate.add(Calendar.DATE, -1)
            3 -> fromDate.add(Calendar.DATE, 0)
            4 -> fromDate.add(Calendar.YEAR, -10)
        }

        yearFrom = fromDate.get(Calendar.YEAR)
        monthFrom = fromDate.get(Calendar.MONTH)
        dayFrom = fromDate.get(Calendar.DATE)
    }

    private fun distanceCheck(firstLong: Double, firstLat: Double, secondLong: Double, secondLat: Double)
            = Math.sqrt( Math.pow((firstLong - secondLong), 2.0) + Math.pow((firstLat - secondLat), 2.0))

    fun drawTrace() {
        var prevLongitude = 0.0
        var prevLatitude = 0.0
        var prevAdminId = ""
        var prevDay = ""
        for (i in traceList) {
            if (traceList.indexOf(i) == 0
                    || i.userId != prevAdminId
                    || i.datetime.substring(0, 10) != prevDay
                    || distanceCheck(prevLongitude, prevLatitude, i.longitude, i.latitude) > (if (mapActivity!!.mapController!!.zoomLevel < 12) Math.pow(2.0, 12.0 - mapActivity!!.mapController!!.zoomLevel) else 1.0) * LINK_DISTANCE) {
                if (!tracePathDataList.isEmpty()) {
                    tracePathDataList.last().endPathData()
                }
                tracePathDataList.add(NMapPathData(0))
                tracePathDataList.last().initPathData()
            }
            prevAdminId = i.userId
            prevDay = i.datetime.substring(0, 10)

            prevLongitude = i.longitude
            prevLatitude = i.latitude
            tracePathDataList.last().addPathPoint(i.longitude, i.latitude, NMapPathLineStyle.TYPE_SOLID)
        }
        if (!tracePathDataList.isEmpty()) {
            tracePathDataList.last().endPathData()
        }
        for (j in tracePathDataList) {
            traceOverlayList.add(mapActivity!!.overlayManager!!.createPathDataOverlay(j))
        }
        for (k in traceOverlayList) {
            k.setLineColor(2864755, 150)
        }
    }

    fun drawAdditionalTrace() {
        if (!traceShowing) {
            return
        }
        if (traceCurrentOverlay != null) {
            mapActivity?.overlayManager?.removeOverlay(traceCurrentOverlay)
        }
        traceCurrentPathData = NMapPathData(0)
        traceCurrentPathData?.initPathData()
        var firstDash = true
        for (i in (Math.max(tracePrevCount - 1, 0))..traceList.size - 1) {
            traceCurrentPathData?.addPathPoint(traceList[i].longitude, traceList[i].latitude, if (firstDash) NMapPathLineStyle.DATA_TYPE_POLYGON else NMapPathLineStyle.TYPE_SOLID)
            firstDash = false
        }
        traceCurrentPathData?.endPathData()
        traceCurrentOverlay = mapActivity?.overlayManager?.createPathDataOverlay(traceCurrentPathData)
        traceCurrentOverlay?.setLineColor(16732328, 150)
    }
}