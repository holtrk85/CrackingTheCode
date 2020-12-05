package com.md5.search;

import java.util.List;

import com.md5.database.Database;
import com.md5.database.Database.SearchResult;
import com.md5.util.Utilities;

/**
 * Class used to search the database using a list of words input from a file.
 */
public class SearchUsingAbsoluteWordList 
{
    
    private final Database _database = new Database() ;
    
    private int _searchCount;
    private int _successCount;
    private String _fileName ;
    private long _msDt;

    /**
     * Constructor.   Takes a list of file names and creates numerous searches
     * from the words in each file.
     * 
     * @param fileNames
     */
    public SearchUsingAbsoluteWordList( String[] fileNames ) 
    {
        super();
        
        for( String fileName : fileNames )
        {
            _fileName = fileName ;
            long msStart = System.currentTimeMillis() ;
            _parse() ;
            _msDt = System.currentTimeMillis() - msStart ;
            _displayResults() ;
        }
    }
    
    /**
     * Method to display the results for each set of searches for each file used as input.
     */
    private void _displayResults() 
    {
        double percentCracked = (double)_successCount / (double)_searchCount * 100.0 ;
        System.err.println( "   Total :" + _searchCount + " searches "  + _successCount + " passwords cracked : " + percentCracked + "% success " ) ;
        System.err.println( "   Time : " + (_msDt/1000.0) + " seconds." );
    }

    /**
     * Method that reads the input file.  The words in the list are used to 
     * generate a wide variety of searches by upper casing, lower casing, capitalizing
     * and adding punctuation and numerals to each generated password.
     */
    private void _parse() 
    {
        System.err.println( _fileName ) ;
        List<String> wordList = Utilities.getWordList( _fileName ) ;
        
        _searchCount = 0 ;
        _successCount = 0 ;
        
        int numWords = wordList.size() ;
        for( int i = 0 ; i < numWords ; i++ )
        {
            String word1 = wordList.get( i ) ;
            if( _search( word1 ) ) _successCount++ ;
        }
    }
        
    /**
     * Method to initiate a search of the database using a single password.
     * The password is turned into a MD5 hash and the database is searched.
     * 
     * @param password
     * @return True if successful
     */
    private boolean _search( String password )
    {
        _searchCount++ ;
        String hash = Utilities.getMd5Hash( password ) ;
        SearchResult searchResult = _database.search( hash ) ;
                    
        return searchResult.success() ;
    }
    
    public static void main( String[] args )
    {
        if( args.length == 0 )
        {
            System.err.println( "Please provide a word list" ) ;
        }
        
        new SearchUsingAbsoluteWordList( args ) ;
    }
}
