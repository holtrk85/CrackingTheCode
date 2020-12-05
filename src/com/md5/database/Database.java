package com.md5.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Class to control access to our database.
 */
public class Database 
{
    /**
     * Class to represent a set of pairs that belong in the same database file.
     */
    private class PairSet
    {
        private String _filePath;
        private String _fileName;
        private List<String> _pairList = new ArrayList<String>() ;

        PairSet( String fileKey ) 
        {
            String fileDir = fileKey.substring(0,2) ;
            _filePath = _dbPath + "/" + fileDir ;
            _fileName = _filePath + "/" + fileKey ;
        }
        
        /**
         * Method to add a pair to the unwritten pair list.
         * 
         * @param password
         * @param md5
         */
        void addPair( String password, String md5 ) 
        {
            _pairList.add( md5 + " " + password ) ;
            _unwrittenPairs++ ;

            // If the pair list is greater than 100, write the pairs to a file.
            if( _pairList.size() >= 100 )
            {
                writePairList() ;
            }
        }
        
        /**
         * Method to write a the pair list to the 
         * @return
         */
        int writePairList() 
        {
            int count = 0 ;
            if( _pairList.size() > 0 )
            {
                File pathFile = new File(_filePath) ;
                File pairFile = new File(_fileName) ;
                
                if( pairFile.exists() == false )
                {
                    try 
                    {
                        pathFile.mkdirs() ;
                        pairFile.createNewFile() ;
                    } 
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
    
                BufferedWriter bw = null;
                try
                {
                    bw = new BufferedWriter(new FileWriter(_fileName, true)) ;
                    for( int i = 0 ; i < _pairList.size() ; i++ )
                    {
                        String pair = _pairList.get(i) ;
                        bw.write(pair);
                        bw.newLine();
                        count++ ;
                        _unwrittenPairs-- ;
                    }
                    
                    bw.flush() ;
                }
                catch (IOException ioe) 
                {
                    ioe.printStackTrace();
                } 
                finally 
                {
                    if (bw != null)
                    {
                        try 
                        {
                            bw.close();
                        } 
                        catch (IOException ioe2) 
                        {
                            // Ugh
                        }
                    }
                } 
            
                _pairList.clear() ;
            }
            
            return count ;
        }
    }
    /**
     * Class to represent the data returned from a search of the database.
     */
    public static class SearchResult
    {
        private boolean _success ;
        private String _location ;
        private String _password ;
        
        SearchResult() {}
        
        public void setSuccess( boolean value )
            { _success = value ; }

        public boolean success()
            { return _success ; }

        public void setLocation( String value )
            { _location = value ; }
    
        public String getLocation()
            { return _location ; }
    
        public void setPassword( String value )
            { _password = value ; }
    
        public String getPassword()
            { return _password ; }
    }

    private final SearchResult _searchResult = new SearchResult() ;
    private final HashMap<String, PairSet> _pairMap = new HashMap<String, PairSet>() ;
    private final String _dbPath ;
    
    private int _unwrittenPairs ;

    public Database()
    {
        this( System.getProperty("user.dir") + "/database" ) ;
    }

    public Database( String dbPath ) 
    {
        super() ;
        
        _dbPath = dbPath ;
    }

    /**
     * Method to search the database for a password using a MD5 hash. as input.
     * 
     * @param md5
     * @return Returns the search result.
     */
    public SearchResult search( String md5 )
    {
        String fileDir = md5.substring(0,2) ;
        String fileKey = md5.substring(0,4) ;
        String path =  _dbPath + "/" + fileDir ;
        String fileName = path + "/" + fileKey ;
        File file = new File( fileName ) ;
        
        boolean success = false ;
        String resultPassword = "" ;
        if( file.exists() == false )
        {
            // Do nothing
        }
        else
        {
            try
            {
                Scanner scanner = new Scanner(file);
                while( scanner.hasNext() )
                {
                    String pair = scanner.nextLine();
                    if( pair.contains(md5)) 
                    {
                        StringTokenizer tokenizer = new StringTokenizer( pair.trim() ) ;
                        tokenizer.nextToken() ;
                        resultPassword = tokenizer.nextToken().trim() ;
                        success = true ;
                        break ;
                    }        
                }
                scanner.close() ; 
            } 
            catch (FileNotFoundException e) 
            {
                e.printStackTrace();
            }
        }
        
        _searchResult.setSuccess( success ) ;
        if( success )
        {
           _searchResult.setLocation( fileDir + "/" + fileKey ) ;
           _searchResult.setPassword( resultPassword );
        }
        
        return _searchResult ;

    }    
    
    /** 
     * Method to add a new pair to the database.
     * 
     * @param password
     * @param md5
     */
    public void addPair( String password, String md5 )
    {
        String fileKey = md5.substring(0,4) ;
        PairSet pairSet = _pairMap.get(fileKey) ;
        if( pairSet == null )
        {
            pairSet = new PairSet( fileKey ) ;
            _pairMap.put( fileKey, pairSet ) ;
        }
        pairSet.addPair(password, md5) ;
    }
    
    /**
     * Method to force the writing of unwritten pairs.
     */
    public void flush() 
    {
        System.err.println( "Writing " + _unwrittenPairs + " pairs" ) ;
        _unwrittenPairs = 0 ;
        Iterator<PairSet> pairSets = _pairMap.values().iterator() ;
        while( pairSets.hasNext() )
        {
            PairSet pairSet = pairSets.next() ;
            pairSet.writePairList() ;            
        }
    }
    
    public String getDatabasePath()
    {
    	return _dbPath ;
    }
}
