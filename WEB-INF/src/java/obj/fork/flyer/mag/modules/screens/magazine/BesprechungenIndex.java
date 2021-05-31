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
 * Besprechungen Index Screen
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: BesprechungenIndex.java,v 1.3 2001/09/09 14:51:33 heiko Exp $
 */
public class BesprechungenIndex extends ChannelGroupScreen
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

        // get items for music
        try
        {
            Vector musicItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.MUSIC,
                3
                );

            context.put(
                "musicItems",
                musicItems
                );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for music channel: "
                + noItem.getMessage()
                );
        }

        // get items for movies
        try
        {
            Vector movieItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.MOVIES,
                3
                );

            context.put(
                "movieItems",
                movieItems
                );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for movie channel: "
                + noItem.getMessage()
                );
        }

        // get items for books
        try
        {
            Vector bookItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.BOOKS
                );

            context.put(
                "bookItems",
                bookItems
                );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for book channel: "
                + noItem.getMessage()
                );
        }

        // get items for games
        try
        {
            Vector gameItems = getItemsForChannel(
                activeIssue, 
                activeEdition, 
                MagazineConstants.GAMES
                );

            context.put(
                "gameItems",
                gameItems
                );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Unable to get items for games channel: "
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
