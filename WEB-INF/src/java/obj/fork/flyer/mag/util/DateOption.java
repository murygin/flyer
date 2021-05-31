package obj.fork.flyer.mag.util;

import java.util.Date;

/**
 * class to hold data vor html date selectboxes
 * it holds the option text and the option value
 *
 * Organisation:  FORK Unstable Media
 * @author schmidt. hlan, laser, vger, mrks
 */
public class DateOption {

  /**
   * the text to display in the selectbox
   */
  private String m_sText;

  /**
   * the value of the option
   */
  private String m_sValue;

  public DateOption( String p_sText, String p_sValue ) {
    setText( p_sText );
    setValue( p_sValue );
  }

  public DateOption( Date p_Date ) {
    String sDay;
    String sMonth = new Integer( p_Date.getMonth()+1 ).toString();
    if( sMonth.length()==1 )
      sMonth = "0" + sMonth;
    sDay = new Integer( p_Date.getDate() ).toString();
    if( sDay.length()==1 )
      sDay = "0" + sDay;
    int nDayId=p_Date.getDay();
    String sDayName = null;
    switch( nDayId ) {
      case 1: sDayName="Montag"; break;
      case 2: sDayName="Dienstag"; break;
      case 3: sDayName="Mittwoch"; break;
      case 4: sDayName="Donnerstag"; break;
      case 5: sDayName="Freitag"; break;
      case 6: sDayName="Samstag"; break;
      case 0: sDayName="Sonntag"; break;
    }
    m_sText = sDayName + ", " + sDay + "." + sMonth + "." + new Integer( p_Date.getYear()+1900 ).toString();
    m_sValue = new Integer( p_Date.getYear()+1900 ).toString() + "-" + sMonth + "-" + sDay;
  }

  public DateOption( Date p_Date, String p_sLangId, int p_nDummy ) {
    String sDay;
    String sMonth = new Integer( p_Date.getMonth()+1 ).toString();
    if( sMonth.length()==1 )
      sMonth = "0" + sMonth;
    sDay = new Integer( p_Date.getDate() ).toString();
    if( sDay.length()==1 )
      sDay = "0" + sDay;
    int nDayId=p_Date.getDay();
    String sDayName = null;
    switch( nDayId ) {
      case 1:
        if( p_sLangId.equals( "de" ) )
          sDayName="Montag";
        if( p_sLangId.equals( "en" ) )
          sDayName="Monday";
        break;
      case 2:
        if( p_sLangId.equals( "de" ) )
          sDayName="Dienstag";
        if( p_sLangId.equals( "en" ) )
          sDayName="Tuesday";
        break;
      case 3:
        if( p_sLangId.equals( "de" ) )
          sDayName="Mittwoch";
        if( p_sLangId.equals( "en" ) )
          sDayName="Wednesday";
        break;
      case 4:
        if( p_sLangId.equals( "de" ) )
          sDayName="Donnerstag";
        if( p_sLangId.equals( "en" ) )
          sDayName="Thursday";
        break;
      case 5:
        if( p_sLangId.equals( "de" ) )
          sDayName="Freitag";
        if( p_sLangId.equals( "en" ) )
          sDayName="Friday";
        break;
      case 6:
        if( p_sLangId.equals( "de" ) )
          sDayName="Samstag";
        if( p_sLangId.equals( "en" ) )
          sDayName="Saturday";
        break;
      case 0:
        if( p_sLangId.equals( "de" ) )
          sDayName="Sonntag";
        if( p_sLangId.equals( "en" ) )
          sDayName="Sunday";
        break;
    }
    if( p_sLangId.equals( "de" ) )
      m_sText = sDayName + ", " + sDay + "." + sMonth + "." + new Integer( p_Date.getYear()+1900 ).toString();
    if( p_sLangId.equals( "en" ) )
      m_sText = sDayName + ", " + sMonth + "/" + sDay + "/" + new Integer( p_Date.getYear()+1900 ).toString();
    m_sValue = new Integer( p_Date.getYear()+1900 ).toString() + "-" + sMonth + "-" + sDay;
  }

  public DateOption( Date p_Date, String p_sText ) {
    String sDay;
    String sMonth = new Integer( p_Date.getMonth()+1 ).toString();
    if( sMonth.length()==1 )
      sMonth = "0" + sMonth;
    sDay = new Integer( p_Date.getDate() ).toString();
    if( sDay.length()==1 )
      sDay = "0" + sDay;
    m_sText = p_sText;
    m_sValue = new Integer( p_Date.getYear()+1900 ).toString() + "-" + sMonth + "-" + sDay;
  }


  /**
   * returns the text
   */
  public String getText() {
    return m_sText;
  }
  /**
   * sets the text
   */
  public void setText( String p_sText ) {
    m_sText = p_sText;
  }

  /**
   * returns the value
   */
  public String getValue() {
    return m_sValue;
  }
  /**
   * sets the value
   */
  public void setValue( String p_sValue ) {
    m_sValue = p_sValue;
  }
}