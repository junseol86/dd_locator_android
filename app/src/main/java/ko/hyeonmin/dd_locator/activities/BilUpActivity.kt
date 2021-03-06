package ko.hyeonmin.dd_locator.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Base64
import android.util.Log
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
import ko.hyeonmin.dd_locator.bil_up_bools.Phonenum
import ko.hyeonmin.dd_locator.naver_map_tools.MapSingleton
import ko.hyeonmin.dd_locator.utils.Consts
import ko.hyeonmin.dd_locator.utils.Secrets
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

/**
 * Created by junse on 2017-12-28.
 */
class BilUpActivity: Activity() {

    var rq: RequestQueue? = null
//    val assetTypes: Array<String> = arrayOf("one", "sg", "lnd", "hs", "")
    val tel2Types: Array<String> = arrayOf("G", "J", "B")

    var address: TextView? = null
    var coords: TextView? = null
    var purps: TextView? = null
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

    var phonenum: Phonenum? = null

    var telOwnerEdit: EditText? = null
    var telOwnerCheck: Button? = null
    var telGwanEdit: EditText? = null
    var telGwanCheck: Button? = null
    var ipkeyEditLl: LinearLayout? = null
    var ipkeyEdit: EditText? = null
    var roomkeyEditLl: LinearLayout? = null
    var roomkeyEdit: EditText? = null
    var fmlyCntEditLl: LinearLayout? = null
    var fmlyCntEdit: EditText? = null
    var onWallTv: TextView? = null
    var onWallEdit: EditText? = null
    var onParkedTv: TextView? = null
    var onParkedEdit: EditText? = null
    var uploadBtn: Button? = null
    var modifyDeleteLl: LinearLayout? = null
    var requestBtn: Button? = null
    var modifyBtn: Button? = null
    var deleteBtn: Button? = null
    var workRequested: LinearLayout? = null

    var uploadPhotoBtn: Button? = null
    var hasPhotoLl: LinearLayout? = null
    var seePhotoBtn: Button? = null
    var updatePhotoBtn: Button? = null
    var removePhotoBtn: Button? = null

    var visitedCbLl: LinearLayout? = null
    var visitedCb: CheckBox? = null
    
    var factoryCountLl: LinearLayout? = null
    var factoryCount: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_bil_up)
        val activity = this

        rq = Volley.newRequestQueue(this)

        address = findViewById(R.id.address)
        coords = findViewById(R.id.coords)
        purps = findViewById(R.id.purps)

        address?.text = "${MapSingleton.asset!!.platPlc}\n${MapSingleton.asset!!.newPlatPlc}"
        coords?.text = "좌표: " + MapSingleton.asset!!.bldMapX + ", " + MapSingleton.asset!!.bldMapY
        coords?.text = "주용도 | 기타용도: " + MapSingleton.asset!!.mainPurps + " | " + MapSingleton.asset!!.etcPurps

        assetTypeSpinner = findViewById(R.id.asset_type_spinner)
//        assetTypeAdpater = ArrayAdapter.createFromResource(this, R.array.asset_type, R.layout.support_simple_spinner_dropdown_item)
        var bldTypeArray = Array(Consts.bldTypeOpt.size, { i -> Consts.bldTypeOpt[i].first})
        assetTypeAdpater = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, bldTypeArray)
        assetTypeSpinner?.adapter = assetTypeAdpater
        assetTypeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                assetTypeIndex = p2
                when (Consts.bldTypeOpt[p2].second) {
                    "one" -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.one_name, R.layout.support_simple_spinner_dropdown_item)
                        bldNameSpinner?.adapter = bldNameAdpater
                    }
                    "sg" -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.sg_name, R.layout.support_simple_spinner_dropdown_item)
                        bldNameSpinner?.adapter = bldNameAdpater
                    }
                    "lnd" -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.lnd_name, R.layout.support_simple_spinner_dropdown_item)
                        bldNameSpinner?.adapter = bldNameAdpater
                    }
