package obj.fork.flyer.mag.modules.navigations.locations;

import java.util.Vector;

import org.apache.turbine.modules.navigations.VelocityNavigation;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.Log;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;
import org.apache.turbine.om.peer.BasePeer;
import org.apache.turbine.services.servlet.ServletService;

import com.workingdogs.village.Record;

import org.apache.velocity.context.Context;

import javax.servlet.http.HttpSession;

import obj.fork.laika.om.Edition;
import obj.fork.laika.om.KeywordPeer;
import obj.fork.laika.om.AddressPeer;
import obj.fork.laika.om.LocationPeer;
import obj.fork.laika.om.SubjectPeer;
import obj.fork.laika.om.EditionSubjectPeer;
import obj.fork.laika.om.Issue;

import obj.fork.flyer.mag.MagConstants;

/**
 * SelectForm sets values for the selectform of the locations
 * it sets two vectors, one for the existing districts of the current edition
 * the other one for types and styles for locations
 * edition is specified by param edition_id
 *
 * @param edition_id the edition id
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
    Vector vecDistricts=null;

    //setting up the cache service
    GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

    //the edition id
    int nEditionId=-1;

    int nLangId;

    //read parameters
    if( data.getSession().getAttribute( "activeEdition" )!=null )
      nEditionId = ((Edition)data.getSession().getAttribute( "activeEdition" )).getEditionId().getBigDecimal().intValue();
    else {
      Log.error( "SelectForm, doBuildTemplate, no edition found in session. Stop excecuting." );
      return;
    }

    if( data.getSession().getAttribute( "activeEdition" )!=null )
      nLangId = ((Issue)data.getSession().getAttribute( "activeIssue" )).getLangId().getBigDecimal().intValue();
    else {
      Log.error( "SelectForm(Event), doPerform, no issue found in session. Stop excecuting." );
      return;
    }

    //districts
    try {
      vecDistricts = (Vector) cacheService.getObject( "eventDistricts" + new Integer(nEditionId).toString() ).getContents();
    } catch( ObjectExpiredException oee ) {
      try {
        vecDistricts = getDistricts( nEditionId );
      } catch( Exception e ){
        Log.error("SelectForm, doBuildTemplate, error while reading districts and keywords: " + e.getMessage()  );
      }
      cacheService.addObject( "eventDistricts" + new Integer(nEditionId).toString(),
                              new CachedObject( vecDistricts, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );

    }
    context.put( "vecDistricts", vecDistricts );

    // provide keywords
    try {
      vecCharacter = (Vector) cacheService.getObject( "locationKeywords" ).getContents();
    } catch( ObjectExpiredException oee ) {
      try {
        vecCharacter = KeywordPeer.characterForSubjecttype(2,nLangId);
      } catch( Exception e ){
        Log.error("SelectForm, doBuildTemplate, error while reading districts and keywords: " + e.getMessage()  );
      }
      cacheService.addObject( "locationKeywords",
                              new CachedObject( vecCharacter, MagConstants.N_LOCATION_CACHING_TIMEOUT ) );
    }
    context.put( "vecCharacter", vecCharacter );
  }

  /**
   * return a vector of districs for a specified edition
   *
   * @param p_nEditionId
   */
  private Vector getDistricts( int p_nEditionId )
    throws Exception {
    //districts
    Criteria critAddress = new Criteria();
    critAddress.addSelectColumn( AddressPeer.DISTRICT );
    critAddress.addAscendingOrderByColumn( AddressPeer.DISTRICT );
    critAddress.setDistinct();
    critAddress.addJoin( AddressPeer.ADDRESS_ID, LocationPeer.ADDRESS_ID );
    critAddress.addJoin( LocationPeer.LOCATION_ID, SubjectPeer.FOREIGN_ID );
    critAddress.addJoin( SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID );
    critAddress.add(  EditionSubjectPeer.EDITION_ID, p_nEditionId );
    Vector vecRecords = BasePeer.doSelect( critAddress );
    Vector vecDistricts = new Vector();
    for( int i=0; i< vecRecords.size(); i++ )
      vecDistricts.add( ((Record)vecRecords.get(i)).getValue(1).asString() );
    return vecDistricts;
  }

}
