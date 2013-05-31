package org.bbplus;

import java.util.Map;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BBTrustedRegistrationValidator implements TrustedRegistrationValidator {

	@Override
	public void validate(String postBody, Authentication auth) {
		
		JsonObject regRequest = (JsonObject) new JsonParser().parse(postBody);
		
		// BlueButton permits open registration, so no auth = A-OK.
		if (!auth.isAuthenticated())
			return;
		
		JsonObject bbClient = ((PreregistrationToken) auth).getClientDefinitionFromTrustedRegistry();
		JsonObject fixedParams = (JsonObject) bbClient.get("fixed_registration_parameters");

		for (Map.Entry<String,JsonElement> entry  : fixedParams.entrySet()){
			String claimed = entry.getValue().toString();
			String requested = regRequest.get(entry.getKey()).toString();
			if (!(claimed.equals(requested)))
				throw new AuthenticationServiceException("App preregistered a claim for " +
						entry.getKey() +"="+claimed + 
						" but os asking for " +
						entry.getKey() +"="+requested);
						
			}
		
		return;
	}

}