//                "hs" -> {
//                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.hs_name, R.layout.support_simple_spinner_dropdown_item)
//                    bldNameSpinner?.adapter = bldNameAdpater
//                }
                    else -> {
                        bldNameAdpater = ArrayAdapter.createFromResource(activity, R.array.etc_name, R.layout.support_simple_spinner_dropdown_item)
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

        ipkeyEditLl = findViewById(R.id.ipkey_num_ll)
        ipkeyEdit = findViewById(R.id.ipkey_num)
        ipkeyEditLl?.visibility = if (MapSingleton.asset!!.bldType.contains("ftr") || MapSingleton.asset!!.bldType == "str" || MapSingleton.asset!!.bldType.contains("FACTORY"))
            View.GONE else View.VISIBLE
        roomkeyEditLl = findViewById(R.id.roomkey_num_ll)
        roomkeyEdit = findViewById(R.id.roomkey_num)
        roomkeyEditLl?.visibility = if (MapSingleton.asset!!.bldType.contains("ftr") || MapSingleton.asset!!.bldType == "str" || MapSingleton.asset!!.bldType.contains("FACTORY"))
            View.GONE else View.VISIBLE
        fmlyCntEditLl = findViewById(R.id.fmlyCntEdit_ll)
        fmlyCntEdit = findViewById(R.id.fmlyCntEdit)
        fmlyCntEditLl?.visibility = if (MapSingleton.asset!!.bldType.contains("ftr") || MapSingleton.asset!!.bldType == "str" || MapSingleton.asset!!.bldType.contains("FACTORY"))
            View.GONE else View.VISIBLE

        gwanEdit = findViewById(R.id.gwan_edit)

        phonenum = Phonenum(this)

        telOwnerEdit = findViewById(R.id.tel_owner_edit)
        telOwnerCheck = findViewById(R.id.tel_owner_check)
        telOwnerCheck?.setOnClickListener({
            phonenum?.searchPhoenum(telOwnerEdit!!.text.toString(), true)
        })
        telGwanEdit = findViewById(R.id.tel_gwan_edit)
        telGwanCheck = findViewById(R.id.tel_gwan_check)
        telGwanCheck?.setOnClickListener({
            phonenum?.searchPhoenum(telGwanEdit!!.text.toString(), false)
        })

        onWallTv = findViewById(R.id.on_wall_tv)
        onWallEdit = findViewById(R.id.on_wall_edit)
        onParkedTv = findViewById(R.id.on_parked_tv)
        onParkedEdit = findViewById(R.id.on_parked_edit)

        seePhotoBtn = findViewById(R.id.see_photo_btn)
        seePhotoBtn?.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("${Secrets.photoUrl}${MapSingleton.asset!!.photo}"
                            .replace("\"", "")
                            .replace("'", ""))))
            val clipBoard: ClipboardManager = this?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("seePhotoAddress",
                    "${Secrets.photoUrl}${MapSingleton.asset!!.photo}".replace("\"", "")
                            .replace("'", ""))
            clipBoard.primaryClip = clipData

            Toast.makeText(this!!, "복사되었습니다", Toast.LENGTH_LONG).show()
        })

        uploadPhotoBtn = findViewById(R.id.upload_photo_btn)
        uploadPhotoBtn?.setOnClickListener({
            getPhoto()
        })

        hasPhotoLl = findViewById(R.id.has_photo_ll)
        updatePhotoBtn = findViewById(R.id.update_photo_btn)
        updatePhotoBtn?.setOnClickListener({
            getPhoto()
        })
        removePhotoBtn = findViewById(R.id.remove_photo_btn)
        removePhotoBtn?.setOnClickListener({
            deletePhoto()
        })

        photoResult()

        visitedCbLl = findViewById(R.id.visited_cb_ll)
        visitedCb = findViewById(R.id.visited_cb)
