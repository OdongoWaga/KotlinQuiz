package com.lpw.kotlinquiz.UI

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.lpw.kotlinquiz.Adapter.GridAnswerAdapter
import com.lpw.kotlinquiz.Adapter.MyFragmentAdapter
import com.lpw.kotlinquiz.Adapter.QusetionListHelperAdapter
import com.lpw.kotlinquiz.Common.Common
import com.lpw.kotlinquiz.Common.SpaceItemDecoration
import com.lpw.kotlinquiz.DBHelper.DBHelper
import com.lpw.kotlinquiz.DBHelper.OnlineDBHelper
import com.lpw.kotlinquiz.Fragments.QuestionFragment
import com.lpw.kotlinquiz.Interface.MyFirebaseCallback
import com.lpw.kotlinquiz.Model.CurrentQuestion
import com.lpw.kotlinquiz.Model.Question
import com.lpw.kotlinquiz.R
import kotlinx.android.synthetic.main.activity_main_question.*
import kotlinx.android.synthetic.main.content_main_question.*
import java.util.concurrent.TimeUnit

class MainQuestionActivity : AppCompatActivity(){

    val CODE_GET_RESULT = 9999


    var time_play = Common.TOTAL_TIME

    var isAnswerModeView = false

    lateinit var countDownTimer: CountDownTimer
    lateinit var adapter : GridAnswerAdapter
    lateinit var questionHelperAdapter: QusetionListHelperAdapter

    lateinit var txt_wrong_answer:TextView

    lateinit var  recycler_helper_answer_sheet:RecyclerView

