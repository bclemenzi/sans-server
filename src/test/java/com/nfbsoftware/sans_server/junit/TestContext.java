package com.nfbsoftware.sans_server.junit;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * A simple mock implementation of the {@code Context} interface. Default values
 * are stubbed out, and setters are provided so you can customize the context
 * before passing it to your function.
 * 
 * @author Brendan Clemenzi
 */
public class TestContext implements Context
{
    private String awsRequestId = "EXAMPLE";
    private ClientContext clientContext;
    private String functionName = "EXAMPLE";
    private String functionVersion = "1.0";
    private CognitoIdentity identity;
    private String invokedFunctionArn = "EXAMPLE";
    private String logGroupName = "EXAMPLE";
    private String logStreamName = "EXAMPLE";
    private LambdaLogger logger = new TestLogger();
    private int memoryLimitInMB = 128;
    private int remainingTimeInMillis = 15000;

    public String getAwsRequestId()
    {
        return awsRequestId;
    }
    public void setAwsRequestId(String awsRequestId)
    {
        this.awsRequestId = awsRequestId;
    }

    public ClientContext getClientContext()
    {
        return clientContext;
    }
    public void setClientContext(ClientContext clientContext)
    {
        this.clientContext = clientContext;
    }

    public String getFunctionName()
    {
        return functionName;
    }
    public void setFunctionName(String functionName)
    {
        this.functionName = functionName;
    }

    public String getFunctionVersion()
    {
        return functionVersion;
    }
    public void setFunctionVersion(String functionVersion)
    {
        this.functionVersion = functionVersion;
    }

    public CognitoIdentity getIdentity()
    {
        return identity;
    }
    public void setIdentity(CognitoIdentity identity)
    {
        this.identity = identity;
    }

    public String getInvokedFunctionArn()
    {
        return invokedFunctionArn;
    }
    public void setInvokedFunctionArn(String invokedFunctionArn)
    {
        this.invokedFunctionArn = invokedFunctionArn;
    }

    public String getLogGroupName()
    {
        return logGroupName;
    }
    public void setLogGroupName(String logGroupName)
    {
        this.logGroupName = logGroupName;
    }

    public String getLogStreamName()
    {
        return logStreamName;
    }
    public void setLogStreamName(String logStreamName)
    {
        this.logStreamName = logStreamName;
    }

    public LambdaLogger getLogger()
    {
        return logger;
    }
    public void setLogger(LambdaLogger logger)
    {
        this.logger = logger;
    }

    public int getMemoryLimitInMB()
    {
        return memoryLimitInMB;
    }
    public void setMemoryLimitInMB(int memoryLimitInMB)
    {
        this.memoryLimitInMB = memoryLimitInMB;
    }

    public int getRemainingTimeInMillis()
    {
        return remainingTimeInMillis;
    }
    public void setRemainingTimeInMillis(int remainingTimeInMillis)
    {
        this.remainingTimeInMillis = remainingTimeInMillis;
    }

    /**
     * A simple {@code LambdaLogger} that prints everything to stderr.
     */
    private static class TestLogger implements LambdaLogger 
    {
        public void log(String message) 
        {
            System.err.println(message);
        } 
    }
}
