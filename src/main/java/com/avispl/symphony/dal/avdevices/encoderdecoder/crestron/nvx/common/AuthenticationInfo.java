/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.avdevices.encoderdecoder.crestron.nvx.common;

/**
 * AuthenticationInfo represents information used to validation
 *
 * @author Kevin / Symphony Dev Team<br>
 * Created on 5/3/2024
 * @since 1.0.0
 */
public class AuthenticationInfo {
	private String trackId;
	private String origin;
	private String referer;
	private String token;
	private String authenticationCookies;
	private String authorizeStatus = CrestronConstant.UNAUTHORIZED;

	/**
	 * Default constructor
	 */
	public AuthenticationInfo()  {

	}

	/**
	 * Create an instance of {@link AuthenticationInfo}
	 */
	public AuthenticationInfo(String trackId, String origin, String referer) {
		this.trackId = trackId;
		this.origin =	origin;
		this.referer = referer;
	}

	/**
	 * Retrieves {@link #trackId}
	 *
	 * @return value of {@link #trackId}
	 */
	public String getTrackId() {
		return trackId;
	}

	/**
	 * Sets {@link #trackId} value
	 *
	 * @param trackId new value of {@link #trackId}
	 */
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	/**
	 * Retrieves {@link #origin}
	 *
	 * @return value of {@link #origin}
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * Sets {@link #origin} value
	 *
	 * @param origin new value of {@link #origin}
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * Retrieves {@link #referer}
	 *
	 * @return value of {@link #referer}
	 */
	public String getReferer() {
		return referer;
	}

	/**
	 * Sets {@link #referer} value
	 *
	 * @param referer new value of {@link #referer}
	 */
	public void setReferer(String referer) {
		this.referer = referer;
	}

	/**
	 * Retrieves {@link #token}
	 *
	 * @return value of {@link #token}
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Sets {@link #token} value
	 *
	 * @param token new value of {@link #token}
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Retrieves {@link #authenticationCookies}
	 *
	 * @return value of {@link #authenticationCookies}
	 */
	public String getAuthenticationCookies() {
		return authenticationCookies;
	}

	/**
	 * Sets {@link #authenticationCookies} value
	 *
	 * @param authenticationCookies new value of {@link #authenticationCookies}
	 */
	public void setAuthenticationCookies(String authenticationCookies) {
		this.authenticationCookies = authenticationCookies;
	}

	/**
	 * Retrieves {@link #authorizeStatus}
	 *
	 * @return value of {@link #authorizeStatus}
	 */
	public String getAuthorizeStatus() {
		return authorizeStatus;
	}

	/**
	 * Sets {@link #authorizeStatus} value
	 *
	 * @param authorizeStatus new value of {@link #authorizeStatus}
	 */
	public void setAuthorizeStatus(String authorizeStatus) {
		this.authorizeStatus = authorizeStatus;
	}
}
