package com.danusys.web.commons.auth.encryption;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Funcs {

	private static final char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'd', 'd', 'e', 'f' };

	public static String getkey(HashMap<String, String> m, String v){
		for(String o: m.keySet()) 
		{
			if(m.get(o).equals(v))
			{
				return o;
			} 
		}
		return null;

	}
	public static String encodingURL(String url){
		try {
			return URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//에러시 주요 문자만 인코딩
			return url.replaceAll(" ", "%20").replaceAll("//", "%2F").replaceAll("+", "%2B");
		}
	}
	public static String getMD5Str(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bf = md.digest(input.getBytes("UTF-8"));
			StringBuilder stbf = new StringBuilder();
			for( int i = 0; i < bf.length ; i++) {
				stbf.append(HEXCHAR[((bf[i]>>4) & 0x0f)]);
				stbf.append(HEXCHAR[(bf[i] & 0x0f)]);
			}
			return stbf.toString();
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String getSha512Str( String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bf = md.digest(input.getBytes());
			StringBuilder stbf = new StringBuilder();
			for( int i = 0; i < bf.length ; i++) {
				stbf.append(HEXCHAR[((bf[i]>>4) & 0x0f)]);
				stbf.append(HEXCHAR[(bf[i] & 0x0f)]);
			}
			return stbf.toString();
		} catch (NoSuchAlgorithmException e) {
		}
		return null;
	}

	public static byte[] hexToByteArray(String input) {
		if (input == null || input.length() % 2 != 0) {
			return null;
		}
		byte[] bytes = new byte[input.length() >> 1];
		for (int i = 0; i < input.length(); i += 2) {
			byte value = (byte)Integer.parseInt(input.substring(i, i + 2), 16);
			bytes[(i>>1)] = value;
		}
		return bytes;
	}

	public static final void convtCommaStr( String str , List<String> lststr) {
		if( str == null || str.length() == 0) return;

		char[] buf = str.toCharArray();
		int pt, spt, cnt, stat,len;
		pt = spt = cnt = 0;
		len = buf.length;
		char nextfind;

		try {
			while( pt < len) {
				if( buf[pt] == '"') {
					nextfind = '"';
					stat = 2;
					pt++;
					spt = pt;
				} else {
					if( buf[pt] == ',') {
						lststr.add("");
						pt++;
						if( pt == len) {
							lststr.add("");
						}
						spt = pt;
						continue;
					} else {
						nextfind = ',';
						stat = 1;
						spt = pt;
						cnt++;
						pt++;
					}
				}
				while (pt < len ) {
					if( buf[pt] != nextfind) {
						pt++; cnt++; continue;
					}
					if( stat == 1) {
						lststr.add( new String(buf, spt, cnt));
						pt++;
						if(pt == len) {
							lststr.add("");
						}
						cnt = 0;
						break;
					}
					if(stat == 2) {
						pt++;
						if( buf[pt] != nextfind) {
							lststr.add( new String(buf, spt, cnt));
							cnt = 0;
							pt++;
							if( pt == len) {
								lststr.add("");
							}
							break;
						} else {
							int i = pt;
							while( i < len) {
								buf[i-1] = buf[i];
								i++;
							}
							len--;
							cnt++;
						}
					}
				}
			}
			if( cnt > 0) 
				lststr.add( new String( buf, spt, cnt));
		}
		catch( Exception e) {
			if( cnt > 0)
				lststr.add( new String(buf, spt, cnt));
		}
	}

	public static final List<String> convtCommaStr( String str ) {
		if( str == null || str.length() == 0) return null;

		char[] buf = str.toCharArray();
		int pt, spt, cnt, stat,len;
		pt = spt = cnt = 0;
		len = buf.length;
		char nextfind;
		List<String> lststr = new ArrayList<String>();

		try {
			while( pt < len) {
				if( buf[pt] == '"') {
					nextfind = '"';
					stat = 2;
					pt++;
					spt = pt;
				} else {
					if( buf[pt] == ',') {
						lststr.add("");
						pt++;
						if( pt == len) {
							lststr.add("");
						}
						spt = pt;
						continue;
					} else {
						nextfind = ',';
						stat = 1;
						spt = pt;
						cnt++;
						pt++;
					}
				}
				while (pt < len ) {
					if( buf[pt] != nextfind) {
						pt++; cnt++; continue;
					}
					if( stat == 1) {
						lststr.add( new String(buf, spt, cnt));
						pt++;
						if(pt == len) {
							lststr.add("");
						}
						cnt = 0;
						break;
					}
					if(stat == 2) {
						pt++;
						if( buf[pt] != nextfind) {
							lststr.add( new String(buf, spt, cnt));
							cnt = 0;
							pt++;
							if( pt == len) {
								lststr.add("");
							}
							break;
						} else {
							int i = pt;
							while( i < len) {
								buf[i-1] = buf[i];
								i++;
							}
							len--;
							cnt++;
						}
					}
				}
			}
			if( cnt > 0) 
				lststr.add( new String( buf, spt, cnt));
		}
		catch( Exception e) {
			if( cnt > 0)
				lststr.add( new String(buf, spt, cnt));
		}
		return lststr;
	}

	public static final String[] commaStrToStrs( String str , int count) {
		if( str == null || str.length() == 0) return null;

		String[] arstrs = new String[count];
		int numidx = 0;
		char[] buf = str.toCharArray();
		int pt, spt, cnt, stat,len;
		pt = spt = cnt = 0;
		len = buf.length;
		char nextfind;

		try {
			while( pt < len) {
				if( buf[pt] == '"') {
					nextfind = '"';
					stat = 2;
					pt++;
					spt = pt;
				} else {
					if( buf[pt] == ',') {
						if( numidx == count) return null;
						arstrs[numidx] = "";
						numidx++;
						pt++;
						if( pt == len) {
							if( numidx == count) return null;
							arstrs[numidx] = "";
							numidx++;
						}
						spt = pt;
						continue;
					} else {
						nextfind = ',';
						stat = 1;
						spt = pt;
						cnt++;
						pt++;
					}
				}
				while (pt < len ) {
					if( buf[pt] != nextfind) {
						pt++; cnt++; continue;
					}
					if( stat == 1) {
						if( numidx == count) return null;
						arstrs[numidx] = new String(buf, spt, cnt);
						numidx++;
						pt++;
						if(pt == len) {
							if( numidx == count) return null;
							arstrs[numidx] = "";
							numidx++;
						}
						cnt = 0;
						break;
					}
					if(stat == 2) {
						pt++;
						if( buf[pt] != nextfind) {
							if( numidx == count) return null;
							arstrs[numidx] =  new String(buf, spt, cnt);
							numidx++;
							pt++;
							if(  pt == len) {
								if( numidx == count) return null;
								arstrs[numidx] = "";
								numidx++;
							}
							cnt = 0;
							break;
						} else {
							int i = pt;
							while( i < len) {
								buf[i-1] = buf[i];
								i++;
							}
							len--;
							cnt++;
						}
					}
				}
			}
			if( cnt > 0) {
				if( numidx == count) return null;
				arstrs[numidx] =  new String( buf, spt, cnt);
				numidx++;
			}
		}
		catch( Exception e) {
			if( cnt > 0) {
				if( numidx == count) return null;
				arstrs[numidx] =  new String( buf, spt, cnt);
				numidx++;
			}
		}
		if( count == numidx ) return arstrs;
		else return null;
	}

	public static Map ConverObjectToMap(Object obj){
		try {
			//Field[] fields = obj.getClass().getFields(); //private field는 나오지 않음.
			Field[] fields = obj.getClass().getDeclaredFields();
			Map resultMap = new HashMap();
			for(int i=0; i<=fields.length-1;i++){
				fields[i].setAccessible(true);
				resultMap.put(fields[i].getName(), fields[i].get(obj));
			}
			return resultMap;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String toJson(Object obj){
		ObjectMapper mapper = new ObjectMapper();
		String json = "";

		try {
			json = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public static CloseableHttpClient createAcceptSelfSignedCertificateClient()
			throws Exception {

		SSLContext sslContext = SSLContextBuilder
				.create()
				.loadTrustMaterial(new TrustSelfSignedStrategy())
				.build();

		sslContext.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
		HostnameVerifier allowAllHosts = new HostnameVerifier(){
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

		return HttpClients
				.custom()
				.setSSLSocketFactory(connectionFactory)
				.build();
	}
	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
