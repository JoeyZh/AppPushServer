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
import com.joeyzh.pushclient.IPushApiInterface
import com.joeyzh.pushlib.IMessageInterface
import com.joeyzh.pushlib.httpserver.PushHttpServer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var pushHttpServer: PushHttpServer? = null
    var mStub: IMessageInterface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pushHttpServer = PushHttpServer.newInstance()
//        startService(Intent(this, HttpService::class.java))
        var intent = Intent()
        intent!!.setAction("com.joeyzh.push.clientdemo.ClientService")
        intent!!.setPackage("com.joeyzh.push.clientdemo")
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        tv_notice.text = "" + pushHttpServer!!.port
    }

    fun start(view: View?) {
        pushHttpServer!!.start()
        tv_notice.text = pushHttpServer!!.getHost(this)
    }

    fun stop(view: View?) {
        pushHttpServer!!.close()
        tv_notice.text = "" + pushHttpServer!!.port
    }

    var serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mStub = IMessageInterface.Stub.asInterface(service);
            if (mStub == null) {
                LogUtils.e("stub is null")
                return
            }
            mStub!!.onListener("review message")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
