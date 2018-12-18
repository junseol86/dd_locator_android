package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.activities.MapActivity
import ko.hyeonmin.dd_locator.utils.Consts

/**
 * Created by junse on 2018-01-25.
 */
class MapFilter(val ma: MapActivity) {
    var bldCtgr = "0000"
    var bldType = ma.caches!!.bldType
    var hasName = 0
    var hasNumber = 0
    var hasGwan = 0
    var fmlyMin = -1
    var fmlyMax = -1
    var mainPurps = ""
    var useaprDay = ""
    var visited = -1
    var factory_count = -1 // -1: 모름, 0: 없음, 1: 있음
//    var floor_min = 0 // 0: 무관, 1~: #층 이상

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

    val bldCtgrCb1: CheckBox = ma.findViewById(R.id.bldCtgrCb1)
    val bldCtgrCb2: CheckBox = ma.findViewById(R.id.bldCtgrCb2)
    val bldCtgrCb3: CheckBox = ma.findViewById(R.id.bldCtgrCb3)
    val bldCtgrCb4: CheckBox = ma.findViewById(R.id.bldCtgrCb4)

    val bldTypeSpinner: Spinner = ma.findViewById(R.id.bldType)
    val bldTypeAdapter = ArrayAdapter(ma, android.R.layout.simple_spinner_dropdown_item, Consts.bldTypeOptForFilter.map {it.first})

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

    val floorMinEt: EditText = ma.findViewById(R.id.flrMinEt)

    val filterApplyBtn: Button = ma.findViewById(R.id.filterApplyBtn)
    val filterClearBtn: Button = ma.findViewById(R.id.filterClearBtn)

    val view: View = ma.currentFocus
    val imm: InputMethodManager = ma.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    init {
        bldCtgrCb1.setOnCheckedChangeListener { _, p1 ->
            val zeroOne = if (p1) "1" else "0"
            bldCtgr = "" + zeroOne + bldCtgr[1] + bldCtgr[2] + bldCtgr[3]
        }
        bldCtgrCb2.setOnCheckedChangeListener { _, p1 ->
            val zeroOne = if (p1) "1" else "0"
            bldCtgr = "" + bldCtgr[0] + zeroOne +  bldCtgr[2] + bldCtgr[3]
        }
        bldCtgrCb3.setOnCheckedChangeListener { _, p1 ->
            val zeroOne = if (p1) "1" else "0"
            bldCtgr = "" + bldCtgr[0] + bldCtgr[1] + zeroOne + bldCtgr[3]
        }
        bldCtgrCb4.setOnCheckedChangeListener { _, p1 ->
            val zeroOne = if (p1) "1" else "0"
            bldCtgr = "" + bldCtgr[0] + bldCtgr[1] + bldCtgr[2] + zeroOne
        }

        bldTypeSpinner.adapter = bldTypeAdapter
        bldTypeSpinner.setSelection(0)
        bldTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                bldTypeTemp = Consts.bldTypeOptForFilter[p2].second
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
        bldCtgrCb1.isChecked = false
        bldCtgrCb2.isChecked = false
        bldCtgrCb3.isChecked = false
        bldCtgrCb4.isChecked = false
        bldCtgr = "0000"

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

        floorMinEt.setText("0")

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
        ma.caches?.bldType = bldType

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