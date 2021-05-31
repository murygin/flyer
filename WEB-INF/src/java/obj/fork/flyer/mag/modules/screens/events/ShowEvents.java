package obj.fork.flyer.mag.modules.screens.events;

// jdk
import java.util.Vector;
import java.lang.ClassCastException;

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
import obj.fork.laika.om.Artist;
import obj.fork.laika.om.ArtistPeer;
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
 * class ShowEvents provides data for template /screens/events/ShowEvents.vm
 * ShowEvents displays events
 * its gets several parameter of /navigations/events/SelectForm.vm
 * all values are post values of the select form
 *
 * @cmb_type the txpe of the event, a keyword_id value of table keyword
 * @cmb_style the style of the event, a keyword_id value of table keyword
 * @txtKeyword a free set text, a name value of table keyword
 * @cmdDate the date of the event
 */
public class ShowEvents extends WeakScreen {

    //the number of displayed records displaed on one page
    private int m_nNumberOfRecods=15;

    //the fist record to display
    int m_nFirstRecord=0;

    //total number of events
    private int m_nNumberOfEvents;

    public void doBuildTemplate( RunData data, Context context ) {
      //the edition id
      int nEditionId=-1;
      String sLangId = null;
      //search parameters
      String sTypeId="-1";
      String sStyleId="-1";
      String sDate="-1";
      String sKeyword="-1";

      String sQuery=new String();

      Vector vecEventKeywords = null;

      //setting up the cache service
      GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

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

    	if( data.getParameters().getString( "cmb_type" )!=null ) {
    		sTypeId = data.getParameters().getString( "cmb_type" );
        sQuery= sQuery + "&cmb_type=" + sTypeId;
        context.put( "sSelectedType", sTypeId );
    	}

    	if( data.getParameters().getString( "cmb_style" )!=null ) {
    		sStyleId = data.getParameters().getString( "cmb_style" );
        sQuery= sQuery + "&cmb_style=" + sStyleId;
        context.put( "sSelectedStyle", sStyleId );
      }

      if( data.getParameters().getString( "cmbDate" )!=null )
        sDate = data.getParameters().getString( "cmbDate" );
      else
        sDate = UtilFunctions.getCurrentMySQLDate();

      sQuery= sQuery + "&cmbDate=" + sDate;
      context.put( "sSelectedDate", sDate );

      if( data.getParameters().getString( "txtKeyword" )!=null &&
          data.getParameters().getString( "txtKeyword" ).length()>0 ) {
        sKeyword = data.getParameters().getString( "txtKeyword" );
        sQuery= sQuery + "&txtKeyword=" + sKeyword;
        context.put( "sKeyword", sKeyword );
      }
      if( data.getParameters().getString( "urlNumber" )!=null )
      	m_nNumberOfRecods = data.getParameters().getInt( "urlNumber" );

      if( data.getParameters().getString( "urlFirst" )!=null )
      	m_nFirstRecord = data.getParameters().getInt( "urlFirst" );

      //event caching only if search param sKeyword and sStyleId is not set
      //and if there is no forward/backward
      if( sKeyword.equals( "-1" ) &&
          sStyleId.equals( "-1" ) &&
          data.getParameters().getString( "urlFirst" )==null ) {
        try {
          vecEventKeywords = (Vector) cacheService.getObject( "events" + sDate + sTypeId + new Integer(nEditionId).toString() ).getContents();
        } catch( ObjectExpiredException oee ) {
          try {
            vecEventKeywords = getEvents( sTypeId, sStyleId, sDate, sKeyword, nEditionId );
          } catch( Exception e ) {
            Log.error("ShowEvents, doBuildTemplate, error while selecting events: " + e.getMessage());
          }
          //add events to the cache if the is only one resul page
          if( m_nNumberOfEvents <= vecEventKeywords.size() )
            cacheService.addObject( "events" + sDate + sTypeId + new Integer(nEditionId).toString(),
                                    new CachedObject( vecEventKeywords, MagConstants.N_EVENT_CACHING_TIMEOUT ) );

        }
      }
      else {
        try {
          vecEventKeywords = getEvents( sTypeId, sStyleId, sDate, sKeyword, nEditionId );
        } catch( Exception e ) {
          Log.error("ShowEvents, doBuildTemplate, error while selecting events: " + e.getMessage());
        }
      }


      //set value for back/next toolbar
      Vector vecSteps = new Vector();
      int nCounter=1;
      for( int i=1; i<=m_nNumberOfEvents; i=i+m_nNumberOfRecods ) {
        vecSteps.add( new Integer(nCounter) );
        nCounter++;
      }
      context.put( "vecSteps", vecSteps );
      context.put( "nNumber", new Integer(m_nNumberOfRecods) );
      context.put( "nCurr", new Integer(m_nFirstRecord) );

      if( m_nNumberOfEvents > (m_nFirstRecord+m_nNumberOfRecods) )
        context.put( "nNext", new Integer( m_nFirstRecord + m_nNumberOfRecods ).toString() );
      else
        context.put( "nNext", "-1" );

      if( 0 < (m_nFirstRecord-m_nNumberOfRecods) )
        context.put( "nLast", new Integer( m_nFirstRecord-m_nNumberOfRecods ).toString() );
      else {
        if( -m_nNumberOfRecods < (m_nFirstRecord-m_nNumberOfRecods) )
          context.put( "nLast", "0" );
        else
          context.put( "nLast", "-1" );
      }

      context.put( "sLangId", sLangId );
    	context.put( "vecEventKeywords", vecEventKeywords );
      context.put( "sSearch", sQuery );
    }


