package com.eventhorizonwebdesign.chandownlaoder.ui

import android.os.AsyncTask
import android.os.Environment
import android.widget.ListView
import org.apache.commons.io.FileUtils
import org.jsoup.Jsoup
import java.io.File
import java.net.URL
import java.util.*






/**
 * Created by Alienware on 1/14/2018.
 */

class DownloadListModel(var url: String, var id: String, var progress: Int, var view: ListView, var adapter: DownloadListAdapter){
    var imageLinksList = Vector<String>()
    var linksGrabbed = false
    var imagesGrabbed = false
    var working = false
    var indeterminate = false
    var max = 0
    class DownloadManagerTask: AsyncTask<DownloadListModel, Int, DownloadListModel>() {
        var p = 0
        lateinit var el: DownloadListModel
        override fun onPreExecute() {
            el.working = true
            el.adapter.notifyDataSetChanged()
            el.view.invalidateViews()
        }
        override fun doInBackground(vararg params: DownloadListModel): DownloadListModel {
            el = params[0]
            val downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
            val downloadsParentPath = downloadsPath + System.getProperty("file.separator") + params[0].id
            File(downloadsParentPath).mkdirs()
            for (path: String in params[0].imageLinksList) {
                //download
                FileUtils.copyURLToFile(URL(path), File(downloadsParentPath + System.getProperty("file.separator") + File(path).name))
                //increment progress with publishProgress(int)
                p += 1
                publishProgress(p)
            }
            params[0].imagesGrabbed = true
            return params[0]
        }

        override fun onProgressUpdate(vararg values: Int?) {
            el.progress = values[0]!!
            el.adapter.notifyDataSetChanged()
            el.view.invalidateViews()
        }

        override fun onPostExecute(result: DownloadListModel) {
            result.working = false
            result.progress = result.max
            result.adapter.notifyDataSetChanged()
            result.view.invalidateViews()
        }
    }
    class PopulateLinksListTask: AsyncTask<DownloadListModel, Int, DownloadListModel>() {
        lateinit var el: DownloadListModel
        override fun onPreExecute() {
            el.indeterminate = true
            el.working = true
            el.adapter.notifyDataSetChanged()
            el.view.invalidateViews()
        }
        override fun doInBackground(vararg params: DownloadListModel): DownloadListModel {
            val doc = Jsoup.connect(params[0].url).get()
            val links = doc.select("a[href]") //href of a, not src of img, will give full res
            links
                    .map { it.attr("abs:href") }
                    .filter {
                        it.contains(".jpg", true) ||
                                it.contains(".png", true) ||
                                it.contains(".webm", true) ||
                                it.contains(".mp4", true) ||
                                it.contains(".webmv", true)
                    }
                    .forEach { params[0].addLinkToList(it) }
            params[0].linksGrabbed = true
            params[0].max = params[0].imageLinksList.size
            return params[0]
        }

        override fun onPostExecute(result: DownloadListModel) {
            result.indeterminate = false
            result.working = false
            result.adapter.notifyDataSetChanged()
            result.view.invalidateViews()
        }

    }
    fun populateLinksList(){
        PopulateLinksListTask().execute(this)
    }
    fun download(){
        DownloadManagerTask().execute(this)
    }
    fun addLinkToList(url: String){
        imageLinksList.addElement(url)
    }
}