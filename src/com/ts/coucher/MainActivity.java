package com.ts.coucher;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ts.coucher.db.CbHelper;
import com.ts.coucher.util.Typewriter;

/**
 * 
 * @author Nomad
 *
 */
public class MainActivity extends ActionBarActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// holds output text
		Typewriter output = (Typewriter) findViewById(R.id.output);		
		//Add a character every 35ms
		output.setCharacterDelay(35);	
		
		CbHelper helper = new CbHelper(this);
		helper.process(output);
		scrollDown();
			
		// display success
		//Toast.makeText(this, R.string.db_success, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Scroll down automatically to last print
	 */
	private void scrollDown(){
		
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.scroller));
		scrollview.getViewTreeObserver()
		          .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scrollview.post(new Runnable() {
                    public void run() {
                        scrollview.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_create) {
			toDo();
			return true;
		}
		if (id == R.id.action_retrieve) {
			toDo();
			return true;
		}
		if (id == R.id.action_update) {
			toDo();
			return true;
		}
		if (id == R.id.action_delete) {
			toDo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** */
	private void toDo(){
		//TODO
		Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT).show();
	}

}
