package com.nfbsoftware.sans_server.core.lambda;

import com.nfbsoftware.sans_server.core.model.HandlerResponse;

/**
 * The ILambdaFunction interface...
 * 
 * @author Brendan Clemenzi
 */
public interface ILambdaFunction
{
    public HandlerResponse processRequest() throws Exception;
}
