package com.ts.coucher.util;

import android.content.Context;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.couchbase.lite.util.Log;
import com.ts.coucher.R;

/**
 * Utility class for dealing with errors end exceptions.
 * 
 * @author Nomad
 *
 */
public class ErrorChecker {
	
	public static final String TAG = "Coucher";
	
	
	/**
	 * Check if db was created
	 * @return
	 */
	public static boolean checkDb(Context ctx, Database database){
		
		if(database == null){			
			 showError( ctx, R.string.err_no_db_set ) ;
			 return false;
		}
		return true;
	}
	
	/**
	 * Display err message
	 * @param errId
	 */
	public static void showError(Context ctx, int errId){

		Toast.makeText(ctx, ctx.getResources().getString(errId), Toast.LENGTH_LONG)
		.show();
	}
	
	/**
	 * 
	 * @param ctx
	 * @param errId
	 * @param e
	 */
	public static void ShowException (Context ctx, int errId, Exception e){
		
		showError( ctx, errId );
		Log.e (TAG, e.getMessage(), e);
	}

}
