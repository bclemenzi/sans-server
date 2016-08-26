package com.nfbsoftware.sansserver.user.model;

/**
 * The AuthenticateUserResponse object is returned to the end user for use on the client.  Items like password are not included for security.
 * 
 * @author Brendan Clemenzi
 */
public class AuthenticatedUser
{
    private String userId;
    private String fullName;
    private String region;
    private String identityId;
    private String apiKey;
    private String identityPoolId;
    private String openIdToken;
    
    public String getUserId()
    {
        return userId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getFullName()
    {
        return fullName;
    }
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }
    
    public String getIdentityId()
    {
        return identityId;
    }
    public void setIdentityId(String identityId)
    {
        this.identityId = identityId;
    }
    
    public String getRegion()
    {
        return region;
    }
    public void setRegion(String region)
    {
        this.region = region;
    }
    
    public String getIdentityPoolId()
    {
        return identityPoolId;
    }
    public void setIdentityPoolId(String identityPoolId)
    {
        this.identityPoolId = identityPoolId;
    }
    
    public String getApiKey()
    {
        return apiKey;
    }
    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }
    
    public String getOpenIdToken()
    {
        return openIdToken;
    }
    public void setOpenIdToken(String openIdToken)
    {
        this.openIdToken = openIdToken;
    }
}
