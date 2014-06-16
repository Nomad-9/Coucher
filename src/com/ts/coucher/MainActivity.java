package com.ts.coucher;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import com.ts.coucher.db.CbDatabase;
import com.ts.coucher.util.Typewriter;

/**
 * 
 * @author Nomad
 *
 */
public class MainActivity extends ActionBarActivity {
	
	private CbDatabase db;
	private static final String DB_NAME = "testdb";
	private static final String KEY_MAIL = "email";
	private static final String KEY_REG = "registered";
	private static final String KEY_SCORES = "scores";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// holds output text
		Typewriter output = (Typewriter) findViewById(R.id.output);		
		//Add a character every 35ms
		output.setCharacterDelay(35);				
		String txt = output.getText().toString();
		
		// db setup
		output.animateText( txt += ("\n\nBEGIN: ") );		
		db = new CbDatabase(DB_NAME, this);
		
		output.animateText( txt += ("\n\nCREATE --> ") );		
		// get the current date and time
		Date now = new Date();
		String nowString = DateFormat.getDateTimeInstance(
	            DateFormat.LONG, DateFormat.LONG).format(now);

		List<Double> scores = new ArrayList<Double>();
		scores.add(190.00);
		scores.add(210.00);
		scores.add(250.00);
		scores.add(275.00);
		
		// create an object that contains data for a document
		Map<String, Object> docContent = new HashMap<String, Object>();
		docContent.put(KEY_MAIL, "Nomad@nomad.com");
		docContent.put(KEY_REG, nowString);
		docContent.put(KEY_SCORES, scores);
		
		String docId = null;
		
		try {
			// 1. Create
			docId = db.create(docContent);  
			assert(docId != null);
			output.animateText( txt += ("Created doc with id " + docId + "\n") );

			output.animateText( txt += ("\n\nRETRIEVE --> ") );
			// 2. Retrieve
			docContent= db.retrieve(docId);
			assert(docContent != null);							
			output.animateText( txt += ("Retrieved Doc " + String.valueOf(docContent) + "\n") );

			output.animateText( txt += ("\n\nUPDATE --> ") );
			// 3. Update
			scores.add(350.00);
			db.update(KEY_SCORES, scores, docId);
			Map<String, Object> updatedContent = db.retrieve(docId); // verify update
			output.animateText( txt += ("Updated content: " + String.valueOf(updatedContent) + "\n") );

			output.animateText( txt += ("\n\nDELETE --> ") );
			// 4. Delete
			boolean deleted = db.delete(docId);
			assert(deleted == true);
			output.animateText( txt += ("Deleted document with id: " + docId + "\n") );
			
			output.animateText( txt += ("\n\nSUCCESS.") );
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		finally{
			if(db != null){
			   output.animateText( txt += ("\n\nClosing databases...") );
			   db.close();
			   output.animateText( txt += ("DONE.\n") );
			}
		}
		
	    // scroll down automatically to last print
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
		// display success
		//Toast.makeText(this, R.string.db_success, Toast.LENGTH_LONG).show();
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
