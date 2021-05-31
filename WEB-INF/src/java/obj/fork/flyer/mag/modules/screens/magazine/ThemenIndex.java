package obj.fork.flyer.mag.modules.screens.magazine;

// jdk
import java.util.Vector;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.Log;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.*;
import org.apache.turbine.om.NumberKey;

// mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.NoItemAvailableException;

import obj.fork.flyer.mag.util.UtilFunctions;

// laika
import obj.fork.laika.om.*;

/*
 * Themen Index Screen
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: ThemenIndex.java,v 1.4 2001/09/09 14:51:34 heiko Exp $
 */
public class ThemenIndex extends ChannelGroupScreen
{
    private final int MAGNAV_TIMEOUT = MagazineConstants.MAGNAV_TIMEOUT;

    public void doBuildTemplate( RunData data, Context context )
        throws Exception
    {
        GlobalCacheService gs = (GlobalCacheService)TurbineServices.getInstance()
            .getService(GlobalCacheService.SERVICE_NAME);

        // which issue?
        Issue activeIssue = (Issue)data.getSession().getAttribute("activeIssue");

        // which edition?
        Edition activeEdition = (Edition)data.getSession().getAttribute("activeEdition");

        // get items for channel article
        try
        {
            Vector articleItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.ARTICLE,
                3
                );

            context.put(
                "articleItems",
                articleItems
                );

        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for article channel: "
                + noItem.getMessage()
                );
        }

        // get items for interview's 
        try
        {
            Vector interviewItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.INTERVIEWS,
                3
                );

            context.put(
                "interviewItems",
                interviewItems
                );

        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for interview group: "
                + noItem.getMessage()
                );
        }

        // get items for galeries
        try
        {
            Vector galerieItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.GALERIES,
                3
                );

            context.put(
                "galerieItems",
                galerieItems
                );

        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for galerie group: "
                + noItem.getMessage()
                );
        }

        // get items for theater
        try
        {
            Vector theaterItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.THEATER
                );

            context.put(
                "theaterItems",
                theaterItems
                );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for theater group: "
                + noItem.getMessage()
                );
        }

        Vector otherChannel, spezialChannel;
        try
        {
            // get other
            try
            {
                // Look for item in the cache.
                otherChannel = (Vector) gs.getObject(
                    "otherChannel" + activeIssue.getIssueId().toString()
                    ).getContents();

                Log.debug("Retrieved other group from cache");
            }
            catch(ObjectExpiredException gone)
            {
                otherChannel = UtilFunctions.getUsedChannel(
                    activeIssue, 
                    activeEdition,
                    MagazineConstants.CHANNEL_OTHER
                    );

                gs.addObject(
                    "otherChannel" + activeIssue.getIssueId().toString(),
                    new CachedObject(otherChannel, MAGNAV_TIMEOUT )
                    );

                Log.debug("Added other group to cache");
            }

            // does spezial have items behind it?
            try
            {
                spezialChannel = (Vector) gs.getObject(
                    "spezialChannel" + activeIssue.getIssueId().toString()
                    ).getContents();
            }
            catch(ObjectExpiredException gone)
            {
                spezialChannel = UtilFunctions.getUsedChannel(
                    activeIssue,
                    activeEdition,
                    MagazineConstants.CHANNEL_SPEZIAL
                    );

                gs.addObject(
                    "spezialChannel" + activeIssue.getIssueId().toString(),
                    new CachedObject(spezialChannel, MAGNAV_TIMEOUT )
                    );
            }

            if(!spezialChannel.isEmpty())
                context.put(
                    "spezialHasContents",
                    new Boolean(true)
                    );

            context.put("otherChannel", otherChannel);
        }
        catch(Exception e)
        {
            Log.error(
                "Unable to get other and spezial: "
                + e.getMessage()
                );
        }
    }

}
