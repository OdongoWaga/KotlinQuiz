package com.lpw.kotlinquiz.UI

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.lpw.kotlinquiz.Adapter.CategoryAdapter
import com.lpw.kotlinquiz.Common.Common
import com.lpw.kotlinquiz.Common.SpaceItemDecoration
import com.lpw.kotlinquiz.DBHelper.DBHelper
import com.lpw.kotlinquiz.R
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.category,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.action_settings){
            showSettings()
        }
        return true
    }

    private fun showSettings() {
        val settings_layout = LayoutInflater.from(this)
            .inflate(R.layout.settings_layout,null)

        val ckb_online_mode = settings_layout.findViewById(R.id.ckb_online_mode)as CheckBox

        ckb_online_mode.isChecked = Paper.book().read(Common.KEY_ONLINE_MODE, false)

        MaterialStyledDialog.Builder(this)
            .setIcon(R.drawable.ic_settings_white_24dp)
            .setTitle("Settings")
            .setDescription("Please choose action")
            .setCustomView(settings_layout)
            .setNegativeText("Dismiss")
            .onNegative { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveText("Save")
            .onPositive { _,_ ->

                Common.isOnline = ckb_online_mode.isChecked
                Paper.book().write(Common.KEY_ONLINE_MODE, Common.isOnline)


            }.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(this)

        Common.isOnline = Paper.book().read(Common.KEY_ONLINE_MODE,false)

        toolbar.title = "Test"
        setSupportActionBar(toolbar)

        recycler_category.setHasFixedSize(true)
        recycler_category.layoutManager = GridLayoutManager(this,2)

        val adapter = CategoryAdapter(this, DBHelper.getInstance(this).allCategories)
        recycler_category.addItemDecoration(SpaceItemDecoration(4))
        recycler_category.adapter = adapter
    }
}
