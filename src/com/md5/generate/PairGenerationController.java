package com.md5.generate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.md5.database.Database;
import com.md5.util.Utilities;

/** 
 * A class for generating password / hash pairs and using those for populating
 * the database.
 */
public class PairGenerationController
{
    /**
     * Class to generate pairs using a space separated list of words in a thread.
     * A dialog is preseted when complete showing the number of new pairs added to the
     * database.
     */
    private class PairGenThread implements Runnable
    {
        private String _wordListString;

        PairGenThread( String wordListString ) 
        {
            super();

            _wordListString = wordListString ;
            
            Thread genThread = new Thread( this ) ;
            genThread.start() ;
        }

        @Override
        public void run() 
        {
            StringTokenizer tokenizer = new StringTokenizer( _wordListString ) ;
            List<String> wordList = new ArrayList<String>() ;
            while( tokenizer.hasMoreTokens() )
            {
                wordList.add( tokenizer.nextToken().trim() ) ;
            }
            
            int pairCount = 0 ;
            _method2(wordList) ;
            pairCount += _methodPairCount ;
            _method3(wordList) ;
            pairCount += _methodPairCount ;
            _writeAllPairSets() ;

            String message = pairCount + " pairs created using \"" + _wordListString + "\"" ;
            JOptionPane.showMessageDialog( null, message, "Pair Generator", JOptionPane.INFORMATION_MESSAGE ) ;
        }
    }
    
    /**
     * Class that generates a variety of pairs from a single input word.  The word be used as is
     * as well as adding punctuation and numerals to the end of the word.
     */
    private class PairGenerator
    {
        private List<String> _numberList ;
        
        PairGenerator()
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
        
        public int numPairs()
        {
            return _numberList.size() + 1 ;
        }
        
        public void genPairsWith( String baseWord )
        {
            _createPair( baseWord ) ;
            for( int i = 0 ; i < _numberList.size() ; i++ )
            {
                String password = baseWord + _numberList.get(i) ;
                _createPair( password ) ;
            }
        }
    }

    private static int __unwrittenPairs ;

    private int _methodPairCount = 0 ;
    private int _totalPairCount = 0 ;
    
    private final PairGenerator _pairGenerator = new PairGenerator() ;
    private final Database _database  ;

    /**
     * Class for creating passwords using an input of words and known common passwords.
     */
    public PairGenerationController( Database database ) 
    {
    	_database = database ;
    }
    
    /**
     * Forces the database to write unwritten pairs.
     */
    private void _writeAllPairSets() 
    {
        _database.flush() ;
    }
    
    /** 
     * Method to add a pair to the database.
     */
    private void _createPair( String password )
    {
        String md5 = Utilities.getMd5Hash(password) ;
        _database.addPair(password, md5) ;
        _methodPairCount++ ;
        _totalPairCount++ ;
    }
    
    /**
     * Method to display the results of a generation method.
     * 
     * @param pairCount
     * @param msTime
     */
    private void _provideStatus( int pairCount, double msTime )
    {
        double seconds = msTime / 1000.0 ;
        System.err.println( "  "+seconds+" seconds : "+pairCount+" pairs : "+(pairCount/seconds*100)/100+" pairs/second.") ;
    }
    
