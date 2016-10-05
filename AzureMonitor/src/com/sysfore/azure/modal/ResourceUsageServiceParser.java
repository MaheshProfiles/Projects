package com.sysfore.azure.modal;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.sysfore.azure.model.ResourceUsageService;

import android.util.Xml;

public class ResourceUsageServiceParser {
	
	 private static final String ns = null;
	   
	    public ResourceUsageService parse(InputStream in) throws XmlPullParserException, IOException {
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
	    
	    private ResourceUsageService readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
	        
	        ResourceUsageService usageService = new ResourceUsageService();
	        parser.require(XmlPullParser.START_TAG, ns, "Subscription");
	        while (parser.next() != XmlPullParser.END_TAG) {
	        	
	            if (parser.getEventType() != XmlPullParser.START_TAG) {
	                continue;
	            }
	            String name = parser.getName();
	            // Starts by looking for the entry tag
	            if (name.equals("MaxStorageAccounts")) {
	            	parser.require(XmlPullParser.START_TAG, ns, "MaxStorageAccounts");
	            	usageService.setMaxStorageAccounts(readText(parser));
	                parser.require(XmlPullParser.END_TAG, ns, "MaxStorageAccounts");
	                

	            }else if(name.equals("MaxHostedServices")) {
	            	parser.require(XmlPullParser.START_TAG, ns, "MaxHostedServices");
	            	usageService.setMaxHostedServices(readText(parser));
	                parser.require(XmlPullParser.END_TAG, ns, "MaxHostedServices");
	            	
	            }
	            else if(name.equals("CurrentCoreCount")) {
	            	parser.require(XmlPullParser.START_TAG, ns, "CurrentCoreCount");
	            	usageService.setCurrentCoreCount(readText(parser));
	                parser.require(XmlPullParser.END_TAG, ns, "CurrentCoreCount");
	            }
	            else if(name.equals("CurrentHostedServices")) {
	            	parser.require(XmlPullParser.START_TAG, ns, "CurrentHostedServices");
	            	usageService.setCurrentHostedServices(readText(parser));
	                parser.require(XmlPullParser.END_TAG, ns, "CurrentHostedServices");
	            	
	            }
	            else if(name.equals("CurrentStorageAccounts")) {
	            	parser.require(XmlPullParser.START_TAG, ns, "CurrentStorageAccounts");
	            	usageService.setCurrentStorageAccounts(readText(parser));
	                parser.require(XmlPullParser.END_TAG, ns, "CurrentStorageAccounts");
	            }
	            
	            else if(name.equals("MaxCoreCount")) {
	            	parser.require(XmlPullParser.START_TAG, ns, "MaxCoreCount");
	            	usageService.setMaxCoreCount(readText(parser));
	                parser.require(XmlPullParser.END_TAG, ns, "MaxCoreCount");
	            }
	            
	            else {
	                skip(parser);
	            }
	        } 
	        
	        return usageService;
	        
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
	    
	    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	        String result = "";
	        if (parser.next() == XmlPullParser.TEXT) {
	            result = parser.getText();
	            parser.nextTag();
	        }
	        return result;
	    }



}
