package ko.hyeonmin.dd_locator.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

/**
 * Created by hyeonmin on 2016-05-25.
 */

class Caches (activity : Activity) {

    var sharedPrefs : SharedPreferences = activity.getSharedPreferences("account", Context.MODE_PRIVATE)

    var loggedIn : Boolean
        get() = sharedPrefs.getBoolean("loggedIn", false)
        set(value) {
            sharedPrefs.edit().putBoolean("loggedIn", value).commit()
        }

    var id: String
        get() = sharedPrefs.getString("id", "")
        set(string) {
            sharedPrefs.edit().putString("id", string).commit()
        }

    var password: String
        get() = sharedPrefs.getString("password", "")
        set(string) {
            sharedPrefs.edit().putString("password", string).commit()
        }


    var latitude: String
        get() = sharedPrefs.getString("latitude", "")
        set(value) {
            sharedPrefs.edit().putString("latitude", value).commit()
        }

    var longitude: String
        get() = sharedPrefs.getString("longitude", "")
        set(value) {
            sharedPrefs.edit().putString("longitude", value).commit()
        }

    var bldType: String
        get() = sharedPrefs.getString("bldType", "all")
        set(value) {
            sharedPrefs.edit().putString("bldType", value).commit()
        }

    fun logout() {
        loggedIn = false
        id = ""
        password = ""
    }
}
