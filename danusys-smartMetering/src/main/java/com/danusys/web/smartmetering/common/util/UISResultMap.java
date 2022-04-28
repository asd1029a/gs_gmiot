package com.danusys.web.smartmetering.common.util;

import java.io.Serializable;

public class UISResultMap  extends UISMap implements Serializable {
    
	public UISResultMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    public UISResultMap(int initialCapacity) {
        super(initialCapacity);
    }
    
    public UISResultMap() {
        super();
    }
    
    public UISResultMap(
            int initialCapacity,
            float loadFactor,
            boolean accessOrder) {
            super(initialCapacity, loadFactor, accessOrder);
    }
    
    /**
     * key 에 대하여 Camel Case 변환하여 super.put 을 호출한다.
     * @author     : scs
     * @param key - '_' 가 포함된 변수명
     * @param value - 명시된 key 에 대한 값 (변경 없음)
     * @return previous value associated with specified key, or null if there was no mapping for key
     */
    @Override
    public Object put(Object key, Object value) {
    	//return super.put(CamelUtil.convert2CamelCase((String)key), value); //오히려 성능이 더 안좋음
    	Object resultVal = value;
    	
    	try {
    		if(value!=null) {
    			resultVal = value.toString();
    		}
		} catch (Exception e) {
			resultVal = value;
		}
    	return super.put(camelize((String)key), resultVal);
    }
    
    /**
     * key 에 대하여 super.put 을 호출한다. CamelCase 변환을 하지 않는 순수 put method
     * @author     : scs
     * @param key : Map key
     * @param value : Map value
     * @return previous value associated with specified key, or null if there was no mapping for key
     */
    public Object put2(Object key, Object value) {
    	return super.put(key, value);
    }   
    
    /**
     *  추가 12.10.25 y.j.heon from ef.base.util.StringUtil
     */
    public  String camelize(String s, String splitPattern) {
        //  if (!(hasText(s))) {
          if ( (s == null)||("".equals(s)) ) {
              return s;
          }

          String[] parts = s.split(splitPattern);
          
          if (parts.length == 1) {
              return parts[0].toLowerCase();
          }

          StringBuffer camelized = new StringBuffer();

          for (int i = 0; i < parts.length; ++i) {
        	  
            //  if (!(hasText(parts[i]))) {
              if ((parts[i] == null)||(parts[i].equals(""))) {
                  continue;
              }
              camelized.append(Character.toUpperCase(parts[i].charAt(0)));
              camelized.append(parts[i].substring(1).toLowerCase());
          }

          camelized.setCharAt(0, Character.toLowerCase(camelized.charAt(0)));

          return camelized.toString();
          
      }

      public  String camelize(String s) {
        return camelize(s, "_");
      }
}
