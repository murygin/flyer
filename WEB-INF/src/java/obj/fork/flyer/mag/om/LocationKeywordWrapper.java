package obj.fork.flyer.mag.om;

import java.util.Vector;

import obj.fork.laika.om.Keyword;
import obj.fork.laika.om.Location;
import obj.fork.laika.om.Event;

import obj.fork.flyer.mag.om.ExtEvent;

/**
 * class to hold location, types and styles in one object
 *
 * @author <a href="mailto:schmidt@fork.de">Daniel Schmidt</a>
 */
public class LocationKeywordWrapper {

    /**
     * the vector for the location objects
     */
    private Location m_Location;

    /**
     * the vector for the events of the location
     */
    private Vector m_vecEvents;

    /**
     * the vector for the keyword styles
     */
    private Vector m_vecStyles;

    /**
     * the vector for the keyword types
     */
    private Vector m_vecTypes;

    /**
     * the memo string of the location
     */
    private String m_sMemo;

    //______________________________________________________________________________

    /**
     * constructs a new LocationKeyword
     */
    public LocationKeywordWrapper() {
        m_vecTypes = new Vector();
        m_vecStyles = new Vector();
    }

    /**
     * constructs a new LocationKeyword
     */
    public LocationKeywordWrapper(
        Location p_Location,
        Vector p_vecEvents,
        Vector p_vecKeywords,
        String p_sMemo )
    {
        m_Location = p_Location;
        m_vecEvents = new Vector();
        if( p_vecEvents!=null ) {
          for( int i=0; i<p_vecEvents.size(); i++ ) {
            m_vecEvents.add( new ExtEvent((Event)p_vecEvents.get(i)) );
          }
        }
        m_vecTypes = new Vector();
        m_vecStyles = new Vector();
        m_sMemo = p_sMemo;
        for( int i=0; i<p_vecKeywords.size(); i++ ) {
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "type" ) )
                m_vecTypes.add( (Keyword)p_vecKeywords.get(i) );
            if( ((Keyword)p_vecKeywords.get(i)).getCharacterId().equals( "style" ) )
                m_vecStyles.add( (Keyword)p_vecKeywords.get(i) );
        }
    }


    //______________________________________________________________________________

    public Location getLocation() {
        return m_Location;
    }
    public void setLocation( Location p_Location ) {
        m_Location = p_Location;
    }

    public Vector getEvents() {
        return m_vecEvents;
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

    public String getMemo() {
        return m_sMemo;
    }
}
