package com.joeyzh.sample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import com.joeyzh.pushlib.IMessageInterface
import com.joeyzh.pushlib.httpserver.*
import com.joeyzh.ui.IMessage
import com.joeyzh.ui.NoticeCreator
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
        // 启动服务
        startService(Intent(this, PushService::class.java))

        // 配置消息显示内容
        var intent = Intent()
        intent.action = PushService.NOTICE_INIT
        intent.putExtra("smallIcon", R.mipmap.ic_launcher)
        intent.putExtra("icon", R.mipmap.ic_launcher)
        intent.putExtra("appName", "测试demo")
        sendBroadcast(intent)

        //配置远程监听服务
        intent = Intent()
        intent!!.action = "com.joeyzh.push.clientdemo.ClientService"
        intent!!.`package` = "com.joeyzh.push.clientdemo"
        bindService(intent, stubConnection, Context.BIND_AUTO_CREATE)
//        tv_notice.text = "" + pushHttpServer!!.port
    }

    fun start(view: View?) {
        tv_notice.text = pushHttpServer!!.getHost(this)
        Log.i("MainActivity", Thread.currentThread().toString())
        var create = NoticeCreator(this)
        create.setInfo(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "测试")
        var message = IMessage()
        message.content = "一条测试记录"
        message.title = "标题"
        create.processCustomMessage(this, message)
    }

    fun stop(view: View?) {
//        tv_notice.text = "" + pushHttpServer!!.port
//        if (mStub == null) {
//            Log.e("MainActivity", "stub is null")
//            return
//        }
//        mStub!!.onListener("开始啦")

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
