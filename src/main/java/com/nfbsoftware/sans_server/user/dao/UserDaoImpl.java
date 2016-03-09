package com.nfbsoftware.sans_server.user.dao;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.nfbsoftware.sans_server.core.dao.AbstractAmazonDaoImpl;
import com.nfbsoftware.sans_server.user.model.User;

/**
 * The UserManager is an implementation class used to manage the CRUD operations for the User model objects in DynamoDB
 * 
 * @author Brendan Clemenzi
 */
public class UserDaoImpl extends AbstractAmazonDaoImpl
{
    public UserDaoImpl(String accessKey, String secretKey, String regionName, String tableNamePrefix) throws Exception
    {
        // Init our DOA with our keys, region, table prefix, table name, and primary key column
        super(accessKey, secretKey, Region.getRegion(Regions.fromName(regionName)), tableNamePrefix, "USERS", "USERNAME");
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
