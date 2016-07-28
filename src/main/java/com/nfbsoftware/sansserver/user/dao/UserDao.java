package com.nfbsoftware.sansserver.user.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.nfbsoftware.sansserverplugin.sdk.dynamodb.AbstractDynamoDbDao;
import com.nfbsoftware.sansserver.user.model.User;

/**
 * The UserManager is an implementation class used to manage the CRUD operations for the User model objects in DynamoDB
 * 
 * @author Brendan Clemenzi
 */
public class UserDao extends AbstractDynamoDbDao
{
    public UserDao(Properties properties) throws Exception
    {
        // Init our DOA with our keys, region, table prefix, table name, and primary key column
        super(properties, "USERS", "USERNAME");
    }

    public User getUser(String username)
    {
        return (User)get(User.class, username);
    }

    public void createUser(User userModel) throws Exception
    {
        create(userModel);
    }

    public void updateUser(User userModel) throws Exception
    {
        update(userModel);
    }

    public void deleteUser(User userModel) throws Exception
    {
        delete(userModel);
    }
    
    public Long getUserCount() throws Exception
    {
        Long recordCount = new Long("0");
        
        try
        {
            ScanRequest scanRequest = new ScanRequest();
            scanRequest.withTableName(m_tableName);

            // Get our scan results
            ScanResult scanResult = m_amazonDynamoDBClient.scan(scanRequest);

            int scannedCount = scanResult.getScannedCount();
            recordCount = new Long(scannedCount);
        }
        catch (AmazonServiceException ase)
        {
            m_logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            m_logger.error("\nError Message:    " + ase.getMessage());
            m_logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            m_logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            m_logger.error("\nError Type:       " + ase.getErrorType());
            m_logger.error("\nRequest ID:       " + ase.getRequestId());
            
            throw new Exception(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            m_logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            m_logger.error("Error Message: " + ace.getMessage());
            
            throw new Exception(ace.getMessage());
        }
        
        return recordCount;
    }
    
    public List<User> getUsers(String sortField, String sortDirection, int skip, int pageSize) throws Exception
    {
        List<User> tempUsers = new ArrayList<User>();
        
        try
        {
            ScanRequest scanRequest = new ScanRequest();
            scanRequest.withTableName(m_tableName);

            // Get our scan results
            ScanResult scanResult = m_amazonDynamoDBClient.scan(scanRequest);

            for (int i = 0; i < scanResult.getCount(); i++) 
            {
                HashMap<String, AttributeValue> item = (HashMap<String, AttributeValue>) scanResult.getItems().get(i);
                
                User userValue = new User();
                userValue.setFullName(item.get("FULL_NAME").getS());
                userValue.setUserId(item.get("USER_ID").getS());
                userValue.setUsername(item.get("USERNAME").getS());
                
                tempUsers.add(userValue);
            }
        }
        catch (AmazonServiceException ase)
        {
            m_logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            m_logger.error("\nError Message:    " + ase.getMessage());
            m_logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            m_logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            m_logger.error("\nError Type:       " + ase.getErrorType());
            m_logger.error("\nRequest ID:       " + ase.getRequestId());
            
            throw new Exception(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            m_logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            m_logger.error("Error Message: " + ace.getMessage());
            
            throw new Exception(ace.getMessage());
        }
        
        return tempUsers;
    }
    
    public List<User> scanUser(String columnName, String value) throws Exception
    {
        List<User> tempUsers = new ArrayList<User>();
        List<Object> tempObjects = scan(User.class, columnName, value);
        
        for(Object tempObject : tempObjects)
        {
            tempUsers.add((User)tempObject);
        }
        
        return tempUsers;
    }

    public String getTableName()
    {
        return super.getTableName();
    }
}
