package com.osmand.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.osmand.Algoritms;

// http://wiki.openstreetmap.org/wiki/Amenity
// POI tags : amenity, leisure, shop, sport, tourism, historic; accessories (internet-access), natural ?
public enum AmenityType {
	// Some of those types are subtypes of Amenity tag 
	SUSTENANCE, // restaurant, cafe ...
	EDUCATION, // school, ...
	TRANSPORTATION, // car_wash, parking, ...
	FINANCE, // bank, atm, ...
	HEALTHCARE, // hospital ...
	ENTERTAINMENT, // cinema, ... (+! sauna, brothel)
	TOURISM, // [TAG] hotel, sights, museum .. 
	HISTORIC, // [TAG] historic places, monuments (should we unify tourism/historic)
	SHOP, // [TAG] convenience (product), clothes...
	LEISURE_AND_SPORT, // [TAG] sport
	OTHER, // grave-yard, police, post-office [+Internet_access]
	;
	
	public static AmenityType fromString(String s){
		try {
			return AmenityType.valueOf(s.toUpperCase());
		} catch (IllegalArgumentException e) {
			return AmenityType.OTHER;
		}
	}
	
	public static String valueToString(AmenityType t){
		return t.toString().toLowerCase();
	}
	
	public static AmenityType[] getCategories(){
		return AmenityType.values();
	}
	
	public static List<String> getSubCategories(AmenityType t){
		List<String> list = new ArrayList<String>();
		for(String s : amenityMap.keySet()){
			if(amenityMap.get(s) == t){
				list.add(s);
			}
		}
		return list;
	}
	
	public static String toPublicString(AmenityType t){
		return Algoritms.capitalizeFirstLetterAndLowercase(t.toString().replace('_', ' '));
	}
	

