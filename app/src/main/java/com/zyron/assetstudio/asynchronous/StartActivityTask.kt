package com.zyron.assetstudio.asynchronous

import android.content.Context
import android.content.Intent
import com.zyron.assetstudio.activities.IconLauncherActivity
import com.zyron.assetstudio.MainActivity

class StartActivityTask(private val context: Context) : Runnable {

    override fun run() {
        if (context is MainActivity) {
            context.runOnUiThread {
                val intent = Intent(context, IconLauncherActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}