package com.eventhorizonwebdesign.chandownlaoder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.eventhorizonwebdesign.chandownlaoder.ui.DownloadListAdapter
import com.eventhorizonwebdesign.chandownlaoder.ui.DownloadListModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var downloadListView: ListView
    private val downloadModelList = ArrayList<DownloadListModel>()
    private lateinit var adapter: DownloadListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        downloadListView = findViewById<ListView>(R.id.downloadList) as ListView
        fab.setOnClickListener {
            val mDialog = AlertDialog.Builder(this)
            val mView = layoutInflater.inflate(R.layout.content_add_dialog, null)
            val urlInputField = mView.findViewById<EditText>(R.id.threadURLInput)
            val addButton = mView.findViewById<Button>(R.id.addThreadButton) as Button
            mDialog.setView(mView)
            val addDialog = mDialog.create()
            addDialog.show()
            addButton.setOnClickListener {
                downloadModelList.add(DownloadListModel(urlInputField.text.toString(), urlInputField.text.toString().substring( urlInputField.text.toString().lastIndexOf("/") + 1,  urlInputField.text.toString().length), 0, downloadListView, adapter))
                Snackbar.make(findViewById(R.id.mainLayout), "URL added to list", Snackbar.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
                downloadListView.invalidateViews()
                addDialog.dismiss()
            }
        }
        adapter = DownloadListAdapter(downloadModelList, applicationContext)
        downloadListView.adapter = adapter
        val mGetLinksButton = findViewById<Button>(R.id.getLinksButton)
        mGetLinksButton.setOnClickListener {
            for (model: DownloadListModel in downloadModelList){
                model.populateLinksList()
            }
        }
        val mDownloadButton = findViewById<Button>(R.id.downloadButton)
        mDownloadButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                for (model: DownloadListModel in downloadModelList) {
                    model.download()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    for (model: DownloadListModel in downloadModelList) {
                        model.download()
                    }
                } else {
                    //Error
                    Snackbar.make(findViewById(R.id.mainLayout), "SD card access needed :(", Snackbar.LENGTH_SHORT).show()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
