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

import java.util.Set;

import org.mitre.oauth2.service.SystemScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

@Component
public class BBPermissions {
	
	@Autowired
	private SystemScopeService scopeService;

	public boolean hasStructuredScope(Authentication auth, String scope){
		
    	if (!(auth instanceof OAuth2Authentication))
    		return false;
    	
    	OAuth2Authentication oa = (OAuth2Authentication) auth;
    	Set<String> scopes = oa.getAuthorizationRequest().getScope();
    	
    	for(String  s : scopes) {
    		if (scopeService.baseScopeString(s).equals(scope))
    			return true;    		
    	}
    	
    	return false;
	}
	
    public boolean search(Authentication auth) {
    	return hasStructuredScope(auth, "search");
    }

    public boolean summary(Authentication auth) {
    	return hasStructuredScope(auth, "summary");
    }

}
