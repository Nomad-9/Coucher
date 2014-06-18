package com.ts.coucher.db;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.ts.coucher.util.Typewriter;

public class CbHelper {
	public CbDatabase db;
	
	private static final String DB_NAME = "testdb";
	private static final String KEY_MAIL = "email";
	private static final String KEY_REG = "registered";
	private static final String KEY_SCORES = "scores";

	/**
	 * Ctor
	 * @param ctx
	 */
	public CbHelper(Context ctx) {
		db = new CbDatabase(DB_NAME, ctx);
	}
	
	/**
	 * 
	 * @param output
	 */
	public void doCRUD(Typewriter output) {
		String txt = output.getText().toString();
		
		output.animateText( txt += ("\n\nBEGIN CRUD: ") );		
				
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
	}

}