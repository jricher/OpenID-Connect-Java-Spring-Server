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
package org.mitre.oauth2.web;

import java.security.Principal;
import java.util.Map;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.model.SystemScope;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.oauth2.service.OAuth2TokenEntityService;
import org.mitre.oauth2.service.SystemScopeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

@Controller
public class IntrospectionEndpoint {

	@Autowired
	private OAuth2TokenEntityService tokenServices;

	@Autowired
	private ClientDetailsEntityService clientService;

	@Autowired
	private SystemScopeService scopeService;

	private static Logger logger = LoggerFactory.getLogger(IntrospectionEndpoint.class);

	public IntrospectionEndpoint() {

	}

	public IntrospectionEndpoint(OAuth2TokenEntityService tokenServices) {
		this.tokenServices = tokenServices;
	}

	@PreAuthorize("hasRole('ROLE_CLIENT')")
	@RequestMapping("/introspect")
	public String verify(@RequestParam("token") String tokenValue, Principal p, Model model) {

		if (Strings.isNullOrEmpty(tokenValue)) {
			logger.error("Verify failed; token value is null");
			Map<String,Boolean> entity = ImmutableMap.of("valid", Boolean.FALSE);
			model.addAttribute("entity", entity);
			return "jsonEntityView";
		}

		OAuth2AccessTokenEntity token = null;

		try {
			token = tokenServices.readAccessToken(tokenValue);
		} catch (InvalidTokenException e) {
			logger.error("Verify failed; AuthenticationException", e);
			Map<String,Boolean> entity = ImmutableMap.of("valid", Boolean.FALSE);
			model.addAttribute("entity", entity);
			return "jsonEntityView";
		}

		ClientDetailsEntity tokenClient = token.getClient();
		// clientID is the principal name in the authentication
		String clientId = p.getName();
		ClientDetailsEntity authClient = clientService.loadClientByClientId(clientId);

		if (tokenClient != null && authClient != null) {
			if (authClient.isAllowIntrospection()) {

				// if it's the same client that the token was issued to, or it at least has all the scopes the token was issued with
				if (authClient.equals(tokenClient)) {
					// if it's a valid token, we'll print out information on it
					model.addAttribute("entity", token);
					return "tokenIntrospection";
				} else {
					
					boolean scopesConsistent = true;
					for (String ts : token.getScope()){
						if (!authClient.getScope().contains(scopeService.baseScope(ts))){
							scopesConsistent = false;
							break;
						}						
					}
					
					if (scopesConsistent) {
						model.addAttribute("entity", token);
						return "tokenIntrospection";
					}
					
					logger.error("Verify failed; client tried to introspect a token of an incorrect scope");
					model.addAttribute("code", HttpStatus.FORBIDDEN);
					return "httpCodeView";
				}
			} else {
				logger.error("Verify failed; client " + clientId + " is not allowed to call introspection endpoint");
				model.addAttribute("code", HttpStatus.FORBIDDEN);
				return "httpCodeView";
			}
		} else {
			// This is a bad error -- I think it means we have a token outstanding that doesn't map to a client?
			logger.error("Verify failed; client " + clientId + " not found.");
			model.addAttribute("code", HttpStatus.NOT_FOUND);
			return "httpCodeView";
		}

	}

}
