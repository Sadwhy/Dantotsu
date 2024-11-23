package ani.dantotsu.others


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.LinearGradient
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import ani.dantotsu.R


class Xubtitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    private var outlineStrokeColor: Int = Color.BLACK
    private var outlineThickness: Float = 4f

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.Xubtitle, 0, 0).apply {
            try {
                outlineStrokeColor = getColor(R.styleable.Xubtitle_outlineStrokeColor, Color.BLACK)
                outlineThickness = getDimension(R.styleable.Xubtitle_outlineThickness, 4f)
            } finally {
                recycle()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val textPaint = paint

        // Draw outline (custom color and thickness)
        textPaint.style = Paint.Style.STROKE
        textPaint.strokeWidth = outlineThickness
        textPaint.color = outlineStrokeColor
        canvas.drawText(text, 0f, baseline.toFloat(), textPaint)

        // Draw fill (default text color)
        textPaint.style = Paint.Style.FILL
        textPaint.color = currentTextColor
        canvas.drawText(text, 0f, baseline.toFloat(), textPaint)
    }

    fun applyShineEffect() {
        val shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(Color.BLACK, Color.WHITE, Color.WHITE),
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        invalidate()
    }

    fun applyDropShadow() {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // Required for shadow effects
        paint.setShadowLayer(8f, 4f, 4f, Color.BLACK)
        invalidate()
    }

    // Custom XML attributes for outline properties
    fun setOutlineStrokeColor(color: Int) {
        outlineStrokeColor = color
    }

    fun setOutlineThickness(thickness: Float) {
        outlineThickness = thickness
    }
}