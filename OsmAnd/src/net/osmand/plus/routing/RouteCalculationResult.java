package net.osmand.plus.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.osmand.OsmAndFormatter;
import net.osmand.osm.LatLon;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;
import net.osmand.plus.activities.ApplicationMode;
import net.osmand.router.RouteSegmentResult;
import net.osmand.router.TurnType;
import android.content.Context;
import android.location.Location;

public class RouteCalculationResult {
	// could not be null and immodifiable!
	private final List<Location> locations;
	private final List<RouteDirectionInfo> directions;
	private final String errorMessage;
	private final int[] listDistance;
	

	// Note always currentRoute > get(currentDirectionInfo).routeOffset, 
	//         but currentRoute <= get(currentDirectionInfo+1).routeOffset 
	protected int currentDirectionInfo = 0;
	protected int currentRoute = 0;

	public RouteCalculationResult(String errorMessage) {
		this(null, null, null, null, errorMessage, null, false, false);
	}

	public RouteCalculationResult(List<Location> list, List<RouteDirectionInfo> directions, Location start, LatLon end, String errorMessage, 
			Context ctx, boolean leftSide, boolean addMissingTurns) {
		this.errorMessage = errorMessage;
		List<Location> locations = list == null ? new ArrayList<Location>() : new ArrayList<Location>(list);
		List<RouteDirectionInfo> localDirections = directions == null? new ArrayList<RouteDirectionInfo>() : new ArrayList<RouteDirectionInfo>(directions);
		if (!locations.isEmpty()) {
			// if there is no closest points to start - add it
			introduceFirstPoint(locations, start);
			checkForDuplicatePoints(locations);
			removeUnnecessaryGoAhead();
		}
		this.locations = Collections.unmodifiableList(locations);
		this.listDistance = new int[locations.size()];
		updateListDistanceTime();
		
		if(addMissingTurns) {
			OsmandSettings settings = ((OsmandApplication) ctx.getApplicationContext()).getSettings();
			addMissingTurnsToRoute(localDirections, start, end, settings.getApplicationMode(), ctx, leftSide);
		}
		this.directions = Collections.unmodifiableList(localDirections);
		updateDirectionsTime();
	}
	
	public RouteCalculationResult(List<RouteSegmentResult> list, Location start, LatLon end, 
			Context ctx, boolean leftSide) {
		List<RouteDirectionInfo> computeDirections = new ArrayList<RouteDirectionInfo>();
		this.errorMessage = null;
		List<Location> locations = new ArrayList<Location>();
		convertVectorResult(computeDirections, locations, list, ctx);
		introduceFirstPoint(locations, start);
		this.locations = Collections.unmodifiableList(locations);
		this.listDistance = new int[locations.size()];
		updateListDistanceTime();
		
		this.directions = Collections.unmodifiableList(computeDirections);
		updateDirectionsTime();
	}

