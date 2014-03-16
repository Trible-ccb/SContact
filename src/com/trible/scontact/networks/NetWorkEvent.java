package com.trible.scontact.networks;
/**
 * 
 * @author yuandunlong
 *
 */
public class NetWorkEvent {
	
	private String exceptionApi;
	
	private Exception e;
	
	private String extra;
	
	private int eventType;
	
	
	public String getExceptionApi() {
		return exceptionApi;
	}


	public void setExceptionApi(String exceptionApi) {
		this.exceptionApi = exceptionApi;
	}


	public Exception getE() {
		return e;
	}


	public void setE(Exception e) {
		this.e = e;
	}


	public String getExtra() {
		return extra;
	}


	public void setExtra(String extra) {
		this.extra = extra;
	}


	public int getEventType() {
		return eventType;
	}


	public void setEventType(int eventType) {
		this.eventType = eventType;
	}


	public static int TIME_OUT_TYPE=1;

}
