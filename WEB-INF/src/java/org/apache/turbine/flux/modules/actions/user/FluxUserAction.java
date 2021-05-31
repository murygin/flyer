package org.apache.turbine.flux.modules.actions.user;

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

import java.util.Date;

import org.apache.velocity.context.Context;

import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.UnknownEntityException;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.flux.modules.actions.FluxAction;

/**
 * Action to manage users in Turbine.
 * 
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 */
public class FluxUserAction extends FluxAction
{
    /**
     * ActionEvent responsible for inserting a new user
     * into the Turbine security system.
     */
    public void doInsert(RunData data, Context context)
        throws Exception
    {
        /*
         * Create a TamboraUser object here, it will be
         * used even if there is an error. It will be
         * fed back into the context to give the user
         * the chance to correct any errors.
         */
        User user = TurbineSecurity.getUserInstance();
        data.getParameters().setProperties(user);

        /*
         * Grab the username entered in the form.
         */
        String username = data.getParameters().getString("username");
        String password = data.getParameters().getString("password");

        if (password == null)
            password = "";

        /*
         * Make sure this account doesn't already exist.
         * If the account already exists then alert
         * the user and make them change the username.
         */
        if (TurbineSecurity.accountExists(username))
        {
            context.put("username", username);
            context.put("errorTemplate", "/screens/user/FluxUserAlreadyExists.vm");
            context.put("user", user);
            /*
             * We are still in insert mode. So keep this
             * value alive.
             */
            data.getParameters().add("mode", "insert");
            setTemplate(data, "/user/FluxUserForm.vm");
        }
        else
        {
            /*
             * Set some default date properties, this needs
             * to be more rigourous.
             */
            
            Date now = new Date();
            
            //user.setModifiedDate(now);
            user.setCreateDate(now);
            user.setLastLogin(new Date(0));
            
            TurbineSecurity.addUser(user, password);
        }
    }

    /**
     * ActionEvent responsible updating a user
     * in the Tambora system. Must check the input
     * for integrity before allowing the user info
     * to be update in the database.
     */
    public void doUpdate(RunData data, Context context)
        throws Exception
    {
        User user = TurbineSecurity.getUser(data.getParameters().getString("username"));
        data.getParameters().setProperties(user);

        Date now = new Date();

        //user.setModifiedDate(now);
        user.setCreateDate(now);
        user.setLastLogin(new Date(0));
        
        TurbineSecurity.saveUser(user);
    }

    /**
     * ActionEvent responsible for removing a user
     * from the Tambora system.
     */
    public void doDelete(RunData data, Context context)
        throws Exception
    {
        User user = TurbineSecurity.getUser(
            data.getParameters().getString("username"));
        
        TurbineSecurity.removeUser(user);
    }

    /**
     * Update the roles that are to assigned to a user
     * for a project.
     */
     public void doRoles(RunData data, Context context)
        throws Exception
     {
        /*
         * Get the user we are trying to update. The username
         * has been hidden in the form so we will grab the
         * hidden username and use that to retrieve the
         * user.
         */
        String username = data.getParameters().getString("username");
        User user = TurbineSecurity.getUser(username);

        AccessControlList acl = TurbineSecurity.getACL(user);

        /*
         * Grab all the Groups and Roles in the system.
         */
        Group[] groups = TurbineSecurity.getAllGroups().getGroupsArray();
        Role[] roles = TurbineSecurity.getAllRoles().getRolesArray();
     
        for (int i = 0; i < groups.length; i++)
        {
            String groupName = groups[i].getName();
            
            for (int j = 0; j < roles.length; j++)
            {
                /*
                 * In the UserRoleForm.vm we made a checkbox
                 * for every possible Group/Role combination
                 * so we will compare every possible combination
                 * with the values that were checked off in
                 * the form. If we have a match then we will
                 * grant the user the role in the group.
                 */
                String roleName = roles[j].getName();
                String groupRole = groupName + roleName;
                
                String formGroupRole = data.getParameters().getString(groupRole);
                
                if ( formGroupRole != null && !acl.hasRole(roles[j], groups[i]))
                {
                    TurbineSecurity.grant(user, groups[i], roles[j]);
                }                    
                else if (formGroupRole == null && acl.hasRole(roles[j], groups[i]))
                {
                    TurbineSecurity.revoke(user, groups[i], roles[j]);
                }                    
            }
        }
     }

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
        System.out.println("Running do perform!");
        data.setMessage("Can't find the requested action!");
    }
}
