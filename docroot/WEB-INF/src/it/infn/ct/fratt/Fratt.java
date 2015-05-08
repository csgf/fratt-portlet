/*
*************************************************************************
Copyright (c) 2011-2013:
Istituto Nazionale di Fisica Nucleare (INFN), Italy
Consorzio COMETA (COMETA), Italy

See http://www.infn.it and and http://www.consorzio-cometa.it for details on
the copyright holders.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

@author <a href="mailto:giuseppe.larocca@ct.infn.it">Giuseppe La Rocca</a>
***************************************************************************
*/
package it.infn.ct.fratt;

// import liferay libraries
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;

// import DataEngine libraries
import com.liferay.portal.util.PortalUtil;
import it.infn.ct.GridEngine.InformationSystem.BDII;
import it.infn.ct.GridEngine.Job.*;

// import generic Java libraries
import it.infn.ct.GridEngine.UsersTracking.UsersTrackingDBInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.URI;

// import portlet libraries
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

// Importing Apache libraries
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Fratt extends GenericPortlet {

    private static Log log = LogFactory.getLog(Fratt.class);   

    @Override
    protected void doEdit(RenderRequest request,
            RenderResponse response)
            throws PortletException, IOException
    {

        PortletPreferences portletPreferences =
                (PortletPreferences) request.getPreferences();

        response.setContentType("text/html");
        
        // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the COMETA VO
        String cometa_fratt_INFRASTRUCTURE = portletPreferences.getValue("cometa_fratt_INFRASTRUCTURE", "N/A");
        // Getting the FRATT VONAME from the portlet preferences for the COMETA VO
        String cometa_fratt_VONAME = portletPreferences.getValue("cometa_fratt_VONAME", "N/A");
        // Getting the FRATT TOPPBDII from the portlet preferences for the COMETA VO
        String cometa_fratt_TOPBDII = portletPreferences.getValue("cometa_fratt_TOPBDII", "N/A");
        // Getting the FRATT WMS from the portlet preferences for the COMETA VO
        String[] cometa_fratt_WMS = portletPreferences.getValues("cometa_fratt_WMS", new String[5]);
        // Getting the FRATT ETOKENSERVER from the portlet preferences for the COMETA VO
        String cometa_fratt_ETOKENSERVER = portletPreferences.getValue("cometa_fratt_ETOKENSERVER", "N/A");
        // Getting the FRATT MYPROXYSERVER from the portlet preferences for the COMETA VO
        String cometa_fratt_MYPROXYSERVER = portletPreferences.getValue("cometa_fratt_MYPROXYSERVER", "N/A");
        // Getting the FRATT PORT from the portlet preferences for the COMETA VO
        String cometa_fratt_PORT = portletPreferences.getValue("cometa_fratt_PORT", "N/A");
        // Getting the FRATT ROBOTID from the portlet preferences for the COMETA VO
        String cometa_fratt_ROBOTID = portletPreferences.getValue("cometa_fratt_ROBOTID", "N/A");
        // Getting the FRATT ROLE from the portlet preferences for the COMETA VO
        String cometa_fratt_ROLE = portletPreferences.getValue("cometa_fratt_ROLE", "N/A");
        // Getting the FRATT RENEWAL from the portlet preferences for the COMETA VO
        String cometa_fratt_RENEWAL = portletPreferences.getValue("cometa_fratt_RENEWAL", "checked");
        // Getting the FRATT DISABLEVOMS from the portlet preferences for the COMETA VO
        String cometa_fratt_DISABLEVOMS = portletPreferences.getValue("cometa_fratt_DISABLEVOMS", "unchecked");

        // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the GRIDIT VO
        String gridit_fratt_INFRASTRUCTURE = portletPreferences.getValue("gridit_fratt_INFRASTRUCTURE", "N/A");
        // Getting the FRATT VONAME from the portlet preferences for the GRIDIT VO
        String gridit_fratt_VONAME = portletPreferences.getValue("gridit_fratt_VONAME", "N/A");
        // Getting the FRATT TOPPBDII from the portlet preferences for the GRIDIT VO
        String gridit_fratt_TOPBDII = portletPreferences.getValue("gridit_fratt_TOPBDII", "N/A");
        // Getting the FRATT WMS from the portlet preferences for the GRIDIT VO
        String[] gridit_fratt_WMS = portletPreferences.getValues("gridit_fratt_WMS", new String[5]);
        // Getting the FRATT ETOKENSERVER from the portlet preferences for the GRIDIT VO
        String gridit_fratt_ETOKENSERVER = portletPreferences.getValue("gridit_fratt_ETOKENSERVER", "N/A");
        // Getting the FRATT MYPROXYSERVER from the portlet preferences for the GRIDIT VO
        String gridit_fratt_MYPROXYSERVER = portletPreferences.getValue("gridit_fratt_MYPROXYSERVER", "N/A");
        // Getting the FRATT PORT from the portlet preferences for the GRIDIT VO
        String gridit_fratt_PORT = portletPreferences.getValue("gridit_fratt_PORT", "N/A");
        // Getting the FRATT ROBOTID from the portlet preferences for the GRIDIT VO
        String gridit_fratt_ROBOTID = portletPreferences.getValue("gridit_fratt_ROBOTID", "N/A");
        // Getting the FRATT ROLE from the portlet preferences for the GRIDIT VO
        String gridit_fratt_ROLE = portletPreferences.getValue("gridit_fratt_ROLE", "N/A");
        // Getting the FRATT RENEWAL from the portlet preferences for the GRIDIT VO
        String gridit_fratt_RENEWAL = portletPreferences.getValue("gridit_fratt_RENEWAL", "checked");
        // Getting the FRATT DISABLEVOMS from the portlet preferences for the GRIDIT VO
        String gridit_fratt_DISABLEVOMS = portletPreferences.getValue("gridit_fratt_DISABLEVOMS", "unchecked");

        // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the EUMED VO
        String eumed_fratt_INFRASTRUCTURE = portletPreferences.getValue("eumed_fratt_INFRASTRUCTURE", "N/A");
        // Getting the FRATT VONAME from the portlet preferences for the EUMED VO
        String eumed_fratt_VONAME = portletPreferences.getValue("eumed_fratt_VONAME", "N/A");
        // Getting the FRATT TOPPBDII from the portlet preferences for the EUMED VO
        String eumed_fratt_TOPBDII = portletPreferences.getValue("eumed_fratt_TOPBDII", "N/A");
        // Getting the FRATT WMS from the portlet preferences for the EUMED VO
        String[] eumed_fratt_WMS = portletPreferences.getValues("eumed_fratt_WMS", new String[5]);
        // Getting the FRATT ETOKENSERVER from the portlet preferences for the EUMED VO
        String eumed_fratt_ETOKENSERVER = portletPreferences.getValue("eumed_fratt_ETOKENSERVER", "N/A");
        // Getting the FRATT MYPROXYSERVER from the portlet preferences for the EUMED VO
        String eumed_fratt_MYPROXYSERVER = portletPreferences.getValue("eumed_fratt_MYPROXYSERVER", "N/A");
        // Getting the FRATT PORT from the portlet preferences for the EUMED VO
        String eumed_fratt_PORT = portletPreferences.getValue("eumed_fratt_PORT", "N/A");
        // Getting the FRATT ROBOTID from the portlet preferences for the EUMED VO
        String eumed_fratt_ROBOTID = portletPreferences.getValue("eumed_fratt_ROBOTID", "N/A");
        // Getting the FRATT ROLE from the portlet preferences for the EUMED VO
        String eumed_fratt_ROLE = portletPreferences.getValue("eumed_fratt_ROLE", "N/A");
        // Getting the FRATT RENEWAL from the portlet preferences for the EUMED VO
        String eumed_fratt_RENEWAL = portletPreferences.getValue("eumed_fratt_RENEWAL", "checked");
        // Getting the FRATT DISABLEVOMS from the portlet preferences for the EUMED VO
        String eumed_fratt_DISABLEVOMS = portletPreferences.getValue("eumed_fratt_DISABLEVOMS", "unchecked");

        // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the BIOMED VO
        String biomed_fratt_INFRASTRUCTURE = portletPreferences.getValue("biomed_fratt_INFRASTRUCTURE", "N/A");
        // Getting the FRATT VONAME from the portlet preferences for the BIOMED VO
        String biomed_fratt_VONAME = portletPreferences.getValue("biomed_fratt_VONAME", "N/A");
        // Getting the FRATT TOPPBDII from the portlet preferences for the BIOMED VO
        String biomed_fratt_TOPBDII = portletPreferences.getValue("biomed_fratt_TOPBDII", "N/A");
        // Getting the FRATT WMS from the portlet preferences for the BIOMED VO
        String[] biomed_fratt_WMS = portletPreferences.getValues("biomed_fratt_WMS", new String[5]);
        // Getting the FRATT ETOKENSERVER from the portlet preferences for the BIOMED VO
        String biomed_fratt_ETOKENSERVER = portletPreferences.getValue("biomed_fratt_ETOKENSERVER", "N/A");
        // Getting the FRATT MYPROXYSERVER from the portlet preferences for the BIOMED VO
        String biomed_fratt_MYPROXYSERVER = portletPreferences.getValue("biomed_fratt_MYPROXYSERVER", "N/A");
        // Getting the FRATT PORT from the portlet preferences for the BIOMED VO
        String biomed_fratt_PORT = portletPreferences.getValue("biomed_fratt_PORT", "N/A");
        // Getting the FRATT ROBOTID from the portlet preferences for the BIOMED VO
        String biomed_fratt_ROBOTID = portletPreferences.getValue("biomed_fratt_ROBOTID", "N/A");
        // Getting the FRATT ROLE from the portlet preferences for the BIOMED VO
        String biomed_fratt_ROLE = portletPreferences.getValue("biomed_fratt_ROLE", "N/A");
        // Getting the FRATT RENEWAL from the portlet preferences for the BIOMED VO
        String biomed_fratt_RENEWAL = portletPreferences.getValue("biomed_fratt_RENEWAL", "checked");
        // Getting the FRATT DISABLEVOMS from the portlet preferences for the BIOMED VO
        String biomed_fratt_DISABLEVOMS = portletPreferences.getValue("biomed_fratt_DISABLEVOMS", "unchecked");

        // Getting the FRATT APPID from the portlet preferences
        String fratt_APPID = portletPreferences.getValue("fratt_APPID", "N/A");
        // Getting the FRATT OUTPUT_PATH from the portlet preferences
        String fratt_OUTPUT_PATH = portletPreferences.getValue("fratt_OUTPUT_PATH", "/tmp");
        // Getting the FRATT SFOTWARE from the portlet preferences
        String fratt_SOFTWARE = portletPreferences.getValue("fratt_SOFTWARE", "N/A");
        // Getting the TRACKING_DB_HOSTNAME from the portlet preferences
        String TRACKING_DB_HOSTNAME = portletPreferences.getValue("TRACKING_DB_HOSTNAME", "N/A");
        // Getting the TRACKING_DB_USERNAME from the portlet preferences
        String TRACKING_DB_USERNAME = portletPreferences.getValue("TRACKING_DB_USERNAME", "N/A");
        // Getting the TRACKING_DB_PASSWORD from the portlet preferences
        String TRACKING_DB_PASSWORD = portletPreferences.getValue("TRACKING_DB_PASSWORD", "N/A");
        // Getting the SMTP_HOST from the portlet preferences
        String SMTP_HOST = portletPreferences.getValue("SMTP_HOST", "N/A");
        // Getting the SENDER MAIL from the portlet preferences
        String SENDER_MAIL = portletPreferences.getValue("SENDER_MAIL", "N/A");
        // Get the list of enabled Infrastructures
        String[] infras = portletPreferences.getValues("fratt_ENABLEINFRASTRUCTURE", new String[3]);

        // Set the default portlet preferences
        request.setAttribute("gridit_fratt_INFRASTRUCTURE", gridit_fratt_INFRASTRUCTURE.trim());
        request.setAttribute("gridit_fratt_VONAME", gridit_fratt_VONAME.trim());
        request.setAttribute("gridit_fratt_TOPBDII", gridit_fratt_TOPBDII.trim());
        request.setAttribute("gridit_fratt_WMS", gridit_fratt_WMS);
        request.setAttribute("gridit_fratt_ETOKENSERVER", gridit_fratt_ETOKENSERVER.trim());
        request.setAttribute("gridit_fratt_MYPROXYSERVER", gridit_fratt_MYPROXYSERVER.trim());
        request.setAttribute("gridit_fratt_PORT", gridit_fratt_PORT.trim());
        request.setAttribute("gridit_fratt_ROBOTID", gridit_fratt_ROBOTID.trim());
        request.setAttribute("gridit_fratt_ROLE", gridit_fratt_ROLE.trim());
        request.setAttribute("gridit_fratt_RENEWAL", gridit_fratt_RENEWAL);
        request.setAttribute("gridit_fratt_DISABLEVOMS", gridit_fratt_DISABLEVOMS);
        
        request.setAttribute("cometa_fratt_INFRASTRUCTURE", cometa_fratt_INFRASTRUCTURE.trim());
        request.setAttribute("cometa_fratt_VONAME", cometa_fratt_VONAME.trim());
        request.setAttribute("cometa_fratt_TOPBDII", cometa_fratt_TOPBDII.trim());
        request.setAttribute("cometa_fratt_WMS", cometa_fratt_WMS);
        request.setAttribute("cometa_fratt_ETOKENSERVER", cometa_fratt_ETOKENSERVER.trim());
        request.setAttribute("cometa_fratt_MYPROXYSERVER", cometa_fratt_MYPROXYSERVER.trim());
        request.setAttribute("cometa_fratt_PORT", cometa_fratt_PORT.trim());
        request.setAttribute("cometa_fratt_ROBOTID", cometa_fratt_ROBOTID.trim());
        request.setAttribute("cometa_fratt_ROLE", cometa_fratt_ROLE.trim());
        request.setAttribute("cometa_fratt_RENEWAL", cometa_fratt_RENEWAL);
        request.setAttribute("cometa_fratt_DISABLEVOMS", cometa_fratt_DISABLEVOMS);

        request.setAttribute("eumed_fratt_INFRASTRUCTURE", eumed_fratt_INFRASTRUCTURE.trim());
        request.setAttribute("eumed_fratt_VONAME", eumed_fratt_VONAME.trim());
        request.setAttribute("eumed_fratt_TOPBDII", eumed_fratt_TOPBDII.trim());
        request.setAttribute("eumed_fratt_WMS", eumed_fratt_WMS);
        request.setAttribute("eumed_fratt_ETOKENSERVER", eumed_fratt_ETOKENSERVER.trim());
        request.setAttribute("eumed_fratt_MYPROXYSERVER", eumed_fratt_MYPROXYSERVER.trim());
        request.setAttribute("eumed_fratt_PORT", eumed_fratt_PORT.trim());
        request.setAttribute("eumed_fratt_ROBOTID", eumed_fratt_ROBOTID.trim());
        request.setAttribute("eumed_fratt_ROLE", eumed_fratt_ROLE.trim());
        request.setAttribute("eumed_fratt_RENEWAL", eumed_fratt_RENEWAL);
        request.setAttribute("eumed_fratt_DISABLEVOMS", eumed_fratt_DISABLEVOMS);

        request.setAttribute("biomed_fratt_INFRASTRUCTURE", biomed_fratt_INFRASTRUCTURE.trim());
        request.setAttribute("biomed_fratt_VONAME", biomed_fratt_VONAME.trim());
        request.setAttribute("biomed_fratt_TOPBDII", biomed_fratt_TOPBDII.trim());
        request.setAttribute("biomed_fratt_WMS", biomed_fratt_WMS);
        request.setAttribute("biomed_fratt_ETOKENSERVER", biomed_fratt_ETOKENSERVER.trim());
        request.setAttribute("biomed_fratt_MYPROXYSERVER", biomed_fratt_MYPROXYSERVER.trim());
        request.setAttribute("biomed_fratt_PORT", biomed_fratt_PORT.trim());
        request.setAttribute("biomed_fratt_ROBOTID", biomed_fratt_ROBOTID.trim());
        request.setAttribute("biomed_fratt_ROLE", biomed_fratt_ROLE.trim());
        request.setAttribute("biomed_fratt_RENEWAL", biomed_fratt_RENEWAL);
        request.setAttribute("biomed_fratt_DISABLEVOMS", biomed_fratt_DISABLEVOMS);

        request.setAttribute("fratt_ENABLEINFRASTRUCTURE", infras);
        request.setAttribute("fratt_APPID", fratt_APPID.trim());
        request.setAttribute("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
        request.setAttribute("fratt_SOFTWARE", fratt_SOFTWARE.trim());
        request.setAttribute("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
        request.setAttribute("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
        request.setAttribute("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
        request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
        request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());

        log.info("\nStarting the EDIT mode...with this settings"
        + "\ncometa_fratt_INFRASTRUCTURE: " + cometa_fratt_INFRASTRUCTURE
        + "\ncometa_fratt_VONAME: " + cometa_fratt_VONAME
        + "\ncometa_fratt_TOPBDII: " + cometa_fratt_TOPBDII                    
        + "\ncometa_fratt_ETOKENSERVER: " + cometa_fratt_ETOKENSERVER
        + "\ncometa_fratt_MYPROXYSERVER: " + cometa_fratt_MYPROXYSERVER
        + "\ncometa_fratt_PORT: " + cometa_fratt_PORT
        + "\ncometa_fratt_ROBOTID: " + cometa_fratt_ROBOTID
        + "\ncometa_fratt_ROLE: " + cometa_fratt_ROLE
        + "\ncometa_fratt_RENEWAL: " + cometa_fratt_RENEWAL
        + "\ncometa_fratt_DISABLEVOMS: " + cometa_fratt_DISABLEVOMS
                
        + "\ngridit_fratt_INFRASTRUCTURE: " + gridit_fratt_INFRASTRUCTURE
        + "\ngridit_fratt_VONAME: " + gridit_fratt_VONAME
        + "\ngridit_fratt_TOPBDII: " + gridit_fratt_TOPBDII                    
        + "\ngridit_fratt_ETOKENSERVER: " + gridit_fratt_ETOKENSERVER
        + "\ngridit_fratt_MYPROXYSERVER: " + gridit_fratt_MYPROXYSERVER
        + "\ngridit_fratt_PORT: " + gridit_fratt_PORT
        + "\ngridit_fratt_ROBOTID: " + gridit_fratt_ROBOTID
        + "\ngridit_fratt_ROLE: " + gridit_fratt_ROLE
        + "\ngridit_fratt_RENEWAL: " + gridit_fratt_RENEWAL
        + "\ngridit_fratt_DISABLEVOMS: " + gridit_fratt_DISABLEVOMS

        + "\n\neumed_fratt_INFRASTRUCTURE: " + eumed_fratt_INFRASTRUCTURE
        + "\neumed_fratt_VONAME: " + eumed_fratt_VONAME
        + "\neumed_fratt_TOPBDII: " + eumed_fratt_TOPBDII                    
        + "\neumed_fratt_ETOKENSERVER: " + eumed_fratt_ETOKENSERVER
        + "\neumed_fratt_MYPROXYSERVER: " + eumed_fratt_MYPROXYSERVER
        + "\neumed_fratt_PORT: " + eumed_fratt_PORT
        + "\neumed_fratt_ROBOTID: " + eumed_fratt_ROBOTID
        + "\neumed_fratt_ROLE: " + eumed_fratt_ROLE
        + "\neumed_fratt_RENEWAL: " + eumed_fratt_RENEWAL
        + "\neumed_fratt_DISABLEVOMS: " + eumed_fratt_DISABLEVOMS

        + "\n\nbiomed_fratt_INFRASTRUCTURE: " + biomed_fratt_INFRASTRUCTURE
        + "\nbiomed_fratt_VONAME: " + biomed_fratt_VONAME
        + "\nbiomed_fratt_TOPBDII: " + biomed_fratt_TOPBDII                   
        + "\nbiomed_fratt_ETOKENSERVER: " + biomed_fratt_ETOKENSERVER
        + "\nbiomed_fratt_MYPROXYSERVER: " + biomed_fratt_MYPROXYSERVER
        + "\nbiomed_fratt_PORT: " + biomed_fratt_PORT
        + "\nbiomed_fratt_ROBOTID: " + biomed_fratt_ROBOTID
        + "\nbiomed_fratt_ROLE: " + biomed_fratt_ROLE
        + "\nbiomed_fratt_RENEWAL: " + biomed_fratt_RENEWAL
        + "\nbiomed_fratt_DISABLEVOMS: " + biomed_fratt_DISABLEVOMS
        
        + "\nfratt_APPID: " + fratt_APPID
        + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
        + "\nfratt_SOFTWARE: " +fratt_SOFTWARE
        + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
        + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
        + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
        + "\nSMTP Server: " + SMTP_HOST
        + "\nSender: " + SENDER_MAIL);

        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/edit.jsp");

        dispatcher.include(request, response);
    }

    @Override
    protected void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        //super.doHelp(request, response);

        response.setContentType("text/html");

        log.info("\nStarting the HELP mode...");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/help.jsp");

        dispatcher.include(request, response);
    }

    @Override
    protected void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        PortletPreferences portletPreferences =
                (PortletPreferences) request.getPreferences();

        response.setContentType("text/html");

        //java.util.Enumeration listPreferences = portletPreferences.getNames();
        PortletRequestDispatcher dispatcher = null;
        
        String cometa_fratt_TOPBDII = "";
        String cometa_fratt_VONAME = "";
        String gridit_fratt_TOPBDII = "";
        String gridit_fratt_VONAME = "";
        String eumed_fratt_TOPBDII = "";
        String eumed_fratt_VONAME = "";
        String biomed_fratt_TOPBDII = "";
        String biomed_fratt_VONAME = "";
        
        String cometa_fratt_ENABLEINFRASTRUCTURE = "";
        String gridit_fratt_ENABLEINFRASTRUCTURE = "";
        String eumed_fratt_ENABLEINFRASTRUCTURE = "";
        String biomed_fratt_ENABLEINFRASTRUCTURE = "";
        String[] infras = new String[4];
        
        String[] fratt_INFRASTRUCTURES = 
                portletPreferences.getValues("fratt_ENABLEINFRASTRUCTURE", new String[4]);
        
        for (int i=0; i<fratt_INFRASTRUCTURES.length; i++) {            
            if (fratt_INFRASTRUCTURES[i]!=null && fratt_INFRASTRUCTURES[i].equals("cometa")) 
                { cometa_fratt_ENABLEINFRASTRUCTURE = "checked"; log.info ("\n COMETA!"); }
            if (fratt_INFRASTRUCTURES[i]!=null && fratt_INFRASTRUCTURES[i].equals("gridit")) 
                { gridit_fratt_ENABLEINFRASTRUCTURE = "checked"; log.info ("\n GRIDIT!"); }
            if (fratt_INFRASTRUCTURES[i]!=null && fratt_INFRASTRUCTURES[i].equals("eumed")) 
                { eumed_fratt_ENABLEINFRASTRUCTURE = "checked"; log.info ("\n EUMED!"); }
            if (fratt_INFRASTRUCTURES[i]!=null && fratt_INFRASTRUCTURES[i].equals("biomed")) 
                { biomed_fratt_ENABLEINFRASTRUCTURE = "checked"; log.info ("\n BIOMED!"); }            
        }
        
        // Getting the FRATT ENABLEINFRASTRUCTURE from the portlet preferences
        //fratt_ENABLEINFRASTRUCTURE = portletPreferences.getValue("fratt_ENABLEINFRASTRUCTURE", "NULL");
        // Getting the FRATT APPID from the portlet preferences
        String fratt_APPID = portletPreferences.getValue("fratt_APPID", "N/A");
        // Getting the FRATT OUTPUT_PATH from the portlet preferences
        String fratt_OUTPUT_PATH = portletPreferences.getValue("fratt_OUTPUT_PATH", "/tmp");
        // Getting the FRATT SOFTWARE from the portlet preferences
        String fratt_SOFTWARE = portletPreferences.getValue("fratt_SOFTWARE", "N/A");
        // Getting the TRACKING_DB_HOSTNAME from the portlet preferences
        String TRACKING_DB_HOSTNAME = portletPreferences.getValue("TRACKING_DB_HOSTNAME", "N/A");
        // Getting the TRACKING_DB_USERNAME from the portlet preferences
        String TRACKING_DB_USERNAME = portletPreferences.getValue("TRACKING_DB_USERNAME", "N/A");
        // Getting the TRACKING_DB_PASSWORD from the portlet preferences
        String TRACKING_DB_PASSWORD = portletPreferences.getValue("TRACKING_DB_PASSWORD", "N/A");
        // Getting the SMTP_HOST from the portlet preferences
        String SMTP_HOST = portletPreferences.getValue("SMTP_HOST", "N/A");
        // Getting the SENDER_MAIL from the portlet preferences
        String SENDER_MAIL = portletPreferences.getValue("SENDER_MAIL", "N/A");
        
        if (cometa_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
        {
            infras[0]="cometa";
            // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the COMETA VO
            String cometa_fratt_INFRASTRUCTURE = portletPreferences.getValue("cometa_fratt_INFRASTRUCTURE", "N/A");
            // Getting the FRATT VONAME from the portlet preferences for the COMETA VO
            cometa_fratt_VONAME = portletPreferences.getValue("cometa_fratt_VONAME", "N/A");
            // Getting the FRATT TOPPBDII from the portlet preferences for the COMETA VO
            cometa_fratt_TOPBDII = portletPreferences.getValue("cometa_fratt_TOPBDII", "N/A");
            // Getting the FRATT WMS from the portlet preferences for the COMETA VO
            String[] cometa_fratt_WMS = portletPreferences.getValues("cometa_fratt_WMS", new String[5]);
            // Getting the FRATT ETOKENSERVER from the portlet preferences for the COMETA VO
            String cometa_fratt_ETOKENSERVER = portletPreferences.getValue("cometa_fratt_ETOKENSERVER", "N/A");
            // Getting the FRATT MYPROXYSERVER from the portlet preferences for the COMETA VO
            String cometa_fratt_MYPROXYSERVER = portletPreferences.getValue("cometa_fratt_MYPROXYSERVER", "N/A");
            // Getting the FRATT PORT from the portlet preferences for the COMETA VO
            String cometa_fratt_PORT = portletPreferences.getValue("cometa_fratt_PORT", "N/A");
            // Getting the FRATT ROBOTID from the portlet preferences for the COMETA VO
            String cometa_fratt_ROBOTID = portletPreferences.getValue("gridit_fratt_ROBOTID", "N/A");
            // Getting the FRATT ROLE from the portlet preferences for the COMETA VO
            String cometa_fratt_ROLE = portletPreferences.getValue("cometa_fratt_ROLE", "N/A");
            // Getting the FRATT RENEWAL from the portlet preferences for the COMETA VO
            String cometa_fratt_RENEWAL = portletPreferences.getValue("cometa_fratt_RENEWAL", "checked");
            // Getting the FRATT DISABLEVOMS from the portlet preferences for the COMETA VO
            String cometa_fratt_DISABLEVOMS = portletPreferences.getValue("cometa_fratt_DISABLEVOMS", "unchecked");
            
            // Fetching all the WMS Endpoints for the COMETA VO
            String cometa_WMS = "";
            if (cometa_fratt_ENABLEINFRASTRUCTURE.equals("checked")) {
                if (cometa_fratt_WMS!=null) {
                    //log.info("length="+cometa_fratt_WMS.length);
                    for (int i = 0; i < cometa_fratt_WMS.length; i++)
                        if (!(cometa_fratt_WMS[i].trim().equals("N/A")) ) 
                            cometa_WMS += cometa_fratt_WMS[i] + " ";                        
                } else { log.info("WMS not set for COMETA!"); cometa_fratt_ENABLEINFRASTRUCTURE="unchecked"; }
            }
            
            // Save the portlet preferences
            request.setAttribute("cometa_fratt_INFRASTRUCTURE", cometa_fratt_INFRASTRUCTURE.trim());
            request.setAttribute("cometa_fratt_VONAME", cometa_fratt_VONAME.trim());
            request.setAttribute("cometa_fratt_TOPBDII", cometa_fratt_TOPBDII.trim());
            request.setAttribute("cometa_fratt_WMS", cometa_WMS);
            request.setAttribute("cometa_fratt_ETOKENSERVER", cometa_fratt_ETOKENSERVER.trim());
            request.setAttribute("cometa_fratt_MYPROXYSERVER", cometa_fratt_MYPROXYSERVER.trim());
            request.setAttribute("cometa_fratt_PORT", cometa_fratt_PORT.trim());
            request.setAttribute("cometa_fratt_ROBOTID", cometa_fratt_ROBOTID.trim());
            request.setAttribute("cometa_fratt_ROLE", cometa_fratt_ROLE.trim());
            request.setAttribute("cometa_fratt_RENEWAL", cometa_fratt_RENEWAL);
            request.setAttribute("cometa_fratt_DISABLEVOMS", cometa_fratt_DISABLEVOMS);
            
            //request.setAttribute("fratt_ENABLEINFRASTRUCTURE", fratt_ENABLEINFRASTRUCTURE);
            request.setAttribute("fratt_APPID", fratt_APPID.trim());
            request.setAttribute("fratt_SOFTWARE", fratt_SOFTWARE.trim());
            request.setAttribute("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
            request.setAttribute("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
            request.setAttribute("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
            request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
            request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());
        }
        
        if (gridit_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
        {
            infras[1]="gridit";
            // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the GRIDIT VO
            String gridit_fratt_INFRASTRUCTURE = portletPreferences.getValue("gridit_fratt_INFRASTRUCTURE", "N/A");
            // Getting the FRATT VONAME from the portlet preferences for the GRIDIT VO
            gridit_fratt_VONAME = portletPreferences.getValue("gridit_fratt_VONAME", "N/A");
            // Getting the FRATT TOPPBDII from the portlet preferences for the GRIDIT VO
            gridit_fratt_TOPBDII = portletPreferences.getValue("gridit_fratt_TOPBDII", "N/A");
            // Getting the FRATT WMS from the portlet preferences for the GRIDIT VO
            String[] gridit_fratt_WMS = portletPreferences.getValues("gridit_fratt_WMS", new String[5]);
            // Getting the FRATT ETOKENSERVER from the portlet preferences for the GRIDIT VO
            String gridit_fratt_ETOKENSERVER = portletPreferences.getValue("gridit_fratt_ETOKENSERVER", "N/A");
            // Getting the FRATT MYPROXYSERVER from the portlet preferences for the GRIDIT VO
            String gridit_fratt_MYPROXYSERVER = portletPreferences.getValue("gridit_fratt_MYPROXYSERVER", "N/A");
            // Getting the FRATT PORT from the portlet preferences for the GRIDIT VO
            String gridit_fratt_PORT = portletPreferences.getValue("gridit_fratt_PORT", "N/A");
            // Getting the FRATT ROBOTID from the portlet preferences for the GRIDIT VO
            String gridit_fratt_ROBOTID = portletPreferences.getValue("gridit_fratt_ROBOTID", "N/A");
            // Getting the FRATT ROLE from the portlet preferences for the GRIDIT VO
            String gridit_fratt_ROLE = portletPreferences.getValue("gridit_fratt_ROLE", "N/A");
            // Getting the FRATT RENEWAL from the portlet preferences for the GRIDIT VO
            String gridit_fratt_RENEWAL = portletPreferences.getValue("gridit_fratt_RENEWAL", "checked");
            // Getting the FRATT DISABLEVOMS from the portlet preferences for the GRIDIT VO
            String gridit_fratt_DISABLEVOMS = portletPreferences.getValue("gridit_fratt_DISABLEVOMS", "unchecked");
            
            // Fetching all the WMS Endpoints for the GRIDIT VO
            String gridit_WMS = "";
            if (gridit_fratt_ENABLEINFRASTRUCTURE.equals("checked")) {            
                if (gridit_fratt_WMS!=null) {
                    //log.info("length="+gridit_fratt_WMS.length);
                    for (int i = 0; i < gridit_fratt_WMS.length; i++)
                        if (!(gridit_fratt_WMS[i].trim().equals("N/A")) ) 
                            gridit_WMS += gridit_fratt_WMS[i] + " ";                        
                } else { log.info("WMS not set for GRIDIT!"); gridit_fratt_ENABLEINFRASTRUCTURE="unchecked"; }
            }
            
            // Save the portlet preferences
            request.setAttribute("gridit_fratt_INFRASTRUCTURE", gridit_fratt_INFRASTRUCTURE.trim());
            request.setAttribute("gridit_fratt_VONAME", gridit_fratt_VONAME.trim());
            request.setAttribute("gridit_fratt_TOPBDII", gridit_fratt_TOPBDII.trim());
            request.setAttribute("gridit_fratt_WMS", gridit_WMS);
            request.setAttribute("gridit_fratt_ETOKENSERVER", gridit_fratt_ETOKENSERVER.trim());
            request.setAttribute("gridit_fratt_MYPROXYSERVER", gridit_fratt_MYPROXYSERVER.trim());
            request.setAttribute("gridit_fratt_PORT", gridit_fratt_PORT.trim());
            request.setAttribute("gridit_fratt_ROBOTID", gridit_fratt_ROBOTID.trim());
            request.setAttribute("gridit_fratt_ROLE", gridit_fratt_ROLE.trim());
            request.setAttribute("gridit_fratt_RENEWAL", gridit_fratt_RENEWAL);
            request.setAttribute("gridit_fratt_DISABLEVOMS", gridit_fratt_DISABLEVOMS);
            
            //request.setAttribute("fratt_ENABLEINFRASTRUCTURE", fratt_ENABLEINFRASTRUCTURE);
            request.setAttribute("fratt_APPID", fratt_APPID.trim());
            request.setAttribute("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
            request.setAttribute("fratt_SOFTWARE", fratt_SOFTWARE.trim());
            request.setAttribute("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
            request.setAttribute("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
            request.setAttribute("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
            request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
            request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());
        }
        
        if (eumed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
        {
            infras[2]="eumed";
            // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the EUMED VO
            String eumed_fratt_INFRASTRUCTURE = portletPreferences.getValue("eumed_fratt_INFRASTRUCTURE", "N/A");
            // Getting the FRATT VONAME from the portlet preferences for the EUMED VO
            eumed_fratt_VONAME = portletPreferences.getValue("eumed_fratt_VONAME", "N/A");
            // Getting the FRATT TOPPBDII from the portlet preferences for the EUMED VO
            eumed_fratt_TOPBDII = portletPreferences.getValue("eumed_fratt_TOPBDII", "N/A");
            // Getting the FRATT WMS from the portlet preferences for the EUMED VO
            String[] eumed_fratt_WMS = portletPreferences.getValues("eumed_fratt_WMS", new String[5]);
            // Getting the FRATT ETOKENSERVER from the portlet preferences for the EUMED VO
            String eumed_fratt_ETOKENSERVER = portletPreferences.getValue("eumed_fratt_ETOKENSERVER", "N/A");
            // Getting the FRATT MYPROXYSERVER from the portlet preferences for the EUMED VO
            String eumed_fratt_MYPROXYSERVER = portletPreferences.getValue("eumed_fratt_MYPROXYSERVER", "N/A");
            // Getting the FRATT PORT from the portlet preferences for the EUMED VO
            String eumed_fratt_PORT = portletPreferences.getValue("eumed_fratt_PORT", "N/A");
            // Getting the FRATT ROBOTID from the portlet preferences for the EUMED VO
            String eumed_fratt_ROBOTID = portletPreferences.getValue("eumed_fratt_ROBOTID", "N/A");
            // Getting the FRATT ROLE from the portlet preferences for the EUMED VO
            String eumed_fratt_ROLE = portletPreferences.getValue("eumed_fratt_ROLE", "N/A");
            // Getting the FRATT RENEWAL from the portlet preferences for the EUMED VO
            String eumed_fratt_RENEWAL = portletPreferences.getValue("eumed_fratt_RENEWAL", "checked");
            // Getting the FRATT DISABLEVOMS from the portlet preferences for the EUMED VO
            String eumed_fratt_DISABLEVOMS = portletPreferences.getValue("eumed_fratt_DISABLEVOMS", "unchecked");
                                    
            // Fetching all the WMS Endpoints for the EUMED VO
            String eumed_WMS = "";
            if (eumed_fratt_ENABLEINFRASTRUCTURE.equals("checked")) {            
                if (eumed_fratt_WMS!=null) {
                    //log.info("length="+eumed_fratt_WMS.length);
                    for (int i = 0; i < eumed_fratt_WMS.length; i++)
                        if (!(eumed_fratt_WMS[i].trim().equals("N/A")) ) 
                            eumed_WMS += eumed_fratt_WMS[i] + " ";                        
                } else { log.info("WMS not set for EUMED!"); eumed_fratt_ENABLEINFRASTRUCTURE="unchecked"; }
            }
            
            // Save the portlet preferences
            request.setAttribute("eumed_fratt_INFRASTRUCTURE", eumed_fratt_INFRASTRUCTURE.trim());
            request.setAttribute("eumed_fratt_VONAME", eumed_fratt_VONAME.trim());
            request.setAttribute("eumed_fratt_TOPBDII", eumed_fratt_TOPBDII.trim());
            request.setAttribute("eumed_fratt_WMS", eumed_WMS);
            request.setAttribute("eumed_fratt_ETOKENSERVER", eumed_fratt_ETOKENSERVER.trim());
            request.setAttribute("eumed_fratt_MYPROXYSERVER", eumed_fratt_MYPROXYSERVER.trim());
            request.setAttribute("eumed_fratt_PORT", eumed_fratt_PORT.trim());
            request.setAttribute("eumed_fratt_ROBOTID", eumed_fratt_ROBOTID.trim());
            request.setAttribute("eumed_fratt_ROLE", eumed_fratt_ROLE.trim());
            request.setAttribute("eumed_fratt_RENEWAL", eumed_fratt_RENEWAL);
            request.setAttribute("eumed_fratt_DISABLEVOMS", eumed_fratt_DISABLEVOMS);

            //request.setAttribute("fratt_ENABLEINFRASTRUCTURE", fratt_ENABLEINFRASTRUCTURE);
            request.setAttribute("fratt_APPID", fratt_APPID.trim());
            request.setAttribute("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
            request.setAttribute("fratt_SOFTWARE", fratt_SOFTWARE.trim());
            request.setAttribute("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
            request.setAttribute("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
            request.setAttribute("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
            request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
            request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());
        }

        if (biomed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
        {
            infras[3]="biomed";
            // Getting the FRATT INFRASTRUCTURE from the portlet preferences for the BIOMED VO
            String biomed_fratt_INFRASTRUCTURE = portletPreferences.getValue("biomed_fratt_INFRASTRUCTURE", "N/A");
            // Getting the FRATT VONAME from the portlet preferences for the biomed VO
            biomed_fratt_VONAME = portletPreferences.getValue("biomed_fratt_VONAME", "N/A");
            // Getting the FRATT TOPPBDII from the portlet preferences for the BIOMED VO
            biomed_fratt_TOPBDII = portletPreferences.getValue("biomed_fratt_TOPBDII", "N/A");
            // Getting the FRATT WMS from the portlet preferences for the BIOMED VO
            String[] biomed_fratt_WMS = portletPreferences.getValues("biomed_fratt_WMS", new String[5]);
            // Getting the FRATT ETOKENSERVER from the portlet preferences for the BIOMED VO
            String biomed_fratt_ETOKENSERVER = portletPreferences.getValue("biomed_fratt_ETOKENSERVER", "N/A");
            // Getting the FRATT MYPROXYSERVER from the portlet preferences for the BIOMED VO
            String biomed_fratt_MYPROXYSERVER = portletPreferences.getValue("biomed_fratt_MYPROXYSERVER", "N/A");
            // Getting the FRATT PORT from the portlet preferences for the BIOMED VO
            String biomed_fratt_PORT = portletPreferences.getValue("biomed_fratt_PORT", "N/A");
            // Getting the FRATT ROBOTID from the portlet preferences for the BIOMED VO
            String biomed_fratt_ROBOTID = portletPreferences.getValue("biomed_fratt_ROBOTID", "N/A");
            // Getting the FRATT ROLE from the portlet preferences for the BIOMED VO
            String biomed_fratt_ROLE = portletPreferences.getValue("biomed_fratt_ROLE", "N/A");
            // Getting the FRATT RENEWAL from the portlet preferences for the BIOMED VO
            String biomed_fratt_RENEWAL = portletPreferences.getValue("biomed_fratt_RENEWAL", "checked");
            // Getting the FRATT DISABLEVOMS from the portlet preferences for the BIOMED VO
            String biomed_fratt_DISABLEVOMS = portletPreferences.getValue("biomed_fratt_DISABLEVOMS", "unchecked");              
            
            // Fetching all the WMS Endpoints for the BIOMED VO
            String biomed_WMS = "";
            if (biomed_fratt_ENABLEINFRASTRUCTURE.equals("checked")) {            
                if (biomed_fratt_WMS!=null) {
                    //log.info("length="+biomed_fratt_WMS.length);
                    for (int i = 0; i < biomed_fratt_WMS.length; i++)
                        if (!(biomed_fratt_WMS[i].trim().equals("N/A")) ) 
                            biomed_WMS += biomed_fratt_WMS[i] + " ";                        
                } else { log.info("WMS not set for BIOMED!"); biomed_fratt_ENABLEINFRASTRUCTURE="unchecked"; }
            }
            
            // Save the portlet preferences
            request.setAttribute("biomed_fratt_INFRASTRUCTURE", biomed_fratt_INFRASTRUCTURE.trim());
            request.setAttribute("biomed_fratt_VONAME", biomed_fratt_VONAME.trim());
            request.setAttribute("biomed_fratt_TOPBDII", biomed_fratt_TOPBDII.trim());
            request.setAttribute("biomed_fratt_WMS", biomed_WMS);
            request.setAttribute("biomed_fratt_ETOKENSERVER", biomed_fratt_ETOKENSERVER.trim());
            request.setAttribute("biomed_fratt_MYPROXYSERVER", biomed_fratt_MYPROXYSERVER.trim());
            request.setAttribute("biomed_fratt_PORT", biomed_fratt_PORT.trim());
            request.setAttribute("biomed_fratt_ROBOTID", biomed_fratt_ROBOTID.trim());
            request.setAttribute("biomed_fratt_ROLE", biomed_fratt_ROLE.trim());
            request.setAttribute("biomed_fratt_RENEWAL", biomed_fratt_RENEWAL);
            request.setAttribute("biomed_fratt_DISABLEVOMS", biomed_fratt_DISABLEVOMS);

            //request.setAttribute("fratt_ENABLEINFRASTRUCTURE", fratt_ENABLEINFRASTRUCTURE);
            request.setAttribute("fratt_APPID", fratt_APPID.trim());
            request.setAttribute("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
            request.setAttribute("fratt_SOFTWARE", fratt_SOFTWARE.trim());
            request.setAttribute("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
            request.setAttribute("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
            request.setAttribute("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
            request.setAttribute("SMTP_HOST", SMTP_HOST.trim());
            request.setAttribute("SENDER_MAIL", SENDER_MAIL.trim());
        }
        
        // Save in the preferences the list of supported infrastructures 
        request.setAttribute("fratt_ENABLEINFRASTRUCTURE", infras);

        HashMap<String,Properties> GPS_table = new HashMap<String, Properties>();
        HashMap<String,Properties> GPS_queue = new HashMap<String, Properties>();

        // ********************************************************
        List<String> CEqueues_cometa = null;        
        List<String> CEqueues_gridit = null;
        List<String> CEqueues_eumed = null;
        List<String> CEqueues_biomed = null;
        
        List<String> CEs_list_cometa = null;        
        List<String> CEs_list_gridit = null;        
        List<String> CEs_list_eumed = null;
        List<String> CEs_list_biomed = null;        
        
        BDII bdii = null;

        try {
                if (cometa_fratt_ENABLEINFRASTRUCTURE.equals("checked") && 
                   (!cometa_fratt_TOPBDII.equals("N/A")) ) 
                {
                    log.info("-----*FETCHING*THE*<COMETA>*RESOURCES*-----");                    
                    bdii = new BDII(new URI(cometa_fratt_TOPBDII));
                    CEs_list_cometa = 
                            getListofCEForSoftwareTag(cometa_fratt_VONAME,
                                                      cometa_fratt_TOPBDII,
                                                      fratt_SOFTWARE);
                    
                    CEqueues_cometa = 
                            bdii.queryCEQueues(cometa_fratt_VONAME);
                }
             
                if (gridit_fratt_ENABLEINFRASTRUCTURE.equals("checked") && 
                   (!gridit_fratt_TOPBDII.equals("N/A")) ) 
                {
                    log.info("-----*FETCHING*THE*<GRIDIT>*RESOURCES*-----");
                    bdii = new BDII(new URI(gridit_fratt_TOPBDII));
                    CEs_list_gridit = 
                               getListofCEForSoftwareTag(gridit_fratt_VONAME,
                                                         gridit_fratt_TOPBDII,
                                                         fratt_SOFTWARE);
                    
                    CEqueues_gridit = 
                            bdii.queryCEQueues(gridit_fratt_VONAME);
                }
                
                if (eumed_fratt_ENABLEINFRASTRUCTURE.equals("checked") && 
                   (!eumed_fratt_TOPBDII.equals("N/A")) ) 
                {
                    log.info("-----*FETCHING*THE*<EUMED>*RESOURCES*-----");
                    bdii = new BDII(new URI(eumed_fratt_TOPBDII));
                    CEs_list_eumed = 
                            getListofCEForSoftwareTag(eumed_fratt_VONAME,
                                                      eumed_fratt_TOPBDII,
                                                      fratt_SOFTWARE);
                    
                    CEqueues_eumed = 
                            bdii.queryCEQueues(eumed_fratt_VONAME);
                }
                
                if (biomed_fratt_ENABLEINFRASTRUCTURE.equals("checked") &&
                   (!biomed_fratt_TOPBDII.equals("N/A")) ) 
                {
                    log.info("-----*FETCHING*THE*<BIOMED>*RESOURCES*-----");
                    bdii = new BDII(new URI(biomed_fratt_TOPBDII));
                    CEs_list_biomed = 
                            getListofCEForSoftwareTag(biomed_fratt_VONAME,
                                                      biomed_fratt_TOPBDII,
                                                      fratt_SOFTWARE);
                    
                    CEqueues_biomed = 
                            bdii.queryCEQueues(biomed_fratt_VONAME);
                }
                
                // Merging the list of CEs and queues
                List<String> CEs_list_TOT = new ArrayList<String>();
                if (cometa_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                        CEs_list_TOT.addAll(CEs_list_cometa);
                if (gridit_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                        CEs_list_TOT.addAll(CEs_list_gridit);
                if (eumed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                        CEs_list_TOT.addAll(CEs_list_eumed);
                if (biomed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                        CEs_list_TOT.addAll(CEs_list_biomed);
                
                List<String> CEs_queue_TOT = new ArrayList<String>();
                if (cometa_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                    CEs_queue_TOT.addAll(CEqueues_cometa);
                if (gridit_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                    CEs_queue_TOT.addAll(CEqueues_gridit);
                if (eumed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                    CEs_queue_TOT.addAll(CEqueues_eumed);
                if (biomed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
                    CEs_queue_TOT.addAll(CEqueues_biomed);                
                
                //=========================================================
                // IMPORTANT: INSTANCIATE THE UsersTrackingDBInterface
                //            CLASS USING THE EMPTY CONSTRUCTOR WHEN
                //            WHEN THE PORTLET IS DEPLOYED IN PRODUCTION!!!
                //=========================================================
                UsersTrackingDBInterface DBInterface =
                        new UsersTrackingDBInterface(
                                TRACKING_DB_HOSTNAME.trim(),
                                TRACKING_DB_USERNAME.trim(),
                                TRACKING_DB_PASSWORD.trim());
                
                /*UsersTrackingDBInterface DBInterface =
                        new UsersTrackingDBInterface();*/
                    
                if ( (CEs_list_TOT != null) && (!CEs_list_TOT.isEmpty()) )
                {
                    log.info("NOT EMPTY LIST!");
                    // Fetching the list of CEs publushing the SW
                    for (String CE:CEs_list_TOT) 
                    {
                        log.info("Fetching the CE="+CE);
                        Properties coordinates = new Properties();
                        Properties queue = new Properties();

                        float coords[] = DBInterface.getCECoordinate(CE);                        

                        String GPS_LAT = Float.toString(coords[0]);
                        String GPS_LNG = Float.toString(coords[1]);

                        coordinates.setProperty("LAT", GPS_LAT);
                        coordinates.setProperty("LNG", GPS_LNG);

                        // Fetching the Queues
                        for (String CEqueue:CEs_queue_TOT) {
                                if (CEqueue.contains(CE))
                                    queue.setProperty("QUEUE", CEqueue);
                        }

                        // Saving the GPS location in a Java HashMap
                        GPS_table.put(CE, coordinates);

                        // Saving the queue in a Java HashMap
                        GPS_queue.put(CE, queue);
                    }
                } else log.info ("EMPTY LIST!");
             } catch (URISyntaxException ex) {
               Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NamingException e) {}

            // Checking the HashMap
            Set set = GPS_table.entrySet();
            Iterator iter = set.iterator();
            while ( iter.hasNext() )
            {
                Map.Entry entry = (Map.Entry)iter.next();
                log.info(" - GPS location of the CE " +
                           entry.getKey() + " => " + entry.getValue());
            }

            // Checking the HashMap
            set = GPS_queue.entrySet();
            iter = set.iterator();
            while ( iter.hasNext() )
            {
                Map.Entry entry = (Map.Entry)iter.next();
                log.info(" - Queue " +
                           entry.getKey() + " => " + entry.getValue());
            }

            Gson gson = new GsonBuilder().create();
            request.setAttribute ("GPS_table", gson.toJson(GPS_table));
            request.setAttribute ("GPS_queue", gson.toJson(GPS_queue));

            // ********************************************************
            dispatcher = getPortletContext().getRequestDispatcher("/view.jsp");       
            dispatcher.include(request, response);
    }

    // The init method will be called when installing for the first time the portlet
    // This is the right time to setup the default values into the preferences
    @Override
    public void init() throws PortletException {
        super.init();
    }

    @Override
    public void processAction(ActionRequest request,
                              ActionResponse response)
                throws PortletException, IOException {

        String action = "";

        // Getting the action to be processed from the request
        action = request.getParameter("ActionEvent");

        // Determine the username and the email address
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);        
        User user = themeDisplay.getUser();
        String username = user.getScreenName();
        String emailAddress = user.getDisplayEmailAddress();        

        PortletPreferences portletPreferences =
                (PortletPreferences) request.getPreferences();

        if (action.equals("CONFIG_FRATT_PORTLET")) {
            log.info("\nPROCESS ACTION => " + action);
            
            // Getting the FRATT APPID from the portlet request
            String fratt_APPID = request.getParameter("fratt_APPID");
            // Getting the FRATT OUTPUT_PATH from the portlet request
            String fratt_OUTPUT_PATH = request.getParameter("fratt_OUTPUT_PATH");
            // Getting the FRATT SOFTWARE from the portlet request
            String fratt_SOFTWARE = request.getParameter("fratt_SOFTWARE");
            // Getting the TRACKING_DB_HOSTNAME from the portlet request
            String TRACKING_DB_HOSTNAME = request.getParameter("TRACKING_DB_HOSTNAME");
            // Getting the TRACKING_DB_USERNAME from the portlet request
            String TRACKING_DB_USERNAME = request.getParameter("TRACKING_DB_USERNAME");
            // Getting the TRACKING_DB_PASSWORD from the portlet request
            String TRACKING_DB_PASSWORD = request.getParameter("TRACKING_DB_PASSWORD");
            // Getting the SMTP_HOST from the portlet request
            String SMTP_HOST = request.getParameter("SMTP_HOST");
            // Getting the SENDER_MAIL from the portlet request
            String SENDER_MAIL = request.getParameter("SENDER_MAIL");
            String[] infras = new String[4];
            
            String cometa_fratt_ENABLEINFRASTRUCTURE = "unchecked";
            String gridit_fratt_ENABLEINFRASTRUCTURE = "unchecked";
            String eumed_fratt_ENABLEINFRASTRUCTURE = "unchecked";
            String biomed_fratt_ENABLEINFRASTRUCTURE = "unchecked";
            
            String[] fratt_INFRASTRUCTURES = request.getParameterValues("fratt_ENABLEINFRASTRUCTURES");         

            if (fratt_INFRASTRUCTURES != null) {
                Arrays.sort(fratt_INFRASTRUCTURES);
                cometa_fratt_ENABLEINFRASTRUCTURE =
                    Arrays.binarySearch(fratt_INFRASTRUCTURES, "cometa") >= 0 ? "checked" : "unchecked";
                gridit_fratt_ENABLEINFRASTRUCTURE =
                    Arrays.binarySearch(fratt_INFRASTRUCTURES, "gridit") >= 0 ? "checked" : "unchecked";
                eumed_fratt_ENABLEINFRASTRUCTURE =
                    Arrays.binarySearch(fratt_INFRASTRUCTURES, "eumed") >= 0 ? "checked" : "unchecked";
                biomed_fratt_ENABLEINFRASTRUCTURE =
                    Arrays.binarySearch(fratt_INFRASTRUCTURES, "biomed") >= 0 ? "checked" : "unchecked";
            }           
            
            if (cometa_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
            {
                infras[0]="cometa";
                 // Getting the FRATT INFRASTRUCTURE from the portlet request for the COMETA VO
                String cometa_fratt_INFRASTRUCTURE = request.getParameter("cometa_fratt_INFRASTRUCTURE");
                // Getting the FRATT VONAME from the portlet request for the COMETA VO
                String cometa_fratt_VONAME = request.getParameter("cometa_fratt_VONAME");
                // Getting the FRATT TOPBDII from the portlet request for the COMETA VO
                String cometa_fratt_TOPBDII = request.getParameter("cometa_fratt_TOPBDII");
                // Getting the FRATT WMS from the portlet request for the COMETA VO
                String[] cometa_fratt_WMS = request.getParameterValues("cometa_fratt_WMS");
                // Getting the FRATT ETOKENSERVER from the portlet request for the COMETA VO
                String cometa_fratt_ETOKENSERVER = request.getParameter("cometa_fratt_ETOKENSERVER");
                // Getting the FRATT MYPROXYSERVER from the portlet request for the COMETA VO
                String cometa_fratt_MYPROXYSERVER = request.getParameter("cometa_fratt_MYPROXYSERVER");
                // Getting the FRATT PORT from the portlet request for the COMETA VO
                String cometa_fratt_PORT = request.getParameter("cometa_fratt_PORT");
                // Getting the FRATT ROBOTID from the portlet request for the COMETA VO
                String cometa_fratt_ROBOTID = request.getParameter("cometa_fratt_ROBOTID");
                // Getting the FRATT ROLE from the portlet request for the COMETA VO
                String cometa_fratt_ROLE = request.getParameter("cometa_fratt_ROLE");
                // Getting the FRATT OPTIONS from the portlet request for the COMETA VO
                String[] cometa_fratt_OPTIONS = request.getParameterValues("cometa_fratt_OPTIONS");

                String cometa_fratt_RENEWAL = "";
                String cometa_fratt_DISABLEVOMS = "";

                if (cometa_fratt_OPTIONS == null){
                    cometa_fratt_RENEWAL = "checked";
                    cometa_fratt_DISABLEVOMS = "unchecked";
                } else {
                    Arrays.sort(cometa_fratt_OPTIONS);
                    // Getting the FRATT RENEWAL from the portlet preferences for the COMETA VO
                    cometa_fratt_RENEWAL = Arrays.binarySearch(cometa_fratt_OPTIONS, "enableRENEWAL") >= 0 ? "checked" : "unchecked";
                    // Getting the FRATT DISABLEVOMS from the portlet preferences for the COMETA VO
                    cometa_fratt_DISABLEVOMS = Arrays.binarySearch(cometa_fratt_OPTIONS, "disableVOMS") >= 0 ? "checked" : "unchecked";
                }
                
                int nmax=0;                
                for (int i = 0; i < cometa_fratt_WMS.length; i++)
                    if ( cometa_fratt_WMS[i]!=null && (!cometa_fratt_WMS[i].trim().equals("N/A")) )                        
                        nmax++;
                
                log.info("\n\nLength="+nmax);
                String[] cometa_fratt_WMS_trimmed = new String[nmax];
                for (int i = 0; i < nmax; i++)
                {
                    cometa_fratt_WMS_trimmed[i]=cometa_fratt_WMS[i].trim();
                    log.info ("\n\nCOMETA [" + i + "] WMS=[" + cometa_fratt_WMS_trimmed[i] + "]");
                }
                
                // Set the portlet preferences
                portletPreferences.setValue("cometa_fratt_INFRASTRUCTURE", cometa_fratt_INFRASTRUCTURE.trim());
                portletPreferences.setValue("cometa_fratt_VONAME", cometa_fratt_VONAME.trim());
                portletPreferences.setValue("cometa_fratt_TOPBDII", cometa_fratt_TOPBDII.trim());
                portletPreferences.setValues("cometa_fratt_WMS", cometa_fratt_WMS_trimmed);
                portletPreferences.setValue("cometa_fratt_ETOKENSERVER", cometa_fratt_ETOKENSERVER.trim());
                portletPreferences.setValue("cometa_fratt_MYPROXYSERVER", cometa_fratt_MYPROXYSERVER.trim());
                portletPreferences.setValue("cometa_fratt_PORT", cometa_fratt_PORT.trim());
                portletPreferences.setValue("cometa_fratt_ROBOTID", cometa_fratt_ROBOTID.trim());
                portletPreferences.setValue("cometa_fratt_ROLE", cometa_fratt_ROLE.trim());
                portletPreferences.setValue("cometa_fratt_RENEWAL", cometa_fratt_RENEWAL);
                portletPreferences.setValue("cometa_fratt_DISABLEVOMS", cometa_fratt_DISABLEVOMS);                
                
                portletPreferences.setValue("fratt_APPID", fratt_APPID.trim());
                portletPreferences.setValue("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
                portletPreferences.setValue("fratt_SOFTWARE", fratt_SOFTWARE.trim());
                portletPreferences.setValue("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
                portletPreferences.setValue("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
                portletPreferences.setValue("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
                portletPreferences.setValue("SMTP_HOST", SMTP_HOST.trim());
                portletPreferences.setValue("SENDER_MAIL", SENDER_MAIL.trim());
                
                log.info("\n\nPROCESS ACTION => " + action
                    + "\n- Storing the FRATT portlet preferences ..."
                    + "\ncometa_fratt_INFRASTRUCTURE: " + cometa_fratt_INFRASTRUCTURE
                    + "\ncometa_fratt_VONAME: " + cometa_fratt_VONAME
                    + "\ncometa_fratt_TOPBDII: " + cometa_fratt_TOPBDII                    
                    + "\ncometa_fratt_ETOKENSERVER: " + cometa_fratt_ETOKENSERVER
                    + "\ncometa_fratt_MYPROXYSERVER: " + cometa_fratt_MYPROXYSERVER
                    + "\ncometa_fratt_PORT: " + cometa_fratt_PORT
                    + "\ncometa_fratt_ROBOTID: " + cometa_fratt_ROBOTID
                    + "\ncometa_fratt_ROLE: " + cometa_fratt_ROLE
                    + "\ncometa_fratt_RENEWAL: " + cometa_fratt_RENEWAL
                    + "\ncometa_fratt_DISABLEVOMS: " + cometa_fratt_DISABLEVOMS
                        
                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + "cometa"
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
            }

            if (gridit_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
            {
                infras[1]="gridit";
                 // Getting the FRATT INFRASTRUCTURE from the portlet request for the GRIDIT VO
                String gridit_fratt_INFRASTRUCTURE = request.getParameter("gridit_fratt_INFRASTRUCTURE");
                // Getting the FRATT VONAME from the portlet request for the GRIDIT VO
                String gridit_fratt_VONAME = request.getParameter("gridit_fratt_VONAME");
                // Getting the FRATT TOPBDII from the portlet request for the GRIDIT VO
                String gridit_fratt_TOPBDII = request.getParameter("gridit_fratt_TOPBDII");
                // Getting the FRATT WMS from the portlet request for the GRIDIT VO
                String[] gridit_fratt_WMS = request.getParameterValues("gridit_fratt_WMS");
                // Getting the FRATT ETOKENSERVER from the portlet request for the GRIDIT VO
                String gridit_fratt_ETOKENSERVER = request.getParameter("gridit_fratt_ETOKENSERVER");
                // Getting the FRATT MYPROXYSERVER from the portlet request for the GRIDIT VO
                String gridit_fratt_MYPROXYSERVER = request.getParameter("gridit_fratt_MYPROXYSERVER");
                // Getting the FRATT PORT from the portlet request for the GRIDIT VO
                String gridit_fratt_PORT = request.getParameter("gridit_fratt_PORT");
                // Getting the FRATT ROBOTID from the portlet request for the GRIDIT VO
                String gridit_fratt_ROBOTID = request.getParameter("gridit_fratt_ROBOTID");
                // Getting the FRATT ROLE from the portlet request for the GRIDIT VO
                String gridit_fratt_ROLE = request.getParameter("gridit_fratt_ROLE");
                // Getting the FRATT OPTIONS from the portlet request for the GRIDIT VO
                String[] gridit_fratt_OPTIONS = request.getParameterValues("gridit_fratt_OPTIONS");

                String gridit_fratt_RENEWAL = "";
                String gridit_fratt_DISABLEVOMS = "";

                if (gridit_fratt_OPTIONS == null){
                    gridit_fratt_RENEWAL = "checked";
                    gridit_fratt_DISABLEVOMS = "unchecked";
                } else {
                    Arrays.sort(gridit_fratt_OPTIONS);
                    // Getting the FRATT RENEWAL from the portlet preferences for the GRIDIT VO
                    gridit_fratt_RENEWAL = Arrays.binarySearch(gridit_fratt_OPTIONS, "enableRENEWAL") >= 0 ? "checked" : "unchecked";
                    // Getting the FRATT DISABLEVOMS from the portlet preferences for the GRIDIT VO
                    gridit_fratt_DISABLEVOMS = Arrays.binarySearch(gridit_fratt_OPTIONS, "disableVOMS") >= 0 ? "checked" : "unchecked";
                }
                
                int nmax=0;                
                for (int i = 0; i < gridit_fratt_WMS.length; i++)
                    if ( gridit_fratt_WMS[i]!=null && (!gridit_fratt_WMS[i].trim().equals("N/A")) )                        
                        nmax++;
                
                log.info("\n\nLength="+nmax);
                String[] gridit_fratt_WMS_trimmed = new String[nmax];
                for (int i = 0; i < nmax; i++)
                {
                    gridit_fratt_WMS_trimmed[i]=gridit_fratt_WMS[i].trim();
                    log.info ("\n\nCOMETA [" + i + "] WMS=[" + gridit_fratt_WMS_trimmed[i] + "]");
                }
                
                // Set the portlet preferences
                portletPreferences.setValue("gridit_fratt_INFRASTRUCTURE", gridit_fratt_INFRASTRUCTURE.trim());
                portletPreferences.setValue("gridit_fratt_VONAME", gridit_fratt_VONAME.trim());
                portletPreferences.setValue("gridit_fratt_TOPBDII", gridit_fratt_TOPBDII.trim());
                portletPreferences.setValues("gridit_fratt_WMS", gridit_fratt_WMS_trimmed);
                portletPreferences.setValue("gridit_fratt_ETOKENSERVER", gridit_fratt_ETOKENSERVER.trim());
                portletPreferences.setValue("gridit_fratt_MYPROXYSERVER", gridit_fratt_MYPROXYSERVER.trim());
                portletPreferences.setValue("gridit_fratt_PORT", gridit_fratt_PORT.trim());
                portletPreferences.setValue("gridit_fratt_ROBOTID", gridit_fratt_ROBOTID.trim());
                portletPreferences.setValue("gridit_fratt_ROLE", gridit_fratt_ROLE.trim());
                portletPreferences.setValue("gridit_fratt_RENEWAL", gridit_fratt_RENEWAL);
                portletPreferences.setValue("gridit_fratt_DISABLEVOMS", gridit_fratt_DISABLEVOMS);                
                
                portletPreferences.setValue("fratt_APPID", fratt_APPID.trim());
                portletPreferences.setValue("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
                portletPreferences.setValue("fratt_SOFTWARE", fratt_SOFTWARE.trim());
                portletPreferences.setValue("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
                portletPreferences.setValue("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
                portletPreferences.setValue("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
                portletPreferences.setValue("SMTP_HOST", SMTP_HOST.trim());
                portletPreferences.setValue("SENDER_MAIL", SENDER_MAIL.trim());
                
                log.info("\n\nPROCESS ACTION => " + action
                    + "\n- Storing the FRATT portlet preferences ..."
                    + "\ngridit_fratt_INFRASTRUCTURE: " + gridit_fratt_INFRASTRUCTURE
                    + "\ngridit_fratt_VONAME: " + gridit_fratt_VONAME
                    + "\ngridit_fratt_TOPBDII: " + gridit_fratt_TOPBDII                    
                    + "\ngridit_fratt_ETOKENSERVER: " + gridit_fratt_ETOKENSERVER
                    + "\ngridit_fratt_MYPROXYSERVER: " + gridit_fratt_MYPROXYSERVER
                    + "\ngridit_fratt_PORT: " + gridit_fratt_PORT
                    + "\ngridit_fratt_ROBOTID: " + gridit_fratt_ROBOTID
                    + "\ngridit_fratt_ROLE: " + gridit_fratt_ROLE
                    + "\ngridit_fratt_RENEWAL: " + gridit_fratt_RENEWAL
                    + "\ngridit_fratt_DISABLEVOMS: " + gridit_fratt_DISABLEVOMS
                        
                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + "gridit"
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
            }

            if (eumed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
            {
                infras[2]="eumed";
                // Getting the FRATT INFRASTRUCTURE from the portlet request for the EUMED VO
                String eumed_fratt_INFRASTRUCTURE = request.getParameter("eumed_fratt_INFRASTRUCTURE");
                // Getting the FRATT VONAME from the portlet request for the EUMED VO
                String eumed_fratt_VONAME = request.getParameter("eumed_fratt_VONAME");
                // Getting the FRATT TOPBDII from the portlet request for the EUMED VO
                String eumed_fratt_TOPBDII = request.getParameter("eumed_fratt_TOPBDII");
                // Getting the FRATT WMS from the portlet request for the EUMED VO
                String[] eumed_fratt_WMS = request.getParameterValues("eumed_fratt_WMS");
                // Getting the FRATT ETOKENSERVER from the portlet request for the EUMED VO
                String eumed_fratt_ETOKENSERVER = request.getParameter("eumed_fratt_ETOKENSERVER");
                // Getting the FRATT MYPROXYSERVER from the portlet request for the EUMED VO
                String eumed_fratt_MYPROXYSERVER = request.getParameter("eumed_fratt_MYPROXYSERVER");
                // Getting the FRATT PORT from the portlet request for the EUMED VO
                String eumed_fratt_PORT = request.getParameter("eumed_fratt_PORT");
                // Getting the FRATT ROBOTID from the portlet request for the EUMED VO
                String eumed_fratt_ROBOTID = request.getParameter("eumed_fratt_ROBOTID");
                // Getting the FRATT ROLE from the portlet request for the EUMED VO
                String eumed_fratt_ROLE = request.getParameter("eumed_fratt_ROLE");
                // Getting the FRATT OPTIONS from the portlet request for the EUMED VO
                String[] eumed_fratt_OPTIONS = request.getParameterValues("eumed_fratt_OPTIONS");

                String eumed_fratt_RENEWAL = "";
                String eumed_fratt_DISABLEVOMS = "";

                if (eumed_fratt_OPTIONS == null){
                    eumed_fratt_RENEWAL = "checked";
                    eumed_fratt_DISABLEVOMS = "unchecked";
                } else {
                    Arrays.sort(eumed_fratt_OPTIONS);
                    // Getting the FRATT RENEWAL from the portlet preferences for the EUMED VO
                    eumed_fratt_RENEWAL = Arrays.binarySearch(eumed_fratt_OPTIONS, "enableRENEWAL") >= 0 ? "checked" : "unchecked";
                    // Getting the FRATT DISABLEVOMS from the portlet preferences for the GRIDIT VO
                    eumed_fratt_DISABLEVOMS = Arrays.binarySearch(eumed_fratt_OPTIONS, "disableVOMS") >= 0 ? "checked" : "unchecked";
                }
                
                int nmax=0;                
                for (int i = 0; i < eumed_fratt_WMS.length; i++)
                    if ( eumed_fratt_WMS[i]!=null && (!eumed_fratt_WMS[i].trim().equals("N/A")) )                        
                        nmax++;
                
                log.info("\n\nLength="+nmax);
                String[] eumed_fratt_WMS_trimmed = new String[nmax];
                for (int i = 0; i < nmax; i++)
                {
                    eumed_fratt_WMS_trimmed[i]=eumed_fratt_WMS[i].trim();
                    log.info ("\n\nEUMED [" + i + "] WMS=[" + eumed_fratt_WMS_trimmed[i] + "]");
                }
                
                // Set the portlet preferences
                portletPreferences.setValue("eumed_fratt_INFRASTRUCTURE", eumed_fratt_INFRASTRUCTURE.trim());
                portletPreferences.setValue("eumed_fratt_VONAME", eumed_fratt_VONAME.trim());
                portletPreferences.setValue("eumed_fratt_TOPBDII", eumed_fratt_TOPBDII.trim());
                portletPreferences.setValues("eumed_fratt_WMS", eumed_fratt_WMS_trimmed);
                portletPreferences.setValue("eumed_fratt_ETOKENSERVER", eumed_fratt_ETOKENSERVER.trim());
                portletPreferences.setValue("eumed_fratt_MYPROXYSERVER", eumed_fratt_MYPROXYSERVER.trim());
                portletPreferences.setValue("eumed_fratt_PORT", eumed_fratt_PORT.trim());
                portletPreferences.setValue("eumed_fratt_ROBOTID", eumed_fratt_ROBOTID.trim());
                portletPreferences.setValue("eumed_fratt_ROLE", eumed_fratt_ROLE.trim());
                portletPreferences.setValue("eumed_fratt_RENEWAL", eumed_fratt_RENEWAL);
                portletPreferences.setValue("eumed_fratt_DISABLEVOMS", eumed_fratt_DISABLEVOMS); 
                
                portletPreferences.setValue("fratt_APPID", fratt_APPID.trim());
                portletPreferences.setValue("fratt_OUTPATH_PATH", fratt_OUTPUT_PATH.trim());
                portletPreferences.setValue("fratt_SOFTWARE", fratt_SOFTWARE.trim());
                portletPreferences.setValue("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
                portletPreferences.setValue("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
                portletPreferences.setValue("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
                portletPreferences.setValue("SMTP_HOST", SMTP_HOST.trim());
                portletPreferences.setValue("SENDER_MAIL", SENDER_MAIL.trim());
                
                log.info("\n\nPROCESS ACTION => " + action
                    + "\n- Storing the FRATT portlet preferences ..."                    
                    + "\n\neumed_fratt_INFRASTRUCTURE: " + eumed_fratt_INFRASTRUCTURE
                    + "\neumed_fratt_VONAME: " + eumed_fratt_VONAME
                    + "\neumed_fratt_TOPBDII: " + eumed_fratt_TOPBDII                    
                    + "\neumed_fratt_ETOKENSERVER: " + eumed_fratt_ETOKENSERVER
                    + "\neumed_fratt_MYPROXYSERVER: " + eumed_fratt_MYPROXYSERVER
                    + "\neumed_fratt_PORT: " + eumed_fratt_PORT
                    + "\neumed_fratt_ROBOTID: " + eumed_fratt_ROBOTID
                    + "\neumed_fratt_ROLE: " + eumed_fratt_ROLE
                    + "\neumed_fratt_RENEWAL: " + eumed_fratt_RENEWAL
                    + "\neumed_fratt_DISABLEVOMS: " + eumed_fratt_DISABLEVOMS

                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + "eumed"
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
            }

            if (biomed_fratt_ENABLEINFRASTRUCTURE.equals("checked"))
            {
                infras[3]="biomed";
                // Getting the FRATT INFRASTRUCTURE from the portlet request for the BIOMED VO
                String biomed_fratt_INFRASTRUCTURE = request.getParameter("biomed_fratt_INFRASTRUCTURE");
                // Getting the FRATT VONAME from the portlet request for the BIOMED VO
                String biomed_fratt_VONAME = request.getParameter("biomed_fratt_VONAME");
                // Getting the FRATT TOPBDII from the portlet request for the BIOMED VO
                String biomed_fratt_TOPBDII = request.getParameter("biomed_fratt_TOPBDII");
                // Getting the FRATT WMS from the portlet request for the BIOMED VO
                String[] biomed_fratt_WMS = request.getParameterValues("biomed_fratt_WMS");
                // Getting the FRATT ETOKENSERVER from the portlet request for the BIOMED VO
                String biomed_fratt_ETOKENSERVER = request.getParameter("biomed_fratt_ETOKENSERVER");
                // Getting the FRATT MYPROXYSERVER from the portlet request for the BIOMED VO
                String biomed_fratt_MYPROXYSERVER = request.getParameter("biomed_fratt_MYPROXYSERVER");
                // Getting the FRATT PORT from the portlet request for the BIOMED VO
                String biomed_fratt_PORT = request.getParameter("biomed_fratt_PORT");
                // Getting the FRATT ROBOTID from the portlet request for the BIOMED VO
                String biomed_fratt_ROBOTID = request.getParameter("biomed_fratt_ROBOTID");
                // Getting the FRATT ROLE from the portlet request for the BIOMED VO
                String biomed_fratt_ROLE = request.getParameter("biomed_fratt_ROLE");
                // Getting the FRATT OPTIONS from the portlet request for the BIOMED VO
                String[] biomed_fratt_OPTIONS = request.getParameterValues("biomed_fratt_OPTIONS");

                String biomed_fratt_RENEWAL = "";
                String biomed_fratt_DISABLEVOMS = "";

                if (biomed_fratt_OPTIONS == null){
                    biomed_fratt_RENEWAL = "checked";
                    biomed_fratt_DISABLEVOMS = "unchecked";
                } else {
                    Arrays.sort(biomed_fratt_OPTIONS);
                    // Get the FRATT RENEWAL from the portlet preferences for the BIOMED VO
                    biomed_fratt_RENEWAL = Arrays.binarySearch(biomed_fratt_OPTIONS, "enableRENEWAL") >= 0 ? "checked" : "unchecked";
                    // Get the FRATT DISABLEVOMS from the portlet preferences for the BIOMED VO
                    biomed_fratt_DISABLEVOMS = Arrays.binarySearch(biomed_fratt_OPTIONS, "disableVOMS") >= 0 ? "checked" : "unchecked";
                }
                
                int nmax=0;                
                for (int i = 0; i < biomed_fratt_WMS.length; i++)
                    if ( biomed_fratt_WMS[i]!=null && (!biomed_fratt_WMS[i].trim().equals("N/A")) )                        
                        nmax++;
                
                log.info("\n\nLength="+nmax);
                String[] biomed_fratt_WMS_trimmed = new String[nmax];
                for (int i = 0; i < nmax; i++)
                {
                    biomed_fratt_WMS_trimmed[i]=biomed_fratt_WMS[i].trim();
                    log.info ("\n\nBIOMED [" + i + "] WMS=[" + biomed_fratt_WMS_trimmed[i] + "]");
                }

                // Set the portlet preferences
                portletPreferences.setValue("biomed_fratt_INFRASTRUCTURE", biomed_fratt_INFRASTRUCTURE.trim());
                portletPreferences.setValue("biomed_fratt_VONAME", biomed_fratt_VONAME.trim());
                portletPreferences.setValue("biomed_fratt_TOPBDII", biomed_fratt_TOPBDII.trim());
                portletPreferences.setValues("biomed_fratt_WMS", biomed_fratt_WMS_trimmed);
                portletPreferences.setValue("biomed_fratt_ETOKENSERVER", biomed_fratt_ETOKENSERVER.trim());
                portletPreferences.setValue("biomed_fratt_MYPROXYSERVER", biomed_fratt_MYPROXYSERVER.trim());
                portletPreferences.setValue("biomed_fratt_PORT", biomed_fratt_PORT.trim());
                portletPreferences.setValue("biomed_fratt_ROBOTID", biomed_fratt_ROBOTID.trim());
                portletPreferences.setValue("biomed_fratt_ROLE", biomed_fratt_ROLE.trim());
                portletPreferences.setValue("biomed_fratt_RENEWAL", biomed_fratt_RENEWAL);
                portletPreferences.setValue("biomed_fratt_DISABLEVOMS", biomed_fratt_DISABLEVOMS);
                
                portletPreferences.setValue("fratt_APPID", fratt_APPID.trim());
                portletPreferences.setValue("fratt_OUTPUT_PATH", fratt_OUTPUT_PATH.trim());
                portletPreferences.setValue("fratt_SOFTWARE", fratt_SOFTWARE.trim());
                portletPreferences.setValue("TRACKING_DB_HOSTNAME", TRACKING_DB_HOSTNAME.trim());
                portletPreferences.setValue("TRACKING_DB_USERNAME", TRACKING_DB_USERNAME.trim());
                portletPreferences.setValue("TRACKING_DB_PASSWORD", TRACKING_DB_PASSWORD.trim());
                portletPreferences.setValue("SMTP_HOST", SMTP_HOST.trim());
                portletPreferences.setValue("SENDER_MAIL", SENDER_MAIL.trim());
                
                log.info("\n\nPROCESS ACTION => " + action
                    + "\n- Storing the FRATT portlet preferences ..."                    
                    + "\n\nbiomed_fratt_INFRASTRUCTURE: " + biomed_fratt_INFRASTRUCTURE
                    + "\nbiomed_fratt_VONAME: " + biomed_fratt_VONAME
                    + "\nbiomed_fratt_TOPBDII: " + biomed_fratt_TOPBDII                    
                    + "\nbiomed_fratt_ETOKENSERVER: " + biomed_fratt_ETOKENSERVER
                    + "\nbiomed_fratt_MYPROXYSERVER: " + biomed_fratt_MYPROXYSERVER
                    + "\nbiomed_fratt_PORT: " + biomed_fratt_PORT
                    + "\nbiomed_fratt_ROBOTID: " + biomed_fratt_ROBOTID
                    + "\nbiomed_fratt_ROLE: " + biomed_fratt_ROLE
                    + "\nbiomed_fratt_RENEWAL: " + biomed_fratt_RENEWAL
                    + "\nbiomed_fratt_DISABLEVOMS: " + biomed_fratt_DISABLEVOMS

                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + "biomed"
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
            }
            
            for (int i=0; i<infras.length; i++)
            log.info("\n - Infrastructure Enabled = " + infras[i]);
            portletPreferences.setValues("fratt_ENABLEINFRASTRUCTURE", infras);
            portletPreferences.setValue("cometa_fratt_ENABLEINFRASTRUCTURE",infras[0]);            
            portletPreferences.setValue("gridit_fratt_ENABLEINFRASTRUCTURE",infras[1]);            
            portletPreferences.setValue("eumed_fratt_ENABLEINFRASTRUCTURE",infras[2]);
            portletPreferences.setValue("biomed_fratt_ENABLEINFRASTRUCTURE",infras[3]);            

            portletPreferences.store();
            response.setPortletMode(PortletMode.VIEW);
        } // end PROCESS ACTION [ CONFIG_FRATT_PORTLET ]
        

        if (action.equals("SUBMIT_FRATT_PORTLET")) {
            log.info("\nPROCESS ACTION => " + action);            
            InfrastructureInfo infrastructures[] = new InfrastructureInfo[4];
            int MAX=0;
            
            // Getting the FRATT APPID from the portlet preferences
            String fratt_APPID = portletPreferences.getValue("fratt_APPID", "N/A");
            // Getting the FRATT OUTPUT_PATH from the portlet preferences
            String fratt_OUTPUT_PATH = portletPreferences.getValue("fratt_OUTPUT_PATH", "/tmp");
            // Getting the FRATT SOFTWARE from the portlet preferences
            String fratt_SOFTWARE = portletPreferences.getValue("fratt_SOFTWARE", "N/A");
            // Getting the TRACKING_DB_HOSTNAME from the portlet request
            String TRACKING_DB_HOSTNAME = portletPreferences.getValue("TRACKING_DB_HOSTNAME", "N/A");
            // Getting the TRACKING_DB_USERNAME from the portlet request
            String TRACKING_DB_USERNAME = portletPreferences.getValue("TRACKING_DB_USERNAME", "N/A");
            // Getting the TRACKING_DB_PASSWORD from the portlet request
            String TRACKING_DB_PASSWORD = portletPreferences.getValue("TRACKING_DB_PASSWORD","N/A");
            // Getting the SMTP_HOST from the portlet request
            String SMTP_HOST = portletPreferences.getValue("SMTP_HOST","N/A");
            // Getting the SENDER_MAIL from the portlet request
            String SENDER_MAIL = portletPreferences.getValue("SENDER_MAIL","N/A");
            
            String cometa_fratt_ENABLEINFRASTRUCTURE =
                    portletPreferences.getValue("cometa_fratt_ENABLEINFRASTRUCTURE","null");
            String gridit_fratt_ENABLEINFRASTRUCTURE =
                    portletPreferences.getValue("gridit_fratt_ENABLEINFRASTRUCTURE","null");
            String eumed_fratt_ENABLEINFRASTRUCTURE =
                    portletPreferences.getValue("eumed_fratt_ENABLEINFRASTRUCTURE","null");
            String biomed_fratt_ENABLEINFRASTRUCTURE =
                    portletPreferences.getValue("biomed_fratt_ENABLEINFRASTRUCTURE","null");
            
            if (cometa_fratt_ENABLEINFRASTRUCTURE != null &&
                cometa_fratt_ENABLEINFRASTRUCTURE.equals("cometa"))
            {
                MAX++;
                // Getting the FRATT VONAME from the portlet preferences for the COMETA VO
                String cometa_fratt_INFRASTRUCTURE = portletPreferences.getValue("cometa_fratt_INFRASTRUCTURE", "N/A");
                // Getting the FRATT VONAME from the portlet preferences for the COMETA VO
                String cometa_fratt_VONAME = portletPreferences.getValue("cometa_fratt_VONAME", "N/A");
                // Getting the FRATT TOPPBDII from the portlet preferences for the COMETA VO
                String cometa_fratt_TOPBDII = portletPreferences.getValue("cometa_fratt_TOPBDII", "N/A");
                // Getting the FRATT WMS from the portlet preferences for the COMETA VO                
                String[] cometa_fratt_WMS = portletPreferences.getValues("cometa_fratt_WMS", new String[5]);
                // Getting the FRATT ETOKENSERVER from the portlet preferences for the COMETA VO
                String cometa_fratt_ETOKENSERVER = portletPreferences.getValue("cometa_fratt_ETOKENSERVER", "N/A");
                // Getting the FRATT MYPROXYSERVER from the portlet preferences for the COMETA VO
                String cometa_fratt_MYPROXYSERVER = portletPreferences.getValue("cometa_fratt_MYPROXYSERVER", "N/A");
                // Getting the FRATT PORT from the portlet preferences for the COMETA VO
                String cometa_fratt_PORT = portletPreferences.getValue("cometa_fratt_PORT", "N/A");
                // Getting the FRATT ROBOTID from the portlet preferences for the COMETA VO
                String cometa_fratt_ROBOTID = portletPreferences.getValue("cometa_fratt_ROBOTID", "N/A");
                // Getting the FRATT ROLE from the portlet preferences for the COMETA VO
                String cometa_fratt_ROLE = portletPreferences.getValue("cometa_fratt_ROLE", "N/A");
                // Getting the FRATT RENEWAL from the portlet preferences for the COMETA VO
                String cometa_fratt_RENEWAL = portletPreferences.getValue("cometa_fratt_RENEWAL", "checked");
                // Getting the FRATT DISABLEVOMS from the portlet preferences for the COMETA VO
                String cometa_fratt_DISABLEVOMS = portletPreferences.getValue("cometa_fratt_DISABLEVOMS", "unchecked");
                
                log.info("\n- Getting the IOR portlet preferences ..."
                    + "\ncometa_fratt_INFRASTRUCTURE: " + cometa_fratt_INFRASTRUCTURE
                    + "\ncometa_fratt_VONAME: " + cometa_fratt_VONAME
                    + "\ncometa_fratt_TOPBDII: " + cometa_fratt_TOPBDII                    
                    + "\ncometa_fratt_ETOKENSERVER: " + cometa_fratt_ETOKENSERVER
                    + "\ncometa_fratt_MYPROXYSERVER: " + cometa_fratt_MYPROXYSERVER
                    + "\ncometa_fratt_PORT: " + cometa_fratt_PORT
                    + "\ncometa_fratt_ROBOTID: " + cometa_fratt_ROBOTID
                    + "\ncometa_fratt_ROLE: " + cometa_fratt_ROLE
                    + "\ncometa_fratt_RENEWAL: " + cometa_fratt_RENEWAL
                    + "\ncometa_fratt_DISABLEVOMS: " + cometa_fratt_DISABLEVOMS
                   
                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + cometa_fratt_ENABLEINFRASTRUCTURE
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
                
                // Defining the WMS list for the "COMETA" Infrastructure
                int nmax=0;
                for (int i = 0; i < cometa_fratt_WMS.length; i++)
                    if ((cometa_fratt_WMS[i]!=null) && (!cometa_fratt_WMS[i].equals("N/A"))) nmax++;

                String wmsList[] = new String [nmax];
                for (int i = 0; i < nmax; i++)
                {
                    if (cometa_fratt_WMS[i]!=null) {
                    wmsList[i]=cometa_fratt_WMS[i].trim();
                    log.info ("\n\n[" + nmax
                                      + "] Submitting to COMETA ["
                                      + i
                                      + "] using WMS=["
                                      + wmsList[i]
                                      + "]");
                    }
                }

                infrastructures[0] = new InfrastructureInfo(
                    cometa_fratt_VONAME,
                    cometa_fratt_TOPBDII,
                    wmsList,
                    cometa_fratt_ETOKENSERVER,
                    cometa_fratt_PORT,
                    cometa_fratt_ROBOTID,
                    cometa_fratt_VONAME,
                    cometa_fratt_ROLE,
                    "VO-" + cometa_fratt_VONAME + "-" + fratt_SOFTWARE);
            }
                        
            if (gridit_fratt_ENABLEINFRASTRUCTURE != null &&
                gridit_fratt_ENABLEINFRASTRUCTURE.equals("gridit"))
            {
                MAX++;
                // Getting the FRATTVONAME from the portlet preferences for the GRIDIT VO
                String gridit_fratt_INFRASTRUCTURE = portletPreferences.getValue("gridit_fratt_INFRASTRUCTURE", "N/A");
                // Getting the FRATT VONAME from the portlet preferences for the GRIDIT VO
                String gridit_fratt_VONAME = portletPreferences.getValue("gridit_fratt_VONAME", "N/A");
                // Getting the FRATT TOPPBDII from the portlet preferences for the GRIDIT VO
                String gridit_fratt_TOPBDII = portletPreferences.getValue("gridit_fratt_TOPBDII", "N/A");
                // Getting the FRATT WMS from the portlet preferences for the GRIDIT VO                
                String[] gridit_fratt_WMS = portletPreferences.getValues("gridit_fratt_WMS", new String[5]);
                // Getting the FRATT ETOKENSERVER from the portlet preferences for the GRIDIT VO
                String gridit_fratt_ETOKENSERVER = portletPreferences.getValue("gridit_fratt_ETOKENSERVER", "N/A");
                // Getting the FRATT MYPROXYSERVER from the portlet preferences for the GRIDIT VO
                String gridit_fratt_MYPROXYSERVER = portletPreferences.getValue("gridit_fratt_MYPROXYSERVER", "N/A");
                // Getting the FRATT PORT from the portlet preferences for the GRIDIT VO
                String gridit_fratt_PORT = portletPreferences.getValue("gridit_fratt_PORT", "N/A");
                // Getting the FRATT ROBOTID from the portlet preferences for the GRIDIT VO
                String gridit_fratt_ROBOTID = portletPreferences.getValue("gridit_fratt_ROBOTID", "N/A");
                // Getting the FRATT ROLE from the portlet preferences for the GRIDIT VO
                String gridit_fratt_ROLE = portletPreferences.getValue("gridit_fratt_ROLE", "N/A");
                // Getting the FRATT RENEWAL from the portlet preferences for the GRIDIT VO
                String gridit_fratt_RENEWAL = portletPreferences.getValue("gridit_fratt_RENEWAL", "checked");
                // Getting the FRATT DISABLEVOMS from the portlet preferences for the GRIDIT VO
                String gridit_fratt_DISABLEVOMS = portletPreferences.getValue("gridit_fratt_DISABLEVOMS", "unchecked");
                
                log.info("\n- Getting the FRATT portlet preferences ..."
                    + "\ngridit_fratt_INFRASTRUCTURE: " + gridit_fratt_INFRASTRUCTURE
                    + "\ngridit_fratt_VONAME: " + gridit_fratt_VONAME
                    + "\ngridit_fratt_TOPBDII: " + gridit_fratt_TOPBDII                    
                    + "\ngridit_fratt_ETOKENSERVER: " + gridit_fratt_ETOKENSERVER
                    + "\ngridit_fratt_MYPROXYSERVER: " + gridit_fratt_MYPROXYSERVER
                    + "\ngridit_fratt_PORT: " + gridit_fratt_PORT
                    + "\ngridit_fratt_ROBOTID: " + gridit_fratt_ROBOTID
                    + "\ngridit_fratt_ROLE: " + gridit_fratt_ROLE
                    + "\ngridit_fratt_RENEWAL: " + gridit_fratt_RENEWAL
                    + "\ngridit_fratt_DISABLEVOMS: " + gridit_fratt_DISABLEVOMS
                   
                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + gridit_fratt_ENABLEINFRASTRUCTURE
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
                
                // Defining the WMS list for the "GRIDIT" Infrastructure
                int nmax=0;
                for (int i = 0; i < gridit_fratt_WMS.length; i++)
                    if ((gridit_fratt_WMS[i]!=null) && (!gridit_fratt_WMS[i].equals("N/A"))) nmax++;

                String wmsList[] = new String [nmax];
                for (int i = 0; i < nmax; i++)
                {
                    if (gridit_fratt_WMS[i]!=null) {
                    wmsList[i]=gridit_fratt_WMS[i].trim();
                    log.info ("\n\n[" + nmax
                                      + "] Submitting to GRIDIT ["
                                      + i
                                      + "] using WMS=["
                                      + wmsList[i]
                                      + "]");
                    }
                }

                infrastructures[1] = new InfrastructureInfo(
                    gridit_fratt_VONAME,
                    gridit_fratt_TOPBDII,
                    wmsList,
                    gridit_fratt_ETOKENSERVER,
                    gridit_fratt_PORT,
                    gridit_fratt_ROBOTID,
                    gridit_fratt_VONAME,
                    gridit_fratt_ROLE,
                    "VO-" + gridit_fratt_VONAME + "-" + fratt_SOFTWARE);
            }
            
            if (eumed_fratt_ENABLEINFRASTRUCTURE != null &&
                eumed_fratt_ENABLEINFRASTRUCTURE.equals("eumed"))
            {
                MAX++;
                // Getting the FRATT VONAME from the portlet preferences for the EUMED VO
                String eumed_fratt_INFRASTRUCTURE = portletPreferences.getValue("eumed_fratt_INFRASTRUCTURE", "N/A");
                // Getting the FRATT VONAME from the portlet preferences for the EUMED VO
                String eumed_fratt_VONAME = portletPreferences.getValue("eumed_fratt_VONAME", "N/A");
                // Getting the FRATT TOPPBDII from the portlet preferences for the EUMED VO
                String eumed_fratt_TOPBDII = portletPreferences.getValue("eumed_fratt_TOPBDII", "N/A");
                // Getting the FRATT WMS from the portlet preferences for the EUMED VO
                String[] eumed_fratt_WMS = portletPreferences.getValues("eumed_fratt_WMS", new String[5]);
                // Getting the FRATT ETOKENSERVER from the portlet preferences for the EUMED VO
                String eumed_fratt_ETOKENSERVER = portletPreferences.getValue("eumed_fratt_ETOKENSERVER", "N/A");
                // Getting the FRATT MYPROXYSERVER from the portlet preferences for the EUMED VO
                String eumed_fratt_MYPROXYSERVER = portletPreferences.getValue("eumed_fratt_MYPROXYSERVER", "N/A");
                // Getting the FRATT PORT from the portlet preferences for the EUMED VO
                String eumed_fratt_PORT = portletPreferences.getValue("eumed_fratt_PORT", "N/A");
                // Getting the FRATT ROBOTID from the portlet preferences for the EUMED VO
                String eumed_fratt_ROBOTID = portletPreferences.getValue("eumed_fratt_ROBOTID", "N/A");
                // Getting the FRATT ROLE from the portlet preferences for the EUMED VO
                String eumed_fratt_ROLE = portletPreferences.getValue("eumed_fratt_ROLE", "N/A");
                // Getting the FRATT RENEWAL from the portlet preferences for the EUMED VO
                String eumed_fratt_RENEWAL = portletPreferences.getValue("eumed_fratt_RENEWAL", "checked");
                // Getting the FRATT DISABLEVOMS from the portlet preferences for the EUMED VO
                String eumed_fratt_DISABLEVOMS = portletPreferences.getValue("eumed_fratt_DISABLEVOMS", "unchecked");
                
                log.info("\n- Getting the IOR portlet preferences ..."
                    + "\n\neumed_fratt_INFRASTRUCTURE: " + eumed_fratt_INFRASTRUCTURE
                    + "\neumed_fratt_VONAME: " + eumed_fratt_VONAME
                    + "\neumed_fratt_TOPBDII: " + eumed_fratt_TOPBDII                    
                    + "\neumed_fratt_ETOKENSERVER: " + eumed_fratt_ETOKENSERVER
                    + "\neumed_fratt_MYPROXYSERVER: " + eumed_fratt_MYPROXYSERVER
                    + "\neumed_fratt_PORT: " + eumed_fratt_PORT
                    + "\neumed_fratt_ROBOTID: " + eumed_fratt_ROBOTID
                    + "\neumed_fratt_ROLE: " + eumed_fratt_ROLE
                    + "\neumed_fratt_RENEWAL: " + eumed_fratt_RENEWAL
                    + "\neumed_fratt_DISABLEVOMS: " + eumed_fratt_DISABLEVOMS

                    + "\n\nfratt_ENABLEINFRASTRUCTURE: " + eumed_fratt_ENABLEINFRASTRUCTURE
                    + "\nfratt_APPID: " + fratt_APPID
                    + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH
                    + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                    + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                    + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                    + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                    + "\nSMTP_HOST: " + SMTP_HOST
                    + "\nSENDER_MAIL: " + SENDER_MAIL);
                
                // Defining the WMS list for the "EUMED" Infrastructure
                int nmax=0;
                for (int i = 0; i < eumed_fratt_WMS.length; i++)
                    if ((eumed_fratt_WMS[i]!=null) && (!eumed_fratt_WMS[i].equals("N/A"))) nmax++;
                
                String wmsList[] = new String [nmax];
                for (int i = 0; i < nmax; i++)
                {
                    if (eumed_fratt_WMS[i]!=null) {
                    wmsList[i]=eumed_fratt_WMS[i].trim();
                    log.info ("\n\n[" + nmax
                                      + "] Submitting to EUMED ["
                                      + i
                                      + "] using WMS=["
                                      + wmsList[i]
                                      + "]");
                    }
                }

                infrastructures[2] = new InfrastructureInfo(
                    eumed_fratt_VONAME,
                    eumed_fratt_TOPBDII,
                    wmsList,
                    eumed_fratt_ETOKENSERVER,
                    eumed_fratt_PORT,
                    eumed_fratt_ROBOTID,
                    eumed_fratt_VONAME,
                    eumed_fratt_ROLE,
                    "VO-" + eumed_fratt_VONAME + "-" + fratt_SOFTWARE);
            }

            if (biomed_fratt_ENABLEINFRASTRUCTURE != null &&
                biomed_fratt_ENABLEINFRASTRUCTURE.equals("biomed")) 
            {
                MAX++;
                // Getting the FRATT VONAME from the portlet preferences for the BIOMED VO
                String biomed_fratt_INFRASTRUCTURE = portletPreferences.getValue("biomed_fratt_INFRASTRUCTURE", "N/A");
                // Getting the FRATT VONAME from the portlet preferences for the BIOMED VO
                String biomed_fratt_VONAME = portletPreferences.getValue("biomed_fratt_VONAME", "N/A");
                // Getting the FRATT TOPPBDII from the portlet preferences for the BIOMED VO
                String biomed_fratt_TOPBDII = portletPreferences.getValue("biomed_fratt_TOPBDII", "N/A");
                // Getting the FRATT WMS from the portlet preferences for the BIOMED VO
                String[] biomed_fratt_WMS = portletPreferences.getValues("biomed_fratt_WMS", new String[5]);
                // Getting the FRATT ETOKENSERVER from the portlet preferences for the BIOMED VO
                String biomed_fratt_ETOKENSERVER = portletPreferences.getValue("biomed_fratt_ETOKENSERVER", "N/A");
                // Getting the FRATT MYPROXYSERVER from the portlet preferences for the BIOMED VO
                String biomed_fratt_MYPROXYSERVER = portletPreferences.getValue("biomed_fratt_MYPROXYSERVER", "N/A");
                // Getting the FRATT PORT from the portlet preferences for the BIOMED VO
                String biomed_fratt_PORT = portletPreferences.getValue("biomed_fratt_PORT", "N/A");
                // Getting the FRATT ROBOTID from the portlet preferences for the BIOMED VO
                String biomed_fratt_ROBOTID = portletPreferences.getValue("biomed_fratt_ROBOTID", "N/A");
                // Getting the FRATT ROLE from the portlet preferences for the BIOMED VO
                String biomed_fratt_ROLE = portletPreferences.getValue("biomed_fratt_ROLE", "N/A");
                // Getting the FRATT RENEWAL from the portlet preferences for the BIOMED VO
                String biomed_fratt_RENEWAL = portletPreferences.getValue("biomed_fratt_RENEWAL", "checked");
                // Getting the FRATT DISABLEVOMS from the portlet preferences for the BIOMED VO
                String biomed_fratt_DISABLEVOMS = portletPreferences.getValue("biomed_fratt_DISABLEVOMS", "unchecked");
                
                log.info("\n- Getting the FRATT portlet preferences ..."
                + "\n\nbiomed_fratt_INFRASTRUCTURE: " + biomed_fratt_INFRASTRUCTURE
                + "\nbiomed_fratt_VONAME: " + biomed_fratt_VONAME
                + "\nbiomed_fratt_TOPBDII: " + biomed_fratt_TOPBDII                        
                + "\nbiomed_fratt_ETOKENSERVER: " + biomed_fratt_ETOKENSERVER
                + "\nbiomed_fratt_MYPROXYSERVER: " + biomed_fratt_MYPROXYSERVER
                + "\nbiomed_fratt_PORT: " + biomed_fratt_PORT
                + "\nbiomed_fratt_ROBOTID: " + biomed_fratt_ROBOTID
                + "\nbiomed_fratt_ROLE: " + biomed_fratt_ROLE
                + "\nbiomed_fratt_RENEWAL: " + biomed_fratt_RENEWAL
                + "\nbiomed_fratt_DISABLEVOMS: " + biomed_fratt_DISABLEVOMS

                + "\n\nfratt_ENABLEINFRASTRUCTURE: " + biomed_fratt_ENABLEINFRASTRUCTURE
                + "\nfratt_APPID: " + fratt_APPID
                + "\nfratt_OUTPUT_PATH: " + fratt_OUTPUT_PATH                        
                + "\nfratt_SOFTWARE: " + fratt_SOFTWARE
                + "\nTracking_DB_Hostname: " + TRACKING_DB_HOSTNAME
                + "\nTracking_DB_Username: " + TRACKING_DB_USERNAME
                + "\nTracking_DB_Password: " + TRACKING_DB_PASSWORD
                + "\nSMTP_HOST: " + SMTP_HOST
                + "\nSENDER_MAIL: " + SENDER_MAIL);
                
                // Defining the WMS list for the "BIOMED" Infrastructure
                int nmax=0;
                for (int i = 0; i < biomed_fratt_WMS.length; i++)
                    if ((biomed_fratt_WMS[i]!=null) && (!biomed_fratt_WMS[i].equals("N/A"))) nmax++;
                
                String wmsList[] = new String [biomed_fratt_WMS.length];
                for (int i = 0; i < biomed_fratt_WMS.length; i++)
                {
                    if (biomed_fratt_WMS[i]!=null) {
                    wmsList[i]=biomed_fratt_WMS[i].trim();
                    log.info ("\n\nSubmitting for BIOMED [" + i + "] using WMS=[" + wmsList[i] + "]");
                    }
                }

                infrastructures[3] = new InfrastructureInfo(
                    biomed_fratt_VONAME,
                    biomed_fratt_TOPBDII,
                    wmsList,
                    biomed_fratt_ETOKENSERVER,
                    biomed_fratt_PORT,
                    biomed_fratt_ROBOTID,
                    biomed_fratt_VONAME,
                    biomed_fratt_ROLE,
                    "VO-" + biomed_fratt_VONAME + "-" + fratt_SOFTWARE);
            }

            String[] FRATT_Parameters = new String [6];

            // Upload the input settings for the application
            FRATT_Parameters = uploadFrattSettings( request, response, username );

            log.info ("\n\nPreparing to start a FRATT simulation with these parameters. ");
            log.info("\n- Input Parameters: ");
            log.info("\n- ASCII or Text = " + FRATT_Parameters[0]);
            log.info("\n- Description = " + FRATT_Parameters[1]);
            log.info("\n- FRATT_CE = " + FRATT_Parameters[2]);
            log.info("\n- Jobs = " + FRATT_Parameters[3]);
            log.info("\n- Max CPU Time = " + FRATT_Parameters[5]);
            log.info("\n- Enable Notification = " + FRATT_Parameters[4]);
            
            // Preparing to submit jobs in different grid infrastructure..
            //=============================================================
            // IMPORTANT: INSTANCIATE THE MultiInfrastructureJobSubmission
            //            CLASS USING THE EMPTY CONSTRUCTOR WHEN
            //            WHEN THE PORTLET IS DEPLOYED IN PRODUCTION!!!
            //=============================================================
            MultiInfrastructureJobSubmission FrattMultiJobSubmission =
            new MultiInfrastructureJobSubmission(TRACKING_DB_HOSTNAME,
                                                 TRACKING_DB_USERNAME,
                                                 TRACKING_DB_PASSWORD);
            
            /*MultiInfrastructureJobSubmission FrattMultiJobSubmission =
                new MultiInfrastructureJobSubmission();*/

            // Set the list of infrastructure(s) activated for the portlet           
            if (infrastructures[0]!=null) {
                log.info("\n- Adding the COMETA Infrastructure.");
                 FrattMultiJobSubmission.addInfrastructure(infrastructures[0]); 
            }            
            if (infrastructures[1]!=null) {
                log.info("\n- Adding the GRIDIT Infrastructure.");
                 FrattMultiJobSubmission.addInfrastructure(infrastructures[1]); 
            }
            if (infrastructures[2]!=null) {
                log.info("\n- Adding the EUMED Infrastructure.");
                 FrattMultiJobSubmission.addInfrastructure(infrastructures[2]);
            }
            if (infrastructures[3]!=null) {
                log.info("\n- Adding the BIOMED Infrastructure.");
                 FrattMultiJobSubmission.addInfrastructure(infrastructures[3]);
            }
            
            String FrattFilesPath = getPortletContext().getRealPath("/") +
                                    "WEB-INF/config";                        
            
            // Set the Output path forresults
            FrattMultiJobSubmission.setOutputPath(fratt_OUTPUT_PATH);
                        
            // Set the StandardOutput for FRATT
            FrattMultiJobSubmission.setJobOutput(".std.txt");

            // Set the StandardError for FRATT
            FrattMultiJobSubmission.setJobError(".std.err");
            
            // Set the Executable for FRATT
            FrattMultiJobSubmission.setExecutable("start_geant4-09-05-patch-01.sh");

            // Set the list of Arguments for FRATT
            String Arguments = FRATT_Parameters[0];            
            FrattMultiJobSubmission.setArguments(Arguments);
                
            // Set InputSandbox files
            // (string with comma separated list of file names)            
            String InputSandbox = FrattFilesPath + 
                                  "/start_geant4-09-05-patch-01.sh" +
                                  "," +                                   
                                  FRATT_Parameters[0];

            FrattMultiJobSubmission.setInputFiles(InputSandbox);                 

            // OutputSandbox (string with comma separated list of file names)
            String README = "output.README";
            
            String FrattFiles="results.tar.gz";

            // Set the OutputSandbox files 
            // (string with comma separated list of file names)            
            FrattMultiJobSubmission.setOutputFiles(FrattFiles + "," + README);
            
            int NMAX = Integer.parseInt(FRATT_Parameters[3]);            
            // Adding the proper requirements to run more than 24h
            /*String jdlRequirements[] = new String[1];
            jdlRequirements[0] = "JDLRequirements=(other.GlueCEPolicyMaxCPUTime>1440)";
            FrattMultiJobSubmission.setJDLRequirements(jdlRequirements);*/

            InetAddress addr = InetAddress.getLocalHost();           
            
            Company company;
            try {
                company = PortalUtil.getCompany(request);
                String gateway = company.getName();
                
                // Send a notification email to the user if enabled.
                if (FRATT_Parameters[4]!=null)
                    if ( (SMTP_HOST==null) || 
                         (SMTP_HOST.trim().equals("")) ||
                         (SMTP_HOST.trim().equals("N/A")) ||
                         (SENDER_MAIL==null) || 
                         (SENDER_MAIL.trim().equals("")) ||
                         (SENDER_MAIL.trim().equals("N/A"))
                       )
                    log.info ("\nThe Notification Service is not properly configured!!");
                else {
                            // Enabling Job's notification via email
                            FrattMultiJobSubmission.setUserEmail(emailAddress);
                        
                            sendHTMLEmail(username, 
                                       emailAddress, 
                                       SENDER_MAIL, 
                                       SMTP_HOST, 
                                       "FRATT", 
                                       gateway);
                }                                
                
            } catch (PortalException ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SystemException ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // Submitting...
            for (int i=1; i<=NMAX; i++) 
            { 
                InfrastructureInfo infrastructure = 
                    FrattMultiJobSubmission.getInfrastructure();
                
                String Middleware = null;
                if (infrastructure.getMiddleware().equals("glite"))
                    Middleware = "glite";
                if (infrastructure.getMiddleware().equals("wsgram"))
                    Middleware = "wsgram";
            
                log.info("\n- Selected Infrastructure = " + infrastructure.getName());
                log.info("\n- Enabled Middleware = " + Middleware);
            
                // Check if more than one infrastructure have been enabled                
                if (MAX==1) 
                {
                    String fratt_VONAME = "";
                    String fratt_TOPBDII = "";
                    String RANDOM_CE = "";                    
                    int MAXCpuTime = Integer.parseInt(FRATT_Parameters[5]);
                    
                    if (cometa_fratt_ENABLEINFRASTRUCTURE != null &&
                        cometa_fratt_ENABLEINFRASTRUCTURE.equals("cometa")) 
                    {
                        // Getting the FRATT VONAME from the portlet preferences for the COMETA VO
                        fratt_VONAME = portletPreferences.getValue("cometa_fratt_VONAME", "N/A");
                        // Getting the FRATT TOPPBDII from the portlet preferences for the COMETA VO
                        fratt_TOPBDII = portletPreferences.getValue("cometa_fratt_TOPBDII", "N/A");
                        
                        // Set the queue if it is defined by the user
                        //if (!FRATT_Parameters[2].isEmpty() && NMAX<=16) {
                        //log.info("\n- Submitting of the job [ #" + i + " ] to the selected CE in progress...");
                        //FrattMultiJobSubmission.setJobQueue(FRATT_Parameters[2]);            
                        //} else { 
                        // Get the random CE for the FRATT portlet                
                        //RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE);
                        //log.info("\n- Submitting of the job [ #" + i + " ] to the CE [ " + RANDOM_CE + " ] in progress...");                
                        //FrattMultiJobSubmission.setJobQueue(RANDOM_CE.toString().trim());
                        //}
                    }
                    
                    if (gridit_fratt_ENABLEINFRASTRUCTURE != null &&
                        gridit_fratt_ENABLEINFRASTRUCTURE.equals("gridit")) 
                    {
                        // Getting the FRATT VONAME from the portlet preferences for the GRIDIT VO
                        fratt_VONAME = portletPreferences.getValue("gridit_fratt_VONAME", "N/A");
                        // Getting the FRATT TOPPBDII from the portlet preferences for the GRIDIT VO
                        fratt_TOPBDII = portletPreferences.getValue("gridit_fratt_TOPBDII", "N/A");
                        
                        // Set the queue if it is defined by the user
                        if (!FRATT_Parameters[2].isEmpty()) {
                        log.info("\n- Submitting of the job [ #" + i + " ] to the selected CE in progress...");
                        RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE, MAXCpuTime, FRATT_Parameters[2]);
                        FrattMultiJobSubmission.setJobQueue(RANDOM_CE);
                        } else { 
                        // Get the random CE for the FRATT portlet
                        RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE, MAXCpuTime, "");
                        log.info("\n- Submitting of the job [ #" + i + " ] to the CE [ " + RANDOM_CE + " ] in progress...");                
                        FrattMultiJobSubmission.setJobQueue(RANDOM_CE.toString().trim());
                        }
                    }
                    
                    if (eumed_fratt_ENABLEINFRASTRUCTURE != null &&
                        eumed_fratt_ENABLEINFRASTRUCTURE.equals("eumed")) 
                    {
                        // Getting the FRATT VONAME from the portlet preferences for the EUMED VO
                        fratt_VONAME = portletPreferences.getValue("eumed_fratt_VONAME", "N/A");
                        // Getting the FRATT TOPPBDII from the portlet preferences for the EUMED VO
                        fratt_TOPBDII = portletPreferences.getValue("eumed_fratt_TOPBDII", "N/A");
                        
                        // Set the queue if it is defined by the user
                        if (!FRATT_Parameters[2].isEmpty()) {
                        log.info("\n- Submitting of the job [ #" + i + " ] to the selected CE in progress...");
                        RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE, MAXCpuTime, FRATT_Parameters[2]);
                        FrattMultiJobSubmission.setJobQueue(RANDOM_CE);
                        } else { 
                        // Get the random CE for the FRATT portlet
                        RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE, MAXCpuTime, "");
                        log.info("\n- Submitting of the job [ #" + i + " ] to the CE [ " + RANDOM_CE + " ] in progress...");                
                        FrattMultiJobSubmission.setJobQueue(RANDOM_CE.toString().trim());
                        }
                    }
                    
                    if (biomed_fratt_ENABLEINFRASTRUCTURE != null &&
                        biomed_fratt_ENABLEINFRASTRUCTURE.equals("biomed")) 
                    {
                        // Getting the FRATT VONAME from the portlet preferences for the BIOMED VO
                        fratt_VONAME = portletPreferences.getValue("biomed_fratt_VONAME", "N/A");
                        // Getting the FRATT TOPPBDII from the portlet preferences for the BIOMED VO
                        fratt_TOPBDII = portletPreferences.getValue("biomed_fratt_TOPBDII", "N/A");
                        
                        // Set the queue if it is defined by the user
                        if (!FRATT_Parameters[2].isEmpty()) {
                        log.info("\n- Submitting of the job [ #" + i + " ] to the selected CE in progress...");
                        RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE, MAXCpuTime, FRATT_Parameters[2]);
                        FrattMultiJobSubmission.setJobQueue(RANDOM_CE);
                        } else { 
                        // Get the random CE for the FRATT portlet
                        RANDOM_CE = getRandomCE(fratt_VONAME, fratt_TOPBDII, fratt_SOFTWARE, MAXCpuTime, "");
                        log.info("\n- Submitting of the job [ #" + i + " ] to the CE [ " + RANDOM_CE + " ] in progress...");                
                        FrattMultiJobSubmission.setJobQueue(RANDOM_CE.toString().trim());
                        }
                    }                    
                }
                
                FrattMultiJobSubmission.submitJobAsync(
                        infrastructure,
                        username,
                        addr.getHostAddress()+":8162",
                        Integer.valueOf(fratt_APPID),
                        FRATT_Parameters[1]);
            }            
        } // end PROCESS ACTION [ SUBMIT_FRATT_PORTLET ]
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
                throws PortletException, IOException
    {
        //super.serveResource(request, response);

        PortletPreferences portletPreferences = (PortletPreferences) request.getPreferences();

        final String action = (String) request.getParameter("action");

        if (action.equals("get-ratings")) {
            //Get CE Ratings from the portlet preferences
            String fratt_CE = (String) request.getParameter("fratt_CE");

            String json = "{ \"avg\":\"" + 
                    portletPreferences.getValue(fratt_CE+"_avg", "0.0") +
                    "\", \"cnt\":\"" + portletPreferences.getValue(fratt_CE+"_cnt", "0") + "\"}";

            response.setContentType("application/json");
            response.getPortletOutputStream().write( json.getBytes() );

        } else if (action.equals("set-ratings")) {

            String fratt_CE = (String) request.getParameter("fratt_CE");
            int vote = Integer.parseInt(request.getParameter("vote"));

             double avg = Double.parseDouble(portletPreferences.getValue(fratt_CE+"_avg", "0.0"));
             long cnt = Long.parseLong(portletPreferences.getValue(fratt_CE+"_cnt", "0"));

             portletPreferences.setValue(fratt_CE+"_avg", Double.toString(((avg*cnt)+vote) / (cnt +1)));
             portletPreferences.setValue(fratt_CE+"_cnt", Long.toString(cnt+1));

             portletPreferences.store();
        }
    }


    // Upload FRATT input files
    public String[] uploadFrattSettings(ActionRequest actionRequest,
                                        ActionResponse actionResponse, String username)
    {
        String[] FRATT_Parameters = new String [6];
        boolean status;

        // Check that we have a file upload request
        boolean isMultipart = PortletFileUpload.isMultipartContent(actionRequest);

        if (isMultipart) {
            // Create a factory for disk-based file items.
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // Set factory constrains
            File FRATT_Repository = new File ("/tmp");
            if (!FRATT_Repository.exists()) status = FRATT_Repository.mkdirs();
            factory.setRepository(FRATT_Repository);

            // Create a new file upload handler.
            PortletFileUpload upload = new PortletFileUpload(factory);

            try {
                    // Parse the request
                    List items = upload.parseRequest(actionRequest);

                    // Processing items
                    Iterator iter = items.iterator();

                    while (iter.hasNext())
                    {
                        FileItem item = (FileItem) iter.next();

                        String fieldName = item.getFieldName();
                        
                        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        String timeStamp = dateFormat.format(Calendar.getInstance().getTime());

                        // Processing a regular form field
                        if ( item.isFormField() )
                        {                                                        
                            if (fieldName.equals("fratt_textarea")) 
                            {
                                FRATT_Parameters[0]=
                                        FRATT_Repository +
                                        "/" + timeStamp +
                                        "_" + username +
                                        ".txt";
                            
                                // Store the textarea in a ASCII file
                                storeString(FRATT_Parameters[0], 
                                            item.getString());                               
                            }
                            
                            if (fieldName.equals("fratt_desc"))
                                if (item.getString().equals("Please, insert here a description for your job"))
                                    FRATT_Parameters[1]="Fratt Therapy Simulation Started";
                                else
                                    FRATT_Parameters[1]=item.getString();                            
                                                        
                            if (fieldName.equals("fratt_CE"))
                                FRATT_Parameters[2]=item.getString();
                            
                            if (fieldName.equals("fratt_nmax"))
                                FRATT_Parameters[3]=item.getString();
                            
                        } else {
                            // Processing a file upload
                            if (fieldName.equals("fratt_file"))
                            {                                                               
                                log.info("\n- Uploading the following user's file: "
                                       + "\n[ " + item.getName() + " ]"
                                       + "\n[ " + item.getContentType() + " ]"
                                       + "\n[ " + item.getSize() + "KBytes ]"
                                       );                                                                

                                // Writing the file to disk
                                String uploadFrattFile = 
                                        FRATT_Repository +
                                        "/" + timeStamp +
                                        "_" + username +
                                        "_" + item.getName();

                                log.info("\n- Writing the user's file: [ "
                                        + uploadFrattFile.toString()
                                        + " ] to disk");

                                item.write(new File(uploadFrattFile));
                                
                                // Writing the file to disk
                                String uploadFrattFile_stripped = 
                                        FRATT_Repository +
                                        "/" + timeStamp +
                                        "_" + username +
                                        "_macro.mac";                                                                
                                
                                FRATT_Parameters[0]=
                                        RemoveCarriageReturn(
                                        uploadFrattFile, 
                                        uploadFrattFile_stripped
                                        );                                                                
                            }
                        }
                        
                        if (fieldName.equals("EnableNotification"))
                                FRATT_Parameters[4]=item.getString(); 
                        
                        if (fieldName.equals("fratt_maxcputime"))
                                FRATT_Parameters[5]=item.getString(); 
                        
                    } // end while
            } catch (FileUploadException ex) {
              Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
              Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return FRATT_Parameters;
    }
    
    // Retrieve a random Computing Element
    // matching the Software Tag for the application    
    public String getRandomCE(String fratt_VONAME,
                              String fratt_TOPBDII,
                              String fratt_SOFTWARE,
                              Integer fratt_MaxCPUTime,
                              String selected)
                              throws PortletException, IOException
    {
        String randomCE = null;
        BDII bdii = null;    
        List<String> CEqueues = null;
        boolean flag = true;        
                        
        log.info("\n- Querying the Information System [ " + 
                  fratt_TOPBDII + 
                  " ] and retrieving a random CE matching the SW tag [ VO-" + 
                  fratt_VONAME +
                  "-" +
                  fratt_SOFTWARE + " ]");  

       try {               

                bdii = new BDII( new URI(fratt_TOPBDII) );               
                
                // Get the list of the available queues
                CEqueues = bdii.queryCEQueues(fratt_VONAME);
                                
                // Get the list of the Computing Elements for the given SW tag
                randomCE = bdii.getRandomCEFromSWTag_MaxCPUTime(
                        "VO-" + fratt_VONAME + "-" + fratt_SOFTWARE, 
                        fratt_VONAME, 
                        fratt_MaxCPUTime);
                                    
                if (!selected.isEmpty())
                while (flag) {
                    // Fetching the Queues
                    for (String CEqueue:CEqueues) {
                        if (CEqueue.contains(selected)) {
                            randomCE=CEqueue;                    
                            flag=false;
                        }                        
                    }
                }

        } catch (URISyntaxException ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
        }                   
        
        if (!selected.isEmpty()) log.info("\n- Selected the following computing farm =  " + randomCE);
        else log.info("\n- Selected *randomly* the following computing farm =  " + randomCE);
        return randomCE;
    }        
    
    public String RemoveCarriageReturn (String InputFileName, String OutputFileName)             
    {
        // Remove the carriage return char from a named file.                                
        FileInputStream fis;
        try {
            
            fis = new FileInputStream(InputFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            
            File fout = new File(OutputFileName);
            FileOutputStream fos = new FileOutputStream(fout);                                
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            
            // The pattern matches control characters
            Pattern p = Pattern.compile("\r");
            Matcher m = p.matcher("");
            String aLine = null;

            try {
                while((aLine = in.readLine()) != null) {
                    m.reset(aLine);
                    //Replaces control characters with an empty string.
                    String result = m.replaceAll("");                    
                    out.write(result);
                    out.newLine();
                }
                out.close();                
            } catch (IOException ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
        }                                                                                
        
        log.info("\n- Writing the user's stripped file: [ " + 
                  OutputFileName.toString() + " ] to disk");
        
        return OutputFileName;
    }


    // Retrieve the list of Computing Elements
    // matching the Software Tag for the FRATT application    
    public List<String> getListofCEForSoftwareTag(String fratt_VONAME,
                                                  String fratt_TOPBDII,
                                                  String fratt_SOFTWARE)
                                throws PortletException, IOException
    {
        List<String> CEs_list = null;
        BDII bdii = null;        
        
        log.info("\n- Querying the Information System [ " + 
                  fratt_TOPBDII + 
                  " ] and looking for CEs matching SW tag [ VO-" + 
                  fratt_VONAME +
                  "-" +
                  fratt_SOFTWARE + " ]");  

       try {               

                bdii = new BDII( new URI(fratt_TOPBDII) );                
                CEs_list = bdii.queryCEForSWTag(
                           "VO-" +
                           fratt_VONAME +
                           "-" +
                           fratt_SOFTWARE);

        } catch (URISyntaxException ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
        }

        return CEs_list;
    }

    // Get the GPS location of the given grid resource
    public String[] getCECoordinate(RenderRequest request,
                                    String CE)
                                    throws PortletException, IOException
    {
        String[] GPS_locations = null;
        BDII bdii = null;

        PortletPreferences portletPreferences =
                (PortletPreferences) request.getPreferences();

        // Getting the FRATT TOPPBDII from the portlet preferences
        String cometa_fratt_TOPBDII = 
                portletPreferences.getValue("cometa_fratt_TOPBDII", "N/A");
        String gridit_fratt_TOPBDII = 
                portletPreferences.getValue("gridit_fratt_TOPBDII", "N/A");
        String eumed_fratt_TOPBDII = 
                portletPreferences.getValue("eumed_fratt_TOPBDII", "N/A");
        String biomed_fratt_TOPBDII = 
                portletPreferences.getValue("biomed_fratt_TOPBDII", "N/A");
        
        // Getting the FRATT ENABLEINFRASTRUCTURE from the portlet preferences
        String fratt_ENABLEINFRASTRUCTURE = 
                portletPreferences.getValue("fratt_ENABLEINFRASTRUCTURE", "N/A");

            try {
                if ( fratt_ENABLEINFRASTRUCTURE.equals("cometa") )
                     bdii = new BDII( new URI(cometa_fratt_TOPBDII) );
                
                if ( fratt_ENABLEINFRASTRUCTURE.equals("gridit") )
                     bdii = new BDII( new URI(gridit_fratt_TOPBDII) );

                if ( fratt_ENABLEINFRASTRUCTURE.equals("eumed") )
                     bdii = new BDII( new URI(eumed_fratt_TOPBDII) );

                if ( fratt_ENABLEINFRASTRUCTURE.equals("biomed") )
                    bdii = new BDII( new URI(biomed_fratt_TOPBDII) );

                GPS_locations = bdii.queryCECoordinate("ldap://" + CE + ":2170");

            } catch (URISyntaxException ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Fratt.class.getName()).log(Level.SEVERE, null, ex);
            }

            return GPS_locations;
    }
    
    private void storeString (String fileName, String fileContent) 
                              throws IOException 
    { 
        log.info("\n- Writing textarea in a ASCII file [ " + fileName + " ]");        
        // Removing the Carriage Return (^M) from text
        String pattern = "[\r]";
        String stripped = fileContent.replaceAll(pattern, "");        
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));        
        writer.write(stripped);
        writer.write("\n");
        writer.close();
    }
    
    private void sendHTMLEmail (String USERNAME,
                                String TO, 
                                String FROM, 
                                String SMTP_HOST, 
                                String ApplicationAcronym,
                                String GATEWAY)
    {
                
        log.info("\n- Sending email notification to the user " + USERNAME + " [ " + TO + " ]");
        
        log.info("\n- SMTP Server = " + SMTP_HOST);
        log.info("\n- Sender = " + FROM);
        log.info("\n- Receiver = " + TO);
        log.info("\n- Application = " + ApplicationAcronym);
        log.info("\n- Gateway = " + GATEWAY);        
        
        // Assuming you are sending email from localhost
        String HOST = "localhost";
        
        // Get system properties
        Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST, HOST);
        
        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);
        
        try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(FROM));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
         //message.addRecipient(Message.RecipientType.CC, new InternetAddress(FROM));

         // Set Subject: header field
         message.setSubject(" [liferay-sg-gateway] - [ " + GATEWAY + " ] ");

	 Date currentDate = new Date();
	 currentDate.setTime (currentDate.getTime());

         // Send the actual HTML message, as big as you like
         message.setContent(
	 "<br/><H4>" +         
         "<img src=\"http://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc6/195775_220075701389624_155250493_n.jpg\" width=\"100\">Science Gateway Notification" +
	 "</H4><hr><br/>" +
         "<b>Description:</b> Notification for the application <b>[ " + ApplicationAcronym + " ]</b><br/><br/>" +         
         "<i>The application has been successfully submitted from the [ " + GATEWAY + " ]</i><br/><br/>" +
         "<b>TimeStamp:</b> " + currentDate + "<br/><br/>" +
	 "<b>Disclaimer:</b><br/>" +
	 "<i>This is an automatic message sent by the Science Gateway based on Liferay technology.<br/>" + 
	 "If you did not submit any jobs through the Science Gateway, please " +
         "<a href=\"mailto:" + FROM + "\">contact us</a></i>",
	 "text/html");

         // Send message
         Transport.send(message);         
      } catch (MessagingException mex) { mex.printStackTrace(); }        
    }
}
