package com.artyombuzuk.reddita.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artyombuzuk.reddita.R
import com.artyombuzuk.reddita.others.PermanentStorage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermanentStorage.NoContextStrings.nowString = getString(R.string.now)
        PermanentStorage.NoContextStrings.postedString = getString(R.string.posted)
        PermanentStorage.NoContextStrings.minutesAgoString = getString(R.string.minutes_ago)
        PermanentStorage.NoContextStrings.hoursAgoString = getString(R.string.hours_ago)
    }
}