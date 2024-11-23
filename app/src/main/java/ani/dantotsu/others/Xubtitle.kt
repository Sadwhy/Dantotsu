package ani.dantotsu.others

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
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
    private var isDepressedEffectApplied: Boolean = false

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

        // Create StaticLayout for line breaks and proper text alignment
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)  // Center-align text
            .setLineSpacing(0f, 1f)
            .build()

        // Draw text with outline only if applied
        if (isOutlineApplied) {
            textPaint.style = Paint.Style.STROKE
            textPaint.strokeWidth = outlineThickness
            textPaint.color = outlineStrokeColor
            canvas.save()
            canvas.translate(0f, 0f)  // Adjust the position to avoid clipping
            staticLayout.draw(canvas)
            canvas.restore()
        }

        // Draw text with default fill color (no outline unless applied)
        textPaint.style = Paint.Style.FILL
        textPaint.color = currentTextColor
        canvas.save()
        canvas.translate(0f, 0f)  // Adjust the position to avoid clipping
        staticLayout.draw(canvas)
        canvas.restore()

        // Apply shine effect if enabled
        if (isShineEffectApplied) {
            applyShineEffect(canvas)
        }

        // Apply depressed effect if enabled
        if (isDepressedEffectApplied) {
            applyDepressedEffect(canvas)
        }
    }

    // Apply shine effect (gradient effect)
    private fun applyShineEffect(canvas: Canvas) {
        val shader = LinearGradient(
            0f, 0f, width.toFloat(), height.toFloat(),
            intArrayOf(Color.BLACK, Color.WHITE, Color.BLACK),
            null,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        canvas.save()
        canvas.translate(0f, 0f)
        paint.color = currentTextColor
        canvas.drawText(text.toString(), 0f, baseline.toFloat(), paint)
        canvas.restore()
    }

    // Apply depressed (embossed) effect
    private fun applyDepressedEffect(canvas: Canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.setShadowLayer(10f, 5f, 5f, Color.BLACK)
        canvas.save()
        canvas.translate(0f, 0f)
        paint.color = currentTextColor
        canvas.drawText(text.toString(), 0f, baseline.toFloat(), paint)
        canvas.restore()
    }

    // Apply outline effect (only draws outline when called)
    fun applyOutline(outlineStrokeColor: Int = this.outlineStrokeColor, outlineThickness: Float = this.outlineThickness) {
        this.outlineStrokeColor = outlineStrokeColor
        this.outlineThickness = outlineThickness
        isOutlineApplied = true  // Enable outline drawing
        invalidate()  // Redraw with outline
    }

    // Enable shine effect
    fun applyShineEffect() {
        isShineEffectApplied = true
        invalidate()
    }

    // Enable depressed effect
    fun applyDepressedEffect() {
        isDepressedEffectApplied = true
        invalidate()
    }
}