	/**
	 * PREPARATION 
	 */
	private void convertVectorResult(List<RouteDirectionInfo> directions, List<Location> locations, List<RouteSegmentResult> list, Context ctx) {
		float prevDirectionTime = 0;
		float prevDirectionDistance = 0;
		for (int routeInd = 0; routeInd < list.size(); routeInd++) {
			RouteSegmentResult s = list.get(routeInd);
			boolean plus = s.getStartPointIndex() < s.getEndPointIndex();
			int i = s.getStartPointIndex();
			int prevLocationSize = locations.size();
			while (true) {
				Location n = new Location(""); //$NON-NLS-1$
				LatLon point = s.getPoint(i);
				n.setLatitude(point.getLatitude());
				n.setLongitude(point.getLongitude());
				if (i == s.getEndPointIndex() && routeInd != list.size() - 1) {
					break;
				}
				locations.add(n);
				if (i == s.getEndPointIndex() ) {
					break;
				}
				
				if (plus) {
					i++;
				} else {
					i--;
				}
			}
			TurnType turn = s.getTurnType();

			if(turn != null) {
				RouteDirectionInfo info = new RouteDirectionInfo(s.getSegmentSpeed(), turn);
				String description = toString(turn, ctx);
				info.setDescriptionRoute(description);
				info.routePointOffset = prevLocationSize;
				if(directions.size() > 0 && prevDirectionTime > 0 && prevDirectionDistance > 0) {
					RouteDirectionInfo prev = directions.get(directions.size() - 1);
					prev.setAverageSpeed(prevDirectionDistance / prevDirectionTime);
					prev.setDescriptionRoute(prev.getDescriptionRoute() + " " + OsmAndFormatter.getFormattedDistance(prevDirectionDistance, ctx));
					prevDirectionDistance = 0;
					prevDirectionTime = 0;
				}
				directions.add(info);
			}
			prevDirectionDistance += s.getDistance();
			prevDirectionTime += s.getSegmentTime();
		}
		if(directions.size() > 0 && prevDirectionTime > 0 && prevDirectionDistance > 0) {
			RouteDirectionInfo prev = directions.get(directions.size() - 1);
			prev.setAverageSpeed(prevDirectionDistance / prevDirectionTime);
			prev.setDescriptionRoute(prev.getDescriptionRoute() + " " + OsmAndFormatter.getFormattedDistance(prevDirectionDistance, ctx));
		}
	}
	
