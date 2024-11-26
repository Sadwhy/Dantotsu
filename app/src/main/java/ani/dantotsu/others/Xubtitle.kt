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
    private var outlineThickness: Float = 0f
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

        // Configure effect before drawing
        when (currentEffect) {
            Effect.DROP_SHADOW -> {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
                // Apply a regular shadow (no gradient here)
                textPaint.setShadowLayer(8f, 4f, 4f, effectColor)
            }
            Effect.SHINE -> {
                // Create a gradient for the shadow under the text (shine effect)
                val shadowShader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(Color.WHITE, effectColor, Color.BLACK),
                    null,
                    Shader.TileMode.CLAMP
                )

                // Create a paint for the shadow with the gradient shader
                val shadowPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.FILL
                    textSize = textPaint.textSize
                    typeface = textPaint.typeface
                    shader = shadowShader
                }

                // Draw the shadow manually with an offset
                canvas.drawText(
                    text,
                    x + 4f,  // Shadow offset X (adjust as needed)
                    y + 4f,  // Shadow offset Y (adjust as needed)
                    shadowPaint
                )

                // Apply the shine gradient to the text
                val shader = LinearGradient(
                    0f, 0f, width.toFloat(), height.toFloat(),
                    intArrayOf(effectColor, Color.WHITE, Color.WHITE),
                    null,
                    Shader.TileMode.CLAMP
                )
                textPaint.shader = shader
            }
            else -> {
                textPaint.shader = null // Reset shader for other effects
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

        // Reset paint properties after drawing
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
    }

    // Apply shine effect with custom gradient colors
    fun applyShineEffect(color: Int) {
        this.effectColor = color
        currentEffect = Effect.SHINE
    }

    // Apply drop shadow with custom shadow color
    fun applyDropShadow(color: Int, subStroke: Float) {
        this.effectColor = color
        paint.setShadowLayer(subStroke, 4f, 4f, color)
        currentEffect = Effect.DROP_SHADOW
    }
}