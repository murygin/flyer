package obj.fork.flyer.mag.modules.screens.events;

// jdk
import java.util.Vector;
import java.sql.Date;

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
import obj.fork.laika.om.Edition;
import obj.fork.laika.om.Location;
import obj.fork.laika.om.LocationPeer;
import obj.fork.laika.om.Event;
import obj.fork.laika.om.EventPeer;
import obj.fork.laika.om.ArticlePeer;
import obj.fork.laika.om.ArticleSubject;
import obj.fork.laika.om.ArticleSubjectPeer;
import obj.fork.laika.om.Subject;
import obj.fork.laika.om.SubjectPeer;
import obj.fork.laika.om.SubjectKeywordPeer;
import obj.fork.laika.om.SubjecttypePeer;
import obj.fork.laika.om.SubjecttypeKeywordPeer;
import obj.fork.laika.om.EditionSubjectPeer;
import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.util.Selectable;

//mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.om.EventKeywordWrapper;
import obj.fork.flyer.mag.MagConstants;
import obj.fork.flyer.mag.util.UtilFunctions;


/**
 * class StartEvents provides values for the startpage of events
 * it select 5/3 events of the types nightlife, excibition, concert,
 * theatre and different for one day
 * you must specify this day by param urlDate (yyyy-mm-dd)
 * if its not specified the current date is used
 *
 * @param urlDate the date of the events
 * @param edition_id the edition id
 *
 * @author <a href="mailto:schmidt@fork.de>fork ost, daniel schmidt</a>
 */
public class EventIndex extends WeakScreen {

    private int m_nEditionId;

    Vector vecLocations;
		Vector vecEvetns;
		Location currLocation;
  	int nForeignId;

    private String m_sDate="-1";
		private String m_sKeyword="-1";

