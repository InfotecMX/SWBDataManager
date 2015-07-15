package org.semanticwb.datamanager;

import org.junit.Test;

import javax.script.ScriptEngine;
import java.io.File;

import static org.junit.Assert.*;

public class DataMgrTest {

    @Test
    public void testDataMgr() throws Exception {
        File file = new File(DataMgrTest.class.getResource("/demo.js").getFile());
        String path = file.getParent();
        assertNotNull(DataMgr.createInstance(path));
        assertEquals(path, DataMgr.getApplicationPath());
        ScriptEngine engine = DataMgr.getNativeScriptEngine();
        int suma = (Integer)engine.eval("{1+2}");
        assertEquals(suma,3);
        SWBScriptEngine swbEngine = DataMgr.getScriptEngine("/demo.js");

    }
}