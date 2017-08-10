package com.fcott.spadger.utils.netstatus;

public interface NetChangeObserver {

	/**
	 * when network connected callback
	 */
	void onNetConnected(NetUtils.NetType type);

	/**
	 *  when network disconnected callback
	 */
	void onNetDisConnect();
}
