package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import mdp_common.XMLUtil;
import org.apache.log4j.Logger;

import user.User;
import user.UserDAOManager;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import common.HibernateUtil;

public class LoadUS411 {
	private static Logger logger = Logger.getLogger(LoadUS411.class);
	
	private UserDAOManager userDao;
	private static Integer categoryId = 3;
	
	public void LoadUS411() {
		
	}
	
	//this is for accessing the datasource defined in context.xml outside of a container
	private static DataSource createDataSource() {
		MysqlDataSource ds = new MysqlDataSource();
		ds.setURL("jdbc:mysql://localhost/test?autoReconnect=true");
		ds.setUser("root");
		ds.setPassword("mysqladmin");
		return ds;
	}
	
	private static void setupNamingContext(DataSource ds) throws NamingException {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		Context ctx = new InitialContext();
		Context javaCtx = ctx.createSubcontext("java");
		javaCtx.createSubcontext("comp").createSubcontext("env").createSubcontext("jdbc").bind("pooledInstab", ds);
		ctx.bind("java:", javaCtx);
	}
	
	public static void main(String[] args) {
		try {
			if (args.length < 1) {
				System.out.println("Usage: LoadUS411 <input file>");
				return;
			}
			
			setupNamingContext(createDataSource());
			new LoadUS411().doit(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doit(String infile) {
		try {
			XMLUtil xmlUtil = new XMLUtil(infile);
			NodeList nl1 = xmlUtil.getDoc().getElementsByTagName("business");
			Element el1 = (Element) nl1.item(0);
			System.out.println("====>" + xmlUtil.getValue(el1, "email"));
			
			RandomString rs = new RandomString(8);
			User user = new User();
			user.setLogin(rs.nextString());
			user.setPassword(rs.nextString());
			logger.debug("login/password: " + user.getLogin() + ", " + user.getPassword());
			user.setEmail(xmlUtil.getValue(el1, "email"));
			user.setCategoryId(categoryId);
			
			userDao = new UserDAOManager();
			userDao.saveUser(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
