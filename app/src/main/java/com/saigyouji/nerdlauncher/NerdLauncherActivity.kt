package com.saigyouji.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

private const val TAG = "NerdLauncherActivity"
class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(this)
        setupAdapter()
    }
    private fun setupAdapter(){
        val startupIntent = Intent(Intent.ACTION_MAIN).apply { 
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(startupIntent, 0)
        activities.sortWith(Comparator{a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })
        Log.i(TAG, "Found ${activities.size} activities")
        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(val itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

        private val nameTextView: TextView = itemView.findViewById(R.id.activity_text)
        private val image: ImageView = itemView.findViewById(R.id.activity_img)
        private lateinit var resolveInfo: ResolveInfo

        init{
            itemView.setOnClickListener(this)
        }
        fun bindActivity(resolveInfo: ResolveInfo){
            this.resolveInfo = resolveInfo
            val pm = itemView.context.packageManager
            val app_nm = resolveInfo.loadLabel(pm).toString()
            nameTextView.text = app_nm
            image.setImageDrawable(resolveInfo.loadIcon(pm))
        }

        override fun onClick(v: View) {
            val act_info = resolveInfo.activityInfo

            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(act_info.applicationInfo.packageName, act_info.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = v.context
            context.startActivity(intent)
        }
    }
    private class ActivityAdapter(val activities: List<ResolveInfo>) :
        RecyclerView.Adapter<ActivityHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val v = layoutInflater
                .inflate(R.layout.item_activitity, parent, false)
            return ActivityHolder(v)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val rsv_info = activities[position]
            holder.bindActivity(rsv_info)
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }
}