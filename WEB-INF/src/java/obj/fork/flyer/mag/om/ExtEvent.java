package obj.fork.flyer.mag.om;

import java.util.Date;

import obj.fork.laika.om.Event;

/**
 * ExtEvent Erweitert das om Event um konfortable funktion
 * zur ausgabe des datums
 *
 * Organisation:  FORK Unstable Media
 * @author schmidt. hlan, laser, vger, mrks
 * @version
 */

public class ExtEvent extends Event {

  public ExtEvent( Event event ) {
    setEventId( event.getEventId() );
    setName(event.getName() );
    setBegin(event.getBegin() );
    setEnd( event.getEnd() );
    setOpeningtimes( event.getOpeningtimes() );
    setPrice( event.getPrice() );
    setBoxoffice( event.getBoxoffice() );
    setUrl1( event.getUrl1() );
    setUrl2( event.getUrl2() );
  }

  public ExtEvent() { }

  public String getBrdDate() {
    Date date = getBegin();
    String sYear, sMonth, sDay;
    sMonth = new Integer( date.getMonth()+1 ).toString();
    if( sMonth.length()==1 )
      sMonth = "0" + sMonth;
    sDay = new Integer( date.getDate() ).toString();
    if( sDay.length()==1 )
      sDay = "0" + sDay;
    int nDayId=date.getDay();
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
    return sDayName + ", " + sDay + "." + sMonth + "." + new Integer( date.getYear()+1900 ).toString();
  }

  public String getDate( String p_sLangId ) {
    String sResult=null;
    Date date = getBegin();
    String sYear, sMonth, sDay;
    sMonth = new Integer( date.getMonth()+1 ).toString();
    if( sMonth.length()==1 )
      sMonth = "0" + sMonth;
    sDay = new Integer( date.getDate() ).toString();
    if( sDay.length()==1 )
      sDay = "0" + sDay;
    int nDayId=date.getDay();
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
      sResult = sDayName + ", " + sDay + "." + sMonth + "." + new Integer( date.getYear()+1900 ).toString();
    if( p_sLangId.equals( "en" ) )
      sResult = sDayName + ", " + sMonth + "/" + sDay + "/" + new Integer( date.getYear()+1900 ).toString();
    return sResult;
  }

  public String getYear() {
    return new Integer( getBegin().getYear()+1900 ).toString();
  }

  public String getMonth() {
    String sMonth;
    sMonth = new Integer( getBegin().getMonth()+1 ).toString();
    if( sMonth.length()==1 )
      sMonth = "0" + sMonth;
    return sMonth;
  }

  public String getDayOfMonth() {
    String sDay;
    sDay = new Integer( getBegin().getDate() ).toString();
    if( sDay.length()==1 )
      sDay = "0" + sDay;
    return sDay;
  }

  public String getHour() {
    Integer nHour;
    if( getOpeningtimes()==null )
      return "00";
    if( getOpeningtimes().length()<3 )
      return "00";
    try {
      nHour = new Integer( getOpeningtimes().substring(0,2) );
    }
    catch( NumberFormatException nfe ) {
      return "00";
    }
    return nHour.toString();
  }

  public String getMinute() {
    Integer nMinute;
    if( getOpeningtimes().length()<6 )
      return "00";
    try {
      nMinute = new Integer( getOpeningtimes().substring(3,5) );
    }
    catch( NumberFormatException nfe ) {
      return "00";
    }
    return nMinute.toString();
  }
}