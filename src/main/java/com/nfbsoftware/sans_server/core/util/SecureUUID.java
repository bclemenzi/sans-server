package com.nfbsoftware.sans_server.core.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

/**
 * The SecureUUID class is a utility that will help developers create IDs to be used for various purposes.
 * 
 * @author Brendan Clemenzi
 */
public class SecureUUID 
{
    /**
     * Generate a UUID that can be used for unguessable ids; for example, primary keys in the database.
     *  
     * @return
     */
    public static String generateGUID()
    {
        UUID uid = UUID.randomUUID();
        
        return uid.toString();
    }
    
    /**
     * Generates a random string, 14 characters in length, to be used for a salt.
     * 
     * @return
     */
    public static String generateSalt()
    {

        UUID uid = UUID.randomUUID();

        // Remove the dashes
        String uidStr = uid.toString().replace("-", "").substring(0, 14);
        
        // Return the "salt" string
        return uidStr;
    }
    
    /**
     * Generate a random unique id to be used visual ids; for example, public user profiles.
     * 
     * @return
     */
    public static long generateUniqueNumber() throws Exception
    {
        final Random gen = new Random();
        final Integer part1 = Math.abs(gen.nextInt());
        final Integer part2 = Math.abs(gen.nextInt());
        
        Long parts = Long.parseLong((part1.toString() + part2.toString()).substring(0, 16).trim());
        
        return parts;
    }
    
    /**
     * Generate a random unique id to be used visual ids; for example, public user profiles.
     * 
     * @return
     */
    public static long generateUniqueNumber(int size) throws Exception
    {
        final Random gen = new Random();
        final Integer part1 = Math.abs(gen.nextInt());
        final Integer part2 = Math.abs(gen.nextInt());
        
        String part1String = part1.toString().substring(size/2);
        String part2String = part2.toString().substring(size/2);
        
        Long parts = Long.parseLong((part1String + part2String).substring(0, size).trim());
        
        return parts;
    }
    
    /**
     * Generate an MD5 hash from an input string
     * 
     * @param input
     * @return
     */
    public static String generateMD5(String input) 
    {
        String md5 = null;

        try 
        {   
            // Create MessageDigest object for MD5
            MessageDigest digest = MessageDigest.getInstance("MD5");
         
            // Update input string in message digest
            digest.update(input.getBytes(), 0, input.length());
 
            // Converts message digest value in base 16 (hex) 
            md5 = new BigInteger(1, digest.digest()).toString(16);
 
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        
        return md5;
    }
    
    /**
     * Generate a salted MD5 hash from an input and salt string
     * 
     * @param input
     * @param salt
     * @return
     */
    public static String generateSaltedMD5(String input, String salt) 
    {
        return generateMD5(input + salt);
    }
}
