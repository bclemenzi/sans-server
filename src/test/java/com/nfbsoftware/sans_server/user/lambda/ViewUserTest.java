package com.nfbsoftware.sans_server.user.lambda;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.nfbsoftware.sansserver.user.lambda.ViewUser;
import com.nfbsoftware.sansserver.user.model.User;
import com.nfbsoftware.sansserverplugin.sdk.junit.TestContext;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;

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
        input = new LinkedHashMap<String, String>();
        input.put("userId", "097f50a3-3481-4ab3-a7f2-b0ffcdece0e6");
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