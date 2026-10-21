package com.visiontv.launcher

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.visiontv.launcher.databinding.ActivityAllAppsBinding

class AllAppsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllAppsBinding

    data class AppItem(val name: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllAppsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackFromDrawer.setOnClickListener { finish() }

        binding.recyclerAllAppsGrid.layoutManager = GridLayoutManager(this, 5)

        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        val pm = packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val leanbackIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        val allResolves = (pm.queryIntentActivities(mainIntent, 0) + pm.queryIntentActivities(leanbackIntent, 0))
            .distinctBy { it.activityInfo.packageName }
            .filter { it.activityInfo.packageName != packageName } // Exclude this launcher itself
            .sortedBy { it.loadLabel(pm).toString().lowercase() }

        val appItems = allResolves.map {
            AppItem(
                name = it.loadLabel(pm).toString(),
                packageName = it.activityInfo.packageName,
                icon = it.loadIcon(pm)
            )
        }

        binding.txtAppCountHeader.text = "${appItems.size} APPS INSTALLED"

        binding.recyclerAllAppsGrid.adapter = object : RecyclerView.Adapter<AppViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_drawer_app, parent, false)
                return AppViewHolder(v)
            }

            override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
                val item = appItems[position]
                holder.txtName.text = item.name
                holder.imgIcon.setImageDrawable(item.icon)

                holder.itemView.setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        view.animate().scaleX(1.08f).scaleY(1.08f).translationZ(16f).setDuration(160).start()
                    } else {
                        view.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(160).start()
                    }
                }

                holder.itemView.setOnClickListener {
                    val launchIntent = pm.getLeanbackLaunchIntentForPackage(item.packageName)
                        ?: pm.getLaunchIntentForPackage(item.packageName)
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                    }
                }
            }

            override fun getItemCount(): Int = appItems.size
        }
    }

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.imgAppIcon)
        val txtName: TextView = view.findViewById(R.id.txtAppName)
    }
}
