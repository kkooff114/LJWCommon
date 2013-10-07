package com.loujiwei.common.application;



import com.loujiwei.common.customfunction.CommonLog;

import android.app.Application;
import android.util.Log;

/**
 *
 * @author Lou Jiwei 
 * @email  kkooff114@gmail.com
 * @create 2013-10-5 下午12:37:27
 * 
 */
public class LJWApplication extends Application{
	
	private static LJWApplication instance=null;
	
	private String takePhoto_Path="";
	
	@Override
	public void onCreate() {
		super.onCreate();
		new CommonLog().i("LJWApplication onCreate");
		instance = this;
	}
	
	public static LJWApplication getInstance() {
		return instance;
	}

	public String getTakePhoto_Path() {
		return takePhoto_Path;
	}

	public void setTakePhoto_Path(String takePhoto_Path) {
		this.takePhoto_Path = takePhoto_Path;
	}
	

}
