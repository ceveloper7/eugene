package com.gba.eugene.kernel.util;

public final class KeyNamePair extends NamePair{

    /**
     * generated serial id
     */
    private static final long serialVersionUID = 3697385478063688473L;

    public static final KeyNamePair EMPTY = new KeyNamePair(-1, "");

    /**
     *	Constructor KeyNamePair Pair -
     *  @param key Key (-1 is considered as null)
     *  @param name string representation
     */
    public KeyNamePair(int key, String name)
    {
        super(name);
        m_key = key;
    }   //  KeyNamePair

    /** The Key         */
    private int 	m_key = 0;

    /**
     *	Get Key
     *  @return key
     */
    public int getKey()
    {
        return m_key;
    }	//	getKey

    @Override
    public String getID() {
        if (m_key == -1)
            return null;
        return String.valueOf(m_key);
    }

    /**
     *	Equals
     *  @param obj object
     *  @return true if equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof KeyNamePair)
        {
            KeyNamePair pp = (KeyNamePair)obj;
            if (pp.getKey() == m_key
                    && pp.getName() != null
                    && pp.getName().equals(getName()))
                return true;
            return false;
        }
        return false;
    }	//	equals

    /**
     *  Return key as hash code of object
     *  @return key value
     */
    @Override
    public int hashCode()
    {
        return m_key;
    }   //  hashCode
}
