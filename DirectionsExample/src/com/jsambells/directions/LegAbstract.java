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
import com.jsambells.directions.RouteAbstract.RoutePathSmoothness;
import com.jsambells.directions.google.DirectionsAPIWaypoint;

/**
 * Represents a leg of a route.
 * 
 */
public abstract class LegAbstract
{
	List<StepAbstract> steps;
	DirectionsAPIWaypoint startLocation, endLocation;
	String startAddress, endAddress, distance, duration;
	
	public void setSteps(List steps) {
		this.steps = steps;
	}
	public List<StepAbstract> getSteps() {
		return this.steps;
	}

	public String getDistance() {
		return this.distance;
	}

	public String getDuration() {
		return this.duration;
	}

	public void setEndAddress(String addr) {
		this.endAddress = addr;
	}
	public String getEndAddress() {
		return this.endAddress;
	}

	public void setEndLocation(DirectionsAPIWaypoint wp) {
		this.endLocation = wp;
	}
	public DirectionsAPIWaypoint getEndLocation() {
		return this.endLocation;
	}

	public void setStartAddress(String addr) {
		this.startAddress = addr;
	}
	public String getStartAddress() {
		return this.startAddress;
	}

	public void setStartLocation(DirectionsAPIWaypoint wp) {
		this.startLocation = wp;
	}
	public DirectionsAPIWaypoint getStartLocation() {
		return this.startLocation;
	}

	public List<GeoPoint> getGeoPointPath( ) {
		List<GeoPoint> merged = new ArrayList<GeoPoint>();
		
		Iterator<StepAbstract> itr = steps.listIterator();
		while( itr.hasNext() ) {
			StepAbstract step = (StepAbstract)itr.next();
			List<GeoPoint> points = step.getGeoPoints();
			if (points != null) {
				merged.addAll(points);
			}
		}
		return merged;
	}
	
	public String toString() {
		return "Leg: (" + this.getStartLocation() + ")->(" + this.getEndLocation() + ") [" + this.getSteps() + "] ";
	}

}