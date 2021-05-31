package obj.fork.flyer.mag.modules.screens.locations;

// jdk
import java.util.Vector;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.Log;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;


// jsdk
import javax.servlet.http.HttpSession;

// laika
import obj.fork.laika.om.Issue;
import obj.fork.laika.om.Location;
import obj.fork.laika.om.LocationPeer;
import obj.fork.laika.om.SubjectPeer;
import obj.fork.laika.om.SubjectKeywordPeer;
import obj.fork.laika.om.EditionSubjectPeer;
import obj.fork.laika.om.SubjecttypePeer;
import obj.fork.laika.om.SubjecttypeKeywordPeer;
import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.om.AddressPeer;
import obj.fork.laika.om.Edition;
import obj.fork.laika.om.Issue;
import obj.fork.laika.util.Selectable;

//mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.om.LocationKeywordWrapper;
import obj.fork.flyer.mag.MagConstants;


/**
 * class Start provides values for the startpage of locations
 * it selects a number of new locations depending on some parameter
 * @param cmb_type the type of the locations
 * @param cmb_style the style of the location
 * @param txtChar the name of the location or the beginning of the name
 * @param cmb_district the district of the location
 * @param edition_id the edition id
 *
 * sets the total number of locations group by different types
 *
 * nNumberOfDisplayedLocations the number of selected locations
 *
 * @author <a href="mailto:schmidt@fork.de>fork ost, daniel schmidt</a>
 */
public class LocationIndex extends WeakScreen  {

  //search parameters
  private String m_sTypeId="-1";
  private String m_sStyleId="-1";
  private String m_sName="-1";
  private String m_sDistrict="-1";

  //the number of new locations to display
  private int m_nNumberOfDisplayedLocations = 5;


