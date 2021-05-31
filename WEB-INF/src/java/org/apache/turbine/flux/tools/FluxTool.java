package org.apache.turbine.flux.tools;

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

import java.util.Vector;
import java.util.Iterator;

import org.apache.velocity.context.Context;

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.User;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.om.security.peer.TurbineUserPeer;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.db.Criteria;
import org.apache.turbine.util.template.SelectorBox;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.pool.Recyclable;
import org.apache.turbine.util.Log;

/**
 * The pull api for flux templates
 *
 * @author <a href="mailto:jmcnally@apache.org">John D. McNally</a>
 */
public class FluxTool 
    implements Recyclable, ApplicationTool
{
    /** 
     * The object containing request specific data 
     */
    private RunData data;

    /**
     * A Group object for use within the Flux API.
     */
    private Group group = null;

    /**
     * A Issue object for use within the Flux API.
     */
    private Role role = null;

    /**
     * A Permission object for use within the Flux API.
     */
    private Permission permission = null;

    /**
     * A User object for use within the Flux API.
     */
    private User user = null;

    
    public void init(Object data)
    {
        this.data = (RunData)data;
    }

    /**
     * nulls out the issue and user objects
     */
    public void refresh()
    {
        // do not need since it is a request tool
    }

    /**
     * Constructor does initialization stuff
     */    
    public FluxTool()
    {
    }


    public Group getGroup()
        throws Exception
    {
        if (group == null)
        {
            String name = data.getParameters().getString("name"); 
            if ( name == null || name.length() == 0)
            {
                group = TurbineSecurity.getNewGroup(null);
            }
            else 
            {
                group = TurbineSecurity.getGroup(name);
            }
        }
        return group;
    }

    public String getMode()
    {
        return data.getParameters().getString("mode");
    }

    public Group[] getGroups()
        throws Exception
    { 
        return TurbineSecurity.getAllGroups().getGroupsArray();
    }

    public Role getRole()
        throws Exception
    {
        if (role == null)
        {
            String name = data.getParameters().getString("name"); 
            if ( name == null || name.length() == 0)
            {
                role = TurbineSecurity.getNewRole(null);  
            }
            else 
            {
                role = TurbineSecurity.getRole(name);
            }
        }
        return role;
    }

    /**
     */
    public Role[] getRoles()
        throws Exception
    {
        Criteria criteria = new Criteria();
        System.out.println("Getting roles");
        System.out.println("roles.size=" + TurbineSecurity.getRoles(criteria).getRolesArray().length);
        return TurbineSecurity.getRoles(criteria).getRolesArray();
    }

    public Permission getPermission()
        throws Exception
    {
        if (permission == null)
        {
            String name = data.getParameters().getString("name"); 
            if ( name == null || name.length() == 0)
            {
                permission = TurbineSecurity.getNewPermission(null);  
            }
            else 
            {
                permission = TurbineSecurity.getPermission(name);
            }
        }
        return permission;
    }

    /**
     * Get all permissions.
     */
    public Permission[] getPermissions()
        throws Exception
    {
        return TurbineSecurity.getAllPermissions().getPermissionsArray();
    }

    public User getUser()
        throws Exception
    {
        if (user == null)
        {
            String name = data.getParameters().getString("username"); 
            if ( name == null || name.length() == 0)
            {
                user = TurbineSecurity.getUserInstance();  
            }
            else 
            {
                user = TurbineSecurity.getUser(name);
            }
        }
        return user;
    }

    public AccessControlList getACL()
        throws Exception
    {
        return  TurbineSecurity.getACL(getUser());
    }



    /**
     */
    public SelectorBox getFieldList() throws Exception
    {
        Object[] names = {"username", "firstname", "middlename", "lastname"}; 
        Object[] values = 
            { "Username", "First Name", "Middle Name", "Last Name" };
        return  new SelectorBox("fieldList", names, values);
    }

    /**
     */
    public SelectorBox getUserFieldList() 
        throws Exception
    {
        /**
         * This is a tie to the DB implementation
         * something should be added the pluggable pieces
         * to allow decent parameterized searching.
         */
    
        Object[] names = 
        { 
            TurbineUserPeer.USERNAME, 
            TurbineUserPeer.FIRST_NAME, 
            TurbineUserPeer.LAST_NAME 
        }; 
        
        Object[] values = 
        { 
            "User Name" , 
            "First Name", 
            "Last Name" 
        };

        return  new SelectorBox("fieldList", names, values);
    }

    /**
     * Select all the users and place them in an array
     * that can be used within the UserList.vm template.
     */
    public User[] getUsers() 
        throws Exception
    {
        Criteria criteria = new Criteria();
        
        String fieldList = data.getParameters().getString("fieldList");
        
        if (fieldList != null)
        {
            /*
             * This is completely database centric.
             */
            String searchField = data.getParameters().getString("searchField");
            
            /*
             * Does this tie this admin app to a database
             * implementation? And our implementation at
             * that?
             */
            
            /*
             * How the hell do you make a where name like 'j%'
             * type statement with Criteria?
             */
            criteria.add(fieldList, (Object) searchField, Criteria.LIKE);
        }
        
        return TurbineSecurity.getUsers(criteria);
    }

    // ****************** Recyclable implementation ************************

    private boolean disposed;

    /**
     * Recycles the object for a new client. Recycle methods with
     * parameters must be added to implementing object and they will be
     * automatically called by pool implementations when the object is
     * taken from the pool for a new client. The parameters must
     * correspond to the parameters of the constructors of the object.
     * For new objects, constructors can call their corresponding recycle
     * methods whenever applicable.
     * The recycle methods must call their super.
     */
    public void recycle()
    {
        disposed = false;
    }

    /**
     * Disposes the object after use. The method is called
     * when the object is returned to its pool.
     * The dispose method must call its super.
     */
    public void dispose()
    {
        data = null;
        user = null;
        group = null;
        role = null;
        permission = null;

        disposed = true;
    }

    /**
     * Checks whether the recyclable has been disposed.
     * @return true, if the recyclable is disposed.
     */
    public boolean isDisposed()
    {
        return disposed;
    }
}
