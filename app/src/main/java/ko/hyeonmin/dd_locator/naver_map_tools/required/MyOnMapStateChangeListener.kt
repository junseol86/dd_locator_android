package ko.hyeonmin.dd_locator.naver_map_tools.required

import com.nhn.android.maps.NMapView
import com.nhn.android.maps.maplib.NGeoPoint
import com.nhn.android.maps.nmapmodel.NMapError
import ko.hyeonmin.dd_locator.activities.MapActivity

/**
 * Created by Hyeonmin on 2017-06-27.
 */
object MyOnMapStateChangeListener : NMapView.OnMapStateChangeListener {

    override fun onMapInitHandler(mapView: NMapView, errorInfo: NMapError?) {
        if (errorInfo == null) { // success
            // restore map view state such as map center position and zoom level.
            (mapView.context as MapActivity).mapAssetLoader?.loadIfNeeded(false)
        } else { // fail
        }
    }
    override fun onAnimationStateChange(mapView: NMapView, animType: Int, animState: Int) {
    }
    override fun onMapCenterChange(mapView: NMapView, center: NGeoPoint) {
        (mapView.context as MapActivity).caches?.latitude = center.latitude.toString()
        (mapView.context as MapActivity).caches?.longitude = center.longitude.toString()
        (mapView.context as MapActivity).mapAssetLoader?.loadIfNeeded(false)
    }
    override fun onZoomLevelChange(mapView: NMapView, level: Int) {
        (mapView.context as MapActivity).mapAssetLoader?.loadIfNeeded(((mapView.context as MapActivity).zoomLevel > 10 && level <= 10) || (mapView.context as MapActivity).zoomLevel <= 10 && level > 10 || (mapView.context as MapActivity).showingClusteredAssets)
        (mapView.context as MapActivity).zoomLevel = level
    }
    override fun onMapCenterChangeFine(mapView: NMapView) {
    }
}