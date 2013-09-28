package com.loujiwei.common.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

/**
 * HTTP Helper （POST）
 * 
 * @author Lou Jiwei
 * @descript 基础http连接 + Webservices连接(含安全验证)
 */
public class HttpRequestUtils {

	private final static int CONNECT_TIMEOUT = 5000;
	private final static int REPEATS = 3;
	private final static int READ_TIMEOUT = 10000;
	
	private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/download_test/";//下载图片的目录
	private static String imgPath = "";
	
//	private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识
//	// 随机生成
//	private static final String PREFIX = "--";
//	private static final String LINE_END = "\r\n";
//	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
//	private static final String TAG = "UploadUtil";

	/**
	 * Send data of XML format to the server
	 * 
	 * @param path
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static InputStream requestByXML(String path, String xml) {
		int requestCounts = 0;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		byte[] data = xml.getBytes();

		while (requestCounts < REPEATS) {
			try {
				URL url = new URL(path);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				conn.setReadTimeout(READ_TIMEOUT);
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type",
						"text/xml; charset=UTF-8");
				conn.setRequestProperty("Content-Length",
						String.valueOf(data.length));
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(data);
				outputStream.flush();
				outputStream.close();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					throw new RuntimeException();
				} else {
					inputStream = conn.getInputStream();
				}

				if (null == inputStream) {
					throw new RuntimeException();
				} else {
					requestCounts = REPEATS;
				}
			} catch (IOException e) {
				requestCounts++;
			} catch (RuntimeException e) {
				requestCounts++;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return inputStream;
	}

	/**
	 * Request by json
	 * 
	 * @param path
	 * @param json
	 * @return
	 */
	public static InputStream requestByJSON(String path, String json) {
		int requestCounts = 0;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		byte[] data = json.getBytes();

		while (requestCounts < REPEATS) {
			try {
				URL url = new URL(path);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				conn.setReadTimeout(READ_TIMEOUT);
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type",
						"text/json; charset=UTF-8");
				conn.setRequestProperty("Content-Length",
						String.valueOf(data.length));
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(data);
				outputStream.flush();
				outputStream.close();

				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					throw new RuntimeException();
				} else {
					inputStream = conn.getInputStream();
				}

				if (null == inputStream) {
					throw new RuntimeException();
				} else {
					requestCounts = REPEATS;
				}
			} catch (IOException e) {
				requestCounts++;
			} catch (RuntimeException e) {
				requestCounts++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return inputStream;
	}

