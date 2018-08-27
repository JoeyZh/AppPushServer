package com.joeyzh.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.joeyzh.pushlib.httpserver.HttpService
import com.joeyzh.pushlib.httpserver.PushServer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var pushServer: PushServer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushServer = PushServer.newInstance()
        startService(Intent(this, HttpService::class.java))
        tv_notice.text = "" + pushServer!!.port
    }

    fun start(view: View?) {
//        pushServer!!.start()
        tv_notice.text = pushServer!!.getHost(this)
    }

    fun stop(view: View?) {
//        pushServer!!.close()
        tv_notice.text = "" + pushServer!!.port
    }

}
