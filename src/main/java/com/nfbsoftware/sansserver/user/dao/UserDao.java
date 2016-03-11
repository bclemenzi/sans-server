package com.nfbsoftware.sansserver.user.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.nfbsoftware.sansserver.sdk.dynamodb.AbstractDynamoDbDao;
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
    
    public List<User> scanUser(int totalSegments, String columnName, String value) throws Exception
    {
        List<User> tempUsers = new ArrayList<User>();
        List<Object> tempObjects = scan(User.class, totalSegments, columnName, value);
        
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
