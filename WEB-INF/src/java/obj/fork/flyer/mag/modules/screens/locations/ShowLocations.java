package obj.fork.flyer.mag.modules.screens.locations;

// jdk
import java.util.Vector;
import java.util.Enumeration;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.template.TemplateLink;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;

// jsdk
import javax.servlet.http.HttpSession;

// laika
import obj.fork.laika.om.Issue;
import obj.fork.laika.om.Edition;
import obj.fork.laika.om.EditionSubjectPeer;
import obj.fork.laika.om.Location;
import obj.fork.laika.om.LocationPeer;
import obj.fork.laika.om.SubjectPeer;
import obj.fork.laika.om.SubjectKeywordPeer;
import obj.fork.laika.om.SubjecttypePeer;
import obj.fork.laika.om.SubjecttypeKeywordPeer;
import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.om.AddressPeer;
import obj.fork.laika.util.Selectable;

//mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.om.LocationKeywordWrapper;
import obj.fork.flyer.mag.MagConstants;

/**
 * class ShowLocations provides data for template /screens/locations/ShowLocations.vm
 * ShowLocations displays locations
 * its gets several parameter of /navigations/locations/SelectForm.vm
 * all values are post values of the select form
 *
 * @cmb_type the txpe of the location, a keyword_id value of table keyword
 * @cmb_style the wstyle of the location, a keyword_id value of table keyword
 * @txtChar the name of the location or the beginning of the name
 * @cmb_district the district of the location
 */
public class ShowLocations extends WeakScreen {

    //the number of displayed records
    private int m_nNumberOfRecods=15;

    //the fist record to display
    int m_nFirstRecord=0;

    //total number of locations
    private int m_nNumberOfLocations;

