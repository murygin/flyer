package obj.fork.flyer.mag;

/*
 * FLYER.DE configuration
 */
public class MagConstants {

  /*
   * flyexible magazine navigation channel
   * thises get checked wether or not they have an article
   * behind it. makes up the dynamic navigation
   */

  // magazine other (new, travel, shopping)
  public static int[] MAGAZINE_OTH_CHANNEL =
  {
    1,11,12
  };

  // magazine spezial (contests, mode, polas, e-cards)
  public static int[] MAGAZINE_SPZ_CHANNEL =
  {
      15,4,13,14
  };


  // caching
  public static int MAGAZINE_NAV_TIMEOUT = 5000;

  //the id for public momos
  public static final int N_PUBLIC_MEMO_ID=2;

  /**
  * constanst for flyer events
  */
  /**
  * event caching timeout
  */
  public static final int N_EVENT_CACHING_TIMEOUT = 1800000;

  //arrays for the event type keywords
  public static String[] AS_NIGHTLIFE_KEYWORDS = { "nightlife", "livedjset", "gogodancing" };
  public static String[] AS_CONCERT_KEYWORDS = { "concerts" };
  public static String[] AS_THEATRE_KEYWORDS = { "theatre", "stage" };
  public static String[] AS_EXHIBITION_KEYWORDS = { "exhibitions" };
  public static String[] AS_MISC_KEYWORDS = { "misc" };

  public static final String S_EVENTMEMO_STYLESHEET = "view_memo.xsl";

  //the number of days events should be displayed an location detail page
  public static final int N_NUMBER_OF_DAYS = 14;

  //the number of displayed flyer presents events
  public static final int N_NUMBER_OF_FLYER_PRESENTS = 5;

  /**
  * constanst for flyer locations
  */
  /**
  * event caching timeout
  */
  public static final int N_LOCATION_CACHING_TIMEOUT = 1800000;

  //arrays for location type keywords
  public static final String[] AS_CLUBS_BARS_KEYWORDS = new String[]{ "clubs" };
  public static final String[] AS_VENUES_KEYWORDS = new String[]{ "venues" };
  public static final String[] AS_CINEMAS_KEYWORDS = new String[]{ "cinemas" };
  public static final String[] AS_STAGE_KEYWORDS = new String[]{ "stage" };
  public static final String[] AS_MUSEUM_KEYWORDS = new String[]{ "arts" };
  public static final String[] AS_FOOD_KEYWORDS = new String[]{ "food" };
  public static final String[] AS_SHOPPING_KEYWORDS = new String[]{ "shopping_cat" };

  public static final String S_LOCATIONMEMO_STYLESHEET = "view_memo.xsl";

  /*
   * image types
   */
  public final static String THUMBNAIL = "_th";
  public final static String REGULAR = "_rg";

}
