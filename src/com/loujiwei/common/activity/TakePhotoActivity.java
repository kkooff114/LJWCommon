package com.loujiwei.common.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.loujiwei.common.R;
import com.loujiwei.common.application.Content;
import com.loujiwei.common.application.LJWApplication;
import com.loujiwei.common.utils.ImageUtils;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TakePhotoActivity extends LJWBaseActivity {
	private LinearLayout layout;
	private LinearLayout title_takPicPage, layout_takPicPage, title_back,
			title_complete;
	private ImageView imageView;
	private File mPhotoFile;
	private String newpath, tmpPath;
	private boolean hasOnActivityResult = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog_from_settings);
		if (savedInstanceState != null) {
			// new SaveInstanceState().getSaveInstance(savedInstanceState);
			// //新建一个SaveInstanceStatee类,新建getSaveInstance方法,传入outState参数,通过outState.get方法设置要保存的参数

		}
		init();

	}

	private void init() {
		layout = (LinearLayout) findViewById(R.id.exit_layout2);
		title_back = (LinearLayout) findViewById(R.id.title_back);
		title_complete = (LinearLayout) findViewById(R.id.title_complete);
		title_takPicPage = (LinearLayout) findViewById(R.id.title_takPicPage);
		imageView = (ImageView) findViewById(R.id.showPic);
		layout_takPicPage = (LinearLayout) findViewById(R.id.layout_takPicPage);
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "点击小窗口意外关闭",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!hasOnActivityResult) {
			finish();
		}
		return true;
	}

	public void takePhoto(View v) {
		try {
			Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			mPhotoFile = new File(Environment.getExternalStorageDirectory()
					+ "/image.jpg");
			if (!mPhotoFile.exists()) {
				mPhotoFile.createNewFile();
			}
			// application.hasTakePhoto = true;// 标志开始相机
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
			startActivityForResult(intent, 0);
		} catch (Exception e) {
		}
	}

	public void pickPhoto(View v) {
		try {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exitbutton1(View view) {
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("TakePhotoActivity", "requestCode:" + requestCode
				+ " resultCode:" + resultCode + " data:" + data);
		if (resultCode != 0) {
			hasOnActivityResult = true;
			title_takPicPage.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.VISIBLE);
			title_back.setVisibility(View.VISIBLE);
			title_complete.setVisibility(View.VISIBLE);
			imageView.setBackgroundResource(R.drawable.background_android);
			layout_takPicPage.setVisibility(View.GONE);

			if (requestCode == 0) {
				// 从相机返回到图片展示界面
				tmpPath = Environment.getExternalStorageDirectory()
						+ "/image.jpg";

				// application.hasTakePhoto = false;// 标志开始相机
			} else if (requestCode == 1) {
				try {
					Uri uri = data.getData();
					Cursor cursor = getContentResolver().query(uri, null, null,
							null, null);
					cursor.moveToFirst();
					// String imgNo = cursor.getString(0); // 图片编号
					String imgPath = cursor.getString(1); // 图片文件路径
					// String imgSize = cursor.getString(2); // 图片大小
					// String imgName = cursor.getString(3); // 图片文件名

					tmpPath = imgPath;
				} catch (Exception e) {
					Toast.makeText(TakePhotoActivity.this, "没有选择图片",
							Toast.LENGTH_SHORT).show();
				}
			}

			try {
				// start 旋转图片start
				Bitmap bitmap = ImageUtils.zoomBitmapForGood(tmpPath);

				imageView.setImageBitmap(bitmap);
				// 将压缩的图片从新保存start
				newpath = "mnt/sdcard/DCIM/Camera/" + getPhotoFileName();
				File myCaptureFile = new File(newpath);
				BufferedOutputStream bos;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(
							myCaptureFile));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
					try {
						bos.flush();
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 将压缩的图片从新保存end

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public void onclick(View view) {
		int id = view.getId();
		if (id == R.id.title_back) {
			this.finish();
		} else if (id == R.id.title_complete) {
			// newpath即所获得的图片, 在这里将newpath进行处理展示等.
			Content.setTakePhoto_Path(newpath);
			this.finish();
		} else {
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		// new SaveInstanceState().setSaveInstance(outState);
		// //新建一个SaveInstanceStatee类,新建setSaveInstance方法,传入outState参数,通过outState.set方法设置要保存的参数
	}

}
