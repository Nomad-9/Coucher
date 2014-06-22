package com.ts.coucher.processor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ts.coucher.db.CbDatabase;
import com.ts.coucher.util.Typewriter;

import static com.ts.coucher.util.Keys.*;

/**
 * Process CBL CRUD ops
 * @author Nomad
 *
 */
public class CRUDProcessor {

	/**
	 * 
	 * @param output
	 */
	public String doCRUD(CbDatabase db, Typewriter output, String txt) {
		
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
		return txt;
	}
}
