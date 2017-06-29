package com.example.test;

import com.example.test.MoviePlayer;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.example.test.MovieRecorder;
import com.example.test.R;

import android.R.drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
@SuppressLint("NewApi")
public class MainActivity extends Activity {
	
	MovieRecorder mRecorder;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	ImageButton ibtnRecoding;
	ImageButton ibtnFileList;
	/*ImageButton ibtnFileBack;*/
	TextView tvFileName;
	TextView tvFileSize;
	MoviePlayer mPlayer;
	/*MyAdatpter mAdatpter;*/
	//Button btRecorder;
	Button btnScreenShot;
	//String path;
	private  ArrayList<String> search_result = new ArrayList<String>();//一个字符串数组列表保存结果  
	private ArrayList<String> filterDateList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> itemdatafilter=null;
	private ListView file_list;
	// private ClearEditText inputsearch;//输入查询
	private String infopath;
	//Vector btFileList;
	String filePath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		creatFile();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		
		setContentView(R.layout.activity_main);
		
		ibtnRecoding = (ImageButton) findViewById(R.id.imageButton1);
		ibtnFileList = (ImageButton) findViewById(R.id.imageButton2);
		/*ibtnFileBack = (ImageButton) findViewById(R.id.file_back1);*/
		ibtnFileList.setOnClickListener(fileListClick);
		ibtnRecoding.setOnClickListener(mRecordingClick);
		/*ibtnFileBack.setOnClickListener(fileBackClick);*/
		//btRecorder = (Button) findViewById(R.id.btnRecoder);
		
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback); 
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		
		mRecorder = new MovieRecorder();
		/*if(!"".equals(path)&&path!=null)
			btRecorder.setText(path);
		else
			btRecorder.setText("mmp");*/
		mPlayer = new MoviePlayer();
		
		refreshViewByRecordingState();
	
		btnScreenShot = (Button) this.findViewById(R.id.btn_save);
		btnScreenShot.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				screenshot();
				return;
			}
		});
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@SuppressLint("NewApi")
	private Bitmap createVideoThumbnail(String filePath) {  
		Bitmap bitmap = null;  
		android.media.MediaMetadataRetriever retriever = new android.media.MediaMetadataRetriever();  
		try {// MODE_CAPTURE_FRAME_ONLY  
		//          retriever  
		//                  .setMode(android.media.MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);  
		//          retriever.setMode(MediaMetadataRetriever.MODE_CAPTURE_FRAME_ONLY);  
			retriever.setDataSource(filePath);  
		//          bitmap = retriever.captureFrame();  
			String timeString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);  
			/*long time = Long.parseLong(timeString) * 1000;  */
			long time = Long.parseLong(timeString) * 1000;  
		//Log.i("TAG","time = " + time);  
			
			bitmap = retriever.getFrameAtTime(time*31/160); //按视频长度比例选择帧  
			btnScreenShot.setText(time+ "");
				
			} catch (IllegalArgumentException ex) {  
		// Assume this is a corrupt video file  
		} catch (RuntimeException ex) {  
		// Assume this is a corrupt video file.  
		} finally {  
			try {  
				retriever.release();  
			} catch (RuntimeException ex) {  
		// Ignore failures while cleaning up.  
			}  
		}  
		return bitmap;  
	}  

	
	private void screenshot()
	{
		// 获取屏幕
		View dView = getWindow().getDecorView();  
		dView.setDrawingCacheEnabled(true);   
		dView.buildDrawingCache();
        /*Bitmap bmp = dView.getDrawingCache();*/
		Bitmap bmp;
		if(mPlayer.isPlaying)
			bmp = createVideoThumbnail(mPlayer.filePath);
		else 
			bmp = dView.getDrawingCache();
        if (bmp != null)
        {
        	try {
        		// 获取内置SD卡路径
        		String sdCardPath = Environment.getExternalStorageDirectory() + "/mRecorder/pic";
        		// 图片文件路径
        		long  time=System.currentTimeMillis();
        		String filePath = sdCardPath + "/" + time +".png";
        		
        		File file = new File(filePath);
        		FileOutputStream os = new FileOutputStream(file);
        		bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        		os.flush();
        		os.close();
			} catch (Exception e) {
			}
        }
	}
	
	public static void creatFile(){
		String mkdir = Environment.getExternalStorageDirectory() + "/mRecorder/pic";
		File file = new File(mkdir);
		if(!file.exists()){
		file.mkdirs();
		}
	}
	
	Callback surfaceHolderCallback = new Callback() {

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			surfaceView = null;
			surfaceHolder = null;
			mRecorder = null;
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			surfaceHolder = arg0;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			surfaceHolder = arg0;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void refreshViewByRecordingState() {
		if (mRecorder.isRecording) {
			mRecorder.isRecording = true;
			
		} else {
			mRecorder.isRecording = false;
			/*if(!"".equals(path))
				btRecorder.setText(path);*/
			
		}
	}
	
	OnClickListener mRecordingClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (!mRecorder.isRecording) {
				ibtnRecoding.setImageDrawable(getResources().getDrawable(R.drawable.end_recorder));
				new Thread(){
					public void run(){
					try {
						sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					}.start();
				mRecorder.startRecording(surfaceView);
				mRecorder.isRecording = true;
				
				refreshViewByRecordingState();
				////////////////////////////
				/*String sdpath = Environment.getExternalStorageDirectory().toString();
				if(!"".equals(sdpath))
				btRecorder.setText(sdpath);*/
			} else {
				mRecorder.stopRecording();
				mRecorder.isRecording = false;
				ibtnRecoding.setImageDrawable(getResources().getDrawable(R.drawable.recording));
				/*if(!"".equals(path))
				btRecorder.setText(path);*/
				refreshViewByRecordingState();
			}

		}
	};
	
	/*OnClickListener fileBackClick = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			setContentView(R.layout.search_list);
			InitView();
			}
		};*/
	
	OnClickListener fileListClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			/*String pathy = Environment.getExternalStorageDirectory() + "/mRecorder/video/mov_-1410132000_19s.mp4";
			mPlayer.play(pathy, surfaceView);*/
			setContentView(R.layout.search_list);  
	        InitView();
	        file_list.setOnItemClickListener(itemListenerfile);
		}
	};
	
	OnItemClickListener itemListenerfile = new OnItemClickListener() {
		//读取文件夹下的word文件
		@SuppressWarnings("deprecation")
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub						
				String filename=filterDateList.get(position).toString();
				filePath=infopath + "/video/" + filename;
				   /* 取得扩展名 */  
				/*String end=filename.substring(filename.lastIndexOf(".")+1,  
						   filename.length()).toLowerCase();*/   
				/*if(end.equals("doc")){
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, InformationScanViewFile.class);				
					startActivity(intent);
					Util.pathfile=filePath;
				}else if(end.equals("pdf")){					
					getPdfFileIntent(filePath);
				}else */
			/*if(end.equals("mp4")){	*/
					/*File file = new File(filePath);
					Intent intent = getVideoFileIntent(file);
			    	startActivity(intent);*/
					setContentView(R.layout.player);
					surfaceView = (SurfaceView) findViewById(R.id.surfaceView2); 
					
					mPlayer.play(Environment.getExternalStorageDirectory() + "/mRecorder/video/mov_-1410132000_19s.mp4",surfaceView);		
			/*}*/
	    }
	};
	
	/*public void foundFile(){
		String mFileName = Environment.getExternalStorageDirectory() + "/mRecorder/video";
		File f = new File(mFileName);
	
		String[] found = f.list();
		if(found.length!=0)
			btRecorder.setText(found.length+"");
		else
			btRecorder.setText("mmpmmp");
		for(int i = 0; i<found.length; i ++){
			
		}
		}*/
	
	private void InitView(){
		file_list=(ListView)this.findViewById(R.id.file_list);	 
		try {
			LoadData();
		} catch (Exception e) {
			Toast.makeText(this, "未获取到信息", Toast.LENGTH_LONG).show();
		}
	  }
	
	private void LoadData() throws Exception {  
		
		boolean hasSDCard = Environment.getExternalStorageState().equals(
				
				Environment.MEDIA_MOUNTED);
		if (hasSDCard) {
			infopath = Environment.getExternalStorageDirectory()
					+ "/mRecorder";
		} else {
			infopath = Environment.getDownloadCacheDirectory()
					+ "/mRecorder";
		}
		File file = new File(infopath);
		if (!file.exists()) {
			//mkdir()只能在已经存在的目录中创建文件夹
			//mkdirs()可以在不存在的目录下创建多级的文件夹
			file.mkdirs();
		}
		getFileList(file, search_result);
		file_list.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,search_result)); ////绑定适配器 	
		filterDateList=search_result;
	  }	
	  
	private void getFileList(File path, ArrayList<String> search_result){  
     //如果是文件夹的话   
     if(path.isDirectory()){     
         //返回文件夹中有的数据      
         File[] files = path.listFiles();      
         //先判断下有没有权限，如果没有权限的话，就不执行了     
         if(null == files)    
             return;
         for(int i = 0; i < files.length; i++){     
             getFileList(files[i], search_result);      
         }                  
     }  
     //如果是文件的话直接加入     
     else{
         //进行文件的处理     
         String filePath = path.getAbsolutePath();     
         //文件名      
         String fileName = filePath.substring(filePath.lastIndexOf("/")+1);      
         //添加    
         search_result.add(fileName);               
     }  
 }
	  
	private void filterData(String filterStr){		
		//break是结束当前的循环，但是循环体后面的部分还是会执行
		filterDateList=new ArrayList<String>();
		if(filterDateList==null){
			return; //return是返回的意思，就是跳出当前执行的方法,不在执行后面的语句；
			}	 	
		if(!TextUtils.isEmpty(filterStr)){	
			filterDateList.clear();		 
			for (int i = 0; i < search_result.size(); i++) {
				if(search_result.get(i).contains(filterStr)){
				filterDateList.add(search_result.get(i));
				}
			}
			}else
				filterDateList=search_result;
			getDataFilter(filterDateList);		
			file_list.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,filterDateList)); ////绑定适配器 						
		}
	  
	  /*public Intent getVideoFileIntent(File videoFile)
	    {
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        intent.putExtra("oneshot", 0);
	        intent.putExtra("configchange", 0);
	        Uri uri = Uri.fromFile(videoFile);
	        intent.setDataAndType(uri, "video/*");
	        return intent;
	    }*/
	  
	
	
	private void getDataFilter(ArrayList<String> fieldlist) {
		itemdatafilter=new ArrayList<HashMap<String, Object>>();
		if(fieldlist.size()==0)
			/*Toast.makeText(this, "没有您要查找的信息", 3000).show();*/
			Toast.makeText(this, "没有您要查找的信息", 3000).show();
		for (int i = 0; i < fieldlist.size(); i++) {
			try {
				HashMap<String, Object> map = new HashMap<String, Object>();								
				map.put("list", fieldlist.get(i));
				itemdatafilter.add(map);				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*class MyAdatpter extends BaseAdapter {
		String path;
		File[] lst;

		public MyAdatpter() {
			super();
			query();
		}

		public void query() {
			String mFileName = Environment.getExternalStorageDirectory() + "/mRecorder/video";
			File f = new File(mFileName);
			File[] found = f.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().startsWith("mov_")) {
						path = pathname.getAbsolutePath();
						return true;
					}
					return false;
				}
			});

			lst = new File[found.length];
			for (int i = 0; i < found.length; i++) {
				lst[i] = found[found.length - i - 1];
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return lst == null ? 0 : lst.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = getLayoutInflater().inflate(R.layout.item, null);
			TextView txtText = (TextView) v.findViewById(R.id.txtText);
			TextView txtSize = (TextView) v.findViewById(R.id.txtSize);

			File f = lst[arg0];
			txtText.setText(f.getName());
			txtSize.setText(f.length() / 1024 + " kb");

			return v;
		}

	}*/

}


