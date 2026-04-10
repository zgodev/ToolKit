package com.zhangyt.widget

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import android.graphics.*

/**
 *@author zhangyt
 *@description
 *@Date 2025/4/23 15:47
 **/
class NodeProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Configuration properties
    private var progress = 0
    private var max = 100
    private var primaryNodeColor = Color.BLUE
    private var secondaryNodeColor = Color.GRAY
    private var nodeRadius = 16f
    private var secondaryNodeRadius = 8f
    private var labelTextSize = 12f
    private var labelTextColor = Color.BLACK
    private var progressBarHeight = 8f
    private var progressColor = Color.BLUE
    private var trackColor = Color.GRAY

    // Node data
    private val primaryNodes = mutableListOf<PrimaryNode>()
    private val secondaryNodes = mutableListOf<SecondaryNode>()

    // Drawing tools
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val primaryNodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val secondaryNodePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Touch handling
    private var isDragging = false
    private var currentPosition = 0f
    private val thumbRadius = 24f

    // Listeners
    var onProgressChangedListener: ((Int) -> Unit)? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.NodeProgressBar).apply {
            progress = getInt(R.styleable.NodeProgressBar_progress, 0)
            max = getInt(R.styleable.NodeProgressBar_max, 100)
            primaryNodeColor = getColor(R.styleable.NodeProgressBar_primaryNodeColor, Color.BLUE)
            secondaryNodeColor = getColor(R.styleable.NodeProgressBar_secondaryNodeColor, Color.GRAY)
            nodeRadius = getDimension(R.styleable.NodeProgressBar_nodeRadius, 16f)
            secondaryNodeRadius = getDimension(R.styleable.NodeProgressBar_secondaryNodeRadius, 8f)
            labelTextSize = getDimension(R.styleable.NodeProgressBar_labelTextSize, 12f)
            labelTextColor = getColor(R.styleable.NodeProgressBar_labelTextColor, Color.BLACK)
            progressBarHeight = getDimension(R.styleable.NodeProgressBar_progressBarHeight, 8f)
            progressColor = getColor(R.styleable.NodeProgressBar_progressColor, Color.BLUE)
            trackColor = getColor(R.styleable.NodeProgressBar_trackColor, Color.GRAY)
        }

        setupPaints()
    }

    private fun setupPaints() {
        trackPaint.color = trackColor
        trackPaint.style = Paint.Style.FILL

        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.FILL

        primaryNodePaint.color = primaryNodeColor
        secondaryNodePaint.color = secondaryNodeColor

        labelPaint.color = labelTextColor
        labelPaint.textSize = labelTextSize

        thumbPaint.color = progressColor
    }

    fun addPrimaryNode(position: Int, label: String) {
        primaryNodes.add(PrimaryNode(position, label))
        primaryNodes.sortBy { it.position }
        invalidate()
    }

    fun addSecondaryNode(position: Int) {
        secondaryNodes.add(SecondaryNode(position))
        secondaryNodes.sortBy { it.position }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        val progressWidth = width - paddingLeft - paddingRight
        val startX = paddingLeft.toFloat()
        val endX = width - paddingRight.toFloat()

        // Draw track
        canvas.drawRoundRect(
            RectF(startX, centerY - progressBarHeight / 2, endX, centerY + progressBarHeight / 2),
            progressBarHeight,
            progressBarHeight,
            trackPaint
        )

        // Draw progress
        val progressEnd = startX + (progressWidth * progress / max.toFloat())
        canvas.drawRoundRect(
            RectF(startX, centerY - progressBarHeight / 2, progressEnd, centerY + progressBarHeight / 2),
            progressBarHeight,
            progressBarHeight,
            progressPaint
        )

        // Draw secondary nodes
        secondaryNodes.forEach { node ->
            val x = startX + (node.position.toFloat() / max) * progressWidth
            canvas.drawCircle(x, centerY, secondaryNodeRadius, secondaryNodePaint)
        }

        // Draw primary nodes and labels
        primaryNodes.forEach { node ->
            val x = startX + (node.position.toFloat() / max) * progressWidth
            canvas.drawCircle(x, centerY, nodeRadius, primaryNodePaint)

            // Draw label
            val labelY = centerY - nodeRadius - labelPaint.textSize - 8
            canvas.drawText(node.label, x, labelY, labelPaint)
        }

        // Draw thumb
        currentPosition = startX + (progressWidth * progress / max.toFloat())
        canvas.drawCircle(currentPosition, centerY, thumbRadius, thumbPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (abs(event.x - currentPosition) < thumbRadius * 2 &&
                    abs(event.y - height / 2f) < thumbRadius * 2) {
                    isDragging = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    updateProgressFromTouch(event.x)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                snapToNearestNode()
            }
        }
        return true
    }

    private fun updateProgressFromTouch(touchX: Float) {
        val progressWidth = width - paddingLeft - paddingRight
        val rawProgress = ((touchX - paddingLeft) / progressWidth * max).coerceIn(0f, max.toFloat())
        progress = findNearestNodePosition(rawProgress)
        onProgressChangedListener?.invoke(progress)
        invalidate()
    }

    private fun findNearestNodePosition(rawProgress: Float): Int {
        val allNodes = primaryNodes.map { it.position } + secondaryNodes.map { it.position }
        return allNodes.minByOrNull { abs(it - rawProgress) } ?: rawProgress.toInt()
    }

    private fun snapToNearestNode() {
        val nearest = findNearestNodePosition(progress.toFloat())
        if (nearest != progress) {
            animateProgress(nearest)
        }
    }

    private fun animateProgress(target: Int) {
        ValueAnimator.ofInt(progress, target).apply {
            duration = 200
            addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onProgressChangedListener?.invoke(progress)
                }
            })
        }
    }

    fun setProgress(progress: Int) {
        this.progress = progress.coerceIn(0, max)
        invalidate()
    }

    data class PrimaryNode(val position: Int, val label: String)
    data class SecondaryNode(val position: Int)
}