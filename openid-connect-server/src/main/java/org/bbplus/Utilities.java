package org.bbplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Utilities {
	private static String bbPath = "/WEB-INF/classes/blue_button_plus/";
	
	public static DateFormat iso8601 =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
	
	@Autowired private ApplicationContext applicationContext;
	@Autowired private ConfigurationPropertiesBean config;


	public Resource[] sampleFiles() throws IOException{
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(applicationContext);
		Resource[] resources = resolver.getResources(Utilities.bbPath+"documents/sample-ccda-*.xml");
		return resources;
	}

	public Document getXmlDocument(long id) {

		Resource doc = applicationContext.getResource(Utilities.bbPath+"documents/sample-ccda-"
				+String.format("%04d",id)+".xml");
		
		Document docroot = null;
		
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			dbfactory.setNamespaceAware(true);
			docroot = dbfactory.newDocumentBuilder().parse(doc.getInputStream());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return docroot;
	}


	public String getXmlRaw(long id) {
		
		Resource doc = applicationContext.getResource(Utilities.bbPath+"documents/sample-ccda-"
				+String.format("%04d",id)+".xml");
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(doc.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		StringBuilder builder = new StringBuilder();
		String aux = "";

		try {
			while ((aux = reader.readLine()) != null) {
			    builder.append(aux);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}


	public JsonObject getJsonFile(String filename) {
		Resource template = applicationContext.getResource(Utilities.bbPath+filename);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(template.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JsonObject templateObject = (JsonObject) new JsonParser().parse(reader);
		return templateObject;
		
	}

}
