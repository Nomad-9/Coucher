package com.ts.coucher.db;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import com.couchbase.lite.*;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.ts.coucher.R;
import com.ts.coucher.util.ErrorChecker;
import com.ts.coucher.util.Keys.Replica;
import com.ts.coucher.util.Keys.Span;

import android.content.Context;

/**
 * CouchBase Lite database wrapper
 * @author Nomad
 *
 */
public class CbDatabase {
    
	private Database database;
	private android.content.Context ctx;
	private Manager manager;
	// keep a reference to a running replication to avoid GC
	private Replication replica;
	
    /**
     * Ctor
     * @param dbname
     * @param ctx
     */
	public CbDatabase(String dbname, Context context){
		
		this.ctx = context;
		/* Manages access to databases */
		try {
		    manager = new Manager( new AndroidContext(ctx), Manager.DEFAULT_OPTIONS );
		} 
		catch (IOException e) {
			ErrorChecker.ShowException(ctx, R.string.err_create_manager, e );
		    return;
		}		
		// create a name for the database and make sure the name is legal
		// Only the following characters are valid: 
		// abcdefghijklmnopqrstuvwxyz0123456789_$()+-/
		if ( ! Manager.isValidDatabaseName(dbname)) {
			ErrorChecker.showError( ctx, R.string.err_db_name );
		    return;
		}		
		// get existing db with that name
		// or create a new one if it doesn't exist		
		try {
		    database = manager.getDatabase(dbname);
		} 
		catch (CouchbaseLiteException e) {
			ErrorChecker.ShowException(ctx, R.string.err_no_db, e );
		    return;
		}      
	}
	
	/** Release all resources and close all Databases. */
	public void close(){
		if(manager != null){
			manager.close();
		}
	}
	
	/* Replication *********************************/
	
	/**
	 * Create a push/pull Replica that is continuous/one-shot
	 * @param remote URL (Server or P2P)
	 * @param push: local DB --> remote DB  
	 *        pull: remote DB --> local DB
	 * @param continuous: stay active indefinitely,
	 *        one-shot: transfer all changes, then quit.
	 * @return
	 */
	public Replication startReplica( URL remote, Replica pushOrPull, Span span){
		
		replica = (pushOrPull == Replica.PUSH) 
				   ? database.createPushReplication(remote)
				   : database.createPullReplication(remote);
		
		replica.setContinuous(span == Span.CONTINUOUS ? true : false);
		replica.start();//a replication runs asynchronously
		
		return replica;
	}
	
	/**
	 * A replica can be active/stopped/off-line/idle
	 * @param rep
	 * @return
	 */
	public boolean isReplicaActive( Replication rep){
		return rep != null && (rep.getStatus() == 
				Replication.ReplicationStatus.REPLICATION_ACTIVE);
	}
	
	/* Attachments *********************************/
	
	/**
	 * Write an Attachment for a given Document
	 * @param docId
	 * @param attachName
	 * @param mimeType  e.g. "image/jpeg"
	 * @param stream
	 */
	 public void writeAttachment(String docId, String attachName, 
			                    String mimeType, InputStream in){
		
		 try {
			 Document doc = database.getDocument(docId);
			 UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
			 newRev.setAttachment(attachName, mimeType, in);
			 newRev.save();
		} 
		 catch (CouchbaseLiteException e) {
			 ErrorChecker.ShowException(ctx, R.string.err_write_attach, e );
		}
	 }
	 
	 
	/**
	 * Get a given Document's attachment if any
	 * @param docId
	 * @param attchName
	 * @return Attachment
	 */
	public Attachment getAttachment(String docId, String attachName){
		
		Document doc = database.getDocument(docId);
		Revision rev = doc.getCurrentRevision();
		return rev.getAttachment(attachName);
	}
	
	/**
	 * Remove an Attachment from a Document
	 * @param docId
	 * @param attachName
	 */
	public void deleteAttachment(String docId, String attachName){
		
		try {
			Document doc = database.getDocument(docId);
			UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
			newRev.removeAttachment(attachName);
			// (You could also update newRev.properties while you're here)
			newRev.save();
		} 
		catch (CouchbaseLiteException e) {
			 ErrorChecker.ShowException(ctx, R.string.err_delete_attach, e );
		}
	}
	
	
	/* CRUD Operations *********************************/
	
	/**
	 * C-rud
	 * @param docContent
	 * @return docId
	 */
	public String create( Map<String, Object> docContent ){
		
		if( ! ErrorChecker.checkDb(ctx, database)){
			return null;
		}		
		// create an empty document
		Document doc = database.createDocument();
		// add content to document and write the document to the database
		try {
		    doc.putProperties(docContent);
		} 
		catch (CouchbaseLiteException e) {
			ErrorChecker.ShowException(ctx, R.string.err_db_write, e ) ;
			return null;
		}
		return doc.getId();   
	}
	
	/**
	 * c-R-ud
	 * @param docId
	 * @return Doc content
	 */
	public Map<String, Object> retrieve(String docId){
		
		if( ! ErrorChecker.checkDb(ctx, database)){
			return null;
		}			
		// retrieve the document from the database
		Document doc = database.getDocument(docId);
		// display the retrieved document
		return doc.getProperties(); 
	}
	
	
	/**
	 * cr-U-d
	 * @param key
	 * @param value
	 * @param docId
	 * @return success or failure
	 */
	public boolean update( final String key, final Object value, String docId ){
		
		if( ! ErrorChecker.checkDb(ctx, database)){
			return false;
		}			
		// update the document    
		try {
			Document doc = database.getDocument(docId);
			
			// this alternative way is better for handling write conflicts 
			doc.update(new Document.DocumentUpdater() {
			    @Override
			    public boolean update(UnsavedRevision newRevision) {
			    	Map<String, Object> properties = newRevision.getUserProperties();
			    	properties.put(key, value);
			    	newRevision.setUserProperties(properties);
			        return true;
			    }
			});
			
		/*	Map<String, Object> docContent = doc.getProperties();
			//Working on a copy 
			Map<String, Object> updatedContent = new HashMap<String, Object>();
			updatedContent.putAll(docContent); 
			updatedContent.put(key, value);
			doc.putProperties(updatedContent);*/
		} 
		catch (CouchbaseLiteException e) {
			ErrorChecker.ShowException(ctx, R.string.err_db_update, e ) ;
		    return false;
		}
		return true;
	}
	
	
	/**
	 * cru-D
	 * @param docId
	 * @return
	 */
	public boolean delete(String docId){
		
		if( ! ErrorChecker.checkDb(ctx, database)){
			return false;
		}
		Document doc = null;
		// delete the document
		try {
			doc = database.getDocument(docId);
		    doc.delete();
		} 
		catch (CouchbaseLiteException e) {
			ErrorChecker.ShowException(ctx, R.string.err_db_delete, e ) ;
		} 
		return  doc.isDeleted();
	}
	
}
