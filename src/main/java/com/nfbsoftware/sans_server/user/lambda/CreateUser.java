package com.nfbsoftware.sans_server.user.lambda;

import com.nfbsoftware.sans_server.user.dao.UserDaoImpl;
import com.nfbsoftware.sans_server.user.model.User;
import com.nfbsoftware.sansserver.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserver.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserver.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserver.sdk.util.Entity;
import com.nfbsoftware.sansserver.sdk.util.SecureUUID;
import com.nfbsoftware.sansserver.sdk.util.StringUtil;

/**
 * The CreateUser function is used to create a new user account in the system.  The account is created 
 * with a salted password hash to ensure passord security.
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(name="Create User", desc="User self registration function", handlerMethod="handleRequest")
public class CreateUser extends BaseLambdaHandler
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
            String username = StringUtil.emptyIfNull(this.getParameter("username"));
            String clearPassword = StringUtil.emptyIfNull(this.getParameter("password"));
            String fullName = StringUtil.emptyIfNull(this.getParameter("fullName"));
            
            // Get our property values from our base handler.
            String region = this.getProperty(Entity.FrameworkProperties.AWS_REGION);
            String accessKey = this.getProperty(Entity.FrameworkProperties.AWS_ACCESS_KEY);
            String secretKey = this.getProperty(Entity.FrameworkProperties.AWS_SECRET_KEY);
            String dynamoDbTableNamePrefix = this.getProperty(Entity.FrameworkProperties.AWS_DYNAMODB_TABLE_NAME_PREFIX);
            
            // Initialize our user datasoure
            UserDaoImpl userDao = new UserDaoImpl(accessKey, secretKey, region, dynamoDbTableNamePrefix);
            
            // Create our User model
            User userModel = new User();
            userModel.setUserId(SecureUUID.generateGUID());
            userModel.setUsername(username);
            userModel.setFullName(fullName);
            
            // Create our salt string
            String passwordSalt = SecureUUID.generateSalt();
            userModel.setSalt(passwordSalt);
            
            // Create our salted password hash
            String passwordHash = SecureUUID.generateSaltedMD5(clearPassword, passwordSalt);
            userModel.setPassword(passwordHash);
            
            // Create our user record.
            userDao.createUser(userModel);
            
            // Add the user id to the response map
            handlerResponse.getData().put("userId", userModel.getUserId());
            
            // Set our process status
            handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
            handlerResponse.setStatusMessage("");
        }
        catch (Exception e)
        {
            m_logger.log("Error processing creating user record: " + e.getMessage());
            
            handlerResponse.setStatus(HandlerResponse.StatusKeys.FAILURE);
            handlerResponse.setStatusMessage(e.getMessage());
        }
        
        return handlerResponse;
    }
}