    /**
     * returns a vector of EventKeywordWrappern for the given search params
     * the search params can be null accept of p_sDate
     *
     * @praram p_sType the type of the events
     * @param p_sStyle the style of the events
     * @param p_sDate the date of the events in the mysql format yyyy-mm-dd
     * @param p_sKeyword a free keyword or the beginning of the keyword
     */
    private Vector getEvents( String p_sTypeId,
                              String p_sStyleId,
                              String p_sDate,
                              String p_sKeyword,
                              int p_nEditionId )
      throws Exception {

      Vector vecSubjects = new Vector();
		  Vector vecEventKeywords = new Vector();
		  Event currEvent;
      Subject currSubject;
      Location currLocation = null;
  	  int nForeignId;

      //build query
      Criteria crit = new Criteria();
      Criteria.Criterion criton=null;

      crit.setDistinct();
      crit.addAscendingOrderByColumn( EventPeer.BEGIN );
      crit.addAscendingOrderByColumn( EventPeer.NAME );

      //join tables
     	crit.addJoin( SubjectPeer.FOREIGN_ID, EventPeer.EVENT_ID );
      crit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );

      crit.add( EditionSubjectPeer.EDITION_ID, new int[]{p_nEditionId,666}, Criteria.IN );
      crit.add( SubjectPeer.TYPE_ID, 5 );

      if( !p_sTypeId.equals("-1") || !p_sTypeId.equals("-1") || !p_sKeyword.equals("-1") ) {
      	crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
        crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
      }

      //build where statement
      if( !p_sTypeId.equals("-1") ) {
        if( criton==null )
          criton = crit.getNewCriterion( SubjectKeywordPeer.KEYWORD_ID, p_sTypeId, Criteria.EQUAL );
        else
      	  criton.or( crit.getNewCriterion( SubjectKeywordPeer.KEYWORD_ID, p_sTypeId, Criteria.EQUAL ) );
      }
      if( !p_sStyleId.equals("-1") ) {
        if( criton==null )
          criton = crit.getNewCriterion( SubjectKeywordPeer.KEYWORD_ID, p_sStyleId, Criteria.EQUAL );
        else
          criton.or( crit.getNewCriterion( SubjectKeywordPeer.KEYWORD_ID, p_sStyleId, Criteria.EQUAL ) );
      }
      if( !p_sKeyword.equals("-1") ) {
        if( criton==null )
          criton = crit.getNewCriterion( KeywordPeer.NAME, p_sKeyword + "%", Criteria.LIKE );
        else
          criton.or( crit.getNewCriterion( KeywordPeer.NAME, p_sKeyword + "%", Criteria.LIKE ) );
      }
      if( !p_sDate.equals("-1") ) {
        crit.add( EventPeer.BEGIN, p_sDate );
      }
      else {
        if( criton==null ) {
          criton = crit.getNewCriterion( EventPeer.BEGIN,
                                         (Object)UtilFunctions.getCurrentMySQLDate( System.currentTimeMillis() + MagConstants.N_NUMBER_OF_DAYS*24*60*60*1000 ),
                                         Criteria.LESS_EQUAL );
        }
        else {
          criton.and( crit.getNewCriterion( EventPeer.BEGIN,
                                            (Object)UtilFunctions.getCurrentMySQLDate( System.currentTimeMillis() + MagConstants.N_NUMBER_OF_DAYS*24*60*60*1000 ),
                                            Criteria.LESS_EQUAL ) );
        }
        criton.and( crit.getNewCriterion( EventPeer.BEGIN,
                                          (Object)UtilFunctions.getCurrentMySQLDate(),
                                          Criteria.GREATER_EQUAL ) );
      }

