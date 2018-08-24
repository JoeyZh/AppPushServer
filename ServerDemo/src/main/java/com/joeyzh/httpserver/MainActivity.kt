package com.joeyzh.httpserver

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.joeyzh.pushlib.httpserver.PushServer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var pushServer: PushServer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushServer = PushServer()
    }

    fun start(view: View?) {
        pushServer!!.start()
        tv_notice.setText("" + pushServer!!.serverUrl)
    }

    fun stop(view: View?) {
        pushServer!!.close()
    }
}
