package obj.fork.flyer.mag.modules.screens;

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
import org.apache.turbine.om.NumberKey;

// mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.modules.screens.magazine.MagazineConstants;
import obj.fork.flyer.mag.NoItemAvailableException;
import obj.fork.flyer.mag.util.ItemPresentation;
import obj.fork.flyer.mag.util.UtilFunctions;

// laika
import obj.fork.laika.om.*;

/*
 * FLYER.DE Home Screen
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version $Id: Home.java,v 1.4 2001/09/07 11:34:25 schmidt Exp $
 */
public class Home extends WeakScreen
{
    /**
     * Screen config
     */

    // channel
    private final int[] MAGNAV_CHANNEL = {
        11,12
    };

    private final int[] MAG_OPENER_CHANNEL =
    MagazineConstants.CHANNEL_THEMEN;

    // cache
    private final int MAG_CHANNEL_TIMEOUT = 5000;
    private final int MAG_OPENER_TIMEOUT = 5000;


    /*
     **/

    public void doBuildTemplate( RunData data, Context context )
        throws Exception
    {
        GlobalCacheService gs = (GlobalCacheService)TurbineServices.getInstance()
            .getService(GlobalCacheService.SERVICE_NAME);

        // which issue?
        Issue activeIssue =
        (Issue)data.getSession().getAttribute(
            "activeIssue"
            );

        // which edition?
        Edition activeEdition =
        (Edition)data.getSession().getAttribute(
            "activeEdition"
            );

        /*
         * NOTE: we should implement a double check
         * if the session constains all required var's
         * (activeIssue + activeEdition)
         */

        // the flexible magazine channel
        try
        {

            // get used magazine channel
            Vector usedMagazineChannel;
            try
            {
                // Look for item in the cache.
                usedMagazineChannel = (Vector) gs.getObject(
                    "usedMagazineChannel"
                    + activeIssue.getIssueId().toString()
                    + activeEdition.getEditionId().toString()
                    ).getContents();

                Log.debug("Retrieved usedMagazineChannel from cache");
            }
            catch(ObjectExpiredException gone)
            {
                //nav
                usedMagazineChannel = getUsedMagazineChannel(
                    activeIssue,
                    activeEdition,
                    MAGNAV_CHANNEL
                    );

                gs.addObject(
                    "usedMagazineChannel"
                    + activeIssue.getIssueId().toString()
                    + activeEdition.getEditionId().toString(),
                    new CachedObject(usedMagazineChannel, MAG_CHANNEL_TIMEOUT)
                    );

                Log.debug("Added usedMagazineChannel to cache");
            }

            context.put(
                "usedMagazineChannel",
                usedMagazineChannel
                );


            // get the magazine opener
            ItemPresentation magazineOpener;
            try
            {
                // Look for item in the cache.
                magazineOpener = (ItemPresentation) gs.getObject(
                    "magazineOpener"
                    + activeIssue.getIssueId().toString()
                    + activeEdition.getEditionId().toString()
                    ).getContents();

                Log.debug("Retrieved magazineOpener from cache");
            }
            catch(ObjectExpiredException gone)
            {
                // magazine opener
                magazineOpener = getRandomMagazineOpener(
                    activeIssue,
                    activeEdition,
                    MAG_OPENER_CHANNEL
                    );

                gs.addObject(
                    "magazineOpener"
                    + activeIssue.getIssueId().toString()
                    + activeEdition.getEditionId().toString(),
                    new CachedObject(magazineOpener, MAG_OPENER_TIMEOUT)
                    );

                Log.debug("Added magazineOpener to cache");
            }

            context.put(
                "magazineOpener",
                magazineOpener
                );

        }
        catch(NoItemAvailableException noItem)
        {
            Log.error( noItem.getMessage() );
        }

        /*
         *  END MAGAZINE PART
         **/

        // get event opener
        try
        {
            ItemPresentation eventOpener = getRandomEventOpener(
                activeIssue,
                activeEdition
                );

            context.put(
                "eventOpener",
                eventOpener
                );
        }
        catch(NoItemAvailableException noItem)
        {
            Log.error( noItem.getMessage() );
        }
    }

    /*
     * get a list of channels which are not
     * statically linked
     * @param Issue
     * @param Edition
     * @param channel_ids, the potential channels
     * @returns a Vector of om.Channel objects
     */
    private Vector getUsedMagazineChannel(
        Issue issue,
        Edition edition,
        int[] channel_ids
        )
        throws Exception
    {

        Vector usedChannel = new Vector();

        Criteria crit;
        Vector result;

        for(int i=0; i<channel_ids.length; i++)
        {
            crit = new Criteria();
            crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
            crit.add(ArticleChannelPeer.CHANNEL_ID, channel_ids[i]);
            crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
            crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
            crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());

            result = ArticlePeer.doSelect(crit);

            if(!result.isEmpty())
            {
                usedChannel.add(
                    ChannelPeer.retrieveByPK(
                        new NumberKey(channel_ids[i]),
                        new NumberKey(issue.getLangId())
                        )
                    );
            }
        }

