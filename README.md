SansServer
==============

SansServer is a small web application implemented with a serverless architecture.  By “serverless”, we mean no explicit infrastructure required, as in: no servers, no deployments onto servers, no installed software of any kind. We’ll use only managed cloud services and a laptop.  This design allows for rapid development of scalable and highly available applications without the hassle of maintaining, or paying for, servers.  

For the SansServer demo application, we'll be using the Amazon API Gateway to expose the Lambda functions as HTTP endpoints.  We'll also use Identity and Access Management (IAM) with Amazon Cognito to retrieve temporary credentials for a user to grant access to these APIs and functions.  Amazon DynamoDB will be used for the application's persistent storage system.

Please keep in mind, this project has been written as an example and therefore should not be considered production ready.  It has; however, tried to follow best practices to give you a clean development framework to build on top of; which is made possible by the SansServer Plugin/SDK (http://github.com/bclemenzi/sans-server-plugin).  I recommended visiting the sans-server-plugin project page, but the high-level feature gain is the ability to use Java annotations to fully build, configure, and deploy our application under Amazon Web Services.

Features
--------

  * Java-based Lambda Functions identified by sans-server-plugin annotations
  * SPA (single-page-application) Web application front-end with Unauthenticated and Authenticated pages
  * Web application uses KendoUI developed by great people over at Telerik (http://www.telerik.com/kendo-ui)
  * Uses SansServer Plugin/SDK (https://github.com/bclemenzi/sans-server-plugin) to build and deploy application to Amazon Web Services
   * Project properties file used at runtime
   * JUnit test harness for locally testing your Lambda functions before deployment
   * Configuration schema to allow for the isolation of multiple deployments:  Multi-Engineer Development, QA, Production
  
Demo Site
---------------
http://sansserver.nfbsoftware.com

Questions or Comments
--------
Join the conversation on Gitter by clicking on the badge below:

[![Join the chat at https://gitter.im/bclemenzi/sans-server](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/bclemenzi/sans-server?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Getting started
---------------
The SansServer application bases itself off the source code layout defined by the sans-server-plugin (https://github.com/bclemenzi/sans-server-plugin).  The plugin provides a number of wrapper classes and utilities that were developed to make working with Amazon Web Services easier.

The SansServer application uses the sans-server-plugin to build, test, provision, and deploy the webapp to AWS.  The following are recommended Maven goals to use during your development:

 * mvn clean package
  * Clean and package should be used to build our source code and execute our JUnit test cases.
 * mvn clean package shade:shade com.nfbsoftware:sans-server-plugin:build-properties com.nfbsoftware:sans-server-plugin:deploy-lambda
  * Build, test, and Deploy just our Lambda functions - This is ideal when working on the back-end of your application
 * mvn clean package com.nfbsoftware:sans-server-plugin:deploy-webapp
  * Deploy just our web application - This is ideal when working on the UI of your application
 * mvn clean package shade:shade install
  * Adding shade:shade will package (JAR) our Java class files for Lambda deployment within Amazon Web Services.  The install goal performs the AWS configurations and deploys our code for use in the cloud.  Please see the sans-server-plugin project (https://github.com/bclemenzi/sans-server-plugin) to learn more about the available features found inside the plugin.

SansServer Framework
---------------
This project contains a number of java classes that try to help simplify the authoring of lambda functions, including a base handler (BaseLambdaHandler) to configure your system and collect your request parameters.  The BaseLambdaHandler also defines a simple response format to allow the client-side webapp to determine the status of any given transaction.  Below is an example of a basic Lambda function that utilizes this framework to view a user record within the system:

```java
package com.nfbsoftware.sansserver.user.lambda;

import com.nfbsoftware.sansserverplugin.sdk.annotation.AwsLambda;
import com.nfbsoftware.sansserverplugin.sdk.lambda.BaseLambdaHandler;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserverplugin.sdk.util.StringUtil;
import com.nfbsoftware.sansserver.user.dao.UserDao;
import com.nfbsoftware.sansserver.user.model.User;

/**
 * The ViewUser function simply returns the user record requested.  The defined "handlerMethod" within the 
 * class annotation is found in the BaseLambdaHandler that this class extends.  The BaseLambdaHandler class 
 * provides a number of convenience systems to make authoring Java-base Lambda functions easier.
 * 
 * @author Brendan Clemenzi
 */
@AwsLambda(
        name="ViewUser", 
        desc="Function to view a given user record", 
        handlerMethod="handleRequest", 
        memorySize="512", 
        timeout="60")
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
            String userId = StringUtil.emptyIfNull((String)this.getInputObject("userId"));
            
            UserDao userDao = new UserDao(this.m_properties);
            
            m_logger.log("Get user record by id: " + userId);
            User user = (User)userDao.scanUser("USER_ID", userId).get(0);
            
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
```

The JUnit class for testing the ViewUser Lambda function looks as follows:

```java
package com.nfbsoftware.sansserver.user.lambda;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.nfbsoftware.sansserverplugin.sdk.junit.TestContext;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;
import com.nfbsoftware.sansserver.user.lambda.ViewUser;
import com.nfbsoftware.sansserver.user.model.User;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 * 
 * @author Brendan Clemenzi
 */
public class ViewUserTest
{
    private static LinkedHashMap<String, String> input;

    @BeforeClass
    public static void createInput() throws IOException
    {
        input = new HashMap<String, Object>();
        
        // Declair our hash maps to mimic a client request through the gateway api
        HashMap<String, Object> headerHash = new HashMap<String, Object>();
        HashMap<String, Object> paramsHash = new HashMap<String, Object>();
        HashMap<String, Object> queryHash = new HashMap<String, Object>();
        HashMap<String, Object> bodyHash = new HashMap<String, Object>();
        
        // Set body elements
        input.put("userId", "097f50a3-3481-4ab3-a7f2-b0ffcdece0e6");
        
        // Put the hash maps on the input
        input.put("headers", headerHash);
        input.put("params", paramsHash);
        input.put("query", queryHash);
        input.put("body", bodyHash);
    }

    private Context createContext()
    {
        TestContext ctx = new TestContext();

        ctx.setFunctionName("View User");

        return ctx;
    }

    @Test
    public void testViewUser()
    {
        ViewUser handler = new ViewUser();
        Context ctx = createContext();

        HandlerResponse output = (HandlerResponse)handler.handleRequest(input, ctx);

        if(output.getStatus().equalsIgnoreCase("SUCCESS")) 
        {
            User userValue = (User)output.getData().get("user");
            
            System.out.println("Full Name: " + userValue.getFullName());
            
            System.out.println("ViewUser JUnit Test Passed");
        }
        else
        {
            Assert.fail("ViewUser JUnit Test Failed");
        }
    }
}
```


