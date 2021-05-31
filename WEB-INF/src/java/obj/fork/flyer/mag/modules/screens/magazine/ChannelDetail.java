package obj.fork.flyer.mag.modules.screens.magazine;

// jdk
import java.util.Vector;
import java.io.FileReader;
import java.io.File;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.Log;

import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.xslt.TurbineXSLT;
import org.apache.turbine.services.xslt.TurbineXSLTService;
import org.apache.turbine.services.xslt.XSLTService;

import org.apache.turbine.services.servlet.ServletService;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.*;
import org.apache.turbine.om.NumberKey;

// mag
import obj.fork.flyer.mag.modules.screens.WeakScreen;
import obj.fork.flyer.mag.NoItemAvailableException;
import obj.fork.flyer.mag.util.UtilFunctions;
import obj.fork.flyer.mag.util.ItemPresentation;
import obj.fork.flyer.mag.MagConstants;
// laika
import obj.fork.laika.om.*;

/*
 * Channel Detail
 * displays the first available article or a specific one
 * and the remaining items from the same channel
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: ChannelDetail.java,v 1.5 2001/09/10 13:30:28 heiko Exp $
 */
public class ChannelDetail extends WeakScreen
{
    final private int activeItemTimeout = MagazineConstants.ACTIVE_ARTICLE_TIMEOUT;
    final private int remainingItemTimeout = MagazineConstants.REMAINING_ITEMS_TIMEOUT;
    final private String xslsheet = MagazineConstants.VIEW_ARTICLE_XSL;
    private final int MAGNAV_TIMEOUT = MagazineConstants.MAGNAV_TIMEOUT;

