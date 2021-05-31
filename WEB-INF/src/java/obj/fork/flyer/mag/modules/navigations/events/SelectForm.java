package obj.fork.flyer.mag.modules.navigations.events;

import java.util.Vector;
import java.util.Date;

import org.apache.turbine.modules.navigations.VelocityNavigation;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.Log;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;
import org.apache.turbine.om.peer.BasePeer;

import org.apache.velocity.context.Context;

import org.apache.turbine.services.servlet.ServletService;

import javax.servlet.http.HttpSession;

import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.om.AddressPeer;
import obj.fork.laika.om.Issue;


import obj.fork.flyer.mag.util.DateOption;
import obj.fork.flyer.mag.MagConstants;

import com.workingdogs.village.Record;

/**
 * select all required values for the select form of the event navigation
 * it sets two vectors one for the dates of the next 14 days
 * and one for the types and styles
 *
 * @author <a href="mailto:schmidt@fork.de">Daniel Schmidt</a>
 */
public class SelectForm extends VelocityNavigation  {

  /**
   * Put the transformed doc into velo. context
   * & calculate execution time.
   *
   */
  public void doBuildTemplate( RunData data, Context context ) {
    Vector vecCharacter=null;
    int nLangId;

    //setting up the cache service
    GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

    //read parameters
    if( data.getSession().getAttribute( "activeEdition" )!=null )
      nLangId = ((Issue)data.getSession().getAttribute( "activeIssue" )).getLangId().getBigDecimal().intValue();
    else {
      Log.error( "SelectForm(Event), doPerform, no issue found in session. Stop excecuting." );
      return;
    }

    try {
      vecCharacter = (Vector) cacheService.getObject( "eventKeywords" ).getContents();
    } catch( ObjectExpiredException oee ) {
      try {
        vecCharacter = KeywordPeer.characterForSubjecttype(5,nLangId);
      } catch( Exception e ){
    	  Log.error("SelectForm, doBuildTemplate, error while reading districts and keywords: " + e.getMessage()  );
      }
      cacheService.addObject( "eventKeywords",
                              new CachedObject( vecCharacter, MagConstants.N_EVENT_CACHING_TIMEOUT ) );
    }

    context.put( "vecCharacter", vecCharacter );

    //datum
    Vector vecDates = new Vector();
    Date currDate = new Date( System.currentTimeMillis() );
    vecDates.add( new DateOption( currDate, "heute" ) );
    for( int i=0; i<13; i++ ) {
      currDate = new Date( System.currentTimeMillis() + (i+1)*86400000 );
      vecDates.add( new DateOption( currDate ) );
    }
    context.put( "vecDates", vecDates );
  }

}
