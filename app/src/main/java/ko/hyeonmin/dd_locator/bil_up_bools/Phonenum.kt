package ko.hyeonmin.dd_locator.bil_up_bools

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.widget.EditText
import android.widget.LinearLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ko.hyeonmin.dd_locator.activities.BilUpActivity
import ko.hyeonmin.dd_locator.utils.Secrets
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by junse on 2018-01-29.
 */
class Phonenum(val ba: BilUpActivity) {
    var rq: RequestQueue? = null
    var currentPn = ""
    var org = true
    var numOnly = "0123456789"

    init {
        rq = Volley.newRequestQueue(ba)
    }

    fun filtStr(str: String, containDash: Boolean): String {
        var result = ""
        str.map {
            if ((containDash && it == '-') || numOnly.indexOf(it) > -1)
                result += it
        }
        return result
    }

    fun searchPhoenum(pnNum: String, ownerOrGwan: Boolean) {
        currentPn = pnNum
        org = ownerOrGwan

        val phonenumSearchUrl = Secrets.apiUrl + "phoneNumberList"
        val phonenumSearchRequest: StringRequest = object: StringRequest(Request.Method.GET, phonenumSearchUrl,
                Response.Listener<String> {
                    searchPhonenumResult(JSONArray(it))
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["keyword"] = filtStr(pnNum, false)
                return params
            }
        }
        rq?.add(phonenumSearchRequest)
    }

    fun searchPhonenumResult(pns: JSONArray) {
        var pnsAry = Array<String>(pns.length(), {
            i -> "${(pns[i] as JSONObject)["pn_belong"]}: ${(pns[i] as JSONObject)["pn_number"]}"
        })

        (0 until pns.length()).map {
            if ((pns[it] as JSONObject)["pn_number"] == currentPn) {
                if (org)
                    ba.telOwnerEdit?.setText("")
                else
                    ba.telGwanEdit?.setText("")
            }
        }

        AlertDialog.Builder(ba)
                .setTitle("검색결과: $currentPn")
                .setItems(pnsAry, { _, i ->
                    pickSearched(pns[i] as JSONObject)
                })
                .setPositiveButton("전화걸기", { _, _ ->
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel: $currentPn")

                    if (PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(ba, Manifest.permission.CALL_PHONE)) {
                        if (Build.VERSION.SDK_INT > 22)
                            requestPermissions(ba, arrayOf(Manifest.permission.CALL_PHONE), 1)
                    } else {
                        ba.startActivity(intent)
                        savePhonenumPopup()
                    }
                })
                .setNeutralButton("저장하기", { _, _ ->
                    savePhonenumPopup()
                })
                .setNegativeButton("취소", {_,_->})
                .show()
    }

    fun savePhonenumPopup() {
        val input = EditText(ba)
        input.hint = "이 이름으로 저장"
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        AlertDialog.Builder(ba)
                .setTitle("전화번호를 리스트에 추가하시겠습니까?")
                .setView(input)
                .setPositiveButton("추가하기", { _, _ ->
                    if (input.text.toString().trim() != "") {
                        savePhonenum(input.text.toString())
                    } else {
                        AlertDialog.Builder(ba)
                                .setTitle("업체 이름을 입력하세요.")
                                .setPositiveButton("확인", {_,_->
                                    savePhonenumPopup()
                                })
                                .show()
                    }
                })
                .setNegativeButton("취소", {_,_->})
                .show()
    }

    fun savePhonenum(belong: String) {
        val insertRequest: StringRequest = object: StringRequest(Request.Method.POST,
                Secrets.apiUrl + "phoneNumberInsert",
                Response.Listener {
                    searchPhoenum(currentPn, org)
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
                params["pn_belong"] = belong
                params["pn_number"] = filtStr(currentPn, true)
                return params
            }
        }
        rq?.add(insertRequest)

    }

    fun pickSearched(jo: JSONObject) {
        val ar = if (jo["pn_idx"] == 0) arrayOf("이 번호 선택", "전화걸기") else arrayOf("이 번호로 입력", "전화걸기", "삭제")
        AlertDialog.Builder(ba)
                .setItems(ar, { _, i ->
                    when (i) {
                        0 -> {
                            if (org) {
                                ba.telOwnerEdit?.setText(jo["pn_number"].toString())
                            } else
                                ba.telGwanEdit?.setText(jo["pn_number"].toString())
                        }
                        1 -> {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel: ${jo["pn_number"]}")

                            if (PackageManager.PERMISSION_GRANTED!= ContextCompat.checkSelfPermission(ba, Manifest.permission.CALL_PHONE)) {
                                if (Build.VERSION.SDK_INT > 22)
                                    requestPermissions(ba, arrayOf(Manifest.permission.CALL_PHONE), 1)
                            } else {
                                ba.startActivity(intent)
                            }
                        }
                        2 -> {
                            AlertDialog.Builder(ba)
                                    .setTitle("이 번호를 삭제하시겠습니까?")
                                    .setPositiveButton("예", { _, _ ->
                                        val deleteRequest: StringRequest = object : StringRequest(Request.Method.DELETE,
                                                Secrets.apiUrl + "phoneNumberDelete",
                                                Response.Listener {
                                                    searchPhoenum(currentPn, org)
                                                },
                                                Response.ErrorListener {
                                                    println("ERROR")
                                                }
                                        ) {
                                            override fun getHeaders(): Map<String, String> {
                                                val params = HashMap<String, String>()
                                                params["pn_idx"] = jo["pn_idx"].toString()
                                                return params
                                            }
                                        }
                                        rq?.add(deleteRequest)
                                    })
                                    .setNegativeButton("아니오", {_,_->})
                                    .show()
                        }
                    }
                })
                .show()
    }
}