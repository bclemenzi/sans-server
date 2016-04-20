package com.nfbsoftware.sansserver.user.lambda;

import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambdaWithGateway;
import com.nfbsoftware.sansserverplugin.sdk.aws.AmazonSESManager;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.Entity;
import com.nfbsoftware.sansserverplugin.sdk.util.SecureUUID;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;

/**
 * The UserRegistration function is used to create a new user account in the system.  The account is created 
 * with a salted password hash to ensure passord security.
 * 
 * @author Brendan Clemenzi
 */
@AwsLambdaWithGateway(name="UserRegistration", desc="SansServer Self Registration", handlerMethod="handleRequest", memorySize="512", timeout="60", resourceName="userRegistration", method=AwsLambdaWithGateway.MethodTypes.POST, authorization=AwsLambdaWithGateway.AuthorizationTypes.NONE, keyRequired=false, enableCORS=true)
public class UserRegistration extends BaseLambdaHandler
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
            
            // Make sure no script tags have been sent in
            // TODO In production we'd want to put some type of OWASP/AntiSamy system in place to protect our data
            username = StringUtil.stripHTML(username);
            clearPassword = StringUtil.stripHTML(clearPassword);
            fullName = StringUtil.stripHTML(fullName);
            
            // Initialize our user datasoure
            UserDao userDao = new UserDao(this.m_properties);
            
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
                
                // Create our user record.
                userDao.createUser(userModel);
                
                // Email our user to say thank you
                sendEmailConfirmations(username, fullName);
                
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
            m_logger.log("Error processing creating user: " + e.getMessage());
            
            handlerResponse.setStatus(HandlerResponse.StatusKeys.FAILURE);
            handlerResponse.setStatusMessage(e.getMessage());
        }
        
        return handlerResponse;
    }
    
    private void sendEmailConfirmations(String email, String fullName)
    {
        // Send user confirmation
        try
        {
            AmazonSESManager awsSESManager = new AmazonSESManager(this.m_properties);
            
            String subject = "Welcome to SansServer";
            StringBuffer messageBody = new StringBuffer();
            
            String siteUrl = m_properties.getProperty(Entity.FrameworkProperties.ENVIRONEMNT_SITE_URL);
            
            messageBody.append("<div>");
            messageBody.append("<div>Hi " + fullName + ",</div>");
            messageBody.append("<br/>");
            messageBody.append("<div>We just wanted to send you a quick note to say thank you for registering for a demo account with SansServer.</div>");
            messageBody.append("<br/>");
            messageBody.append("<div>SansServer is a sample application.  We will not communicate with or share this email address with anyone.  This email has been sent as a confirmation to the creation of a demo account on the SansServer application.</div>");
            messageBody.append("<br/>");
            messageBody.append("<div>To access your new demo account, please visit <a href='" + siteUrl + "'>" + siteUrl + "</a> and login with the email address and password you created during registration.</div>");
            messageBody.append("<br/>");
            messageBody.append("<div>Thank you!<br/>The SansServer Team</div>");
            messageBody.append("</div>");
            
            // Send email message
            awsSESManager.buildEmailMessage(email, subject, messageBody.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            m_logger.log("Error sending email confirmations: " + e.getMessage());
        }
        
        // Send registration notice
        try
        {
            AmazonSESManager awsSESManager = new AmazonSESManager(this.m_properties);
            
            String subject = "SansServer - New User";
            StringBuffer messageBody = new StringBuffer();
            
            messageBody.append("<div>Please welcome user " + fullName + " (" + email + ") to the SansServer application</div>");
            
            // Send email message
            awsSESManager.buildEmailMessage("brendan@nfbsoftware.com", subject, messageBody.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
            m_logger.log("Error sending registration notice: " + e.getMessage());
        }
    }
}
