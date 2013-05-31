package org.bbplus;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;

@Service
public class PreregistrationTokenService implements ResourceServerTokenServices {

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException {
		Map<String,String> authParams = new HashMap<String,String>();
		authParams.put("accessToken", accessToken);
		AuthorizationRequest r = new DefaultAuthorizationRequest(authParams);
		PreregistrationToken ret = new PreregistrationToken(r, null);
		return ret;
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		return null;
	}

}
