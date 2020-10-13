/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.model.passport.idpinitiated;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jgomer on 2019-02-21.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IIConfiguration {

    private OIDCDetails openidclient;
    private List<AuthzParams> authorizationParams=new ArrayList<>();

    public OIDCDetails getOpenidclient() {
        return openidclient;
    }

    public void setOpenidclient(OIDCDetails openidclient) {
        this.openidclient = openidclient;
    }

    public List<AuthzParams> getAuthorizationParams() {
        return authorizationParams;
    }

    public void setAuthorizationParams(List<AuthzParams> authorizationParams) {
        this.authorizationParams = authorizationParams;
    }

}
