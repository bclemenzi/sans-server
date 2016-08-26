package com.nfbsoftware.sansserver.user.lambda;

import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The UpdateUser function is used to update the settings of a given user
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(
        name="UpdateUser", 
        desc="Function to update a given user record", 
        handlerMethod="handleRequest", 
        memorySize="512", 
        timeout="60")
public class UpdateUser extends BaseLambdaHandler
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
            String userId = StringUtil.emptyIfNull((String)this.getInputObject("userId"));
            String username = StringUtil.emptyIfNull((String)this.getInputObject("username"));
            String fullName = StringUtil.emptyIfNull((String)this.getInputObject("fullName"));
            String activeString = StringUtil.replaceIfNull((String)this.getInputObject("active"), "true");
            
            // Init our user DAO
            UserDao userDao = new UserDao(this.m_properties);
            
            m_logger.log("Get user record by id: " + userId);
            User user = (User)userDao.scanUser("USER_ID", userId).get(0);
            
            if(user != null)
            {
                user.setUsername(username);
                user.setFullName(fullName);
                user.setActive(Boolean.parseBoolean(activeString));
                
                userDao.updateUser(user);
                
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
