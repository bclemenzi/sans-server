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
    private String identityId;
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
    
    public String getOpenIdToken()
    {
        return openIdToken;
    }
    public void setOpenIdToken(String openIdToken)
    {
        this.openIdToken = openIdToken;
    }
}
