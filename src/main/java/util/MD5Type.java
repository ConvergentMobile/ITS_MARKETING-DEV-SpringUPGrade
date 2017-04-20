package util;

import java.io.Serializable;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Hex;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

abstract class MD5Type implements UserType {
 
    public int[] sqlTypes() {
        return new int[] { Types.VARCHAR };      
    }

    public Class returnedClass() {
        return String.class;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return (x == y) || (x != null && y != null && (x.equals(y)));
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException {
        String val = rs.getString(names[0]);
        return val != null ? val.trim() : "";
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        String passwd = (String)value;
         // generate md5sum for passwd
        try {
          MessageDigest digest = MessageDigest.getInstance("MD5");
          digest.update(passwd.getBytes());
          passwd = new String(Hex.encodeHex(digest.digest()));
        }
        catch (Exception e) {}
        
        st.setString(index, passwd);
    }

    public Object deepCopy(Object value) throws HibernateException {
        if (value == null) return null;

        return new String((String)value);
    }

    public boolean isMutable() {
        return true;
    }

	@Override
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable disassemble(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}
}
