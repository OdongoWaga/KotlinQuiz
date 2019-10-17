package com.lpw.kotlinquiz.Interface

import com.lpw.kotlinquiz.Model.CurrentQuestion

interface IAnswerSelect {
    fun selectedAnswer():CurrentQuestion
    fun showCorrectAnswer()
    fun disableAnswer()
    fun resetQuestion()
}