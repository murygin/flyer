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
 * Polaroid Screen
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: Polaroid.java,v 1.2 2001/09/10 13:30:28 heiko Exp $
 */
public class Polaroid extends WeakScreen
{
    // suffices to identify the imagetype
    private final String THUMBNAIL = "_th";
    private final String REGULAR = "_rg";
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

        String article_id = data.getParameters().getString("article_id", "-1");


        // path to repository
        try
        {
            context.put(
                "imgSrcPath",
                UtilFunctions.getImageHTTPPath()
                );
        }
        catch(Exception e)
        {
             Log.error(
                 "Unable to get Image source path: "
                 + e.getMessage()
                 );
        }

        //thumbnails
        try
        {
            context.put(
                "thumbnails",
                getImages( article_id, THUMBNAIL)
                );

        }
        catch(Exception e)
        {
            Log.error(
                "Unable to get thumbnails for polaroid screen: "
                + e.getMessage()
                );
        }

        //regulars
        try
        {
            context.put(
                "regulars",
                getImages( article_id, REGULAR)
                );

        }
        catch(Exception e)
        {
            Log.error(
                "Unable to get regulars for polaroid screen: "
                + e.getMessage()
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

    /*
     * get the images depending on type
     * @returns a Vector of om.Articleimage objects
     */
    private Vector getImages(
        String article_id,
        String imagetype
        )
        throws Exception
    {

        String typeCriteria = "%"+imagetype+"%";

        Criteria crit = new Criteria();
        crit.add(ArticleimagePeer.ARTICLE_ID, article_id);
        crit.add(
            ArticleimagePeer.SYSTEM_ID,
            (Object)typeCriteria,
            Criteria.LIKE
            );

        return ArticleimagePeer.doSelect(crit);
    }
}
