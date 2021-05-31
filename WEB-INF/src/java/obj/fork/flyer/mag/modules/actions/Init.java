package obj.fork.flyer.mag.modules.actions;

import java.util.Vector;

import org.apache.velocity.context.Context;

import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;

import javax.servlet.http.HttpSession;

import obj.fork.flyer.mag.modules.actions.PublicAction;

import obj.fork.laika.util.Selectable;
import obj.fork.laika.om.Edition;
import obj.fork.laika.om.EditionPeer;
import obj.fork.laika.om.Publishedissue;
import obj.fork.laika.om.PublishedissuePeer;;

/**
 * class Init initialises the website
 * its acalled with param sEdition, the short name for the edition
 * it adds the edition id and the issue_id to the session
 *
 * Organisation:  FORK Unstable Media
 * @author schmidt. hlan, laser, vger, mrks
 * @version
 */

public class Init extends PublicAction {

  /**
 	 * adds the edition id and the issue id to the session
   * name of the session valus are
   *
   * edition id:  edition_id
   * issue id:    issue_id
   *
   * getsd the short form of the edition as param in data
   *
   * @param data Turbine information.
   * @param context Context for web pages.
   * @exception Exception, a generic exception.
   */
	public void doPerform( RunData data,Context context ) {

    //if values area set already there is nothing to do
    if( data.getSession().getAttribute( "edition_id" )!=null &&
        data.getSession().getAttribute( "edition_id" )!=null )
      return;


    //the short name of the edition
    String sEditionShort;
    //the edition id
    int nEditionId=-1;
    //the issue id
    int nIssueId=-1;
    //result sets
    Vector vecEdition = new Vector();
    Vector vecIssue = new Vector();

    sEditionShort = data.getParameters().getString( "edition" );
    if( sEditionShort==null ) {
      Log.error( "Init, doPerform, param 'edition' is not set. Stop excecuting." );
      return;
    }

    if( data.getSession().getAttribute( "edition_id" )==null ) {
      //edition
      Criteria critEdition = new Criteria();

      critEdition.add( EditionPeer.NAME_SHORT, sEditionShort );

      try {
        vecEdition = EditionPeer.doSelect( critEdition );
      } catch( Exception e ) {
        Log.error( "Init, doPerform, error while reading edition from database: " + e.getMessage() + ". edition short is: '" + sEditionShort + "'. Stop excecuting." );
        return;
      }
      if( vecEdition.size()>0 ) {
        nEditionId = ((Edition)vecEdition.get(0)).getEditionId().getBigDecimal().intValue();
      }
      else {
        Log.error( "Init, doPerform, no edition found in databse for edition short: '" + sEditionShort + "'. Stop excecuting." );
        return;
      }

      if( nEditionId!=-1 ) {
        data.getSession().setAttribute( "edition_id", new Integer( nEditionId ) );
      }
      else {
        Log.error( "Init, doPerform, no edition found in databse for edition short: '" + sEditionShort + "'. Stop excecuting." );
        return;
      }
    }

    if( data.getSession().getAttribute( "edition_id" )==null ) {
      //issue
      Criteria critIssue = new Criteria();

      critIssue.add( EditionPeer.EDITION_ID, nEditionId );
      critIssue.addJoin( EditionPeer.EDITION_ID, PublishedissuePeer.EDITION_ID );
      critIssue.addDescendingOrderByColumn( PublishedissuePeer.DATE_START );

      try {
        vecIssue = PublishedissuePeer.doSelect( critIssue );
      } catch( Exception e ) {
        Log.error( "Init, doPerform, error while reading issue id from database: " + e.getMessage() + ". edition short is: '" + sEditionShort + "'. Stop excecuting." );
        return;
      }
      if( vecIssue.size()>0 ) {
        nIssueId = ((Publishedissue)vecIssue.get(0)).getIssueId().getBigDecimal().intValue();
      }
      else {
        Log.error( "Init, doPerform, no issue id found in table publishedissue. Edition short is: '" + sEditionShort + "'. Stop excecuting." );
        return;
      }

      if( nIssueId!=-1 ) {
        data.getSession().setAttribute( "issue_id", new Integer( nIssueId ) );
      }
      else {
        Log.error( "Init, doPerform, no issue found in databse for edition short: '" + sEditionShort + "'. Stop excecuting." );
        return;
      }
    }


	}
}