package com.nfbsoftware.sansserver.user.lambda;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.apigateway.model.CreateApiKeyRequest;
import com.amazonaws.services.apigateway.model.CreateApiKeyResult;
import com.amazonaws.services.apigateway.model.GetRestApiResult;
import com.amazonaws.services.apigateway.model.StageKey;
import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserverplugin.sdk.aws.AmazonGatewayManager;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.Entity;
import com.nfbsoftware.sansserverplugin.sdk.util.SecureUUID;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The CreateUser function is used to add a new user to the system
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(
        name="CreateUser", 
        desc="Function to create a new user record", 
        handlerMethod="handleRequest", 
        memorySize="512", 
        timeout="60")
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
            String username = StringUtil.emptyIfNull((String)this.getInputObject("username"));
            String clearPassword = StringUtil.emptyIfNull((String)this.getInputObject("password"));
            String fullName = StringUtil.emptyIfNull((String)this.getInputObject("fullName"));
            
            // Initialize our user database
            UserDao userDao = new UserDao(this.m_properties);
            AmazonGatewayManager amazonGatewayManager = new AmazonGatewayManager(this.m_properties);
            
            User userModel = userDao.getUser(username);
            
            if(userModel == null)
            {
                // Create our User model
                userModel = new User();
                userModel.setUserId(SecureUUID.generateGUID());
                userModel.setActive(true);
                userModel.setUsername(username);
                userModel.setFullName(fullName);
                
                // Create our salt string
                String passwordSalt = SecureUUID.generateSalt();
                userModel.setSalt(passwordSalt);
                
                // Create our salted password hash
                String passwordHash = SecureUUID.generateSaltedMD5(clearPassword, passwordSalt);
                userModel.setPassword(passwordHash);
                
                // Generate an API key
                String environmentPrefix = StringUtil.emptyIfNull(this.m_properties.getProperty(Entity.FrameworkProperties.ENVIRONEMNT_PREFIX));
                String environmentStage = StringUtil.emptyIfNull(this.m_properties.getProperty(Entity.FrameworkProperties.ENVIRONEMNT_STAGE));
                
                // Look up our environment gateway
                GetRestApiResult restApiResult = amazonGatewayManager.getRestApiByName(environmentPrefix + "_GATEWAY");
                
                if(restApiResult != null)
                {
                    List<StageKey> stageKeys = new ArrayList<StageKey>();
                    
                    StageKey stageKey = new StageKey();
                    stageKey.setRestApiId(restApiResult.getId());
                    stageKey.setStageName(environmentStage);
                    
                    stageKeys.add(stageKey);
                    
                    // Create a new API Key
                    CreateApiKeyRequest createApiKeyRequest = new CreateApiKeyRequest();
                    createApiKeyRequest.setEnabled(true);
                    createApiKeyRequest.setName(userModel.getUserId());
                    createApiKeyRequest.setDescription("API Key for " + StringUtil.emptyIfNull(userModel.getFullName()));
                    createApiKeyRequest.setStageKeys(stageKeys);
                    
                    CreateApiKeyResult createApiKeyResult = amazonGatewayManager.createApiKey(createApiKeyRequest);
                    
                    userModel.setApiKey(createApiKeyResult.getId());
                }
                else
                {
                    userModel.setApiKey(StringUtil.EMPTY_STRING);
                }
                
                // Create our user record.
                userDao.createUser(userModel);
                
                // Add the user id to the response map
                handlerResponse.getData().put("userId", userModel.getUserId());
                
                // Set our process status
                handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
                handlerResponse.setStatusMessage("");
            }
            else
            {
                // username is already in use
                handlerResponse.setStatus(HandlerResponse.StatusKeys.FAILURE);
                handlerResponse.setStatusMessage("Sorry, your email has already been associated with an account.");
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