    public void doBuildTemplate( RunData data, Context context ) {
      vecLocations = new Vector();
      vecEvetns = new Vector();
      String sLangId = null;

      Vector vecWrapper=null;

      //setting up the cache service
      GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

      //read parameters
    	if( data.getSession().getAttribute( "activeEdition" )!=null )
        m_nEditionId = ((Edition)data.getSession().getAttribute( "activeEdition" )).getEditionId().getBigDecimal().intValue();
      else {
        Log.error( "EventIndex, doPerform, no edition found in session. Stop excecuting." );
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
        Log.error( "EventIndex, doPerform, no issue found in session. Stop excecuting." );
        return;
      }

      if( data.getParameters().getString( "urlDate" )!=null ) {
        m_sDate = data.getParameters().getString( "urlDate" );
      }
      else {
      	m_sDate = UtilFunctions.getCurrentMySQLDate();
      }

      if( data.getParameters().getString( "txtKeyword" )!=null &&
          data.getParameters().getString( "txtKeyword" ).length()>0 ) {
        m_sKeyword = data.getParameters().getString( "txtKeyword" );
      }

      //select events with article
      try {
        vecWrapper = (Vector) cacheService.getObject( "articleEvents" + new Integer(m_nEditionId).toString() + m_sDate ).getContents();
      } catch( ObjectExpiredException oee ) {
        try {
          vecWrapper = getArticleEvents();
        } catch( Exception e ) {
           Log.error("EventIndex, doBuildTemplate, error while selecting flyer presents events: " + e.getMessage());
        }
        cacheService.addObject( "articleEvents" + new Integer(m_nEditionId).toString() + m_sDate,
                                new CachedObject( vecWrapper, MagConstants.N_EVENT_CACHING_TIMEOUT ) );

      }

      context.put( "vecFlyerPresents", vecWrapper );

      //sets events of the current date to the context
      Vector vecNightlife, vecConcert, vecTheatre, vecExhibition, vecDifferent;
      try {
        vecNightlife = (Vector) cacheService.getObject( "nightlife" + m_sDate + new Integer(m_nEditionId).toString() ).getContents();
      } catch( ObjectExpiredException oee ) {
        vecNightlife = getEvents( 5, MagConstants.AS_NIGHTLIFE_KEYWORDS, m_sDate );
        cacheService.addObject( "nightlife" + m_sDate + new Integer(m_nEditionId).toString(),
                                new CachedObject( vecNightlife, MagConstants.N_EVENT_CACHING_TIMEOUT ) );
      }
      try {
        vecConcert = (Vector) cacheService.getObject( "concert" + m_sDate + new Integer(m_nEditionId).toString() ).getContents();
      } catch( ObjectExpiredException oee ) {
        vecConcert = getEvents( 3, MagConstants.AS_CONCERT_KEYWORDS, m_sDate );
        cacheService.addObject( "concert" + m_sDate + new Integer(m_nEditionId).toString(),
                                new CachedObject( vecConcert, MagConstants.N_EVENT_CACHING_TIMEOUT ) );
      }
      try {
        vecTheatre = (Vector) cacheService.getObject( "theatre" + m_sDate + new Integer(m_nEditionId).toString() ).getContents();
      } catch( ObjectExpiredException oee ) {
        vecTheatre = getEvents( 3, MagConstants.AS_THEATRE_KEYWORDS, m_sDate );
        cacheService.addObject( "theatre" + m_sDate + new Integer(m_nEditionId).toString(),
                                new CachedObject( vecTheatre, MagConstants.N_EVENT_CACHING_TIMEOUT ) );
      }
      try {
        vecExhibition = (Vector) cacheService.getObject( "exhibition" + m_sDate + new Integer(m_nEditionId).toString() ).getContents();
      } catch( ObjectExpiredException oee ) {
        vecExhibition = getEvents( 3, MagConstants.AS_EXHIBITION_KEYWORDS, m_sDate );
        cacheService.addObject( "exhibition" + m_sDate + new Integer(m_nEditionId).toString(),
                                new CachedObject( vecExhibition, MagConstants.N_EVENT_CACHING_TIMEOUT ) );
      }
      try {
        vecDifferent = (Vector) cacheService.getObject( "different" + m_sDate + new Integer(m_nEditionId).toString() ).getContents();
      } catch( ObjectExpiredException oee ) {
        vecDifferent = getEvents( 3, MagConstants.AS_MISC_KEYWORDS, m_sDate );
        cacheService.addObject( "different" + m_sDate + new Integer(m_nEditionId).toString(),
                                new CachedObject( vecDifferent, MagConstants.N_EVENT_CACHING_TIMEOUT ) );
      }

      context.put( "sLangId", sLangId );
      context.put( "vecNightlife", vecNightlife );
      context.put( "vecConcert", vecConcert );
      context.put( "vecTheatre", vecTheatre );
      context.put( "vecExcibition", vecExhibition );
      context.put( "vecDifferent", vecDifferent );
    }


    /**
     * returns a vector of events with an article as description for an edition
     */
    private Vector getArticleEvents()
      throws Exception {
      Vector vecWrapper=new Vector();
      Vector vecSubjects = new Vector();
      Subject currSubject=null;
      Event currEvent=null;

      //select events with article
      Criteria crit = new Criteria();
      crit.addAscendingOrderByColumn( EventPeer.BEGIN );

      crit.setLimit( MagConstants.N_NUMBER_OF_FLYER_PRESENTS );
      crit.addJoin( EventPeer.EVENT_ID, SubjectPeer.FOREIGN_ID );
      crit.addJoin( SubjectPeer.SUBJECT_ID, ArticleSubjectPeer.SUBJECT_ID  );
      crit.addJoin( ArticleSubjectPeer.ARTICLE_ID, ArticlePeer.ARTICLE_ID );
      crit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );

      crit.add( EventPeer.BEGIN, (Object) m_sDate, Criteria.GREATER_EQUAL );
      crit.add( SubjectPeer.TYPE_ID, 5 );
      crit.add( ArticlePeer.SYSTEM_ID, (Object) new String("/dev/null.xml"), Criteria.NOT_EQUAL );
      crit.add( EditionSubjectPeer.EDITION_ID, new int[]{m_nEditionId,666}, Criteria.IN );

      vecSubjects = SubjectPeer.doSelect( crit );

