package com.example.slideview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller

class ScrollViewPager(context: Context,atts:AttributeSet?) : ViewGroup(context,atts) {
    /**
     * 第一步 定义 Scroller 实例
     */
    private var mScroller = Scroller(context)

    /**
     * 判断拖动的最小移动像素点
     */
    private var mTouchSlop = 0

    /**
     * 界面可以滚动的左边界
     */
    private var mLeftBorder = 0

    /**
     * 界面可以滚动的右边界
     */
    private var mRightBorder = 0

    /**
     * 手指按下屏幕的 x 坐标
     */
    private var mDownX = 0f

    /**
     * 记录上一次触发 按下是的x坐标
     */
    private var mLastMoveX = 0f

    /**
     * 手指当前所在坐标
     */
    private var mMoveX  = 0f


    init {
        /**
         * 通过 ViewConfiguration 拿到认为手指滑动的最短的移动 px 值
         */
        mTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop
    }


    constructor(context: Context) : this(context,null) {}


    /**
     * 测量 child 宽高
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //拿到子View 个数
        val childCount = childCount
        for (index in 0..childCount - 1) {
            val childView = getChildAt(index)
            //为 ScrollerViewPager 中的每一个子控件测量大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
        }
    }



    /**
     * 测量完之后，拿到 child 的大小然后开始对号入座
     * 测量child坐标
     */

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            //存在child
            val childCount = childCount
            for (index in 0..childCount - 1) {
                //拿到子View
                val childView = getChildAt(index)
                //开始对号入座 测量坐标
                childView.layout(index * childView.measuredWidth, 0, (index + 1) * childView.measuredWidth, childView.measuredHeight)
            }


            //初始化左右边界
            mLeftBorder = getChildAt(0).left
            mRightBorder = getChildAt(childCount - 1).right

        }

    }


    /**
     * 重写拦截事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                //拿到手指按下相当于屏幕的坐标
                mDownX = ev.getRawX()
                mLastMoveX = mDownX
            }
            MotionEvent.ACTION_MOVE -> {
                //拿到当前移动的 x 坐标
                 mMoveX = ev.getRawX()
                //拿到差值
                val absDiff = Math.abs(mMoveX - mDownX)
                mLastMoveX = mMoveX
                //当手指拖动值大于 TouchSlop 值时，就认为是在滑动，拦截子控件的触摸事件
                if (absDiff > mTouchSlop)
                    return true
            }
        }


        return super.onInterceptTouchEvent(ev)
    }



    /**
     * 父容器没有拦截事件
     * viewGroup接收到用户的触摸事件
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                //拿到当前滑动的相对于屏幕左上角的坐标
                mMoveX = event.getRawX()
                var scrolledX = (mLastMoveX - mMoveX).toInt()
                //边界判断
                if (scrollX + scrolledX < mLeftBorder) {
                    scrollTo(mLeftBorder, 0)
                    return true
                }else if (scrollX + width + scrolledX > mRightBorder){
                    scrollTo(mRightBorder-width,0)
                    return true

                }
                scrollBy(scrolledX,0)
                mLastMoveX = mMoveX
            }
            MotionEvent.ACTION_UP -> {
                //当手指抬起是，根据当前滚动值来判定应该回滚到哪个子控件的界面上
                var targetIndex = (scrollX + width/2 ) / width
                //destx x坐标滑动终点
                var dx = targetIndex * width - scrollX
                /** 第二步 调用 startScroll 方法弹性回滚并刷新页面*/
                mScroller.startScroll(scrollX,0,dx,0)
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }


    override fun computeScroll() {
        super.computeScroll()
        /**
         * 第三步 重写 computeScroll 方法，并在其内部完成平滑滚动的逻辑
         */
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.currX,mScroller.currY)
            postInvalidate()
        }
    }









}