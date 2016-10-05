package com.sysfore.azure.modal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.sysfore.azure.model.HostedService;
import com.sysfore.azure.model.VMService;

import android.util.Log;
import android.util.Xml;

public class VMServiceParser {
private static final String ns = null;
	
	public VMService parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
        	if(in != null) {
        		in.close();
        	}
        }
    }
	
	private VMService readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		
	    parser.require(XmlPullParser.START_TAG, ns, "Deployment");
	    VMService vmService = new VMService();
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("RoleInstanceList")) {
	        	vmService.setPowerState(readRoleInstantService(parser));
	        }else if (name.equals("RoleList")) {
	         	vmService.setRoleSize(readRoleListService(parser));
	        }
	        else {
	            skip(parser);
	        }
	    }  
	    return vmService;
	}
	
	private String readRoleInstantService(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "RoleInstanceList");
	    String powerState = null;
	    
	    HostedService hostedService = new HostedService();;
	    //hostedService = 
	    while (parser.next() != XmlPullParser.END_TAG) {
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("RoleInstance")) {
	        	powerState =readPowerState(parser);
	        	
	        } else {
	            skip(parser);
	        }
	    }
	    return powerState;
	}
	    
	public String readPowerState(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "RoleInstance");
		String powerState = "";
		while (parser.next() != XmlPullParser.END_TAG) {
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	       
	        if (name.equals("PowerState")) {
	        	parser.require(XmlPullParser.START_TAG, ns, "PowerState");
	        	powerState = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "PowerState");
	        } else {
	            skip(parser);
	        }
		}
		return powerState;
	}
    
	private String readRoleListService(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "RoleList");
	    String roleSize = null;
	    
	   
	    //hostedService = 
	    while (parser.next() != XmlPullParser.END_TAG) {
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("Role")) {
	        	roleSize =readRoleSize(parser);
	        	
	        } else {
	            skip(parser);
	        }
	    }
	    return roleSize;
	} 
	
	public String readRoleSize(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "Role");
		String roleSize = "";
		while (parser.next() != XmlPullParser.END_TAG) {
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	       
	        if (name.equals("RoleSize")) {
	        	parser.require(XmlPullParser.START_TAG, ns, "RoleSize");
	        	roleSize = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "RoleSize");
	        } else {
	            skip(parser);
	        }
		}
		return roleSize;
	}
	

	// For the tags title and summary, extracts their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}

	
	 private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            throw new IllegalStateException();
	        }
	        int depth = 1;
	        while (depth != 0) {
	            switch (parser.next()) {
	            case XmlPullParser.END_TAG:
	                depth--;
	                break;
	            case XmlPullParser.START_TAG:
	                depth++;
	                break;
	            }
	        }
	     }
	    
	

}