  public void doBuildTemplate( RunData data, Context context ) {
    Vector vecLocationKeywords=null;

    //the edition id
    int nEditionId=-1;
    String sLangId = null;

    //setting up the cache service
    GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

    //read parameters
    try {
      Edition activeEdition = (Edition)data.getSession().getAttribute("activeEdition");
      nEditionId = Integer.parseInt( activeEdition.getEditionId().toString()  );
    } catch(Exception e) {
      Log.error( "An error occured retrieving edition from session: " + e.getMessage() );
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

    try {
      try {
        vecLocationKeywords = (Vector) cacheService.getObject( "newLocations" + new Integer(nEditionId).toString()  ).getContents();
      } catch( ObjectExpiredException oee ) {
        vecLocationKeywords = getNewLocations( 5, nEditionId );
        cacheService.addObject( "newLocations" + new Integer(nEditionId).toString(),
                                new CachedObject( vecLocationKeywords, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

      }
    } catch( Exception e ) {
      Log.error( "ShowLocations, doBuildTemplate, error while selecting keywords: " + e.getMessage() );
    }

    context.put( "vecLocationKeywords", vecLocationKeywords );

	  //set the number of locations
    Integer nVenues, nClubs, nCinemas, nMuseum, nStage, nFood, nShops;
    try {
      nVenues = (Integer) cacheService.getObject( "numberOfVenues" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nVenues = getNumberOfLocation( nEditionId, MagConstants.AS_VENUES_KEYWORDS );
      cacheService.addObject( "numberOfVenues" + new Integer(nEditionId).toString(),
                              new CachedObject( nVenues, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    try {
      nClubs = (Integer) cacheService.getObject( "numberOfClubs" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nClubs = getNumberOfLocation( nEditionId, MagConstants.AS_CLUBS_BARS_KEYWORDS );
      cacheService.addObject( "numberOfClubs" + new Integer(nEditionId).toString(),
                              new CachedObject( nClubs, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    try {
      nCinemas = (Integer) cacheService.getObject( "numberOfCinemas" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nCinemas = getNumberOfLocation( nEditionId, MagConstants.AS_CINEMAS_KEYWORDS );
      cacheService.addObject( "numberOfCinemas" + new Integer(nEditionId).toString(),
                              new CachedObject( nCinemas, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    try {
      nStage = (Integer) cacheService.getObject( "numberOfStage" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nStage = getNumberOfLocation( nEditionId, MagConstants.AS_STAGE_KEYWORDS );
      cacheService.addObject( "numberOfStage" + new Integer(nEditionId).toString(),
                              new CachedObject( nStage, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    try {
      nMuseum = (Integer) cacheService.getObject( "numberOfMuseum" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nMuseum = getNumberOfLocation( nEditionId, MagConstants.AS_MUSEUM_KEYWORDS );
      cacheService.addObject( "numberOfMuseum" + new Integer(nEditionId).toString(),
                              new CachedObject( nMuseum, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    try {
      nFood = (Integer) cacheService.getObject( "numberOfFood" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nFood = getNumberOfLocation( nEditionId, MagConstants.AS_FOOD_KEYWORDS );
      cacheService.addObject( "numberOfFood" + new Integer(nEditionId).toString(),
                              new CachedObject( nFood, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    try {
      nShops = (Integer) cacheService.getObject( "numberOfShops" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      nShops = getNumberOfLocation( nEditionId, MagConstants.AS_SHOPPING_KEYWORDS );
      cacheService.addObject( "numberOfShops" + new Integer(nEditionId).toString(),
                              new CachedObject( nShops, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }

    context.put( "sLangId", sLangId );
    context.put( "nNumberOfVenues", nVenues );
    context.put( "nNumberOfClubs", nClubs );
    context.put( "nNumberOfCinemas", nCinemas );
    context.put( "nNumberOfTheatres", nStage );
    context.put( "nNumberOfMuseums", nMuseum );
    context.put( "nNumberOfRestaurants", nFood );
    context.put( "nNumberOfShops", nShops );
  }

  /**
   * returns the number of locations specified by
   * array p_asKeywords
   *
   * @param p_asKeywords
   */
  private Integer getNumberOfLocation( int p_nEditionId, String[] p_asKeywords )  {
    //build query for number of venue
    Criteria crit = new Criteria();
    Vector vecNumberResults = new Vector();

    crit.addJoin( SubjectPeer.FOREIGN_ID, LocationPeer.LOCATION_ID );
	  crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
    crit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );

    crit.add( EditionSubjectPeer.EDITION_ID, new int[]{p_nEditionId,666}, Criteria.IN );

	  for( int i=0; i<p_asKeywords.length; i++ ) {
	    crit.add( SubjectKeywordPeer.KEYWORD_ID, p_asKeywords[i] );
	  }

	  if( !m_sName.equals("-1") ) {
	    crit.add( LocationPeer.NAME, (Object)(m_sName + "%"), Criteria.LIKE );
	  }

	  if( !m_sDistrict.equals("-1") ) {
	    crit.addJoin( LocationPeer.ADDRESS_ID, AddressPeer.ADDRESS_ID );
	    crit.add( AddressPeer.DISTRICT, m_sDistrict );
	  }

	  try {
	    vecNumberResults = LocationPeer.doSelect( crit );
    } catch( Exception e ) {
	    Log.error( "ShowLocations, doBuildTemplate, error while selecting locations: " + e.getMessage() );
	    return null;
	  }

    return new Integer( vecNumberResults.size() );
  }


  /**
   * returns the specified number of new location for a edition
   *
   * @param p_nNumberOfLocations the number of new selected locations
   * @param p_nEditionId the edition id
   */
  private Vector getNewLocations( int p_nNumberOfLocations, int p_nEditionId )
    throws Exception {
    Vector vecLocations = new Vector();
    Vector vecNumberResults = new Vector();
    Vector vecLocationKeywords = new Vector();
    Location currLocation;
    int nForeignId;

    //build query for new locations
    Criteria crit = new Criteria();

    crit.setDistinct();
    crit.addDescendingOrderByColumn( LocationPeer.DATE_MOD );
    crit.addAscendingOrderByColumn( LocationPeer.NAME );
    crit.setLimit( m_nNumberOfDisplayedLocations );

    //join tables
    crit.addJoin( SubjectPeer.FOREIGN_ID, LocationPeer.LOCATION_ID );
    crit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );

    crit.add( SubjectPeer.TYPE_ID, 2 );
    crit.add( EditionSubjectPeer.EDITION_ID, new int[]{p_nEditionId,666}, Criteria.IN );

    //select location
    try {
      vecLocations = LocationPeer.doSelect( crit );
    } catch( Exception e ) {
	    Log.error( "ShowLocations, doBuildTemplate, error while selecting locations: " + e.getMessage() );
    }

    //select the types and styles of the location
    crit = new Criteria();
    for( int i=0; i<vecLocations.size(); i++ ) {
      currLocation=(Location)vecLocations.get(i);
      nForeignId=new Integer(currLocation.getLocationId().toString()).intValue();
      crit.add( SubjectPeer.FOREIGN_ID, nForeignId );
      crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
      crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );

      vecLocationKeywords.add( new LocationKeywordWrapper( currLocation, new Vector(), KeywordPeer.doSelect( crit ), null ) );
    }
    return vecLocationKeywords;
  }

}

