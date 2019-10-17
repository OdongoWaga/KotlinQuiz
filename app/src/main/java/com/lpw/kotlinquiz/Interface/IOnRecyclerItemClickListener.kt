package com.lpw.kotlinquiz.Interface

import android.view.View

interface IOnRecyclerItemClickListener {
    fun onClick(view: View, position:Int)
}