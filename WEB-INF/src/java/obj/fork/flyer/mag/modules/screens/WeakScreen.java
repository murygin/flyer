package obj.fork.flyer.mag.modules.screens;

// jdk
import java.util.Vector;

// tdk
import org.apache.turbine.modules.screens.VelocitySecureScreen;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.Log;

import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.peer.UserGroupRolePeer;
import org.apache.turbine.om.security.peer.TurbineUserPeer;
import org.apache.turbine.om.security.peer.GroupPeer;
import org.apache.turbine.om.security.peer.RolePeer;
import org.apache.turbine.om.security.TurbineGroup;

import org.apache.turbine.util.db.Criteria;
import org.apache.velocity.context.Context;

import obj.fork.laika.om.Issue;

public class WeakScreen extends VelocitySecureScreen
{
    public void doBuildTemplate(RunData data, Context context)
        throws Exception
    {
    }

    protected boolean isAuthorized( RunData data )  throws Exception
    {
        boolean isAuthorized = true;
        boolean hasIssue = false;

        Issue issue;
        try
        {
            issue = (Issue)data.getSession().getAttribute("activeIssue");

            if(issue != null)
                hasIssue = true;

            Log.debug("issue obj " + issue);
            Log.debug("hasIssue " + hasIssue);
        }
        catch(Exception e)
        {
            // ignore, the session will be reseted
        }

        AccessControlList acl = data.getACL();

        if (acl==null || !hasIssue)
        {
            Log.debug("Calling reset...");

            data.setScreenTemplate(
                TurbineResources.getString("template.resetframes")
                );
        }
        return isAuthorized;
    }
}
