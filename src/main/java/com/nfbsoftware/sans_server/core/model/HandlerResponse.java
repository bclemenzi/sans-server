package com.nfbsoftware.sans_server.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The HandlerResponse is a base class used to bring consistency to the responses of our Lambda handlers
 * 
 * @author Brendan Clemenzi
 */
public class HandlerResponse
{
    private String m_status;
    private String m_statusMessage;
    private Map<String, Object> m_data = new HashMap<String, Object>();
    
    public class StatusKeys
    {
        public static final String SUCCESS  = "SUCCESS";
        public static final String WARNING  = "WARNING";
        public static final String FAILURE  = "FAILURE";
    }
    
    public String getStatus()
    {
        return m_status;
    }
    public void setStatus(String status)
    {
        m_status = status;
    }
    
    public String getStatusMessage()
    {
        return m_statusMessage;
    }
    public void setStatusMessage(String statusMessage)
    {
        m_statusMessage = statusMessage;
    }
    
    public Map<String, Object> getData()
    {
        return m_data;
    }
    public void setData(Map<String, Object> data)
    {
        m_data = data;
    }
}
