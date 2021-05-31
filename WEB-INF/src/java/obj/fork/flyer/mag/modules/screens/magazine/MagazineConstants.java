package obj.fork.flyer.mag.modules.screens.magazine;

/*
 * a collection of constants used throughout the magazine
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: MagazineConstants.java,v 1.7 2001/09/10 13:30:28 heiko Exp $
 */
public class MagazineConstants 
{
    /*
     * CHANNEL AGGREGATION
     * specify channel which belong to one group
     */

    /*
     * "themen" (article,interviews,mode,galeries,theater)
     * a collection of channel_id's
     * usaly theres mode aswell, today we skip it
     */
    public final static int[] CHANNEL_THEMEN = {
        2,3,5,6
    };

    /*
     * "besprechungen" (music,movies,books,games) 
     * a collection of channel_id's
     */
    public final static int[] CHANNEL_BESPRECHUNGEN = {
        7,8,9,10
    };

    /*
     * "spezial" (polaroids,ecards,contests) 
     * a collection of channel_id's
     */
    public final static int[] CHANNEL_SPEZIAL = {
        13,14,15
    };

     /*
     * "spezial" (polaroids,ecards,contests) 
     * a collection of channel_id's
     */
    public final static int[] CHANNEL_OTHER = {
        11,12
    };


    /*
     * article types
     */
    public final static int REGULAR = 1;
    public final static int OPENER = 2;
    public final static int TIP = 3;

    /*
     * important channel
     */
    public final static int NEWS_CHANNEL = 1;

    /*
     * article status information
     */
    public final static int APPROVED = 7;

    /*
     * caching options
     */

    // the article which is shown in ChanelDetail.java
    public final static int ACTIVE_ARTICLE_TIMEOUT = 5000;
    // the remaining items from same channel teased 
    public final static int REMAINING_ITEMS_TIMEOUT = 5000;


    /*
     * xml/xsl options
     */

    // thw stylesheet used to display article
    public final static String VIEW_ARTICLE_XSL = "view_article.xsl";  

    /*
     * channel aggregation
     */

     // Themen config
    public final static int ARTICLE= 2;
    public final static int INTERVIEWS = 3;
    public final static int MODE = 4;
    public final static int GALERIES = 5;
    public final static int THEATER = 6;

    // Besprechungen config
    public final static int MUSIC = 7;
    public final static int MOVIES = 8;
    public final static int BOOKS = 9;
    public final static int GAMES = 10;

      // Spezial config
    public final static int SHOPPING = 11;
    public final static int TRAVEL = 12;
    public final static int POLAROIDS = 13;
    public final static int ECARDS = 14;
    public final static int CONTESTS = 15;


    /*
     * quick nav timeout
     */

    public final static int MAGNAV_TIMEOUT = 5000;
}
