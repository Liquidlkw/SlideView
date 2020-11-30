package com.example.slideview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class SlideView(context: Context, attrs: AttributeSet?): View(context) {
    /**
     * 记录上次滑动的坐标
     */
    private var mLastX = 0;
    private var mLastY = 0;

    /**
     * 初始化画笔
     */
    val paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 3f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //拿到相对于自己按下的坐标点
                mLastX = event.getX().toInt();
                mLastY = event.getY().toInt();
                println("拿到相对于屏幕按下的坐标点: x:$mLastX y:$mLastY")

            }
            MotionEvent.ACTION_MOVE -> {
                //1
                //计算 View 的位移距离
                var offsetX = event.getX().toInt() - mLastX;
                var offsetY = event.getY().toInt() - mLastY;
                //重新放置新的位置
                layout(getLeft() + offsetX, getTop() + offsetY, getRight() + offsetX, getBottom() + offsetY);


                //2 View 在平移过程中，top 和 left 表示的是原始左上角的位置信息，其值并不会发生改变，
                // 此时发生改变的是 x 、y、translationX 、translationY 这四个参数。
                // setX 内部也会调用 setTranslationX
//                x = event.getRawX() - mLastX
//                y =  event.getRawY() - mLastY

                //3
//                translationX = event.getRawX() - mLastX
//                translationY = event.getRawY() - mLastY


            }

            MotionEvent.ACTION_UP -> {

            }
        }
        return true//消耗触摸事件
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(300f, 300f, 150f, paint)
    }





}