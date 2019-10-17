package com.lpw.kotlinquiz.Adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.lpw.kotlinquiz.Fragments.QuestionFragment

class MyFragmentAdapter(fm:FragmentManager, var context: Context,
                       var fragmentList:List<QuestionFragment>):FragmentPagerAdapter(fm){
    override fun getItem(pos: Int): Fragment {
        return fragmentList[pos]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return StringBuilder("Question").append(position+1).toString()
    }

    internal var instance:MyFragmentAdapter?=null


}