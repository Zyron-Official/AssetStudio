package com.zyron.assetstudio

import android.content.*
import android.graphics.*
import android.net.*
import android.os.*
import android.provider.*
import androidx.annotation.RequiresApi
import android.util.*
import android.view.*
import android.widget.*
import androidx.appcompat.app.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.zyron.assetstudio.activities.*
import com.zyron.assetstudio.asynchronous.StartActivityTask
import com.zyron.assetstudio.databinding.ActivityMainBinding
import java.util.concurrent.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var activityMain: DrawerLayout
    private lateinit var layoutMain: View
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private lateinit var fileEncoderLaunch: Button
    private var selectedNavItem = R.id.navigation_home
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    companion object {
        private var instance: MainActivity? = null

        @JvmStatic
        fun getInstance(): MainActivity? {
            return instance
        }

        private const val END_SCALE = 0.7f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.layoutMain.toolbar)

        activityMain = binding.activityMain
        navigationView = binding.navigationView
        toolbar = binding.layoutMain.toolbar
        layoutMain = findViewById(R.id.layoutMain)
        fileEncoderLaunch = findViewById(R.id.fileEncoderLaunch)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, activityMain, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        navigationDrawer()
        animateNavigationDrawer()

        activityMain.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }
        
        fileEncoderLaunch.setOnClickListener {
            val intent = Intent(this, FileEncoderActivity::class.java)
            startActivity(intent)
        }

        findViewById<FloatingActionButton>(R.id.fab_draw).setOnClickListener {
            executor.execute(StartActivityTask(this))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!hasManageExternalStoragePermission()) {
                requestManageExternalStoragePermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasManageExternalStoragePermission(): Boolean {
        return Environment.isExternalStorageManager()
    }

    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
      
            Toast.makeText(this, "No need to request permission on older Android versions", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigationDrawer() {
        navigationView.bringToFront()
        navigationView.setCheckedItem(R.id.navigation_home)
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> closeDrawerWithDelay()
                R.id.navigation_shapeshifter -> startActivityWithDelay(ShapeShifterActivity::class.java)
                R.id.navigation_preferences -> startActivityWithDelay(PreferencesActivity::class.java)
                R.id.navigation_xmldesigner -> startActivityWithDelay(XMLDesignerActivity::class.java)
                R.id.navigation_faq -> startActivityWithDelay(FaqActivity::class.java)
                R.id.navigation_updates -> openUrl()
                R.id.navigation_share -> shareApp()
            }
            true
        }
    }

    private fun closeDrawerWithDelay() {
        activityMain.postDelayed({
            activityMain.closeDrawer(GravityCompat.START)
        }, 230)
    }

    private fun startActivityWithDelay(activityClass: Class<*>) {
        activityMain.closeDrawer(GravityCompat.START)
        activityMain.postDelayed({
            startActivity(Intent(this, activityClass))
        }, 230)
    }

    private fun animateNavigationDrawer() {
        activityMain.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset

                layoutMain.scaleX = offsetScale
                layoutMain.scaleY = offsetScale

                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff = drawerView.width * diffScaledOffset / 1.5f
                val xTranslation = xOffset - xOffsetDiff
                layoutMain.translationX = xTranslation
            }
        })
    }

    override fun onBackPressed() {
        if (activityMain.isDrawerOpen(GravityCompat.START)) {
            activityMain.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun openUrl() {
        val githubUrl = "https://github.com/Zyron-Official/Asset-Studio"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)))
    }

    private fun shareApp() {
        val appName = getString(R.string.app_name)
        val appLink = "https://github.com/Zyron-Official/Asset-Studio"
        val appDescription = getString(R.string.app_name)
        val shareInfo = "$appName $appLink $appDescription"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareInfo)
        }

        val chooser = Intent.createChooser(shareIntent, shareInfo)
        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        }
    }
}