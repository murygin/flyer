package obj.fork.flyer.mag.modules.actions;

//turbine
import org.apache.turbine.modules.actions.VelocitySecureAction;
import org.apache.turbine.util.RunData;

//velocity
import org.apache.velocity.context.Context;

/**
 * class PublicAction is the baseclass for all public action
 * no login is necessary to perform this action
 */
public class PublicAction extends VelocitySecureAction {

  /**
     * Implement this to add information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    public void doPerform( RunData data,Context context )
        throws Exception
    {
    }

    /**
     * returns true because everybody is alowed to perform PublicAction.
     *
     * @param data Turbine information.
     * @return true.
     * @exception Exception, a generic exception.
     */
    protected boolean isAuthorized( RunData data ) throws Exception {
      return true;
    }
}