package ko.hyeonmin.dd_locator.naver_map_tools

/**
 * Created by Hyeonmin on 2017-06-27.
 */
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log

import com.nhn.android.maps.NMapOverlay
import com.nhn.android.maps.NMapOverlayItem
import com.nhn.android.maps.NMapView
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay
import com.nhn.android.mapviewer.overlay.NMapResourceProvider

/**
 * Customized callout overlay.

 * @author kyjkim
 */
class NMapCalloutCustomOldOverlay(itemOverlay: NMapOverlay, item: NMapOverlayItem, itemBounds: Rect,
                                  resourceProvider: NMapCalloutCustomOldOverlay.ResourceProvider?) : NMapCalloutOverlay(itemOverlay, item, itemBounds) {

    private val mTextPaint = TextPaint()
    private var mOffsetX: Float = 0.toFloat()
    private var mOffsetY: Float = 0.toFloat()

    private val mMarginX: Float
    private val mPaddingX: Float
    private val mPaddingY: Float
    private val mPaddingOffset: Float
    private val mMinimumWidth: Float
    private val mTotalHeight: Float
    private val mBackgroundHeight: Float
    private val mItemGapY: Float
    private val mTailGapX: Float
    private val mTitleOffsetY: Float

    private val mBackgroundDrawable: Drawable
    protected val mTemp2Rect = Rect()
    private val mRightButtonRect: Rect?
    private val mRightButtonText: String?
    private val mCalloutRightButtonWidth: Int
    private val mCalloutRightButtonHeight: Int
    private var mDrawableRightButton: Array<Drawable>? = null
    private val mCalloutButtonCount = 1

    private var mTitleTruncated: String? = null
    private var mWidthTitleTruncated: Int = 0

    private val mTailText: String?
    private var mTailTextWidth: Float = 0.toFloat()

    /**
     * Resource provider should implement this interface
     */
    interface ResourceProvider {

        fun getCalloutBackground(item: NMapOverlayItem): Drawable

        fun getCalloutRightButtonText(item: NMapOverlayItem): String

        fun getCalloutRightButton(item: NMapOverlayItem): Array<Drawable>

        fun getCalloutRightAccessory(item: NMapOverlayItem): Array<Drawable>
    }

    init {
        mTextPaint.isAntiAlias = true
        // set font style
        mTextPaint.color = CALLOUT_TEXT_COLOR
        // set font size
        mTextPaint.textSize = CALLOUT_TEXT_SIZE * NMapResourceProvider.getScaleFactor()
        // set font type
        if (CALLOUT_TEXT_TYPEFACE != null) {
            mTextPaint.typeface = CALLOUT_TEXT_TYPEFACE
        }

        mMarginX = NMapResourceProvider.toPixelFromDIP(CALLOUT_MARGIN_X).toFloat()
        mPaddingX = NMapResourceProvider.toPixelFromDIP(CALLOUT_PADDING_X).toFloat()
        mPaddingOffset = NMapResourceProvider.toPixelFromDIP(CALLOUT_PADDING_OFFSET).toFloat()
        mPaddingY = NMapResourceProvider.toPixelFromDIP(CALLOUT_PADDING_Y).toFloat()
        mMinimumWidth = NMapResourceProvider.toPixelFromDIP(CALLOUT_MIMIMUM_WIDTH).toFloat()
        mTotalHeight = NMapResourceProvider.toPixelFromDIP(CALLOUT_TOTAL_HEIGHT).toFloat()
        mBackgroundHeight = NMapResourceProvider.toPixelFromDIP(CALLOUT_BACKGROUND_HEIGHT).toFloat()
        mItemGapY = NMapResourceProvider.toPixelFromDIP(CALLOUT_ITEM_GAP_Y).toFloat()

        mTailGapX = NMapResourceProvider.toPixelFromDIP(CALLOUT_TAIL_GAP_X).toFloat()
        mTailText = item.tailText

        mTitleOffsetY = NMapResourceProvider.toPixelFromDIP(CALLOUT_TITLE_OFFSET_Y).toFloat()

        if (resourceProvider == null) {
            throw IllegalArgumentException(
                    "NMapCalloutCustomOverlay.ResourceProvider should be provided on creation of NMapCalloutCustomOverlay.")
        }

        mBackgroundDrawable = resourceProvider.getCalloutBackground(item)

        var hasRightAccessory = false
        mDrawableRightButton = resourceProvider.getCalloutRightAccessory(item)
        if (mDrawableRightButton != null && mDrawableRightButton!!.size > 0) {
            hasRightAccessory = true

            mRightButtonText = null
        } else {
            mDrawableRightButton = resourceProvider.getCalloutRightButton(item)
            mRightButtonText = resourceProvider.getCalloutRightButtonText(item)
        }

        if (mDrawableRightButton != null) {
            if (hasRightAccessory) {
                mCalloutRightButtonWidth = mDrawableRightButton!![0].intrinsicWidth
                mCalloutRightButtonHeight = mDrawableRightButton!![0].intrinsicHeight
            } else {
                mCalloutRightButtonWidth = NMapResourceProvider.toPixelFromDIP(CALLOUT_RIGHT_BUTTON_WIDTH)
                mCalloutRightButtonHeight = NMapResourceProvider.toPixelFromDIP(CALLOUT_RIGHT_BUTTON_HEIGHT)
            }

            mRightButtonRect = Rect()

            super.setItemCount(mCalloutButtonCount)
        } else {
            mCalloutRightButtonWidth = 0
            mCalloutRightButtonHeight = 0
            mRightButtonRect = null
        }

        mTitleTruncated = null
        mWidthTitleTruncated = 0
    }

    override fun hitTest(hitX: Int, hitY: Int): Boolean {

        // hit test for right button only ?
        //    	if (mRightButtonRect != null) {
        //    		return  mRightButtonRect.contains(hitX, hitY);
        //    	}

        return super.hitTest(hitX, hitY)
    }

    override fun isTitleTruncated(): Boolean {
        return mTitleTruncated !== mOverlayItem.title
    }

    override fun getMarginX(): Int {
        return mMarginX.toInt()
    }

    override fun getBounds(mapView: NMapView): Rect {

        adjustTextBounds(mapView)

        mTempRect.set(mTempRectF.left.toInt(), mTempRectF.top.toInt(), mTempRectF.right.toInt(), mTempRectF.bottom.toInt())
        mTempRect.union(mTempPoint.x, mTempPoint.y)

        return mTempRect
    }

    override fun getSclaingPivot(): PointF {
        val pivot = PointF()

        pivot.x = mTempRectF.centerX()
        pivot.y = mTempRectF.top + mTotalHeight

        return pivot
    }

    override fun drawCallout(canvas: Canvas, mapView: NMapView, shadow: Boolean, `when`: Long) {

        adjustTextBounds(mapView)

        stepAnimations(canvas, mapView, `when`)

        drawBackground(canvas)

        var left: Float
        var top: Float

        // draw title
        mOffsetX = (mTempPoint.x - mTempRect.width() / 2).toFloat()
        mOffsetX -= mPaddingOffset
        mOffsetY = mTempRectF.top + mPaddingY + mTextPaint.textSize + mTitleOffsetY
        canvas.drawText(mTitleTruncated!!, mOffsetX, mOffsetY, mTextPaint)

        // draw right button
        if (mDrawableRightButton != null) {
            left = mTempRectF.right - mPaddingX - mCalloutRightButtonWidth.toFloat()
            top = mTempRectF.top + (mBackgroundHeight - mCalloutRightButtonHeight) / 2

            // Use background drawables depends on current state
            mRightButtonRect!!.left = (left + 0.5f).toInt()
            mRightButtonRect.top = (top + 0.5f).toInt()
            mRightButtonRect.right = (left + mCalloutRightButtonWidth.toFloat() + 0.5f).toInt()
            mRightButtonRect.bottom = (top + mCalloutRightButtonHeight.toFloat() + 0.5f).toInt()

            val itemState = super.getItemState(0)
            val drawable = getDrawable(0, itemState)
            if (drawable != null) {
                drawable.bounds = mRightButtonRect
                drawable.draw(canvas)
            }

            if (mRightButtonText != null) {
                mTextPaint.getTextBounds(mRightButtonText, 0, mRightButtonText.length, mTempRect)

                left = (mRightButtonRect.left + (mCalloutRightButtonWidth - mTempRect.width()) / 2).toFloat()
                top = mRightButtonRect.top.toFloat() + ((mCalloutRightButtonHeight - mTempRect.height()) / 2).toFloat() + mTempRect.height().toFloat()
                +mTitleOffsetY
                canvas.drawText(mRightButtonText, left, top, mTextPaint)
            }
        }

        // draw tail text
        if (mTailText != null) {
            if (mRightButtonRect != null) {
                left = mRightButtonRect.left.toFloat()
            } else {
                left = mTempRectF.right
            }
            left -= mPaddingX + mTailTextWidth
            top = mOffsetY

            canvas.drawText(mTailText, left, top, mTextPaint)
        }
    }

    /* Internal Functions */

    private fun drawBackground(canvas: Canvas) {

        mTemp2Rect.left = (mTempRectF.left + 0.5f).toInt()
        mTemp2Rect.top = (mTempRectF.top + 0.5f).toInt()
        mTemp2Rect.right = (mTempRectF.right + 0.5f).toInt()
        mTemp2Rect.bottom = (mTempRectF.top + mTotalHeight + 0.5f).toInt()

        mBackgroundDrawable.bounds = mTemp2Rect
        mBackgroundDrawable.draw(canvas)
    }

    private fun adjustTextBounds(mapView: NMapView) {

        //  First determine the screen coordinates of the selected MapLocation
        mapView.mapProjection.toPixels(mOverlayItem.pointInUtmk, mTempPoint)

        val mapViewWidth = mapView.mapController.viewFrameVisibleWidth
        if (mTitleTruncated == null || mWidthTitleTruncated != mapViewWidth) {
            mWidthTitleTruncated = mapViewWidth
            var maxWidth = mWidthTitleTruncated.toFloat() - 2 * mMarginX - 2 * mPaddingX
            if (DEBUG) {
//                Log.i(LOG_TAG, "adjustTextBounds: maxWidth=" + maxWidth + ", mMarginX=" + mMarginX + ", mPaddingX="
//                        + mPaddingX)
            }

            if (mDrawableRightButton != null) {
                maxWidth -= mPaddingX + mCalloutRightButtonWidth
            }

            if (mTailText != null) {
                mTextPaint.getTextBounds(mTailText, 0, mTailText.length, mTempRect)
                mTailTextWidth = mTempRect.width().toFloat()

                maxWidth -= mTailGapX + mTailTextWidth
            }

            val title = TextUtils.ellipsize(mOverlayItem.title, mTextPaint, maxWidth,
                    TextUtils.TruncateAt.END).toString()

            mTitleTruncated = title

            if (DEBUG) {
//                Log.i(LOG_TAG, "adjustTextBounds: mTitleTruncated=" + mTitleTruncated + ", length="
//                        + mTitleTruncated!!.length)
            }
        }

        mTextPaint.getTextBounds(mTitleTruncated, 0, mTitleTruncated!!.length, mTempRect)

        if (mDrawableRightButton != null) {
            mTempRect.right += (mPaddingX + mCalloutRightButtonWidth).toInt()
        }

        if (mTailText != null) {
            mTempRect.right += (mTailGapX + mTailTextWidth).toInt()
        }

        if (DEBUG) {
//            Log.i(LOG_TAG, "adjustTextBounds: mTempRect.width=" + mTempRect.width() + ", mTempRect.height="
//                    + mTempRect.height())
        }

        //  Setup the callout with the right size & location
        mTempRectF.set(mTempRect)
        val dy = (mBackgroundHeight - mTempRect.height()) / 2
        mTempRectF.inset(-mPaddingX, -dy)
        //mTempRectF.inset(-mPaddingX, -mPaddingY);

        // set minimum size
        if (mTempRectF.width() < mMinimumWidth) {
            val dx = (mMinimumWidth - mTempRectF.width()) / 2
            mTempRectF.inset(-dx, 0f)
        }

        // set position
        val left = (mTempPoint.x - (mTempRectF.width() * mOverlayItem.anchorXRatio).toInt()).toFloat()
        val top = mTempPoint.y.toFloat() - (mItemBounds.height() * mOverlayItem.anchorYRatio).toInt().toFloat() - mItemGapY
        -mTotalHeight
        mTempRectF.set(left, top, left + mTempRectF.width(), top + mTempRectF.height())

    }

    override fun getDrawable(rank: Int, itemState: Int): Drawable? {
        if (mDrawableRightButton != null && mDrawableRightButton!!.size >= 3) {
            var idxDrawable = 0
            if (NMapOverlayItem.isPressedState(itemState)) {
                idxDrawable = 1
            } else if (NMapOverlayItem.isSelectedState(itemState)) {
                idxDrawable = 2
            } else if (NMapOverlayItem.isFocusedState(itemState)) {
                idxDrawable = 2
            }
            val drawable = mDrawableRightButton!![idxDrawable]
            return drawable
        }

        return null
    }

    companion object {
        private val LOG_TAG = "NMapCalloutCustomOverlay"
        private val DEBUG = false

        private val CALLOUT_TEXT_COLOR = 0xFFFFFFFF.toInt()
        private val CALLOUT_TEXT_SIZE = 16.0f
        private val CALLOUT_TEXT_TYPEFACE: Typeface? = null//Typeface.DEFAULT_BOLD;

        private val CALLOUT_RIGHT_BUTTON_WIDTH = 50.67f
        private val CALLOUT_RIGHT_BUTTON_HEIGHT = 34.67f

        private val CALLOUT_MARGIN_X = 9.33f
        private val CALLOUT_PADDING_X = 9.33f
        private val CALLOUT_PADDING_OFFSET = 0.45f
        private val CALLOUT_PADDING_Y = 17.33f
        private val CALLOUT_MIMIMUM_WIDTH = 63.33f
        private val CALLOUT_TOTAL_HEIGHT = 64.0f
        private val CALLOUT_BACKGROUND_HEIGHT = CALLOUT_PADDING_Y + CALLOUT_TEXT_SIZE + CALLOUT_PADDING_Y
        private val CALLOUT_ITEM_GAP_Y = 0.0f
        private val CALLOUT_TAIL_GAP_X = 6.67f
        private val CALLOUT_TITLE_OFFSET_Y = -2.0f
    }
}