package org.apache.turbine.flux.modules.actions.role;

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

import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.UnknownEntityException;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.flux.modules.actions.FluxAction;

/**
 * Action to manager roles in Turbine.
 * 
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 */
public class FluxRoleAction extends FluxAction
{
    public void doInsert(RunData data, Context context)
        throws Exception
    {
        //Role role = new Role();
        Role role = TurbineSecurity.getNewRole(null);
        data.getParameters().setProperties(role);

        String name = data.getParameters().getString("name");

        try
        {
            TurbineSecurity.addRole(role);
        }
        catch (EntityExistsException eee)
        {
            context.put("name", name);
            context.put("errorTemplate", "/screens/role/FluxRoleAlreadyExists.vm");
            context.put("role", role);
            /*
             * We are still in insert mode. So keep this
             * value alive.
             */
            data.getParameters().add("mode", "insert");
            setTemplate(data, "/role/FluxRoleForm.vm");
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
        Role role = TurbineSecurity.getRole(
            data.getParameters().getString("name"));
        
        data.getParameters().setProperties(role);
        
        try
        {
            TurbineSecurity.saveRole(role);
        }
        catch (UnknownEntityException uee)
        {
            /*
             * Should do something here but I still
             * think we should use the an id so that
             * this can't happen.
             */
        }
    }

    /**
     * ActionEvent responsible for removing a user
     * from the Tambora system.
     */
    public void doDelete(RunData data, Context context)
        throws Exception
    {
        Role role = TurbineSecurity.getRole(
            data.getParameters().getString("name"));

        try
        {
            TurbineSecurity.removeRole(role);
        }
        catch (UnknownEntityException uee)
        {
            /*
             * Should do something here but I still
             * think we should use the an id so that
             * this can't happen.
             */
        }
    }

    /**
     * Update the roles that are to assigned to a user
     * for a project.
     */
     public void doPermissions(RunData data, Context context)
        throws Exception
     {
        /*
         * Grab the role we are trying to update.
         */
        String name = data.getParameters().getString("name");
        Role role = TurbineSecurity.getRole(name);

        /*
         * Grab the permissions for the role we are
         * dealing with.
         */
        PermissionSet rolePermissions = role.getPermissions();

        /*
         * Grab all the permissions.
         */
        Permission[] permissions = TurbineSecurity.getAllPermissions()
            .getPermissionsArray();
        
        String roleName = role.getName();
        
        for (int i = 0; i < permissions.length; i++)
        {
            String permissionName = permissions[i].getName();
            String rolePermission = roleName + permissionName;
        
            String formRolePermission = data.getParameters().getString(rolePermission);
            Permission permission = TurbineSecurity.getPermission(permissionName);
            
            
            if (formRolePermission != null && !rolePermissions.contains(permission))
            {
                /*
                 * Checkbox has been checked AND the role doesn't already
                 * contain this permission. So assign the permission to
                 * the role.
                 */
                 
                System.out.println("adding " + permissionName + " to " + roleName); 
                role.grant(permission);
            }
            else if (formRolePermission == null && rolePermissions.contains(permission))
            {
                /*
                 * Checkbox has not been checked AND the role
                 * contains this permission. So remove this
                 * permission from the role.
                 */
                System.out.println("removing " + permissionName + " from " + roleName);
                role.revoke(permission);
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
