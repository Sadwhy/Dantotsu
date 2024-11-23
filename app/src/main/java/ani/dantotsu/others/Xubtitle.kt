package ani.dantotsu.others

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
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
    private var currentEffect: Effect = Effect.NONE

    enum class Effect {
        NONE,
        OUTLINE,
        SHINE,
        DROP_SHADOW
    }

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

        // Configure effect before drawing
        when (currentEffect) {
            Effect.DROP_SHADOW -> {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
                textPaint.setShadowLayer(8f, 4f, 4f, Color.BLACK)
            }
            Effect.SHINE -> {
                val shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.BLACK, Color.WHITE, Color.BLACK),
                    null,
                    Shader.TileMode.CLAMP
                )
                textPaint.shader = shader
            }
            else -> {
                // No special configuration needed for NONE or OUTLINE
            }
        }

        // Create StaticLayout for line breaks and proper text alignment
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1f)
            .build()

        canvas.save()

        // Draw outline if it's the current effect
        if (currentEffect == Effect.OUTLINE) {
            textPaint.style = Paint.Style.STROKE
            textPaint.strokeWidth = outlineThickness
            textPaint.color = outlineStrokeColor
            staticLayout.draw(canvas)
        }

        // Draw filled text with any active effect
        textPaint.style = Paint.Style.FILL
        textPaint.color = currentTextColor
        staticLayout.draw(canvas)

        canvas.restore()

        // Reset paint properties
        textPaint.shader = null
        if (currentEffect == Effect.DROP_SHADOW) {
            textPaint.clearShadowLayer()
        }
    }

    // Apply outline effect with color and thickness
    fun applyOutline(outlineStrokeColor: Int, outlineThickness: Float) {
        this.outlineStrokeColor = outlineStrokeColor
        this.outlineThickness = outlineThickness
        currentEffect = Effect.OUTLINE
        invalidate()
    }

    // Apply shine effect with custom gradient colors
    fun applyShineEffect(colors: IntArray = intArrayOf(Color.BLACK, Color.WHITE, Color.BLACK)) {
        currentEffect = Effect.SHINE
        invalidate()
    }

    // Apply drop shadow with custom shadow color
    fun applyDropShadow(shadowColor: Int = Color.BLACK) {
        paint.setShadowLayer(8f, 4f, 4f, shadowColor)
        currentEffect = Effect.DROP_SHADOW
        invalidate()
    }
}