//        visitedCbLl?.visibility = if (MapSingleton.asset!!.bldType.contains("ftr") || MapSingleton.asset!!.bldType == "str")
//            View.VISIBLE else View.GONE

        factoryCountLl = findViewById(R.id.factory_count_ll)
        factoryCount = findViewById(R.id.factory_count)
        factoryCountLl?.visibility = if (MapSingleton.asset!!.bldType.contains("ftr") || MapSingleton.asset!!.bldType == "str" || MapSingleton.asset!!.bldType.contains("FACTORY"))
            View.VISIBLE else View.GONE

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
            for (i in 0 until Consts.bldTypeOpt.size) {
                if (Consts.bldTypeOpt[i].second == MapSingleton.asset!!.bldType) {
                    assetTypeSpinner?.setSelection(i)
                    assetTypeTv?.text = Consts.bldTypeOpt[i].first
                }
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
//                "hs" -> {
//                    bldNameAdpater = ArrayAdapter.createFromResource(this, R.array.hs_name, R.layout.support_simple_spinner_dropdown_item)
//                    bldNameSpinner?.adapter = bldNameAdpater
//                }
                else -> {
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
            fmlyCntEdit?.setText(MapSingleton.asset!!.bldFmlyCnt)


            onWallTv?.text =
                    if (MapSingleton.asset!!.bldType.contains("ftr")
                            || MapSingleton.asset!!.bldType == "str"
                            || MapSingleton.asset!!.bldType.contains("FACTORY"))
                    "현수막:" else "벽:"
            onWallEdit?.setText(MapSingleton.asset!!.bldOnWall)
            onParkedTv?.text =
                    if (MapSingleton.asset!!.bldType.contains("ftr")
                            || MapSingleton.asset!!.bldType == "str"
                            || MapSingleton.asset!!.bldType.contains("FACTORY"))
                        "세입자:" else "주차장:"
            onParkedEdit?.setText(MapSingleton.asset!!.bldOnParked)
            workRequested?.visibility = if (MapSingleton.asset!!.workRequested.trim() == "") View.GONE else View.VISIBLE
            visitedCb?.isChecked = MapSingleton.asset!!.visited == "1"
            factoryCount?.setText(MapSingleton.asset!!.factoryCount)
        }

        if (!MapSingleton.asset!!.insertOrModify && MapSingleton.noPhotoFilter == "1") {
            ipkeyEdit?.isFocusableInTouchMode = true
            ipkeyEdit?.requestFocus()
        }
    }

    var currentPhotoPath = ""
    fun createImgFile(): File {
        var strDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile("temp", ".jpg", strDir)
        currentPhotoPath = image.absolutePath
        return image
    }

    fun getPhoto() {
        var photoFile: File? = null
        try {
            photoFile = createImgFile()
        } catch (e: Exception) {
            println(e)
        }
        if (photoFile != null) {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile))
            startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
//            var imgBitmap: Bitmap = data!!.extras.get("data") as Bitmap

            var imgBitmap = BitmapFactory.decodeStream(FileInputStream(File(currentPhotoPath)))
            var resizedBitmap = Bitmap.createScaledBitmap(imgBitmap, 1024, imgBitmap.height * 1024 / imgBitmap.width, false)
            var output = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            var bytes = output.toByteArray()
            var encoded = Base64.encodeToString(bytes, Base64.DEFAULT)

