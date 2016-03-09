package com.nfbsoftware.sans_server.core.util;

/**
 * The Entity class is used to store static values of our various objects
 *
 * @author Brendan Clemenzi
 *
 */
public class Entity
{
    /**
     * The FrameworkProperties class is used to store known properties that will be loaded by the BaseLambdaHandler
     */
    public class FrameworkProperties
    {
        public static final String AWS_REGION                       = "aws.region";
        public static final String AWS_ACCESS_KEY                   = "aws.accessKey";
        public static final String AWS_SECRET_KEY                   = "aws.secretKey";
        public static final String AWS_COGNITO_IDENTITY_POOL_ID     = "aws.cognito.identityPoolId";
        public static final String AWS_CONGITO_PROVIDER_NAME        = "aws.cognito.providerName";
        public static final String AWS_DYNAMODB_TABLE_NAME_PREFIX   = "aws.dynamodb.tableNamePrefix";
    }
}
