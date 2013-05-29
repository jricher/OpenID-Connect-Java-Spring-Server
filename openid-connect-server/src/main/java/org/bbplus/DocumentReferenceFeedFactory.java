package org.bbplus;

import java.util.Date;

import org.bbplus.DocumentReferenceFactory.DocumentReference;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collection;

public class DocumentReferenceFeedFactory{
	
	@Autowired private Utilities bbUtilities ;
	
	public DocumentReferenceFeed fromDocuments(Collection<DocumentReference> docs){
		
		JsonObject feedTemplate = bbUtilities.getJsonFile("templates/feed.json");
		JsonObject entryTemplate = bbUtilities.getJsonFile("templates/entry.json");
		
		DocumentReferenceFeed ret = new DocumentReferenceFeed();
		ret.setDocs(docs);
		ret.setFeedTemplate(feedTemplate);
		ret.setEntryTemplate(entryTemplate);
		
		return ret;
	}

	public class DocumentReferenceFeed {
		private Collection<DocumentReference> docs;
		private JsonObject feedTemplate;
		private JsonObject entryTemplate;
		
		public Collection<DocumentReference> getDocs() {
			return docs;
		}

		public void setDocs(Collection<DocumentReference> docs) {
			this.docs = docs;
		}

		public JsonObject getFeedTemplate() {
			return feedTemplate;
		}

		public void setFeedTemplate(JsonObject template) {
			this.feedTemplate = template;
		}
		
		public JsonObject toJson(){
			feedTemplate.addProperty("updated", Utilities.iso8601.format(new Date()));
			
			for (DocumentReference d : docs){
				
				JsonObject jsonDoc = (JsonObject) new JsonParser().parse(
						new Gson().toJson(entryTemplate));
				
				jsonDoc.addProperty("title", d.getDescription());
				jsonDoc.addProperty("updated",  Utilities.iso8601.format(new Date()));
				jsonDoc.add("content", d.toJson());
				
				feedTemplate.getAsJsonArray("entry").add(jsonDoc);	
			}
						
			return feedTemplate;
		}

		public JsonObject getEntryTemplate() {
			return entryTemplate;
		}

		public void setEntryTemplate(JsonObject entryTemplate) {
			this.entryTemplate = entryTemplate;
		}
		
	}
	
}

