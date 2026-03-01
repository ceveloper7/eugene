package com.gba.eugene.kernel.util;

public final class ValueNamePair extends NamePair{

    /**
     * generated serial id
     */
    private static final long serialVersionUID = -8315081356949462163L;

    public static final ValueNamePair EMPTY = new ValueNamePair("", "");

    /**
     *	Construct ValueNamePair Pair
     *  @param value value
     *  @param name string representation
     */
    public ValueNamePair(String value, String name)
    {
        super(name);
        m_value = value;
        if (m_value == null)
            m_value = "";
    }   //  ValueNamePair

    /** The Key Value       */
    private String m_value = null;

    /**
     *	Get Key Value
     *  @return Key Value
     */
    public String getValue()
    {
        return m_value;
    }	//	getValue

    /**
     *	Get String ID
     *  @return Value
     */
    public String getID()
    {
        if("".equals(m_value))
            return null;
        return m_value;
    }	//	getID

    /**
     *	Equals
     *  @param obj Object
     *  @return true, if equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ValueNamePair)
        {
            ValueNamePair pp = (ValueNamePair)obj;
            if (pp.getName() != null && pp.getValue() != null &&
                    pp.getName().equals(getName()) && pp.getValue().equals(m_value))
                return true;
            return false;
        }
        return false;
    }	//	equals

    /**
     * Get the Object representation of the value
     * @return value
     */
    public Object getValueObject()
    {
        return m_value;
    }

    /**
     *  Return Hashcode of value
     *  @return hascode
     */
    @Override
    public int hashCode()
    {
        return m_value.hashCode();
    }   //  hashCode
}
