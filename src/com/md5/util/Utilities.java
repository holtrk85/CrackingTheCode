package com.md5.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class of reusable variables and methods.
 */
public class Utilities 
{
    public static final String[] CHARACTERS = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                                               "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                                               "0","1","2","3","4","5","6","7","8","9"," "} ;
    public static final String[] PUNCTUATION = { "@","#","$","%","&","X" } ;
    
    public static String VALID_CHARS ;
    
    private static MessageDigest _md ;
    
    static
    {
        for( int i = 0 ; i < CHARACTERS.length ; i++ )
        {
            VALID_CHARS += CHARACTERS[i] ;
        }
        
        for( int i = 0 ; i < PUNCTUATION.length ; i++ )
        {
            VALID_CHARS += PUNCTUATION[i] ;
        }
        
        try 
        {
            _md = MessageDigest.getInstance("MD5") ;
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Convert a millisecond duration to a string format
     * 
     * @param millis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        return(sb.toString());
    }
    
    /**
     * Method to convert the words in a file to a Java java.util.List.
     * 
     * @param fileName
     * @return List of words.
     */
    public static List<String> getWordList( String fileName )
    {
        List<String> wordList = new ArrayList<String>() ;
        File file = new File( fileName ) ;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file ));
            String line = null;
            while ((line = br.readLine()) != null) 
            {
                wordList.add( line.trim() ) ;
            }
            br.close();        
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        return wordList ;
    }
    
    /**
     * Method to turn the input word into its capitalized version.
     * 
     * @param value
     * @return
     */
    public static String capitalize( String value )
    {
        String returnValue = value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase() ;
        return returnValue ;
    }
    
    /**
     * Method to get the MD5 hash for an input string.
     * 
     * @param value
     * @return MD5 hash
     */
    public static String getMd5Hash( String value )
    {
        _md.update(value.getBytes());
        byte[] digest = _md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) 
        {
            sb.append(String.format("%02x", b & 0xff));
        }
        String md5 = sb.toString() ;

        return md5 ;
    }
}
