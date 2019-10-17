package com.lpw.kotlinquiz.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.lpw.kotlinquiz.Common.Common
import com.lpw.kotlinquiz.Interface.IOnRecyclerItemClickListener
import com.lpw.kotlinquiz.Model.Category
import com.lpw.kotlinquiz.Model.Question
import com.lpw.kotlinquiz.R
import com.lpw.kotlinquiz.UI.MainQuestionActivity

class CategoryAdapter(internal var context: Context,
                      internal var categoryList: List<Category>):
    RecyclerView.Adapter<CategoryAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, pos: Int) {
        holder.txt_category_name.text = categoryList[pos].name
        holder.setOnRecyclerViewItemClickListener(object :IOnRecyclerItemClickListener{
            override fun onClick(view: View, position: Int) {
              Common.selectedCategory = categoryList[position]
                val intent = Intent(context, MainQuestionActivity::class.java)
                context.startActivity(intent)
            }

        })
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{

        internal var txt_category_name: TextView
        internal var card_category: CardView
        internal lateinit var iOnRecyclerViewItemClickListener: IOnRecyclerItemClickListener

        fun setOnRecyclerViewItemClickListener(IOnRecyclerItemClickListener: IOnRecyclerItemClickListener){
            this.iOnRecyclerViewItemClickListener = IOnRecyclerItemClickListener
        }

        init {
            txt_category_name = itemView.findViewById(R.id.txt_category_name) as TextView
            card_category = itemView.findViewById(R.id.card_category) as CardView

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            iOnRecyclerViewItemClickListener.onClick(view, adapterPosition)
        }

    }
}


