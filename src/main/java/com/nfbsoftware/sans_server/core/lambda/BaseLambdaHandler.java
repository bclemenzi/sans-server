package com.nfbsoftware.sans_server.core.lambda;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.nfbsoftware.sans_server.core.model.HandlerResponse;

/**
 * The BaseHandler should be used as a base function for Lambda.  It will create a baseline set of objects needed by all our functions.
 * When configuring a Lambda function, do so with the function name "handlerRequest".  When implementing your own extension to this
 * class, simply override the "processRequest" method.
 * 
 * @author Brendan Clemenzi
 */
public class BaseLambdaHandler implements ILambdaFunction
{
    protected LambdaLogger m_logger;
    protected Object m_input;
    protected Context m_context;
    
    protected LinkedHashMap<String, String> m_inputHashMap;
    
    protected Properties m_properties = new Properties();
    
    /**
     * Basic constructor
     */
    public BaseLambdaHandler()
    {
        try
        {
            // Load our properties file
            initialize();
            
            // TODO Load other default resources
        }
        catch(Exception e)
        {
            // Since we are out of options in Lambda, just print the stacktrace.
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize our handler with whatever it needs upfront
     * 
     * @throws Exception
     */
    protected void initialize() throws Exception
    {
        // Load our properties file.  This is hard coded into our deployment
        InputStream is = BaseLambdaHandler.class.getResourceAsStream("/lambda.properties");
        
        // Load our properties file into memory
        m_properties.load(is);
    }
    
    /**
     * Returns the raw context object passed to the lambda function
     * 
     * @return
     */
    protected Context getFunctionContext()
    {
        return m_context;
    }
    
    /**
     * Returns the raw input object passed to the lambda function
     * 
     * @return
     */
    protected Object getFunctionInput()
    {
        return m_input;
    }
    
    /**
     * The getProperty method can be used to get a property value found within the default lambda.properties file created during deployment 
     * 
     * @param propertyKey
     * @return
     */
    protected String getProperty(String propertyKey)
    {
        String tmpPropertyValue = m_properties.getProperty(propertyKey);
        
        return tmpPropertyValue;
    }
    
    /**
     * The getParameter method can be used as a simple way to get our function's input parameters.
     * 
     * @param propertyKey
     * @return
     */
    protected String getParameter(String parameterKey)
    {
        String tmpParameterValue = m_inputHashMap.get(parameterKey);
        
        return tmpParameterValue;
    }
    
    /**
     * This is the primary method used/configured for Lambda.  Be default, we do not want to override this method.
     * 
     * @param input
     * @param context
     * @return
     */
    public HandlerResponse handleRequest(Object input, Context context)
    {
        // Set out imputs:
        m_input = input;
        m_context = context;
        
        // Set our logger
        m_logger = m_context.getLogger();
        
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, String> inputHashMap = (LinkedHashMap<String, String>)input;
        m_inputHashMap = inputHashMap;

        try
        {
            // Process our request
            HandlerResponse handlerResponse =  processRequest();
            
            // Return our response object
            return handlerResponse;
        }
        catch (Exception e)
        {
            // Log our exception
            m_logger.log("ERROR: " + e.getMessage());
            
            // Always return at least a failure response
            HandlerResponse handlerResponse = new HandlerResponse();
            
            handlerResponse.setStatus(HandlerResponse.StatusKeys.FAILURE);
            handlerResponse.setStatusMessage(e.getMessage());
            
            // Return our response object
            return handlerResponse;
        }
    }
    
    /**
     * The processRequest method should be overridden in your implementations of this base class
     * 
     * @return
     * @throws Exception
     */
    public HandlerResponse processRequest() throws Exception
    {
        HandlerResponse handlerResponse = new HandlerResponse();
        
        handlerResponse.setStatus(HandlerResponse.StatusKeys.SUCCESS);
        handlerResponse.setStatusMessage("");
        
        return handlerResponse;
    }
}
