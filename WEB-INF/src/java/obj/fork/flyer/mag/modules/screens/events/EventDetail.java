package obj.fork.flyer.mag.modules.screens.events;

// jdk
import java.util.Vector;
import java.util.Iterator;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.Log;
import org.apache.turbine.services.xslt.TurbineXSLT;
import org.apache.turbine.services.xslt.TurbineXSLTService;
import org.apache.turbine.services.xslt.XSLTService;
import org.apache.turbine.services.servlet.ServletService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.resources.TurbineResources;

//velocity
import org.apache.velocity.context.Context;

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
import obj.fork.laika.om.Artist;
import obj.fork.laika.om.Article;
import obj.fork.laika.om.ArticlePeer;
import obj.fork.laika.om.ArticleSubject;
import obj.fork.laika.om.ArticleSubjectPeer;
import obj.fork.laika.om.ArticleChannelPeer;
import obj.fork.laika.om.ChannelMediaPeer;
import obj.fork.laika.om.ChannelPeer;
import obj.fork.laika.om.Subject;
import obj.fork.laika.om.SubjectPeer;
import obj.fork.laika.om.SubjectKeywordPeer;
import obj.fork.laika.om.SubjecttypePeer;
import obj.fork.laika.om.SubjecttypeKeywordPeer;
import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.util.Selectable;
import obj.fork.laika.om.SubjectMemo;
import obj.fork.laika.om.SubjectMemoPeer;

import obj.fork.flyer.mag.util.ItemPresentation;

//mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.om.EventKeywordWrapper;
import obj.fork.flyer.mag.MagConstants;
import obj.fork.flyer.mag.util.ItemPresentation;
import obj.fork.flyer.mag.util.UtilFunctions;
import obj.fork.flyer.mag.om.ExtEvent;


/**
 * class EventDetail provides data for template /screens/events/EventDetail.vm
 * EventDetail displays one single event
 * event is specified by id nEventId
 * EventDetail select data of the event
 * if an article about the event exists its converted by stylesheet
 * /resources/event_article.xsl
 *
 * @param nEventId the event id
 */
public class EventDetail extends WeakScreen {

    private final String S_DEFAULT_XSL = "event_article.xsl";

    //get the default path from ServletService
    ServletService ss = (ServletService)TurbineServices.getInstance().getService(ServletService.SERVICE_NAME);
    String S_DEFAULT_DOC_PATH = ss.getRealPath( TurbineResources.getString("article.source.repository") ) + "/";

		//search parameters
		private String m_sEventId="-1";