	protected void addMissingTurnsToRoute(List<RouteDirectionInfo> originalDirections, Location start, LatLon end, ApplicationMode mode, Context ctx,
			boolean leftSide){
		if(!isCalculated()){
			return;
		}
		// speed m/s
		float speed = 1.5f;
		int minDistanceForTurn = 5;
		if(mode == ApplicationMode.CAR){
			speed = 15.3f;
			minDistanceForTurn = 35;
		} else if(mode == ApplicationMode.BICYCLE){
			speed = 5.5f;
			minDistanceForTurn = 12;
		}

		List<RouteDirectionInfo> computeDirections = new ArrayList<RouteDirectionInfo>();
		int[] listDistance = getListDistance();
		
		int previousLocation = 0;
		int prevBearingLocation = 0;
		RouteDirectionInfo previousInfo = new RouteDirectionInfo(speed, TurnType.valueOf(TurnType.C, leftSide));
		previousInfo.routePointOffset = 0;
		previousInfo.setDescriptionRoute(ctx.getString( R.string.route_head));
		computeDirections.add(previousInfo);
		
		int distForTurn = 0;
		float previousBearing = 0;
		int startTurnPoint = 0;
		
		
		for (int i = 1; i < locations.size() - 1; i++) {
			
			Location next = locations.get(i + 1);
			Location current = locations.get(i);
			float bearing = current.bearingTo(next);
			// try to get close to current location if possible
			while(prevBearingLocation < i - 1){
				if(locations.get(prevBearingLocation + 1).distanceTo(current) > 70){
					prevBearingLocation ++;
				} else {
					break;
				}
			}
			
			if(distForTurn == 0){
				// measure only after turn
				previousBearing = locations.get(prevBearingLocation).bearingTo(current);
				startTurnPoint = i;
			}
			
			TurnType type = null;
			String description = null;
			float delta = previousBearing - bearing;
			while(delta < 0){
				delta += 360;
			}
			while(delta > 360){
				delta -= 360;
			}
			
			distForTurn += locations.get(i).distanceTo(locations.get(i + 1)); 
			if (i < locations.size() - 1 &&  distForTurn < minDistanceForTurn) {
				// For very smooth turn we try to accumulate whole distance
				// simply skip that turn needed for situation
				// 1) if you are going to have U-turn - not 2 left turns
				// 2) if there is a small gap between roads (turn right and after 4m next turn left) - so the direction head
				continue;
			}
			
			
			if(delta > 45 && delta < 315){
				
				if(delta < 60){
					type = TurnType.valueOf(TurnType.TSLL, leftSide);
					description = ctx.getString( R.string.route_tsll);
				} else if(delta < 120){
					type = TurnType.valueOf(TurnType.TL, leftSide);
					description = ctx.getString( R.string.route_tl);
				} else if(delta < 150){
					type = TurnType.valueOf(TurnType.TSHL, leftSide);
					description = ctx.getString( R.string.route_tshl);
				} else if(delta < 210){
					type = TurnType.valueOf(TurnType.TU, leftSide);
					description = ctx.getString( R.string.route_tu);
				} else if(delta < 240){
					description = ctx.getString( R.string.route_tshr);
					type = TurnType.valueOf(TurnType.TSHR, leftSide);
				} else if(delta < 300){
					description = ctx.getString( R.string.route_tr);
					type = TurnType.valueOf(TurnType.TR, leftSide);
				} else {
					description = ctx.getString( R.string.route_tslr);
					type = TurnType.valueOf(TurnType.TSLR, leftSide);
				}
				
				// calculate for previousRoute 
				previousInfo.distance = listDistance[previousLocation]- listDistance[i];
				previousInfo.setDescriptionRoute(previousInfo.getDescriptionRoute()
						+ " " + OsmAndFormatter.getFormattedDistance(previousInfo.distance, ctx)); //$NON-NLS-1$
				type.setTurnAngle(360 - delta);
				previousInfo = new RouteDirectionInfo(speed, type);
				previousInfo.setDescriptionRoute(description);
				previousInfo.routePointOffset = startTurnPoint;
				computeDirections.add(previousInfo);
				previousLocation = startTurnPoint;
				prevBearingLocation = i; // for bearing using current location
			}
			// clear dist for turn
			distForTurn = 0;
		} 
			
		previousInfo.distance = listDistance[previousLocation];
		previousInfo.setDescriptionRoute(previousInfo.getDescriptionRoute()
				+ " " + OsmAndFormatter.getFormattedDistance(previousInfo.distance, ctx)); //$NON-NLS-1$
		
		// add last direction go straight (to show arrow in screen after all turns)
		if(previousInfo.distance > 80){
			RouteDirectionInfo info = new RouteDirectionInfo(speed, TurnType.valueOf(TurnType.C, leftSide));
			info.distance = 0;
			info.routePointOffset = locations.size() - 1;
			computeDirections.add(info);
		}
		
		
		if (originalDirections.isEmpty()) {
			originalDirections.addAll(computeDirections);
		} else {
			int currentDirection = 0;
			// one more
			for (int i = 0; i <= originalDirections.size() && currentDirection < computeDirections.size(); i++) {
				while (currentDirection < computeDirections.size()) {
					int distanceAfter = 0;
					if (i < originalDirections.size()) {
						RouteDirectionInfo resInfo = originalDirections.get(i);
						int r1 = computeDirections.get(currentDirection).routePointOffset;
						int r2 = resInfo.routePointOffset;
						distanceAfter = listDistance[resInfo.routePointOffset];
						float dist = locations.get(r1).distanceTo(locations.get(r2));
						// take into account that move roundabout is special turn that could be very lengthy
						if (dist < 100) {
							// the same turn duplicate
							currentDirection++;
							continue; // while cycle
						} else if (computeDirections.get(currentDirection).routePointOffset > resInfo.routePointOffset) {
							// check it at the next point
							break;
						}
					}

					// add turn because it was missed
					RouteDirectionInfo toAdd = computeDirections.get(currentDirection);

					if (i > 0) {
						// update previous
						RouteDirectionInfo previous = originalDirections.get(i - 1);
						toAdd.setAverageSpeed(previous.getAverageSpeed());
					}
					toAdd.distance = listDistance[toAdd.routePointOffset] - distanceAfter;
					if (i < originalDirections.size()) {
						originalDirections.add(i, toAdd);
					} else {
						originalDirections.add(toAdd);
					}
					i++;
					currentDirection++;
				}
			}

		}
		
		int sum = 0;
		for (int i = originalDirections.size() - 1; i >= 0; i--) {
			originalDirections.get(i).afterLeftTime = sum;
			sum += originalDirections.get(i).getExpectedTime();
		}
	}
	
	
	public String toString(TurnType type, Context ctx) {
		if(type.isRoundAbout()){
			return ctx.getString(R.string.route_roundabout, type.getExitOut());
		} else if(type.getValue().equals(TurnType.C)) {
			return ctx.getString(R.string.route_head);
		} else if(type.getValue().equals(TurnType.TSLL)) {
			return ctx.getString(R.string.route_tsll);
		} else if(type.getValue().equals(TurnType.TL)) {
			return ctx.getString(R.string.route_tl);
		} else if(type.getValue().equals(TurnType.TSHL)) {
			return ctx.getString(R.string.route_tshl);
		} else if(type.getValue().equals(TurnType.TSLR)) {
			return ctx.getString(R.string.route_tslr);
		} else if(type.getValue().equals(TurnType.TR)) {
			return ctx.getString(R.string.route_tr);
		} else if(type.getValue().equals(TurnType.TSHR)) {
			return ctx.getString(R.string.route_tshr);
		} else if(type.getValue().equals(TurnType.TU)) {
			return ctx.getString(R.string.route_tu);
		} else if(type.getValue().equals(TurnType.TRU)) {
			return ctx.getString(R.string.route_tu);
		} else if(type.getValue().equals(TurnType.KL)) {
			return ctx.getString(R.string.route_kl);
		} else if(type.getValue().equals(TurnType.KR)) {
			return ctx.getString(R.string.route_kr);
		}
		return "";
	}

