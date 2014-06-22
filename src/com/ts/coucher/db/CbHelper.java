package com.ts.coucher.db;

import static com.ts.coucher.util.Keys.DB_NAME;
import android.content.Context;

import com.ts.coucher.processor.AttachProcessor;
import com.ts.coucher.processor.CRUDProcessor;
import com.ts.coucher.processor.ReplicaProcessor;
import com.ts.coucher.util.Typewriter;

/**
 *  Test a bunch of CBL features
 * <ul>
 * <li>CRUD operations</li>
 * <li>Attachments</li>
 * <li>Replication</li>
 * <li>etc.</li>
 * </ul>
 * 
 * @author Nomad
 *
 */
public class CbHelper {
	
	private android.content.Context ctx;
	private CbDatabase db;


	/**
	 * Ctor
	 * @param ctx
	 */
	public CbHelper(Context ctx) {
		this.ctx = ctx;
		db = new CbDatabase(DB_NAME, ctx);
	}
	
	/**
	 * Calls various tests and closes the DB
	 * 
	 * @param ouput
	 * */ 
	public void process(Typewriter output){
		
		String txt = output.getText().toString();
		try {
			// continuous text
			txt = new CRUDProcessor().doCRUD(db, output, txt); // TESTED OK
			
			//txt = new AttachProcessor().doAttachments(db, ctx, output, txt); // TESTED OK
			
			// NOT TESTED (needs remote db address)
			//txt = new ReplicaProcessor().doReplication(db, ctx, output, txt); 
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