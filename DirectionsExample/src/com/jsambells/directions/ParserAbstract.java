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
import java.util.List;

import android.os.AsyncTask;

import com.google.android.maps.GeoPoint;
import com.jsambells.directions.google.DirectionsAPIStep;

public abstract class ParserAbstract
{
	public interface IDirectionsListener {
		/**
		 * Invoked when driving directions have become available.
		 * 
		 * @param routeAbstract The route that defines the driving path.
		 */
		public abstract void onDirectionsAvailable (RouteAbstract routeAbstract, Mode mode);
		
		/**
		 * Invoked when driving directions are not available.
		 */
		public abstract void onDirectionsNotAvailable ();
	}
	
	public enum Mode {
		DRIVING,
		WALKING,
		BICYCLING
	}

	private Mode mode;
	private IDirectionsListener listener;
	
	/**
	 * Get the driving directions from one point to another.
	 * 
	 * @param startPoint The starting point.
	 * @param endPoint The ending point.
	 * @param mode The driving mode, either driving or walking.
	 * @param listener The object to be notified when the directions are available. It can be null.
	 */
	public AsyncTask getDirectionsThruWaypoints(List<GeoPoint> waypoints, Mode mode, IDirectionsListener listener)
	{
		if ((waypoints == null) || (waypoints.size() < 2) || (mode == null)) {
			throw new IllegalArgumentException ("waypoints must be > 1 or mode arguments can't be null");
		}
		
		this.mode = mode;
		this.listener = listener;
		
		return getThruWaypoints(waypoints, mode, listener);
	}
	
	/**
	 * Subclasses must override this method and provide their specific implementation to retrieve
	 * the driving directions from one point to another.
	 * 
	 * @param startPoint The starting point.
	 * @param endPoint The ending point.
	 * @param mode The driving mode, either driving or walking.
	 * @param listener The object to be notified when the directions are available. It can be null.
	 */
	protected abstract AsyncTask getThruWaypoints(List<GeoPoint>waypoints, Mode mode, IDirectionsListener listener);
	
	protected void onDirectionsAvailable (RouteAbstract routeAbstract)
	{
		if (listener != null) {
			listener.onDirectionsAvailable(routeAbstract, mode);
		}
	}
	
	protected void onDirectionsNotAvailable ()
	{
		if (listener != null) {
			listener.onDirectionsNotAvailable();
		}
	}
}