	protected static Map<String, AmenityType> amenityMap = new LinkedHashMap<String, AmenityType>();
	static {
		
		amenityMap.put("alpine_hut", AmenityType.TOURISM);
		amenityMap.put("attraction", AmenityType.TOURISM);
		amenityMap.put("artwork", AmenityType.TOURISM);
		amenityMap.put("camp_site", AmenityType.TOURISM);
		amenityMap.put("caravan_site", AmenityType.TOURISM);
		amenityMap.put("chalet", AmenityType.TOURISM);
		amenityMap.put("guest_house", AmenityType.TOURISM);
		amenityMap.put("hostel", AmenityType.TOURISM);
		amenityMap.put("hotel", AmenityType.TOURISM);
		amenityMap.put("information", AmenityType.TOURISM);
		amenityMap.put("motel", AmenityType.TOURISM);
		amenityMap.put("museum", AmenityType.TOURISM);
		amenityMap.put("picnic_site", AmenityType.TOURISM);
		amenityMap.put("theme_park", AmenityType.TOURISM);
		amenityMap.put("viewpoint", AmenityType.TOURISM);
		amenityMap.put("zoo", AmenityType.TOURISM);
		
		amenityMap.put("archaeological_site", AmenityType.HISTORIC);
		amenityMap.put("battlefield", AmenityType.HISTORIC);
		amenityMap.put("boundary_stone", AmenityType.HISTORIC);
		amenityMap.put("castle", AmenityType.HISTORIC);
		amenityMap.put("fort", AmenityType.HISTORIC);
		amenityMap.put("memorial", AmenityType.HISTORIC);
		amenityMap.put("pa", AmenityType.HISTORIC);
		amenityMap.put("monument", AmenityType.HISTORIC);
		amenityMap.put("ruins", AmenityType.HISTORIC);
		amenityMap.put("wayside_cross", AmenityType.HISTORIC);
		amenityMap.put("wayside_shrine", AmenityType.HISTORIC);
		amenityMap.put("wreck", AmenityType.HISTORIC);
		
		amenityMap.put("alcohol", AmenityType.SHOP);
		amenityMap.put("bakery", AmenityType.SHOP);
		amenityMap.put("beauty", AmenityType.SHOP);
		amenityMap.put("beverages", AmenityType.SHOP);
		amenityMap.put("bicycle", AmenityType.SHOP);
		amenityMap.put("books", AmenityType.SHOP);
		amenityMap.put("boutique", AmenityType.SHOP);
		amenityMap.put("butcher", AmenityType.SHOP);
		amenityMap.put("car", AmenityType.SHOP);
		amenityMap.put("car_repair", AmenityType.SHOP);
		amenityMap.put("charity", AmenityType.SHOP);
		amenityMap.put("chemist", AmenityType.SHOP);
		amenityMap.put("clothes", AmenityType.SHOP);
		amenityMap.put("computer", AmenityType.SHOP);
		amenityMap.put("confectionery", AmenityType.SHOP);
		amenityMap.put("convenience", AmenityType.SHOP);
		amenityMap.put("department_store", AmenityType.SHOP);
		amenityMap.put("dry_cleaning", AmenityType.SHOP);
		amenityMap.put("doityourself", AmenityType.SHOP);
		amenityMap.put("electronics", AmenityType.SHOP);
		amenityMap.put("fabrics", AmenityType.SHOP);
		amenityMap.put("farm", AmenityType.SHOP);
		amenityMap.put("florist", AmenityType.SHOP);
		amenityMap.put("funeral_directors", AmenityType.SHOP);
		amenityMap.put("furniture", AmenityType.SHOP);
		amenityMap.put("garden_centre", AmenityType.SHOP);
		amenityMap.put("general", AmenityType.SHOP);
		amenityMap.put("gift", AmenityType.SHOP);
		amenityMap.put("glaziery", AmenityType.SHOP);
		amenityMap.put("greengrocer", AmenityType.SHOP);
		amenityMap.put("hairdresser", AmenityType.SHOP);
		amenityMap.put("hardware", AmenityType.SHOP);
		amenityMap.put("hearing_aids", AmenityType.SHOP);
		amenityMap.put("hifi", AmenityType.SHOP);
		amenityMap.put("ice_cream", AmenityType.SHOP);
		amenityMap.put("hardware", AmenityType.SHOP);
		amenityMap.put("hearing_aids", AmenityType.SHOP);
		amenityMap.put("hifi", AmenityType.SHOP);
		amenityMap.put("ice_cream", AmenityType.SHOP);
		amenityMap.put("jewelry", AmenityType.SHOP);
		amenityMap.put("kiosk", AmenityType.SHOP);
		amenityMap.put("laundry", AmenityType.SHOP);
		amenityMap.put("mall", AmenityType.SHOP);
		amenityMap.put("massage", AmenityType.SHOP);
		amenityMap.put("money_lender", AmenityType.SHOP);
		amenityMap.put("motorcycle", AmenityType.SHOP);
		amenityMap.put("newsagent", AmenityType.SHOP);
		amenityMap.put("optician", AmenityType.SHOP);
		amenityMap.put("organic", AmenityType.SHOP);
		amenityMap.put("outdoor", AmenityType.SHOP);
		amenityMap.put("pawnbroker", AmenityType.SHOP);
		amenityMap.put("second_hand", AmenityType.SHOP);
		amenityMap.put("sports", AmenityType.SHOP);
		amenityMap.put("stationery", AmenityType.SHOP);
		amenityMap.put("supermarket", AmenityType.SHOP);
		amenityMap.put("shoes", AmenityType.SHOP);
		amenityMap.put("tattoo", AmenityType.SHOP);
		amenityMap.put("toys", AmenityType.SHOP);
		amenityMap.put("travel_agency", AmenityType.SHOP);
		amenityMap.put("variety_store", AmenityType.SHOP);
		amenityMap.put("video", AmenityType.SHOP);
		
		
		amenityMap.put("dog_park", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("sports_centre", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("golf_course", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("stadium", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("track", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("pitch", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("water_park", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("marina", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("slipway", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("fishing", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("nature_reserve", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("park", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("playground", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("garden", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("common", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("ice_rink", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("miniature_golf", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("dance", AmenityType.LEISURE_AND_SPORT);
		
		
		amenityMap.put("9pin", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("10pin", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("archery", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("athletics", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("australian_football", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("baseball", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("basketball", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("beachvolleyball", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("boules", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("bowls", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("canoe", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("chess", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("climbing", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("cricket", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("cricket_nets", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("croquet", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("cycling", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("diving", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("dog_racing", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("equestrian", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("football", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("golf", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("gymnastics", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("hockey", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("horse_racing", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("ice_stock", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("korfball", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("motor", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("multi", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("orienteering", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("paddle_tennis", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("paragliding", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("pelota", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("racquet", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("rowing", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("rugby", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("shooting", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("skating", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("skateboard", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("skiing", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("soccer", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("swimming", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("table_tennis", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("team_handball", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("tennis", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("toboggan", AmenityType.LEISURE_AND_SPORT);
		amenityMap.put("volleyball", AmenityType.LEISURE_AND_SPORT);
		
		// amenity sub type
		amenityMap.put("place_of_worship", AmenityType.HISTORIC);
		
		amenityMap.put("restaurant", AmenityType.SUSTENANCE);
		amenityMap.put("food_court", AmenityType.SUSTENANCE);
		amenityMap.put("fast_food", AmenityType.SUSTENANCE);
		amenityMap.put("drinking_water", AmenityType.SUSTENANCE);
		amenityMap.put("bbq", AmenityType.SUSTENANCE);
		amenityMap.put("pub", AmenityType.SUSTENANCE);
		amenityMap.put("bar", AmenityType.SUSTENANCE);
		amenityMap.put("cafe", AmenityType.SUSTENANCE);
		amenityMap.put("biergarten", AmenityType.SUSTENANCE);
		
		amenityMap.put("kindergarten", AmenityType.EDUCATION);
		amenityMap.put("school", AmenityType.EDUCATION);
		amenityMap.put("college", AmenityType.EDUCATION);
		amenityMap.put("library", AmenityType.EDUCATION);
		amenityMap.put("university", AmenityType.EDUCATION);

		amenityMap.put("ferry_terminal", AmenityType.TRANSPORTATION);
		amenityMap.put("bicycle_parking", AmenityType.TRANSPORTATION);
		amenityMap.put("bicycle_rental", AmenityType.TRANSPORTATION);
		amenityMap.put("bus_station", AmenityType.TRANSPORTATION);
		amenityMap.put("car_rental", AmenityType.TRANSPORTATION);
		amenityMap.put("car_sharing", AmenityType.TRANSPORTATION);
		amenityMap.put("fuel", AmenityType.TRANSPORTATION);
		amenityMap.put("car_wash", AmenityType.TRANSPORTATION);
		amenityMap.put("grit_bin", AmenityType.TRANSPORTATION);
		amenityMap.put("parking", AmenityType.TRANSPORTATION);
		amenityMap.put("taxi", AmenityType.TRANSPORTATION);
		
		amenityMap.put("atm", AmenityType.FINANCE);
		amenityMap.put("bank", AmenityType.FINANCE);
		amenityMap.put("bureau_de_change", AmenityType.FINANCE);
		
		amenityMap.put("pharmacy", AmenityType.HEALTHCARE);
		amenityMap.put("hospital", AmenityType.HEALTHCARE);
		amenityMap.put("baby_hatch", AmenityType.HEALTHCARE);
		amenityMap.put("dentist", AmenityType.HEALTHCARE);
		amenityMap.put("doctors", AmenityType.HEALTHCARE);
		amenityMap.put("veterinary", AmenityType.HEALTHCARE);
		amenityMap.put("first_aid", AmenityType.HEALTHCARE);
		
		amenityMap.put("architect_office", AmenityType.ENTERTAINMENT);
		amenityMap.put("arts_centre", AmenityType.ENTERTAINMENT);
		amenityMap.put("cinema", AmenityType.ENTERTAINMENT);
		amenityMap.put("community_centre", AmenityType.ENTERTAINMENT);
		amenityMap.put("fountain", AmenityType.ENTERTAINMENT);
		amenityMap.put("nightclub", AmenityType.ENTERTAINMENT);
		amenityMap.put("stripclub", AmenityType.ENTERTAINMENT);
		amenityMap.put("studio", AmenityType.ENTERTAINMENT);
		amenityMap.put("theatre", AmenityType.ENTERTAINMENT);
		amenityMap.put("sauna", AmenityType.ENTERTAINMENT);
		amenityMap.put("brothel", AmenityType.ENTERTAINMENT);
		
		amenityMap.put("internet_access", AmenityType.OTHER);
		amenityMap.put("bench", AmenityType.OTHER);
		amenityMap.put("clock", AmenityType.OTHER);
		amenityMap.put("courthouse", AmenityType.OTHER);
		amenityMap.put("crematorium", AmenityType.OTHER);
		amenityMap.put("embassy", AmenityType.OTHER);
		amenityMap.put("emergency_phone", AmenityType.OTHER);
		amenityMap.put("fire_hydrant", AmenityType.OTHER);
		amenityMap.put("fire_station", AmenityType.OTHER);
		amenityMap.put("grave_yard", AmenityType.OTHER);
		amenityMap.put("hunting_stand", AmenityType.OTHER);
		amenityMap.put("marketplace", AmenityType.OTHER);
		amenityMap.put("police", AmenityType.OTHER);
		amenityMap.put("post_box", AmenityType.OTHER);
		amenityMap.put("post_office", AmenityType.OTHER);
		amenityMap.put("prison", AmenityType.OTHER);
		amenityMap.put("public_building", AmenityType.OTHER);
		amenityMap.put("recycling", AmenityType.OTHER);
		amenityMap.put("shelter", AmenityType.OTHER);
		amenityMap.put("telephone", AmenityType.OTHER);
		amenityMap.put("toilets", AmenityType.OTHER);
		amenityMap.put("townhall", AmenityType.OTHER);
		amenityMap.put("vending_machine", AmenityType.OTHER);
		amenityMap.put("waste_basket", AmenityType.OTHER);
		amenityMap.put("waste_disposal", AmenityType.OTHER);
		
	}
	
}