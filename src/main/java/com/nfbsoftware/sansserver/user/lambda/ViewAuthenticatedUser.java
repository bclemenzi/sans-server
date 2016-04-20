package com.nfbsoftware.sansserver.user.lambda;

import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The ViewAuthenticatedUser function returns the user record registered with the identityId in the context identity
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(name="ViewAuthenticatedUser", desc="Function to view a given user record", handlerMethod="handleRequest", memorySize="512", timeout="60")
public class ViewAuthenticatedUser extends BaseLambdaHandler
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
            UserDao userDao = new UserDao(this.m_properties);
            
            String authenticatedIdentityId = getFunctionContext().getIdentity().getIdentityId();
            m_logger.log("authenticatedIdentityId: " + authenticatedIdentityId);
            
            if(!StringUtil.isNullOrEmpty(authenticatedIdentityId))
            {
                User user = (User)userDao.scanUser("IDENTITY_ID", authenticatedIdentityId).get(0);
                
                // Add the model to the response map
                handlerResponse.getData().put("user", user);
                
                // Set our process status
                handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
                handlerResponse.setStatusMessage("");
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
