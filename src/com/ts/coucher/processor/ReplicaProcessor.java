package com.ts.coucher.processor;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;

import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.BasicAuthenticator;
import com.couchbase.lite.replicator.Replication;
import com.ts.coucher.db.CbDatabase;
import com.ts.coucher.util.Keys.Replica;
import com.ts.coucher.util.Keys.Span;
import com.ts.coucher.util.Typewriter;

/**
 * Process Replication
 * 
 * A Replication object represents a replication (or "sync") task 
 * that transfers changes between a local database and a remote one. 
 * 
 * A typical application will create a pair of replications(PUSH and PULL) 
 * at launch time, both pointing to the URL of a server.
 * 
 * <b>PUSH</b>: local DB on device --> remote DB on Server
 * <b>PULL</b>: remote DB --> local DB
 * 
 * <b>One-shot</b>: replication runs long enough to transfer all the changes 
 * then quits.
 * <b>Continuous</b>:  will stay active indefinitely, watching for further changes 
 * to occur and transferring them.
 * <b>Filtered</b>: Replications can have filters that restrict what documents 
 * they'll transfer. 
 * 
 * @author Nomad
 *
 */
public class ReplicaProcessor {
	
	/**
	 * 
	 * @param db
	 * @param ctx
	 * @param output
	 * @param txt
	 * @return
	 */
	public String doReplication(final CbDatabase db, Context ctx, Typewriter output, String txt){
		
		output.animateText( txt += ("\n\n------------------------\n\n") );
		output.animateText( txt += ("REPLICATION\n\n ") );
		
		try {
			URL remote = new URL("https://example.com/mydatabase/"); // TODO
			// one-shot push replica
			final Replication push = db.startReplica(remote, Replica.PUSH, Span.ONESHOT);
			output.animateText( txt += ("Created one-shot push replication\n ") );
			
			// one-shot pull replica
			final Replication pull = db.startReplica(remote, Replica.PULL, Span.ONESHOT);
			output.animateText( txt += ("Created one-shot pull replication\n ") );
			
			// Auth
			Authenticator auth = new BasicAuthenticator("nomad", "pass");
			push.setAuthenticator(auth);
			pull.setAuthenticator(auth);
			output.animateText( txt += ("Authenticating...\n ") );
			
			// Monitor the replication's progress
			final ProgressDialog progressDialog 
			      = ProgressDialog.show(ctx, "Please wait ...", "Sync-ing", false);
			
			pull.addChangeListener(new Replication.ChangeListener() {
			    @Override
			    public void changed(Replication.ChangeEvent event) {
			        // look at both the push and pull.
			        boolean active = db.isReplicaActive(push) ||
			                         db.isReplicaActive(pull);
			        if ( ! active) {
			            progressDialog.dismiss();
			        } 
			        else {
			            int total = push.getCompletedChangesCount() 
			            		   + pull.getCompletedChangesCount();
			            progressDialog.setMax(total);
			            progressDialog.setProgress(push.getChangesCount() 
			            		   + pull.getChangesCount());
			        }
			    }
			});
			
		} 
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO
		
		return txt;
	}

}
