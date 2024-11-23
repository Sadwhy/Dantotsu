package ani.dantotsu.others

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.LinearGradient
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import ani.dantotsu.R

class Xubtitle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var outlineStrokeColor: Int = Color.BLACK
    private var outlineThickness: Float = 4f
    private var isOutlineApplied: Boolean = false
    private var isShineEffectApplied: Boolean = false
    private var isDropShadowApplied: Boolean = false

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

        // Draw text with outline only if applied
        if (isOutlineApplied) {
            textPaint.style = Paint.Style.STROKE
            textPaint.strokeWidth = outlineThickness
            textPaint.color = outlineStrokeColor
            canvas.drawText(text, 0f, baseline.toFloat(), textPaint)
        }

        // Draw text with default fill color (no outline unless applied)
        textPaint.style = Paint.Style.FILL
        textPaint.color = currentTextColor
        canvas.drawText(text, 0f, baseline.toFloat(), textPaint)

        // Apply shine effect if enabled
        if (isShineEffectApplied) {
            applyShineEffect()
        }

        // Apply drop shadow if enabled
        if (isDropShadowApplied) {
            applyDropShadow()
        }
    }

    // Apply shine effect
    fun applyShineEffect() {
        if (!isShineEffectApplied) {
            val shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(Color.BLACK, Color.WHITE, Color.WHITE),
                null,
                Shader.TileMode.CLAMP
            )
            paint.shader = shader
            isShineEffectApplied = true
            invalidate()
        }
    }

    // Apply drop shadow effect
    fun applyDropShadow() {
        if (!isDropShadowApplied) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
            paint.setShadowLayer(8f, 4f, 4f, Color.BLACK)
            isDropShadowApplied = true
            invalidate()
        }
    }

    // Apply outline effect (only draws outline when called)
    fun applyOutline(outlineStrokeColor: Int = this.outlineStrokeColor, outlineThickness: Float = this.outlineThickness) {
        this.outlineStrokeColor = outlineStrokeColor
        this.outlineThickness = outlineThickness
        isOutlineApplied = true  // Enable outline drawing
        invalidate()  // Redraw with outline
    }

    // Custom XML attributes for outline properties
    fun setOutlineStrokeColor(color: Int) {
        outlineStrokeColor = color
    }

    fun setOutlineThickness(thickness: Float) {
        outlineThickness = thickness
    }

    // New method to remove outline
    fun removeOutline() {
        isOutlineApplied = false  // Disable outline drawing
        invalidate()
    }

    // New method to remove shine effect
    fun removeShineEffect() {
        isShineEffectApplied = false
        paint.shader = null  // Remove the shader
        invalidate()
    }

    // New method to remove drop shadow
    fun removeDropShadow() {
        isDropShadowApplied = false
        paint.clearShadowLayer()  // Remove shadow layer
        invalidate()
    }
}