package com.example.shopinglist.utils

import android.view.MotionEvent
import android.view.View

 // слушатель прикосновений, будем передвигать палитру цветов в разное место экрана
class MyTouchListener: View.OnTouchListener {

     var xDelta = 0.0f
     var yDelta = 0.0f

    override fun onTouch(p0: View, p1: MotionEvent?): Boolean {

        when(p1?.action){  // какие действия происходят

            MotionEvent.ACTION_DOWN ->{   // когда мы отпустили элемент
                xDelta = p0.x - p1.rawX // настоящая позиция - позиия куда переместили
                yDelta = p0.y - p1.rawY
            }

            MotionEvent.ACTION_MOVE -> { // когда мы схватили объект и движем его

                // когда движем чтобы было видно куда движется объект
                p0.x = xDelta + p1.rawX
                p0.y = yDelta + p1.rawY
            }
        }
        return true
    }
}