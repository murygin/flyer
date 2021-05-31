package obj.fork.flyer.mag.util;

// jdk
import java.util.Vector;
import java.util.Date;

// turbine
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.Log;

import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.servlet.ServletService;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.cache.*;
import org.apache.turbine.om.NumberKey;

// mag
import obj.fork.flyer.mag.NoItemAvailableException;
import obj.fork.flyer.mag.modules.screens.magazine.MagazineConstants;
import obj.fork.flyer.mag.MagConstants;
// laika
import obj.fork.laika.om.*;
import obj.fork.laika.util.Selectable;


/*
 * util collections
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: UtilFunctions.java,v 1.7 2001/09/12 08:40:40 schmidt Exp $
 */
public class UtilFunctions
{

    /*
     * getAvailableChannel
     * a channel is available when
     * it has article behind it
     *
     * @param issue, the active issue
     * @param edition, the choosen edition
     * @param channel_ids, potential channel
     * @returns a vector with om.channel objects
     */
    public static Vector getUsedChannel(
        Issue issue,
        Edition edition,
        int[] channel_ids
        )
        throws Exception
    {
        String issue_id = issue.getIssueId().toString();
        Vector availableChannel = new Vector();

        // match potential channel against channel bound to edition
        Criteria edCrit = new Criteria();
        edCrit.addJoin(EditionPeer.EDITION_ID, EditionChannelPeer.EDITION_ID);
        edCrit.addJoin(EditionChannelPeer.CHANNEL_ID, ChannelPeer.CHANNEL_ID);
        edCrit.addJoin(ChannelPeer.CHANNEL_ID, ChannelMediaPeer.CHANNEL_ID);
        edCrit.add(ChannelMediaPeer.MEDIA_ID, "1");
        edCrit.add(EditionPeer.EDITION_ID, "666");
        edCrit.or(EditionPeer.EDITION_ID, edition.getEditionId());
        edCrit.setDistinct();
        Vector channelForEdition = EditionChannelPeer.doSelect(edCrit);

        Vector channelWithRelation = new Vector();

        for(int i=0; i<channelForEdition.size(); i++)
        {
            int currentId = Integer.parseInt(
                ((EditionChannel)channelForEdition.elementAt(i)).getChannelId().toString()
                );

            for(int c=0; c<channel_ids.length; c++)
            {
                if(channel_ids[c] == currentId)
                {
                    channelWithRelation.add(
                        String.valueOf(currentId)
                        );

                    break;
                }
            }
        }

        Criteria crit;
        for(int i=0; i<channelWithRelation.size(); i++)
        {
            crit = new Criteria();
            crit.addJoin(ArticlePeer.ARTICLE_ID, ArticleChannelPeer.ARTICLE_ID);
            crit.addJoin(ArticlePeer.FLOW_ID, ArticleflowPeer.FLOW_ID);
            crit.add(ArticlePeer.ISSUE_ID, issue.getIssueId());
            crit.add(ArticleflowPeer.STATUS_ID, MagazineConstants.APPROVED);

            crit.add(
                ArticleChannelPeer.CHANNEL_ID,
                channelWithRelation.elementAt(i)
                );

            crit.setLimit(1);

            Vector result = ArticlePeer.doSelect(crit);

            if(result.size() > 0)
            {
                availableChannel.add(
                    ChannelPeer.retrieveByPK(
                        new NumberKey(
                            (String)channelWithRelation.elementAt(i)
                            ),
                        new NumberKey( issue.getLangId().toString() )
                        )
                    );
            }
        }

        if(availableChannel.size() > 0)
        {
            return availableChannel;
        }
        else
        {
            throw new NoItemAvailableException(
                "UtilFunction.getUsedChannel() :: "
                + channel_ids
                );
        }
    }

    /*
     * returns a random index between 0 and size
     */
    public static int randomElement(int size)
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

    /*
     * get the path to article image sources
     */
    public static String getImageHTTPPath()
    {
        return "/mag" + TurbineResources.getString("article.image.repository") + "/";
    }

    /*
     * get the path to article sources
     */
    public static String getArticleSourcePath()
    {

        ServletService ss = (ServletService)TurbineServices.getInstance()
            .getService(ServletService.SERVICE_NAME);

        return ss.getRealPath(
            TurbineResources.getString("article.source.repository")
            )
            + "/";
    }

    /*
     * turn a vector of article
     * into a vector of itemPresentations
     * @param vector of article
     * @returns a vector of ItemsPresentations
     */
    public static Vector article2ItemPresentation(Vector articles)
        throws Exception
    {
        Article current;
        ItemPresentation presentation;
        Vector items = new Vector();

        for(int i=0; i<articles.size(); i++)
        {
            current = (Article)articles.elementAt(i);
            presentation = new ItemPresentation(current);

            // get the thumbnails
            Vector thumbs = getImages(
                current,
                MagConstants.THUMBNAIL
                );

            presentation.setThumbnails(thumbs);

            items.add(presentation);
        }

        return items;
    }

    /*
     * get article images
     */
    public static Vector getImages(Article article, String imgSuffix)
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

    /**
     * returns the current date in the mysql format yyyy-mm-dd
     */
    public static String getCurrentMySQLDate() {
      Date currDate = new Date( System.currentTimeMillis() );
      String sYear, sMonth, sDay;
      sYear = new Integer( currDate.getYear() + 1900 ).toString();
      sMonth= new Integer( currDate.getMonth() + 1 ).toString();
      if( sMonth.length()==1 )
        sMonth = "0" + sMonth;
      sDay = new Integer( currDate.getDate() ).toString();
      if( sDay.length()==1 )
        sDay = "0" + sDay;
      return sYear + "-" + sMonth + "-" + sDay;
    }

    /**
     * returns the date specified by long p_lTimeMillis
     * in the mysql format yyyy-mm-dd
     *
     * @param p_lTimeMillis the time as timestamp
     */
    public static String getCurrentMySQLDate( long p_lTimeMillis ) {
      Date currDate = new Date( p_lTimeMillis );
      String sYear, sMonth, sDay;
      sYear = new Integer( currDate.getYear() + 1900 ).toString();
      sMonth= new Integer( currDate.getMonth() + 1 ).toString();
      if( sMonth.length()==1 )
        sMonth = "0" + sMonth;
      sDay = new Integer( currDate.getDate() ).toString();
      if( sDay.length()==1 )
        sDay = "0" + sDay;
      return sYear + "-" + sMonth + "-" + sDay;
    }

    /**
     * returns the date specified by date p_Date
     * in the mysql format yyyy-mm-dd
     *
     * @param p_Date the date
     */
    public static String getMySQLDate( Date p_Date ) {
      if( p_Date==null )
        return null;
      String sYear, sMonth, sDay;
      sYear = new Integer( p_Date.getYear() + 1900 ).toString();
      sMonth= new Integer( p_Date.getMonth() + 1 ).toString();
      if( sMonth.length()==1 )
        sMonth = "0" + sMonth;
      sDay = new Integer( p_Date.getDate() ).toString();
      if( sDay.length()==1 )
        sDay = "0" + sDay;
      return sYear + "-" + sMonth + "-" + sDay;
    }
}
