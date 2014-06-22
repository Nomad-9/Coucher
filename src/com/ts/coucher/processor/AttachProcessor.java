package com.ts.coucher.processor;

import static com.ts.coucher.util.Keys.KEY_MAIL;
import static com.ts.coucher.util.Keys.KEY_REG;
import static com.ts.coucher.util.Keys.KEY_SCORES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.ts.coucher.R;
import com.ts.coucher.db.CbDatabase;
import com.ts.coucher.util.Typewriter;

/**
 * Process CBL Attachments
 * 
 * Attachments are uninterpreted data (blobs) stored separately 
 * from the JSON body. 
 * Their primary purpose is to make it efficient to 
 * store large binary data in a document. 
 * 
 * A document can have any number of attachments, 
 * each with a different name. 
 * 
 * Attachments make replication more efficient. 
 * When a document with pre-existing attachments is sync-ed, 
 * only attachments that have changed since the last sync 
 * are transferred over the network.
 * 
 * @author Nomad
 *
 */
public class AttachProcessor {
	
	/**
	 * Write/Read/Delete Attachments
	 * @param output
	 */
	public String doAttachments(CbDatabase db, Context ctx, Typewriter output, String txt) {
		
		output.animateText( txt += ("\n\n------------------------\n\n") );
		output.animateText( txt += ("ATTACHMENTS\n\n ") );
		Date now = new Date();
		String nowString = DateFormat.getDateTimeInstance(
	            DateFormat.LONG, DateFormat.LONG).format(now);

		List<Double> scores = new ArrayList<Double>();
		scores.add(1990.00);
		scores.add(2110.00);
		
		// create an object that contains data for a document
		Map<String, Object> docContent = new HashMap<String, Object>();
		docContent.put(KEY_MAIL, "scorpio@nomad.com");
		docContent.put(KEY_REG, nowString);
		docContent.put(KEY_SCORES, scores);
		
		String docId = null;
		
		try {
			docId = db.create(docContent);  
			assert(docId != null);
			output.animateText( txt += ("Created Document with Id " + docId + "\n") );

			// 1. WRITE
			output.animateText( txt += ("\n\nWRITING ATTACH. --> ") );

			Bitmap scorpio =  BitmapFactory.decodeResource(ctx.getResources(), 
					R.drawable.scorpion);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			scorpio.compress(Bitmap.CompressFormat.PNG, 50, out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			db.writeAttachment(docId, "scorpion", "image/png", in);
			output.animateText( txt += ("...OK ") );


			// 2. READ
			output.animateText( txt += ("\n\nREADING ATTACH. --> ") );	

			Attachment att = db.getAttachment(docId, "scorpion");
			if (att == null) {
				output.animateText( txt += ("No attachments found. Aborting.\n\n") );
				return txt;
			}			
			InputStream is = att.getContent();
			Drawable dw = Drawable.createFromStream(is, "scorpion");
			// make image appear at the bottom of text
			// if it appears, it means that write/read are working as intended
			output.setCompoundDrawablesWithIntrinsicBounds(null, null, null, dw);
			output.animateText( txt += ("...OK ") );

			// verify that the attachment is not part of JSON Document:
			//output.animateText( txt += ("\nDocument's content: " + String.valueOf(docContent) + "\n") );

			// 3. DELETE

			output.animateText( txt += ("\n\nDELETING ATTACH. --> ") );
			db.deleteAttachment(docId, "scorpion");
			output.animateText( txt += ("DONE.\n") );
			output.animateText( txt += ("\nDeleting Document") );
			boolean deleted = db.delete(docId);
			assert(deleted == true);
			output.animateText( txt += ("...OK ") );

		} 
		catch (CouchbaseLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return txt;
	}

}
