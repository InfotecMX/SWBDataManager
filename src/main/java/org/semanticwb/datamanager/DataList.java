/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.semanticwb.datamanager;

import java.util.ArrayList;

/**
 *
 * @author javiersolis
 */
public class DataList<E> extends ArrayList<E>
{
    public String getString(int index) {
        Object obj=get(index);
        if(obj==null)return null;
        return obj.toString();
    }
    
public int getInt(int index)
    {
        Object obj=get(index);
        if(obj instanceof Integer)return (Integer)obj;
        try
        {
            return Integer.parseInt(getString(index));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    
    public long getLong(int index)
    {
        Object obj=get(index);
        if(obj instanceof Long)return (Long)obj;
        try
        {
            return Long.parseLong(getString(index));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }   
    
    public boolean getBoolean(int index)
    {
        Object obj=get(index);
        if(obj instanceof Boolean)return (Boolean)obj;
        try
        {
            return Boolean.parseBoolean(getString(index));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }          
    
}
