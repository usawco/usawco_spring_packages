package usawco_grandparent.spring;

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




	public static final void HowdyGramps (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(HowdyGramps)>> ---
		// @sigtype java 3.5
		IDataMap map = new IDataMap(pipeline);
		int x = Integer.parseInt(map.getAsString("x"));
		int y = Integer.parseInt(map.getAsString("y"));
		
		String msg = String.format(
				"%s) Hello from the grandparent. %s / %s = %s",				
				System.currentTimeMillis(), x, y, x/y);
		
		System.err.println(msg);		
		map.put("msg",msg);
			
		// --- <<IS-END>> ---

                
	}



	public static final void Ping (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(Ping)>> ---
		// @sigtype java 3.5
		// DebugMsg.DEBUG_SPRING, JournalLogger.FAC_SPRING_SERVER,
		long now = System.currentTimeMillis();
		IDataMap m = new IDataMap(pipeline);
		JournalLogger.logInfo(9999, 182, "usawco_spring_grandparent package  usawco_grandparent.spring.pub:Ping  .... " + now);
		m.put("now",now);
		// --- <<IS-END>> ---

                
	}
}

