/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

import java.util.Arrays;

/**
 * Enum representing different types of time zone
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/5/2024
 * @since 1.0.0
 */
public enum TimeZone {
	INTERNATIONAL_DATE_LINE_WEST("000", "(UTC-12:00) International Date Line West", "-12:00"),
	UTC_MINUS_11("002", "(UTC-11:00) Coordinated Universal Time -11", "-11:00"),
	ALEUTIAN_ISLANDS("147", "(UTC-10:00) Aleutian Islands", "-10:00"),
	HAWAII("003", "(UTC-10:00) Hawaii", "-10:00"),
	TAHITI("112", "(UTC-10:00) Tahiti", "-10:00"),
	MARQUESAS_ISLANDS("100", "(UTC-09:30) Marquesas Islands", "-09:30"),
	ALASKA("004", "(UTC-09:00) Alaska", "-09:00"),
	UTC_MINUS_09("151", "(UTC-09:00) Coordinated Universal Time-09", "-09:00"),
	GAMBIER("101", "(UTC-09:00) Gambier", "-09:00"),
	PACIFIC_TIME_US_CANADA("005", "(UTC-08:00) Pacific Time (US & Canada)", "-08:00"),
	BAJA_CALIFORNIA("006", "(UTC-08:00) Baja California", "-08:00"),
	UTC_MINUS_08("150", "(UTC-08:00) Coordinated Universal Time-08", "-08:00"),
	PITCAIRN_ISLANDS("099", "(UTC-08:00) Pitcairn Islands", "-08:00"),
	MOUNTAIN_TIME_US_CANADA("007", "(UTC-07:00) Mountain Time (US & Canada)", "-07:00"),
	CHIHUAHUA_LA_PAZ_MAZATLAN("009", "(UTC-07:00) Chihuahua, La Paz, Mazatlan", "-07:00"),
	ARIZONA("008", "(UTC-07:00) Arizona", "-07:00"),
	SASKATCHEWAN("011", "(UTC-06:00) Saskatchewan", "-06:00"),
	CENTRAL_AMERICA("012", "(UTC-06:00) Central America", "-06:00"),
	CENTRAL_TIME_US_CANADA("010", "(UTC-06:00) Central Time (US & Canada)", "-06:00"),
	GUADALAJARA_MEXICO_CITY_MONTERREY("013", "(UTC-06:00) Guadalajara, Mexico City, Monterrey", "-06:00"),
	EASTER_ISLAND("102", "(UTC-06:00) Easter Island", "-06:00"),
	HAVANA("143", "(UTC-05:00) Havana", "-05:00"),
	EASTERN_TIME_US_CANADA("014", "(UTC-05:00) Eastern Time (US & Canada)", "-05:00"),
	CHETUMAL("127", "(UTC-05:00) Chetumal", "-05:00"),
	HAITI("142", "(UTC-05:00) Haiti", "-05:00"),
	BOGOTA_LIMA_QUITO_RIO_BRANCO("016", "(UTC-05:00) Bogota, Lima, Quito, Rio Branco", "-05:00"),
	TURKS_AND_CAICOS("152", "(UTC-05:00) Turks and Caicos", "-05:00"),
	INDIANA_EAST("015", "(UTC-05:00) Indiana (East)", "-05:00"),
	ATLANTIC_TIME_CANADA("018", "(UTC-04:00) Atlantic Time (Canada)", "-04:00"),
	CUIABA("021", "(UTC-04:00) Cuiaba", "-04:00"),
	SANTIAGO("020", "(UTC-04:00) Santiago", "-04:00"),
	ASUNCION("022", "(UTC-04:00) Asuncion", "-04:00"),
	GEORGETOWN_LA_PAZ_MANAUS_SAN_JUAN("019", "(UTC-04:00) Georgetown, La Paz, Manaus, San Juan", "-04:00"),
	CARACAS("017", "(UTC-04:00) Caracas", "-04:00"),
	NEWFOUNDLAND("023", "(UTC-03:30) Newfoundland", "-03:30"),
	BUENOS_AIRES("027", "(UTC-03:00) Buenos Aires", "-03:00"),
	SALVADOR("111", "(UTC-03:00) Salvador", "-03:00"),
	BRASILIA("024", "(UTC-03:00) Brasilia", "-03:00"),
	GREENLAND("026", "(UTC-03:00) Greenland", "-03:00"),
	PUNTA_ARENAS("156", "(UTC-03:00) Punta Arenas", "-03:00"),
	MONTEVIDEO("028", "(UTC-03:00) Montevideo", "-03:00"),
	CAYENNE_FORTALEZA("025", "(UTC-03:00) Cayenne, Fortaleza", "-03:00"),
	SAINT_PIERRE_AND_MIQUELON("138", "(UTC-03:00) Saint Pierre and Miquelon", "-03:00"),
	ARAGUAINA("136", "(UTC-03:00) Araguaina", "-03:00"),
	MID_ATLANTIC("029", "(UTC-02:00) Mid-Atlantic", "-02:00"),
	UTC_MINUS_02("032", "(UTC-02:00) Coordinated Universal Time -02", "-02:00"),
	AZORES("031", "(UTC-01:00) Azores", "-01:00"),
	CABO_VERDE_IS("128", "(UTC-01:00) Cabo Verde Is.", "-01:00"),
	UTC("036", "(UTC) Coordinated Universal Time", "+00:00"),
	DUBLIN_EDINBURGH_LISBON_LONDON("033", "(UTC+00:00) Dublin, Edinburgh, Lisbon, London", "+00:00"),
	MONROVIA_REYKJAVIK("034", "(UTC+00:00) Monrovia, Reykjavik", "+00:00"),
	CASABLANCA("035", "(UTC+00:00) Casablanca", "+00:00"),
	BELGRADE_BRATISLAVA_BUDAPEST_LJUBLJANA_PRAGUE("037", "(UTC+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague", "+01:00"),
	SARAJEVO_SKOPJE_WARSAW_ZAGREB("038", "(UTC+01:00) Sarajevo, Skopje, Warsaw, Zagreb", "+01:00"),
	BRUSSELS_COPENHAGEN_MADRID_PARIS("039", "(UTC+01:00) Brussels, Copenhagen, Madrid, Paris", "+01:00"),
	SAO_TOME("155", "(UTC+01:00) Sao Tome", "+01:00"),
	WEST_CENTRAL_AFRICA("041", "(UTC+01:00) West Central Africa", "+01:00"),
	AMSTERDAM_BERLIN_BERN_ROME_STOCKHOLM_VIENNA("040", "(UTC+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna", "+01:00"),
	CHISINAU("043", "(UTC+02:00) Chisinau", "+02:00"),
	CAIRO("044", "(UTC+02:00) Cairo", "+02:00"),
	HELSINKI_KYIV_RIGA_SOFIA_TALLINN_VILNIUS("045", "(UTC+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius", "+02:00"),
	ATHENS_BUCHAREST("046", "(UTC+02:00) Athens, Bucharest", "+02:00"),
	JERUSALEM("048", "(UTC+02:00) Jerusalem", "+02:00"),
	AMMAN("049", "(UTC+02:00) Amman", "+02:00"),
	TRIPOLI("113", "(UTC+02:00) Tripoli", "+02:00"),
	BEIRUT("050", "(UTC+02:00) Beirut", "+02:00"),
	WINDHOEK("042", "(UTC+02:00) Windhoek", "+02:00"),
	KALININGRAD("121", "(UTC+02:00) Kaliningrad", "+02:00"),
	HARARE_PRETORIA("047", "(UTC+02:00) Harare, Pretoria", "+02:00"),
	KHARTOUM("153", "(UTC+02:00) Khartoum", "+02:00"),
	DAMASCUS("051", "(UTC+02:00) Damascus", "+02:00"),
	GAZA_HEBRON("133", "(UTC+02:00) Gaza, Hebron", "+02:00"),
	KUWAIT_RIYADH("054", "(UTC+03:00) Kuwait, Riyadh", "+03:00"),
	BAGHDAD("056", "(UTC+03:00) Baghdad", "+03:00"),
	MINSK("114", "(UTC+03:00) Minsk", "+03:00"),
	NAIROBI("055", "(UTC+03:00) Nairobi", "+03:00"),
	MOSCOW_ST_PETERSBURG_VOLGOGRAD("117", "(UTC+03:00) Moscow, St. Petersburg, Volgograd", "+03:00"),
	ISTANBUL("052", "(UTC+03:00) Istanbul", "+03:00"),
	TEHRAN("058", "(UTC+03:30) Tehran", "+03:30"),
	ABU_DHABI_MUSCAT("060", "(UTC+04:00) Abu Dhabi, Muscat", "+04:00"),
	ASTRAKHAN_ULYANOVSK("130", "(UTC+04:00) Astrakhan, Ulyanovsk", "+04:00"),
	BAKU("062", "(UTC+04:00) Baku", "+04:00"),
	YEREVAN("061", "(UTC+04:00) Yerevan", "+04:00"),
	TBILISI("059", "(UTC+04:00) Tbilisi", "+04:00"),
	PORT_LOUIS("063", "(UTC+04:00) Port Louis", "+04:00"),
	IZHEVSK_SAMARA("132", "(UTC+04:00) Izhevsk, Samara", "+04:00"),
	SARATOV("154", "(UTC+04:00) Saratov", "+04:00"),
	KABUL("064", "(UTC+04:30) Kabul", "+04:30"),
	ISLAMABAD_KARACHI("067", "(UTC+05:00) Islamabad, Karachi", "+05:00"),
	EKATERINBURG("122", "(UTC+05:00) Ekaterinburg", "+05:00"),
	ASHGABAT_TASHKENT("066", "(UTC+05:00) Ashgabat, Tashkent", "+05:00"),
	CHENNAI_KOLKATA_MUMBAI_NEW_DELHI("068", "(UTC+05:30) Chennai, Kolkata, Mumbai, New Delhi", "+05:30"),
	SRI_JAYAWARDENEPURA("069", "(UTC+05:30) Sri Jayawardenepura", "+05:30"),
	KATHMANDU("070", "(UTC+05:45) Kathmandu", "+05:45"),
	DHAKA("073", "(UTC+06:00) Dhaka", "+06:00"),
	ASTANA("071", "(UTC+06:00) Astana", "+06:00"),
	OMSK("129", "(UTC+06:00) Omsk", "+06:00"),
	YANGON("074", "(UTC+06:30) Yangon (Rangoon)", "+06:30"),
	BARNAUL("146", "(UTC+07:00) Barnaul, Gorno-Altaysk", "+07:00"),
	NOVOSIBIRSK("140", "(UTC+07:00) Novosibirsk", "+07:00"),
	KRASNOYARSK("118", "(UTC+07:00) Krasnoyarsk", "+07:00"),
	BANGKOK_HANOI_JAKARTA("075", "(UTC+07:00) Bangkok, Hanoi, Jakarta", "+07:00"),
	TOMSK("135", "(UTC+07:00) Tomsk", "+07:00"),
	HOVD("134", "(UTC+07:00) Hovd", "+07:00"),
	BEIJING_CHONGQING_HONG_KONG_URUMQI("077", "(UTC+08:00) Beijing, Chongqing, Hong Kong, Urumqi", "+08:00"),
	KUALA_LUMPUR_SINGAPORE("081", "(UTC+08:00) Kuala Lumpur, Singapore", "+08:00"),
	IRKUTSK("119", "(UTC+08:00) Irkutsk", "+08:00"),
	SINGAPORE("105", "(UTC+08:00) Singapore", "+08:00"),
	TAIPEI("078", "(UTC+08:00) Taipei", "+08:00"),
	ULAANBAATAR("082", "(UTC+08:00) Ulaanbaatar", "+08:00"),
	PERTH("079", "(UTC+08:00) Perth", "+08:00"),
	EUCLA("148", "(UTC+08:45) Eucla", "+08:45"),
	SEOUL("083", "(UTC+09:00) Seoul", "+09:00"),
	PYONGYANG("149", "(UTC+09:00) Pyongyang", "+09:00"),
	YAKUTSK("115", "(UTC+09:00) Yakutsk", "+09:00"),
	OSAKA_SAPPORO_TOKYO("084", "(UTC+09:00) Osaka, Sapporo, Tokyo", "+09:00"),
	CHITA("131", "(UTC+09:00) Chita", "+09:00"),
	DARWIN("086", "(UTC+09:30) Darwin", "+09:30"),
	ADELAIDE("087", "(UTC+09:30) Adelaide", "+09:30"),
	CANBERRA_MELBOURNE_SYDNEY("088", "(UTC+10:00) Canberra, Melbourne, Sydney", "+10:00"),
	BRISBANE("089", "(UTC+10:00) Brisbane", "+10:00"),
	VLADIVOSTOK("116", "(UTC+10:00) Vladivostok", "+10:00"),
	HOBART("090", "(UTC+10:00) Hobart", "+10:00"),
	GUAM_PORT_MORESBY("092", "(UTC+10:00) Guam, Port Moresby", "+10:00"),
	LORD_HOWE_ISLAND("141", "(UTC+10:30) Lord Howe Island", "+10:30"),
	BOUGAINVILLE_ISLAND("145", "(UTC+11:00) Bougainville Island", "+11:00"),
	SOLOMON_IS_NEW_CALEDONIA("093", "(UTC+11:00) Solomon Is., New Caledonia", "+11:00"),
	MAGADAN("094", "(UTC+11:00) Magadan", "+11:00"),
	NORFOLK_ISLAND("139", "(UTC+11:00) Norfolk Island", "+11:00"),
	CHOKURDAKH("125", "(UTC+11:00) Chokurdakh", "+11:00"),
	SAKHALIN("137", "(UTC+11:00) Sakhalin", "+11:00"),
	FIJI("095", "(UTC+12:00) Fiji", "+12:00"),
	AUCKLAND_WELLINGTON("096", "(UTC+12:00) Auckland, Wellington", "+12:00"),
	ANADYR_PETROPAVLOVSK_KAMCHATSKY("126", "(UTC+12:00) Anadyr, Petropavlovsk-Kamchatsky", "+12:00"),
	UTC_PLUS_12("097", "(UTC+12:00) Coordinated Universal Time +12", "+12:00"),
	CHATHAM_ISLANDS("144", "(UTC+12:45) Chatham Islands", "+12:45"),
	SAMOA("001", "(UTC+13:00) Samoa", "+13:00"),
	NUKU_ALOFA("098", "(UTC+13:00) Nuku'alofa", "+13:00"),
	UTC_PLUS_13("157", "(UTC+13:00) Coordinated Universal Time+13", "+13:00"),
	KIRITIMATI_ISLAND("123", "(UTC+14:00) Kiritimati Island", "+14:00");

	private final String value;
	private final String name;
	private final String offset;

	TimeZone(String value, String name, String offset) {
		this.value = value;
		this.name = name;
		this.offset = offset;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #offset}
	 *
	 * @return value of {@link #offset}
	 */
	public String getOffset() {
		return offset;
	}

	/**
	 * Get specific timezone by given value
	 */
	public static TimeZone getEnumByValue(String time) {
		return Arrays.stream(values())
				.filter(item -> item.getValue().equals(time))
				.findFirst()
				.orElse(null);
	}
}