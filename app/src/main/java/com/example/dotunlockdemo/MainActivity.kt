package com.example.dotunlockdemo

import android.graphics.Point
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import kotlin.contracts.Returns

class MainActivity : AppCompatActivity() {
    //用数组保存9个圆点的对象  用于滑动过程中遍历
    //对象创建的顺序：构造方法->init代码块、属性的创建->onCreate(setContentView(R.layout.activity_main)
    //private val dots= arrayOf(dot1,dot2,dot3,dot4,dot5,dot6,dot7,dot8,dot9)   直接这样会报错（空指针异常） 因为加载顺序中init代码块和属性创建是谁在前面谁先加载，这里把属性放在前面，还没有初始化，所以找不到这个，所以会报错
    //下面是解决这种问题的两种方法
    //private var dots:Array<ImageView>?=null   //创建为可选值（可为空）避免了空指针异常
    //懒加载
    private val dots: Array<ImageView> by lazy {
        arrayOf(sdot1, sdot2, sdot3, sdot4, sdot5, sdot6, sdot7, sdot8, sdot9)
    }

    //使用懒加载获取屏幕状态栏和标题栏的高度
    //因为滑动的时候要经常调用这个方法，所以使用懒加载
    private val barHeight:Int by lazy {
        //1.获取屏幕的尺寸
        val display=DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(display)

        //获取操作区域的尺寸
        val drawRect= Rect()
        window.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT).getDrawingRect(drawRect)
        display.heightPixels-drawRect.height()

    }

    //保存当前被点亮的视图
    private val allSelectedViews= mutableListOf<ImageView>()

    //用于滑懂过程中的轨迹
    private val password=StringBuilder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


    //监听触摸事件
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val location:Point =convertTouchLocateToContainer(event)
                findViewContainsPoint(location).also {
                    highlightView(it)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val location:Point =convertTouchLocateToContainer(event)
                findViewContainsPoint(location).also {
                    highlightView(it)
                }
            }


            MotionEvent.ACTION_UP -> {
                reset()
            }

        }
        return false;
    }

    //点亮视图
    private fun highlightView(v:ImageView?) {
        if (v != null&&v.visibility==View.INVISIBLE) {
            //点亮这个点
            v.visibility = View.VISIBLE
            allSelectedViews.add(v)
            password.append(v.tag)
        }
    }

    //还原操作
    private fun reset(){
        //遍历保存点亮点的数组
        for(item in allSelectedViews){
            item.visibility=View.INVISIBLE
        }

        //清空
        allSelectedViews.clear()

        Log.v("wxw",password.toString())
        password.clear()
    }

    //将触摸点的坐标转化为相对于容器的坐标
    private fun convertTouchLocateToContainer(event: MotionEvent): Point {
        return Point().apply {
            //x=触摸点的x-容器的x
            x=(event.x-mContainer.x).toInt()
            //y=触摸点的y-状态栏高度-容器的y
            y=(event.y-barHeight-mContainer.y).toInt()
        }
    }

    //获取当前触摸点所在的圆点视图
    private fun findViewContainsPoint(point: Point):ImageView?{
        //遍历所有的点是否包含这个point
        for(dotView in dots){
            //判断这个视图是否包含point
            getRectForView(dotView).also{
                if(it.contains(point.x,point.y))
                    return dotView

            }
        }
        return null
    }

    //获取视图对应的Rect
    private fun getRectForView(v:ImageView)=Rect(v.left,v.top,v.right,v.bottom)
}