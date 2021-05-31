package obj.fork.flyer.mag.util;

//jdk
import java.util.Vector;

// laika
import obj.fork.laika.om.*;

/*
 * ItemPresenation
 * 
 * wrapper class to bundle an om.Article, 
 * its related images and subjects in one class.
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: ItemPresentation.java,v 1.3 2001/09/10 13:31:24 heiko Exp $
 */
public class ItemPresentation
{
    // om.Article
    private Article article;

    // image thumbnails
    private Vector thumbnails = null;

    // regular images 
    private Vector regulars = null;

    private String channel_id;
   
    /*
     * constr. without images
     */
    public ItemPresentation(Article art)
        throws Exception
    {
        // the article
        this.article = art;

        setChannelId(article);
    }

    /*
     * constr. with thumbs 
     */
    public ItemPresentation(Article art, Vector thumbs)
        throws Exception
    {
        this.article = art;
        this.thumbnails = thumbs;

        setChannelId(article);
    }

    /*
     * constr. with thumbs and regulars
     */
    public ItemPresentation(Article art, Vector thumbs, Vector regs)
        throws Exception
    {
        this.article = art;
        this.thumbnails = thumbs;
        this.regulars = regs;

        setChannelId(this.article);
    }

    /*
     * add thumbnails
     */
    public void setThumbnails(Vector v)
    {
        this.thumbnails = v;
    }

    /*
     * add regulars
     */
    public void setRegulars(Vector v)
    {
        this.regulars = v;
    }

    /*
     * get thumbnails
     */
    public Vector getThumbnails()
    {
        return thumbnails;
    }

    /*
     * get the frist thumbnail
     */
    public Articleimage getThumbnail()
    {
        if(hasThumbnails())
            return (Articleimage)thumbnails.firstElement();

        return null;
    }

    /*
     * add regulars
     */
    public Vector getRegulars()
    {
        return regulars;
    }

    /*
     * get the frist regular
     */
    public Articleimage getRegular()
    {
        if(hasRegulars())
            return (Articleimage)regulars.firstElement();

        return null;
    }

    /*
     * get the underlying article
     */
    public Article getArticle()
    {
        return article;
    }

    /*
     * set the chanelId
     */
    private void setChannelId(Article a)
        throws Exception
    {
        ArticleChannel ac = (ArticleChannel)a.getArticleChannels().elementAt(0);
        this.channel_id = ac.getChannelId().toString();
    }

    public String getChannelId()
    {
        return channel_id;
    }

    /*
     * does it have thumbs?
     */
    public boolean hasThumbnails()
    {
        return hasImages(thumbnails);
    }

    /*
     * does it have regulars?
     */
    public boolean hasRegulars()
    {
        return hasImages(regulars);
    }

    /*
     * does it have images?
     */
    private boolean hasImages(Vector images)
    {
        if(images != null)
            return ! images.isEmpty();
        else
            return false;
    }
}
