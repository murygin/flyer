package org.apache.turbine.flux.modules.actions;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.velocity.context.Context;

import org.apache.turbine.modules.actions.VelocityAction;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;

/**
 * General Flux action.
 * 
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 */
public class FluxAction extends VelocityAction
{
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
     * This currently only checks to make sure that user is allowed to
     * view the storage area.  If you create an action that requires more
     * security then override this method.
     *
     * @param data Turbine information.
     * @return True if the user is authorized to access the screen.
     * @exception Exception, a generic exception.
     */
    protected boolean isAuthorized( RunData data ) throws Exception
    {
        boolean isAuthorized = false;

        AccessControlList acl = (AccessControlList)
                data.getSession().getValue(AccessControlList.SESSION_KEY);

        /*
         * Grab the Flux Admin role listed in the Flux.properties
         * file that is included in the the standard
         * TurbineResources.properties file.
         */
         String fluxAdminRole = TurbineResources.getString("flux.admin.role");

        /*
         * This should be taken from a config file, this
         * hard coding is no good for widespread use
         * of the admin app. I think a permissions file
         * would be better for the whole system.
         */
        if (acl==null || ! acl.hasRole(fluxAdminRole))
        {
            isAuthorized = false;
        }
        else if(acl.hasPermission(fluxAdminRole))
        {
            isAuthorized = true;
        }

        return isAuthorized;
    }
}
