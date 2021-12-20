package ch.usi.inf.mwc.cusi.utils.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.graphics.withTranslation
import androidx.core.text.inSpans
import androidx.recyclerview.widget.RecyclerView
import ch.usi.inf.mwc.cusi.R

/**
 *
 */
class SideHeaderDecoration(
    context: Context,
    data: Map<Int, Pair<String, String>>,
) : RecyclerView.ItemDecoration() {
    private val paint: TextPaint
    private val width: Int
    private val padding: Int

    private val primaryTextSizeSpan: AbsoluteSizeSpan
    private val secondaryTextSizeSpan: AbsoluteSizeSpan
    private val boldSpan = StyleSpan(Typeface.BOLD)

    private val headers: Map<Int, StaticLayout>


    init {
        val attrs = context.obtainStyledAttributes(
            R.style.AppTheme_SideHeader,
            R.styleable.SideHeader
        )

        paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = attrs.getColor(R.styleable.SideHeader_android_textColor, Color.BLACK)
        }

        width = attrs.getDimensionPixelSize(R.styleable.SideHeader_android_width, 0)
        padding = attrs.getDimensionPixelSize(R.styleable.SideHeader_android_padding, 0)

        val primaryTextSize =
            attrs.getDimensionPixelSize(R.styleable.SideHeader_primaryTextSize, 0)
        val secondaryTextSize =
            attrs.getDimensionPixelSize(R.styleable.SideHeader_secondaryTextSize, 0)
        primaryTextSizeSpan = AbsoluteSizeSpan(primaryTextSize)
        secondaryTextSizeSpan = AbsoluteSizeSpan(secondaryTextSize)

        attrs.recycle()

        headers = data.mapValues { createHeader(it.value) }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        if (headers.isEmpty() || parent.childCount == 0) {
            return
        }

        val isRtl = parent.layoutDirection == View.LAYOUT_DIRECTION_RTL
        if (isRtl) {
            c.apply {
                save()
                translate((parent.width - width).toFloat(), 0f)
            }
        }

        var earliestPosition = Int.MAX_VALUE
        var previousHeaderPosition = -1
        var previousHasHeader = false
        var earliestChild: View? = null
        for (i in parent.childCount - 1 downTo 0) {
            // This should not be null, but observed null at times.
            val child = parent.getChildAt(i) ?: continue

            if (child.y > parent.height || (child.y + child.height) < 0) {
                // Can't see this child
                continue
            }

            val position = parent.getChildAdapterPosition(child)
            if (position < 0) {
                continue
            }
            if (position < earliestPosition) {
                earliestPosition = position
                earliestChild = child
            }

            val header = headers[position]
            if (header != null) {
                drawHeader(c, child, header, child.alpha, previousHasHeader)
                previousHeaderPosition = position
                previousHasHeader = true
            } else {
                previousHasHeader = false
            }
        }

        if (earliestChild != null && earliestPosition != previousHeaderPosition) {
            // This child needs a sticky header
            findHeaderBeforePosition(earliestPosition)?.let { stickyHeader ->
                previousHasHeader = previousHeaderPosition - earliestPosition == 1
                drawHeader(c, earliestChild, stickyHeader, 1f, previousHasHeader)
            }
        }

        if (isRtl) {
            c.restore()
        }
    }

    private fun findHeaderBeforePosition(position: Int): StaticLayout? {
        for (key in headers.keys.reversed()) {
            if (key < position) {
                return headers[key]
            }
        }

        return null
    }

    private fun drawHeader(
        canvas: Canvas,
        child: View,
        header: StaticLayout,
        headerAlpha: Float,
        previousHasHeader: Boolean
    ) {
        val childTop = child.y.toInt()
        val childBottom = childTop + child.height
        var top = (childTop + padding).coerceAtLeast(padding)
        if (previousHasHeader) {
            top = top.coerceAtMost(childBottom - header.height - padding)
        }
        paint.alpha = (headerAlpha * 255).toInt()
        canvas.withTranslation(y = top.toFloat()) {
            header.draw(canvas)
        }
    }

    private fun createHeader(value: Pair<String, String>): StaticLayout {
        val (primaryText, secondaryText) = value

        val text = SpannableStringBuilder().apply {
            inSpans(boldSpan) {
                inSpans(primaryTextSizeSpan) {
                    append(primaryText)
                }
            }
            if (secondaryText.isNotEmpty()) {
                append('\n')
                inSpans(secondaryTextSizeSpan) {
                    append(secondaryText)
                }
            }
        }

        return StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .setLineSpacing(0f, 1f)
            .build()
    }

}
