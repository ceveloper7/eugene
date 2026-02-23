package com.gba.eugene.kernel.util;

public class ConfigSystemError extends Exception{

    /**	Details					*/
    private Object	m_detail = null;

    public ConfigSystemError(String message){
        super(message);
    }

    public ConfigSystemError(String message, Object detail){
        super(message);
        setDetail(detail);
    }

    public ConfigSystemError(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * Get detail
     * @return detail.
     */
    public Object getDetail ()
    {
        return m_detail;
    }

    /**
     * Set detail
     * @param detail The detail to set.
     */
    public void setDetail (Object detail)
    {
        m_detail = detail;
    }

    @Override
    public String toString(){
        super.toString();
        StringBuilder sb = new StringBuilder("SystemError: ");
        sb.append(getLocalizedMessage());
        if(m_detail != null)
            sb.append("(").append(m_detail).append(")");

        return sb.toString();
    }
}