    public void doBuildTemplate( RunData data, Context context ) {
      Vector vecSubjects = new Vector();
      Vector vecEventKeywords = new Vector();
      ExtEvent currEvent;
      Subject currSubject;
      Location currLocation = null;
      Vector vecArtists = new Vector();
  	  int nForeignId;
      ItemPresentation item = null;
      Memo memo = null;
      String sMemo = null;
      String sMySqlDate = null;
      String sLangId = null;

      String sXslFile = S_DEFAULT_XSL;

      //read parameters
    	if( data.getParameters().getString( "nEventId" )!=null ) {
    		m_sEventId = data.getParameters().getString( "nEventId" );
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

      /* debug
      System.out.println( "event id: " + m_sTypeId );
      */

      //build query
      Criteria crit = new Criteria();

      //join tables
     	crit.addJoin( SubjectPeer.FOREIGN_ID, EventPeer.EVENT_ID );
      crit.add( EventPeer.EVENT_ID, new Integer( m_sEventId ).intValue() );
      crit.add( SubjectPeer.TYPE_ID, 5 );
      //select events
      try {
      	vecSubjects = SubjectPeer.doSelect( crit );
      	//System.out.println( crit.toString() );
      } catch( Exception e ) {
        Log.error("EventDetail, doBuildTemplate, error while selecting event: " + e.getMessage());
      }

    	//select location, types and styles of the event
    	if( vecSubjects.size()>0 ) {
        crit = new Criteria();
        try {
          currSubject = (Subject)vecSubjects.get(0);
          currEvent=new ExtEvent( (Event) currSubject.getConcrete() );

          //date
          sMySqlDate = UtilFunctions.getMySQLDate( currEvent.getBegin() );

          //memo
          try {
            if( currSubject.getSubjectMemos().size()>0 ) {
              if( ((SubjectMemo) currSubject.getSubjectMemos().get(0)).getMemo().getMemotypeId().getBigDecimal().intValue()==MagConstants.N_PUBLIC_MEMO_ID )
                memo = ((SubjectMemo) currSubject.getSubjectMemos().get(0)).getMemo();
                sMemo = MemoFacade.parseMemoBody( MagConstants.S_EVENTMEMO_STYLESHEET, memo, data );
                context.put( "sMemo", sMemo);
            }
          } catch( Exception e ) {
            Log.error("LocationDetail, doBuildTemplate, error while selecting and transforming memo: " + e.getMessage());
          }

          //select location, artikel
          Vector vecRelatedSubjects;
          Vector vecArticles;
          if( currSubject.getArticleSubjects().size()>0 ) {
            int nArticleId = ((ArticleSubject)currSubject.getArticleSubjects().get(0)).getArticleId().getBigDecimal().intValue();
            Criteria locationCrit = new Criteria();
            locationCrit.addJoin( ArticleSubjectPeer.ARTICLE_ID, ArticlePeer.ARTICLE_ID );
            locationCrit.add( ArticleSubjectPeer.ARTICLE_ID, nArticleId );
            vecRelatedSubjects = ArticleSubjectPeer.doSelect( locationCrit );
            vecArticles = getArticle( nArticleId );
            if( vecArticles.size()>0 ) {
              item = new ItemPresentation( (Article)vecArticles.get(0) );
            }

            for( int k=0; k<vecRelatedSubjects.size(); k++ ) {
              if( ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getTypeId().toString().equals("2") ) {
                currLocation = (Location) ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getConcrete();
              }
              if( ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getTypeId().toString().equals("1") ) {
                vecArtists.add( (Artist) ((ArticleSubject)vecRelatedSubjects.get(k)).getSubject().getConcrete() );
              }
            }
          }
          //types and styles
          nForeignId=new Integer(currEvent.getEventId().toString()).intValue();
          crit.add( SubjectPeer.FOREIGN_ID, nForeignId );
          crit.add( SubjectPeer.TYPE_ID, "5" );
          crit.addJoin( SubjectPeer.SUBJECT_ID, SubjectKeywordPeer.SUBJECT_ID );
          crit.addJoin( SubjectKeywordPeer.KEYWORD_ID, KeywordPeer.KEYWORD_ID );
    			vecEventKeywords.add( new EventKeywordWrapper( currEvent,
                                                         currLocation,
                                                         vecArtists,
                                                         KeywordPeer.doSelect( crit ),
                                                         item ) );
    		} catch( Exception e ) {
        	Log.error("EventDetail, doBuildTemplate, error while selecting keywords and location: " + e.getMessage() );
      	  e.printStackTrace();
        }
    	}

      context.put( "sLangId", sLangId );
      if( sMySqlDate!=null )
        context.put( "sSelectedDate", sMySqlDate );
    	context.put( "vecEventKeywords", vecEventKeywords );
    }


  /**
   * returns a vector of om article for an event if there is an article source
   * and not only a dummy article and if the article is in a html channel
   * if its a dummy article null is returned
   *
   * @param p_nArticleId the article id
   */
  private Vector getArticle( int p_nArticleId )
    throws Exception {
    Criteria crit = new Criteria();
    crit.addJoin( ArticleSubjectPeer.ARTICLE_ID, ArticlePeer.ARTICLE_ID );
    crit.addJoin( ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID );
    crit.addJoin( ArticleChannelPeer.CHANNEL_ID, ChannelPeer.CHANNEL_ID );
    crit.addJoin( ChannelPeer.CHANNEL_ID, ChannelMediaPeer.CHANNEL_ID );
    crit.add( ChannelMediaPeer.MEDIA_ID, 1 );
    crit.add( ArticleSubjectPeer.ARTICLE_ID, p_nArticleId );
    return ArticlePeer.doSelect( crit );
  }

  /*
  private Article getArticle( Vector vecRelatedSubjects )
    throws Exception {
    String sArticleFile;
    Vector vecArticleChannels;
    Iterator itArticleChannels;
    Vector vecChannelMedias;
    Iterator itChannelMedias;
    boolean bIsHtml=false;
    if( vecRelatedSubjects.size()>0 ) {
      Article omArticle = null;
      omArticle = ((ArticleSubject)vecRelatedSubjects.get(0)).getArticle();
      vecArticleChannels = omArticle.getArticleChannels();
      itArticleChannels = vecArticleChannels.iterator();
      while( itArticleChannels.hasNext() && !bIsHtml ) {
        vecChannelMedias = ((ArticleChannel)itArticleChannels.next()).getChannel().getChannelMedias();
        itChannelMedias = vecChannelMedias.iterator();
        while( itChannelMedias.hasNext() && !bIsHtml ) {
          bIsHtml = ( ((ChannelMedia) itChannelMedias.next()).getMediaId().getBigDecimal().intValue()==1 );
        }
      }
      sArticleFile = omArticle.getSystemId();
      if( sArticleFile!=null && !sArticleFile.equals( "/dev/null.xml" ) && bIsHtml ) {
        return omArticle;
      }
      else {
        return null;
      }
    }
    else
      return null;
  }
  */
   /**
   * Get the transformed document as String Object
   * @param xsl XSL Sheet
   * @param xml doc Source document
   * @param data RunData
   * @return transformed Document
   */
  private String xml2Html(String xsl, org.w3c.dom.Element element, RunData data) {

    TurbineXSLTService xsltService = (TurbineXSLTService)TurbineServices.getInstance().getService(XSLTService.SERVICE_NAME);

    GlobalCacheService gs = null;
    String transformedDoc = null;
    /*caching deaktiviert
    try {
      //Look for the item in the cache. If it doesn't exist or the item is stale,
      //the cache will throw an exception.
      gs = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

      transformedDoc = (String) gs.getObject(xmluri).getContents();

      data.setMessage( " Got output from global cache!" );
    }
    catch(ObjectExpiredException gone) {
      //Build document
      try {
        transformedDoc = xsltService.transform( xsl, element );
      }
      catch (Exception e) {
        Log.debug( "system", "ViewArticle, Fehler bei der Stylesheettransformation. " + e.getMessage() );
        data.addMessage( " ViewArticle, Fehler bei der Stylesheettransformation. " + e.getMessage() );
      }
      //Add document to the cache.
      gs.addObject( xmluri, new CachedObject(transformedDoc,20000));
      data.addMessage( " Added transformedDoc to" + " the cache! Expires in 20 seconds" );
    }
    */

    //Build document
    try {
    	transformedDoc = xsltService.transform( xsl, element );
    }
    catch (Exception e) {
    	Log.error( "EventDetail, xml2Html, Fehler bei der Stylesheettransformation. " + e.getMessage() );
    }
    return transformedDoc;
  }
}

