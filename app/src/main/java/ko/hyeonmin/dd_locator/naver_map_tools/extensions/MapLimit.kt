package ko.hyeonmin.dd_locator.naver_map_tools.extensions

import ko.hyeonmin.dd_locator.activities.MapActivity

/**
 * Created by Hyeonmin on 2017-08-07.
 */
class MapLimit(val ma: MapActivity) {

    var limitTop = 0
    var limitBottom = 0
    var limitLeft = 0
    var limitRight = 0

    fun setLimits() {
//        상, 하, 좌, 우 각각 화면 크기 만큼의 여유를 두고 영역 설정 (가로·세로가 각각 3배, 총 영역이 보이는 화면의 9배가 되도록)
        val rect = ma!!.mapView!!.mapProjection!!.screenBoundsE6
        limitTop = rect.top + (rect.top - rect.bottom)/2
        limitBottom = rect.bottom - (rect.top - rect.bottom)/2
        limitLeft = rect.left - (rect.right - rect.left)
        limitRight = rect.right + (rect.right - rect.left)
    }

    fun isOffLimit(): Boolean {
        val rect = ma!!.mapView!!.mapProjection!!.screenBoundsE6
        return rect.top > limitTop || rect.bottom < limitBottom || rect.left < limitLeft || rect.right > limitRight
    }

}