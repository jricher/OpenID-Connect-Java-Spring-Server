package org.bbplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;



public class DocumentReferenceFactory{
	
	@Autowired
	Utilities bbUtilities;
	
	public DocumentReference loadFromFile(Long id, String location){
		DocumentReference ret = new DocumentReference();
				
		Document docroot = bbUtilities.getXmlDocument(id);
		
		@SuppressWarnings("serial")
		SimpleNamespaceContext namespaces = new SimpleNamespaceContext(new HashMap<String, String>() {{
		    put("h", "urn:hl7-org:v3");
		}});
				
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(namespaces);
		String created = null;
		String title = null;
		try {
			created = (String) xpath.evaluate("/h:ClinicalDocument/h:effectiveTime/@value", docroot, XPathConstants.STRING);
			title = (String) xpath.evaluate("/h:ClinicalDocument/h:title", docroot, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SimpleDateFormat hl7date = new SimpleDateFormat("yyyyMMdd");
		
		try {
			ret.setCreated(hl7date.parse(created.substring(0, 8)));
			ret.setIndexed(hl7date.parse(created.substring(0, 8)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ret.setDescription(title);
		ret.setLocation( location );
		ret.setTemplate(bbUtilities.getJsonFile("templates/documentreference.json"));
		
		return ret;	
	}



	public class SimpleNamespaceContext implements NamespaceContext {

	    private final Map<String, String> PREF_MAP = new HashMap<String, String>();

	    public SimpleNamespaceContext(final Map<String, String> prefMap) {
	        PREF_MAP.putAll(prefMap);       
	    }

	    public String getNamespaceURI(String prefix) {
	        return PREF_MAP.get(prefix);
	    }

	    public String getPrefix(String uri) {
	        throw new UnsupportedOperationException();
	    }

	    public Iterator<String> getPrefixes(String uri) {
	        throw new UnsupportedOperationException();
	    }

	}


public class DocumentReference {

	private Date created;
	private Date indexed;
	private String location;
	private String description;
	
	private JsonElement template;

	
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getIndexed() {
		return indexed;
	}
	public void setIndexed(Date indexed) {
		this.indexed = indexed;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public JsonElement toJson(){
		JsonObject je = (JsonObject) template;
		je.getAsJsonObject("DocumentReference").getAsJsonObject("description").addProperty("value", description);
		je.getAsJsonObject("DocumentReference").getAsJsonObject("created").addProperty("value", Utilities.iso8601.format(created));
		je.getAsJsonObject("DocumentReference").getAsJsonObject("indexed").addProperty("value", Utilities.iso8601.format(indexed));
		je.getAsJsonObject("DocumentReference").getAsJsonObject("location").addProperty("value", location);
		return je;
	}
	public JsonElement getTemplate() {
		return template;
	}
	public void setTemplate(JsonElement template) {
		this.template = template;
	}

}

	
}

