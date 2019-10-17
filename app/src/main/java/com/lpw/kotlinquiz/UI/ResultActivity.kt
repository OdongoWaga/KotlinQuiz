package com.lpw.kotlinquiz.UI

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.GridLayoutManager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.lpw.kotlinquiz.Adapter.ResultGridAdapter
import com.lpw.kotlinquiz.Common.Common
import com.lpw.kotlinquiz.Common.SpaceItemDecoration
import com.lpw.kotlinquiz.R
import kotlinx.android.synthetic.main.activity_main_question.*
import kotlinx.android.synthetic.main.activity_result.*
import java.util.concurrent.TimeUnit

class ResultActivity : AppCompatActivity() {

    private var backToQuestion:BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent!!.action!!.toString() == Common.KEY_BACK_FROM_RESULT){
                val questionIndex = intent.getIntExtra(Common.KEY_BACK_FROM_RESULT,-1)
                goBackActivityWithoutQuestionIndex(questionIndex)
            }
        }

    }

    private fun goBackActivityWithoutQuestionIndex(questionIndex: Int) {
        val returnIntent = Intent()
        returnIntent.putExtra(Common.KEY_BACK_FROM_RESULT,questionIndex)
        setResult(Activity.RESULT_OK,returnIntent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId)
        {
            R.id.menu_do_quiz_again -> doQuizAgain()
            R.id.menu_view_answer -> viewAnswer()
            android.R.id.home ->{
                val intent = Intent(applicationContext,MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
        return true
    }

    private fun viewAnswer() {
        val returnIntent = Intent()
        returnIntent.putExtra("action" , "viewanswer")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun doQuizAgain() {
        MaterialStyledDialog.Builder(this)
            .setTitle("Do quiz again?")
            .setDescription("do you really want to delete this data?")
            .setIcon(R.drawable.ic_mood_white_24dp)
            .setNegativeText("No")
            .onNegative { dialog, _ ->  dialog.dismiss()}
            .setPositiveText("Yes")
            .onPositive { _, _ ->

                val returnIntent = Intent()
                returnIntent.putExtra("action" , "doquizagain")
                setResult(Activity.RESULT_OK, returnIntent)
                finish()

            }.show()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(backToQuestion)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(backToQuestion, IntentFilter(Common.KEY_BACK_FROM_RESULT))

        toolbar.title = "Result"
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        txt_time.text = java.lang.String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(Common.timer.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(Common.timer.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(Common.timer.toLong())
            ))

        txt_right_answer.text = "${Common.right_answer_count}/${Common.questionList.size}"

        btn_filter_total.text = "${Common.questionList.size}"
        btn_filter_right.text = "${Common.right_answer_count}"
        btn_filter_wrong.text = "${Common.wrong_answer_count}"
        btn_no_answer.text = "${Common.no_answer_count}"

        val percent = Common.right_answer_count * 100 / Common.questionList.size
        if (percent > 80)
            txt_result.text = "EXCELLENT"
        else if (percent > 70)
            txt_result.text = "GOOD"
        else if (percent > 60)
            txt_result.text = "FAIR"
        else if (percent > 50)
            txt_result.text = "POOR"
        else if (percent > 40)
            txt_result.text = "BAD"
        else
            txt_result.text = "FAILING"

        btn_filter_total.setOnClickListener {
            val adapter = ResultGridAdapter(this, Common.answerSheetList)
            recycler_result.adapter = adapter
        }

        btn_no_answer.setOnClickListener {
            Common.answerSheetListFiltered.clear()

            for (currentQuestion in Common.answerSheetList)
                if(currentQuestion.type == Common.ANSWER_TYPE.NO_ANSWER)
                    Common.answerSheetListFiltered.add(currentQuestion)

            val adapter = ResultGridAdapter(this, Common.answerSheetListFiltered)
            recycler_result.adapter = adapter
        }

        btn_filter_wrong.setOnClickListener {
            Common.answerSheetListFiltered.clear()

            for (currentQuestion in Common.answerSheetList)
                if(currentQuestion.type == Common.ANSWER_TYPE.WRONG_ANSWER)
                    Common.answerSheetListFiltered.add(currentQuestion)

            val adapter = ResultGridAdapter(this, Common.answerSheetListFiltered)
            recycler_result.adapter = adapter
        }

        btn_filter_right.setOnClickListener {
            Common.answerSheetListFiltered.clear()

            for (currentQuestion in Common.answerSheetList)
                if(currentQuestion.type == Common.ANSWER_TYPE.RIGHT_ANSWER)
                    Common.answerSheetListFiltered.add(currentQuestion)

            val adapter = ResultGridAdapter(this, Common.answerSheetListFiltered)
            recycler_result.adapter = adapter
        }

        val adapter = ResultGridAdapter(this, Common.answerSheetList)
        recycler_result.setHasFixedSize(true)
        recycler_result.layoutManager = GridLayoutManager(this, 4)
        recycler_result.addItemDecoration(SpaceItemDecoration(4))
        recycler_result.adapter = adapter
    }
}
