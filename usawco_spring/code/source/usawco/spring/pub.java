package usawco.spring;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.JDBCConnectionManager;
import com.wm.app.b2b.server.ThreadManager;
import com.wm.app.b2b.server.User;
import com.wm.app.b2b.server.UserManager;
import com.wm.app.b2b.server.stats.Statistics;
import com.wm.app.b2b.server.SessionManager;
import com.wm.util.JournalLogger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.Session;
// --- <<IS-END-IMPORTS>> ---

public final class pub

{
	// ---( internal utility methods )---

	final static pub _instance = new pub();

	static pub _newInstance() { return new pub(); }

	static pub _cast(Object o) { return (pub)o; }

	// ---( server methods )---




	public static final void CrushCPU (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(CrushCPU)>> ---
		// @sigtype java 3.5
		// [i] field:0:required count
		// [i] field:0:required duration
		IDataMap m = new IDataMap( pipeline);
		try {
			
			final AtomicInteger ai = new AtomicInteger();
			int theMaxTime = 30;
			int cores = Runtime.getRuntime().availableProcessors();
			int theMaxThreads = cores - 1;
			final int count = Math.min( theMaxThreads, m.getAsInteger("count", 1));
			final int dur = Math.min( theMaxTime, m.getAsInteger("duration", 5));
		
			if ( count <= 0) {
				throw new ServiceException("'count' must be > 0");				
			}
			if ( dur <= 0) {
				throw new ServiceException("'duration' must be > 0");				
			}
			final int duration = Math.min( theMaxTime, dur);
			System.err.printf( 
					 "Regarding inputs. %n" + 
					 "This will not run longer than %s seconds. %n " + 
					 "This server has %s cores, so it cannot use more than %s threads %n%n " +
					 "Creating %s threads to spin CPU for approximately %s seconds %n", 
							 theMaxTime, cores, theMaxThreads, count, duration);
										
			Runnable r = new Runnable() {
				
				public void run() {
					String threadName = String.format( "%s %s", 
							Thread.currentThread().getId(),
							Thread.currentThread().getName());
					
					System.err.printf("Starting %s %n",threadName); 
							
							
					long start = System.currentTimeMillis();
					long goal = start + (duration * 1000);
					try {
						long bench = 100000000;
						long a = 0;		
						boolean cont = true;
						boolean print = false;
						while ( cont) {
							a += 1;
							if ( a % bench == 0) {
								long now = System.currentTimeMillis(); 
								if ( now > goal || ai.get() > 0 ) {
									cont = false;
								} else if (!print) {
									System.err.printf("%nThread %s got to %,d in %,d ms %n",
											threadName, bench, (now - start));
									print = true;
								} else {
									System.err.print(".");
								}
								a = 0;
							}
						}
						
					} catch (Exception e) {
							System.err.printf("%d %s %s %n", 
									Thread.currentThread().getId(),
									Thread.currentThread().getName(),
									e.getMessage());												
					}
				}
			};
			long begin = System.currentTimeMillis();
			
			List<Thread> threads = new ArrayList<>();
			try {
				for ( int idx = 0; idx < count; idx++) { 
					Thread t = new Thread( r);
					t.start();
					threads.add( t);
				}
				System.err.println("Sleeping...");
				Thread.sleep( duration * 1000);
				
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt(); 
			} finally {
				ai.set(1);
				if ( threads != null) {
					System.err.printf("%n Stopping threads...%n");
					for ( Thread t : threads) {
						t.interrupt();
					}
				}
			}			
			System.err.printf("Done in %,d ms", (System.currentTimeMillis() - begin));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void CrushJDBCConnections (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(CrushJDBCConnections)>> ---
		// @sigtype java 3.5
		// [i] field:0:required count
		// [i] field:0:required duration
		int theMax = 100;
		IDataMap m = new IDataMap(pipeline);
		final String alias = "ISInternal";
		final AtomicInteger ai = new AtomicInteger();
		try {
			
			int connMax = JDBCConnectionManager.getConnPool(alias).getMaxConnections();
			final int count = m.getAsInteger( "count", 
				Math.min(theMax, connMax));
			final int duration = m.getAsInteger("duration", 5);
		
			
			if ( count <= 0) {
				throw new ServiceException("'count' must be > 0");				
			}
			if ( duration <= 0) {
				throw new ServiceException("'duration' must be > 0");				
			}		
		
			System.err.printf( 
					"Creating %,d %s JDBC connections for %,d seconds %n" + 
			         "Note pool max is: %,d %n" +
					 "This will not produce more than %,d connections. %n", 
					 count, alias, duration, connMax, theMax);
			
			
			final List<Connection> connList = new ArrayList<>();		
			
			Runnable r = new Runnable() {
				
				public void run() {
			
					try {
						for ( int idx = 0; idx < count; idx ++) {
							connList.add( JDBCConnectionManager.getConnection( alias));							
						
							if ( idx > 0 && idx % 5 == 0) {
								System.err.print(idx);
							} else {
								System.err.print(".");
							}
						}
						
						System.err.printf("%n %,d connections created.  Sleeping... %n", 
								connList.size());
						Thread.sleep( duration * 1000);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						System.err.println( "Woke up runner");
					} catch (Exception e) {
							System.err.printf("Exception creating connection. %s %n",
									e.getMessage());						
						
					} finally {
						if ( connList != null) {
							ai.set( connList.size());
							System.err.printf("Returning %,d connections back to pool. %n",
									connList.size());
							for ( Connection c : connList) {
								try {
									JDBCConnectionManager.releaseConnection( alias, c);
								} catch (Exception e) {
									System.err.printf("Exception closing connection. %s",
											e.getMessage());
								}
							}
						}
					}
				}
			};
			
			Thread t = new Thread( r);
			t.start();
			try {
				Thread.sleep( duration * 1000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				t.interrupt();
			}
			
		   System.err.printf( "Trying to create the %,d connections now. Sleeping... %n", count);
			try {
				Thread.sleep( duration * 1000);
				
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				System.err.println("Done.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void CrushMemory (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(CrushMemory)>> ---
		// @sigtype java 3.5
		// [i] field:0:required memSize
		// [i] field:0:required duration
		IDataMap m = new IDataMap(pipeline);
		Integer duration = m.getAsInteger( "duration", 5);
		
		if ( duration <= 0) {
			throw new ServiceException("'duration' must be > 0");				
		}
		
		String memSize = m.getAsString( "memSize");
		long bytes = 0L;
		if ( StringUtils.isEmpty( memSize)) {
			bytes = Runtime.getRuntime().freeMemory();
			memSize = org.apache.commons.io.FileUtils.byteCountToDisplaySize( 
					bytes);
			String[] parts = memSize.trim().split(" ");
			
			memSize = parts[0] + String.valueOf(parts[1].charAt(0));
			
		}		
		
		Pattern pRange = Pattern.compile("[0-9]{1,3}[kKmMgG]?");
		Matcher mRange = pRange.matcher(memSize);
		int interval = 0;
		if (mRange.matches()) {
		
		    try {
		        interval = Integer.parseInt(memSize.substring(0, memSize.length() - 1));
		    } catch (NumberFormatException nfe) {
		        // We already regex the expression, so this is definitely numeric.^M
		    }
		    try {
		        interval = Integer.parseInt(memSize.substring(0, memSize.length() - 1));
		        switch( memSize.substring( memSize.length()-1)) {
		            case "k":
		            case "K":
		                bytes = interval * 1024L;
		                break;
		
		            case "m":
		            case "M":
		                bytes = interval * 1024 * 1024L;
		                break;
		
		            case "g":
		            case "G":
		                bytes = interval * 1024 * 1024 * 1024L;
		                break;
		
		            default:
		        }
		    } catch (NumberFormatException nfe) {
		        // We already regex the expression, so this is definitely numeric.^M
		    }
		
		
		} else {
		    throw new IllegalArgumentException(String.format(
		            " %s is invalid.  1-3 digits and suffix ([k]ilobytes,[m]egabytes or, [g]igabytes)", memSize));
		}
		System.err.printf( "Creating a %s variable for %,d seconds %n", memSize, duration);
		StringBuilder sb = new StringBuilder();
		for ( long idx=0; idx<bytes; idx++) {
			sb.append("x");
		}
		System.err.println("Variable created. Sleeping...");
		try {
			Thread.sleep( duration * 1000);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		} finally {
			System.err.println("Done.");
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void CrushStatefulSessions (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(CrushStatefulSessions)>> ---
		// @sigtype java 3.5
		// [i] field:0:required count
		// [i] field:0:required duration
		long theMax = 5000;
		IDataMap m = new IDataMap(pipeline);
		long sessionMax = SessionManager.getStatefulSessionMax();
		long count = m.getAsLong( "count", 
				Math.min(theMax, sessionMax));
		int duration = m.getAsInteger("duration", 5);
		
		
		if ( count <= 0) {
			throw new ServiceException("'count' must be > 0");				
		}
		if ( duration <= 0) {
			throw new ServiceException("'duration' must be > 0");				
		}		
		
		try {
			System.err.printf( 
					"Creating %s stateful sessions for %,d seconds %n" + 
			         "Note watt.server.session.stateful.max is: %,d %n" +
					 "This will not produce more than %,d sessions %n", 
					 count, duration, sessionMax, theMax);
			
			
			List<Session> sessions = new ArrayList<>();
			System.err.println("Creating CrushStatefulSessions...");
			long now = System.currentTimeMillis();
			
			for ( long idx=0; idx<count; idx++) {			
				Session s = SessionManager.create().createContext( duration * 1000, 
						String.format("%,d ", idx));
				s.setStateful(true);
				Statistics.startStatefulSession();
				s.setUsingLicenseSeat(true);
				sessions.add(s);
				if ( idx % 100 == 0 ) {
					System.err.printf("%,d ", idx);
				}
			}
			System.err.printf("%,d Sessions created. Sleeping... %n", count);
			SessionManager.create().validateNumberOfStatefulSessions(true);
			try {
				Thread.sleep( duration * 1000);
				
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				if ( sessions != null) {
					System.err.printf("Killing sessions...%n");
					for ( Session s : sessions) {
						Statistics.endStatefulSession(now);
						SessionManager.create().killContext(s);
					}
				}
				System.err.println("Done.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
			
		// --- <<IS-END>> ---

                
	}



	public static final void CrushThreadPool (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(CrushThreadPool)>> ---
		// @sigtype java 3.5
		// [i] field:0:required count
		// [i] field:0:required duration
		int theMax = 5000;
		IDataMap m = new IDataMap(pipeline);
		int threadMax = ThreadManager.getPoolMax();
		int count = m.getAsInteger( "count", 
				Math.min(theMax, threadMax));
		int duration = m.getAsInteger("duration", 5);
		
		
		if ( count <= 0) {
			throw new ServiceException("'count' must be > 0");				
		}
		if ( duration <= 0) {
			throw new ServiceException("'duration' must be > 0");				
		}		
		
		try {
			System.err.printf( 
					"Creating %s service threads for %,d seconds %n" + 
			         "Note watt.server.threadPool is: %,d %n" +
					 "This will not produce more than %,d threads. %n", 
					 count, duration, threadMax, theMax);
			
			
			
			Runnable rServiceSleep = new Runnable() {
				public void run() {
					String threadName = String.format("IS service thread: %s %s", 
							Thread.currentThread().getId(), 
							Thread.currentThread().getName());
					
					try {
		
						Thread.currentThread().sleep( duration * 1000);
		
					} catch (Exception e) {
						System.err.printf("%s %s %n",
								threadName,
								e.getMessage());												
					} finally {
						// TODO, do I need to return it? I don't think so....
						System.err.printf("Returning thread: %s %n",threadName);
					}
				}
			};
						
			Runnable r = new Runnable() {
				
				public void run() {
					String threadName = String.format( "%s %s", 
							Thread.currentThread().getId(),
							Thread.currentThread().getName());
					
					System.err.printf("Starting 'CrushThreadPool thread: %s %nCreating service threads...%n",threadName);
					
					for ( long idx = 0; idx < count; idx++) {
						System.err.print(".");
						ThreadManager.getThreadManagerImpl().runTarget(rServiceSleep, duration * 1000);												 				
						if ( idx > 0 && idx % 100 == 0 ) {
							System.err.printf("%,d ", idx);
						}
					}
				}
			};
			
			Thread t = new Thread(r);
			t.start();
		
			System.err.printf("%nSleeping while %,d IS service threads are allocated... %n", count);
			try {
				Thread.sleep( duration * 1000);
				
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				System.err.println("Done.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
		
		
			
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
		JournalLogger.logInfo(9999, 182, "usawco_spring package  usawco.spring.pub:Ping  .... " + now);
		m.put("now",now);
		// --- <<IS-END>> ---

                
	}
}

