//package com.joeyzh.pushlib.httpserver;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.Inet4Address;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.Socket;
//import java.net.SocketException;
//import java.net.URLConnection;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.Random;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpException;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.entity.FileEntity;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.DefaultConnectionReuseStrategy;
//import org.apache.http.impl.DefaultHttpResponseFactory;
//import org.apache.http.impl.DefaultHttpServerConnection;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.params.CoreProtocolPNames;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.BasicHttpProcessor;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.protocol.HttpRequestHandler;
//import org.apache.http.protocol.HttpRequestHandlerRegistry;
//import org.apache.http.protocol.HttpService;
//import org.apache.http.protocol.ResponseConnControl;
//import org.apache.http.protocol.ResponseContent;
//import org.apache.http.protocol.ResponseDate;
//import org.apache.http.protocol.ResponseServer;
//
//import android.content.Context;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.text.TextUtils;
//
//import com.taixin.boxassistant.ALog;
//import com.taixin.boxassistant.net.MyServerSocket;
///**
// * 功能介绍:此类实现一个webserver的功能.在构造函数中传入端口号,以及要通过http要共享的目录.
// * 		对输入的端口号,进行绑定,如果当前的端口号已经被绑定,则将该端口号+1,最多加两次.如果再绑定不成功,则此返回失败.
// * 使用方法:1 通过获取实例并启动线程.
// * 		  2 通过getBindSuccesed()判断绑定端口是否成功.
// * 		  3 通过getHttpServerRunningStatus()判断httpserver运行是否成功.
// * 		  4 通过调用close()关闭httpserver
// * 		  5 httpserver 正常启动之后,通过getBindPort()来获取bind的Port
// * **/
//
//public class WebServer extends Thread {
//
//	public static WebServer mWebServer;
//	private static String TAG="WebServer: ";
//	private static Context mContext;
//	private int port;
//	private String webRoot;
//	private boolean isLoop = false;
//	MyServerSocket serverSocket = null;
//	private boolean bindSuccess = false;
//	private boolean httpServerRunning = false;
//
//	private static int SHARE_PICTURE_ID = 0;
//	private static int SHARE_MUSIC_ID = 1;
//	private static int SHARE_VIDEO_ID = 2;
//
//	/*进行图片,音乐,视频三个目录进行标示*/
//	private String picturePath = "/storage/external_storage/sda1/picture";
//	private String musicPath =  "/storage/external_storage/sda1/music";
//	private String videoPath =  "/storage/external_storage/sda1/video";
//	private String mediaPath = "/data/taixin/media";
//	private Random mRandom;
//	private int maxFileNum = 200;
//	private LRUCache<String, String> localUrlMap;
//	private LRUCache<String, String> httpUrlMap;
//
//	private void SetMaxFileNum(int num)
//	{
//		maxFileNum = num;
//		localUrlMap = new LRUCache<String, String>(maxFileNum);
//		httpUrlMap = new LRUCache<String, String>(maxFileNum);
//	}
//
//	public LRUCache<String, String> getLocalUrlMap()
//	{
//		return localUrlMap;
//	}
//
//	public String getPicturePath() {
//		return picturePath;
//	}
//
//	public void setPicturePath(String picturePath) {
//		this.picturePath = picturePath;
//	}
//
//	public String getMusicPath() {
//		return musicPath;
//	}
//
//	public void setMusicPath(String musicPath) {
//		this.musicPath = musicPath;
//	}
//
//	public String getVideoPath() {
//		return videoPath;
//	}
//
//	public void setVideoPath(String videoPath) {
//		this.videoPath = videoPath;
//	}
//
//	static public WebServer getInstance(int port ,final String webRoot,Context context)
//	{
//		if(mWebServer == null)
//		{
//			synchronized(WebServer.class){
//				if(mWebServer == null){
//					mWebServer = new WebServer(port,webRoot,context);
//					mWebServer.start();
//				}
//			}
//		}
//		return mWebServer;
//	}
//
//	public WebServer(int port, final String webRoot,Context context) {
//		super();
//		this.setName("httpServer");
//		this.port = port;
//		this.webRoot = webRoot;
//		mContext=context;
//		mRandom=new Random(10000000);
//		if(localUrlMap == null || httpUrlMap == null)
//		{
//			localUrlMap = new LRUCache<String, String>(maxFileNum);
//			httpUrlMap = new LRUCache<String, String>(maxFileNum);
//		}
//
//		// 创建服务器套接字
//		if(!initSocket())
//			return ;
//	}
//
//	public boolean getBindSuccesed()
//	{
//		return bindSuccess;
//	}
//
//	public boolean getHttpServerRunningStatus()
//	{
//		return httpServerRunning;
//	}
//
//	public int getBindPort()
//	{
//		return port;
//	}
//	public boolean initSocket()
//	{
//		for(int i = 0; i<3;i++)
//		{
//			//判断是否绑定
//			try{
//				serverSocket = new MyServerSocket(port);
//				ALog.i(TAG, "Bind port is :"+port);
//				bindSuccess = true;
//				return true;
//			}
//			catch(IOException e)
//			{
//				e.printStackTrace();
//			}
//			port++;
//			isLoop = false;
//		}
//
//		return false;
//
//	}
//
//	@Override
//	public void run() {
//
//		try {
//
//			// 创建HTTP协议处理器
//			BasicHttpProcessor httpproc = new BasicHttpProcessor();
//			// 增加HTTP协议拦截器
//			httpproc.addInterceptor(new ResponseDate());
//			httpproc.addInterceptor(new ResponseServer());
//			httpproc.addInterceptor(new ResponseContent());
//			httpproc.addInterceptor(new ResponseConnControl());
//			// 创建HTTP服务
//			HttpService httpService = new HttpService(httpproc,
//					new DefaultConnectionReuseStrategy(),
//					new DefaultHttpResponseFactory());
//			// 创建HTTP参数
//			HttpParams params = new BasicHttpParams();
//			params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
//					.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
//							8 * 1024)
//					.setBooleanParameter(
//							CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
//					.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
//					.setParameter(CoreProtocolPNames.ORIGIN_SERVER,"WebServer/1.1");
//
//			// 设置HTTP参数
//			httpService.setParams(params);
//
//			// 创建HTTP请求执行器注册表
//			HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
//
//			// 增加HTTP请求执行器
//			reqistry.register("*", new HttpFileHandler(webRoot));
//
//			// 设置HTTP请求执行器
//			httpService.setHandlerResolver(reqistry);
//
//			/* 循环接收各客户端 请求*/
//			httpServerRunning = true;
//			isLoop = true;
//
//			while (isLoop && !Thread.interrupted()) {
//				ALog.i("TXhttpserver", "@@@@@@@@@@@@@------");
//				// 接收客户端套接字
//				Socket socket = serverSocket.accept();
//				// 绑定至服务器端HTTP连接
//				DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
//				conn.bind(socket, params);
//				// 派送至WorkerThread处理请求
//				Thread t = new WorkerThread(httpService, conn);
//				t.setName("httpServer-worktread:"+t.getId());
//				t.setDaemon(true); // 设为守护线程
//				t.start();
//			}
//		} catch (IOException e) {
//			isLoop = false;
//			httpServerRunning = false;
//			e.printStackTrace();
//			mWebServer = null;
//		} finally {
//				try {
//					mWebServer = null;
//					if (serverSocket != null) {
//						ALog.i(TAG, "serverSocket error,will be close ");
//						serverSocket.close();
//
//					}
//				} catch (IOException e) {
//			}
//		}
//	}
//
//	public String convertLocalUrlToHttpUrl(String localUrl) {
//		String httpUrl = null;
//		httpUrl = mWebServer.localUrlMap.get(localUrl);
//		if (httpUrl == null) {
//			int pos = localUrl.indexOf(mWebServer.webRoot);
//			if (pos != -1) {
//				String suffix = localUrl.substring(localUrl.lastIndexOf("/")+1);
//				//中文名称的文件某些播放器不支持，因此用随机数替换
//				String type=localUrl.substring(localUrl.lastIndexOf("."), localUrl.length());
//				suffix=mRandom.nextInt(100000000)+type;
//				String revUrl = mediaPath + System.currentTimeMillis() + suffix;
//				httpUrl = "http://" + getWifiIpAddress() + ":" + mWebServer.port + revUrl;
//
//				mWebServer.localUrlMap.put(localUrl, httpUrl);
//				mWebServer.httpUrlMap.put(revUrl, localUrl);
//			}
//		}
//		return httpUrl;
//	}
//	public static String getWifiIpAddress(){
//		WifiManager manager=(WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info=manager.getConnectionInfo();
//		int ipAddress=info.getIpAddress();
//		String ip="";
//		if(ipAddress!=0){
//			ip=((ipAddress&0xff)+"."+(ipAddress>>8&0xff)+"."+(ipAddress>>16&0xff)+"."+(ipAddress>>24&0xff));
//			ALog.i("the IP is:"+ip);
//			return ip;
//		}else{
//			return getLocalIpAddress();
//		}
//	}
//	  public static String getLocalIpAddress() {
//	        try {
//	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//	                NetworkInterface intf = en.nextElement();
//	                String inferfaceType = intf.getName();
//	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//
//	                    while (enumIpAddr.hasMoreElements()) {
//	                        InetAddress inetAddress = enumIpAddr.nextElement();
//
//	                        if (inetAddress != null && !inetAddress.isLoopbackAddress()
//	                                && (inetAddress instanceof Inet4Address)) {
//	                            if (inferfaceType != null && !inferfaceType.startsWith("rmnet")) {
//	                                return inetAddress.getHostAddress().toString();
//	                            }
//	                        }
//	                    }
//	                }
//	            }
//
//	            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//	                NetworkInterface intf = en.nextElement();
//	                String inferfaceType = intf.getName();
//	                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//
//	                    while (enumIpAddr.hasMoreElements()) {
//	                        InetAddress inetAddress = enumIpAddr.nextElement();
//	                        if (inetAddress != null && !inetAddress.isLoopbackAddress()
//	                                && (inetAddress instanceof Inet4Address)) {
//	                            if (inferfaceType != null && inferfaceType.startsWith("rmnet")) {
//	                                return inetAddress.getHostAddress().toString();
//	                            }
//	                        }
//	                    }
//	                }
//	            }
//	        } catch (SocketException ex) {
//	           ALog.i(TAG, "WifiPreference IpAddress"+ex.toString());
//	        }
//
//	        return null;
//	    }
//
//
//	public void close() {
//		isLoop = false;
//		httpServerRunning = false;
//		interrupted();
//
//		ALog.i(TAG, "httpserver close");
//	}
//
//	class HttpFileHandler implements HttpRequestHandler {
//
//		private String webRoot;
//
//		public HttpFileHandler(final String webRoot) {
//			this.webRoot = webRoot;
//		}
//
//		@Override
//		public void handle(HttpRequest request, HttpResponse response,
//				HttpContext context) throws HttpException, IOException {
//
//			String target = URLDecoder.decode(request.getRequestLine().getUri(),
//					"UTF-8");
//
//			if(!target.contains(mediaPath))
//			{
//				ALog.i("invalid access path");
//				return;
//			}
//
//			String localpath = mWebServer.httpUrlMap.get(target);
//			if(TextUtils.isEmpty(localpath))
//			{
//				ALog.i("get mWebServer.httpUrlMap by"+target+"is empty or null");
//				return ;
//			}
//
//			final File file = new File(this.webRoot, localpath);
//
//			if (!file.exists()) { // 不存在
//				response.setStatusCode(HttpStatus.SC_NOT_FOUND);
//				StringEntity entity = new StringEntity(
//						"<html><body><h1>Error 404, file not found.</h1></body></html>",
//						"UTF-8");
//				response.setHeader("Content-Type", "text/html");
//				response.setEntity(entity);
//			} else if (file.canRead()) { // 可读
//				response.setStatusCode(HttpStatus.SC_OK);
//				HttpEntity entity = null;
//				if (file.isDirectory()) { // 文件夹
//					entity = createDirListHtml(file, target);
//					response.setHeader("Content-Type", "text/html");
//				} else { // 文件
//					String contentType = URLConnection
//							.guessContentTypeFromName(file.getAbsolutePath());
//					contentType = null == contentType ? "charset=UTF-8"
//							: contentType + "; charset=UTF-8";
//
//					/* 实际测试一下用文件和inputstream,用效率高的.
//					FileInputStream fis = new FileInputStream(file);
//					InputStreamEntity body = new InputStreamEntity(fis, file.length());
//					*/
//					entity = new FileEntity(file, contentType);
//					response.setHeader("Content-Type", contentType);
//					response.setStatusCode(HttpStatus.SC_OK);
//
//				}
//				response.setEntity(entity);
//			} else { // 不可读
//				response.setStatusCode(HttpStatus.SC_FORBIDDEN);
//				StringEntity entity = new StringEntity(
//						"<html><body><h1>Error 403, access denied.</h1></body></html>",
//						"UTF-8");
//				response.setHeader("Content-Type", "text/html");
//				response.setEntity(entity);
//			}
//		}
//
//		/** 创建文件列表浏览网页 */
//		private StringEntity createDirListHtml(File dir, String target)
//				throws UnsupportedEncodingException {
//			StringBuffer sb = new StringBuffer();
//			sb.append("<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n<title>");
//			sb.append(null == target ? dir.getAbsolutePath() : target);
//			sb.append(" 的索引</title>\n");
//			sb.append("<link rel=\"shortcut icon\" href=\"/mnt/sdcard/.wfs/img/favicon.ico\">\n");
//			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/mnt/sdcard/.wfs/css/wsf.css\">\n");
//			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/mnt/sdcard/.wfs/css/examples.css\">\n");
//			sb.append("<script type=\"text/javascript\" src=\"/mnt/sdcard/.wfs/js/jquery-1.7.2.min.js\"></script>\n");
//			sb.append("<script type=\"text/javascript\" src=\"/mnt/sdcard/.wfs/js/jquery-impromptu.4.0.min.js\"></script>\n");
//			sb.append("<script type=\"text/javascript\" src=\"/mnt/sdcard/.wfs/js/wsf.js\"></script>\n");
//			sb.append("</head>\n<body>\n<h1 id=\"header\">");
//			sb.append(null == target ? dir.getAbsolutePath() : target);
//			sb.append(" 的索引</h1>\n<table id=\"table\">\n");
//			sb.append("<tr class=\"header\">\n<td>名称</td>\n<td class=\"detailsColumn\">大小</td>\n<td class=\"detailsColumn\">修改日期</td>\n<td class=\"detailsColumn\">处理操作</td>\n</tr>\n");
//			/* 上级目录 */
//			if (!isSamePath(dir.getAbsolutePath(), this.webRoot)) {
//				sb.append("<tr>\n<td><a class=\"icon up\" href=\"..\">[上级目录]</a></td>\n<td></td>\n<td></td>\n<td></td>\n</tr>\n");
//			}
//
//			/* 目录列表 */
//			File[] files = dir.listFiles();
//			if (null != files) {
//				sort(files); // 排序
//				for (File f : files) {
//					appendRow(sb, f);
//				}
//			}
//
//			return new StringEntity(sb.toString(), "UTF-8");
//		}
//
//		private boolean isSamePath(String a, String b) {
//			String left = a.substring(b.length(), a.length()); // a以b开头
//			if (left.length() >= 2) {
//				return false;
//			}
//			if (left.length() == 1 && !left.equals("/")) {
//				return false;
//			}
//			return true;
//		}
//
//		/** 排序：文件夹、文件，再各按字符顺序 */
//		private void sort(File[] files) {
//			Arrays.sort(files, new Comparator<File>() {
//				@Override
//				public int compare(File f1, File f2) {
//					if (f1.isDirectory() && !f2.isDirectory()) {
//						return -1;
//					} else if (!f1.isDirectory() && f2.isDirectory()) {
//						return 1;
//					} else {
//						return f1.toString().compareToIgnoreCase(f2.toString());
//					}
//				}
//			});
//		}
//
//		private SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd ahh:mm");
//
//		private void appendRow(StringBuffer sb, File f) {
//			String clazz, link, size;
//			if (f.isDirectory()) {
//				clazz = "icon dir";
//				link = f.getName() + "/";
//				size = "";
//			} else {
//				clazz = "icon file";
//				link = f.getName();
//				size = formatFileSize(f.length());
//			}
//			sb.append("<tr>\n<td><a class=\"");
//			sb.append(clazz);
//			sb.append("\" href=\"");
//			sb.append(link);
//			sb.append("\">");
//			sb.append(link);
//			sb.append("</a></td>\n");
//			sb.append("<td class=\"detailsColumn\">");
//			sb.append(size);
//			sb.append("</td>\n<td class=\"detailsColumn\">");
//			sb.append(sdf.format(new Date(f.lastModified())));
//			sb.append("</td>\n<td class=\"operateColumn\">");
//			sb.append("<span><a href=\"");
//			sb.append(link);
//			//sb.append(WebServer.SUFFIX_ZIP);
//			sb.append("\">下载</a></span>");
//			if (f.canWrite()) {
//				sb.append("<span><a href=\"");
//				sb.append(link);
//				//sb.append(WebServer.SUFFIX_DEL);
//				sb.append("\" onclick=\"return confirmDelete('");
//				sb.append(link);
//				//sb.append(WebServer.SUFFIX_DEL);
//				sb.append("')\">删除</a></span>");
//			}
//			sb.append("</td>\n</tr>\n");
//		}
//
//		public boolean hasWfsDir(File f) {
//			String path = f.isDirectory() ? f.getAbsolutePath() + "/" : f
//					.getAbsolutePath();
//			return path.indexOf("/.wfs/") != -1;
//		}
//
//		/** 获得文件大小表示 */
//		private String formatFileSize(long len) {
//			if (len < 1024)
//				return len + " B";
//			else if (len < 1024 * 1024)
//				return len / 1024 + "." + (len % 1024 / 10 % 100) + " KB";
//			else if (len < 1024 * 1024 * 1024)
//				return len / (1024 * 1024) + "." + len % (1024 * 1024) / 10 % 100
//						+ " MB";
//			else
//				return len / (1024 * 1024 * 1024) + "." + len
//						% (1024 * 1024 * 1024) / 10 % 100 + " MB";
//		}
//
//	}
//
//}
