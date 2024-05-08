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
	INTERNATIONAL_DATE_LINE_WEST("000", "(UTC-12:00) International Date Line West"),
	COORDINATED_UNIVERSAL_TIME_11("002", "(UTC-11:00) Coordinated Universal Time -11"),
	ALEUTIAN_ISLANDS("147", "(UTC-10:00) Aleutian Islands"),
	HAWAII("003", "(UTC-10:00) Hawaii"),
	TAHITI("112", "(UTC-10:00) Tahiti"),
	MARQUESAS_ISLANDS("100", "(UTC-09:30) Marquesas Islands"),
	ALASKA("004", "(UTC-09:00) Alaska"),
	COORDINATED_UNIVERSAL_TIME_09("151", "(UTC-09:00) Coordinated Universal Time-09"),
	GAMBIER("101", "(UTC-09:00) Gambier"),
	PACIFIC_TIME("005", "(UTC-08:00) Pacific Time (US & Canada)"),
	BAJA_CALIFORNIA("006", "(UTC-08:00) Baja California"),
	COORDINATED_UNIVERSAL_TIME_08("150", "(UTC-08:00) Coordinated Universal Time-08"),
	PITCAIRN_ISLANDS("99", "(UTC-08:00) Pitcairn Islands"),
	MOUNTAIN_TIME("007", "(UTC-07:00) Mountain Time (US & Canada)"),
	CHIHUAHUA_LA_PAZ_MAZATLAN("009", "(UTC-07:00) Chihuahua, La Paz, Mazatlan"),
	ARIZONA("008", "(UTC-07:00) Arizona"),
	SASKATCHEWAN("011", "(UTC-06:00) Saskatchewan"),
	CENTRAL_AMERICA("012", "(UTC-06:00) Central America"),
	CENTRAL_TIME("010", "(UTC-06:00) Central Time (US & Canada)"),
	GUADALAJARA_MEXICO_CITY_MONTERREY("013", "(UTC-06:00) Guadalajara, Mexico City, Monterrey"),
	EASTER_ISLAND("102", "(UTC-06:00) Easter Island"),
	HAVANA("143", "(UTC-05:00) Havana"),
	EASTERN_TIME("014", "(UTC-05:00) Eastern Time (US & Canada)"),
	CHETUMAL("127", "(UTC-05:00) Chetumal"),
	HAITI("142", "(UTC-05:00) Haiti"),
	BOGOTA_LIMA_QUITO_RIO_BRANCO("016", "(UTC-05:00) Bogota, Lima, Quito, Rio Branco"),
	TURKS_AND_CAICOS("152", "(UTC-05:00) Turks and Caicos"),
	INDIANA_EAST("015", "(UTC-05:00) Indiana (East)"),
	ATLANTIC_TIME("018", "(UTC-04:00) Atlantic Time (Canada)"),
	CUIABA("021", "(UTC-04:00) Cuiaba"),
	SANTIAGO("020", "(UTC-04:00) Santiago"),
	ASUNCION("022", "(UTC-04:00) Asuncion"),
	GEORGETOWN_LA_PAZ_MANAUS_SAN_JUAN("019", "(UTC-04:00) Georgetown, La Paz, Manaus, San Juan"),
	CARACAS("017", "(UTC-04:00) Caracas"),
	NEWFOUNDLAND("023", "(UTC-03:30) Newfoundland"),
	BUENOS_AIRES("027", "(UTC-03:00) Buenos Aires"),
	SALVADOR("111", "(UTC-03:00) Salvador"),
	BRASILIA("024", "(UTC-03:00) Brasilia"),
	GREENLAND("026", "(UTC-03:00) Greenland"),
	PUNTA_ARENAS("156", "(UTC-03:00) Punta Arenas"),
	MONTEVIDEO("028", "(UTC-03:00) Montevideo"),
	CAYENNE_FORTALEZA("025", "(UTC-03:00) Cayenne, Fortaleza"),
	SAINT_PIERRE_AND_MIQUELON("138", "(UTC-03:00) Saint Pierre and Miquelon"),
	ARAGUAINA("136", "(UTC-03:00) Araguaina"),
	MID_ATLANTIC("029", "(UTC-02:00) Mid-Atlantic"),
	COORDINATED_UNIVERSAL_TIME_02("032", "(UTC-02:00) Coordinated Universal Time -02"),
	AZORES("031", "(UTC-01:00) Azores"),
	CABO_VERDE_IS("128", "(UTC-01:00) Cabo Verde Is."),
	COORDINATED_UNIVERSAL_TIME("036", "(UTC) Coordinated Universal Time"),
	DUBLIN_EDINBURGH_LISBON_LONDON("033", "(UTC+00:00) Dublin, Edinburgh, Lisbon, London"),
	MONROVIA_REYKJAVIK("034", "(UTC+00:00) Monrovia, Reykjavik"),
	CASABLANCA("035", "(UTC+00:00) Casablanca"),
	BELGRADE_BRATISLAVA_BUDAPEST_LJUBLJANA_PRAGUE("037", "(UTC+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague"),
	SARAJEVO_SKOPJE_WARSAW_ZAGREB("038", "(UTC+01:00) Sarajevo, Skopje, Warsaw, Zagreb"),
	BRUSSELS_COPENHAGEN_MADRID_PARIS("039", "(UTC+01:00) Brussels, Copenhagen, Madrid, Paris"),
	SAO_TOME("155", "(UTC+01:00) Sao Tome"),
	WEST_CENTRAL_AFRICA("041", "(UTC+01:00) West Central Africa"),
	AMSTERDAM_BERLIN_BERN_ROME_STOCKHOLM_VIENNA("040", "(UTC+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"),
	CHISINAU("043", "(UTC+02:00) Chisinau"),
	CAIRO("044", "(UTC+02:00) Cairo"),
	HELSINKI_KYIV_RIGA_SOFIA_TALLINN_VILNIUS("045", "(UTC+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius"),
	ATHENS_BUCHAREST("046", "(UTC+02:00) Athens, Bucharest"),
	JERUSALEM("048", "(UTC+02:00) Jerusalem"),
	AMMAN("049", "(UTC+02:00) Amman"),
	TRIPOLI("113", "(UTC+02:00) Tripoli"),
	BEIRUT("050", "(UTC+02:00) Beirut"),
	WINDHOEK("042", "(UTC+02:00) Windhoek"),
	KALININGRAD("121", "(UTC+02:00) Kaliningrad"),
	HARARE_PRETORIA("047", "(UTC+02:00) Harare, Pretoria"),
	KHARTOUM("153", "(UTC+02:00) Khartoum"),
	DAMASCUS("051", "(UTC+02:00) Damascus"),
	GAZA_HEBRON("133", "(UTC+02:00) Gaza, Hebron"),
	KUWAIT_RIYADH("054", "(UTC+03:00) Kuwait, Riyadh"),
	BAGHDAD("056", "(UTC+03:00) Baghdad"),
	MINSK("114", "(UTC+03:00) Minsk"),
	NAIROBI("055", "(UTC+03:00) Nairobi"),
	MOSCOW_ST_PETERSBURG_VOLGOGRAD("117", "(UTC+03:00) Moscow, St. Petersburg, Volgograd"),
	ISTANBUL("052", "(UTC+03:00) Istanbul"),
	PYONGYANG("149", "(UTC+09:00) Pyongyang"),
	YAKUTSK("115", "(UTC+09:00) Yakutsk"),
	OSAKA_SAPPORO_TOKYO("084", "(UTC+09:00) Osaka, Sapporo, Tokyo"),
	CHITA("131", "(UTC+09:00) Chita"),
	DARWIN("086", "(UTC+09:30) Darwin"),
	ADELAIDE("087", "(UTC+09:30) Adelaide"),
	CANBERRA_MELBOURNE_SYDNEY("088", "(UTC+10:00) Canberra, Melbourne, Sydney"),
	BRISBANE("089", "(UTC+10:00) Brisbane"),
	VLADIVOSTOK("116", "(UTC+10:00) Vladivostok"),
	HOBART("090", "(UTC+10:00) Hobart"),
	GUAM_PORT_MORESBY("092", "(UTC+10:00) Guam, Port Moresby"),
	LORD_HOWE_ISLAND("141", "(UTC+10:30) Lord Howe Island"),
	BOUGAINVILLE_ISLAND("145", "(UTC+11:00) Bougainville Island"),
	SOLOMON_IS_NEW_CALEDONIA("093", "(UTC+11:00) Solomon Is., New Caledonia"),
	MAGADAN("094", "(UTC+11:00) Magadan"),
	NORFOLK_ISLAND("139", "(UTC+11:00) Norfolk Island"),
	CHOKURDAKH("125", "(UTC+11:00) Chokurdakh"),
	SAKHALIN("137", "(UTC+11:00) Sakhalin"),
	FIJI("095", "(UTC+12:00) Fiji"),
	AUCKLAND_WELLINGTON("096", "(UTC+12:00) Auckland, Wellington"),
	ANADYR_PETROPAVLOVSK_KAMCHATSKY("126", "(UTC+12:00) Anadyr, Petropavlovsk-Kamchatsky"),
	COORDINATED_UNIVERSAL_TIME_PLUS_12("097", "(UTC+12:00) Coordinated Universal Time +12"),
	CHATHAM_ISLANDS("144", "(UTC+12:45) Chatham Islands"),
	SAMOA("001", "(UTC+13:00) Samoa"),
	NUKUALOFA("098", "(UTC+13:00) Nuku'alofa"),
	COORDINATED_UNIVERSAL_TIME_PLUS_13("157", "(UTC+13:00) Coordinated Universal Time+13"),
	KIRITIMATI_ISLAND("123", "(UTC+14:00) Kiritimati Island");

	private final String value;
	private final String name;

	TimeZone(String value, String name) {
		this.name = name;
		this.value = value;
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
	 * Get specific timezone by given value
	 */
	public static TimeZone getEnumByValue(String time) {
		return Arrays.stream(values())
				.filter(item -> item.getValue().equals(time))
				.findFirst()
				.orElse(null);
	}
}