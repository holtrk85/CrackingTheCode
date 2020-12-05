package com.md5.util;


/**
 * Convenience class for doing MD5 conversions.
 */
public class MD5
{
	public static void main( String[] args )
	{
		System.err.println( Utilities.getMd5Hash(args[0]) ) ;
	}
}
