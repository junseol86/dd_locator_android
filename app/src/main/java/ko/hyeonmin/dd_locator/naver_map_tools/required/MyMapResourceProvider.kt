package ko.hyeonmin.dd_locator.naver_map_tools.required

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ListView

import com.nhn.android.maps.NMapOverlayItem
import com.nhn.android.maps.overlay.NMapPOIitem
import com.nhn.android.mapviewer.overlay.NMapResourceProvider
import ko.hyeonmin.dd_locator.R
import ko.hyeonmin.dd_locator.naver_map_tools.NMapCalloutCustomOldOverlay

/**
 * Wrapper class to provider resources on map view.

 * @author kyjkim
 */
class MyMapResourceProvider(context: Context) : NMapResourceProvider(context), NMapCalloutCustomOldOverlay.ResourceProvider {

	private val mTempRect = Rect()
	private val mTextPaint = Paint()

	init {

		mTextPaint.isAntiAlias = true
	}

	/**
	 * Get drawable for markerId at focused state

	 * @param markerId unique id for POI or Number icons.
	 * *
	 * @param focused true for focused state, false otherwise.
	 * *
	 * @return
	 */
	fun getDrawable(markerId: Int, focused: Boolean, item: NMapOverlayItem): Drawable? {
		var marker: Drawable? = null

		var resourceId = findResourceIdForMarker(markerId, focused)
		if (resourceId > 0) {
			marker = mContext.resources.getDrawable(resourceId)
		} else {
			resourceId = 4 * markerId
			if (focused) {
				resourceId += 1
			}

			marker = getDrawableForMarker(markerId, focused, item)
		}

		// set bounds
		if (marker != null) {
			setBounds(marker, markerId, item)
		}

		return marker
	}

	fun getBitmap(markerId: Int, focused: Boolean, item: NMapOverlayItem): Bitmap {
		var bitmap: Bitmap? = null

		val marker = getDrawable(markerId, focused, item)
		if (marker != null) {
			bitmap = getBitmap(marker)
		}

		return bitmap!!
	}

	fun getBitmap(marker: Drawable?): Bitmap {
		var bitmap: Bitmap? = null

		if (marker != null) {
			val width = marker.intrinsicWidth
			val height = marker.intrinsicHeight
			bitmap = Bitmap.createBitmap(width, height, BITMAP_CONFIG_DEFAULT)

			marker.setBounds(0, 0, width, height)

			val canvas = Canvas(bitmap!!)
			canvas.drawColor(0x00000000)

			marker.draw(canvas)
		}

		return bitmap!!
	}

	fun getBitmap(resourceId: Int): Bitmap {
		var bitmap: Bitmap? = null

		var marker: Drawable? = null
		if (resourceId > 0) {
			marker = mContext.resources.getDrawable(resourceId)
		}

		if (marker != null) {
			bitmap = getBitmap(marker)
		}

		return bitmap!!
	}

	fun getBitmapWithNumber(resourceId: Int, strNumber: String, offsetY: Float, fontColor: Int, fontSize: Float): Bitmap {
		var bitmap: Bitmap? = null
		val marker = getDrawableWithNumber(resourceId, strNumber, offsetY, fontColor, fontSize)

		if (marker != null) {
			bitmap = getBitmap(marker)
		}

		return bitmap!!
	}

	override fun getDrawableForInfoLayer(item: NMapOverlayItem?): Drawable? {
		return null
	}

	/**
	 * Class to find resource Ids on map view
	 */
	private inner class ResourceIdsOnMap internal constructor(internal var markerId: Int, internal var resourceId: Int, internal var resourceIdFocused: Int)

