package com.nfbsoftware.sans_server.user.lambda;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.nfbsoftware.sansserver.user.lambda.ViewUsers;
import com.nfbsoftware.sansserverplugin.sdk.junit.TestContext;
import com.nfbsoftware.sansserverplugin.sdk.lambda.model.HandlerResponse;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 * 
 * @author Brendan Clemenzi
 */
public class ViewUsersTest
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

        ctx.setFunctionName("View Users");

        return ctx;
    }

    @Test
    public void testViewUser()
    {
        ViewUsers handler = new ViewUsers();
        Context ctx = createContext();

        HandlerResponse output = (HandlerResponse)handler.handleRequest(input, ctx);

        if(output.getStatus().equalsIgnoreCase("SUCCESS")) 
        {
            Long pageTotal = (Long)output.getData().get("pageTotal");
            
            System.out.println("pageTotal: " + pageTotal);
            
            // @SuppressWarnings("unchecked")
            //List<User> users = (List<User>)output.getData().get("users");
            
            //for(User userValue : users)
            //{
            //    System.out.println("userValue full name: " + userValue.getFullName());
            //}
            
            System.out.println("ViewUsers JUnit Test Passed");
        }
        else
        {
            Assert.fail("ViewUsers JUnit Test Failed");
        }
    }
}