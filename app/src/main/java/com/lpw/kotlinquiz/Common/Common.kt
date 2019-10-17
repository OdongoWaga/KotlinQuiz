package com.lpw.kotlinquiz.Common

import com.lpw.kotlinquiz.Fragments.QuestionFragment
import com.lpw.kotlinquiz.Model.Category
import com.lpw.kotlinquiz.Model.CurrentQuestion
import com.lpw.kotlinquiz.Model.Question

object Common {
    val TOTAL_TIME = 20 * 60 * 1000

    var answerSheetListFiltered:MutableList<CurrentQuestion> = ArrayList()
    var answerSheetList:MutableList<CurrentQuestion> = ArrayList()
    var questionList:MutableList<Question> = ArrayList()
    var selectedCategory:Category?=null

    var fragmentList:MutableList<QuestionFragment> = ArrayList()

    var selected_values:MutableList<String> = ArrayList()

    var timer = 0
    var right_answer_count = 0
    var wrong_answer_count = 0
    var no_answer_count = 0
    var data_question = StringBuilder()
    val KEY_GO_TO_QUESTION: String?="position_go_to"
    val KEY_BACK_FROM_RESULT: String?="back_from_result"
    val KEY_ONLINE_MODE: String?="ONLINE_MODE"
    var isOnline = false

    enum class ANSWER_TYPE{
        NO_ANSWER,
        RIGHT_ANSWER,
        WRONG_ANSWER
    }
}