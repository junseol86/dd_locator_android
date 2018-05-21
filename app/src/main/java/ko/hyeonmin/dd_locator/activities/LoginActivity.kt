package ko.hyeonmin.dd_locator.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.utils.Caches
import ko.hyeonmin.dd_locator.utils.Secrets

/**
 * Created by junse on 2017-12-27.
 */
class LoginActivity: Activity() {

    var caches: Caches? = null
    var versionCode: Int? = null

    var editId: EditText? = null
    var editPw: EditText? = null
    var loginBtn: Button? = null

    var id: String = ""
    var pw: String = ""

    var requestQ: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_login)

        caches = Caches(this)

        editId = findViewById(R.id.editId)
        editPw = findViewById(R.id.editPw)
        loginBtn = findViewById(R.id.loginButton)

        requestQ = Volley.newRequestQueue(this)

        loginBtn?.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> loginBtn?.setBackgroundResource(R.drawable.all_rounded_dark_blue)
                MotionEvent.ACTION_UP ->  {
                    loginBtn?.setBackgroundResource(R.drawable.all_rounded_blue)
                    login(false)
                }
            }
            false
        }

        versionCode = packageManager.getPackageInfo("ko.hyeonmin.dd_locator", 0).versionCode

        if (caches!!.loggedIn)
            login(true)
    }


    private fun login(loggedIn: Boolean) {

        val loginUrl = Secrets.apiUrl + "account"

        id = if (loggedIn) caches!!.id else editId?.text.toString()
        pw = if (loggedIn) caches!!.password else editPw?.text.toString()

        println(loginUrl)

        val loginRequest: StringRequest = object: StringRequest(Request.Method.GET, loginUrl,
                Response.Listener<String> {
                    loginResult(it)
                },
                Response.ErrorListener {
                    println("ERROR")
                }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id
                params["pw"] = pw

                return params
            }
        }
        requestQ?.add(loginRequest)
    }

    fun loginResult(result: String) {
        if (result.toInt() == 0) {
            Toast.makeText(this, "아이디와 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
        } else {
            caches?.id = id
            caches?.password = pw
            caches?.loggedIn = true
            goToMapActivity()
        }
    }

    private fun goToMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish()
    }
}