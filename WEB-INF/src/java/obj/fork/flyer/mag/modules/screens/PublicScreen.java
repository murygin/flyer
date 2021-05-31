package obj.fork.flyer.mag.modules.screens;

import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.velocity.context.Context;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.Log;

import obj.fork.laika.om.Issue;
import obj.fork.laika.om.Edition;

/*
 * base class for lal screens which 
 * can be viewed without being authenticated
 */
public class PublicScreen extends VelocityScreen    
{

    //nothing to check that the User 
    //can view this screen   
    protected void doBuildTemplate( RunData data, Context context ) 
        throws Exception
    {
        //call to Super
        super.doBuildTemplate(data, context);
    }
}
