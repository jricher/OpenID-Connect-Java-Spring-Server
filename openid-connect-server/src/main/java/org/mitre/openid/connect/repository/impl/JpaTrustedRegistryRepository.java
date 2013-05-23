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
package org.mitre.openid.connect.repository.impl;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.mitre.openid.connect.model.TrustedRegistry;
import org.mitre.openid.connect.repository.TrustedRegistryRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Trusted Registry repository implementation
 * 
 * @author Josh Mandel
 * 
 */
@Repository
public class JpaTrustedRegistryRepository implements TrustedRegistryRepository {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Collection<TrustedRegistry> getAll() {
		TypedQuery<TrustedRegistry> query = manager.createNamedQuery("TrustedRegistry.getAll", TrustedRegistry.class);
		return query.getResultList();
	}
}
