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

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.jsambells.directions.google.DirectionsAPIWaypoint;

/**
 * Represents a step within a route leg.
 * 
 */
public abstract class StepAbstract
{
	
	DirectionsAPIWaypoint start, end;
	String distance, duration, instructions;
	List<GeoPoint> polyline;
	
	
	public void setStartLocation(DirectionsAPIWaypoint wp) {
		this.start = wp;
	}
	public DirectionsAPIWaypoint getStartLocation() {
		return this.start;
	}

	public void setEndLocation(DirectionsAPIWaypoint wp) {
		this.end = wp;
	}
	public DirectionsAPIWaypoint getEndLocation() {
		return this.end;
	}
	
	public void setDuration(String length) {
		this.duration = length;
	}
	public String getDuration() {
		return this.duration;
	}

	public void setGeoPoints(List<GeoPoint> poly) {
		this.polyline = poly;
	}
	public List<GeoPoint> getGeoPoints() {
		return this.polyline;
	}

	public void setDistance(String dist) {
		this.distance = dist;
	}
	public String getDistance() {
		return this.distance;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getInstructions() {
		return this.instructions;
	}

	public String getTravelMode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		return "Step: (" + this.getStartLocation() + ")->(" + this.getEndLocation() + ") " + this.getInstructions();
	}

}