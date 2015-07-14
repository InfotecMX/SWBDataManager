/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.semanticwb.datamanager;

import java.util.HashMap;
import java.util.Iterator;
import org.semanticwb.datamanager.datastore.DataStoreMongo;

/**
 *
 * @author javiersolis
 */
public class DataObject extends HashMap<String, Object>
{
    public String getString(String key)
    {
        Object obj=get(key);
        if(obj==null)return null;
        return obj.toString();
    }
    
    public int getInt(String key)
    {
        Object obj=get(key);
        if(obj instanceof Integer)return (Integer)obj;
        try
        {
            return Integer.parseInt(getString(key));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    
    public long getLong(String key)
    {
        Object obj=get(key);
        if(obj instanceof Long)return (Long)obj;
        try
        {
            return Long.parseLong(getString(key));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }   
    
    public boolean getBoolean(String key)
    {
        Object obj=get(key);
        if(obj instanceof Boolean)return (Boolean)obj;
        try
        {
            return Boolean.parseBoolean(getString(key));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }      
    
    public static Object parseJSON(String json)
    {
        return DataStoreMongo.parseJSON(json);
    }
    
    public String toString() 
    {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }    
    
}
