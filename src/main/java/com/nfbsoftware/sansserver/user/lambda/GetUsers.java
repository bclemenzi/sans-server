package com.nfbsoftware.sansserver.user.lambda;

import java.util.List;

import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambdaWithGateway;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The ViewUsers function simply returns all the user records in the[ system
 * 
 * @author Brendan Clemenzi
 */
@AwsLambdaWithGateway(
        name="GetUsers", 
        desc="Function to view all the users in the system", 
        handlerMethod="handleRequest", 
        memorySize="512", 
        timeout="60", 
        resourceName="getUsers", 
        method=AwsLambdaWithGateway.MethodTypes.POST, 
        authorization=AwsLambdaWithGateway.AuthorizationTypes.NONE, 
        keyRequired=true, 
        enableCORS=true)
public class GetUsers extends BaseLambdaHandler
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
            String sortField = StringUtil.replaceIfNull(this.getBody("sort[0][field]"), "creationDate");
            String sortDirection = StringUtil.replaceIfNull(this.getBody("sort[0][dir]"), "desc");

            int skip = new Integer(StringUtil.replaceIfNull(this.getBody("skip"), "0"));
            int pageSize = new Integer(StringUtil.replaceIfNull(this.getBody("pageSize"), "12"));
            
            UserDao userDao = new UserDao(this.m_properties);
            
            Long userTotal = userDao.getUserCount();
            List<User> users = userDao.getUsers(sortField, sortDirection, skip, pageSize);
            
            // Add the model to the response map
            handlerResponse.getData().put("pageTotal", userTotal);
            handlerResponse.getData().put("users", users);
                
            // Set our process status
            handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
            handlerResponse.setStatusMessage("");
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
