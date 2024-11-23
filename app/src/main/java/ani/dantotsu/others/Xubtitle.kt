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
      
          // Create StaticLayout for line breaks and proper text alignment
          val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
              .setAlignment(Layout.Alignment.ALIGN_CENTER)  // Center-align text
              .setLineSpacing(0f, 1f)
              .build()
      
          // Save canvas state before drawing
          canvas.save()
      
          // Draw outline if it's applied
          if (isOutlineApplied) {
              textPaint.style = Paint.Style.STROKE
              textPaint.strokeWidth = outlineThickness
              textPaint.color = outlineStrokeColor
              staticLayout.draw(canvas)  // Draw only outline
          } else {
              // If outline is not applied, draw filled text normally
              textPaint.style = Paint.Style.FILL
              textPaint.color = currentTextColor
              staticLayout.draw(canvas)  // Draw filled text
          }
      
          // Restore canvas after drawing text
          canvas.restore()
      
          // Apply shine effect if enabled
          if (isShineEffectApplied) {
              applyShineEffect(canvas)
          }

          // Apply drop shadow effect if enabled
          if (isDropShadowApplied) {
              applyDropShadow(canvas)
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
        paint.color = currentTextColor
        canvas.drawText(text.toString(), 0f, baseline.toFloat(), paint)
        canvas.restore()
    }

    // Apply drop shadow effect
    private fun applyDropShadow(canvas: Canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.setShadowLayer(8f, 4f, 4f, Color.BLACK)  // Standard drop shadow
        canvas.save()
        paint.color = currentTextColor
        canvas.drawText(text.toString(), 0f, baseline.toFloat(), paint)
        canvas.restore()
    }

    // Apply outline effect (only draws outline when called)
    fun applyOutline(outlineStrokeColor: Int = this.outlineStrokeColor, outlineThickness: Float = this.outlineThickness) {
        this.outlineStrokeColor = outlineStrokeColor
        this.outlineThickness = outlineThickness
        isOutlineApplied = true  // Enable outline drawing
        invalidate()
    }

    // Enable shine effect
    fun applyShineEffect() {
        isShineEffectApplied = true
        invalidate()
    }

    // Enable drop shadow effect
    fun applyDropShadow() {
        isDropShadowApplied = true
        invalidate()
    }

    // Method to remove all effects
    fun removeAllEffects() {
        isShineEffectApplied = false
        isDepressedEffectApplied = false
        isDropShadowApplied = false
        invalidate()
    }
}