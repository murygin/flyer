package obj.fork.flyer.mag.modules.actions;

// jdk
import java.util.Vector;
import java.util.Hashtable;

// turbine
import org.apache.velocity.context.Context;

import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.om.NumberKey;

import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.TurbineSecurityException;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.om.security.peer.GroupPeer;

import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.Log;
import org.apache.turbine.TurbineConstants;

// jsdk 
import javax.servlet.http.HttpServletResponse;
// laika
import obj.fork.laika.om.*;

/*
 * Magazine Login
 * After the User choose an edition
 * this action sets the active issue (edition)
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>#
 */
public class MagLogin extends VelocityAction
{
    /*
     * NOTE:
     * change this to published issues later
     * currently this marks open issues
     */
    private final int PUBLIC_ISSUE = 1;

    public void doPerform( RunData data, Context context )
        throws Exception
    {
        String edition_id = data.getParameters().getString("edition_id", "-1");
        User user = null;
        try
        {
            // Authenticate the user and get the object.
            //user = TurbineSecurity.getAnonymousUser();

            user = TurbineSecurity.getUser("Visitor");

            // set has logged in
            user.setHasLoggedIn(new Boolean(true));

            // Set the last_login date in the database.
            user.updateLastLogin();

            // Store the user object.
            data.setUser(user);

            AccessControlList acl = data.getACL();

            if ( acl == null )
            {
                acl = TurbineSecurity.getACL( data.getUser() );
                data.getSession().setAttribute( AccessControlList.SESSION_KEY,
                                                (Object)acl );
            }
            
            data.setACL(acl);            
            data.save();

            data.setMessage(TurbineResources.getString("login.welcome"));

        }
        catch ( Exception e )
        {
            Log.error(
                "This happens when logging in: "
                + e.getMessage()
                );

            e.printStackTrace();

        }

        Issue activeIssue;
        Edition activeEdition;
        try
        {
            /*
             * store the selectd edition
             */
            activeEdition = EditionPeer.retrieveByPK(
                new NumberKey( edition_id )
                );

            /*
             * retireve the latest issue
             * ans store it in session
             */
            activeIssue = getLatestIssue(
                activeEdition.getEditionId().toString()
                );

        }
        catch(Exception e)
        {
            Log.error(
                "An error occured retrieving activeEditon and latestIssue: "
                + e.getMessage()
                );

            HttpServletResponse response = data.getResponse();

            String msg = "There+is+no+Issue+available+for+this+Edition";
            String uri = "http://"+data.getServerName()+":8080/mag/servlet/mag/template/Entrance.vm?msg="+msg;

            String redir = response.encodeRedirectURL(uri);
            try
            {
                response.sendRedirect(redir);
                response.setStatus(302);
            }
            catch(java.io.IOException io)
            {
                Log.error(
                    "Unable to send redirect: "
                    + io.getMessage()
                    );
            }

            return;

        }

        /*
         * put both items into session
         */
        data.getSession().setAttribute(
            "activeEdition",
            activeEdition
            );

        data.getSession().setAttribute(
            "activeIssue",
            activeIssue
            );

    }

    /*
     * get the latest Issue available
     */
    private Issue getLatestIssue(String edition_id)
        throws Exception
    {
        // frist get all groups for this edition
        Criteria crit = new Criteria();
        crit.addJoin(EditionPeer.EDITION_ID, EditionTurbineGroupPeer.EDITION_ID);
        crit.add(EditionPeer.EDITION_ID, edition_id);
        crit.setDistinct();

        Vector groups = EditionTurbineGroupPeer.doSelect(crit);

        if(groups.isEmpty())
            throw new Exception("The selected Edition doesnt belong to a group ");

        Vector groupNames = new Vector();

        for(int i=0; i<groups.size(); i++)
        {
            EditionTurbineGroup current = (EditionTurbineGroup)groups.elementAt(i);
            groupNames.add(
                current.getGroupName()
                );
        }

        /*
         * get Issues
         * for developement we select the latest open issue
         */

        Criteria issueCrit = new Criteria();
        issueCrit.addIn(IssuePeer.GROUP_NAME, groupNames);

        issueCrit.add(
            IssuePeer.STATUS,
            PUBLIC_ISSUE
            );

        issueCrit.addAscendingOrderByColumn(IssuePeer.DATE_START);
        issueCrit.setDistinct();

        Vector issues = IssuePeer.doSelect(issueCrit);

        if(!issues.isEmpty())
        {
            return (Issue)issues.elementAt(0);
        }
        else
        {
            throw new Exception(
                "There is no Issue available for edition with id "
                + edition_id
                );
        }
    }
}
