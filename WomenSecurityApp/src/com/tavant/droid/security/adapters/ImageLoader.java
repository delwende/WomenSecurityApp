

package com.tavant.droid.security.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tavant.droid.security.R;
import com.tavant.droid.security.utils.Utils;



public class ImageLoader {

	// the simplest in-memory cache implementation. This should be replaced with
	// something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	private HashMap<String, WeakReference<Bitmap>> cache = new HashMap<String,WeakReference<Bitmap>>();

	private int FILE_LIMIT = 100;
	private ArrayList<String> cacheKey = new ArrayList<String>();
	private static ImageLoader mImageLoader;
	private static String FolerName = null;

	public static ImageLoader getInstance() {
		if (mImageLoader == null)
			mImageLoader = new ImageLoader();
		return mImageLoader;
	}

	public void setSysFilePath(String path){
		try{
			File tempDirectory=new File(path+"/VIVOX_dir/");
			if(!tempDirectory.exists())
				tempDirectory.mkdir();
			FolerName = tempDirectory.getAbsolutePath()+"/";
		}catch (Exception e) {
		}
	}


	private ImageLoader() {
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
	}

	public void DisplayImage(String url, Context activity, ImageView imageView,ProgressBar progress) {
		if(FolerName == null){
			File path = ((Activity)activity).getFilesDir();
			setSysFilePath(path.getAbsolutePath());
		}
		if (cache.containsKey(url)){
			if(cache.get(url).get()==null){	
				String filename = null;
				try {
					filename = String.valueOf(url.hashCode());
					File f = new File(FolerName, filename);
					if (((String) imageView.getTag(R.string.ImageUrl)).equals(url))
						imageView.setImageBitmap(decodeFile(f));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				if (((String) imageView.getTag(R.string.ImageUrl)).equals(url))
					imageView.setImageBitmap(cache.get(url).get());
			}
			if(progress!=null)
				progress.setVisibility(View.GONE);
		}else {
			if(progress!=null)
				progress.setVisibility(View.VISIBLE);
			imageView.setImageResource(R.drawable.com_facebook_profile_default_icon);
			queuePhoto(url, activity, imageView,progress);
		}
	}



	private void queuePhoto(String url, Context activity, ImageView imageView,ProgressBar bar) {
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView,bar);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}

		try{
			if(!RunThread && photoLoaderThread.getState() == Thread.State.TERMINATED){
				RunThread = true;
				photoLoaderThread = new PhotosLoader();
			}
		}catch (Exception e) {
			if(!RunThread){
				RunThread = true;
				photoLoaderThread = new PhotosLoader();
			}
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	private Bitmap getBitmap(String url,Context ctx) {
		// I identify images by hashcode. Not a perfect solution, good for the
		// demo.
		String filename = null;
		try {
			filename = String.valueOf(url.hashCode());
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		File f=new File(FolerName,filename);
		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;
		// from web
		InputStream is = null;
		OutputStream os = null;
		HttpURLConnection connection=null;
		try {
			Bitmap bitmap = null;			
			connection = (HttpURLConnection)(new URL(url)).openConnection();
			is = connection.getInputStream();

			os=new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			try {
				if (is != null)
					is.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale++;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public ProgressBar progress;
		public PhotoToLoad(String u, ImageView i,ProgressBar p) {
			url = u;
			imageView = i;
			progress = p;
		}
	}

	private PhotosQueue photosQueue = new PhotosQueue();

	public void stopThread() {
		RunThread = false;
		photoLoaderThread.interrupt();
	}

	// stores list of photos to download
	class PhotosQueue {
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();
		// removes all instances of this ImageView
		public void Clean(ImageView image) {
			for (int j = 0; j < photosToLoad.size();) {
				try	{
					if (photosToLoad.get(j).imageView == image)
						photosToLoad.remove(j);			
					else
						++j;
				}catch(ArrayIndexOutOfBoundsException e){
					e.printStackTrace();
				}
			}
		}
	}

	class PhotosLoader extends Thread {

		public void run() {
			try {
				while (RunThread) {
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0) {
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						if(photoToLoad.url != null ){
							Log.i("TAG","imageurl"+photoToLoad.url);
							Bitmap bmp = getBitmap(photoToLoad.url,photoToLoad.imageView.getContext());
							if (bmp != null) {
								if (cache.size() == FILE_LIMIT) {
									clearCache();
								}
								if (cacheKey.contains(photoToLoad.url))
									cacheKey.remove(photoToLoad.url);
								cache.put(photoToLoad.url, new WeakReference<Bitmap>(bmp));
								cacheKey.add(photoToLoad.url);
							}
							try {
								if (((String) photoToLoad.imageView.getTag(R.string.ImageUrl)).equals(photoToLoad.url)) {
									BitmapDisplayer bd = new BitmapDisplayer(bmp,photoToLoad.imageView,photoToLoad.progress);
									Activity a = (Activity) photoToLoad.imageView.getContext();
									a.runOnUiThread(bd);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (Thread.interrupted())
							break;
					}
				}
			} catch (InterruptedException e) {
				// allow thread to exit
			}
		}
	}

	private PhotosLoader photoLoaderThread = new PhotosLoader();
	private boolean RunThread = true;

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;
		ProgressBar progressBar;
		public BitmapDisplayer(Bitmap b, ImageView i,ProgressBar bar) {
			bitmap = b;
			imageView = i;
			progressBar = bar;
		}
		public void run() {
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
				if(progressBar!=null)
					progressBar.setVisibility(View.GONE);
			} else {
				imageView.setImageResource(R.drawable.com_facebook_profile_default_icon);
			} 
		}
	}

	private void clearCache() {
		// clear memory cache
		String url = cacheKey.get(0);
		// cache.clear();
		cache.remove(url);
		cacheKey.remove(0);

		String filename = null;
		try {
			filename = String.valueOf(url.hashCode());
			File f = new File(FolerName, filename);
			if (f != null && f.isFile()) {
				f.delete();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}


	public void emptyCache(){
		try {
			File tempDirectory=new File(FolerName);
			tempDirectory.mkdir();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
