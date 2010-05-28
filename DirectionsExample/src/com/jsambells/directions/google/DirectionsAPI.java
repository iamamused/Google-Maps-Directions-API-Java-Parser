/**
 * Google Directions API parser
 * 
 * The MIT License
 * 
 * Copyright (c) 2010 TropicalPixels, Jeffrey Sambells
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.jsambells.directions.google;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.jsambells.directions.ParserAbstract;
import com.jsambells.directions.RouteAbstract;

/**
 * Implementation of ParserAbstract that connects to the Google Maps Direction API
 */
public class DirectionsAPI extends ParserAbstract {
	
	IDirectionsListener listener;
	
	@Override
	protected AsyncTask getThruWaypoints(List<GeoPoint>waypoints, Mode mode, IDirectionsListener listener) {
		return new LoadDirectionsTask(waypoints).execute(mode);
	}

	private class LoadDirectionsTask extends AsyncTask<Mode, Void, DirectionsAPIRoute> {
		
		static final String TAG = "LoadDirectionsTask";

		private static final String BASE_URL = "http://maps.google.com/maps/api/directions/xml?";

		private static final String ELEMENT_ROUTE = "route";
		
		// summary contains a short textual description for the route, suitable for naming and disambiguating the route from alternatives.
		private static final String ELEMENT_ROUTE_SUMMARY = "summary";
		
		//waypoint_order contains an array indicating the order of any waypoints in the calculated route. This waypoints may be reordered if the request was passed optimize:true within its waypoints parameter.
		private static final String ELEMENT_ROUTE_WAYPOINT_ORDER = "waypoint_order";
		
		//overview_path contains an object holding an array of encoded points and levels that represent an approximate (smoothed) path of the resulting directions.
		private static final String ELEMENT_ROUTE_OVERVIEW_PATH = "overview_path";
		
		//copyrights contains the copyrights text to be displayed for this route. You must handle and display this information yourself.
		private static final String ELEMENT_ROUTE_COPYRIGHTS = "copyrights";
		
		//warnings[] contains an array of warnings to be displayed when showing these directions. You must handle and display these warnings yourself.
		private static final String ELEMENT_ROUTE_WARNINGS = "warnings";
		
		//legs[] contains an array which contains information about a leg of the route, between two locations within the given route. A separate leg will be present for each waypoint or destination specified. (A route with no waypoints will contain exactly one leg within the legs array.) Each leg consists of a series of steps. (See Directions Legs below.)
		private static final String ELEMENT_LEG = "leg";
		private static final String ELEMENT_STEP = "step";
		private static final String ELEMENT_START_LOCATION = "start_location";
		private static final String ELEMENT_END_LOCATION = "end_location";

		private static final String ELEMENT_DISTANCE = "distance";
		private static final String ELEMENT_DURATION = "duration";
		
		private List<GeoPoint> waypoints;
		private GeoPoint endPoint;

		public LoadDirectionsTask(List<GeoPoint>waypoints) {
			this.waypoints = waypoints;
		}

		@Override
		protected DirectionsAPIRoute doInBackground(Mode... params) {

			StringBuilder urlString = new StringBuilder();
			urlString.append(BASE_URL);
			urlString.append("&origin=" + (waypoints.get(0).getLatitudeE6() / 1E6) + "," + (waypoints.get(0).getLongitudeE6() / 1E6));
			urlString.append("&waypoints=");
			
			Iterator<GeoPoint> itr = waypoints.listIterator();
			itr.next(); // Skip the first point since it was the origin.
			while( itr.hasNext() ) {
				GeoPoint p = (GeoPoint)itr.next();
				urlString.append((p.getLatitudeE6() / 1E6) + "," + (p.getLongitudeE6() / 1E6));
				if (itr.hasNext()) {
					urlString.append("|");
				}
			}
			
			if (params[0] == Mode.WALKING) {
				urlString.append("&mode=walking");
			} else if (params[0] == Mode.BICYCLING) {
				urlString.append("&mode=bicycling");
			}

			urlString.append("&sensor=false");
			
			DirectionsAPIRoute route = null;
			try {
				Log.i(TAG, "Open URL:" + urlString.toString());
				URL url = new URL(urlString.toString());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.connect();
				route = parseResponse(connection.getInputStream());
			} catch (Exception e) {
				// Don't handle the exception but set the RouteAbstract to null.
				Log.e(TAG, "Error Parsing result", e);
				route = null;
			}

			return route;
		}

		private DirectionsAPIRoute parseResponse(InputStream inputStream) throws Exception
		{
			// Parse the result returned by the Google Directions web service
			// http://code.google.com/apis/maps/documentation/directions/#XML
				
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			// TODO: Deal with status codes
			
			NodeList routeList = document.getElementsByTagName(ELEMENT_ROUTE);
			
			// Process only first route for now.
			Node routeNode = routeList.item(0);
			DirectionsAPIRoute route = parseRoute(routeNode);
			
			return route;
		}
		
		private DirectionsAPIRoute parseRoute(Node item) {
			DirectionsAPIRoute route = new DirectionsAPIRoute();

			List<DirectionsAPILeg> directionsAPILegs = new ArrayList<DirectionsAPILeg>();

			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);