//            var encoded = Base64.encodeToString(, Base64.DEFAULT)
            uploadPhoto(MapSingleton.asset!!.bldIdx, encoded)
        }
    }

    fun uploadPhoto(bld_idx: String, encoded: String) {
        val uploadRequest: StringRequest = object: StringRequest(Request.Method.POST,
                Secrets.apiUrl + "asset/uploadPhoto",
                Response.Listener {
                    MapSingleton.asset?.photo = it
                    photoResult()
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            override fun getParams(): MutableMap<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["bld_idx"] = bld_idx
                params["image"] = encoded

                return params
            }
        }
        rq?.add(uploadRequest)
    }

    fun deletePhoto() {
        val deleteRequest: StringRequest = object : StringRequest(Request.Method.DELETE,
                Secrets.apiUrl + "asset/deletePhoto",
                Response.Listener {
                    MapSingleton.asset?.photo = it
                    photoResult()
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["bld_idx"] = MapSingleton.baseIdx
                return params
            }
        }
        rq?.add(deleteRequest)
    }

    fun photoResult() {
        uploadPhotoBtn?.visibility = if (MapSingleton.asset!!.insertOrModify || MapSingleton.asset!!.photo.length > 3) View.GONE else View.VISIBLE
        hasPhotoLl?.visibility = if (MapSingleton.asset!!.insertOrModify || MapSingleton.asset!!.photo.length <= 3) View.GONE else View.VISIBLE
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
                params["bld_map_x"] = MapSingleton.asset!!.bldMapX
                params["bld_map_y"] = MapSingleton.asset!!.bldMapY
                params["plat_plc"] = MapSingleton.asset!!.platPlc
                params["new_plat_plc"] = MapSingleton.asset!!.newPlatPlc
                params["bld_type"] = Consts.bldTypeOpt[assetTypeIndex].second
                params["bld_name"] = if (bldNameIndex == 0) if (bldNameEdit!!.text.toString() == "") "(수동입력)" else bldNameEdit!!.text.toString()
                else  {
                    when (assetTypeIndex) {
                        0 -> resources.getStringArray(R.array.one_name)[bldNameIndex]
                        1 -> resources.getStringArray(R.array.sg_name)[bldNameIndex]
                        else -> resources.getStringArray(R.array.lnd_name)[bldNameIndex]
                    }
                }
                params["bld_fmly_cnt"] = fmlyCntEdit!!.text.toString()
                params["bld_memo"] = memoEdit!!.text.toString()
                params["bld_gwan"] = gwanEdit!!.text.toString()
                params["bld_tel_owner"] = telOwnerEdit!!.text.toString()
                params["bld_tel_gwan"] = telGwanEdit!!.text.toString()
                params["bld_ipkey"] = ipkeyEdit!!.text.toString()
                params["bld_roomkey"] = roomkeyEdit!!.text.toString()
                params["bld_on_wall"] = onWallEdit!!.text.toString()
                params["bld_on_parked"] = onParkedEdit!!.text.toString()
                params["work_requested"] = "false"
                return params
            }
        }
        rq?.add(insertRequest)
    }

    fun modifyAsset(request: Boolean) {
        val modifyRequest: StringRequest = object: StringRequest(Request.Method.PUT,
                Secrets.apiUrl + "asset/modify_S2",
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
                params["bld_map_x"] = MapSingleton.asset!!.bldMapX
                params["bld_map_y"] = MapSingleton.asset!!.bldMapY
                params["plat_plc"] = MapSingleton.asset!!.platPlc
                params["new_plat_plc"] = MapSingleton.asset!!.newPlatPlc
                params["bld_type"] = Consts.bldTypeOpt[assetTypeIndex].second
                params["bld_name"] = if (bldNameIndex == 0) if (bldNameEdit!!.text.toString() == "") "(수동입력)" else bldNameEdit!!.text.toString()
                else  {
                    when (assetTypeIndex) {
                        0 -> resources.getStringArray(R.array.one_name)[bldNameIndex]
                        1 -> resources.getStringArray(R.array.sg_name)[bldNameIndex]
                        else -> resources.getStringArray(R.array.lnd_name)[bldNameIndex]
                    }
                }
                params["bld_memo"] = memoEdit!!.text.toString()
                params["bld_gwan"] = gwanEdit!!.text.toString()
                params["bld_tel_owner"] = telOwnerEdit!!.text.toString()
                params["bld_tel_gwan"] = telGwanEdit!!.text.toString()
                params["bld_ipkey"] = ipkeyEdit!!.text.toString()
                params["bld_roomkey"] = roomkeyEdit!!.text.toString()
                params["bld_fmly_cnt"] = fmlyCntEdit!!.text.toString()
                params["bld_on_wall"] = onWallEdit!!.text.toString()
                params["bld_on_parked"] = onParkedEdit!!.text.toString()
                params["visited"] = if (visitedCb!!.isChecked) "1" else "0"
                params["factory_count"] = factoryCount!!.text.toString()
                params["work_requested"] = if (request) "true" else "false"
                println(params)
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