	public String getErrorMessage() {
		return errorMessage;
	}


	/**
	 * PREPARATION 
	 * Remove unnecessary go straight from CloudMade.
	 * Remove also last direction because it will be added after.
	 */
	private void removeUnnecessaryGoAhead() {
		if (directions != null && directions.size() > 1) {
			for (int i = 1; i < directions.size();) {
				RouteDirectionInfo r = directions.get(i);
				if (r.getTurnType().getValue().equals(TurnType.C)) {
					RouteDirectionInfo prev = directions.get(i - 1);
					prev.setAverageSpeed((prev.distance + r.distance)
							/ (prev.distance / prev.getAverageSpeed() + r.distance / r.getAverageSpeed()));
					directions.remove(i);
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * PREPARATION
	 * Check points for duplicates (it is very bad for routing) - cloudmade could return it
	 */
	private void checkForDuplicatePoints(List<Location> locations) {
		// 
		for (int i = 0; i < locations.size() - 1;) {
			if (locations.get(i).distanceTo(locations.get(i + 1)) == 0) {
				locations.remove(i);
				if (directions != null) {
					for (RouteDirectionInfo info : directions) {
						if (info.routePointOffset > i) {
							info.routePointOffset--;
						}
					}
				}
			} else {
				i++;
			}
		}
	}

	/**
	 * PREPARATION
	 * If beginning is too far from start point, then introduce GO Ahead
	 */
	private void introduceFirstPoint(List<Location> locations, Location start) {
		if (!locations.isEmpty() && locations.get(0).distanceTo(start) > 200) {
			// add start point
			locations.add(0, start);
			if (directions != null && !directions.isEmpty()) {
				for (RouteDirectionInfo i : directions) {
					i.routePointOffset++;
				}
				RouteDirectionInfo info = new RouteDirectionInfo(directions.get(0).getAverageSpeed(),
						TurnType.valueOf(TurnType.C, false));
				info.routePointOffset = 0;
				// info.setDescriptionRoute(ctx.getString( R.string.route_head));//; //$NON-NLS-1$
				directions.add(0, info);
			}
		}
	}

	/**
	 * PREPARATION
	 * At the end always update listDistance local vars and time
	 */
	private void updateListDistanceTime() {
		if (listDistance.length > 0) {
			listDistance[locations.size() - 1] = 0;
			for (int i = locations.size() - 1; i > 0; i--) {
				listDistance[i - 1] = (int) locations.get(i - 1).distanceTo(locations.get(i));
				listDistance[i - 1] += listDistance[i];
			}
		}
	}
	
	private void updateDirectionsTime() {
		int sum = 0;
		for (int i = directions.size() - 1; i >= 0; i--) {
			directions.get(i).afterLeftTime = sum;
			directions.get(i).distance = listDistance[directions.get(i).routePointOffset];
			if (i < directions.size() - 1) {
				directions.get(i).distance -= listDistance[directions.get(i + 1).routePointOffset];
			}
			sum += directions.get(i).getExpectedTime();
		}
	}
	
	//////////////////// MUST BE ALL SYNCHRONIZED ??? //////////////////////
	
	public List<Location> getImmutableLocations() {
		return locations;
	}
	
	public List<RouteDirectionInfo> getDirections() {
		return directions ;
	}
	
	
	public List<Location> getNextLocations() {
		if(currentRoute < locations.size()) {
			return locations.subList(currentRoute, locations.size());
		}
		return Collections.emptyList();
	}


	public int[] getListDistance() {
		return listDistance;
	}

	public boolean isCalculated() {
		return !locations.isEmpty();
	}
	
	public boolean isEmpty() {
		return locations.isEmpty() || currentRoute >= locations.size();
	}
	
	
	public void updateCurrentRoute(int currentRoute) {
		this.currentRoute = currentRoute;
		while (currentDirectionInfo < directions.size() - 1 && directions.get(currentDirectionInfo + 1).routePointOffset < currentRoute) {
			currentDirectionInfo++;
		}
	}
	
	public Location getLocationFromRouteDirection(RouteDirectionInfo i){
		if(i.routePointOffset < locations.size()){
			return locations.get(i.routePointOffset);
		}
		return null;
	}
	
	public RouteDirectionInfo getNextRouteDirectionInfo(){
		if(currentDirectionInfo < directions.size() - 1){
			return directions.get(currentDirectionInfo + 1);
		}
		return null;
	}
	
	public RouteDirectionInfo getCurrentRouteDirectionInfo(){
		if(currentDirectionInfo < directions.size()){
			return directions.get(currentDirectionInfo);
		}
		return null;
	}
	public RouteDirectionInfo getNextNextRouteDirectionInfo(){
		if(currentDirectionInfo < directions.size() - 2){
			return directions.get(currentDirectionInfo + 2);
		}
		return null;
	}

	public List<RouteDirectionInfo> getRouteDirections() {
		if(currentDirectionInfo < directions.size()){
			if(currentDirectionInfo == 0){
				return directions;
			}
			if(currentDirectionInfo < directions.size() - 1){
				return directions.subList(currentDirectionInfo + 1, directions.size());
			}
		}
		return Collections.emptyList();
	}
	
	public Location getNextRouteLocation() {
		if(currentRoute < locations.size()) {
			return locations.get(currentRoute);
		}
		return null;
	}
	
	public Location getNextRouteLocation(int after) {
		if(currentRoute + after < locations.size()) {
			return locations.get(currentRoute + after);
		}
		return null;
	}
	
	public boolean directionsAvailable(){
		return currentDirectionInfo < directions.size();
	}
	
	public int getDistanceToNextTurn(Location fromLoc) {
		if (currentDirectionInfo < directions.size()) {
			int dist = listDistance[currentRoute];
			if (currentDirectionInfo < directions.size() - 1) {
				dist -= listDistance[directions.get(currentDirectionInfo + 1).routePointOffset];
			}
			if (fromLoc != null) {
				dist += fromLoc.distanceTo(locations.get(currentRoute));
			}
			return dist;
		}
		return -1;
	}
	
	public int getDistanceFromNextToNextNextTurn() {
		if (currentDirectionInfo < directions.size() - 1) {
			int dist = listDistance[directions.get(currentDirectionInfo + 1).routePointOffset];
			if (currentDirectionInfo < directions.size() - 2) {
				dist -= listDistance[directions.get(currentDirectionInfo + 2).routePointOffset];
			}
			return dist;
		}
		return -1;
	}
	
	
	public int getDistanceToFinish(Location fromLoc) {
		if(listDistance != null && currentRoute < listDistance.length){
			int dist = listDistance[currentRoute];
			Location l = locations.get(currentRoute);
			if(fromLoc != null){
				dist += fromLoc.distanceTo(l);
			}
			return dist;
		}
		return 0;
	}

	
}