/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.semanticwb.datamanager;

import java.security.MessageDigest;
import org.semanticwb.datamanager.script.ScriptObject;

/**
 *
 * @author javiersolis
 */
public class SWBScriptUtils 
{
    SWBScriptEngine engine;

    public SWBScriptUtils(SWBScriptEngine engine) 
    {
        this.engine=engine;
    }
    
    public String encodeSHA(String str) 
    {
        try
        {
            if(str!=null && !str.startsWith("[SHA-512]"))
            {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                md.update(str.getBytes());

                byte byteData[] = md.digest();

                //convert the byte to hex format method 1
                StringBuffer sb = new StringBuffer();
                sb.append("[SHA-512]");
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }        
        return str;
    }     
    
    public boolean sendMail(String to, String subject, String msg)
    {
        try
        {
            ScriptObject config = engine.getScriptObject().get("config");
            if (config != null) {
                ScriptObject smail=config.get("mail");  
                if(smail==null)return false;
                String from=smail.getString("from");
                String fromName=smail.getString("fromName");
                String host=smail.getString("host");
                String user=smail.getString("user");
                String passwd=smail.getString("passwd");
                int port=(Integer)smail.get("port").getValue();
                boolean ssl=(Boolean)smail.get("ssl").getValue();

                try {
                    Class MultiPartEmail = Class.forName("org.apache.commons.mail.MultiPartEmail");
                    Object mail = MultiPartEmail.getConstructor().newInstance();

                    Class DefaultAuthenticator = Class.forName("org.apache.commons.mail.DefaultAuthenticator");
                    Object auth=DefaultAuthenticator.getConstructor(String.class,String.class).newInstance(user,passwd);

                    MultiPartEmail.getMethod("setAuthenticator", Class.forName("javax.mail.Authenticator")).invoke(mail, auth);            
                    MultiPartEmail.getMethod("setSmtpPort", Integer.TYPE).invoke(mail, port);
                    MultiPartEmail.getMethod("setSSLOnConnect", Boolean.TYPE).invoke(mail, ssl);
                    MultiPartEmail.getMethod("setHostName", String.class).invoke(mail, host);
                    MultiPartEmail.getMethod("addTo", String.class).invoke(mail, to);
                    MultiPartEmail.getMethod("setFrom", String.class,String.class).invoke(mail, from, fromName);
                    MultiPartEmail.getMethod("setSubject", String.class).invoke(mail, subject);
                    MultiPartEmail.getMethod("setMsg", String.class).invoke(mail, msg);
                    MultiPartEmail.getMethod("send").invoke(mail);   
                    
                    return true;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
/*                
                // Create the email message
                MultiPartEmail mail = new MultiPartEmail();
                mail.setSmtpPort(port);
                mail.setAuthenticator(new DefaultAuthenticator(user, passwd));
                //mail.setTLS(true);
                mail.setSSL(ssl);
                mail.setHostName(host);
                mail.addTo(to);
                mail.setFrom(from,fromName);
                mail.setSubject(subject);
                mail.setMsg(msg);
                // add the attachment
                //if(attachment!=null)mail.attach(attachment);
                // send the email
                mail.send();   
                return true;   
*/        
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }      
  
    
}
