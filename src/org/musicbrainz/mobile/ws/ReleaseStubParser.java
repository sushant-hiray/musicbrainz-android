/*
 * Copyright (C) 2010 Jamie McDonald
 * 
 * This file is part of MusicBrainz Mobile (Android).
 * 
 * MusicBrainz Mobile (Android) is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public 
 * License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * MusicBrainz Mobile (Android) is distributed in the hope that it 
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MusicBrainz Mobile (Android). If not, see 
 * <http://www.gnu.org/licenses/>.
 */

package org.musicbrainz.mobile.ws;

import java.util.LinkedList;

import org.musicbrainz.mobile.data.ReleaseStub;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX parser handler for release search.
 * 
 * @author Jamie McDonald - jdamcd@gmail.com
 */
public class ReleaseStubParser extends DefaultHandler {
	
	private LinkedList<ReleaseStub> results;
	
	private ReleaseStub stub;
	
	private StringBuilder sb;
	
	public ReleaseStubParser(LinkedList<ReleaseStub> results) {
		this.results = results;
	}

	// tags status
	private boolean artist = false;
	private boolean label = false;
	
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		
		if (localName.equals("release")) {
			stub = new ReleaseStub();
			stub.setReleaseMbid(atts.getValue("id"));
		} else if (localName.equals("title")) {
			sb = new StringBuilder();
		} else if (localName.equals("artist")) {
			artist = true;
		} else if (localName.equals("name")) {
			sb = new StringBuilder();
		} else if (localName.equals("date")) {
			sb = new StringBuilder();
		} else if (localName.equals("country")) {
			sb = new StringBuilder();
		} else if (localName.equals("track-list")) {
			String num = atts.getValue("count");
			int tracks = Integer.parseInt(num);
			stub.setTracksNum(stub.getTracksNum() + tracks);
		} else if (localName.equals("label")) {
			label = true;
		} else if (localName.equals("format")) {
			sb = new StringBuilder();
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		
		if (localName.equals("release")) {
			results.add(stub);
		} else if (localName.equals("title")) {
			stub.setTitle(sb.toString());
		} else if (localName.equals("artist")) {
			artist = false;
		} else if (localName.equals("name")) {
			if (artist)
				stub.setArtistName(sb.toString());
			else if (label) 
				stub.addLabel(sb.toString());
		} else if (localName.equals("date")) {
			stub.setDate(sb.toString());
		} else if (localName.equals("country")) {
			stub.setCountryCode(sb.toString().toUpperCase());
		} else if (localName.equals("label")) {
			label = false;
		} else if (localName.equals("format")) {
			stub.addFormat(sb.toString());
		}
	}
	
	public void characters(char ch[], int start, int length) {
		
		if (sb != null) {
			for (int i=start; i<start+length; i++) {
	            sb.append(ch[i]);
	        }
		}
	}
	
}
