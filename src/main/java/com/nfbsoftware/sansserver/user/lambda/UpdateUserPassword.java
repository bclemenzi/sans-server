package com.nfbsoftware.sansserver.user.lambda;

import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.SecureUUID;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The UpdateUserPassword function is used to update the settings of the authenticated user.
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(
        name="UpdateUserPassword", 
        desc="Function to update a given user password", 
        handlerMethod="handleRequest", 
        memorySize="512", 
        timeout="60")
public class UpdateUserPassword extends BaseLambdaHandler
{
    /**
     * 
     * @return
     * @throws Exception
     */
    @Override
    public HandlerResponse processRequest() throws Exception
    {
        HandlerResponse handlerResponse = new HandlerResponse();
        
        try
        {
            // Get the parameters for the request
            String oldPassword = StringUtil.emptyIfNull((String)this.getInputObject("oldPassword"));
            String newPassword = StringUtil.emptyIfNull((String)this.getInputObject("newPassword"));
            
            // Init our user DAO
            UserDao userDao = new UserDao(this.m_properties);
            
            // Get the authenticated id
            String authenticatedIdentityId = getFunctionContext().getIdentity().getIdentityId();
            
            if(!StringUtil.isNullOrEmpty(authenticatedIdentityId))
            {
                User userModel = (User)userDao.scanUser("IDENTITY_ID", authenticatedIdentityId).get(0);
                
                if(userModel != null)
                {
                    // Salt and hash the password passed in to make sure it is the same as the one stored in our database
                    String testHash = SecureUUID.generateSaltedMD5(oldPassword, userModel.getSalt());
                    
                    // Verify that the password hashes match
                    if(userModel.getPassword().equalsIgnoreCase(testHash))
                    {
                        // Create our salt string
                        String passwordSalt = SecureUUID.generateSalt();
                        userModel.setSalt(passwordSalt);
                        
                        // Create our salted password hash
                        String passwordHash = SecureUUID.generateSaltedMD5(newPassword, passwordSalt);
                        userModel.setPassword(passwordHash);
                        
                        // Update our user account with the new password
                        userDao.updateUser(userModel);
                    }
                    
                    // Set our process status
                    handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
                    handlerResponse.setStatusMessage("");
                }
                else
                {
                    throw new Exception("No authenticated user found");
                }
            }
            else
            {
                throw new Exception("No authenticated user found");
            }
        }
        catch (Exception e)
        {
            m_logger.log("Error processing request: " + e.getMessage());
            
            handlerResponse.setStatus(HandlerResponse.StatusKeys.FAILURE);
            handlerResponse.setStatusMessage(e.getMessage());
        }
        
        return handlerResponse;
    }
}
