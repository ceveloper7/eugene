package com.gba.eugene.kernel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DisplayType {
    /** Display Type 10	String	*/
    public static final int String     = 10;
    /** Display Type 11	Integer	*/
    public static final int Integer    = 11;
    /** Display Type 12	Amount	*/
    public static final int Amount     = 12;
    /** Display Type 13	ID	*/
    public static final int ID         = 13;
    /** Display Type 14	Text	*/
    public static final int Text       = 14;
    /** Display Type 15	Date	*/
    public static final int Date       = 15;
    /** Display Type 16	DateTime	*/
    public static final int DateTime   = 16;
    /** Display Type 17	List	*/
    public static final int List       = 17;
    /** Display Type 18	Table	*/
    public static final int Table      = 18;
    /** Display Type 19	TableDir	*/
    public static final int TableDir   = 19;
    /** Display Type 20	YN	*/
    public static final int YesNo      = 20;
    /** Display Type 21	Location	*/
    public static final int Location   = 21;
    /** Display Type 22	Number	*/
    public static final int Number     = 22;
    /** Display Type 23	BLOB	*/
    public static final int Binary     = 23;
    /** Display Type 24	Time	*/
    public static final int Time       = 24;
    /** Display Type 25	Account	*/
    public static final int Account    = 25;
    /** Display Type 26	RowID	*/
    public static final int RowID      = 26;
    /** Display Type 27	Color   */
    public static final int Color      = 27;
    /** Display Type 28	Button	*/
    public static final int Button	   = 28;
    /** Display Type 29	Quantity	*/
    public static final int Quantity   = 29;
    /** Display Type 30	Search	*/
    public static final int Search     = 30;
    /** Display Type 31	Locator	*/
    public static final int Locator    = 31;
    /** Display Type 32 Image	*/
    public static final int Image      = 32;
    /** Display Type 33 Assignment	*/
    public static final int Assignment = 33;
    /** Display Type 34	Memo	*/
    public static final int Memo       = 34;
    /** Display Type 35	PAttribute	*/
    public static final int PAttribute = 35;
    /** Display Type 36	CLOB	*/
    public static final int TextLong   = 36;
    /** Display Type 37	CostPrice	*/
    public static final int CostPrice  = 37;
    /** Display Type 38	File Path	*/
    public static final int FilePath  = 38;
    /** Display Type 39 File Name	*/
    public static final int FileName  = 39;
    /** Display Type 53670	File Path or Name	*/
    public static final int FilePathOrName  = 53670;
    /** Display Type 40	URL	*/
    public static final int URL  = 40;
    /** Display Type 42	PrinterName	*/
    public static final int PrinterName  = 42;
    /** Display Type 53370 Chart */
    public static final int Chart = 53370;

    private static Logger log = LoggerFactory.getLogger(DisplayType.class);

    /**
     * 	Get Default Precision.
     * 	Used for databases who cannot handle dynamic number precision.
     *	@param displayType display type
     *	@return scale (decimal precision)
     */
    public static int getDefaultPrecision(int displayType)
    {
        if (displayType == Amount)
            return 2;
        if (displayType == Number)
            return 6;
        if (displayType == CostPrice
                || displayType == Quantity)
            return 4;
        return 0;
    }	//	getDefaultPrecision
}
