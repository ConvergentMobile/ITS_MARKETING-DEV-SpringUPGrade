package common;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.boot.registry.*;
import org. hibernate.boot.*;

import org.hibernate.internal.SessionImpl;
import java.sql.Connection;

public class HibernateUtil {
	private static Logger logger = Logger.getLogger(HibernateUtil.class);

	private static final SessionFactory sessionFactory;

	static {
		try {
			// Create the SessionFactory
			/*sessionFactory = new Configuration().configure()
					.buildSessionFactory(); */
                        
                        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
	Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
	sessionFactory = metaData.getSessionFactoryBuilder().build();
		} catch (Throwable ex) {
			logger.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static final ThreadLocal<Session> session = new ThreadLocal<Session>();

	/*
	 * public static Session currentSession() throws HibernateException {
	 * Session s = session.get(); // Open a new Session, if this Thread has none
	 * yet if (s == null) { s = sessionFactory.openSession(); session.set(s); }
	 * return s; }
	 */

	public static Session currentSession() throws HibernateException {
		int connectionTries = 5;

		do {
			try {
				Session s = session.get();
				// Open a new Session, if this Thread has none yet
				if (s == null) {
					s = sessionFactory.openSession();
					session.set(s);
				}

				final Transaction transaction = s.beginTransaction();
				s.createNativeQuery("select 1").list();
				transaction.commit();

				return s;
			} catch (JDBCException se) {
				if (se.getSQLState() == null
						|| se.getSQLState().equals("08S01")) {
					logger.error("HibernateUtil: reconnecting ...");
					// Use the reconnect method from the same class
					try {
						reconnect();
					} catch (Exception he) {
						// We have exception establishing connection again
						// Just allow it to go through 5 attempts
					}
				}
			} catch (Exception e) {
				logger.error("HibernateUtil: error here - " + e);
			}
			connectionTries = connectionTries - 1;
		} while (connectionTries > 0);

		return null;
	}

	public static void reconnect() throws Exception {
		Session s = session.get();
		session.set(null);
		s = sessionFactory.openSession();
		//s.connection().setAutoCommit(true);
		session.set(s);
	}

	public static void closeSession() throws HibernateException {
		Session s = session.get();
		session.set(null);
		if (s != null)
			s.close();
	}
        
        public static Connection getConnection(Session session)
        {
             SessionImpl sessionImpl = (SessionImpl) session; 
                       Connection conn = sessionImpl.connection(); 
                       return conn;
        }
}