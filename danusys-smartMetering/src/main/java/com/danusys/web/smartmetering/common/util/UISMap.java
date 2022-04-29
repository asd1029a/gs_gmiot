package com.danusys.web.smartmetering.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UISMap extends UISMapAdapter {
	
	private static final Log logger = LogFactory.getLog(UISMapList.class);
    
    public UISMap() {
        super();
    }
    public UISMap(String name) {
        this.name = name;
    }
    
    public UISMap(int initialCapacity) {
        super(initialCapacity);
    } 
    
    public UISMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    } 
    
    public UISMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    } 

    public UISMap(@SuppressWarnings("rawtypes") Map map) {
        super(map);
    } 
    
    public void set(Object key, Object val) {
        super.put(key, val);
    } 
    
    public Object get(Object key) {
        
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
                return "";  
            } else {
                return null;
            } 
        } else {
            return obj;
        } 
        
    } 
        
    public void setString(Object key, String val) {
        super.put(key, val);
    }   
    public void setString2(Object key, String val) {
    	super.put(key, val);
    }   
     
    public String getString(Object key) {
        
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
            	return "";
            } else {
                return null;
            } 
            
        } else {
           // boolean b = obj instanceof java.sql.Clob;
            
            if(obj instanceof java.sql.Clob){
//                System.out.println("=========================================================");
//                System.out.println("UISMap.getString obj instanceof java.sql.Clob"+b+"/"+key);
//                System.out.println("=========================================================");
                
                return clobToString((java.sql.Clob)obj);
            } else {
                return obj.toString();
            }
        } 
    }  
    public String getString2(Object key) {
    	
    	Object obj = super.get(key);
    	
    	if (obj == null) {
    		/*
            if (this.isDefaultInit) {
               
            } else {
                return null;
            } 
    		 */
    		return "";
    	} else {
    		// boolean b = obj instanceof java.sql.Clob;
    		
    		if(obj instanceof java.sql.Clob){
//                System.out.println("=========================================================");
//                System.out.println("UISMap.getString obj instanceof java.sql.Clob"+b+"/"+key);
//                System.out.println("=========================================================");
    			
    			return clobToString((java.sql.Clob)obj);
    		} else {
    			return obj.toString();
    		}
    	} 
    }  

    public void setBoolean(Object key, boolean val) {
        super.put(key, new Boolean(val));
    } 

    public boolean getBoolean(Object key) {
        
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
                return false;
            } else {
                throw new RuntimeException("Key(" + key +") does not exist in " + this.name + " UISMap or Key(" + key +")'s val is null");
            } 
        } else {
            if (obj.getClass().isInstance(new Boolean(true))) {
                return ((Boolean)obj).booleanValue();
            } 
            
            if (obj.getClass().isInstance(new String())) {
                try {
                    return Boolean.valueOf(obj.toString()).booleanValue();
                } catch (Exception e) {
                    throw new RuntimeException("val Type(boolean) does not match : It's type is not boolean.");
                } 
            } 
            throw new RuntimeException("val Type(boolean) does not match : It's type is not boolean.");
        } 
    }   
     
    public void setShort(Object key, short val) {
        super.put(key, new Short(val));
    }  
  
    public short getShort(Object key) {
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
                return 0;
            } else {
                throw new RuntimeException("Key(" + key +") does not exist in " + this.name + " UISMap or Key(" + key +")'s val is null");
            } 
        } else {
            
            Class classType = obj.getClass();
            
            if (classType == Short.class) {
                return ((Short)obj).shortValue();
            } 

            if (classType == String.class) {
                try {
                    return Short.parseShort(obj.toString());
                } catch (Exception e) {
                    throw new RuntimeException("val Type(short) does not match : It's type is not short.");
                } 
            } 
            throw new RuntimeException("val Type(short) does not match : It's type is not short.");
        } 
    }       

    public void setInt(Object key, int val) {
        super.put(key, new Integer(val));
    } 

    public int getInt(Object key) {
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
                return 0;
            } else {
                throw new RuntimeException("Key(" + key +") does not exist in " + this.name + " UISMap or Key(" + key +")'s val is null");
            } 
        } else {
            Class classType = obj.getClass() ;                
            if (classType == Integer.class) {
                return ((Integer)obj).intValue();
            } else if (classType == Short.class) {
                return ((Short)obj).shortValue();
            } else if (classType == Long.class) {
                // JDBC_SUPPORT RowMapper와 관련하여 추가
                return ((Long)obj).intValue();
            }

            if (classType == String.class || 
                    classType == BigDecimal.class) {
                
                // JDBC_SUPPORT RowMapper와 관련하여 추가
                
                try {
                    return Integer.parseInt(obj.toString());
                } catch (Exception e) {    
                    throw new RuntimeException("val Type(int) does not match : It's type is not int.");                       
                } 
            } 
                                                       
            throw new RuntimeException("val Type(int) does not match : It's type is not int.");
        } 
    } 

    public void setLong(Object key, long val) {
        super.put(key, new Long(val));
    } 
     
    public long getLong(Object key) {
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
                return 0;
            } else {
                throw new RuntimeException("Key(" + key +") does not exist in " + this.name + " UISMap or Key(" + key +")'s val is null");
            } 
        } else {
            Class classType = obj.getClass();
            
            if (classType == Long.class) {
                return ((Long)obj).longValue();
            } else if (classType == Integer.class) {
                return ((Integer)obj).intValue();
            } else if (classType == Short.class) {
                return ((Short)obj).shortValue();
            }           
            
            if (classType == String.class || classType == BigDecimal.class) {
                try {
                    return Long.parseLong(obj.toString());
                } catch (Exception e) {  
                    throw new RuntimeException("val Type(long) does not match : It's type is not long.");                     
                } 
            }               
                                                      
            throw new RuntimeException("val Type(long) does not match : It's type is not long.");
        } 
    } 
     
    public void setFloat(Object key, float val) {
        super.put(key, new Float(val));
    } 
     
    public float getFloat(Object key) {
        Object obj = super.get(key);
    
        if (obj == null) {
            if (this.isDefaultInit) {
                return (float)0.0;
            } else {
                throw new RuntimeException("Key(" + key +") does not exist in " + this.name + " UISMap or Key(" + key +")'s val is null");
            } 
        } else {
            
            Class classType = obj.getClass() ;
            
            if (classType == Float.class ) {
                return ((Float)obj).floatValue();
            } 
            
            if (classType == String.class || classType == BigDecimal.class) {
                try {
                    return Float.parseFloat(obj.toString());
                } catch (Exception e) {
                    throw new RuntimeException("val Type(float) does not match : It's type is not float.");                       
                } 
            }               
                                                        
            throw new RuntimeException("val Type(float) does not match : It's type is not float.");
        } 
    }       
     
    public void setDouble(Object key, double val) {
        Double dou = new Double(val);
        super.put(key, dou);
    } 
    
    public double getDouble(Object key) {
        Object obj = super.get(key);
        
        if (obj == null) {
            if (this.isDefaultInit) {
                return 0.0;
            } else {
                throw new RuntimeException(
                        "Key("+ key +") does not exist in " + this.name + " UISMap or Key(" + key +")'s val is null");
            } 
        } else {
            Class cls = obj.getClass() ;
            
            if (cls == Double.class) {
                return ((Double)obj).doubleValue();
            } else if (cls == Float.class) {
                return ((Float)obj).floatValue();
            } 
            
            if (cls == String.class || cls == BigDecimal.class) {
                try {
                    return Double.parseDouble(obj.toString());
                } catch (Exception e) {                                      
                    throw new RuntimeException("val Type(double) does not match : It's type is not double.");                     
                } 
            }                                                                      
            throw new RuntimeException("val Type(double) does not match : It's type is not double.");
        } 
    }   

    public void setBigDecimal(Object key, BigDecimal val) {
        super.put(key, val);
    } 
     
    public BigDecimal getBigDecimal(Object key) {
        
        Object obj = get(key);
        if (obj == null) {
            return new BigDecimal(0);
        } else {
            return (BigDecimal)obj;
        } 
    } 
     
    public String toString() {
        
        int max = super.size() - 1;
        StringBuffer sb = new StringBuffer();

        Set keySet = super.keySet();
        Iterator keys = keySet.iterator();

        sb.append("\t--------------------["+this.getClass().getSimpleName()+" R E S U L T]---------------------");
        //sb.append("\t--------------[E F D S  R E S U L T]---------------");
        sb.append("\n\t\t   key\t\t|\t  val");
        sb.append("\n\t-------------------------------------------------");

        for (int idx=0; idx<=max; idx++) {
            Object obj = keys.next();
            if (obj == null) {
                sb.append("");
            } else {
                String val= obj.toString();
                if (val.length() < 6) {
                    sb.append("\n\t  " + obj + "\t\t\t|    " + this.getString(obj));
                } else if (val.length() < 14) {
                        sb.append("\n\t  " + obj + "\t\t|    " + this.getString(obj));
                } else if (val.length() < 22) {
                    sb.append("\n\t  " + obj + "\t|    " + this.getString(obj));
                } else {
                    sb.append("\n\t  " + obj + "|    " + this.getString(obj));
                } 
            } 
        } 
        sb.append("\n\t-------------------------------------------------");
        
        return sb.toString();
    } 
     
    public Object getKeyWithIndex(int i) {
        Set set1 = keySet();
        if (i > set1.size())
            throw new RuntimeException("[RuntimeException in UISMap(" + name + ")] keyIndex(" + i + ") is greater than key size");
        Object obj = null;
        Iterator iterator = set1.iterator();
        for(int j = 0; j <= i; j++)
            obj = iterator.next();

        return obj;
    }

    public boolean containsKey(Object obj) {
        return super.containsKey(obj);
    }

    public boolean containsValue(Object obj) {
        return super.containsValue(obj);
    }
     
    /**
     * <PRE>
     * Clob 를 String 으로 변경
     * <PRE>
     * @param clob
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public String clobToString(Clob clob)  { 
        if (clob == null) { 
            return "";
        }
        StringBuffer strOut = new StringBuffer();
        String str = "";
        int icnt = 0;
        try
        {
            BufferedReader br = new BufferedReader(clob.getCharacterStream());
            while ((str = br.readLine()) != null) { 
                if(icnt>0)strOut.append("\n");
                strOut.append(str);
                icnt++;
            }
        }
        catch(Exception e)
        {
//            e.printStackTrace();
        	logger.error(e);
        }
        return strOut.toString();
    }  
} 
