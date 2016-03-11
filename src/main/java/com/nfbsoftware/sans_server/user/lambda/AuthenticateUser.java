package com.nfbsoftware.sans_server.user.lambda;

import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenForDeveloperIdentityResult;
import com.nfbsoftware.sans_server.user.dao.UserDaoImpl;
import com.nfbsoftware.sans_server.user.model.AuthenticatedUser;
import com.nfbsoftware.sans_server.user.model.User;
import com.nfbsoftware.sansserver.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserver.sdk.aws.AmazonCognitoImpl;
import com.nfbsoftware.sansserver.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserver.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserver.sdk.util.Entity;
import com.nfbsoftware.sansserver.sdk.util.SecureUUID;
import com.nfbsoftware.sansserver.sdk.util.StringUtil;

/**
 * The AuthenticateUser function is used to verify a user's username and password with that stored in the database.  If verified,
 * the function will return an AuthenticatedUser record to the client that contains identity and openID tokens for client side
 * execution of lambda functions.
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(name="Authenticate User", desc="Custom authentication service", handlerMethod="handleRequest")
public class AuthenticateUser extends BaseLambdaHandler
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
            
            // Get our property values from our base handler.
            String region = this.getProperty(Entity.FrameworkProperties.AWS_REGION);
            String accessKey = this.getProperty(Entity.FrameworkProperties.AWS_ACCESS_KEY);
            String secretKey = this.getProperty(Entity.FrameworkProperties.AWS_SECRET_KEY);
            String cognitoIdentityPoolId = this.getProperty(Entity.FrameworkProperties.AWS_COGNITO_IDENTITY_POOL_ID);
            String cognitoProviderName = this.getProperty(Entity.FrameworkProperties.AWS_CONGITO_PROVIDER_NAME);
            String dynamoDbTableNamePrefix = this.getProperty(Entity.FrameworkProperties.AWS_DYNAMODB_TABLE_NAME_PREFIX);
            
            // Initialize our user datasoure
            UserDaoImpl userDao = new UserDaoImpl(accessKey, secretKey, region, dynamoDbTableNamePrefix);
            
            m_logger.log("Get user record: " + username);
            User user = userDao.getUser(username);
            
            if(user != null)
            {
                m_logger.log("Validate credentials for: " + username);
                
                // Salt and hash the password passed in to make sure it is the same as the one stored in our database
                String testHash = SecureUUID.generateSaltedMD5(clearPassword, user.getSalt());
                
                // Verify that the password hashes match
                if(user.getPassword().equalsIgnoreCase(testHash))
                {
                    m_logger.log("Credentials validated for: " + username);
                    
                    AmazonCognitoImpl amazonCognitoManager = new AmazonCognitoImpl(accessKey, secretKey, cognitoIdentityPoolId, cognitoProviderName);
                    
                    m_logger.log("Get OpenId Token for: " + username);
                    GetOpenIdTokenForDeveloperIdentityResult identityResult = amazonCognitoManager.getDeveloperIdentityResult(user.getUserId());
                    
                    // Update the token on the user record
                    user.setOpenIdToken(identityResult.getToken());
                    
                    // Save the new token to our user account.
                    userDao.updateUser(user);
                    
                    // Build the response object
                    AuthenticatedUser authenticatedUser = new AuthenticatedUser();
                    authenticatedUser.setUserId(user.getUserId());
                    authenticatedUser.setFullName(user.getFullName());
                    authenticatedUser.setIdentityId(identityResult.getIdentityId());
                    authenticatedUser.setOpenIdToken(user.getOpenIdToken());
                    
                    // Add the model to the response map
                    handlerResponse.getData().put("authenticatedUser", authenticatedUser);
                }
                else
                {
                    throw new Exception("Username or password is incorrect");
                }
            }
            else
            {
                throw new Exception("Username or password is incorrect");
            }
            
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
