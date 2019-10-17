package com.lpw.kotlinquiz.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.lpw.kotlinquiz.Common.Common
import com.lpw.kotlinquiz.Interface.IOnRecyclerItemClickListener
import com.lpw.kotlinquiz.Model.CurrentQuestion
import com.lpw.kotlinquiz.R

class QusetionListHelperAdapter(internal var context: Context,
                                internal var answerSheetList:List<CurrentQuestion>):
    RecyclerView.Adapter<QusetionListHelperAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_question_list_helper_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return answerSheetList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        holder.txt_question_num.text = (pos+1).toString()

        if(answerSheetList[pos].type == Common.ANSWER_TYPE.RIGHT_ANSWER)
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_right_answer)
        else   if(answerSheetList[pos].type == Common.ANSWER_TYPE.WRONG_ANSWER)
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_wrong_answer)
        else
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_no_answer)

        holder.setiOnRecyclerViewItemClickListener(object :IOnRecyclerItemClickListener{
            override fun onClick(view: View, position: Int) {
                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(Intent(Common.KEY_GO_TO_QUESTION).putExtra(Common.KEY_GO_TO_QUESTION,position))
            }

        })

    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
       override fun onClick(v: View?) {
           iOnRecyclerViewItemClickListener.onClick(v!!,adapterPosition)
       }

       internal var txt_question_num: TextView
       internal var layout_wrapper:LinearLayout

       lateinit var iOnRecyclerViewItemClickListener: IOnRecyclerItemClickListener

       fun setiOnRecyclerViewItemClickListener(iOnRecyclerItemClickListener: IOnRecyclerItemClickListener){
           this.iOnRecyclerViewItemClickListener = iOnRecyclerItemClickListener
       }

       init {
           txt_question_num = itemView.findViewById(R.id.txt_question_num)as TextView
           layout_wrapper = itemView.findViewById(R.id.layout_wrapper)as LinearLayout

           itemView.setOnClickListener(this)
       }
   }
}