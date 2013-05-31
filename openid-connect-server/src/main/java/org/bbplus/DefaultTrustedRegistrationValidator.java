package org.bbplus;

import java.util.Map;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DefaultTrustedRegistrationValidator implements TrustedRegistrationValidator {

	// Happy validator doesn't care about trusted registration and always approves.
	@Override
	public void validate(String postBody, Authentication auth) {
		return;
	}

}
