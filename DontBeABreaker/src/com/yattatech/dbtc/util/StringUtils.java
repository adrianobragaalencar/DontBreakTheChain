/*
 * Copyright (c) 2014, Yatta Tech and/or its affiliates. All rights reserved.
 * YATTATECH PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.yattatech.dbtc.util;

import java.util.Locale;

/**
 * String manipulation utility class based on 
 * apache lang package
 * 
 * @author Adriano Braga Alencar (adrianobragaalencar@gmail.com)
 *
 */
public final class StringUtils {
	
	public static final String EMPTY_STR = "";

    /**
     * <p>Checks if a String is empty ("") or null or blank space ("   ").</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     * 
     */
    public static boolean isEmpty(String str) {
        return (str == null) || (str.isEmpty()) || (str.trim().isEmpty());
    }
    
    /**
     * <p>Checks if String contains a search String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>false</code>.</p>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return true if the String contains the search String,
     *  false if not or <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }
    
    /**
     * <p>Checks if String contains a search String irrespective of case,
     * handling <code>null</code>. This method uses
     * {@link #contains(String, String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>false</code>.</p>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return true if the String contains the search String irrespective of
     * case or false if not or <code>null</code> string input
     */
    public static boolean containsIgnoreCase(String str, String searchStr) {
    	if ((str == null) || (searchStr == null)) {
            return false;
        }
    	final Locale locale = Locale.getDefault();
        return contains(str.toUpperCase(locale), searchStr.toUpperCase(locale));
    }
    
    /**
     * <p>Compares two Strings, returning <code>true</code> if they are equal.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * @see java.lang.String#equals(Object)
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or
     *  both <code>null</code>
     */
    public static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }
    
    /**
     * <p>Removes all occurances of a substring from within the source string.</p>
     *
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     *  
     */
    public static String remove(String str, String remove) {
        if ((isEmpty(str)) || (remove == null)) {
            return str;
        }
        int start = 0;
        int end   = str.indexOf(remove, start);
        if (end == -1) {
            return str;
        }
        final int removeLength  = remove.length();
        final StringBuilder buf = new StringBuilder(str.length() + 64);
        while (end != -1) {
            buf.append(str.substring(start, end));
            start = end + removeLength;
            end   = str.indexOf(remove, start);
        }
        buf.append(str.substring(start));
        return buf.toString();
    }
    
    /**
     * <p>Checks if the String contains only unicode digits.
     * A decimal point is not a unicode digit and returns false.</p>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if the String has length equals to desirable
     * length
     * 
     * @param str the String to check, may be null
     * @param length desirable length
     * @return <code>true</code> if so, <code>false</code> otherwise
     */
    public static boolean isLengthEqualsTo(String str, int length) {
    	if (str == null) {
    		return false;
    	}
    	return str.length() == length;
    }
	
	private StringUtils() {
		throw new AssertionError();
	}	
}
