package com.directionsexample;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.jsambells.directions.RouteAbstract;
import com.jsambells.directions.ParserAbstract.Mode;
import com.jsambells.directions.google.DirectionsAPI;
import com.jsambells.directions.google.DirectionsAPIRoute;

public class DirectionsExample extends MapActivity implements com.jsambells.directions.ParserAbstract.IDirectionsListener {
	
	protected MapView map;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        String mapKey = "0mdWGydqzklzo5EUI42p8Acf6v9OqxHWDvWAMyg";
        // Create your own key: @see http://code.google.com/android/add-ons/google-apis/mapkey.html#getdebugfingerprint
        
        map = new MapView(this, mapKey);
		map.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		map.setClickable(true);
		map.setEnabled(true);
		map.setBuiltInZoomControls(true);
		
		LinearLayout main = (LinearLayout)findViewById(R.id.main);
		main.addView(map);
        
		// Find a route
		List<GeoPoint> waypoints = new ArrayList<GeoPoint>();
		
    	// Lets go on a tower tour!
		waypoints.add(new GeoPoint(43642085,-79386976)); // CN Tower
		waypoints.add(new GeoPoint(44588328,-104698527)); // Devils Tower
		waypoints.add(new GeoPoint(37802341,-122405811)); // Coit Tower
		
		DirectionsAPI directions = new DirectionsAPI();
		directions.getDirectionsThruWaypoints(
			waypoints, 
			DirectionsAPI.Mode.DRIVING, 
			this
		);
	
        
    }

	/* MapActivity */
	
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* IDirectionsListener */
    
	public void onDirectionsAvailable(RouteAbstract route, Mode mode) {
		// TODO Auto-generated method stub
		// Add it to a map
		
		// Add a directions overlay to the map.
		// This is just a quick example to draw the line on the map.
		DirectionsOverlay directions = new DirectionsOverlay();
		directions.setRoute((DirectionsAPIRoute)route);
		map.getOverlays().add(directions);
		map.requestLayout();
		
	}
	public void onDirectionsNotAvailable() {
		// TODO Auto-generated method stub
		// Show an error?
	}
	
	public class DirectionsOverlay extends Overlay {

		static final String TAG = "DirectionsOverlay";

		// The route to draw
		private DirectionsAPIRoute mRoute;

		// Our Paint
		Paint pathPaint = new Paint();
		
		public DirectionsOverlay() {
			this.pathPaint.setAntiAlias(true);
		}

		public DirectionsOverlay setRoute( DirectionsAPIRoute route) {
			this.mRoute = route;
			return this;
		}
		
		// This function does some fancy drawing
		public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
			
			// This method will be called twice. Once in the 
			// shadow phase, skewed and darkened, then again in 
			// the non-shadow phase. 
			
			if (this.mRoute != null && !shadow) {

				Path thePath = new Path();

				/* Reset our paint. */
				this.pathPaint.setStrokeWidth(3);
				this.pathPaint.setARGB(200, 255, 0, 0);
				// holders of mapped coords...
				Point screen = new Point();

				List<GeoPoint> drawPoints = mRoute.getGeoPoints();
				
				Iterator<GeoPoint> itr = drawPoints.listIterator();

				// convert the start point.
				mapView.getProjection().toPixels( (GeoPoint)itr.next(), screen );
				thePath.moveTo(screen.x, screen.y);
				
				while( itr.hasNext() ) {
					GeoPoint p = (GeoPoint)itr.next();
					map.getProjection().toPixels( p, screen);
				    thePath.lineTo(screen.x, screen.y);
				}
				
				this.pathPaint.setStyle(Paint.Style.STROKE);
				canvas.drawPath(thePath, this.pathPaint);

			}

			super.draw(canvas, mapView, shadow);
			
		}
		
	}

}