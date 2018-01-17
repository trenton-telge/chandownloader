package com.eventhorizonwebdesign.chandownlaoder.ui

import android.content.Context
import android.graphics.Color.rgb
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.eventhorizonwebdesign.chandownlaoder.R




/**
 * Created by Alienware on 1/14/2018.
 */
class DownloadListAdapter(private val dataSet: ArrayList<DownloadListModel>, private var mContext: Context) : ArrayAdapter<DownloadListModel>(mContext, R.layout.thread_list_item, dataSet), View.OnClickListener {

    private var lastPosition = -1

    // View lookup cache
    private class ViewHolder {
        internal var threadContainer: ConstraintLayout? = null
        internal var threadID: TextView? = null
        internal var threadProgress: ProgressBar? = null
        internal var statusView: TextView? = null
    }

    override fun onClick(v: View) {
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var localConvertView = convertView
        // Get the data item for this position
        val dataModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        if (localConvertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            localConvertView = inflater.inflate(R.layout.thread_list_item, parent, false)
            viewHolder.threadContainer = localConvertView!!.findViewById(R.id.threadContainer)
            viewHolder.threadID = localConvertView.findViewById(R.id.threadIDDisplay)
            viewHolder.threadProgress = localConvertView.findViewById(R.id.threadprogress)
            viewHolder.statusView = localConvertView.findViewById(R.id.statusIndicator)

            localConvertView.tag = viewHolder
        } else {
            viewHolder = localConvertView.tag as ViewHolder
        }

        lastPosition = position

        viewHolder.threadID!!.text = dataModel!!.id
        viewHolder.threadProgress!!.max = dataModel.max
        viewHolder.threadProgress!!.progress = dataModel.progress
        if (!dataModel.linksGrabbed){
            viewHolder.statusView!!.text = context.getString(R.string.get_links)
            viewHolder.statusView!!.setBackgroundColor(rgb(127, 29, 29))
        } else {
            if (!dataModel.imagesGrabbed){
                viewHolder.statusView!!.text = context.getString(R.string.ready)
                viewHolder.statusView!!.setBackgroundColor(rgb(135, 126, 9))
            } else {
                viewHolder.statusView!!.text = context.getString(R.string.complete)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewHolder.statusView!!.setBackgroundColor(context.getColor(R.color.colorPrimary))
                }
            }
        }
        if (dataModel.working){
            viewHolder.statusView!!.text = context.getString(R.string.working)
            viewHolder.statusView!!.setBackgroundColor(rgb(0, 43, 114))
        }
        viewHolder.threadProgress!!.isIndeterminate = dataModel.indeterminate
        // Return the completed view to render on screen
        return localConvertView
    }
}