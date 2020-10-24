package GlobalSetting;


import Controller.SensorService;

public class MasterController
{
	public static int POI_count;
	//public static HttprequestModel httprequestmodel ;

	public static SensorService SENSORService = new SensorService();



	public static String strUsername;//user帳戶
	public static String strPassword;//密碼
	public static String strVersion="1.0.33";//手機貼身導遊版本
	public static String strFieldNames[][] =//各table的欄位名稱
			{
					{ "f_id"," f_name" ,"f_account", "f_password", "f_friendNumber", "f_UserLat, f_UserLon"},
					{ "f_id","f_pid", "f_name", "f_lat", "f_lon" ,"f_bigtype","f_smalltype","f_photopath","f_phone","f_info","f_address","f_ranking","f_poitablekey"},
					{ "f_id","f_QRid", "f_store", "f_detail", "f_status" ,"f_storagepath"},
					{ "f_id","f_friendname","f_account", "f_email", "f_info", "f_country" ,"f_photopath"}
			};
	public static String strFieldTypes[][] =//各table的欄位型態
			{
					{ "INTEGER PRIMARY KEY AUTOINCREMENT", "text", "text", "text", "text", "text", "text"},
					{ "INTEGER PRIMARY KEY AUTOINCREMENT", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text", "text"},
					{ "INTEGER PRIMARY KEY AUTOINCREMENT", "text", "text", "text", "text", "text"},
					{ "INTEGER PRIMARY KEY AUTOINCREMENT", "text", "text", "text", "text", "text", "text"},
			};
	public static String strTables[] = { "t_user" ,"t_POI","t_QR","t_Friend"};//要開啟的tables

	public static String[][] POI={
			{"Brigade department","24.875946", "121.265154","Please pay attention to physical health, drink warm water","2"},
			{"Student restaurant","24.877333" ,"121.266914","Today at noon for the double main course","1"},
			{"Library","24.877026", "121.267864","Both physical library and digital knowledge management center","7"},
			{"Armament building","24.875853", "121.266592","Today, the Japanese teacher leave, choose a day to make up classes","4"},
			{"Second teaching building","24.875214", "121.267363","Today's lectures on fluid mechanics are held in the 259 classroom","5"},
			{"First teaching building","24.876325", "121.268614","Please have the person who has the key to return quickly","6"},
			{"Spirit Fortress","24.877219", "121.268490","Do not touch in refurbishment","7"},
			{"Zhongzheng Church","24.877124", "121.269320","Today's air-conditioning maintenance","8"},
			{"Dayu building","24.876542", "121.270329","Please have a man of electronics today remember to make up classes on Thursday night","9"},
			{"Student first hostel","24.877679", "121.265173","Please have a man of electronics today remember to make up classes on Thursday night","5"},
			{"Student second hostel","24.876401", "121.265205","Today captain leave","7"},
			{"Western gate","24.880152", "121.264988","Today captain leave","8"},
			{"Research building","24.875854", "121.269194","Today there are course seminars","6"},
			{"House building","24.878400", "121.269047","Today the dean arrived at the scene","2"},
			{"Football field","24.874521"," 121.265830","It is expected to be weeding on Wednesday afternoon","1"},
			{"Ballistic pavilion","24.873431", "121.265438","Shooting experiment today","11"},
			{"Feian building","24.873648", "121.263778","Please remember to lock the doors and windows","5"},
			{"Weapons park","24.873836", "121.266898","The missile has been completed maintenance","1"},
			{"Large hall","24.874334", "121.267775","Today there are open light weapons exhibition hall","3"},
			{"Ancestral hall","24.874898", "121.268256","Please force the shipbuilding group to attend classes on time","10"},
			{"Playground","24.878406", "121.267927","PU runway slippery Please be careful","1"},
			{"Working group","24.880268", "121.268753","Today's guard post to correct the information","4"},
			{"North gate","24.883197", "121.274520","Today there are open light weapons exhibition hall","5"},
			{"Hot food shop","24.877780", "121.267316","Today,there are selling onion cake","4"},
			{"Convenience store","24.877860", "121.267445","20% off sale","8"},
			{"Barber department","24.877994", "121.267273","Rest today","3"},
			{"Basketball court","24.878309", "121.266394","Open to 1800","9"},
	};
	public static float[][][] PBTPM;
	public static boolean bHascode = false;//是否手機資料庫已有驗證碼
	public static boolean bLoginflag = false;//是否已登入
	public static String contentDNS;
	public static boolean AR_ON;

	public static boolean POIiconinit = false;

	//use to record touch event
	public static boolean bTouchScreen;
	public static float fTouchEventX;
	public static float fTouchEventY;


	public static String iTouchPOIID;
	public static String iTouchPOIInfo;
	public static String iTouchPOIName;
	public static int POI_index;
	public static String iTouchPOILat;
	public static String iTouchPOILog;

	public static String iFocusPOIName;
	public static String iFocusPOIInfo;
	public static String SetPOItype;
	public static boolean HaveNavi;
	public static float typeButtonhigh = 0;
	////////////////////////////////////



	public static boolean temp;


	public static int iLanguage;



	public static int setType = 999;
	public static String lastPOItype;

}

