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
import obj.fork.flyer.mag.util.UtilFunctions;
import obj.fork.flyer.mag.NoItemAvailableException;
import obj.fork.flyer.mag.util.ItemPresentation;
import obj.fork.flyer.mag.MagConstants;

// laika
import obj.fork.laika.om.*;

/*
 * Magazine Index Screen
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: MagazineIndex.java,v 1.7 2001/09/09 14:51:34 heiko Exp $
 */
public class MagazineIndex extends WeakScreen
{
    final private int OPENER_TIMEOUT = 5000;
    final private int GROUP_TIMEOUT = 5000;

    public void doBuildTemplate( RunData data, Context context )
    {
        GlobalCacheService gs = (GlobalCacheService)TurbineServices.getInstance()
            .getService(GlobalCacheService.SERVICE_NAME);

        // which issue?
        Issue activeIssue = (Issue)data.getSession().getAttribute("activeIssue");

        // which edition?
        Edition activeEdition = (Edition)data.getSession().getAttribute("activeEdition");

        // get the required channle groups for navigation
        Vector themenChannel, besprechungenChannel, otherChannel, spezialChannel;

        try
        {
            // get themen
            try
            {
                // Look for item in the cache.
                themenChannel = (Vector) gs.getObject(
                    "themenChannel" + activeIssue.getIssueId().toString()
                    ).getContents();

                Log.debug("Retrieved themen group from cache");
            }
            catch(ObjectExpiredException gone)
            {
                themenChannel = UtilFunctions.getUsedChannel(
                    activeIssue, 
                    activeEdition,
                    MagazineConstants.CHANNEL_THEMEN
                    );

                gs.addObject(
                    "themenChannel" + activeIssue.getIssueId().toString(),
                    new CachedObject(themenChannel, GROUP_TIMEOUT)
                    );

                Log.debug("Added themen group to cache");
            }

            // get besprechungen
            try
            {
                // Look for item in the cache.
                besprechungenChannel = (Vector) gs.getObject(
                    "besprechungenChannel" + activeIssue.getIssueId().toString()
                    ).getContents();

                Log.debug("Retrieved besprechungen group from cache");
            }
            catch(ObjectExpiredException gone)
            {
                besprechungenChannel = UtilFunctions.getUsedChannel(
                    activeIssue,
                    activeEdition,
                    MagazineConstants.CHANNEL_BESPRECHUNGEN
                    );

                gs.addObject(
                    "besprechungenChannel" + activeIssue.getIssueId().toString(),
                    new CachedObject(besprechungenChannel, GROUP_TIMEOUT)
                    );

                Log.debug("Added besprechungen group to cache");
            }

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
                    new CachedObject(otherChannel, GROUP_TIMEOUT )
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
                    new CachedObject(spezialChannel, GROUP_TIMEOUT )
                    );
            }

            if(!spezialChannel.isEmpty())
                context.put(
                    "spezialHasContents",
                    new Boolean(true)
                    );

        }
        catch(Exception e)
        {
            Log.error(
                "Unable to get navigation groups for magazine index: "
                + e.getMessage()
                );

            return;
        }

        // add channel groups to context
        context.put("themenChannel", themenChannel);
        context.put("besprechungenChannel", besprechungenChannel);
        context.put("otherChannel", otherChannel);

        /*
         * opener
         */

        ItemPresentation themenOpener = null;
        try
        {
            // Look for item in the cache.
            themenOpener = (ItemPresentation) gs.getObject(
                "themenOpener" + activeIssue.getIssueId().toString()
                ).getContents();

            Log.debug("Retrieved themen opener from cache");
        }
        catch(ObjectExpiredException gone)
        {
            try
            {
                Article opener = getOpener(activeIssue);
                themenOpener = new ItemPresentation(opener);

                // get the thumbnails
                Vector thumbs = getImages(
                    opener, 
                    MagConstants.THUMBNAIL
                    );

                themenOpener.setThumbnails(thumbs);

            }
            catch(Exception e)
            {
                Log.error(
                    "Unable to get opener for magazine Index: "
                    + e.getMessage()
                    );
            }

            gs.addObject(
                "themenOpener" + activeIssue.getIssueId().toString(),
                new CachedObject(themenOpener, OPENER_TIMEOUT)
                );
        }

        // finally add the opener to context
        try
        {
            context.put(
                "themenOpener",
                themenOpener
                );
        }
        catch(Exception e)
        {
            Log.error(
                "Unable to add opener and its channel to magazine home context: "
                + e.getMessage()
                );
        }

        /*
         * news teaser
         */
        try
        {
            context.put(
                "newsTeaser",
                getNewsTeaser(activeIssue)
                );
        }
        catch(Exception e)
        {
            Log.error(
                "Unable to get news teaser for magazine home: "
                + e.getMessage()
                );
        }

        context.put(
            "imagePath",
            UtilFunctions.getImageHTTPPath()
            );
    }

    /*
     * getOpener
     * try to get an opener for magazine home
     * if it fails return a generic article from 'Themen'
     * 
     * @param Issue, the active Issue
     * @returns an om.Article object
     */
    private Article getOpener(Issue activeIssue)
        throws Exception
    {
        Criteria crit = new Criteria();
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        crit.add(ArticlePeer.ISSUE_ID, activeIssue.getIssueId());
        crit.addIn(ArticleChannelPeer.CHANNEL_ID, MagazineConstants.CHANNEL_THEMEN);
        crit.add(ArticlePeer.ARTICLETYPE_ID, MagazineConstants.OPENER);

        Vector opener = ArticlePeer.doSelect(crit);

        // we could also add some random madness here ;)
        if(!opener.isEmpty())
            return (Article)opener.elementAt(
                UtilFunctions.randomElement(opener.size())
                );

        // no opener found, get a regular article
        Criteria regCrit = new Criteria();
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        regCrit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        regCrit.add(ArticlePeer.ISSUE_ID, activeIssue.getIssueId());
        regCrit.addIn(ArticleChannelPeer.CHANNEL_ID, MagazineConstants.CHANNEL_THEMEN);
        regCrit.add(ArticlePeer.ARTICLETYPE_ID, MagazineConstants.REGULAR);

        Vector regular = ArticlePeer.doSelect(regCrit);

        if(!regular.isEmpty())
        {
            return (Article)regular.elementAt(
                UtilFunctions.randomElement(regular.size())
                );
        }
        else 
        {
            throw new NoItemAvailableException();
        }
        
    }

    /*
     * get the news teaser for magazine home
     * 
     * @param activeIssue, issue to choose from
     * @returns om.Article object
     */
    private Article getNewsTeaser(Issue activeIssue)
        throws Exception
    {
        Criteria crit = new Criteria();
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        crit.add(ArticleChannelPeer.CHANNEL_ID, MagazineConstants.NEWS_CHANNEL);
        crit.add(ArticlePeer.ISSUE_ID, activeIssue.getIssueId());
        crit.addAscendingOrderByColumn(ArticlePeer.MODIFY_STAMP);
        crit.setLimit(1);

        Vector results = ArticlePeer.doSelect(crit);

        return (Article)results.elementAt(0);
    }

    /*
     * get article images
     */
    private Vector getImages(Article article, String imgSuffix)
        throws Exception
    {
        String suffix = "%"+imgSuffix+"%";

        Criteria crit = new Criteria();

        crit.add(
            ArticleimagePeer.SYSTEM_ID,
            (Object)suffix,
            Criteria.LIKE
            );

        Vector images = article.getArticleimages(crit);

        return images;
    }

}
