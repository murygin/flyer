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
 * Mode Screen
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: Mode.java,v 1.1 2001/09/05 16:03:44 heiko Exp $
 */
public class Mode extends WeakScreen
{
    // suffices to identify the imagetype
    private final String THUMBNAIL = "_th";
    private final String REGULAR = "_rg";

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
                "Unable to get thumbnails for mode screen: "
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
                "Unable to get regulars for mode screen: "
                + e.getMessage()
                );
        }

        // the mode article itself
        try
        {
            context.put(
                "modeItem",
                ArticlePeer.retrieveByPK(
                    new NumberKey(article_id)
                    )
                );
        }
        catch(Exception e)
        {
            Log.error(
                "Unable to get mode article: "
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