	// Resource Ids for single icons
	private val mResourceIdsForMarkerOnMap = arrayOf(
			// Spot, Pin icons
			ResourceIdsOnMap(NMapPOIflagType.PIN, R.drawable.ic_pin_01, R.drawable.ic_pin_02), ResourceIdsOnMap(NMapPOIflagType.SPOT, R.drawable.ic_pin_03, R.drawable.ic_pin_04),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W2_GY_SY, R.drawable.asset_jy_w2_gy_sy, R.drawable.asset_jy_w2_gy_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W2_GY_SN, R.drawable.asset_jy_w2_gy_sn, R.drawable.asset_jy_w2_gy_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W2_GN_SY, R.drawable.asset_jy_w2_gn_sy, R.drawable.asset_jy_w2_gn_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W2_GN_SN, R.drawable.asset_jy_w2_gn_sn, R.drawable.asset_jy_w2_gn_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W1_GY_SY, R.drawable.asset_jy_w1_gy_sy, R.drawable.asset_jy_w1_gy_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W1_GY_SN, R.drawable.asset_jy_w1_gy_sn, R.drawable.asset_jy_w1_gy_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W1_GN_SY, R.drawable.asset_jy_w1_gn_sy, R.drawable.asset_jy_w1_gn_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W1_GN_SN, R.drawable.asset_jy_w1_gn_sn, R.drawable.asset_jy_w1_gn_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W0_GY_SY, R.drawable.asset_jy_w0_gy_sy, R.drawable.asset_jy_w0_gy_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W0_GY_SN, R.drawable.asset_jy_w0_gy_sn, R.drawable.asset_jy_w0_gy_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W0_GN_SY, R.drawable.asset_jy_w0_gn_sy, R.drawable.asset_jy_w0_gn_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JY_W0_GN_SN, R.drawable.asset_jy_w0_gn_sn, R.drawable.asset_jy_w0_gn_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W2_GY_SY, R.drawable.asset_jn_w2_gy_sy, R.drawable.asset_jn_w2_gy_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W2_GY_SN, R.drawable.asset_jn_w2_gy_sn, R.drawable.asset_jn_w2_gy_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W2_GN_SY, R.drawable.asset_jn_w2_gn_sy, R.drawable.asset_jn_w2_gn_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W2_GN_SN, R.drawable.asset_jn_w2_gn_sn, R.drawable.asset_jn_w2_gn_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W1_GY_SY, R.drawable.asset_jn_w1_gy_sy, R.drawable.asset_jn_w1_gy_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W1_GY_SN, R.drawable.asset_jn_w1_gy_sn, R.drawable.asset_jn_w1_gy_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W1_GN_SY, R.drawable.asset_jn_w1_gn_sy, R.drawable.asset_jn_w1_gn_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W1_GN_SN, R.drawable.asset_jn_w1_gn_sn, R.drawable.asset_jn_w1_gn_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W0_GY_SY, R.drawable.asset_jn_w0_gy_sy, R.drawable.asset_jn_w0_gy_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W0_GY_SN, R.drawable.asset_jn_w0_gy_sn, R.drawable.asset_jn_w0_gy_sn),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W0_GN_SY, R.drawable.asset_jn_w0_gn_sy, R.drawable.asset_jn_w0_gn_sy),
			ResourceIdsOnMap(NMapPOIflagType.ASSET_JN_W0_GN_SN, R.drawable.asset_jn_w0_gn_sn, R.drawable.asset_jn_w0_gn_sn),

			ResourceIdsOnMap(NMapPOIflagType.FTR_V0_P0_T0, R.drawable.ftr_v0_p0_t0, R.drawable.ftr_v0_p0_t0),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V0_P0_T1, R.drawable.ftr_v0_p0_t1, R.drawable.ftr_v0_p0_t1),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V0_P0_T2, R.drawable.ftr_v0_p0_t2, R.drawable.ftr_v0_p0_t2),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V0_P1_T0, R.drawable.ftr_v0_p1_t0, R.drawable.ftr_v0_p1_t0),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V0_P1_T1, R.drawable.ftr_v0_p1_t1, R.drawable.ftr_v0_p1_t1),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V0_P1_T2, R.drawable.ftr_v0_p1_t2, R.drawable.ftr_v0_p1_t2),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V1_P0_T0, R.drawable.ftr_v1_p0_t0, R.drawable.ftr_v1_p0_t0),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V1_P0_T1, R.drawable.ftr_v1_p0_t1, R.drawable.ftr_v1_p0_t1),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V1_P0_T2, R.drawable.ftr_v1_p0_t2, R.drawable.ftr_v1_p0_t2),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V1_P1_T0, R.drawable.ftr_v1_p1_t0, R.drawable.ftr_v1_p1_t0),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V1_P1_T1, R.drawable.ftr_v1_p1_t1, R.drawable.ftr_v1_p1_t1),
			ResourceIdsOnMap(NMapPOIflagType.FTR_V1_P1_T2, R.drawable.ftr_v1_p1_t2, R.drawable.ftr_v1_p1_t2),

			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_1, R.drawable.cluster_1, R.drawable.cluster_1),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_10, R.drawable.cluster_10, R.drawable.cluster_10),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_20, R.drawable.cluster_20, R.drawable.cluster_20),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_30, R.drawable.cluster_30, R.drawable.cluster_30),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_40, R.drawable.cluster_40, R.drawable.cluster_40),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_50, R.drawable.cluster_50, R.drawable.cluster_50),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_60, R.drawable.cluster_60, R.drawable.cluster_60),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_70, R.drawable.cluster_70, R.drawable.cluster_70),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_80, R.drawable.cluster_80, R.drawable.cluster_80),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_90, R.drawable.cluster_90, R.drawable.cluster_90),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_100, R.drawable.cluster_100, R.drawable.cluster_100),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_150, R.drawable.cluster_150, R.drawable.cluster_150),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_200, R.drawable.cluster_200, R.drawable.cluster_200),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_300, R.drawable.cluster_300, R.drawable.cluster_300),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_400, R.drawable.cluster_400, R.drawable.cluster_400),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_500, R.drawable.cluster_500, R.drawable.cluster_500),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_600, R.drawable.cluster_600, R.drawable.cluster_600),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_700, R.drawable.cluster_700, R.drawable.cluster_700),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_800, R.drawable.cluster_800, R.drawable.cluster_800),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_900, R.drawable.cluster_900, R.drawable.cluster_900),
			ResourceIdsOnMap(NMapPOIflagType.CLUSTER_999, R.drawable.cluster_999, R.drawable.cluster_999),

			// Direction POI icons: From, To
			ResourceIdsOnMap(NMapPOIflagType.FROM, R.drawable.ic_map_start, R.drawable.ic_map_start_over), ResourceIdsOnMap(NMapPOIflagType.TO, R.drawable.ic_map_arrive, R.drawable.ic_map_arrive_over)
	)

	/**
	 * Find resource id corresponding to the markerId.

	 * @param markerId marker id for a NMapPOIitem.
	 * *
	 * @param focused flag to indicated focused or normal state of this marker.
	 * *
	 * *
	 * @return resource id for the given markerId.
	 * *
	 * *
	 * @see NMapPOIflagType
	 */
	override fun findResourceIdForMarker(markerId: Int, focused: Boolean): Int {
		var resourceId = 0

		if (DEBUG) {
//			Log.i(LOG_TAG, "getResourceIdForMarker: markerId=$markerId, focused=$focused")
		}

		if (markerId < NMapPOIflagType.SINGLE_MARKER_END) {
			resourceId = getResourceIdOnMapView(markerId, focused, mResourceIdsForMarkerOnMap)
			if (resourceId > 0) {
				return resourceId
			}
		}

		if (markerId >= NMapPOIflagType.NUMBER_BASE && markerId < NMapPOIflagType.NUMBER_END) { // Direction Number icons

		} else if (markerId >= NMapPOIflagType.CUSTOM_BASE && markerId < NMapPOIflagType.CUSTOM_END) { // Custom POI icons

		}

		return resourceId
	}

	/**
	 * Set bounds for this marker depending on its shape.

	 */
	override fun setBounds(marker: Drawable, markerId: Int, item: NMapOverlayItem?) {

		// check shape of the marker to set bounds correctly.
		if (NMapPOIflagType.isBoundsCentered(markerId)) {
			if (marker.bounds.isEmpty) {
				NMapOverlayItem.boundCenter(marker)
			}

			item?.setAnchorRatio(0.5f, 0.5f)

		} else {
			if (marker.bounds.isEmpty) {
				NMapOverlayItem.boundCenterBottom(marker)
			}

			item?.setAnchorRatio(0.5f, 1.0f)
		}
	}

	override fun getLocationDot(): Array<Drawable> {
		val drawable = arrayOfNulls<Drawable>(2)

		drawable[0] = mContext.resources.getDrawable(R.drawable.pubtrans_ic_mylocation_off)
		drawable[1] = mContext.resources.getDrawable(R.drawable.pubtrans_ic_mylocation_on)

		for (i in drawable.indices) {
			val w = drawable[i]!!.intrinsicWidth / 2
			val h = drawable[i]!!.intrinsicHeight / 2

			drawable[i]!!.setBounds(-w, -h, w, h)
		}

		return drawable as Array<Drawable>
	}

	override fun getDirectionArrow(): Drawable {

		val drawable = mContext.resources.getDrawable(R.drawable.ic_angle)

		if (drawable != null) {
			val w = drawable.intrinsicWidth / 2
			val h = drawable.intrinsicHeight / 2

			drawable.setBounds(-w, -h, w, h)
		}

		return drawable
	}

	fun getDrawableWithNumber(resourceId: Int, strNumber: String, offsetY: Float, fontColor: Int, fontSize: Float): Drawable? {

		val textBitmap = getBitmapWithText(resourceId, strNumber, fontColor, fontSize, offsetY)

		//Log.i(LOG_TAG, "getDrawableWithNumber: width=" + textBitmap.getWidth() + ", height=" + textBitmap.getHeight() + ", density=" + textBitmap.getDensity());

		// set bounds
		val marker = BitmapDrawable(mContext.resources, textBitmap)
		if (marker != null) {
			NMapOverlayItem.boundCenter(marker)
		}

		//Log.i(LOG_TAG, "getDrawableWithNumber: width=" + marker.getIntrinsicWidth() + ", height=" + marker.getIntrinsicHeight());

		return marker
	}

	fun getDrawableWithAlphabet(resourceId: Int, strAlphabet: String, fontColor: Int, fontSize: Float): Drawable {

		val textBitmap = getBitmapWithText(resourceId, strAlphabet, fontColor, fontSize, POI_FONT_OFFSET_ALPHABET)

		// set bounds
		val marker = BitmapDrawable(mContext.resources, textBitmap)
		if (marker != null) {
			NMapOverlayItem.boundCenterBottom(marker)
		}

		return marker
	}

	override fun getDrawableForMarker(markerId: Int, focused: Boolean, item: NMapOverlayItem): Drawable {
		var drawable: Drawable? = null

		if (markerId >= NMapPOIflagType.NUMBER_BASE && markerId < NMapPOIflagType.NUMBER_END) { // Direction Number icons
			val resourceId = if (focused) R.drawable.ic_map_no_02 else R.drawable.ic_map_no_01
			val fontColor = if (focused) POI_FONT_COLOR_ALPHABET else POI_FONT_COLOR_NUMBER

			val strNumber = (markerId - NMapPOIflagType.NUMBER_BASE).toString()

			drawable = getDrawableWithNumber(resourceId, strNumber, 0.0f, fontColor, POI_FONT_SIZE_NUMBER)
		} else if (markerId >= NMapPOIflagType.CUSTOM_BASE && markerId < NMapPOIflagType.CUSTOM_END) { // Custom POI icons

		}

		return drawable!!
	}

	private fun getBitmapWithText(resourceId: Int, strNumber: String, fontColor: Int, fontSize: Float, offsetY: Float): Bitmap {
		var offsetY = offsetY
		val bitmapBackground = BitmapFactory.decodeResource(mContext.resources, resourceId)

		val width = bitmapBackground.width
		val height = bitmapBackground.height
		//Log.i(LOG_TAG, "getBitmapWithText: width=" + width + ", height=" + height + ", density=" + bitmapBackground.getDensity());

		val textBitmap = Bitmap.createBitmap(width, height, BITMAP_CONFIG_DEFAULT)

		val canvas = Canvas(textBitmap)

		canvas.drawBitmap(bitmapBackground, 0f, 0f, null)

		// set font style
		mTextPaint.color = fontColor
		// set font size
		mTextPaint.textSize = fontSize * NMapResourceProvider.mScaleFactor
		// set font type
		if (POI_FONT_TYPEFACE != null) {
			mTextPaint.typeface = POI_FONT_TYPEFACE
		}

		// get text offset
		mTextPaint.getTextBounds(strNumber, 0, strNumber.length, mTempRect)
		val offsetX = ((width - mTempRect.width()) / 2 - mTempRect.left).toFloat()
		if (offsetY == 0.0f) {
			offsetY = ((height - mTempRect.height()) / 2 + mTempRect.height()).toFloat()
		} else {
			offsetY = offsetY * NMapResourceProvider.mScaleFactor + mTempRect.height()
		}

		//Log.i(LOG_TAG, "getBitmapWithText: number=" + number + ", focused=" + focused);
		//Log.i(LOG_TAG, "getBitmapWithText: offsetX=" + offsetX + ", offsetY=" + offsetY + ", boundsWidth=" + mTempRect.width() + ", boundsHeight=" + mTempRect.height());

		// draw text
		canvas.drawText(strNumber, offsetX, offsetY, mTextPaint)

		return textBitmap
	}

	override fun getCalloutBackground(item: NMapOverlayItem): Drawable {

		if (item is NMapPOIitem) {

			if (item.showRightButton()) {
				val drawable = mContext.resources.getDrawable(R.drawable.bg_speech)
				return drawable
			}
		}

		val drawable = mContext.resources.getDrawable(R.drawable.pin_ballon_bg)

		return drawable
	}

	override fun getCalloutRightButtonText(item: NMapOverlayItem): String {
		if (item is NMapPOIitem) {

			if (item.showRightButton()) {
				return "완료"
			}
		}

		return ""
	}

	override fun getCalloutRightButton(item: NMapOverlayItem): Array<Drawable> {
		if (item is NMapPOIitem) {

			if (item.showRightButton()) {
				val drawable = arrayOfNulls<Drawable>(3)

				drawable[0] = mContext.resources.getDrawable(R.drawable.btn_green_normal)
				drawable[1] = mContext.resources.getDrawable(R.drawable.btn_green_pressed)
				drawable[2] = mContext.resources.getDrawable(R.drawable.btn_green_highlight)

				return drawable as Array<Drawable>
			}
		}

		return arrayOf()
	}

	override fun getCalloutRightAccessory(item: NMapOverlayItem): Array<Drawable> {
		if (item is NMapPOIitem) {
			val poiItem = item

			if (poiItem.hasRightAccessory() && poiItem.rightAccessoryId > 0) {
				val drawable = arrayOfNulls<Drawable>(3)

				when (poiItem.rightAccessoryId) {
					NMapPOIflagType.CLICKABLE_ARROW -> {
						drawable[0] = mContext.resources.getDrawable(R.drawable.pin_ballon_arrow)
						drawable[1] = mContext.resources.getDrawable(R.drawable.pin_ballon_on_arrow)
						drawable[2] = mContext.resources.getDrawable(R.drawable.pin_ballon_on_arrow)
					}
				}

				return drawable as Array<Drawable>
			}
		}

		return arrayOf()
	}

	/**
	 * 말풍선의 텍스트 색상을 customize한다.

	 * @param item
	 * *
	 * @return
	 * *
	 * @see com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay.ResourceProvider.getCalloutTextColors
	 */
	override fun getCalloutTextColors(item: NMapOverlayItem): IntArray {
		val colors = IntArray(4)
		colors[0] = CALLOUT_TEXT_COLOR_NORMAL
		colors[1] = CALLOUT_TEXT_COLOR_PRESSED
		colors[2] = CALLOUT_TEXT_COLOR_SELECTED
		colors[3] = CALLOUT_TEXT_COLOR_FOCUSED
		return colors
	}

	override fun getParentLayoutIdForOverlappedListView(): Int {
		// not supported
		return 0
	}

	override fun getOverlappedListViewId(): Int {
		// not supported
		return 0
	}

	override fun getLayoutIdForOverlappedListView(): Int {
		// not supported
		return 0
	}

	override fun getListItemLayoutIdForOverlappedListView(): Int {
		// not supported
		return 0
	}

	override fun getListItemTextViewId(): Int {
		// not supported
		return 0
	}

	override fun getListItemTailTextViewId(): Int {
		// not supported
		return 0
	}

	override fun getListItemImageViewId(): Int {
		// not supported
		return 0
	}

	override fun getListItemDividerId(): Int {
		// not supported
		return 0
	}

	override fun setOverlappedListViewLayout(listView: ListView, itemCount: Int, width: Int, height: Int) {
		// not supported
	}

	override fun setOverlappedItemResource(poiItem: NMapPOIitem, imageView: ImageView) {
		// not supported
	}

	private fun getResourceIdOnMapView(markerId: Int, focused: Boolean, resourceIdsArray: Array<ResourceIdsOnMap>): Int {
		var resourceId = 0

		for (resourceIds in resourceIdsArray) {
			if (resourceIds.markerId == markerId) {
				resourceId = if (focused) resourceIds.resourceIdFocused else resourceIds.resourceId
				break
			}
		}

		return resourceId
	}

	companion object {
		private val LOG_TAG = "NMapViewerResourceProvider"
		private val DEBUG = false

		private val BITMAP_CONFIG_DEFAULT = Bitmap.Config.ARGB_8888

		private val POI_FONT_COLOR_NUMBER = 0xFF909090.toInt()
		private val POI_FONT_SIZE_NUMBER = 10.0f

		private val POI_FONT_COLOR_ALPHABET = 0xFFFFFFFF.toInt()
		private val POI_FONT_OFFSET_ALPHABET = 6.0f
		private val POI_FONT_TYPEFACE: Typeface? = null//Typeface.DEFAULT_BOLD;

		private val CALLOUT_TEXT_COLOR_NORMAL = 0xFFFFFFFF.toInt()
		private val CALLOUT_TEXT_COLOR_PRESSED = 0xFF9CA1AA.toInt()
		private val CALLOUT_TEXT_COLOR_SELECTED = 0xFFFFFFFF.toInt()
		private val CALLOUT_TEXT_COLOR_FOCUSED = 0xFFFFFFFF.toInt()
	}
}