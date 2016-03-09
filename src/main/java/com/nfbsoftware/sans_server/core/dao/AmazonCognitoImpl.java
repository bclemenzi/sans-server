package com.nfbsoftware.sans_server.core.dao;

import java.util.HashMap;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;

/**
 * The AmazonCognitoManager is used to manage the connection to Amazon's Cognito service.  There are a handful of methods to make using the service a little easier.
 * 
 * @author Brendan Clemenzi
 */
public class AmazonCognitoImpl
{
    protected AmazonCognitoIdentityClient m_amazonCognitoIdentityClient;
    protected String m_identityPoolId;
    protected String m_providerName;
    
    /**
     * 
     * @param accessKey
     * @param secretKey
     * @param IdentityPoolId
     */
    public AmazonCognitoImpl(String accessKey, String secretKey, String identityPoolId, String providerName)
    {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        
        m_amazonCognitoIdentityClient = new AmazonCognitoIdentityClient(credentials);
        
        m_identityPoolId = identityPoolId;
        m_providerName = providerName;
    }
    
    /**
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public GetOpenIdTokenForDeveloperIdentityResult getDeveloperIdentityResult(String userId)
    {
        GetOpenIdTokenForDeveloperIdentityRequest tokenRequest = new GetOpenIdTokenForDeveloperIdentityRequest();
        tokenRequest.setIdentityPoolId(m_identityPoolId);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(m_providerName, userId);

        tokenRequest.setLogins(map);
        tokenRequest.setTokenDuration(new Long(10001));
        
        return m_amazonCognitoIdentityClient.getOpenIdTokenForDeveloperIdentity(tokenRequest);
    }
}
