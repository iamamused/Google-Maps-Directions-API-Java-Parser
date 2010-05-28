/**
 * Directions API parser
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
package com.jsambells.directions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.maps.GeoPoint;

/**
 * Represents the directions route including all legs, steps and waypoints..
 */
public abstract class RouteAbstract
{
	
	public enum RoutePathSmoothness {
		ROUGH,
		FINE
	}
	
	private String totalDistance, summary, copyrights;
	private List<GeoPoint> geoPoints;
	private List<LegAbstract> legs;
	private List<WaypointAbstract> waypointAbstracts;

	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}
	public String getTotalDistance() {
		return totalDistance;
	}
	
	public void setRoughGeoPoints(List<GeoPoint> geoPoints) {
		this.geoPoints = geoPoints;
	}
	public List<GeoPoint> getGeoPointPath( RoutePathSmoothness accuracy ) {

		if (accuracy == RoutePathSmoothness.ROUGH) {
			return geoPoints;
		} else if (accuracy == RoutePathSmoothness.FINE) {
			List<GeoPoint> merged = new ArrayList<GeoPoint>();
			
			Iterator<LegAbstract> itr = legs.listIterator();
			while( itr.hasNext() ) {
				LegAbstract leg = (LegAbstract)itr.next();
				List<GeoPoint> points = leg.getGeoPointPath();
				if (points != null) {
					merged.addAll(points);
				}
			}
			
			return merged;
		}
		
		return null;
	}
	
	public void setWaypoints(List<WaypointAbstract> waypointAbstracts) {
		this.waypointAbstracts = waypointAbstracts;
	}
	public List<WaypointAbstract> getWaypoints() {
		return waypointAbstracts;
	}
	
	public void setLegs(List legs) {
		this.legs = legs;
	}
	public List<LegAbstract> getLegs() {
		return this.legs;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getSummary() {
		return this.summary;
	}

	public void setCopyrights(String copyrights) {
		this.copyrights = copyrights;
	}
	public String getCopyrights() {
		return this.copyrights;
	}

	public List<String> getWarnings() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toString() {
		return "Route: [" + this.getLegs() + "] " + this.getCopyrights();
	}


}