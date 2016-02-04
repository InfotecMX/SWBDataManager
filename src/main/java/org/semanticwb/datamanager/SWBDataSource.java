/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.semanticwb.datamanager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.semanticwb.datamanager.datastore.SWBDataStore;
import org.semanticwb.datamanager.script.ScriptObject;

/**
 *
 * @author javier.solis
 */
public class SWBDataSource 
{
    public static final String ACTION_FETCH="fetch";
    public static final String ACTION_UPDATE="update";
    public static final String ACTION_ADD="add";
    public static final String ACTION_REMOVE="remove";
    public static final String ACTION_VALIDATE="validate";
    public static final String ACTION_LOGIN="login";
    public static final String ACTION_LOGOUT="logout";
    public static final String ACTION_USER="user";
    
    private String name=null;
    private String dataStoreName=null;
    private SWBScriptEngine engine=null;
    private ScriptObject script=null;
    private SWBDataStore db=null;
    
    private HashMap<String,DataObject> cache=new HashMap();

    protected SWBDataSource(String name, ScriptObject script, SWBScriptEngine engine)
    {
        this.name=name;
        this.engine=engine;
        this.script=script;        
        dataStoreName=this.script.getString("dataStore");
        this.db=engine.getDataStore(dataStoreName);        
        if(this.db==null)throw new NoSuchFieldError("DataStore not found:"+dataStoreName);
    }

    /**
     * Regresa Nombre del DataSource
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Regresa el SWBScriptEngine que contiene a este DataSource
     * @return SWBScriptEngine
     */
    public SWBScriptEngine getScriptEngine() {
        return engine;
    }
    
    /**
     * Regresa ScriptObject con el script con la definición del datasource definida el el archivo js
     * @return ScriptObject
     */
    public ScriptObject getDataSourceScript()
    {
        return script;
    }      
    
    public DataObject fetch() throws IOException
    {
        return fetch(new DataObject());
    }    
    
//    public DataObject fetch(String query) throws IOException
//    {
//        return fetch((DataObject)JSON.parse(query));
//    }
    
    public DataObject fetch(DataObject json) throws IOException
    {
        DataObject req=engine.invokeDataProcessors(name, SWBDataSource.ACTION_FETCH, SWBDataProcessor.METHOD_REQUEST, json);
        DataObject res=db.fetch(req,this);
        res=engine.invokeDataProcessors(name, SWBDataSource.ACTION_FETCH, SWBDataProcessor.METHOD_RESPONSE, res);
        engine.invokeDataServices(name, SWBDataSource.ACTION_FETCH, req, res);
        return res;
    }
    
    public DataObject fetch(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return fetch(DataUtils.toDataObject(json));
    }

    public DataObject addObj(DataObject obj) throws IOException
    {
        DataObject ret=null;
        DataObject req=new DataObject();
        req.put("data", obj);        
        ret=add(req);
        return ret;
    }  
    
    public DataObject addObj(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return addObj(DataUtils.toDataObject(json));
    }
    
    public DataObject updateObj(DataObject obj) throws IOException
    {
        DataObject ret=null;
        DataObject req=new DataObject();
        req.put("data", obj);        
        ret=update(req);
        return ret;
    }
    
    public DataObject updateObj(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return updateObj(DataUtils.toDataObject(json));
    }

    public DataObject fetchObjByNumId(String id) throws IOException
    {
        return fetchObjById(getBaseUri()+id);
    }    
    
    public DataObject fetchObjById(String id) throws IOException
    {
        DataObject ret=null;
        DataObject req=new DataObject();
        DataObject data=new DataObject();
        data.put("_id", id);
        req.put("data", data);

        DataObject r=(DataObject)fetch(req);
        if(r!=null)
        {
            DataObject res=(DataObject)r.get("response");       
            if(res!=null)
            {
                DataList rdata=(DataList)res.get("data");
                if(rdata!=null && rdata.size()>0)
                {
                    ret=(DataObject)rdata.get(0);
                }
            }            
        }
        return ret;
    }
    