    public void doBuildTemplate( RunData data, Context context )
    {
        GlobalCacheService gs = (GlobalCacheService)TurbineServices.getInstance()
            .getService(GlobalCacheService.SERVICE_NAME);

        // which issue?
        Issue activeIssue = (Issue)data.getSession().getAttribute("activeIssue");
        // which edition?
        Edition activeEdition = (Edition)data.getSession().getAttribute("activeEdition");

        
        String article_id = data.getParameters().getString("article_id", "-1");
        String channel_id = data.getParameters().getString("channel_id");

        try
        {
            Vector availableItems, cachedItems;
            try
            {
                // Look for item in the cache.
                cachedItems = (Vector) gs.getObject(
                    "cachedItems" 
                    + activeIssue.getIssueId().toString()
                    + channel_id
                    ).getContents();

                // clone it, to leave the original untouched
                availableItems = (Vector)cachedItems.clone();

                Log.debug(
                    "Retrieved items for channel "
                    + channel_id
                    +" from cache."
                    );
            }
            catch(ObjectExpiredException gone)
            {
                // get all items for this channel
                cachedItems = getItemsForChannel(
                    activeIssue, 
                    channel_id
                    );

                gs.addObject(
                    "cachedItems" 
                    + activeIssue.getIssueId().toString()
                    + channel_id,
                    new CachedObject(cachedItems, remainingItemTimeout)
                    );

                // clone it, to leave the original untouched
                availableItems = (Vector)cachedItems.clone();

                Log.debug(
                    "Added items for channel "
                    + channel_id
                    +" to cache."
                    );
            }

            // the active channel
            context.put(
                "activeChannel",
                ChannelPeer.retrieveByPK(
                    new NumberKey(channel_id),
                    new NumberKey(activeIssue.getLangId())
                    )
                );

            // retrieve a specific item
            ItemPresentation activeItem = getItemById(
                article_id, 
                availableItems
                );

            // get the thumbnails
            Vector regulars = UtilFunctions.getImages(
                activeItem.getArticle(),
                MagConstants.REGULAR
                );

            activeItem.setRegulars(regulars);

            // the active item itself
            context.put(
                "activeItem",
                activeItem
                );
            
            /*
             * get the parsed activeItem
             */

            String parsedItem;
            Article art = activeItem.getArticle();
            try
            {
                // Look for item in the cache.
                parsedItem = (String) gs.getObject(
                    "parsedItem" 
                    + art.getArticleId().toString()                    
                    ).getContents();

                Log.debug(
                    "Retrieved parsedItem with id "
                    + art.getArticleId()
                    +" from cache."
                    );
            }
            catch(ObjectExpiredException gone)
            {
                // parse the item and add it to cache
                parsedItem = getParsedItem(art, xslsheet);

                gs.addObject(
                    "parsedItem" 
                    + art.getArticleId().toString(),
                    new CachedObject(parsedItem, activeItemTimeout)
                    );

                Log.debug(
                    "Added parsedItems with id "
                    + art.getArticleId()
                    +" to cache."
                    );
            }

            // put the parsedItem into context
            context.put(
                "activeItemBody",
                parsedItem
                );


            // build the forward/next iteration
            try
            {
                ItemIteration forwardBack = getItemIteration(
                    availableItems,
                    activeItem
                    );

                context.put(
                    "forwardBack",
                    forwardBack
                    );
            }
            catch(Exception e)
            {
                Log.error(
                    "Unable to build ItemIteration: "
                    + e.getMessage()
                    );
            }


            // finally add the ramaining items
            availableItems.remove(activeItem);
            context.put(
                "remainingItems",
                availableItems
                );

        }
        catch(NoItemAvailableException noItem)
        {
            Log.error(
                "Error getting Items for channel " 
                + channel_id
                + ": "
                + noItem.getMessage()
                );
        }
        catch(Exception e)
        {
            Log.error(
                "ChannelDetail :: generic Error building template: "
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
     * getItemsForChannel
     * get all items which belong to a specific channel
     *  
     * @param Issue
     * @param channel_id
     * @returns a Vector of om.Article objects
     */
    private Vector getItemsForChannel(
        Issue issue, 
        String channel_id
        )
        throws Exception
    {
        Criteria crit = new Criteria();
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        crit.add(ArticleChannelPeer.CHANNEL_ID, channel_id);

        Vector results = ArticlePeer.doSelect(crit);

        if(results.size() > 0)
        {
            return UtilFunctions.article2ItemPresentation(results);
        }
        else {
            throw new NoItemAvailableException();
        }

    }

    /*
     * get an item from a list of available items
     * if none set (article_id = -1) take the first available
     *
     * @param article_id
     * @param availableItems, a Vector of ItemPresentation objects
     * @returns an ItemPresentation
     */
    private ItemPresentation getItemById(
        String article_id, 
        Vector availableItems
        )
        throws Exception
    {
        ItemPresentation item;
        Article current;
        for(int i=0; i<availableItems.size(); i++)
        {
            item = (ItemPresentation)availableItems.elementAt(i);
            current = item.getArticle();

            if(current.getArticleId().toString().equals(article_id))
            {
                return item;
            }
        }

        return (ItemPresentation)availableItems.elementAt(0);
    }

    /*
     * parse an article source and return it
     * 
     * @param article
     * @returns the parsed article as a String object
     */
    private String getParsedItem(Article article, String xsl)
        throws Exception
    {
        String tansformedDoc = "XSL Transformation failed";
        // servlet service
        ServletService servlet = 
        (ServletService)TurbineServices.getInstance().getService(
            ServletService.SERVICE_NAME
            );

        // path to repository
        String path2repository = servlet.getRealPath(
            TurbineResources.getString("article.source.repository")
            )
            + "/";

        // do the transformation
        TurbineXSLTService xsltService = 
        (TurbineXSLTService)TurbineServices.getInstance().getService(
            XSLTService.SERVICE_NAME
            );

        tansformedDoc = xsltService.transform( 
            xsl, 
            new FileReader(
                path2repository + article.getSystemId()
                )
            );

        return tansformedDoc;
    }

    /*
     * get an ItemIteration object for available article
     *
     * @param items, availableItems
     * @param activeItem, the current article
     * @returns an ItemIteration
     */
    private ItemIteration getItemIteration(
        Vector items,
        ItemPresentation activeItem
        )
        throws Exception
    {
        ItemIteration iteration = new ItemIteration();

        iteration.setCurrent(
            items.indexOf(activeItem)
            );

        ItemPresentation presentation;
        for(int i=0; i<items.size(); i++)
        {
            presentation = (ItemPresentation)items.elementAt(i);

            if( iteration.getCurrent() == 0  
                && items.size() > 1
                )
            {
                // at least 2 items
                iteration.setPrevious(
                    items.size()-1
                    );

                iteration.setNext(1);
                
            }
            else if( iteration.getCurrent() == 0  
                     && items.size() <= 1
                     )
            {
                // just a single item 
                iteration.setPrevious(0);
                iteration.setNext(0);
                
            }
            else if( iteration.isPrevious(i) )
            {
                // the previous item
                iteration.setPrevious(i);
            }
            else if( iteration.isNext(i) )
            {
                // the next item
                iteration.setNext(i);
            }
            
            iteration.add(presentation);
        }

        return iteration;
    }
}
