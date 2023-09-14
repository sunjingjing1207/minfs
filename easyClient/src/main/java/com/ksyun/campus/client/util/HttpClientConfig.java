package com.ksyun.campus.client.util;

public class HttpClientConfig {
	private int socketSendBufferSizeHint = 8192;
	private int socketReceiveBufferSizeHint = 8192;
	/**
	 * socket超时时间，单位毫秒
	 */
	private int socketTimeOut = 50000;
	/**
	 * 连接超时时间，单位毫秒
	 */
	private int connectionTimeOut = 50000;
	private int connectionTTL = -1;
	/**
	 *httpclient 最大连接数
	 */
	private int maxConnections = 50;
	/**
	 * httpclient 重试次数
	 */
	private int maxRetry = 2;
	public int getSocketSendBufferSizeHint() {
		return socketSendBufferSizeHint;
	}
	public void setSocketSendBufferSizeHint(int socketSendBufferSizeHint) {
		this.socketSendBufferSizeHint = socketSendBufferSizeHint;
	}
	public HttpClientConfig withSocketSendBufferSizeHint(int socketSendBufferSizeHint) {
		this.socketSendBufferSizeHint = socketSendBufferSizeHint;
		return this;
	}
	public int getSocketReceiveBufferSizeHint() {
		return socketReceiveBufferSizeHint;
	}
	public void setSocketReceiveBufferSizeHint(int socketReceiveBufferSizeHint) {
		this.socketReceiveBufferSizeHint = socketReceiveBufferSizeHint;
	}
	public HttpClientConfig withSocketReceiveBufferSizeHint(int socketReceiveBufferSizeHint){
		this.socketReceiveBufferSizeHint = socketReceiveBufferSizeHint;
		return this;
	}
	public int getSocketTimeOut() {
		return socketTimeOut;
	}
	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}
	public HttpClientConfig withSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
		return this;
	}
	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}
	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}
	public HttpClientConfig withConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
		return this;
	}
	public int getConnectionTTL() {
		return connectionTTL;
	}
	public void setConnectionTTL(int connectionTTL) {
		this.connectionTTL = connectionTTL;
	}
	public HttpClientConfig withConnectionTTL(int connectionTTL) {
		this.connectionTTL = connectionTTL;
		return this;
	}
	public int getMaxConnections() {
		return maxConnections;
	}
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	public HttpClientConfig withMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
		return this;
	}
	public int getMaxRetry() {
		return maxRetry;
	}
	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}
	public HttpClientConfig withMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
		return this;
	}
}
