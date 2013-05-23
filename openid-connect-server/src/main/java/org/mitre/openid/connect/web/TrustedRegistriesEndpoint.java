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
package org.mitre.openid.connect.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mitre.openid.connect.model.TrustedRegistry;
import org.mitre.openid.connect.repository.NonceRepository;
import org.mitre.openid.connect.repository.TrustedRegistryRepository;
import org.mitre.openid.connect.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.nimbusds.jose.jwk.JWKSet;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/trusted_registries")
public class TrustedRegistriesEndpoint {

	@Autowired
	private TrustedRegistryRepository repository;

	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String registries(Model model) {
		Collection<TrustedRegistry> regs = repository.getAll();
		Collection<TrustedRegistryDetails> deets = new ArrayList<TrustedRegistryDetails>();
		for (TrustedRegistry reg : regs) {
			deets.add(new TrustedRegistryDetails(reg.getValue()));
		}
		model.addAttribute("entity", deets);
		
		Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation()
						.setPrettyPrinting()
						.create();
		
		return gson.toJson(deets);
	}
	
	class TrustedRegistryDetails {

		private RestTemplate template = new RestTemplate();

		@Expose
		private String baseUri;
		
		@Expose
		private JsonElement deets;

		public TrustedRegistryDetails(String value) {
			baseUri = value;
			String uri = value + "/.well-known/bb/registry.json";
			String jsonString = template.getForObject(uri, String.class);
			JsonElement elt = new JsonParser().parse(jsonString);
			this.deets = elt;
		}

		public String getBaseUri() {
			return baseUri;
		}

		public void setBaseUri(String baseUri) {
			this.baseUri = baseUri;
		}
		
	}

}
