package obj.fork.flyer.mag.om;

import java.util.Vector;
import java.util.Date;

import obj.fork.laika.om.Location;
import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.Event;
import obj.fork.flyer.mag.om.ExtEvent;
import obj.fork.flyer.mag.util.ItemPresentation;

/**
 * class to hold event, location, types and styles in one object
 *
 * @author <a href="mailto:schmidt@fork.de">Daniel Schmidt</a>
 */
public class EventKeywordWrapper {

    /**
     * the vector for the location objects
     */
    private ExtEvent m_Event;

    /**
     * the vector for the location objects
     */
    private Location m_Location;

    /**
     * the vector for the artists
     */
    private Vector m_vecArtists;

    /**
     * the vector for the keyword styles
     */
    private Vector m_vecStyles;

    /**
     * the vector for the keyword types
     */
    private Vector m_vecTypes;

    /**
     * the article of the event
     */
    private ItemPresentation m_Item;

    //______________________________________________________________________________

    /**
     * constructs a new EventKeyword
     */
    public EventKeywordWrapper() {
        m_vecTypes = new Vector();
        m_vecStyles = new Vector();
    }

    /**
     * constructs a new EventKeyword
     */
    public EventKeywordWrapper( Event p_Event, Vector p_vecKeywords ) {
        m_Event = new ExtEvent( p_Event );
        //setBrdDate( m_Event.getBegin() );

        m_vecTypes = new Vector();
        m_vecStyles = new Vector();
        for( int i=0; i<p_vecKeywords.size(); i++ ) {
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "type" ) )
                m_vecTypes.add( (Keyword)p_vecKeywords.get(i) );
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "style" ) )
                m_vecStyles.add( (Keyword)p_vecKeywords.get(i) );
        }
    }

    /**
     * constructs a new EventKeyword
     */
    public EventKeywordWrapper(
        Event p_Event,
        Location p_Location,
        Vector p_vecKeywords )
    {
        m_Event = new ExtEvent( p_Event );
        m_Location = p_Location;
        m_vecArtists = new Vector();
        m_vecTypes = new Vector();
        m_vecStyles = new Vector();
        for( int i=0; i<p_vecKeywords.size(); i++ ) {
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "type" ) )
                m_vecTypes.add( (Keyword)p_vecKeywords.get(i) );
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "style" ) )
                m_vecStyles.add( (Keyword)p_vecKeywords.get(i) );
        }
    }

    /**
     * constructs a new EventKeyword
     */
    public EventKeywordWrapper(
        Event p_Event,
        Location p_Location,
        Vector p_vecArtists,
        Vector p_vecKeywords,
        ItemPresentation p_Item )
    {
        m_Event = new ExtEvent( p_Event );
        m_Location = p_Location;
        m_vecArtists = p_vecArtists;
        m_vecTypes = new Vector();
        m_vecStyles = new Vector();
        m_Item = p_Item;
        for( int i=0; i<p_vecKeywords.size(); i++ ) {
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "type" ) )
                m_vecTypes.add( (Keyword)p_vecKeywords.get(i) );
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "style" ) )
                m_vecStyles.add( (Keyword)p_vecKeywords.get(i) );
        }
    }

    //______________________________________________________________________________

    public ExtEvent getEvent() {
      return m_Event;
    }
    public void setEvent( Event p_Event ) {
      m_Event = (ExtEvent) p_Event;
    }

    public Location getLocation() {
        return m_Location;
    }
    public void setLocation( Location p_Location ) {
        m_Location = p_Location;
    }

    public Vector getArtists() {
        return m_vecArtists;
    }
    public void setArtists( Vector p_vecArtists) {
        m_vecArtists = p_vecArtists;
    }

    public Vector getTypes() {
        return m_vecTypes;
    }
    public void setTypes( Vector p_vecTypes) {
        m_vecTypes = p_vecTypes;
    }

    public Vector getStyles() {
        return m_vecStyles;
    }
    public void setStyles( Vector p_vecStyles) {
        m_vecStyles = p_vecStyles;
    }

    public ItemPresentation getItem() {
        return m_Item;
    }
    public void setItem( ItemPresentation p_Item ) {
        m_Item = p_Item;
    }
}
