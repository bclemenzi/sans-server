package com.nfbsoftware.sansserver.user.model;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * 
 * @author Brendan Clemenzi
 */
@DynamoDBTable(tableName="USERS")
public class User implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @DynamoDBHashKey(attributeName="USERNAME")  
    private String username;
    
    @DynamoDBAttribute(attributeName="USER_ID") 
    private String userId;
    
    @DynamoDBAttribute(attributeName="PASSWORD_HASH") 
    private String password;
    
    @DynamoDBAttribute(attributeName="SALT") 
    private String salt;
    
    @DynamoDBAttribute(attributeName="FULL_NAME")
    private String fullName;
    
    @DynamoDBAttribute(attributeName="OPEN_ID_TOKEN")
    private String openIdToken;
    
    @DynamoDBAttribute(attributeName="IDENTITY_ID")
    private String identityId;
    
    @DynamoDBAttribute(attributeName="ACTIVE")
    private boolean active;
    
    public String getUserId()
    {
        return userId;
    }
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getUsername()
    {
        return username;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getSalt()
    {
        return salt;
    }
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
    
    public String getFullName()
    {
        return fullName;
    }
    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }
    
    public String getOpenIdToken() 
    {
        return openIdToken;
    }
    public void setOpenIdToken(String openIdToken) 
    {
        this.openIdToken = openIdToken;
    }
    
    public String getIdentityId()
    {
        return identityId;
    }
    public void setIdentityId(String identityId)
    {
        this.identityId = identityId;
    }
    
    public boolean isActive()
    {
        return active;
    }
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
