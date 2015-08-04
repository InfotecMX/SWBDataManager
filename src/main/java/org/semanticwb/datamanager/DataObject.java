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
    public DataObject getDataObject(String key)
    {
        Object obj=get(key);
        if(obj instanceof DataObject)return (DataObject)obj;  
        return null;
    }
    
    public DataList getDataList(String key)
    {
        Object obj=get(key);
        if(obj instanceof DataList)return (DataList)obj;  
        return null;
    }    
    
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
        Iterator<Entry<String, Object>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<String, Object> e = i.next();
            String key = e.getKey();
            Object value = e.getValue();
            sb.append("\""+key+"\"");
            sb.append(':');
            if(value instanceof String)
                sb.append(value == this ? "(this Map)" : "\""+value+"\"");
            else
                sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }  
    
    public String getId()
    {
        return getString("_id");
    }
    
    public String getNumId()
    {
        String id=getId();
        return id.substring(id.lastIndexOf(":")+1);
    }
    
}
