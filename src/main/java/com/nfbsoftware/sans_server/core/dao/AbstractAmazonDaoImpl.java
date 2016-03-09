package com.nfbsoftware.sans_server.core.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.nfbsoftware.sans_server.core.util.DynamoDbUtility;

/**
 * The AbstractAmazonDaoImpl class is used as a base for managing the CRUD operations of a DynamoDB table.  You will notice that the constructor asks for a table
 * prefix used to override the annotated table name.  This prefix is used to help seperate development from QA and/or Production environments.  For example tables could follow the naming partern of:
 * 
 *  - QA_USERS  (QA Table)
 *  - PRODUCTION_USERS  (Production Table)
 *  - BCLEMENZI_USERS (Developer "Brendan Clemenzi" Tables)
 *  - JOESMITH_USERS (Developer "Joe Smith" Tables)
 * 
 * @author Brendan Clemenzi
 */
public abstract class AbstractAmazonDaoImpl
{
    protected String m_tableName;
    protected String m_baseTableName = "";
    
    protected AmazonDynamoDBClient m_amazonDynamoDBClient;
    
    protected static Log logger = null;
    
    public AbstractAmazonDaoImpl(String accessKey, String secretKey, Region region, String tableNamePrefix, String baseTableName, String primaryId) throws Exception
    {
        logger = LogFactory.getLog(this.getClass());
        
        m_baseTableName = baseTableName;
        
        // Set the Amazon credentials
        try
        {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            m_amazonDynamoDBClient = new AmazonDynamoDBClient(credentials);
            m_amazonDynamoDBClient.setRegion(region);

            if(!StringUtils.isNotEmpty(tableNamePrefix))
            {
                throw new Exception("Our Amazon DynamoDB implementation requires a unique table name prefix.  Please make sure your properties files contains one.");
            }
            else
            {
                m_tableName = tableNamePrefix + "_" + m_baseTableName;
            }

            System.out.println("--> Initializing AWS DynomoDB table: " + m_tableName);
            DynamoDbUtility.initializeDatabaseTable(m_amazonDynamoDBClient, m_tableName, primaryId);
        }
        catch (Exception e)
        {
            System.out.println("--> Could not initialize connection to AWS DynomoDB.  Update time (via ntpdate) and check DNS");
            throw e;
        }
    }
    
    public Object get(Class<?> clazz, String id)
    {
        Object object = null;
        
        try
        {
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(m_amazonDynamoDBClient);
            
            DynamoDBMapperConfig defaultConfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT);
            DynamoDBMapperConfig tableOverrides = new DynamoDBMapperConfig(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(m_tableName));
            
            DynamoDBMapperConfig finalConfiguration = new DynamoDBMapperConfig(defaultConfig, tableOverrides);
            object = dynamoDBMapper.load(clazz, id, finalConfiguration);
        }
        catch (AmazonServiceException ase)
        {
            logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            logger.error("\nError Message:    " + ase.getMessage());
            logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            logger.error("\nError Type:       " + ase.getErrorType());
            logger.error("\nRequest ID:       " + ase.getRequestId());
        }
        catch (AmazonClientException ace)
        {
            logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
        }
        
        return object;
    }
    
    public void create(Object model) throws Exception
    {
        try
        {
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(m_amazonDynamoDBClient);
            
            DynamoDBMapperConfig mapperConfiguration = new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(m_tableName));
            
            // Create the new user object
            dynamoDBMapper.save(model, mapperConfiguration);
        }
        catch (AmazonServiceException ase)
        {
            logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            logger.error("\nError Message:    " + ase.getMessage());
            logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            logger.error("\nError Type:       " + ase.getErrorType());
            logger.error("\nRequest ID:       " + ase.getRequestId());
            
            throw new Exception(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
            
            throw new Exception(ace.getMessage());
        }
    }
    
    public void update(Object model) throws Exception
    {
        try
        {
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(m_amazonDynamoDBClient);
            
            DynamoDBMapperConfig saveConfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.UPDATE);
            DynamoDBMapperConfig tableOverrides = new DynamoDBMapperConfig(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(m_tableName));
            
            DynamoDBMapperConfig mapperConfiguration = new DynamoDBMapperConfig(saveConfig, tableOverrides);
            
            // Create the new user object
            dynamoDBMapper.save(model, mapperConfiguration);
        }
        catch (AmazonServiceException ase)
        {
            logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            logger.error("\nError Message:    " + ase.getMessage());
            logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            logger.error("\nError Type:       " + ase.getErrorType());
            logger.error("\nRequest ID:       " + ase.getRequestId());
            
            throw new Exception(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
            
            throw new Exception(ace.getMessage());
        }
    }
    
    public void delete(Object model) throws Exception
    {
        try
        {
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(m_amazonDynamoDBClient);
            
            DynamoDBMapperConfig defaultConfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT);
            DynamoDBMapperConfig tableOverrides = new DynamoDBMapperConfig(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(m_tableName));
            
            DynamoDBMapperConfig finalConfiguration = new DynamoDBMapperConfig(defaultConfig, tableOverrides);
            
            // Delete the new user object
            dynamoDBMapper.delete(model, finalConfiguration);
        }
        catch (AmazonServiceException ase)
        {
            logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            logger.error("\nError Message:    " + ase.getMessage());
            logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            logger.error("\nError Type:       " + ase.getErrorType());
            logger.error("\nRequest ID:       " + ase.getRequestId());
            
            throw new Exception(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
            
            throw new Exception(ace.getMessage());
        }
    }
    
    /**
     * 
     * @param clazz
     * @param totalSegments
     * @param columnName
     * @param value
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<Object> scan(Class<?> clazz, int totalSegments, String columnName, String value) throws Exception
    {
        List<Object> scanResult = null;
        
        try
        {
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(m_amazonDynamoDBClient);
            
            DynamoDBMapperConfig defaultConfig = new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT);
            DynamoDBMapperConfig tableOverrides = new DynamoDBMapperConfig(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement(m_tableName));
            
            DynamoDBMapperConfig finalConfiguration = new DynamoDBMapperConfig(defaultConfig, tableOverrides);
            
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue> ();
            eav.put(":val1", new AttributeValue().withS(value));
            
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(columnName + " = :val1")
                .withExpressionAttributeValues(eav);

            // Get our scan results
            scanResult = (List<Object>)dynamoDBMapper.parallelScan(clazz, scanExpression, totalSegments, finalConfiguration);
        }
        catch (AmazonServiceException ase)
        {
            logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            logger.error("\nError Message:    " + ase.getMessage());
            logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            logger.error("\nError Type:       " + ase.getErrorType());
            logger.error("\nRequest ID:       " + ase.getRequestId());
            
            throw new Exception(ase.getMessage());
        }
        catch (AmazonClientException ace)
        {
            logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            logger.error("Error Message: " + ace.getMessage());
            
            throw new Exception(ace.getMessage());
        }
        
        return scanResult;
    }
    
    public String getTableName()
    {
        return m_tableName;
    }

    public String getBaseTableName()
    {
        return m_baseTableName;
    }
}
