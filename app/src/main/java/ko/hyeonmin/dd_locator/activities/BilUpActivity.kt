package ko.hyeonmin.dd_locator.activities

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.utils.Secrets

/**
 * Created by junse on 2017-12-28.
 */
class BilUpActivity: Activity() {

    var rq: RequestQueue? = null
    val assetTypes: Array<String> = arrayOf("one", "sg", "lnd", "hs", "")
    val tel2Types: Array<String> = arrayOf("G", "J", "B")

    var address: TextView? = null
    var coords: TextView? = null
    var assetTypeSpinner: Spinner? = null
    var assetTypeTv: TextView? = null
    var assetTypeAdpater: ArrayAdapter<CharSequence>? = null
    var assetTypeIndex: Int = 0
    var bldNameEdit: EditText? = null
    var bldNameSpinner: Spinner? = null
    var bldNameAdpater: ArrayAdapter<CharSequence>? = null
    var bldNameIndex: Int = 0
    var memoEdit: EditText? = null
    var gwanEdit: EditText? = null
    var telOwnerEdit: EditText? = null
    var telGwanEdit: EditText? = null
    var ipkeyEdit: EditText? = null
    var roomkeyEdit: EditText? = null
    var onWallEdit: EditText? = null
    var onParkedEdit: EditText? = null
    var uploadBtn: Button? = null
    var modifyDeleteLl: LinearLayout? = null
    var requestBtn: Button? = null
    var modifyBtn: Button? = null
    var deleteBtn: Button? = null
    var workRequested: LinearLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_bil_up)
        val activity = this

        rq = Volley.newRequestQueue(this)

        address = findViewById(R.id.address)
        coords = findViewById(R.id.coords)

        address?.text = "${MapSingleton.asset!!.platPlc}\n${MapSingleton.asset!!.newPlatPlc}"
        coords?.text = "좌표: " + MapSingleton.asset!!.bldMapX + ", " + MapSingleton.asset!!.bldMapY

        assetTypeSpinner = findViewById(R.id.asset_type_spinner)
        assetTypeAdpater = ArrayAdapter.createFromResource(this, R.array.asset_type, R.layout.support_simple_spinner_dropdown_item)
        assetTypeSpinner?.adapter = assetTypeAdpater
        assetTypeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                assetTypeIndex = p2
                when (p2) {
                    0 -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.one_name, R.layout.support_simple_spinner_dropdown_item)
                        bldNameSpinner?.adapter = bldNameAdpater
                    }
                    1 -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.sg_name, R.layout.support_simple_spinner_dropdown_item)
                        bldNameSpinner?.adapter = bldNameAdpater
                    }
                    else -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.lnd_name, R.layout.support_simple_spinner_dropdown_item)
                        bldNameSpinner?.adapter = bldNameAdpater
                    }
                }
            }
        }
//         All일때는 원룸, 나머지는 필터설정 그대로
        assetTypeSpinner?.setSelection(if (MapSingleton.assetTypeIndex == 0) 0 else MapSingleton.assetTypeIndex - 1)
