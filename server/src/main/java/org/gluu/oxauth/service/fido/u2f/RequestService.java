/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package org.gluu.oxauth.service.fido.u2f;

import io.jans.as.model.config.StaticConfiguration;
import io.jans.orm.PersistenceEntryManager;
import io.jans.orm.model.BatchOperation;
import io.jans.orm.model.SearchScope;
import io.jans.search.filter.Filter;
import org.gluu.oxauth.model.fido.u2f.RequestMessageLdap;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

/**
 * Provides generic operations with U2F requests
 *
 * @author Yuriy Movchan Date: 05/19/2015
 */
@Stateless
@Named("u2fRequestService")
public class RequestService {

	@Inject
	private Logger log;

	@Inject
	private PersistenceEntryManager ldapEntryManager;

	@Inject
	private StaticConfiguration staticConfiguration;

	public List<RequestMessageLdap> getExpiredRequestMessages(BatchOperation<RequestMessageLdap> batchOperation, Date expirationDate, String[] returnAttributes, int sizeLimit, int chunkSize) {
		final String u2fBaseDn = staticConfiguration.getBaseDn().getU2fBase(); // ou=u2f,o=gluu
		Filter expirationFilter = Filter.createLessOrEqualFilter("creationDate", ldapEntryManager.encodeTime(u2fBaseDn, expirationDate));

		List<RequestMessageLdap> requestMessageLdap = ldapEntryManager.findEntries(u2fBaseDn, RequestMessageLdap.class, expirationFilter, SearchScope.SUB, returnAttributes, batchOperation, 0, sizeLimit, chunkSize);

		return requestMessageLdap;
	}

	public void removeRequestMessage(RequestMessageLdap requestMessageLdap) {
		ldapEntryManager.remove(requestMessageLdap);
	}

}
