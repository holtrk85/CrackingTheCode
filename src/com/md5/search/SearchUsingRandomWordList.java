package com.md5.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.md5.database.Database;
import com.md5.database.Database.SearchResult;
import com.md5.util.Utilities;

/**
 * Class used to search the database using a list of words input from a file.
 */
public class SearchUsingRandomWordList 
{
    private class ShortNumberAdder
    {
        private List<String> _numberList ;
        
        ShortNumberAdder()
        {
            _numberList = new ArrayList<String>() ;
            for( int i = 0 ; i <= 99 ; i++ )
            {
                _numberList.add( "" + i ) ;
            }
            for( int i = 0 ; i <= 9 ; i++ )
            {
                _numberList.add( "0" + i ) ;
            }
            
            int startSize = _numberList.size() ;
            for( int i = 0 ; i < startSize ; i++ )
            {
                String currentNumber = _numberList.get(i) ;
                for( int j = 0 ; j < Utilities.PUNCTUATION.length - 1 ; j++ )
                {
                    String toAdd = Utilities.PUNCTUATION[j] + currentNumber ;
                    if( _numberList.contains( toAdd ) )
                    {
                        System.err.println("Contains " + toAdd ) ;
                    }
                    else
                    {        
                        _numberList.add( toAdd ) ;
                    }
                }
            }
        }
        
        Random random = new Random() ;
        boolean search( String basePassword )
        {
            int index = (int) (_numberList.size() * random.nextDouble()) ;
            StringBuffer sb = new StringBuffer(basePassword).append(_numberList.get(index)) ;
            return _search( sb.toString() ) ;
        }
    }
    
    private final ShortNumberAdder _sna = new ShortNumberAdder() ;
    private final Database _database = new Database() ;
    private final StringBuffer _sb = new StringBuffer() ;
    
    private int _searchCount;
    private int _successCount;
    private int _singleWordSearchCount ;
    private int _singleWordSuccessCount ;
    private int _doubleWordSearchCount ;
    private int _doubleWordSuccessCount ;
    private String _fileName ;
    private long _msDt;

    /**
     * Constructor.   Takes a list of file names and creates numerous searches
     * from the words in each file.
     * 
     * @param fileNames
     */
    public SearchUsingRandomWordList( String[] fileNames ) 
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
        int intPercentCracked = (int)((double)_singleWordSuccessCount / (double)_singleWordSearchCount * 10000 ) ;
        double percentCracked = (double)intPercentCracked / 100.0 ;
        System.err.println( "   Single Word : " + _singleWordSearchCount + " searches : " + _singleWordSuccessCount + " passwords cracked : " + percentCracked + "% success " ) ;
        intPercentCracked = (int)((double)_doubleWordSuccessCount / (double)_doubleWordSearchCount * 10000 ) ;
        percentCracked = (double)intPercentCracked / 100.0 ;
        System.err.println( "   Double Word : " + _doubleWordSearchCount + " searches : " + _doubleWordSuccessCount + " passwords cracked : " + percentCracked + "% success " ) ;
        intPercentCracked = (int)((double)_successCount / (double)_searchCount * 10000 ) ;
        percentCracked = (double)intPercentCracked / 100.0 ;
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
        _singleWordSearchCount = 0 ;
        _singleWordSuccessCount = 0 ;
        _doubleWordSearchCount = 0 ;
        _doubleWordSuccessCount = 0 ;
        
        String password = "" ;
        int word1Index = 0 ;
        int word2Index = 0 ;
        int numWords = wordList.size() ;
        boolean done = false ;
        while( done == false )
        {
            String word1 = wordList.get( word1Index ) ;
            String word2 = wordList.get( word2Index ) ;
            String cap1 = Utilities.capitalize(word1) ;
            String cap2 = Utilities.capitalize(word2) ;
            
            if( word1.equals(word2) )
            {
                if( _search( word1 ) ) _singleWordSuccessCount++ ;
                _singleWordSearchCount++ ;
                if( _sna.search( word1 ) ) _singleWordSuccessCount++ ;
                _singleWordSearchCount++ ;
                if( _search( cap1 ) ) _singleWordSuccessCount++ ;
                _singleWordSearchCount++ ;
                if( _sna.search( cap1 ) ) _singleWordSuccessCount++ ;
                _singleWordSearchCount++ ;
                if( _search( word1.toUpperCase() ) ) _singleWordSuccessCount++ ;
                _singleWordSearchCount++ ;
                if( _sna.search( word1.toUpperCase() ) ) _singleWordSuccessCount++ ;
                _singleWordSearchCount++ ;
            }
            
            // Try it the way they are in the word list
            {
                _sb.delete( 0, _sb.length() ) ;
                _sb.append(word1).append(word2) ;
                password = _sb.toString() ;
                _doubleWordSearchCount++ ;
                if( _search( password ) )
                {
                    _doubleWordSuccessCount++ ;
                    if( _sna.search(password) ) _doubleWordSuccessCount++ ;
                    _doubleWordSearchCount++ ;
                }
            }
            
            // Now let's try it with the first letter of the first word capitalized
            {
                _sb.delete( 0, _sb.length() ) ;
                _sb.append(cap1).append(word2) ;
                password = _sb.toString() ;
                _doubleWordSearchCount++ ;
                if( _search( password ) )
                {
                    _doubleWordSuccessCount++ ;
                    if( _sna.search(password) ) _doubleWordSuccessCount++ ;
                    _doubleWordSearchCount++ ;
                }
            }
            
            // Now let's try it with the first letter of the second word capitalized
            {
                _sb.delete( 0, _sb.length() ) ;
                _sb.append(word1).append(cap2) ;
                password = _sb.toString() ;
                _doubleWordSearchCount++ ;
                if( _search( password ) )
                {
                    _doubleWordSuccessCount++ ;
                    if( _sna.search(password) ) _doubleWordSuccessCount++ ;
                    _doubleWordSearchCount++ ;
                }
            }
            
            // Now let's try it with the both wordList capitalized
            {
                _sb.delete( 0, _sb.length() ) ;
                _sb.append(cap1).append(cap2) ;
                password = _sb.toString() ;
                _doubleWordSearchCount++ ;
                if( _search( password ) )
                {
                    _doubleWordSuccessCount++ ;
                    if( _sna.search(password) ) _doubleWordSuccessCount++ ;
                    _doubleWordSearchCount++ ;
                }
            }
            
            word2Index += 1 ;
            if( word2Index >= numWords )
            {
                word2Index = 0 ;
                word1Index += 1 ;
                if( word1Index >= numWords )
                {
                    done = true ;
                }
            }
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
            
        if( searchResult.success() ) _successCount++ ;
        
        return searchResult.success() ;
    }
    
    public static void main( String[] args )
    {
        if( args.length == 0 )
        {
            System.err.println( "Please provide a word list" ) ;
        }
        
        new SearchUsingRandomWordList( args ) ;
    }
}