        return usedChannel;

    }


    /*
     * get the magazine opener
     *
     * @param Issue
     * @param Edition
     * @param channel_ids, the potential channels
     * @returns a random ItemPresentation object
     */
    private ItemPresentation getRandomMagazineOpener(
        Issue issue,
        Edition edition,
        int[] channel_ids
        )
        throws Exception
    {
        Criteria crit = new Criteria();
        crit = new Criteria();
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        crit.addIn(ArticleChannelPeer.CHANNEL_ID, channel_ids);
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());

        crit.add(
            ArticlePeer.ARTICLETYPE_ID,
            MagazineConstants.OPENER
            );

        Vector results = ArticlePeer.doSelect(crit);

        Vector opener = UtilFunctions.article2ItemPresentation(results);

        if(!opener.isEmpty())
        {
            return (ItemPresentation)opener.elementAt(
                randomElement( opener.size() )
                );
        }

        /*
         * none of type opener found,
         * fallback to regular article
         */

        Criteria regCrit = new Criteria();
        regCrit = new Criteria();
        regCrit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        regCrit.addIn(ArticleChannelPeer.CHANNEL_ID, channel_ids);
        regCrit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        regCrit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        regCrit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());

        regCrit.add(
            ArticlePeer.ARTICLETYPE_ID,
            MagazineConstants.REGULAR
            );

        results = ArticlePeer.doSelect(regCrit);

        Vector regulars = UtilFunctions.article2ItemPresentation(results);

        if(!regulars.isEmpty())
        {
            return (ItemPresentation)regulars.elementAt(
                randomElement( regulars.size() )
                );
        }
        else
        {
            throw new NoItemAvailableException(
                "There are no items available for channel "
                + channel_ids
                + " (Magazin)"
                );
        }
    }

    /*
     * get an event opener
     * @param Issue
     * @param Edition
     * @returns a random ItemPresentation
     */
    private ItemPresentation getRandomEventOpener(
        Issue issue,
        Edition edition
        )
        throws Exception
    {
        Criteria crit = new Criteria();
        //base join to event subjects
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleSubjectPeer.ARTICLE_ID);
        crit.addJoin(ArticleSubjectPeer.SUBJECT_ID, SubjectPeer.SUBJECT_ID);
        crit.add(SubjectPeer.TYPE_ID, "5");

        // select baseitems
        crit.add(
            ArticlePeer.SYSTEM_ID,
            (Object) new String("%baseItem%"),
            Criteria.LIKE
            );

        // is openener
        crit.add(
            ArticlePeer.ARTICLETYPE_ID,
            MagazineConstants.OPENER
            );

        // from certain edition
        crit.addJoin(SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID);
        crit.add(EditionSubjectPeer.EDITION_ID, edition.getEditionId());

        // issue and status
        crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);

        Vector results = ArticlePeer.doSelect(crit);
        Vector opener = UtilFunctions.article2ItemPresentation(results);

        if(!opener.isEmpty())
        {
            return (ItemPresentation)opener.elementAt(
                randomElement( opener.size() )
                );
        }

        /*
         * fallback to regular article
         */

        Criteria regCrit = new Criteria();
        //base join to event subjects
        regCrit.addJoin(ArticlePeer.ARTICLE_ID, ArticleSubjectPeer.ARTICLE_ID);
        regCrit.addJoin(ArticleSubjectPeer.SUBJECT_ID, SubjectPeer.SUBJECT_ID);
        regCrit.add(SubjectPeer.TYPE_ID, "5");

        // select baseitems
        regCrit.add(
            ArticlePeer.SYSTEM_ID,
            (Object)new String("%baseItem%"),
            Criteria.LIKE
            );

        // is openener
        regCrit.add(
            ArticlePeer.ARTICLETYPE_ID,
            MagazineConstants.REGULAR
            );

        // from certain edition
        regCrit.addJoin(SubjectPeer.SUBJECT_ID, EditionSubjectPeer.SUBJECT_ID);
        regCrit.add(EditionSubjectPeer.EDITION_ID, edition.getEditionId());

        // issue and status
        regCrit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());
        regCrit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        regCrit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);

        results = ArticlePeer.doSelect(regCrit);
        Vector regulars = UtilFunctions.article2ItemPresentation(results);

        if(!regulars.isEmpty())
        {
            return (ItemPresentation)regulars.elementAt(
                randomElement( regulars.size() )
                );
        }
        else
        {
            throw new NoItemAvailableException(
                "There are no baseItems available for events."
                );
        }
    }

    /*
     * returns a random index between 0 and size
     */
    private int randomElement(int size)
    {
        int max = size-1;

        long randomIndex = Math.round(
            max * Math.random()
            );

        if(randomIndex >= 0)
        {
            return new Long(randomIndex).intValue();
        }
        else
        {
            return max;
        }
    }
}
