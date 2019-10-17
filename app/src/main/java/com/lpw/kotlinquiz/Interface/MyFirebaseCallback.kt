package com.lpw.kotlinquiz.Interface

import com.lpw.kotlinquiz.Model.Question

interface MyFirebaseCallback {
    fun setQuestionList(questionList:List<Question>)
}