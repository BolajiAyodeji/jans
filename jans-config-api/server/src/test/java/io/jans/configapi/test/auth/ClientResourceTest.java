/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.configapi.test.auth;

import static io.restassured.RestAssured.given;
import io.jans.configapi.BaseTest;
import io.jans.model.net.HttpServiceResponse;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.MediaType;

import static org.testng.Assert.*;

import java.util.Map;

import org.apache.http.entity.ContentType;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;


public class ClientResourceTest extends BaseTest{

    private String clientId;
    
    @Parameters({"issuer", "openidClientsUrl"})
    @Test
    public void getClients(final String issuer, final String openidClientsUrl) {
        log.error("accessToken:{}, issuer:{}, openidClientsUrl:{}", accessToken, issuer, openidClientsUrl);
            given().when().contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", AUTHORIZATION_TYPE + " " + accessToken, null)
                .get(issuer+openidClientsUrl).then().statusCode(200);
    }
    
    @Parameters({"issuer", "openidClientsUrl"})
    @Test
    public void getAllClient(final String issuer, final String openidClientsUrl) {
        log.error("getAllClient() - accessToken:{}, issuer:{}, openidClientsUrl:{}", accessToken, issuer, openidClientsUrl);
        Builder request = getResteasyService().getClientBuilder(issuer+openidClientsUrl);
        request.header(AUTHORIZATION, AUTHORIZATION_TYPE + " " + accessToken);
        request.header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        
        Response response = request.get();
        log.error("Response for Access Token -  response:{}", response);

    }

    @Parameters({"issuer", "openidClientsUrl", "openid_client2"})
    @Test
    public void postClient2(final String issuer, final String openidClientsUrl, final String json) {
        log.error("postClient2 - accessToken:{}, issuer:{}, openidClientsUrl:{}, json:{}", accessToken, issuer, openidClientsUrl, json);
        log.error("postClient2 client using json string - getHttpService():{}, this.accessToken:{}", getHttpService(), this.accessToken);

        Builder request = getResteasyService().getClientBuilder(issuer+openidClientsUrl);
        request.header(AUTHORIZATION, AUTHORIZATION_TYPE + " " + accessToken);
        request.header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
        
        //Response response = request.post(Entity.json(json));
        Response response = request.post(Entity.entity(json, MediaType.APPLICATION_JSON));
        log.trace("Response for Access Token -  response:{}", response);

        if (response.getStatus() == 201) {
            log.trace("Response for Access Token -  response.getEntity():{}, response.getClass():{}", response.getEntity(), response.getClass());
        }
      

        
        assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
    }
}
