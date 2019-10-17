package com.lpw.kotlinquiz.DBHelper

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.firebase.database.*
import com.lpw.kotlinquiz.Interface.MyFirebaseCallback
import com.lpw.kotlinquiz.Model.Question
import dmax.dialog.SpotsDialog

class OnlineDBHelper (internal var context: Context,
                      internal var firebaseDatabase: FirebaseDatabase){
    internal var quiz: DatabaseReference;

    init {
        quiz = this.firebaseDatabase.getReference("EDMTQuiz")
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        private var instance:OnlineDBHelper?=null

        @Synchronized
        fun getInstance(context: Context,firebaseDatabase: FirebaseDatabase):OnlineDBHelper{
            if(instance==null)
                instance = OnlineDBHelper(context,firebaseDatabase)
            return instance!!
        }
    }

    fun readData(myCallback: MyFirebaseCallback, category:String)
    {
        val dialog = SpotsDialog.Builder().setContext(context)
            .setCancelable(false)
            .build()

        if(!dialog.isShowing)
            dialog.show()

        quiz.child(category)
            .child("question")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(context,""+p0.message,Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val questionList = ArrayList<Question>()
                    for (questionSnapShot in p0.children)
                        questionList.add(questionSnapShot.getValue(Question::class.java)!!)
                    myCallback.setQuestionList(questionList)
                }
            })

        if(dialog.isShowing)
            dialog.dismiss()
    }
}