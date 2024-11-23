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
    private var effectColor: Int = currentTextColor
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

        // Create StaticLayout for proper line breaking and alignment
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setLineSpacing(0f, 1f)
            .build()

        canvas.save()

        when (currentEffect) {
            Effect.OUTLINE -> {
                textPaint.style = Paint.Style.STROKE
                textPaint.strokeWidth = outlineThickness
                textPaint.color = effectColor
                staticLayout.draw(canvas)
            }
            Effect.SHINE -> {
                val shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(effectColor, Color.WHITE, effectColor),
                    null,
                    Shader.TileMode.CLAMP
                )
                textPaint.shader = shader
                textPaint.style = Paint.Style.FILL
                staticLayout.draw(canvas)
                textPaint.shader = null // Reset shader after use
            }
            Effect.DROP_SHADOW -> {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
                textPaint.setShadowLayer(8f, 4f, 4f, effectColor)
                textPaint.style = Paint.Style.FILL
                staticLayout.draw(canvas)
                textPaint.clearShadowLayer() // Reset shadow after use
            }
            else -> {
                // Default effect (None)
                textPaint.style = Paint.Style.FILL
                textPaint.color = currentTextColor
                staticLayout.draw(canvas)
            }
        }

        canvas.restore()
    }

    fun applyOutline(outlineStrokeColor: Int, outlineThickness: Float) {
        this.effectColor = outlineStrokeColor
        this.outlineThickness = outlineThickness
        currentEffect = Effect.OUTLINE
    }

    fun applyShineEffect(color: Int) {
        this.effectColor = color
        currentEffect = Effect.SHINE
    }

    fun applyDropShadow(color: Int) {
        this.effectColor = color
        currentEffect = Effect.DROP_SHADOW
    }
}