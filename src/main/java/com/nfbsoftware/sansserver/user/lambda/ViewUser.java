package com.nfbsoftware.sansserver.user.lambda;

import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambdaWithGateway;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The ViewUser function simply returns the user record requested
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(name="ViewUser", desc="Function to view a given user record", handlerMethod="handleRequest", memorySize="512", timeout="60")
public class ViewUser extends BaseLambdaHandler
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
            String userId = StringUtil.emptyIfNull(this.getParameter("userId"));
            
            UserDao userDao = new UserDao(this.m_properties);
            
            m_logger.log("Get user record by id: " + userId);
            User user = (User)userDao.scanUser(1, "USER_ID", userId).get(0);
            
            if(user != null)
            {
                // Add the model to the response map
                handlerResponse.getData().put("user", user);
                
                // Set our process status
                handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
                handlerResponse.setStatusMessage("");
            }
            else
            {
                throw new Exception("User record not found");
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
