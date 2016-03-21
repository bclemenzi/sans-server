package com.nfbsoftware.sans_server.user.lambda;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.nfbsoftware.sansserver.user.lambda.AuthenticateUser;
import com.nfbsoftware.sansserver.user.model.AuthenticatedUser;
import com.nfbsoftware.sansserverplugin.sdk.junit.TestContext;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 * 
 * @author Brendan Clemenzi
 */
public class AuthenticateUserTest
{
    private static LinkedHashMap<String, String> input;

    @BeforeClass
    public static void createInput() throws IOException
    {
        input = new LinkedHashMap<String, String>();
        input.put("username", "brendan@clemenzi.com");
        input.put("password","password");
    }

    private Context createContext()
    {
        TestContext ctx = new TestContext();

        // Set our function name
        ctx.setFunctionName("Authenticate User");

        return ctx;
    }

    @Test
    public void testAuthenticateUser()
    {
        AuthenticateUser handler = new AuthenticateUser();
        Context ctx = createContext();

        HandlerResponse output = (HandlerResponse)handler.handleRequest(input, ctx);

        if(output.getStatus().equalsIgnoreCase("SUCCESS")) 
        {
            AuthenticatedUser userValue = (AuthenticatedUser)output.getData().get("authenticatedUser");
            
            System.out.println("User ID: " + userValue.getUserId());
            
            System.out.println("AuthenticateUser JUnit Test Passed");
        }
        else
        {
            Assert.fail("AuthenticateUser JUnit Test Failed");
        }
    }
}