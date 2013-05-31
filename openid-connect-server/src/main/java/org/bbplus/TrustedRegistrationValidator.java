package org.bbplus;

import org.springframework.security.core.Authentication;

public interface TrustedRegistrationValidator {
	void validate(String postBody, Authentication auth);
}