    /**
     * Regresa Objecto de cache NumID y si no lo tiene lo carga, de lo contrario regresa null
     * @param id
     * @return 
     */
    public DataObject getObjectByNumId(String id)
    {
        return getObjectById(getBaseUri()+id);
    }    
    
    /**
     * Regresa Objecto de cache por ID y si no lo tiene lo carga, de lo contrario regresa null
     * @param id
     * @return 
     */
    public DataObject getObjectById(String id)
    {
        DataObject obj=cache.get(id);
        if(obj==null)
        {
            synchronized(cache)
            {
                obj=cache.get(id);
                if(obj==null)
                {
                    try
                    {
                        obj=fetchObjById(id);
                        cache.put(id, obj);
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return obj;
    }
    
    public DataObject removeObjById(String id) throws IOException
    {
        DataObject ret=null;
        DataObject req=new DataObject();
        DataObject data=new DataObject();
        data.put("_id", id);
        req.put("data", data);

        DataObject r=(DataObject)remove(req);
        if(r!=null)
        {
            ret=(DataObject)r.get("response");       
        }
        
        cache.remove(id);
        
        return ret;
    }   
    
//    public DataObject update(String query) throws IOException
//    {
//        return update((DataObject)JSON.parse(query));
//    }
    
    public DataObject update(DataObject json) throws IOException
    {
        DataObject req=engine.invokeDataProcessors(name, SWBDataSource.ACTION_UPDATE, SWBDataProcessor.METHOD_REQUEST, json);
        DataObject res=db.update(req,this);
        res=engine.invokeDataProcessors(name, SWBDataSource.ACTION_UPDATE, SWBDataProcessor.METHOD_RESPONSE, res);
        engine.invokeDataServices(name, SWBDataSource.ACTION_UPDATE, req, res);
        
        if(req!=null)
        {
            DataObject data=req.getDataObject("data");
            if(data!=null)
            {
                String id=data.getString("_id");
                cache.remove(id);
            }
        }
        
        return res;
    }   
    
    public DataObject update(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return update(DataUtils.toDataObject(json));
    }
    
//    public DataObject add(String query) throws IOException
//    {
//        return add((DataObject)JSON.parse(query));
//    }
    
    public DataObject add(DataObject json) throws IOException
    {
        DataObject req=engine.invokeDataProcessors(name, SWBDataSource.ACTION_ADD, SWBDataProcessor.METHOD_REQUEST, json);
        DataObject res=db.add(req,this);
        res=engine.invokeDataProcessors(name, SWBDataSource.ACTION_ADD, SWBDataProcessor.METHOD_RESPONSE, res);
        engine.invokeDataServices(name, SWBDataSource.ACTION_ADD, req, res);
        return res;
    }  
    
    public DataObject add(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return add(DataUtils.toDataObject(json));
    }
    
//    public DataObject remove(String query) throws IOException
//    {
//        return remove((DataObject)JSON.parse(query));
//    }
    
    public DataObject remove(DataObject json) throws IOException
    {
        DataObject req=engine.invokeDataProcessors(name, SWBDataSource.ACTION_REMOVE, SWBDataProcessor.METHOD_REQUEST, json);
        DataObject res=db.remove(req,this);
        res=engine.invokeDataProcessors(name, SWBDataSource.ACTION_REMOVE, SWBDataProcessor.METHOD_RESPONSE, res);
        engine.invokeDataServices(name, SWBDataSource.ACTION_REMOVE, req, res);
        cache.clear();        
        return res;
    }   
    
    public DataObject remove(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return remove(DataUtils.toDataObject(json));
    }
    
//    public DataObject validate(String query) throws IOException
//    {
//        return validate((DataObject)JSON.parse(query));
//    }
    
    public DataObject validate(DataObject json) throws IOException
    {
//        ScriptObject dss=getDataSourceScript();        
//        String modelid=dss.getString("modelid");
//        String scls=dss.getString("scls");
        DataObject ret=new DataObject();
        DataObject resp=new DataObject();
        DataObject errors=new DataObject();
        ret.put("response", resp);

        boolean hasErrors=false;
        

        DataObject data=(DataObject)json.get("data");
        if(data!=null)
        {
            Iterator<Map.Entry<String,Object>> it=data.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry<String,Object> entry=it.next(); 

                String key=entry.getKey();
                Object value=entry.getValue();
                ScriptObject field=getDataSourceScriptField(key);
                if(field!=null)
                {
                    ScriptObject validators=field.get("validators");
                    if(validators!=null)
                    {
                        Iterator<ScriptObject> it2=validators.values().iterator();
                        while (it2.hasNext()) 
                        {
                            ScriptObject validator = it2.next();
                            String type=validator.getString("type");

                            if("serverCustom".equals(type))
                            {
                                ScriptObject func=validator.get("serverCondition");
                                if(func!=null)
                                {
                                    //System.out.println(key+"-->"+value+"-->"+func);
                                    ScriptObject r=func.invoke(engine,key,value,json);
                                    //System.out.println("r:"+r.getValue());
                                    if(r!=null && r.getValue().equals(false))
                                    {
                                        //System.out.println("Error...");
                                        hasErrors=true;
                                        String errmsg=validator.getString("errorMessage");
                                        if(errmsg==null)errmsg="Error..";
                                        errors.put(key, errmsg);
                                    }
                                }
                            }else if("isUnique".equals(type))
                            {
                                String id=(String)data.get("_id");
                                DataObject req=new DataObject();
                                DataObject query=new DataObject();
                                req.put("data", query);
                                query.put(key, value);
                                DataList rdata=(DataList)((DataObject)fetch(req).get("response")).get("data");                                  
                                if(rdata!=null && rdata.size()>0)
                                {
                                    if(rdata.size()>1 || id==null || !((DataObject)rdata.get(0)).get("_id").toString().equals(id))
                                    {
                                        hasErrors=true;
                                        String errmsg=validator.getString("errorMessage");
                                        //TODO:Internacionalizar...
                                        if(errmsg==null)errmsg="El valor debe de ser único..";
                                        errors.put(key, errmsg);
                                    }
                                }                                
                                //System.out.println("isUnique:"+key+"->"+value+" "+id+" "+r);
                            }
                        }
                    }
                }
            }        
        }
        
        if(hasErrors)
        {
            resp.put("status", -4);
            resp.put("errors", errors);
        }else
        {
            resp.put("status", 0);
        }
        return ret;                
    } 
    
    public DataObject validate(jdk.nashorn.api.scripting.ScriptObjectMirror json) throws IOException
    {        
        return validate(DataUtils.toDataObject(json));
    }
    
    public ScriptObject getDataSourceScriptField(String name)
    {
        ScriptObject fields=script.get("fields");
        ScriptObject ret=DataUtils.getArrayNode(fields, "name", name);
        if(ret==null)
        {
            fields=script.get("links");
            ret=DataUtils.getArrayNode(fields, "name", name);
        }
        return ret;
    }
    
    public String getBaseUri()
    {
        String modelid=getDataSourceScript().getString("modelid");
        String scls=getDataSourceScript().getString("scls");
        //TODO:get NS
        return "_suri:"+modelid+":"+scls+":";
        //return "_suri:http://swb.org/"+dataStoreName+"/"+modelid+"/"+scls+":";
    }
            
//******************************************* static *******************************/            
    
    public static DataObject getError(int x)
    {
        DataObject ret=new DataObject();
        DataObject resp=new DataObject();
        ret.put("response", resp);
        resp.put("status", x);
        //resp.put("data", obj);
        return ret;
    }
    
//    private static ScriptObject getServerValidator(ScriptObject field, String type)
//    {
//        ScriptObject validators=field.get("validators");
//        return SWBFormsUtils.getArrayNode(validators, "type", type);
//    }    
    
}
