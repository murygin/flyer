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
import obj.fork.flyer.mag.MagConstants;
import obj.fork.flyer.mag.util.ItemPresentation;
import obj.fork.flyer.mag.util.UtilFunctions;

// laika
import obj.fork.laika.om.*;

/*
 * base class for screens which 
 * aggregate several channel in in screen.
 * a channel overview screen, kind of... 
 */
public class ChannelGroupScreen extends WeakScreen
{

    /*
     * get items for a specific channel
     *
     * @param Issue, the active issue
     * @param Edition, the active edition
     * @param channel_id
     * @returns a vector of om.Article objects
     * @throws NoItemAvailableException, if no items are found in channel list
     */
    protected Vector getItemsForChannel(
        Issue issue, 
        Edition edition, 
        int channel_id,
        int limit
        )
        throws Exception
    {

        Criteria crit = new Criteria();
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        crit.add(ArticleChannelPeer.CHANNEL_ID, channel_id);
        crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());
        crit.setLimit(limit);
        crit.setDistinct();

        Vector result = ArticlePeer.doSelect(crit);

        if(result.isEmpty())
        {
            throw new NoItemAvailableException();
        }
        else 
        {
            return UtilFunctions.article2ItemPresentation(result);
        }

    }

    /*
     * getItemsForChannel
     * with a default limit of 5
     */
    protected Vector getItemsForChannel(
        Issue issue, 
        Edition edition, 
        int channel_id
        )
        throws Exception
    {
        return getItemsForChannel(
            issue, edition, channel_id, 5
            );
    }

    /*
     * get items for artikel & interviews
     *
     * @param Issue, the active issue
     * @param Edition, the active edition
     * @param channel_ids, int array 
     * @returns a vector of om.Article objects
     * @throws NoItemAvailableException, if no items are found in channel list
     */
    protected Vector getItemsForChannelGroup(
        Issue issue, 
        Edition edition, 
        int[] channel_ids,
        int limit
        )
        throws Exception
    {
        Vector usedChannel = getUsedChannel(
            getChannelForEdition(issue, edition),
            channel_ids
            );

        Criteria crit = new Criteria();
        crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
        crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
        crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);
        crit.addIn(ArticleChannelPeer.CHANNEL_ID, usedChannel);
        crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());
        crit.setLimit(limit);
        crit.setDistinct();

        Vector result = ArticlePeer.doSelect(crit);

        if(result.isEmpty())
        {
            throw new NoItemAvailableException();
        }
        else 
        {
            return result;
        }

    }

    /*
     * getItemsForChannelGroup
     * with a default limit of 5 items
     */
    protected Vector getItemsForChannelGroup(
        Issue issue, 
        Edition edition, 
        int[] channel_ids
        )
        throws Exception
    {
        return getItemsForChannelGroup(
            issue, edition, channel_ids, 5
            );
    }

    /*
     * get all available channel for this edition
     * @param Issue
     * @param Edition
     * @returns a Vector with om.Channel objects
     */
    protected Vector getChannelForEdition(Issue issue, Edition edition)
        throws Exception
    {
        Criteria edCrit = new Criteria();
        edCrit.addJoin(EditionPeer.EDITION_ID, EditionChannelPeer.EDITION_ID);
        edCrit.addJoin(EditionChannelPeer.CHANNEL_ID, ChannelPeer.CHANNEL_ID);
        edCrit.addJoin(ChannelPeer.CHANNEL_ID, ChannelMediaPeer.CHANNEL_ID);
        edCrit.add(ChannelPeer.LANG_ID, issue.getLangId());
        edCrit.add(ChannelMediaPeer.MEDIA_ID, "1");
        edCrit.add(EditionPeer.EDITION_ID, "666");
        edCrit.or(EditionPeer.EDITION_ID, edition.getEditionId());
        edCrit.setDistinct();
        Vector channelForEdition = ChannelPeer.doSelect(edCrit);

        return channelForEdition;
    }

    /*
     * match potential channel against available
     * and return an array of channel ids
     */
    protected Vector getUsedChannel(
        Vector availableChannel, 
        int[] potentialChannel
        )
        throws Exception
    {
        Vector usedChannel = new Vector();

        for(int i=0; i<availableChannel.size(); i++)
        {
            Channel current = (Channel)availableChannel.elementAt(i);
            int current_id = Integer.parseInt(
                current.getChannelId().toString()
                );

            for(int c=0; c<potentialChannel.length; c++)
            {
                if(current_id == potentialChannel[c])
                {
                    usedChannel.add(String.valueOf(current_id));
                    break;
                }
            }
        }

        return usedChannel;
    }
}
