package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.activities.MapActivity

/**
 * Created by junse on 2018-01-25.
 */
class MapFilter(val ma: MapActivity) {
    var bldType = "one"
    var hasName = 0
    var hasNumber = 0
    var hasGwan = 0
    var fmlyMin = -1
    var fmlyMax = -1
    var mainPurps = ""
    var useaprDay = ""

    var bldTypeOpt = arrayOf(
            Pair("전체", "all"),
            Pair("아파트", "apt"),
            Pair("연립·다세대", "yd"),
            Pair("오피스텔", "ot"),
            Pair("단독", "dd"),
            Pair("다가구", "one"),
            Pair("상업·업무용", "su"),
            Pair("토지", "lnd"),
            Pair("분양·입주권", "bi")
    )

    var hasNameOpt = arrayOf(
            Pair("이름무관", 0),
            Pair("이름없음", -1),
            Pair("이름있음", 1)
    )

    val hasNumberOpt = arrayOf(
            Pair("번호무관", 0),
            Pair("번호없음", -1),
            Pair("번호있음", 1)
    )

    val hasGwanOpt = arrayOf(
            Pair("업체무관", 0),
            Pair("업체없음", -1),
            Pair("업체있음", 1)
    )

    var bldTypeTemp = "one"
    var hasNameTemp = 0
    var hasNumberTemp = 0
    var hasGwanTemp = 0

    val bldTypeSpinner: Spinner = ma.findViewById(R.id.bldType)
    val bldTypeAdapter = ArrayAdapter(ma, android.R.layout.simple_spinner_dropdown_item, bldTypeOpt.map {it.first})

    val hasNameSpinner: Spinner = ma.findViewById(R.id.hasName)
    val hasNameAdapter = ArrayAdapter(ma, android.R.layout.simple_spinner_dropdown_item, hasNameOpt.map {it.first})

    val hasNumberSpinner: Spinner = ma.findViewById(R.id.hasNumber)
    val hasNumberAdapter = ArrayAdapter(ma, android.R.layout.simple_spinner_dropdown_item, hasNumberOpt.map {it.first})

    val hasGwanSpinner: Spinner = ma.findViewById(R.id.hasGwan)
    val hasGwanAdapter = ArrayAdapter(ma, android.R.layout.simple_spinner_dropdown_item, hasGwanOpt.map {it.first})

    val fmlyMinEt: EditText = ma.findViewById(R.id.fmlMinEt)
    val fmlyMaxEt: EditText = ma.findViewById(R.id.fmlMaxEt)
    val mainPurpsEt: EditText = ma.findViewById(R.id.mainPurpsEt)
    val useaprDayEt: EditText = ma.findViewById(R.id.useaprDayEt)

    val filterApplyBtn: Button = ma.findViewById(R.id.filterApplyBtn)
    val filterClearBtn: Button = ma.findViewById(R.id.filterClearBtn)

    val view: View = ma.currentFocus
    val imm: InputMethodManager = ma.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    init {
        bldTypeSpinner.adapter = bldTypeAdapter
        bldTypeSpinner.setSelection(5)
        bldTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                bldTypeTemp = bldTypeOpt[p2].second
            }
        }
        hasNameSpinner.adapter = hasNameAdapter
        hasNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                hasNameTemp = hasNameOpt[p2].second
            }
        }
        hasNumberSpinner.adapter = hasNumberAdapter
        hasNumberSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                hasNumberTemp = hasNumberOpt[p2].second
            }
        }
        hasGwanSpinner.adapter = hasGwanAdapter
        hasGwanSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                hasGwanTemp = hasGwanOpt[p2].second
            }
        }

        filterApplyBtn.setOnClickListener({
            apply()
        })

        filterClearBtn.setOnClickListener({
            clear()
        })
    }

    val filterCl: ConstraintLayout = ma.findViewById(R.id.filterCl)

    fun filterOnOff(onOff: Boolean) {
        filterCl.visibility = if (onOff) View.VISIBLE else View.GONE
    }



    fun clear() {
        bldTypeSpinner.setSelection(5)
        hasNameSpinner.setSelection(0)
        hasNumberSpinner.setSelection(0)
        hasGwanSpinner.setSelection(0)

        bldTypeTemp = "one"
        hasNameTemp = 0
        hasNumberTemp = 0
        hasGwanTemp = 0

        fmlyMinEt.setText("")
        fmlyMaxEt.setText("")
        mainPurpsEt.setText("")
        useaprDayEt.setText("")

        apply()
    }

    fun isClear():Boolean =
        bldType == "one" &&
        hasName == 0 &&
        hasNumber == 0 &&
        hasGwan == 0 &&
        fmlyMin == -1 &&
        fmlyMax == -1 &&
        mainPurps == "" &&
        useaprDay == ""

    fun apply() {
        bldType = bldTypeTemp
        hasName = hasNameTemp
        hasNumber = hasNumberTemp
        hasGwan = hasGwanTemp

        fmlyMin = if (fmlyMinEt.text.toString() == "") -1 else Math.max(-1, fmlyMinEt.text.toString().toInt())
        fmlyMax = if (fmlyMaxEt.text.toString() == "") -1 else Math.max(-1, fmlyMaxEt.text.toString().toInt())
        mainPurps = mainPurpsEt.text.toString()
        useaprDay = useaprDayEt.text.toString()

        Log.d("CLEAR?", "$hasName $hasNumber $hasGwan $fmlyMin $fmlyMax $mainPurps $useaprDay")

        ma.filterBtn?.setImageResource(if (isClear()) R.drawable.filter_off else R.drawable.filter_on)
        filterOnOff(false)
        ma.mapAssetLoader?.loadIfNeeded(true)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}