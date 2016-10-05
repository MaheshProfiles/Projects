package com.sysfore.azure.modal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.sysfore.azure.model.HostedService;

import android.util.Xml;

public class HostedServiceParser {
	private static final String ns = null;
	
	public List<HostedService> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
	
	private List<HostedService> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
	    List<HostedService> entries = new ArrayList<HostedService>();

	    parser.require(XmlPullParser.START_TAG, ns, "HostedServices");
	    while (parser.next() != XmlPullParser.END_TAG) {
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        // Starts by looking for the entry tag
	        if (name.equals("HostedService")) {
	            entries.add(readHostedService(parser));
	        } else {
	            skip(parser);
	        }
	    }  
	    return entries;
	}
	
	private HostedService readHostedService(XmlPullParser parser) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, "HostedService");
	    String location = null;
	    
	    HostedService hostedService = new HostedService();;
	    //hostedService = 
	    while (parser.next() != XmlPullParser.END_TAG) {
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("ServiceName")) {
	        	parser.require(XmlPullParser.START_TAG, ns, "ServiceName");
	        	hostedService.setServiceName(readText(parser));
                parser.require(XmlPullParser.END_TAG, ns, "ServiceName");
	        } else if (name.equals("HostedServiceProperties")) {
	        	location = readHostedServiceProperties(parser);
	        	hostedService.setLocation(location);
	        } else {
	            skip(parser);
	        }
	    }
	    return hostedService;
	}
	    
	public String readHostedServiceProperties(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "HostedServiceProperties");
		String location = "";
		while (parser.next() != XmlPullParser.END_TAG) {
	    	
	        if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	       
	        if (name.equals("Location")) {
	        	parser.require(XmlPullParser.START_TAG, ns, "Location");
	        	location = readText(parser);
                parser.require(XmlPullParser.END_TAG, ns, "Location");
	        } else {
	            skip(parser);
	        }
		}
		return location;
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
