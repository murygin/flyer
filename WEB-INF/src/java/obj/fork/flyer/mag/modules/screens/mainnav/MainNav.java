package obj.fork.flyer.mag.modules.screens.mainnav;

// jdk
import java.util.Vector;

// tdk
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.Log;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.*;

// mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.util.UtilFunctions;
import obj.fork.flyer.mag.MagConstants;
import obj.fork.flyer.mag.NoItemAvailableException;

// laika
import obj.fork.laika.om.*;


/**
 * Main Navigation Screen
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version $Id: MainNav.java,v 1.5 2001/09/10 13:15:36 schmidt Exp $
 */
public class MainNav extends WeakScreen
{
    public void doBuildTemplate( RunData data, Context context )
        throws Exception
    {
        Vector editions = null;

        GlobalCacheService gs = (GlobalCacheService)TurbineServices.getInstance()
            .getService(GlobalCacheService.SERVICE_NAME);

        // which issue?
        Issue activeIssue = (Issue)data.getSession().getAttribute("activeIssue");

        // which edition?
        Edition activeEdition = (Edition)data.getSession().getAttribute("activeEdition");

        try {
          editions = (Vector) gs.getObject( "editions" ).getContents();
        } catch( ObjectExpiredException oee ) {
          Criteria editionCrit = new Criteria();
          editionCrit.add(EditionPeer.EDITION_ID, 666, Criteria.NOT_EQUAL);
          try {
              editions = EditionPeer.doSelect(editionCrit);
              gs.addObject( "editions",
                            new CachedObject( editions, MagConstants.MAGAZINE_NAV_TIMEOUT ) );
          } catch(Exception e) {
              Log.error( "Unable to retireve Editions for Entrance Screen: " + e.getMessage() );
          }
        }
        context.put("editions", editions);

        // get magazine nav
        Vector usedMagazineChannel;
        try
        {
            // Look for item in the cache.
            usedMagazineChannel = (Vector) gs.getObject(
                "usedMagazineChannel"
                + activeIssue.getIssueId().toString()
                + activeEdition.getEditionId()
                ).getContents();

            Log.debug(
                "Retrieved usedMagazineChannel from cache."
                );
        }
        catch(ObjectExpiredException gone)
        {
            usedMagazineChannel = UtilFunctions.getUsedChannel(
                activeIssue,
                activeEdition,
                MagConstants.MAGAZINE_OTH_CHANNEL
                );

            gs.addObject(
                "usedMagazineChannel"
                + activeIssue.getIssueId().toString()
                + activeEdition.getEditionId(),
                new CachedObject(
                    usedMagazineChannel,
                    MagConstants.MAGAZINE_NAV_TIMEOUT
                    )
                );

            Log.debug(
                "Added usedMagazineChannel to cache."
                );
        }

        context.put(
            "usedMagazineChannel",
            usedMagazineChannel
            );


        // does spezial have items behind it?
        try
        {
            Vector usedSpezial = UtilFunctions.getUsedChannel(
                activeIssue,
                activeEdition,
                MagConstants.MAGAZINE_SPZ_CHANNEL
                );

            if(!usedSpezial.isEmpty())
                context.put(
                    "spezialHasContents",
                    new Boolean(true)
                    );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "An Error occured retrieving items for spezial: "
                + noItem.getMessage()
                );
        }
    }
}
