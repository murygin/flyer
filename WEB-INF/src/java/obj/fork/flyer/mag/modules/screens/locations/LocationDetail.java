package obj.fork.flyer.mag.modules.screens.locations;

// jdk
import java.util.Vector;
import java.util.Enumeration;
import java.util.Date;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.template.TemplateLink;

// jsdk
import javax.servlet.http.HttpSession;

// laika
import obj.fork.laika.om.Issue;
import obj.fork.laika.om.Memo;
import obj.fork.laika.util.MemoFacade;
import obj.fork.laika.om.Location;
import obj.fork.laika.om.LocationPeer;
import obj.fork.laika.om.Event;
import obj.fork.laika.om.EventPeer;
import obj.fork.laika.om.Subject;
import obj.fork.laika.om.SubjectPeer;
import obj.fork.laika.om.SubjectKeywordPeer;
import obj.fork.laika.om.SubjecttypePeer;
import obj.fork.laika.om.SubjecttypeKeywordPeer;
import obj.fork.laika.om.SubjectMemo;
import obj.fork.laika.om.SubjectMemoPeer;
import obj.fork.laika.om.ArticleSubjectPeer;
import obj.fork.laika.om.ArticleSubject;
import obj.fork.laika.om.MemoPeer;
import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.om.AddressPeer;
import obj.fork.laika.util.Selectable;

//mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.om.LocationKeywordWrapper;
import obj.fork.flyer.mag.MagConstants;
import obj.fork.flyer.mag.util.UtilFunctions;

/**
 * class LocationDetail provides data for template /screens/locations/LocationDetail.vm
 * LocationDetail displays one location
 * location is specified by its id
 *
 * @nLocationId the id of the location
 */
public class LocationDetail extends WeakScreen {

		//search parameters
		private String m_sLocationId="-1";

    public void doBuildTemplate( RunData data, Context context ) {
      Vector vecSubjects = new Vector();
      Vector vecLocationKeywords = new Vector();
      Vector vecKeywords = new Vector();
      Location currLocation;
      Subject currSubject;
      org.apache.turbine.util.db.Criteria.Criterion criton=null;

      Memo memo = null;
      String sMemo = null;
      String sDate = null;
      String sDateEnd = null;
      int nSubjectId;
      String sLangId = null;

    	//read parameters
    	if( data.getParameters().getString( "nLocationId" )!=null && data.getParameters().getString( "nLocationId" ).length()>0 ) {
    		m_sLocationId = data.getParameters().getString( "nLocationId" );
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

      sDate = UtilFunctions.getCurrentMySQLDate();
      sDateEnd = UtilFunctions.getCurrentMySQLDate( System.currentTimeMillis() +
                                                    MagConstants.N_NUMBER_OF_DAYS * 24 * 60 * 60 * 1000 );

     	/* debug
      System.out.println( "location id: " + m_sLocationId );
      */

      //build query
      Criteria crit = new Criteria();

      //join tables
     	crit.addJoin( SubjectPeer.FOREIGN_ID, LocationPeer.LOCATION_ID );
      crit.addAscendingOrderByColumn( LocationPeer.NAME );
      crit.add( SubjectPeer.TYPE_ID, 2 );
      crit.add( LocationPeer.LOCATION_ID, new Integer( m_sLocationId ).intValue() );

      try {
      	vecSubjects = SubjectPeer.doSelect( crit );
      	//System.out.println( crit.toString() );
      } catch( Exception e ) {
        Log.error("LocationDetail, doBuildTemplate, error while selecting locations: " + e.getMessage());
      }

    	//selects events, types and style for every location
      if( vecSubjects.size()>0 ) {
        crit = new Criteria();
        Criteria critArticle = new Criteria();
        Criteria critEvents = new Criteria();
        Vector vecEvents = new Vector();
        Vector vecArticle = new Vector();
        currSubject = ((Subject)vecSubjects.get(0));
        //nForeignId=currSubject.getForeignId();
        nSubjectId=currSubject.getSubjectId().getBigDecimal().intValue();
        //events
        critArticle.addJoin( SubjectPeer.SUBJECT_ID, ArticleSubjectPeer.SUBJECT_ID );
        critArticle.add( SubjectPeer.SUBJECT_ID, nSubjectId );
        try {
          vecArticle = ArticleSubjectPeer.doSelect( critArticle );
        } catch( Exception e ) {
          Log.error("LocationDetail, doBuildTemplate, error while selecting events: " + e.getMessage());
        }
        critEvents.addAscendingOrderByColumn( EventPeer.BEGIN );
        for( int k=0; k<vecArticle.size(); k++ ) {
          int nArticleId = ((ArticleSubject)vecArticle.get(k)).getArticleId().getBigDecimal().intValue();
          critEvents.addJoin( ArticleSubjectPeer.SUBJECT_ID, SubjectPeer.SUBJECT_ID );
          critEvents.addJoin( SubjectPeer.FOREIGN_ID, EventPeer.EVENT_ID );
          critEvents.add( ArticleSubjectPeer.ARTICLE_ID, nArticleId );
          critEvents.add( SubjectPeer.TYPE_ID, 5 );
          criton = critEvents.getNewCriterion( EventPeer.BEGIN, (Object) sDate, Criteria.GREATER_EQUAL );
          criton.and( critEvents.getNewCriterion( EventPeer.BEGIN, (Object) sDateEnd, Criteria.LESS_EQUAL ) );
          critEvents.add( criton );
          try {
            vecEvents.addAll( EventPeer.doSelect( critEvents ) );
          } catch( Exception e ) {
            Log.error("LocationDetail, doBuildTemplate, error while selecting events: " + e.getMessage());
          }
        }

        //memo
        try {
          if( currSubject.getSubjectMemos().size()>0 ) {
            if( ((SubjectMemo) currSubject.getSubjectMemos().get(0)).getMemo().getMemotypeId().getBigDecimal().intValue()==MagConstants.N_PUBLIC_MEMO_ID )
              memo = ((SubjectMemo) currSubject.getSubjectMemos().get(0)).getMemo();
              sMemo = MemoFacade.parseMemoBody( MagConstants.S_LOCATIONMEMO_STYLESHEET, memo, data );
              context.put( "sMemo", sMemo);
          }
        } catch( Exception e ) {
          Log.error("LocationDetail, doBuildTemplate, error while selecting and transforming memo: " + e.getMessage());
        }

        try {
          currLocation=(Location) currSubject.getConcrete();
          crit.add( SubjectPeer.FOREIGN_ID, currSubject.getForeignId() );
          crit.add( SubjectPeer.TYPE_ID, "2" );
          crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
          crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
          vecLocationKeywords.add( new LocationKeywordWrapper( currLocation, vecEvents, KeywordPeer.doSelect( crit ), sMemo ) );
      } catch( Exception e ) {
        	Log.error("LocationDetail, doBuildTemplate, error while selecting keywords: " + e.getMessage());
      	}
    	}

    	context.put( "vecLocationKeywords", vecLocationKeywords );
      context.put( "sLangId", sLangId );
    }

}

