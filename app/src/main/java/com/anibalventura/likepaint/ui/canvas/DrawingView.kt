package com.anibalventura.likepaint.ui.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var canvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null

    private var drawPath: CustomPath? = null
    private var paths = ArrayList<CustomPath>()
    private var undonePaths = ArrayList<CustomPath>()

    private var brushSize: Float = 20F
    private var brushColor: Int = Color.BLACK


    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        drawPaint = Paint()
        drawPath = CustomPath(brushColor, brushSize)

        drawPaint!!.color = brushColor
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND

        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitmap!!)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)

        for (path in paths) {
            drawPaint!!.strokeWidth = path.brushThickness
            drawPaint!!.color = path.color
            canvas.drawPath(path, drawPaint!!)
        }

        if (!drawPath!!.isEmpty) {
            drawPaint!!.strokeWidth = drawPath!!.brushThickness
            drawPaint!!.color = drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath!!.color = brushColor
                drawPath!!.brushThickness = brushSize
                drawPath!!.reset()
                drawPath!!.moveTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath!!.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                paths.add(drawPath!!)
                drawPath = CustomPath(brushColor, brushSize)
            }
            else -> return false
        }

        invalidate()
        return true
    }

    fun setBrushSize(size: Float): Float {
        brushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, size,
            resources.displayMetrics
        )
        drawPaint!!.strokeWidth = brushSize

        return brushSize
    }

    fun setBrushColor(newColor: Int) {
        brushColor = newColor
        drawPaint!!.color = brushColor
    }

    fun undoPath() {
        when {
            paths.size > 0 -> {
                undonePaths.add(paths.removeAt(paths.size - 1))
                invalidate()
            }
        }
    }

    fun redoPath() {
        when {
            undonePaths.size > 0 -> {
                paths.add(undonePaths.removeAt(undonePaths.size - 1))
                invalidate()
            }
        }
    }

    fun clearDrawing() {
        drawPath?.reset()
        paths.clear()
        invalidate()
    }
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
}