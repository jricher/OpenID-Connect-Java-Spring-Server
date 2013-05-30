/*******************************************************************************
 * Copyright 2013 The MITRE Corporation 
 *   and the MIT Kerberos and Internet Trust Consortium
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.bbplus;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.bbplus.DocumentReferenceFactory.DocumentReference;
import org.bbplus.DocumentReferenceFeedFactory.DocumentReferenceFeed;
import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;


import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//TODO define an interface for each of the three BB API endpoints
// Rather than the hard-coded implementation below...

//1.  (User, structured scope) --> Summary Document XML content (String)
//2.  (User, structured scope, search params) --> DocumentReference Feed (JSON / Atom)
//3.  (User, structured scope, document ID) --> Document XML content (String)

@Controller
public class SearchEndpoint {

	@Autowired private ApplicationContext applicationContext;

	@Autowired
	private DocumentFactory documentFactory;	
	
	@Autowired
	private DocumentReferenceFactory documentReferenceFactory;	

	@Autowired
	private DocumentReferenceFeedFactory documentReferenceFeedFactory;	
	
	@Autowired
	private Utilities bbUtilities;	
	
	@Autowired
	private ConfigurationPropertiesBean config;

	// The built-in Spring expressions don't handle structured scopes...
	// This is a lightweight stopgap; relies on passing in the authentication object
	@PreAuthorize("hasRole('ROLE_USER') and @bbPermissions.summary(authentication)")
	@RequestMapping(value="/api/bb/summary", method = RequestMethod.GET, produces = "application/xml")
	public @ResponseBody String summary() throws IOException {
		return documentFactory.fromId(1);
	}

	
	@PreAuthorize("hasRole('ROLE_USER') and @bbPermissions.search(authentication)")
	@RequestMapping(value="/api/bb/search", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String search() throws IOException {
		
		String baseUrl = config.getIssuer() + "api/bb/documents/";
		
		Collection<DocumentReference> docs = new ArrayList<DocumentReference>();
		
		Pattern pattern = Pattern.compile("sample-ccda-(\\d{4})\\.xml");
		
		for (Resource r : bbUtilities.sampleFiles()){
			Matcher matcher = pattern.matcher(r.getFilename());
			matcher.matches();
			Long id = Long.parseLong(matcher.group(1));
			docs.add(documentReferenceFactory.loadFromFile(id, baseUrl+String.format("%04d",id)));
		}
		
		Gson gson = new GsonBuilder()
						.setPrettyPrinting()
						.create();
		
		DocumentReferenceFeed feed = documentReferenceFeedFactory.fromDocuments(docs);
		return gson.toJson(feed.toJson());
	}
	
	@PreAuthorize("hasRole('ROLE_USER') and @bbPermissions.search(authentication)")
	@RequestMapping(value="/api/bb/documents/{documentId}", method = RequestMethod.GET, produces = "application/xml")
	public @ResponseBody String retrieve(@PathVariable("documentId") long id) {
		
		return documentFactory.fromId(id);
	}
	
}
