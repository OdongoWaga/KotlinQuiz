package com.lpw.kotlinquiz.Model

class Question {
    var id: Int = 0
    var questionText: String? = null
    var questionImage: String? = null
    var answerA: String? = null
    var answerB: String? = null
    var answerC: String? = null
    var answerD: String? = null
    var correctAnswer: String? = null
    var isImageQuestion: Boolean = false
    var categoryId: Int = 0

   constructor(){}

    constructor(
        id: Int,
        questionText: String?,
        questionImage: String?,
        answerA: String?,
        answerB: String?,
        answerC: String?,
        answerD: String?,
        correctAnswer: String?,
        isImageQuestion: Boolean,
        categoryId: Int
    ) {
        this.id = id
        this.questionText = questionText
        this.questionImage = questionImage
        this.answerA = answerA
        this.answerB = answerB
        this.answerC = answerC
        this.answerD = answerD
        this.correctAnswer = correctAnswer
        this.isImageQuestion = isImageQuestion
        this.categoryId = categoryId
    }


}