	/**
	 * @param path
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static InputStream requestByParams(String path,
			Map<String, String> params) throws Exception {
		List<NameValuePair> paramsPairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				paramsPairs.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramsPairs,
				"UTF-8");
		HttpPost httpPost = new HttpPost(path);
		httpPost.setEntity(entity);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpPost);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		} else {
			return null;
		}
	}

	/**
	 * Get input stream from URL
	 * 
	 * @param path
	 * @return
	 */
	public static InputStream getStreamFromURL(String path) {
		int requestCounts = 0;
		HttpURLConnection conn = null;
		InputStream resultStream = null;

		while (requestCounts < REPEATS) {
			try {
				URL url = new URL(path);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				new CommonLog().i("------:" + conn.getResponseCode());
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					throw new RuntimeException();
				} else {
					resultStream = conn.getInputStream();
				}

				if (resultStream == null) {
					throw new RuntimeException();
				} else {
					requestCounts = REPEATS;
				}
			} catch (IOException e) {
				requestCounts++;
				e.printStackTrace();
			} catch (RuntimeException e) {
				requestCounts++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return resultStream;
	}

	public static boolean downloadFile(String urlPath, String filePath,
			String fileName) {
		boolean flag = false;
		int downloadedSize = 0, totalsize;
		float per;
		try {
			URL url = new URL(urlPath);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// connect
			urlConnection.connect();

			// create a new file, to save the downloaded file
			File fileDir = new File(filePath);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			File file = new File(filePath, fileName);

			FileOutputStream fileOutput = new FileOutputStream(file);

			// Stream used for reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// this is the total size of the file which we are
			// downloading
			totalsize = urlConnection.getContentLength();
			// setText("Starting PDF download...");

			// create a buffer...
			byte[] buffer = new byte[1024 * 1024];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, bufferLength);
				downloadedSize += bufferLength;
				per = ((float) downloadedSize / totalsize) * 100;
				new CommonLog().i("Total PDF File size  : "
						+ (totalsize / 1024) + " KB\n\nDownloading PDF "
						+ (int) per + "% complete");
			}
			// close the output stream when complete //
			fileOutput.close();
			// setText("Download Complete. Open PDF Application installed in the device.");
			flag = true;

		} catch (final Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * EPMS esb服务器的原因,需要先验证一次,然后再交互数据 其他貌似可以直接把账号名密码放在头
	 * 
	 * 利用同一个对象 DefaultHttpClient client
	 * 
	 * @param urlPath
	 * @param xml
	 * @param username
	 * @param password
	 * @param map无用
	 * @param encode
	 * @return
	 */
	public static String requestByXMLNeedSecurity(String urlPath, String xml,
			String username, String password, Map<String, String> map,
			String encode) {
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				// 解析Map传递的参数，使用一个键值对对象BasicNameValuePair保存。
				list.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
		}
		try {
			// 实现将请求 的参数封装封装到HttpEntity中。
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
			// 使用HttpPost请求方式
			HttpPost httpPost = new HttpPost(urlPath);
			// 设置请求参数到Form中。
			httpPost.setEntity(entity);
			// 实例化一个默认的Http客户端
			DefaultHttpClient client = new DefaultHttpClient();
			CredentialsProvider credProvider = new BasicCredentialsProvider();
			credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST,
					AuthScope.ANY_PORT), new UsernamePasswordCredentials(
					username, password));
			client.setCredentialsProvider(credProvider);
			// 执行请求，并获得响应数据
			new CommonLog().i("start execute");
			HttpResponse httpResponse = client.execute(httpPost);
			new CommonLog().i(httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				return "";
			}
			// 判断是否请求成功，为200时表示成功，其他均问有问题。
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 用户名验证成功
				HttpPost post = new HttpPost(urlPath);
				try {
					URL url = new URL(urlPath);
					StringEntity sEntity = new StringEntity(xml, "utf-8");
					post.setEntity(sEntity);
					post.setHeader("Host", url.getHost());
					post.setHeader("Content-Type",
							"application/x-www-form-urlencoded");
					post.setHeader("SOAPAction",
							"21010000000003/ESB_SERVICE_ID");

					new CommonLog().i("post"
							+ EntityUtils.toString(post.getEntity()));
					HttpResponse response = client.execute(post);
					// 打印返回的xml数据
					new CommonLog().i(response.getStatusLine());
					new CommonLog().i("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

					return EntityUtils.toString(response.getEntity());

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			new CommonLog().i("1" + e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			new CommonLog().i("2" + e);
		} catch (IOException e) {
			e.printStackTrace();
			new CommonLog().i("3" + e);
		}
		return "";
	}

	/**
	 * Xml解析
	 * 
	 * @param string
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static void pullXml(String string) throws XmlPullParserException,
			IOException {
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		new CommonLog().i("@");
		xpp.setInput(new StringReader(string));
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_DOCUMENT) {
				new CommonLog().i("Start document");
			} else if (eventType == XmlPullParser.START_TAG) {
				new CommonLog().i("Start tag " + xpp.getName());
			} else if (eventType == XmlPullParser.END_TAG) {
				new CommonLog().i("End tag " + xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				new CommonLog().i("Text " + xpp.getText());
			}

			eventType = xpp.next();
		}
	}

	/**
	 * EPMS 需要用户名密码验证后再下载图片
	 * 
	 * @param urlPath
	 * @param username
	 * @param password
	 * @return
	 */
	public static String getPictureNeedSecurity(String urlPath,
			String username, String password) {
		try {
			URL url = new URL(urlPath);
			URL url2 = new URL(urlPath);

			HttpURLConnection conn = (HttpURLConnection) url2.openConnection();

			HttpURLConnection conn1 = (HttpURLConnection) url.openConnection();// 登陆地连接
			String usernamePassword = username + ":" + password;
			String encoding = Base64.encodeToString(
					usernamePassword.getBytes(), Base64.DEFAULT);
			conn1.setRequestProperty("Authorization", "Basic " + encoding);
			conn1.setInstanceFollowRedirects(false);
			conn1.connect();// 登陆
			new CommonLog().i("完成connect" + conn1.getResponseCode());
			String session_value = conn1.getHeaderField("Set-Cookie");// 返回第一次登陆后的sesson
			if (conn1.getResponseCode() == 200) {
				// sessionId = sessionId.substring(0, sessionId.indexOf(";"));
				String[] sessionId = session_value.split(";");
				new CommonLog().i("session" + sessionId[0]);
				// save session info
				conn.setRequestProperty("Authorization", "Basic " + encoding);
				conn.setRequestProperty("Cookie", sessionId[0]);
				conn.setReadTimeout(READ_TIMEOUT);
				conn.setConnectTimeout(CONNECT_TIMEOUT);
				// conn.setDoInput(true); // 允许输入流
				// conn.setDoOutput(true); // 允许输出流
				// conn.setUseCaches(false); // 不允许使用缓存
				conn.setRequestMethod("GET"); // 请求方式
				// conn.setRequestProperty("Charset", "utf-8"); // 设置编码
				// conn.setRequestProperty("connection", "keep-alive");
				// conn.setRequestProperty("user-agent",
				// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
				// conn.setRequestProperty("Content-Type", CONTENT_TYPE +
				// ";boundary=" + BOUNDARY);
				new CommonLog().i("图片下载连接完成  " + conn.getResponseCode());
				InputStream inStream = conn.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(inStream);

				File dirFile = new File(ALBUM_PATH);
				if (!dirFile.exists()) {
					dirFile.mkdir();
				}
				imgPath = ALBUM_PATH + ImageUtils.getPhotoFileName() + ".jpg";
				File myCaptureFile = new File(imgPath);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(myCaptureFile));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
				bos.flush();
				bos.close();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgPath;

	}
	
}
