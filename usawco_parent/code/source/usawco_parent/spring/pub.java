package usawco_parent.spring;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class pub

{
	// ---( internal utility methods )---

	final static pub _instance = new pub();

	static pub _newInstance() { return new pub(); }

	static pub _cast(Object o) { return (pub)o; }

	// ---( server methods )---




	public static final void Ping (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(Ping)>> ---
		// @sigtype java 3.5
		// DebugMsg.DEBUG_SPRING, JournalLogger.FAC_SPRING_SERVER,
		long now = System.currentTimeMillis();
		IDataMap m = new IDataMap(pipeline);
		JournalLogger.logInfo(9999, 182, "usawco_spring_parent package  usawco_parent.spring.pub:Ping  .... " + now);
		m.put("now",now);
		// --- <<IS-END>> ---

                
	}
}

