package obj.fork.flyer.mag.modules.screens;

import java.util.Vector;

import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.Log;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.GlobalCacheService;
import org.apache.turbine.services.cache.CachedObject;
import org.apache.turbine.services.cache.ObjectExpiredException;


// mag
import obj.fork.flyer.mag.modules.screens.PublicScreen;
import obj.fork.flyer.mag.MagConstants;

// laika
import obj.fork.laika.om.*;

/*
 * Entrance to Magazine
 * provide a list of available editions
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 */
public class Entrance extends PublicScreen
{
    public void doBuildTemplate( RunData data, Context context )
    {
        //setting up the cache service
        GlobalCacheService cacheService = (GlobalCacheService)TurbineServices.getInstance().getService(GlobalCacheService.SERVICE_NAME);

        Criteria crit = new Criteria();
        crit.add(EditionPeer.EDITION_ID, 666, Criteria.NOT_EQUAL);

        String msg = data.getParameters().getString("msg", "");

        if(msg != "")
            data.setMessage(msg);

        try
        {
            Vector editions = EditionPeer.doSelect(crit);
            context.put("editions", editions);
            cacheService.addObject( "editions",
                                    new CachedObject( editions, MagConstants.MAGAZINE_NAV_TIMEOUT ) );
        }
        catch(Exception e)
        {
            Log.error(
                "Unable to retrieve Editions for Entrance Screen: "
                + e.getMessage()
                );
        }
    }

}