    public void doBuildTemplate( RunData data, Context context ) {
      Vector vecLocationKeywords = new Vector();

      //search parameters
		  String sTypeId="-1";
		  String sStyleId="-1";
		  String sName="-1";
		  String sDistrict="-1";

      int nNext=-1;
      int nLast=-1;


      //the edition id
      int nEditionId=-1;
      String sLangId = null;

      //setting up the cache service
      GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

      TemplateLink urlLink = new TemplateLink( data );
      urlLink.setPage( "locations,ShowLocations.vm" );

      TemplateLink urlBack = new TemplateLink( data );
      urlBack.setPage( "locations,ShowLocations.vm" );

      TemplateLink urlNext = new TemplateLink( data );
      urlNext.setPage( "locations,ShowLocations.vm" );

      //read parameters
      if( data.getSession().getAttribute( "activeEdition" )!=null )
        nEditionId = ((Edition)data.getSession().getAttribute( "activeEdition" )).getEditionId().getBigDecimal().intValue();
      else {
        Log.error( "ShowLocations, doPerform, no edition found in session. Stop excecuting." );
        return;
      }
      if( data.getSession().getAttribute( "activeIssue" )!=null ) {
      int nLangId = ((Issue)data.getSession().getAttribute( "activeIssue" )).getLangId().getBigDecimal().intValue();;
        if( nLangId==1 )
          sLangId="de";
        if( nLangId==2 )
          sLangId="en";
      }
      else {
        Log.error( "ShowEvents, doPerform, no issue found in session. Stop excecuting." );
        return;
      }

    	if( data.getParameters().getString( "cmb_type" )!=null && data.getParameters().getString( "cmb_type" ).length()>0 ) {
    		sTypeId = data.getParameters().getString( "cmb_type" );
        urlLink.addQueryData( "cmb_type", sTypeId );
        urlBack.addQueryData( "cmb_type", sTypeId );
        urlNext.addQueryData( "cmb_type", sTypeId );
        context.put( "sSelectedType", sTypeId );
      }

    	if( data.getParameters().getString( "cmb_style" )!=null && data.getParameters().getString( "cmb_style" ).length()>0 ) {
    		sStyleId = data.getParameters().getString( "cmb_style" );
    	  urlLink.addQueryData( "cmb_style", sStyleId );
        urlBack.addQueryData( "cmb_style", sStyleId );
        urlNext.addQueryData( "cmb_style", sStyleId );
        context.put( "sSelectedStyle", sStyleId );
      }

      if( data.getParameters().getString( "txtChar" )!=null && data.getParameters().getString( "txtChar" ).length()>0 ) {
     		sName = data.getParameters().getString( "txtChar" );
        urlLink.addQueryData( "txtChar", sName );
        urlBack.addQueryData( "txtChar", sName );
        urlNext.addQueryData( "txtChar", sName );
        context.put( "sChar", sName );
      }

      if( data.getParameters().getString( "cmb_district" )!=null && data.getParameters().getString( "cmb_district" ).length()>0 ) {
      	sDistrict = data.getParameters().getString( "cmb_district" );
        urlLink.addQueryData( "cmb_district", sDistrict );
        urlBack.addQueryData( "cmb_district", sDistrict );
        urlNext.addQueryData( "cmb_district", sDistrict );
        context.put( "sSelectedDistrict", sDistrict );
      }

      if( data.getParameters().getString( "urlNumber" )!=null )
      	m_nNumberOfRecods = data.getParameters().getInt( "urlNumber" );

      if( data.getParameters().getString( "urlFirst" )!=null )
      	m_nFirstRecord = data.getParameters().getInt( "urlFirst" );

     	/* debug
      System.out.println( "cmb_type: " + sTypeId );
      System.out.println( "cmb_style: " + sStyleId );
      System.out.println( "name: " + sName );
      System.out.println( "cmb_district: " + sDistrict );
      */

      //event caching only if search param sKeyword and sStyleId is not set
      //and if there is no forward/backward
      if( sStyleId.equals( "-1" ) &&
          sName.equals( "-1" ) &&
          sDistrict.equals( "-1" ) &&
          data.getParameters().getString( "urlFirst" )==null ) {
        try {
          vecLocationKeywords = (Vector) cacheService.getObject( "locations" + sTypeId + new Integer(nEditionId).toString() ).getContents();
        } catch( ObjectExpiredException oee ) {
          try {
            vecLocationKeywords = getLocations( sTypeId, sStyleId, sName, sDistrict, nEditionId );
          } catch( Exception e ) {
            Log.error("ShowLocations, doBuildTemplate, error while selecting locations: " + e.getMessage());
          }
          //add locations to the cache if the is only one resul page
          if( m_nNumberOfLocations <= vecLocationKeywords.size() )
            cacheService.addObject( "locations" + sTypeId + new Integer(nEditionId).toString(),
                                    new CachedObject( vecLocationKeywords, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

        }
      }
      else {
        try {
          vecLocationKeywords = getLocations( sTypeId, sStyleId, sName, sDistrict, nEditionId );
        } catch( Exception e ) {
          Log.error("ShowLocations, doBuildTemplate, error while selecting locations: " + e.getMessage());
        }
      }

      //set value for back/next toolbar
      Vector vecSteps = new Vector();
      int nCounter=1;
      for( int i=1; i<=m_nNumberOfLocations; i=i+m_nNumberOfRecods ) {
        vecSteps.add( new Integer(nCounter) );
        nCounter++;

      }
      context.put( "vecSteps", vecSteps );
      context.put( "nNumber", new Integer(m_nNumberOfRecods) );
      context.put( "nCurr", new Integer(m_nFirstRecord) );

      if( m_nNumberOfLocations > (m_nFirstRecord+m_nNumberOfRecods) )
        nNext = m_nFirstRecord + m_nNumberOfRecods;

      if( 0 < (m_nFirstRecord-m_nNumberOfRecods) )
        nLast = m_nFirstRecord-m_nNumberOfRecods;
      else {
        if( -m_nNumberOfRecods < (m_nFirstRecord-m_nNumberOfRecods) )
          nLast = 0;
      }

    	context.put( "vecLocationKeywords", vecLocationKeywords );
      context.put( "urlLink", urlLink );
      if( nNext!=-1 ) {
        urlNext.addQueryData( "urlFirst", nNext );
        context.put( "urlNext", urlNext );
      }
      if( nLast!=-1 ) {
        urlBack.addQueryData( "urlFirst", nLast );
        context.put( "urlBack", urlBack );
      }
      context.put( "sLangId", sLangId );
    }

    /**
     * returns a vector of LocationKeywordWrapper for the given search params
     * and the edition with p_nEditionId
     * the search params can be null
     *
     * @param p_sTypeId the type of the location
     * @param p_sStyleId the style of the location
     * @param p_sName the first character of the location name
     * @param p_sDistrict the district of the location
     * @param p_nEditionId
     */
    private Vector getLocations( String p_sTypeId,
                                 String p_sStyleId,
                                 String p_sName,
                                 String p_sDistrict,
                                 int p_nEditionId ) {
      Vector vecLocations = new Vector();
      Vector vecLocationKeywords = new Vector();
      Vector vecKeywords = new Vector();
      Location currLocation;
      int nForeignId;
      String sQuery=new String();

      //build query
      Criteria crit = new Criteria();
      crit.setDistinct();

      //join tables
     	crit.addJoin( SubjectPeer.FOREIGN_ID, LocationPeer.LOCATION_ID );
      crit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );
      crit.add( EditionSubjectPeer.EDITION_ID, new int[]{p_nEditionId,666}, Criteria.IN );

      crit.addAscendingOrderByColumn( LocationPeer.NAME );

      if( !p_sTypeId.equals("-1") || !p_sStyleId.equals("-1") ) {
      	crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
      }

      //style and type
      if( !p_sTypeId.equals("-1") ) {
      	crit.add( SubjectKeywordPeer.KEYWORD_ID, p_sTypeId );
      	if( !p_sStyleId.equals("-1") ) {
        	crit.or( SubjectKeywordPeer.KEYWORD_ID, p_sStyleId );
				}
      }
      else {
      	if( !p_sStyleId.equals("-1") ) {
        	crit.add( SubjectKeywordPeer.KEYWORD_ID, p_sStyleId );
      	}
      }

      //the name
      if( !p_sName.equals("-1") ) {
        if( p_sName.equals("0") ) {
          crit.or( LocationPeer.NAME, (Object)new String("0%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("1%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("2%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("3%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("4%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("5%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("6%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("7%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("8%"), Criteria.LIKE );
          crit.or( LocationPeer.NAME, (Object)new String("9%"), Criteria.LIKE );
        }
        else {
          if( p_sName.equals("x") ) {
      	    crit.or( LocationPeer.NAME, (Object)new String("x%"), Criteria.LIKE );
            crit.or( LocationPeer.NAME, (Object)new String("y%"), Criteria.LIKE );
            crit.or( LocationPeer.NAME, (Object)new String("z%"), Criteria.LIKE );
          }
          else
            crit.add( LocationPeer.NAME, (Object)(p_sName + "%"), Criteria.LIKE );
        }
      }

      //district
      if( !p_sDistrict.equals("-1") ) {
        crit.addJoin( LocationPeer.ADDRESS_ID, AddressPeer.ADDRESS_ID );
       	crit.add( AddressPeer.DISTRICT, p_sDistrict );
      }

      try {
      	vecLocations = LocationPeer.doSelect( crit );
      	//System.out.println( crit.toString() );
      } catch( Exception e ) {
        Log.error("ShowLocations, doBuildTemplate, error while selecting locations: " + e.getMessage());
      }

      m_nNumberOfLocations = vecLocations.size();

      Vector tempLocations = new Vector();
      if( m_nFirstRecord<m_nNumberOfLocations ) {
        for( int i=m_nFirstRecord; i<m_nFirstRecord+m_nNumberOfRecods && i<m_nNumberOfLocations; i++ ) {
          tempLocations.add( vecLocations.get(i) );
        }
        vecLocations=tempLocations;
      }

    	//selects types and style for every location
    	for( int i=0; i<vecLocations.size(); i++ ) {
        crit = new Criteria();
    		currLocation=(Location)vecLocations.get(i);
    		nForeignId=new Integer(currLocation.getLocationId().toString()).intValue();
        //types and styles
        crit.add( SubjectPeer.FOREIGN_ID, nForeignId );
        crit.add( SubjectPeer.TYPE_ID, "2" );
    		crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
    		crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
    		try {
    			vecLocationKeywords.add( new LocationKeywordWrapper( currLocation, new Vector(), KeywordPeer.doSelect( crit ), null ) );
    		} catch( Exception e ) {
        	Log.error("ShowLocations, doBuildTemplate, error while selecting keywords: " + e.getMessage());
      	}
    	}
      return vecLocationKeywords;
    }
}

