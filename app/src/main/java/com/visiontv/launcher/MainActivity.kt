package com.visiontv.launcher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.visiontv.launcher.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val clockHandler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    // 5 Scenic Pexels Nature Wallpapers
    private val natureWallpapers = listOf(
        R.drawable.bg_nature_1,
        R.drawable.bg_nature_2,
        R.drawable.bg_nature_3,
        R.drawable.bg_nature_4,
        R.drawable.bg_nature_5
    )
    private var currentWallpaperIdx = 0
    private val wallpaperHandler = Handler(Looper.getMainLooper())

    private val clockRunnable = object : Runnable {
        override fun run() {
            binding.tvLiveClock.text = timeFormat.format(Date())
            clockHandler.postDelayed(this, 1000)
        }
    }

    private val wallpaperRunnable = object : Runnable {
        override fun run() {
            crossfadeToNextWallpaper()
            wallpaperHandler.postDelayed(this, 45000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppIcons()
        setupFocusListeners()
        setupClickListeners()

        binding.bgNatureImageBack.setImageResource(natureWallpapers[0])
        binding.bgNatureImageFront.setImageResource(natureWallpapers[1])
        binding.bgNatureImageFront.alpha = 0f

        clockHandler.post(clockRunnable)
        wallpaperHandler.postDelayed(wallpaperRunnable, 45000)

        // Fetch live animated weather
        fetchLiveWeather()

        // Subtle gentle breathing animation on the weather icon
        animateWeatherIcon()

        // Default focus to YouTube
        binding.cardYouTube.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        clockHandler.removeCallbacks(clockRunnable)
        wallpaperHandler.removeCallbacks(wallpaperRunnable)
    }

    private fun animateWeatherIcon() {
        binding.imgWeatherCondition.animate()
            .scaleX(1.05f).scaleY(1.05f)
            .translationY(-4f)
            .setDuration(2200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.imgWeatherCondition.animate()
                        .scaleX(0.96f).scaleY(0.96f)
                        .translationY(4f)
                        .setDuration(2200)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                animateWeatherIcon()
                            }
                        })
                }
            })
    }

    private fun fetchLiveWeather() {
        thread {
            try {
                // Open-Meteo for Kollam / Kerala: Lat 8.8833, Long 76.6000
                val url = URL("https://api.open-meteo.com/v1/forecast?latitude=8.8833&longitude=76.6&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val jsonStr = conn.inputStream.bufferedReader().readText()
                val root = JSONObject(jsonStr)
                val current = root.getJSONObject("current")

                val temp = current.getDouble("temperature_2m").toInt()
                val apparent = current.getDouble("apparent_temperature").toInt()
                val humidity = current.getInt("relative_humidity_2m")
                val wind = current.getDouble("wind_speed_10m").toInt()
                val code = current.getInt("weather_code")

                // Map WMO weather codes to condition text & graphics
                val (condText, iconRes) = when (code) {
                    0 -> "Clear Sky • Warm" to R.drawable.ic_weather_sun_cloud
                    1, 2, 3 -> "Partly Cloudy • Pleasant" to R.drawable.ic_weather_sun_cloud
                    51, 53, 55, 61, 63, 65, 80, 81, 82 -> "Rain Showers • Coastal Breeze" to R.drawable.ic_weather_rain_animated
                    95, 96, 99 -> "Thunderstorm • Heavy Showers" to R.drawable.ic_weather_rain_animated
                    else -> "Tropical Climate • Cloudy" to R.drawable.ic_weather_sun_cloud
                }

                runOnUiThread {
                    binding.tvWeatherTemp.text = "$temp°"
                    binding.tvWeatherCondition.text = condText
                    binding.tvWeatherSubtext.text = "Feels like $apparent°C • Humidity $humidity% • Wind $wind km/h"
                    binding.tvWeatherLocation.text = "Kollam, Kerala • Live Satellite Telemetry"
                    binding.tvLocationHeader.text = "📍 Kollam, Kerala • $temp°C"
                    binding.imgWeatherCondition.setImageResource(iconRes)
                }
            } catch (e: Exception) {
                // Safe offline fallback
                runOnUiThread {
                    binding.tvWeatherTemp.text = "28°"
                    binding.tvWeatherCondition.text = "Rain Showers • Coastal Kerala"
                    binding.tvWeatherSubtext.text = "Feels like 32°C • Humidity 88% • Wind 7 km/h"
                }
            }
        }
    }

    private fun crossfadeToNextWallpaper() {
        currentWallpaperIdx = (currentWallpaperIdx + 1) % natureWallpapers.size
        val nextRes = natureWallpapers[currentWallpaperIdx]

        binding.bgNatureImageFront.setImageResource(nextRes)
        binding.bgNatureImageFront.animate()
            .alpha(1f)
            .setDuration(2000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.bgNatureImageBack.setImageResource(nextRes)
                    binding.bgNatureImageFront.alpha = 0f
                }
            })
    }

    private fun setupAppIcons() {
        loadIconForPackage(listOf("org.smarttube.stable", "app.rvx.android.youtube", "com.google.android.youtube.tv", "com.google.android.youtube"), binding.imgYouTube)
        loadIconForPackage(listOf("in.startv.hotstar", "in.startv.hotstar.dplus"), binding.imgHotstar)
        loadIconForPackage(listOf("com.jio.media.stb.ondemand", "com.jio.media.ondemand"), binding.imgJioCinema)
        loadIconForPackage(listOf("com.crunchyroll.crunchyroid"), binding.imgCrunchyroll)
    }

    private fun loadIconForPackage(packageCandidates: List<String>, imageView: android.widget.ImageView) {
        for (pkg in packageCandidates) {
            try {
                val icon = packageManager.getApplicationIcon(pkg)
                imageView.setImageDrawable(icon)
                return
            } catch (_: Exception) {}
        }
    }

    private fun setupFocusListeners() {
        bindFocusEffect(binding.cardYouTube, "SMARTTUBE (TV 4K HDR)")
        bindFocusEffect(binding.cardNetflix, "NETFLIX (4K DOLBY VISION)")
        bindFocusEffect(binding.cardHotstar, "DISNEY+ HOTSTAR (LIVE SPORTS)")
        bindFocusEffect(binding.cardJioCinema, "JIOCINEMA (REGIONAL & LIVE TV)")
        bindFocusEffect(binding.cardCrunchyroll, "CRUNCHYROLL (ANIME 1080P)")
        bindFocusEffect(binding.cardMoreApps, "ALL APPLICATIONS DRAWER")

        bindFocusEffect(binding.cardCable, "CABLE / DTH (HDMI 1)")
        bindFocusEffect(binding.cardPlayStation, "PLAYSTATION 5 (HDMI 2)")
        bindFocusEffect(binding.cardUltraBlock, "ULTRABLOCK TV FIREWALL")
        bindFocusEffect(binding.cardSettings, "TV SETTINGS & PREFERENCES")
    }

    private fun bindFocusEffect(view: View, label: String) {
        val originalBackground = view.background

        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Apple TV 3D elevation zoom
                view.animate().scaleX(1.09f).scaleY(1.09f).translationZ(18f).setDuration(160).start()

                // Update focus pill on weather widget
                binding.btnFocusedAppInfo.text = "▶ HIGHLIGHTED: $label"

                // Frosted white focus outline
                val focusDrawable = GradientDrawable()
                focusDrawable.shape = GradientDrawable.RECTANGLE
                focusDrawable.cornerRadius = 24f * resources.displayMetrics.density
                focusDrawable.setColor(Color.parseColor("#4DFFFFFF"))
                focusDrawable.setStroke(4, Color.parseColor("#FFFFFF"))
                view.background = focusDrawable
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(160).start()
                view.background = originalBackground
            }
        }
    }

    private fun setupClickListeners() {
        binding.cardYouTube.setOnClickListener {
            launchAnyPackage(listOf("org.smarttube.stable", "app.rvx.android.youtube", "com.google.android.youtube.tv", "com.google.android.youtube"))
        }

        binding.cardNetflix.setOnClickListener {
            launchAnyPackage(listOf("com.netflix.ninja", "com.netflix.mediaclient"))
        }

        binding.cardHotstar.setOnClickListener {
            launchAnyPackage(listOf("in.startv.hotstar", "in.startv.hotstar.dplus"))
        }

        binding.cardJioCinema.setOnClickListener {
            launchAnyPackage(listOf("com.jio.media.stb.ondemand", "com.jio.media.ondemand"))
        }

        binding.cardCrunchyroll.setOnClickListener {
            launchAnyPackage(listOf("com.crunchyroll.crunchyroid"))
        }

        binding.cardMoreApps.setOnClickListener {
            // Open Revamped All Apps Drawer
            startActivity(Intent(this, AllAppsActivity::class.java))
        }

        binding.cardCable.setOnClickListener {
            // Sony BRAVIA Hardware HDMI 1
            switchToTvInput("com.mediatek.tis/.HdmiInputService/HW2", "HDMI 1")
        }

        binding.cardPlayStation.setOnClickListener {
            // Sony BRAVIA Hardware HDMI 2
            switchToTvInput("com.mediatek.tis/.HdmiInputService/HW3", "HDMI 2")
        }

        binding.cardUltraBlock.setOnClickListener {
            launchAnyPackage(listOf("com.ultrablock.app"))
        }

        binding.cardSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Could not open Settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun launchAnyPackage(packages: List<String>) {
        for (pkg in packages) {
            val intent = packageManager.getLaunchIntentForPackage(pkg)
                ?: packageManager.getLeanbackLaunchIntentForPackage(pkg)
            if (intent != null) {
                startActivity(intent)
                return
            }
        }
        Toast.makeText(this, "App not found: ${packages.first()}", Toast.LENGTH_SHORT).show()
    }

    private fun switchToTvInput(hardwareInputId: String, portName: String) {
        try {
            // Standard Android TvContract Passthrough Uri with exact MediaTek TIS input ID
            val encodedInputId = Uri.encode(hardwareInputId)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("content://android.media.tv/passthrough/$encodedInputId")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                // Secondary standard: direct MediaTek TV Center intent
                val directIntent = Intent(Intent.ACTION_VIEW).apply {
                    setClassName("com.mediatek.tv.oneworld.tvcenter", "com.mediatek.tv.oneworld.tvcenter.nav.TurnkeyUiMainActivity")
                    data = Uri.parse("content://android.media.tv/passthrough/${Uri.encode(hardwareInputId)}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(directIntent)
            } catch (e: Exception) {
                Toast.makeText(this, "Could not switch to $portName", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
