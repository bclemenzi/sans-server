package com.nfbsoftware.sansserver.dynamodb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.nfbsoftware.sansserverplugin.sdk.util.DynamoDbUtility;
import com.nfbsoftware.sansserverplugin.sdk.util.Entity;

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
public abstract class AbstractDynamoDbDao
{
    protected String m_tableName;
    protected String m_baseTableName = "";
    
    protected Log m_logger;
    
    protected Properties m_properties;
    
    protected AmazonDynamoDBClient m_amazonDynamoDBClient;
    
    /**
     * 
     * @param properties
     * @param baseTableName
     * @param primaryId
     * @throws Exception
     */
    public AbstractDynamoDbDao(Properties properties, String baseTableName, String primaryId) throws Exception
    {
        m_logger = LogFactory.getLog(this.getClass());
        m_properties = properties;
        m_baseTableName = baseTableName;
        
        // Set the Amazon credentials
        try
        {
            String regionName = m_properties.getProperty(Entity.FrameworkProperties.AWS_REGION);
            String accessKey = m_properties.getProperty(Entity.FrameworkProperties.AWS_ACCESS_KEY);
            String secretKey = m_properties.getProperty(Entity.FrameworkProperties.AWS_SECRET_KEY);
            String environmentePrefix = m_properties.getProperty(Entity.FrameworkProperties.ENVIRONEMNT_PREFIX);
            
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            m_amazonDynamoDBClient = new AmazonDynamoDBClient(credentials);
            
            // Set our region
            m_amazonDynamoDBClient.setRegion(Region.getRegion(Regions.fromName(regionName)));

            if(!StringUtils.isNotEmpty(environmentePrefix))
            {
                throw new Exception("Our Amazon DynamoDB implementation requires a unique table name prefix.  Please make sure your properties files contains one.");
            }
            else
            {
                m_tableName = environmentePrefix + "_" + m_baseTableName;
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
            m_logger.error("Caught an AmazonServiceException, which means your request made it to AWS, but was rejected with an error response for some reason.\n");
            m_logger.error("\nError Message:    " + ase.getMessage());
            m_logger.error("\nHTTP Status Code: " + ase.getStatusCode());
            m_logger.error("\nAWS Error Code:   " + ase.getErrorCode());
            m_logger.error("\nError Type:       " + ase.getErrorType());
            m_logger.error("\nRequest ID:       " + ase.getRequestId());
        }
        catch (AmazonClientException ace)
        {
            m_logger.error("Caught an AmazonClientException, which means the client encountered a serious internal problem while trying to communicate with AWS, such as not being able to access the network.");
            m_logger.error("Error Message: " + ace.getMessage());
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
    public List<Object> scan(Class<?> clazz, String columnName, String value) throws Exception
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
            scanResult = (List<Object>)dynamoDBMapper.scan(clazz, scanExpression, finalConfiguration);
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
