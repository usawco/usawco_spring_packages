package usawco_grandparent.spring;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.util.JournalLogger;
// --- <<IS-END-IMPORTS>> ---

public final class admin

{
	// ---( internal utility methods )---

	final static admin _instance = new admin();

	static admin _newInstance() { return new admin(); }

	static admin _cast(Object o) { return (admin)o; }

	// ---( server methods )---




	public static final void pkgShutdown (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(pkgShutdown)>> ---
		// @sigtype java 3.5
		// DebugMsg.DEBUG_SPRING, JournalLogger.FAC_SPRING_SERVER,
		JournalLogger.logInfo(9999, 182, "usawco_spring_grandparent package  usawco_grandparent.spring.admin:pkgShutdown");
		// --- <<IS-END>> ---

                
	}



	public static final void pkgStartup (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(pkgStartup)>> ---
		// @sigtype java 3.5
		// DebugMsg.DEBUG_SPRING, JournalLogger.FAC_SPRING_SERVER,
		JournalLogger.logInfo(9999, 182, "usawco_spring_grandparent package  usawco_grandparent.spring.admin:pkgStartup");
		// --- <<IS-END>> ---

                
	}
}