				if (node.getNodeName().equals(ELEMENT_ROUTE_SUMMARY)) {
					route.setSummary(node.getFirstChild().getNodeValue());
				} else if (node.getNodeName().equals(ELEMENT_LEG)) {
					DirectionsAPILeg directionsAPILeg = this.parseLeg(node);
					if ( directionsAPILeg != null ) {
						directionsAPILegs.add(directionsAPILeg);
					}
				} else if (node.getNodeName().equals("overview_polyline")) {
					route.setRoughGeoPoints(this.parsePoly(node));
				} else if (node.getNodeName().equals("copyrights")) {
					route.setCopyrights(node.getFirstChild().getNodeValue());
				}
			}
			
			route.setLegs(directionsAPILegs);

			Log.d(TAG, "Parsed Route:" + route);

			return route;
		}

		private DirectionsAPILeg parseLeg(Node item) {
		
			DirectionsAPILeg leg = new DirectionsAPILeg();
			List<DirectionsAPIStep> stepList = new ArrayList<DirectionsAPIStep>();
			
			
			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);

				if (node.getNodeName().equals(ELEMENT_STEP)) {
					DirectionsAPIStep directionsAPIStep = parseStep(node);
					if (directionsAPIStep != null) {
						stepList.add(directionsAPIStep);
					}
				} else if (node.getNodeName().equals(ELEMENT_DURATION)) {
					// TODO parse value/text pairs.
				} else if (node.getNodeName().equals(ELEMENT_DISTANCE)) {
					// TODO parse value/text pairs.
				} else if (node.getNodeName().equals(ELEMENT_START_LOCATION)) {
					leg.setStartLocation(this.parseWaypoint(node));
				} else if (node.getNodeName().equals(ELEMENT_END_LOCATION)) {
					leg.setEndLocation(this.parseWaypoint(node));
				} else if (node.getNodeName().equals("start_address")) {
					leg.setStartAddress(node.getFirstChild().getNodeValue());
				} else if (node.getNodeName().equals("end_address")) {
					leg.setEndAddress(node.getFirstChild().getNodeValue());
				}
			}
			
			leg.setSteps(stepList);
			
			Log.d(TAG, "Parsed Leg:" + leg);

			return leg;
		}
		
		
		private DirectionsAPIStep parseStep(Node item) {
			
			DirectionsAPIStep step = new DirectionsAPIStep();
			
			NodeList children = item.getChildNodes();
			int childCount = children.getLength();
			
			for (int i = 0; i < childCount; i++) {
				Node node = children.item(i);
				
				if (node.getNodeName().equals(ELEMENT_START_LOCATION)) {
					step.setStartLocation(parseWaypoint(node));
				} else if (node.getNodeName().equals(ELEMENT_END_LOCATION)) {
					step.setEndLocation(parseWaypoint(node));					
				} else if (node.getNodeName().equals(ELEMENT_DISTANCE)) {
					// TODO parse value/text pairs.
				} else if (node.getNodeName().equals(ELEMENT_DURATION)) {
					// TODO parse value/text pairs.
				} else if (node.getNodeName().equals("polyline")) {
					step.setGeoPoints(parsePoly(node));
				} else if (node.getNodeName().equals("travel_mode")) {
					// TODO parse travel mode.
				} else if (node.getNodeName().equals("html_instructions")) {
					// TODO parse instructions.
				}
			}
			
			Log.d(TAG, "Parsed Step:" + step);

			return step;
		}

		private DirectionsAPIWaypoint parseWaypoint(Node item) {
			Double lat = 0.0, lng = 0.0;
			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeName().equals("lat")) {
					lat = Double.parseDouble(node.getFirstChild().getNodeValue());
				} else if (node.getNodeName().equals("lng")) {
					lng = Double.parseDouble(node.getFirstChild().getNodeValue());
				}
			}
			DirectionsAPIWaypoint wp = new DirectionsAPIWaypoint();
			wp.setLocation(new GeoPoint((int)(lat * 1E6),(int)(lng * 1E6)));
			
			Log.d(TAG, "Parsed Waypoint:" + wp);

			return wp;
		}
		
		private List<GeoPoint> parsePoly(Node item) {
			NodeList children = item.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeName().equals("points")) {
					List<GeoPoint> poly = decodePoly(node.getFirstChild().getNodeValue());
					return poly;
				} 
			}
			return null;
		}
		
		private List<GeoPoint> decodePoly(String encoded) {
	
			List<GeoPoint> poly = new ArrayList<GeoPoint>();
			int index = 0, len = encoded.length();
			int lat = 0, lng = 0;
	
			while (index < len) {
				int b, shift = 0, result = 0;
				// Decode latitude
				do {
					b = encoded.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int r = (result & 1);
				int dlat = (r != 0 ? ~(result >> 1) : (result >> 1));
				lat += dlat;
	
				// Decode longitude
				shift = 0;
				result = 0;
				do {
					b = encoded.charAt(index++) - 63;
					result |= (b & 0x1f) << shift;
					shift += 5;
				} while (b >= 0x20);
				int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
				lng += dlng;
	
				GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6));
				poly.add(p);
			}
	
			return poly;
		}

		protected void onPostExecute(DirectionsAPIRoute directionsAPIRoute) {
			if (directionsAPIRoute == null) {
				DirectionsAPI.this.onDirectionsNotAvailable();
			} else {
				DirectionsAPI.this.onDirectionsAvailable((RouteAbstract)directionsAPIRoute);
			}
		}
	}
}