    /**
     * A simulation of a letter-style combination lock. Each'wheel' has the 
     * letters A-Z, a-z and 0-9 on it as well as a blank. The idea is that we
     * have a number of wheels for a user name and password and we try each
     * possible combination.Method to display the results of a generation method.
     * 
     * @param numPassWheels
     */
    public void _method1( int numPassWheels )
    {
        System.err.println("Using method 1 to generate pairs with "+numPassWheels+" password wheels.") ;
        
        long starttime = System.currentTimeMillis() ;
        int _methodPairCount = 0 ;

        String[] wheel = new String[Utilities.CHARACTERS.length + Utilities.PUNCTUATION.length] ;
        for( int i = 0 ; i < Utilities.CHARACTERS.length ; i++ )
        {
            wheel[i] = Utilities.CHARACTERS[i] ;
        }
        
        int wheelIndex = Utilities.CHARACTERS.length ;
        for( int i = 0 ; i < Utilities.PUNCTUATION.length ; i++ )
        {
            wheel[wheelIndex] = Utilities.PUNCTUATION[i] ;
            wheelIndex++ ;
        }
 
        // we only allow up to 8 wheels for each password for now
        if ( numPassWheels > 8 )
        {
            System.err.println("Unable to handle the request. No more than 8 characters for a password") ;
        }
        
        // set all of the wheels to the first position
        int[] passWheelArray = new int[numPassWheels] ;
            
        boolean done = false ;
        while( done == false )
        {
            StringBuffer password = new StringBuffer() ;
            for( int i = 0 ; i < numPassWheels ; i++ )
            {
                //if( passWheelArray[i] > 0 )
                {
                    int passWheelInt = passWheelArray[i] ;
                    //String wheelChar = wheel.substring( passWheelInt, passWheelInt+1 ) ;
                    String wheelChar = wheel[ passWheelInt ] ;
                    password.append( wheelChar ) ;
                }
            }
            
            _createPair( password.toString() ) ;
                        
            // spin the rightmost wheel and if it changes, spin the next one over and so on
            int carry = 1 ;
            for( int i = 0 ; i < numPassWheels ; i++ ) // once for each wheel
            {
                passWheelArray[i] = passWheelArray[i] + carry ;
                carry = 0 ;
                if( passWheelArray[i] > wheel.length - 1 )
                {
                    passWheelArray[i] = 1 ;
                    carry = 1 ;
                    if( i == (numPassWheels-1) )
                    {
                        done = true ;
                    }
                }
            }
        }
    
        long msDt = System.currentTimeMillis() - starttime ;
        _provideStatus( _methodPairCount, msDt ) ;
    }
    
    /**
     * Estimates the number of new pairs that will be created using the provided word list and 
     * using method #2.
     * 
     * @param wordList
     */
    public int _method2Count( List<String> wordList )
    {
        int numWords = wordList.size() ;
        return numWords * 3 * _pairGenerator.numPairs() ;
    }
    
    /**
     * Uses input from a file to generate pairs.
     * 
     * @param fileName
     */
    public void _method2( String fileName )
    {
        _method2( Utilities.getWordList( fileName ) ) ;
    }
    
    /**
     * Creates pairs using an word list.  Each word is capitalized, lowercased and uppercased
     * and used as a base for creating new pairs.
     * 
     * @param wordList
     */
    public void _method2( List<String> wordList )
    {
        int numWords = wordList.size() ;
        int estimatedPairs = numWords * 3 * _pairGenerator.numPairs() ;
        System.err.println( "\nUsing method 2 with "+numWords+" in the list") ;
        System.err.println( "Estimated " + estimatedPairs + " unique pairs.\n" ) ;
    
        long starttime = System.currentTimeMillis() ;
        int word1Index = 0 ; 
        int prevPairCount = 0 ;
        _methodPairCount = 0 ;
        
        String password = "" ;
        boolean done = false ;
        while( done == false )
        {
            if( _methodPairCount - prevPairCount > 5000 )
            {
                double pctComplete = (double)_methodPairCount/(double)estimatedPairs * 100.0 ;
                double pctRemain = 100.0 - pctComplete ;
                double msComplete = System.currentTimeMillis() - starttime ;
                double msRemain = ( msComplete / pctComplete ) * pctRemain ;
                System.err.println( "  " + _methodPairCount + " pairs processed of " + estimatedPairs + " : " + Math.round(pctComplete) + "%" ) ;
                System.err.println( "     Time remaining " + Utilities.getDurationBreakdown( (long) msRemain ) ) ;
                System.err.println( "     Last password = " + password ) ;
                System.err.println( "     word1Index = " + word1Index ) ;
                prevPairCount = _methodPairCount ;
            }
                        
            // Lowercase
            password = wordList.get(word1Index).toLowerCase() ;
            _pairGenerator.genPairsWith( password ) ;
                        
            // Capitalize the first letter
            String cap = Utilities.capitalize(password) ;
            _pairGenerator.genPairsWith( cap ) ;
    
            // Uppercase
            String uppercase = password.toUpperCase() ;
            _pairGenerator.genPairsWith( uppercase ) ;
    
            word1Index += 1 ;
            if (word1Index >= numWords)
            {
                done = true ;
            }
        }
    
        long msDt = System.currentTimeMillis() - starttime ;
        _provideStatus( _methodPairCount, msDt ) ;
    }
                
    /**
     * Estimates the number of new pairs that will be created using the provided word list and 
     * using method #3.
     * 
     * @param wordList
     */
    public int _method3Count( List<String> wordList )
    {
        int numWords = wordList.size() ;
        return  numWords * numWords * 4 * _pairGenerator.numPairs() ;
    }
    
    /**
     * Uses input from a file to generate pairs.
     * 
     * @param fileName
     */
    public void _method3( String fileName )
    {
        _method3( Utilities.getWordList( fileName ), 0, 0 );
    }
    
