package com.trible.scontact.networks;

import com.trible.scontact.utils.Bog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

public class SimpleAsynTask{

	@SuppressLint("NewApi")
	public static void doTask2(AsynTaskListner listner){
		if ( listner == null ){
			return;
		}
		MyAsyn mAsyncTask = new MyAsyn(listner);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mAsyncTask.execute();
		}
	}
	public interface AsynTaskListner{
		
		void doInBackground();
		void onTaskDone(NetWorkEvent event);
	}
	
	MyAsyn mAsyncTask;
	Activity mActivity;
	
	public void cancelTask(){
		if ( mAsyncTask != null && !mAsyncTask.isCancelled() )
		mAsyncTask.cancel(true);
	}
	
	@SuppressLint("NewApi")
	public void doTask( AsynTaskListner listner){
		if ( listner == null ){
			return;
		}
		mAsyncTask = new MyAsyn(listner);
//		DiigoHttpClient.ds.registerListener(mAsyncTask);
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
			mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mAsyncTask.execute();
		}
		
	}
	public static class MyAsyn extends AsyncTask<String, Object, String> 
								implements NetWorkExceptionListenser{

		AsynTaskListner mListner;
		NetWorkEvent handlerEvent = null;
		
		public MyAsyn(AsynTaskListner listner){
			mListner = listner;
			
		}
		@Override
		protected String doInBackground(String... params) {
			mListner.doInBackground();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			mListner.onTaskDone(handlerEvent);
			
			super.onPostExecute(result);
		}
		@Override
		public void handleEvent(NetWorkEvent ne) {
			Bog.e("handleEvent = " + ne);
			handlerEvent = ne;
			publishProgress(ne);
		}
		@Override
		protected void onProgressUpdate(Object... values) {

			super.onProgressUpdate(values);
		}
		
	}
}
