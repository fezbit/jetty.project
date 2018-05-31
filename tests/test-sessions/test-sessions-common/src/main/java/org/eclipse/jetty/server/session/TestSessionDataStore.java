//
//  ========================================================================
//  Copyright (c) 1995-2018 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//


package org.eclipse.jetty.server.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TestSessionDataStore
 *
 * Make a fake session data store (non clustered!) that creates a new SessionData object
 * every time load(id) is called.
 */
public class TestSessionDataStore extends AbstractSessionDataStore
{
    public Map<String,SessionData> _map = new HashMap<>();
    public boolean _passivating;

    
    public TestSessionDataStore ()
    {
        _passivating = false;
    }
    
    public TestSessionDataStore (boolean passivating)
    {
        _passivating = passivating;
    }

    @Override
    public boolean isPassivating()
    {
        return _passivating;
    }


    @Override
    public boolean exists(String id) throws Exception
    {
        return _map.containsKey(id);
    }


    @Override
    public SessionData load(String id) throws Exception
    {
        SessionData sd = _map.get(id);
        if (sd == null)
            return null;
        SessionData nsd = new SessionData(id,"","",System.currentTimeMillis(),System.currentTimeMillis(), System.currentTimeMillis(),0 );
        nsd.copy(sd);
        return nsd;
    }


    @Override
    public boolean delete(String id) throws Exception
    {
        return (_map.remove(id) != null);
    }


    @Override
    public void doStore(String id, SessionData data, long lastSaveTime) throws Exception
    {
        _map.put(id,  data);
    }

 
    @Override
    public Set<String> doGetExpired(Set<String> candidates)
    {
       HashSet<String> set = new HashSet<>();
        long now = System.currentTimeMillis();
        
       
        for (SessionData d:_map.values())
        {
            if (d.getExpiry() > 0 && d.getExpiry() <= now)
                set.add(d.getId());
        }
        return set;
    }

    @Override
    public Set<String> doGetOldExpired(long timeLimit)
    {
        Set<String> set =  new HashSet<>();
        
        for (SessionData d:_map.values())
        {
            if (d.getExpiry() > 0 && d.getExpiry() <= timeLimit)
                set.add(d.getId());
        }
        return set;
    }

    @Override
    public void cleanOrphans(long timeLimit)
    {
        //noop
    }
    
    
    
}
