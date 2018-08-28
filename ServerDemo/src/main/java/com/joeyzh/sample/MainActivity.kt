package com.joeyzh.sample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import com.joey.base.util.LogUtils
import com.joeyzh.pushlib.IMessageInterface
import com.joeyzh.pushlib.httpserver.AppReceiveCallback
import com.joeyzh.pushlib.httpserver.PushError
import com.joeyzh.pushlib.httpserver.PushHttpServer
import com.joeyzh.pushlib.httpserver.PushService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var pushHttpServer: PushHttpServer? = null
    var mStub: IMessageInterface? = null
    var message: String = ""
    var mService: PushService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushHttpServer = PushHttpServer.newInstance()
        startService(Intent(this, PushService::class.java))

        var intent = Intent()
        intent!!.action = "com.joeyzh.push.clientdemo.ClientService"
        intent!!.`package` = "com.joeyzh.push.clientdemo"
        bindService(intent, stubConnection, Context.BIND_AUTO_CREATE)
        tv_notice.text = "" + pushHttpServer!!.port
    }

    fun start(view: View?) {
//        pushHttpServer!!.start()
        tv_notice.text = pushHttpServer!!.getHost(this)
    }

    fun stop(view: View?) {
//        pushHttpServer!!.close()
        tv_notice.text = "" + pushHttpServer!!.port
        if (mStub == null) {
            LogUtils.e("stub is null")
            return
        }
        mStub!!.onListener("开始啦")

    }

    var stubConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            mStub = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mStub = IMessageInterface.Stub.asInterface(service)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(stubConnection)
    }
}