//        assetTypeSpinner?.visibility = if (MapSingleton.asset!!.insertOrModify) View.VISIBLE else View.GONE

        assetTypeTv = findViewById(R.id.asset_type_tv)
        assetTypeTv?.visibility = if (!MapSingleton.asset!!.insertOrModify) View.VISIBLE else View.GONE

        bldNameEdit = findViewById(R.id.building_name_edit)
        bldNameSpinner = findViewById(R.id.building_name_spinner)
        bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.one_name, R.layout.support_simple_spinner_dropdown_item)
        bldNameSpinner?.adapter = bldNameAdpater
        bldNameSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                bldNameEdit?.visibility = if (p2 == 0) View.VISIBLE else View.GONE
                bldNameIndex = p2
            }
        }

        memoEdit = findViewById(R.id.memo_edit)

        ipkeyEdit = findViewById(R.id.ipkey_num)
        roomkeyEdit = findViewById(R.id.roomkey_num)

        gwanEdit = findViewById(R.id.gwan_edit)
        telGwanEdit = findViewById(R.id.tel_gwan_edit)
        telOwnerEdit = findViewById(R.id.tel_owner_edit)


        onWallEdit = findViewById(R.id.on_wall_edit)
        onParkedEdit = findViewById(R.id.on_parked_edit)

        workRequested = findViewById(R.id.workRequested)

        uploadBtn = findViewById(R.id.upload_btn)
        uploadBtn?.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> uploadBtn?.setBackgroundResource(R.drawable.all_rounded_dark_blue)
                MotionEvent.ACTION_UP ->  {
                    uploadBtn?.setBackgroundResource(R.drawable.all_rounded_blue)
                    insertAsset()
                }
            }
            false
        }

        modifyDeleteLl = findViewById(R.id.modify_delete_ll)

        uploadBtn?.visibility = if (MapSingleton.asset!!.insertOrModify) View.VISIBLE else View.GONE
        modifyDeleteLl?.visibility = if (!MapSingleton.asset!!.insertOrModify) View.VISIBLE else View.GONE

        modifyBtn = findViewById(R.id.modify_btn)
        modifyBtn?.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> modifyBtn?.setBackgroundResource(R.drawable.all_rounded_dark_blue)
                MotionEvent.ACTION_UP ->  {
                    modifyBtn?.setBackgroundResource(R.drawable.all_rounded_blue)
                    modifyAsset(false)
                }
            }
            false
        }

        requestBtn = findViewById(R.id.request_btn)
        requestBtn?.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> modifyBtn?.setBackgroundResource(R.drawable.all_rounded_dark_blue)
                MotionEvent.ACTION_UP ->  {
                    modifyBtn?.setBackgroundResource(R.drawable.all_rounded_blue)
                    modifyAsset(true)
                }
            }
            false
        }

        deleteBtn = findViewById(R.id.delete_btn)
        deleteBtn?.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> deleteBtn?.setBackgroundResource(R.drawable.all_rounded_dark_red)
                MotionEvent.ACTION_UP ->  {
                    deleteBtn?.setBackgroundResource(R.drawable.all_rounded_red)
                    var dialog = AlertDialog.Builder(this)
                    var context = this
                    dialog.setMessage("[1차 확인]삭제하시겠습니까?")
                            .setPositiveButton("예") { _, _ ->
                                var dialog2 = AlertDialog.Builder(context)
                                dialog2.setMessage("[2차 확인]삭제하시겠습니까?")
                                        .setPositiveButton("예") { _, _ -> deleteAsset() }
                                        .setNegativeButton("아니오") { _, _ -> }
                                        .show()
                            }
                            .setNegativeButton("아니오") { _, _ -> }
                            .show()
                }
            }
            false
        }

        if (!MapSingleton.asset!!.insertOrModify) {
            when (MapSingleton.asset!!.bldType) {
                "one" -> assetTypeSpinner?.setSelection(0)
                "sg" -> assetTypeSpinner?.setSelection(1)
                "lnd" -> assetTypeSpinner?.setSelection(2)
                "hs" -> assetTypeSpinner?.setSelection(3)
                "" -> assetTypeSpinner?.setSelection(4)
            }
            when (MapSingleton.asset!!.bldType) {
                "one" -> assetTypeTv?.text = "원룸"
                "sg" -> assetTypeTv?.text = "상가"
                "lnd" -> assetTypeTv?.text = "토지"
                "hs" -> assetTypeTv?.text = "주택"
                "" -> assetTypeTv?.text = "미분류"
            }
            when (MapSingleton.asset!!.bldType) {
                "one" -> {
                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.one_name, R.layout.support_simple_spinner_dropdown_item)
                    bldNameSpinner?.adapter = bldNameAdpater
                }
                "sg" -> {
                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.sg_name, R.layout.support_simple_spinner_dropdown_item)
                    bldNameSpinner?.adapter = bldNameAdpater
                }
                "lnd" -> {
                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.lnd_name, R.layout.support_simple_spinner_dropdown_item)
                    bldNameSpinner?.adapter = bldNameAdpater
                }
                "hs" -> {
                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.hs_name, R.layout.support_simple_spinner_dropdown_item)
                    bldNameSpinner?.adapter = bldNameAdpater
                }
                "" -> {
                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.etc_name, R.layout.support_simple_spinner_dropdown_item)
                    bldNameSpinner?.adapter = bldNameAdpater
                }
            }
            bldNameEdit?.setText(MapSingleton.asset!!.bldName)
            memoEdit?.setText(MapSingleton.asset!!.bldMemo)
            gwanEdit?.setText(MapSingleton.asset!!.bldGwan)
            telOwnerEdit?.setText(MapSingleton.asset!!.bldTelOwner)
            telGwanEdit?.setText(MapSingleton.asset!!.bldTelGwan)
            ipkeyEdit?.setText(MapSingleton.asset!!.bldIpkey)
            roomkeyEdit?.setText(MapSingleton.asset!!.bldRoomkey)
            onWallEdit?.setText(MapSingleton.asset!!.bldOnWall)
            onParkedEdit?.setText(MapSingleton.asset!!.bldOnParked)
            workRequested?.visibility = if (MapSingleton.asset!!.workRequested.trim() == "") View.GONE else View.VISIBLE
        }

        if (!MapSingleton.asset!!.insertOrModify && MapSingleton.noPhotoFilter == "1") {
            ipkeyEdit?.isFocusableInTouchMode = true
            ipkeyEdit?.requestFocus()
        }
    }

    fun insertAsset() {
        val insertRequest: StringRequest = object: StringRequest(Request.Method.POST,
                Secrets.apiUrl + "asset/insertV2",
                Response.Listener {
                    finish()
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
                params.put("bld_map_x", MapSingleton.asset!!.bldMapX)
                params.put("bld_map_y", MapSingleton.asset!!.bldMapY)
                params.put("plat_plc", MapSingleton.asset!!.platPlc)
                params.put("new_plat_plc", MapSingleton.asset!!.newPlatPlc)
                params.put("bld_type", assetTypes[assetTypeIndex])
                params.put("bld_name",
                        if (bldNameIndex == 0) if (bldNameEdit!!.text.toString() == "") "(수동입력)" else bldNameEdit!!.text.toString()
                        else  {
                            when (assetTypeIndex) {
                                0 -> resources.getStringArray(R.array.one_name)[bldNameIndex]
                                1 -> resources.getStringArray(R.array.sg_name)[bldNameIndex]
                                else -> resources.getStringArray(R.array.lnd_name)[bldNameIndex]
                            }
                        })
                params.put("bld_memo", memoEdit!!.text.toString())
                params.put("bld_gwan", gwanEdit!!.text.toString())
                params.put("bld_tel_owner", telOwnerEdit!!.text.toString())
                params.put("bld_tel_gwan", telGwanEdit!!.text.toString())
                params.put("bld_ipkey", ipkeyEdit!!.text.toString())
                params.put("bld_roomkey", roomkeyEdit!!.text.toString())
                params.put("bld_on_wall", onWallEdit!!.text.toString())
                params.put("bld_on_parked", onParkedEdit!!.text.toString())
                params.put("work_requested", "false")
                return params
            }
        }
        rq?.add(insertRequest)
    }

    fun modifyAsset(request: Boolean) {
        val modifyRequest: StringRequest = object: StringRequest(Request.Method.PUT,
                Secrets.apiUrl + "asset/modifyV2",
                Response.Listener {
                    finish()
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("bld_idx", MapSingleton.baseIdx)
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
                params.put("new_plat_plc", MapSingleton.asset!!.newPlatPlc)
                params.put("bld_type", assetTypes[assetTypeIndex])
                params.put("bld_name",
                        if (bldNameIndex == 0) if (bldNameEdit!!.text.toString() == "") "(수동입력)" else bldNameEdit!!.text.toString()
                        else  {
                            when (assetTypeIndex) {
                                0 -> resources.getStringArray(R.array.one_name)[bldNameIndex]
                                1 -> resources.getStringArray(R.array.sg_name)[bldNameIndex]
                                else -> resources.getStringArray(R.array.lnd_name)[bldNameIndex]
                            }
                        })
                params.put("bld_memo", memoEdit!!.text.toString())
                params.put("bld_gwan", gwanEdit!!.text.toString())
                params.put("bld_tel_owner", telOwnerEdit!!.text.toString())
                params.put("bld_tel_gwan", telGwanEdit!!.text.toString())
                params.put("bld_ipkey", ipkeyEdit!!.text.toString())
                params.put("bld_roomkey", roomkeyEdit!!.text.toString())
                params.put("bld_on_wall", onWallEdit!!.text.toString())
                params.put("bld_on_parked", onParkedEdit!!.text.toString())
                params.put("work_requested", if (request) "true" else "false")
                return params
            }
        }
        rq?.add(modifyRequest)
    }

    fun deleteAsset() {
        val deleteRequest: StringRequest = object : StringRequest(Request.Method.DELETE,
                Secrets.apiUrl + "asset/deleteV2",
                Response.Listener {
                    finish()
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("bld_idx", MapSingleton.baseIdx)
                return params
            }
        }
        rq?.add(deleteRequest)
    }

}