    private var goToQuestionNum:BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
          if(intent!!.action!!.toString()==Common.KEY_GO_TO_QUESTION){
              val question = intent.getIntExtra(Common.KEY_GO_TO_QUESTION, -1)
              if(question != -1){
                  view_pager.currentItem = question
                  drawer_layout.closeDrawer(Gravity.START)
              }
          }
        }

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToQuestionNum)
        if(countDownTimer!=null)
            countDownTimer!!.cancel()
        if(Common.fragmentList != null)
            Common.fragmentList.clear()
        if (Common.answerSheetList!=null)
            Common.answerSheetList.clear()
        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_question)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        LocalBroadcastManager.getInstance(this).registerReceiver(goToQuestionNum, IntentFilter(Common.KEY_GO_TO_QUESTION))

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        recycler_helper_answer_sheet = navView.getHeaderView(0).findViewById<View>(R.id.answer_sheet)as RecyclerView
        recycler_helper_answer_sheet.setHasFixedSize(true)
        recycler_helper_answer_sheet.layoutManager = GridLayoutManager(this,3)
        recycler_helper_answer_sheet.addItemDecoration(SpaceItemDecoration(2))

        val btnDone =  navView.getHeaderView(0).findViewById<View>(R.id.btn_done)as Button
        btnDone.setOnClickListener {
            if(!isAnswerModeView)
            {
                MaterialStyledDialog.Builder(this)
                    .setTitle("Finish?")
                    .setDescription("do you really wont to finish?")
                    .setIcon(R.drawable.ic_mood_white_24dp)
                    .setNegativeText("No")
                    .onNegative { dialog, _ ->  dialog.dismiss()}
                    .setPositiveText("Yes")
                    .onPositive { _, _ ->  finishGame()
                        drawer_layout.closeDrawer(Gravity.LEFT)
                    }.show()
            }else
                finishGame()
        }

        genQuestion()
    }



    private fun countCorrectAnswer() {
        Common.right_answer_count = 0
        Common.wrong_answer_count = 0

        for (item in Common.answerSheetList)
            if (item.type == Common.ANSWER_TYPE.RIGHT_ANSWER)
                Common.right_answer_count++
        else if(item.type == Common.ANSWER_TYPE.WRONG_ANSWER)
                Common.wrong_answer_count++
    }

    private fun genFragmentList() {
        for (i in Common.questionList.indices){
            val bundle = Bundle()
            bundle.putInt("index",i)
            val fragment = QuestionFragment()
            fragment.arguments = bundle

            Common.fragmentList.add(fragment)
        }
    }

    private fun genItems() {
        for(i in Common.questionList.indices){
            Common.answerSheetList.add(CurrentQuestion(i, Common.ANSWER_TYPE.NO_ANSWER))
        }
    }

    private fun genQuestion() {
        if(!Common.isOnline){
            Common.questionList = DBHelper.getInstance(this).getQuestionByCategories(Common.selectedCategory!!.id)

            if (Common.questionList.size==0){
                MaterialStyledDialog.Builder(this)
                    .setTitle("Ooops!")
                    .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                    .setDescription("We don't have any question in this ${Common.selectedCategory!!.name} category")
                    .setPositiveText("Ok")
                    .onPositive{ dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }.show()
            }
            else
                setupQuestion()

        }else{
            OnlineDBHelper.getInstance(this, FirebaseDatabase.getInstance())
                .readData(object:MyFirebaseCallback{
                    override fun setQuestionList(questionList: List<Question>) {
                        Common.questionList.clear()
                        Common.questionList = questionList as MutableList<Question>

                        if (Common.questionList.size==0){
                            MaterialStyledDialog.Builder(this@MainQuestionActivity)
                                .setTitle("Ooops!")
                                .setIcon(R.drawable.ic_sentiment_dissatisfied_black_24dp)
                                .setDescription("We don't have any question in this ${Common.selectedCategory!!.name} category")
                                .setPositiveText("Ok")
                                .onPositive{ dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                }.show()
                        }
                        else
                            setupQuestion()

                    }


                },Common.selectedCategory!!.name!!.replace(" ","")
                    .replace("/","_"))
        }



    }

    @SuppressLint("SetTextI18n")
    private fun setupQuestion(){
        if(Common.questionList.size > 0){
            txt_timer.visibility = View.VISIBLE
            txt_right_answer.visibility = View.VISIBLE

            countTimer()
            genItems()
            grid_answer.setHasFixedSize(true)
            if(Common.questionList.size > 0)
                grid_answer.layoutManager = GridLayoutManager(this,
                    if(Common.questionList.size>5)Common.questionList.size/2
                    else Common.questionList.size)

            adapter = GridAnswerAdapter(this,Common.answerSheetList)
            grid_answer.adapter = adapter

            //generate fragment list
            genFragmentList()

            val fragmentAdapter = MyFragmentAdapter(supportFragmentManager, this, Common.fragmentList)
            view_pager.offscreenPageLimit = Common.questionList.size
            view_pager.adapter = fragmentAdapter

            sliding_tabs.setupWithViewPager(view_pager)

            //event
            view_pager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{

                val SCROLLING_RIGHT = 0
                val SCROLLING_LEFT = 1
                val SCROLLING_UBDETERMINED = 2

                var currentScrollDirection = SCROLLING_UBDETERMINED

                private val isScrollingDirectionUndetermined:Boolean
                    get() = currentScrollDirection == SCROLLING_UBDETERMINED

                private val isScrollingDirectionRight:Boolean
                    get() = currentScrollDirection == SCROLLING_RIGHT

                private val isScrollingDirectionLeft:Boolean
                    get() = currentScrollDirection == SCROLLING_LEFT

                private fun setScrollingDirection(positionOffset:Float){
                    if(1-positionOffset >=.5f)
                        this.currentScrollDirection = SCROLLING_RIGHT
                    else if(1-positionOffset <=.5f)
                        this.currentScrollDirection = SCROLLING_LEFT
                }

                override fun onPageScrollStateChanged(p0: Int) {
                    if(p0 == ViewPager.SCROLL_STATE_IDLE)
                        this.currentScrollDirection = SCROLLING_UBDETERMINED
                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                    if(isScrollingDirectionUndetermined)
                        setScrollingDirection(p1)
                }

                override fun onPageSelected(pos: Int) {
                    val questionFragment: QuestionFragment
                    var position = 0
                    if(pos > 0){
                        if (isScrollingDirectionRight)
                        {
                            questionFragment = Common.fragmentList[pos-1]
                            position = pos - 1
                        }else if(isScrollingDirectionLeft){
                            questionFragment = Common.fragmentList[pos + 1]
                            position = pos + 1
                        }else{
                            questionFragment = Common.fragmentList[pos]
                        }
                    }else{
                        questionFragment = Common.fragmentList[0]
                        position = 0
                    }

                    if(Common.answerSheetList[position].type == Common.ANSWER_TYPE.NO_ANSWER)
                    {
                        val question_state: CurrentQuestion = questionFragment.selectedAnswer()
                        Common.answerSheetList[position] = question_state
                        adapter.notifyDataSetChanged()
                        questionHelperAdapter.notifyDataSetChanged()

                        countCorrectAnswer()

                        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size}")
                        txt_wrong_answer.text = ("${Common.wrong_answer_count}")

                        if(question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
                        {
                            questionFragment.showCorrectAnswer()
                            questionFragment.disableAnswer()
                        }
                    }
                }
            })
            txt_right_answer.text = "${Common.right_answer_count}/${Common.questionList.size}"
            questionHelperAdapter = QusetionListHelperAdapter(this, Common.answerSheetList)
            recycler_helper_answer_sheet.adapter = questionHelperAdapter
        }
    }

    private fun countTimer() {
        countDownTimer = object:CountDownTimer(Common.TOTAL_TIME.toLong(),1000){
            override fun onFinish() {
                finishGame()
            }


            override fun onTick(interval: Long) {
                txt_timer.text = (java.lang.String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(interval),
                    TimeUnit.MILLISECONDS.toSeconds(interval) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval))))
                time_play-=1000
            }
        }.start()
    }

    private fun finishGame() {
        val position = view_pager.currentItem
        val questionFragment = Common.fragmentList[position]

        val question_state: CurrentQuestion = questionFragment.selectedAnswer()

        Common.answerSheetList[position] = question_state

        adapter.notifyDataSetChanged()
        questionHelperAdapter.notifyDataSetChanged()

        countCorrectAnswer()

        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size}")
        txt_wrong_answer.text = ("${Common.wrong_answer_count}")

        if(question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
        {
            questionFragment.showCorrectAnswer()
            questionFragment.disableAnswer()
        }

        val intent = Intent(this, ResultActivity::class.java)
        Common.timer = Common.TOTAL_TIME - time_play
        Common.no_answer_count = Common.questionList.size - (Common.right_answer_count+Common.wrong_answer_count)
        Common.data_question = StringBuilder(Gson().toJson(Common.answerSheetList))

        startActivityForResult(intent, CODE_GET_RESULT)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            this.finish()
            super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item:MenuItem = menu!!.findItem(R.id.menu_wrong_answer)
        val layout = item.actionView as ConstraintLayout
        txt_wrong_answer = layout.findViewById(R.id.txt_wrong_answer) as TextView
        txt_wrong_answer.text = 0.toString()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.question, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.menu_done -> {
                if(!isAnswerModeView)
                {
                    MaterialStyledDialog.Builder(this)
                        .setTitle("Finish?")
                        .setDescription("do you really want to finish?")
                        .setIcon(R.drawable.ic_mood_white_24dp)
                        .setNegativeText("No")
                        .onNegative { dialog, _ ->  dialog.dismiss()}
                        .setPositiveText("Yes")
                        .onPositive { _, _ ->  finishGame()
                            drawer_layout.closeDrawer(Gravity.START)
                        }.show()
                }else
                    finishGame()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE_GET_RESULT){
            if(resultCode == Activity.RESULT_OK){
                val action = data!!.getStringExtra("action")
                if (action == null || TextUtils.isEmpty(action)){
                    val questionIndex = data.getIntExtra(Common.KEY_BACK_FROM_RESULT, -1)
                    view_pager.currentItem = questionIndex

                    isAnswerModeView = true
                    countDownTimer.cancel()

                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility = View.GONE
                    txt_timer.visibility = View.GONE
                }
                else
                {
                    if (action == "doquizagain")
                    {
                        view_pager.currentItem = 0
                        isAnswerModeView = false

                        txt_wrong_answer.visibility = View.VISIBLE
                        txt_right_answer.visibility = View.VISIBLE
                        txt_timer.visibility = View.VISIBLE

                        for (i in Common.fragmentList.indices)
                        {
                            Common.fragmentList[i].resetQuestion()
                        }

                        for (i in Common.answerSheetList.indices)
                            Common.answerSheetList[i].type = Common.ANSWER_TYPE.NO_ANSWER

                        adapter.notifyDataSetChanged()
                        questionHelperAdapter.notifyDataSetChanged()

                        countTimer()
                    }
                    else if(action == "viewanswer")
                    {
                        view_pager.currentItem = 0
                        isAnswerModeView = true
                        countDownTimer.cancel()

                        txt_wrong_answer.visibility = View.GONE
                        txt_right_answer.visibility = View.GONE
                        txt_timer.visibility = View.GONE

                        for (i in Common.fragmentList.indices)
                        {
                            Common.fragmentList[i].showCorrectAnswer()
                            Common.fragmentList[i].disableAnswer()
                        }
                    }
                }
            }
        }
    }
}