      //search for locations
      if( !p_sKeyword.equals("-1") ) {
        Vector vecArticleSubject = new Vector();
        Criteria subjectCrit = new Criteria();
        Criteria.Criterion locationCritonName=null;
        Criteria.Criterion locationCritonId=null;
        Criteria.Criterion artistCritonName=null;
        Criteria.Criterion artistCritonId=null;

        //joins
        subjectCrit.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );
        subjectCrit.addJoin( LocationPeer.LOCATION_ID, SubjectPeer.FOREIGN_ID );
        subjectCrit.addJoin( ArtistPeer.ARTIST_ID, SubjectPeer.FOREIGN_ID );
        subjectCrit.addJoin( SubjectPeer.SUBJECT_ID, ArticleSubjectPeer.SUBJECT_ID );
        //allgemein
        subjectCrit.add( EditionSubjectPeer.EDITION_ID, new int[]{p_nEditionId,666}, Criteria.IN );

        //location
        locationCritonName = subjectCrit.getNewCriterion( LocationPeer.NAME, (Object)(p_sKeyword + "%"), Criteria.LIKE );
        locationCritonId = subjectCrit.getNewCriterion( SubjectPeer.TYPE_ID, new Integer(2), Criteria.EQUAL );

        //artist
        artistCritonName = subjectCrit.getNewCriterion( ArtistPeer.NAME, (Object)(p_sKeyword + "%"), Criteria.LIKE );
        artistCritonId = subjectCrit.getNewCriterion( SubjectPeer.TYPE_ID, new Integer(1), Criteria.EQUAL );

        //subjectCrit.add( locationCritonName.and(locationCritonId).or(artistCritonName.and(artistCritonId) ) );
        subjectCrit.add( locationCritonName.and(locationCritonId) );
        vecArticleSubject = ArticleSubjectPeer.doSelect( subjectCrit );

        //join selected locations with table article
        if( vecArticleSubject.size()>0 ) {
          crit.addJoin( ArticleSubjectPeer.SUBJECT_ID, SubjectPeer.SUBJECT_ID );
          for( int i=0; i<vecArticleSubject.size(); i++ ) {
            int nArticleId=((ArticleSubject)vecArticleSubject.get(i)).getArticleId().getBigDecimal().intValue();
            criton.or( crit.getNewCriterion( ArticleSubjectPeer.ARTICLE_ID, new Integer(nArticleId), Criteria.EQUAL ) );
          }
        }
      }

      if( criton!=null )
        crit.add( criton );

      //select events
      vecSubjects = SubjectPeer.doSelect( crit );

      m_nNumberOfEvents = vecSubjects.size();

      //copy values from resultset to displayed resultset
      Vector tempEvents = new Vector();
      if( m_nFirstRecord<vecSubjects.size() ) {
        for( int i=m_nFirstRecord; i<m_nFirstRecord+m_nNumberOfRecods && i<vecSubjects.size(); i++ ) {
          tempEvents.add( vecSubjects.get(i) );
        }
        vecSubjects=tempEvents;
      }

    	//select location, types and styles of the event
    	for( int i=0; i<vecSubjects.size(); i++ ) {
        crit = new Criteria();

        currSubject = (Subject)vecSubjects.get(i);
        currEvent=(Event) currSubject.getConcrete();

        //select location
        Vector vecRelatedSubjects;
        if( currSubject.getArticleSubjects().size()>0 ) {
          int nArticleId = ((ArticleSubject)currSubject.getArticleSubjects().get(0)).getArticleId().getBigDecimal().intValue();
          Criteria locationCrit = new Criteria();
          locationCrit.addJoin( ArticleSubjectPeer.SUBJECT_ID, SubjectPeer.SUBJECT_ID );
          locationCrit.add( SubjectPeer.TYPE_ID, "2" );
          locationCrit.add( ArticleSubjectPeer.ARTICLE_ID, nArticleId );
          vecRelatedSubjects = ArticleSubjectPeer.doSelect( locationCrit );
          for( int k=0; k<vecRelatedSubjects.size(); k++ ) {
            if( ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getTypeId().toString().equals("2") ) {
              currLocation = (Location) ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getConcrete();
              break;
            }
          }
        }
        //types and styles
        nForeignId=new Integer(currEvent.getEventId().toString()).intValue();
        crit.add( SubjectPeer.FOREIGN_ID, nForeignId );
        crit.add( SubjectPeer.TYPE_ID, "5" );
        crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
        crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
        vecEventKeywords.add( new EventKeywordWrapper( currEvent, currLocation, KeywordPeer.doSelect( crit ) ) );
    	}
      return vecEventKeywords;
    } //end getEvents
}//end of class ShowEvents

