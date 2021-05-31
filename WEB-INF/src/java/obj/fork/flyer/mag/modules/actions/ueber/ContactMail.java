package obj.fork.flyer.mag.modules.actions.ueber;

// turbine
import org.apache.velocity.context.Context;

import org.apache.turbine.modules.actions.VelocityAction;
import org.apache.turbine.util.RunData;

import org.apache.turbine.util.Log;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.util.velocity.VelocityEmail;
import org.apache.turbine.services.resources.TurbineResources;

/*
 * Basic Contact Mail
 *
 * @author <a href="mailto:heiko@fork.de">Heiko Braun</a>
 * @version: $Id: ContactMail.java,v 1.1 2001/09/10 16:44:04 heiko Exp $
 */
public class ContactMail extends VelocityAction
{

    public void doPerform( RunData data, Context context )
        throws Exception
    {
        String sender = data.getParameters().getString("sender", "user@anonymous.net");
        String subject = data.getParameters().getString("subject", "No Subject choosen");
        String body = data.getParameters().getString("msgbody", "nobody");

        if(!body.equals("nobody"))
        {
            try
            {
                context.put("msgbody", body);

                VelocityEmail ve = new VelocityEmail();
                ve.setTo(
                    "Information", 
                    TurbineResources.getString("mail.contact")
                    );
                ve.setFrom("Webcontact", sender).setSubject(subject);
                ve.setContext(context);
                ve.setTemplate("/email/Contact.vm");
                ve.setWordWrap(72);
                ve.send();
            }
            catch(Exception e)
            {
                Log.error(
                    "Unable to process Contact mail: "
                    +e.getMessage()
                    );
            }
        }
    }
}
