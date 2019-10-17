package com.lpw.kotlinquiz.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lpw.kotlinquiz.Common.Common
import com.lpw.kotlinquiz.Model.CurrentQuestion
import com.lpw.kotlinquiz.R

class GridAnswerAdapter(internal var context: Context,
                        internal var answerSheetList: List<CurrentQuestion>):
    RecyclerView.Adapter<GridAnswerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_answer_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return answerSheetList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (answerSheetList[position].type == Common.ANSWER_TYPE.RIGHT_ANSWER)
            holder.questionItem.setBackgroundResource(R.drawable.grid_item_right_answer)
        else if(answerSheetList[position].type == Common.ANSWER_TYPE.WRONG_ANSWER)
            holder.questionItem.setBackgroundResource(R.drawable.grid_item_wrong_answer)
        else
            holder.questionItem.setBackgroundResource(R.drawable.grid_item_no_answer)
    }

    inner class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

            internal var questionItem:View
            init {
                questionItem = itemView.findViewById(R.id.question_item)as View
            }
    }
}
