package com.nfbsoftware.sans_server.core.util;

import java.util.Arrays;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.TableStatus;

/**
 * The DynamoDbUtility class is used as a helper utility for initializing the Amazon DynamoDB tables
 * 
 * @author Brendan Clemenzi
 */
public class DynamoDbUtility
{
    public static void initializeDatabaseTable(AmazonDynamoDBClient amazonDynamoDBClient, String tableName, String primaryId) throws Exception
    {
        // Check to see if we have already created the table space
        if(!hasDatabaseTable(amazonDynamoDBClient, tableName))
        {
            DynamoDB dynamoDB = new DynamoDB(amazonDynamoDBClient);
            
            System.out.println("Creating Table " + tableName);
            
            dynamoDB.createTable(tableName,
                Arrays.asList(
                    new KeySchemaElement(primaryId, KeyType.HASH)), //Partition key
                    Arrays.asList(
                            new AttributeDefinition(primaryId, ScalarAttributeType.S)), 
                    new ProvisionedThroughput(10L, 10L));
            
            System.out.println("Created Table " + tableName);
            
            waitForTableToBecomeAvailable(amazonDynamoDBClient, tableName);
        }
    }
    
    /**
     * 
     * @param tableName
     * @return
     */
    public static boolean hasDatabaseTable(AmazonDynamoDBClient amazonDynamoDBClient, String tableName)
    {
        String lastEvaluatedTableName = null;
        do
        {
            ListTablesRequest listTablesRequest = new ListTablesRequest().withLimit(100).withExclusiveStartTableName(lastEvaluatedTableName);

            ListTablesResult result = amazonDynamoDBClient.listTables(listTablesRequest);
            lastEvaluatedTableName = result.getLastEvaluatedTableName();

            for (String name : result.getTableNames())
            {
                if(name.equalsIgnoreCase(tableName))
                {
                    System.out.println("Found Table " + tableName);
                    return true;
                }
            }
        }
        while (lastEvaluatedTableName != null);
        
        System.out.println("Table " + tableName + " not found.");
        return false;
    }
    
    /**
     * 
     * @param tableName
     */
    public static void waitForTableToBecomeAvailable(AmazonDynamoDBClient amazonDynamoDBClient, String tableName)
    {
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (10 * 60 * 1000);
        while (System.currentTimeMillis() < endTime)
        {
            try
            {
                Thread.sleep(1000 * 20);
            }
            catch (Exception e)
            {
            }
            try
            {
                DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
                TableDescription tableDescription = amazonDynamoDBClient.describeTable(request).getTable();
                String tableStatus = tableDescription.getTableStatus();
                System.out.println("  - current state " + tableName + ": " + tableStatus);
                if (tableStatus.equals(TableStatus.ACTIVE.toString()))
                {
                    return;
                }
            }
            catch (AmazonServiceException ase)
            {
                if (ase.getErrorCode().equalsIgnoreCase("ResourceNotFoundException") == false)
                {
                    throw ase;
                }
            }
        }

        throw new RuntimeException("Table " + tableName + " never went active");
    }
}