      //select location, types and styles of the event
      for( int i=0; i<vecSubjects.size(); i++ ) {
        crit = new Criteria();

        currSubject = (Subject)vecSubjects.get(i);
        currEvent=(Event) currSubject.getConcrete();

        //select location
        Vector vecRelatedSubjects;
        int nArticleId = ((ArticleSubject)currSubject.getArticleSubjects().get(0)).getArticleId().getBigDecimal().intValue();
        Criteria locationCrit = new Criteria();
        locationCrit.add( ArticleSubjectPeer.ARTICLE_ID, nArticleId );
        vecRelatedSubjects = ArticleSubjectPeer.doSelect( locationCrit );
        currLocation = null;
        for( int k=0; k<vecRelatedSubjects.size(); k++ ) {
          if( ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getTypeId().toString().equals("2") ) {
            currLocation = (Location) ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getConcrete();
            break;
          }
        }
        //types and styles
        nForeignId=new Integer(currEvent.getEventId().toString()).intValue();
        crit.add( SubjectPeer.FOREIGN_ID, nForeignId );
        crit.add( SubjectPeer.TYPE_ID, "5" );
        crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
        crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
        vecWrapper.add( new EventKeywordWrapper( currEvent, currLocation, KeywordPeer.doSelect( crit ) ) );
      }
      return vecWrapper;
    }

    /**
     * returns the specified number of events/location/keywords
     * of the specified event and the specified date
     *
     * @param p_nNumber
     * @param p_sKeyword
     * @param p_dateToday (yyyy-mm-dd)
     * @return a vector of EventKeyword wrappern
     */
    private Vector getEvents( int p_nNumber,
                              String[] p_asKeyword,
                              String p_sToday ) {
      Vector vecWrapper=new Vector();
      Vector vecSubjects=new Vector();
      Subject currSubject=null;
      Event currEvent=null;
      Criteria crit = new Criteria();
      org.apache.turbine.util.db.Criteria.Criterion criton=null;

      crit.setLimit( p_nNumber );
      crit.setDistinct();
      crit.addAscendingOrderByColumn( EventPeer.BEGIN );

      crit.addJoin( SubjectPeer.FOREIGN_ID, EventPeer.EVENT_ID );
      crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
      crit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );

      crit.add( EditionSubjectPeer.EDITION_ID, new int[]{m_nEditionId,666}, Criteria.IN );
      crit.add( SubjectPeer.TYPE_ID, 5 );

      //build where where statement
      criton = crit.getNewCriterion( SubjectKeywordPeer.KEYWORD_ID,  p_asKeyword, Criteria.IN );

      crit.add( criton );
      crit.add( EventPeer.BEGIN, (Object)p_sToday, Criteria.GREATER_EQUAL );

      //select events
      try {
        vecSubjects = SubjectPeer.doSelect( crit );
      } catch( Exception e ) {
        Log.error("StartEvents, doBuildTemplate, error while selecting locations: " + e.getMessage());
      }

      //select location, types and styles of the event
      for( int i=0; i<vecSubjects.size(); i++ ) {
        crit = new Criteria();
        try {
          currSubject = (Subject)vecSubjects.get(i);
          currEvent=(Event) currSubject.getConcrete();

          //select location
          Vector vecRelatedSubjects;
          int nArticleId = ((ArticleSubject)currSubject.getArticleSubjects().get(0)).getArticleId().getBigDecimal().intValue();
          Criteria locationCrit = new Criteria();
          locationCrit.add( ArticleSubjectPeer.ARTICLE_ID, nArticleId );
          vecRelatedSubjects = ArticleSubjectPeer.doSelect( locationCrit );
          for( int k=0; k<vecRelatedSubjects.size(); k++ ) {
            if( ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getTypeId().toString().equals("2") ) {
              currLocation = (Location) ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getConcrete();
              break;
            }
          }
          //types and styles
          nForeignId=new Integer(currEvent.getEventId().toString()).intValue();
          crit.add( SubjectPeer.FOREIGN_ID, nForeignId );
          crit.add( SubjectPeer.TYPE_ID, "5" );
          crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
          crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
          vecWrapper.add( new EventKeywordWrapper( currEvent, currLocation, KeywordPeer.doSelect( crit ) ) );
        } catch( Exception e ) {
          Log.error("ShowEvents, doBuildTemplate, error while selecting keywords and location: " + e.getMessage());
        }
      }
      return vecWrapper;
    } //end getEvents

} //end of class

