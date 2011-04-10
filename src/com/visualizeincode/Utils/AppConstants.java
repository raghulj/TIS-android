package com.visualizeincode.Utils;

public class AppConstants {

	//public static final String BASE_URL				=	"http://192.168.0.4/btis/";
	public static final String BASE_URL				=	"http://btis.in/";
	public static final String CITY_URL 			=	BASE_URL+"cities.json";
	public static final String BUS_URL				=	BASE_URL+"javascripts/bus_stops.js";
	public static final String TRAFFIC_HOTSPOTS		=	BASE_URL+"trafficstatus_cache.txt";
	public static final String TRAFFIC_CAMERA_IMAGE	=	BASE_URL+"cameras/images/";
	public static final String TRAFFIC_FINE			=	BASE_URL+"fines/fetch";
	
	public static final String BUS_NORESULT			=	BASE_URL+"php/getStagePoints.php?type=route&routeno=" ;
	public static final String BUS_LOCRESULT		=	BASE_URL+"php/getStagePoints.php?type=stop&stage=";
	public static final String CAMERA_PICTURE		=	BASE_URL+"cameras/images/";
	
	// post type "route" 91-c"
	public static final String BUS_ROUTE			=	BASE_URL+"bus/route";
	
	public static final String TRAFFIC_RTO			=	BASE_URL+"rto/fetch";
	
	public static final int MENU_HOTSPOT			=	0;
	public static final int MENU_CAMERA				=	1;
	public static final int MENU_BUSES				=	2;
	public static final int MENU_FINES				=	3;
	public static final int MENU_RTO				=	4;
	public static final int MENU_DIRECTIONS			=	5;
	
	
	public static final int SHOW_CAMERA				=	0;
	public static final int SHOW_HOTSPOT			=	1;
	public static final int SHOW_BUSSTOP			=	2;
	
	
	//Preference constants
	public static final String APP_LAUNCHED			=	"LAUNCHED";
	public static final String DEFAULT_CITY_NAME	=	"CITY_NAME";
	public static final String DEFAULT_CITY_ID		=	"CITY_ID";
}
