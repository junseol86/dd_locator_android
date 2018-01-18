package ko.hyeonmin.dd_locator.naver_map_tools.required

import android.view.MotionEvent
import com.nhn.android.maps.NMapView
import ko.hyeonmin.dd_locator.activities.MapActivity

/**
 * Created by Hyeonmin on 2017-06-27.
 */
object MyOnMapViewTouchListener: NMapView.OnMapViewTouchEventListener {

    override fun onLongPressCanceled(p0: NMapView?) {
    }

    override fun onTouchDown(p0: NMapView?, p1: MotionEvent?) {
    }

    override fun onSingleTapUp(mapView: NMapView?, event: MotionEvent?) {
        val context = mapView?.context as MapActivity
        context.onTouchOnArea(event)
    }

    override fun onTouchUp(p0: NMapView?, p1: MotionEvent?) {
    }

    override fun onScroll(p0: NMapView?, p1: MotionEvent?, p2: MotionEvent?) {
    }

    override fun onLongPress(p0: NMapView?, p1: MotionEvent?) {
    }
}