    /**
     * Uses input from a word list to generate pairs.
     * 
     * @param wordList
     */
    public void _method3( List<String> wordList )
    {
        _method3(wordList, 0, 0);
    }
    
    /**
     * Uses input from a word list to generate pairs.  Each word in the list is combined with
     * the other words in the list to create double word passwords.  Each of these in turn have punctuation
     * and numerals added to them.
     * 
     * @param wordList
     * @param word1Index
     * @param word2Index
     */
    public void _method3( List<String> wordList, int word1Index, int word2Index )
    {
        int numWords = wordList.size() ;
        int estimatedPairs = numWords * numWords * 4 * _pairGenerator.numPairs() ;
        System.err.println( "\nUsing method 3 with "+numWords+" in the list") ;
        System.err.println( "Estimated " + estimatedPairs + " unique pairs.\n" ) ;
        
        long starttime = System.currentTimeMillis() ;
        int prevPairCount = 0 ;
        _methodPairCount = 0 ;

        boolean done = false ;
        String password = "" ;
        while( done == false )
        {
            if( _methodPairCount - prevPairCount > 5000 )
            {
                double pctComplete = (double)_methodPairCount/(double)estimatedPairs * 100.0 ;
                double pctRemain = 100.0 - pctComplete ;
                double msComplete = System.currentTimeMillis() - starttime ;
                double msRemain = ( msComplete / pctComplete ) * pctRemain ;
                System.err.println( "  " + _methodPairCount + " pairs processed of " + estimatedPairs + " : " + Math.round(pctComplete) + "%" ) ;
                System.err.println( "     Time remaining " + Utilities.getDurationBreakdown( (long) msRemain ) ) ;
                System.err.println( "     Last password = " + password ) ;
                System.err.println( "     word1Index = " + word1Index ) ;
                System.err.println( "     word2Index = " + word2Index ) ;
                prevPairCount = _methodPairCount ;
                System.err.println( ">>>>>> " + __unwrittenPairs ) ;
            }
            
            String word1 = wordList.get( word1Index ).toLowerCase() ;
            String word2 = wordList.get( word2Index ).toLowerCase() ;
            String cap1 = Utilities.capitalize(word1) ;
            String cap2 = Utilities.capitalize(word2) ;
            

            // Try it the way they are in the word list
            password = word1 + word2 ;
            _pairGenerator.genPairsWith( password ) ;
            
            // Now let's try it with the first letter of the first word capitalized
            password = cap1 + word2 ;
            _pairGenerator.genPairsWith( password ) ;
            
            // Now let's try it with the first letter of the second word capitalized
            password = word1 + cap2 ;
            _pairGenerator.genPairsWith( password ) ;
            
            // Now let's try it with the both wordList capitalized
            password = cap1 + cap2 ;
            _pairGenerator.genPairsWith( password ) ;
    
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
    
        long msDt = System.currentTimeMillis() - starttime ;
        _provideStatus( _methodPairCount, msDt ) ;
    }
    
    /**
     * Method to create our database from our master word files.
     */
    private void _generatePairs()
    {
        long msStartTime = System.currentTimeMillis() ;
        _method2("100000.sorted") ;
        _method3("500.sorted") ;
        
        _writeAllPairSets() ;
        
        long msDt = System.currentTimeMillis() - msStartTime ;
        
        double seconds = msDt / 1000.0 ;
        System.err.println( "\nTOTAL: "+seconds+" seconds : "+_totalPairCount+" pairs : "+(_totalPairCount/seconds)+" pairs per second)") ;
    }
    
    /** 
     * Method used by GUI to add new pairs to the database.
     * @param wordListString
     */
    public void generatePairs( String wordListString )
    {
        new PairGenThread( wordListString ) ;
    }
    
    /** 
     * Method used by GUI to add new pairs to the database.
     * @param wordListString
     */
    public int estimateNewPairCount( String wordListString )
    {
        StringTokenizer tokenizer = new StringTokenizer( wordListString ) ;
        List<String> wordList = new ArrayList<String>() ;
        while( tokenizer.hasMoreTokens() )
        {
            wordList.add( tokenizer.nextToken().trim() ) ;
        }
        return _method2Count(wordList) + _method3Count(wordList) ;
    }
    
    public static void main( String[] args )
    {
        PairGenerationController pairGenController = new PairGenerationController( new Database() ) ;
        pairGenController._generatePairs();
    }
}
