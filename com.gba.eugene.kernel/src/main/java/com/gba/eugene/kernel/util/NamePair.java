package com.gba.eugene.kernel.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;

public abstract class NamePair implements Comparator<Object>, Serializable, Comparable<Object> {

    /**
     * generated serial id
     */
    private static final long serialVersionUID = -8951698533385247842L;

    /**
     *  Protected Constructor
     *  @param   name    (Display) Name of the Pair
     */
    protected NamePair (String name)
    {
        m_name = name;
        if (m_name == null)
            m_name = "";
    }   //  NamePair

    /** The Name        */
    private String  m_name;

    /**
     *  Returns display value
     *  @return name
     */
    public String getName()
    {
        return m_name;
    }   //  getName

    /**
     *  Returns Key or Value as String
     *  @return String ID or null
     */
    public abstract String getID();

    /**
     * 	Comparable Interface (based on toString value)
     *  @param   o the Object to be compared.
     *  @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo (Object o)
    {
        return compare (this, o);
    }	//	compareTo

    /**
     * 	Comparable Interface (based on toString value) using Collator
     *  @param   o the Object to be compared.
     *  @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    public int compareTo (NamePair o)
    {
        return compare (this, o);
    }	//	compareTo

    /**
     *	Compare o1 and o2 (based on toString value)
     *  @param o1 Object 1
     *  @param o2 Object 2
     *  @return compareTo value
     */
    @Override
    public int compare(Object o1, Object o2) {
        String s1 = o1 == null ? "" : o1.toString();
        String s2 = o2 == null ? "" : o2.toString();
        return s1.compareTo (s2);    //  sort order ??
    }

    /**
     *	Compare o1 and o2 (based on toString value) using Collator
     *  @param o1 Object 1
     *  @param o2 Object 2
     *  @return compareTo value
     */
    public int compare (NamePair o1, NamePair o2)
    {
        String s1 = o1 == null ? "" : o1.toString();
        String s2 = o2 == null ? "" : o2.toString();
        Collator collator = Collator.getInstance();
        return collator.compare(s1, s2);
    }	//	compare

    /**
     *	To String - returns name
     *  @return Name
     */
    @Override
    public String toString()
    {
        return m_name;
    }	//	toString

    /**
     *	To String - detail
     *  @return String in format ID=Name
     */
    public String toStringX()
    {
        StringBuilder sb = new StringBuilder (getID());
        sb.append("=").append(m_name);
        return sb.toString();
    }	//	toStringX
}
