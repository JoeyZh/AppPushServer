package com.joeyzh.pushlib.httpserver;//package com.joeyzh.pushlib.httpserver;
//
//import java.io.IOException;
//
//import org.apache.http.ConnectionClosedException;
//import org.apache.http.HttpException;
//import org.apache.http.HttpServerConnection;
//import org.apache.http.protocol.BasicHttpContext;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.protocol.HttpService;
//
//import com.taixin.boxassistant.ALog;
//
///**
// * 功能函数:封装了一个线程,用于执行接受一个客户端的连接请求处理
// * 函数使用:和普通Thread一样使用.
// * **/
//
//public class WorkerThread extends Thread {
//
//	private static String TAG="WorkerThread: ";
//
//	private final HttpService httpservice;
//	private final HttpServerConnection conn;
//
//	public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
//		super();
//		this.httpservice = httpservice;
//		this.conn = conn;
//	}
//
//	@Override
//	public void run() {
//		HttpContext context = new BasicHttpContext();
//		try {
//			while (!Thread.interrupted() && this.conn.isOpen()) {
//				this.httpservice.handleRequest(this.conn, context);
//			}
//		} catch (ConnectionClosedException ex) {
//			ALog.i(TAG, "Client closed connection");
//		} catch (IOException ex) {
//			ALog.i(TAG, "I/O error: " + ex.getMessage());
//		} catch (HttpException ex) {
//			ALog.i(TAG, "Unrecoverable HTTP protocol violation: " + ex.getMessage());
//		} finally {
//			try {
//				this.conn.shutdown();
//			} catch (IOException ignore) {
//			}
//		}
//	}
//
//}
