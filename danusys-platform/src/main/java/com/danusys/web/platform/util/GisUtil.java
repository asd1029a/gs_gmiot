package com.danusys.web.platform.util;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.EgovStringUtil;
import com.danusys.web.platform.controller.GisRestController;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionException;
import com.jhlabs.map.proj.ProjectionFactory;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

//@Slf4j
@Component
public class GisUtil {
//	private static final Logger logger = LoggerFactory.getLogger(GisUtil.class);
	private Map<String, String[]> projectionMap;

	public GisUtil() {
		this.projectionMap = new HashMap();

		this.projectionMap.put("EPSG:5179", new String[] { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.5E", "+ellps=GRS80",
				"+units=m", "+x_0=1000000", "+y_0=2000000", "+k=0.9996" });

		this.projectionMap.put("EPSG:5186", new String[] { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.0E", "+ellps=GRS80",
				"+units=m", "+x_0=200000", "+y_0=600000", "+k=1" });

		this.projectionMap.put("EPSG:3857", new String[] { "+proj=merc", "+a=6378137", "+b=6378137", "+lat_ts=0.0",
				"+lon_0=0.0", "+x_0=0.0", "+y_0=0", "+k=1.0", "+units=m", "+nadgrids=@null", "+no_defs" });
		this.projectionMap.put("EPSG:900913", new String[] { "+proj=merc", "+a=6378137", "+b=6378137", "+lat_ts=0.0",
				"+lon_0=0.0", "+x_0=0.0", "+y_0=0", "+k=1.0", "+units=m", "+nadgrids=@null", "+no_defs" });

		this.projectionMap.put("EPSG:5181", new String[] { "+proj=tmerc", "+lat_0=38", "+lon_0=127", "+k=1",
				"+x_0=200000", "+y_0=500000", "+ellps=GRS80", "+units=m", "+no_defs" });
		this.projectionMap.put("EPSG:4326", new String[] { "+proj=longlat", "+ellps=WGS84", "+datum=WGS84", "+no_defs"});
	}

	/**
	 * geoJson 생성
	 * @param geoList
	 * @return geoJson
	 * @throws Exception
	 */
	public static String getGeoJson(List<Map<String, Object>> geoList, String id) throws Exception {
		LinkedHashMap<String,Object> geoObj = new LinkedHashMap<String,Object>();
		geoObj.put("type","FeatureCollection");
		//features
		ArrayList<Map<String, Object>> ary = new ArrayList<Map<String, Object>>();
		Integer i = 1;
		for(Map<String,Object> map : geoList) {
			CommonUtil.validMapNull(map);
			LinkedHashMap<String,Object> each = new LinkedHashMap<String,Object>();

			each.put("type", "Feature");
			each.put("id", id+i);
			i++;

			LinkedHashMap<String,Object> geom = new LinkedHashMap<String,Object>();
			geom.put("type", "Point");

			ArrayList<Double> coordinates = new ArrayList<Double>();

			coordinates.add(Double.parseDouble(CommonUtil.validOneNull(map,"longitude")));
			coordinates.add(Double.parseDouble(CommonUtil.validOneNull(map,"latitude")));

			geom.put("coordinates",coordinates);
			each.put("geometry",geom);

			each.put("geometry_name","geom");

			Map<String,Object> prop = new HashMap<String,Object>();
			for(String key : map.keySet()) {
				prop.put(key,map.get(key));
				if((key.equals("longitude"))||(key.equals("latitude"))){
					prop.put(key, Double.parseDouble(map.get(key).toString()));
				}
			}
			each.put("properties",prop);

			ary.add(each);
		}
		geoObj.put("features",ary);
		JSONObject json = new JSONObject(geoObj);

		return json.toString();
	}


	public Map<String, Object> createGeoJson(List<EgovMap> list, String lon, String lat, String featureKind) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("type", "FeatureCollection");

		List<Map<String, Object>> features = new LinkedList<Map<String, Object>>();
		
		try {
			for (EgovMap map : list) {
				Point2D.Double tp = new Point2D.Double();

				String strLon = EgovStringUtil.nullConvert(map.get(lon));
				String strLat = EgovStringUtil.nullConvert(map.get(lat));
				if ((!"".equals(strLon)) && (!"".equals(strLat))) {
					tp = convertWGS842ETM(strLon, strLat);

					List<Double> coordinates = new LinkedList<Double>();
					coordinates.add(Double.valueOf(tp.x));
					coordinates.add(Double.valueOf(tp.y));

					Map<String, Object> geometry = new HashMap<String, Object>();
					geometry.put("type", "Point");
					geometry.put("coordinates", coordinates);
					map.put("featureKind",featureKind);

					Map<String, Object> feature = new HashMap<String, Object>();
					feature.put("type", "Feature");
					feature.put("geometry", geometry);
					feature.put("properties", map);
					if("cctv".equals(featureKind)) feature.put("id", map.get("fcltId"));
					else if("event".equals(featureKind)) feature.put("id", map.get("evtOcrNo"));
					else if("fclt".equals(featureKind)) feature.put("id", map.get("fcltId"));
					
					features.add(feature);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		resultMap.put("features", features);

		return resultMap;
	}
	
	public Map<String, Object> createGeoJson2(List<EgovMap> list, String lineString ) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("type", "FeatureCollection");

		List<Map<String, Object>> features = new LinkedList<Map<String, Object>>();
		try {
			for (EgovMap map : list) {
				Point2D.Double tp = new Point2D.Double();
				String line = EgovStringUtil.nullConvert(map.get(lineString));
				String[] lineArray = line.split("\\s");
				ArrayList<List<Double>> coordinates = new ArrayList<List<Double>>();
				
				for(int i = 0; i < lineArray.length; i++) {
					List<Double> coordinates2 = new LinkedList<Double>();
					String[] lineArray2 = lineArray[i].split(",");
					String strLon = EgovStringUtil.nullConvert(lineArray2[0]);
					String strLat = EgovStringUtil.nullConvert(lineArray2[1]);
					if ((!"".equals(strLon)) && (!"".equals(strLat))) {
						tp = convertWGS842ETM(strLon, strLat);
						coordinates2.add(Double.valueOf(tp.x));
						coordinates2.add(Double.valueOf(tp.y));
					}
					coordinates.add(coordinates2);
				}
				Map<String, Object> geometry = new HashMap<String, Object>();
				geometry.put("type", "LineString");
				geometry.put("coordinates", coordinates);
				
				Map<String, Object> feature = new HashMap<String, Object>();
				feature.put("type", "Feature");
				feature.put("geometry", geometry);
				feature.put("properties", map);
				features.add(feature);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		resultMap.put("features", features);

		return resultMap;
	}
	

	public Point2D.Double convertByWgs84(String lon, String lat) {
		Point2D.Double rst = new Point2D.Double();
		try {
			Projection by = ProjectionFactory
					.fromPROJ4Specification((String[]) this.projectionMap.get(GisRestController.gisProjection));
			by.transform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		} catch (ProjectionException localProjectionException) {
		}
		return rst;
	}

	public Point2D.Double convertToWgs84(String lon, String lat) {
		Point2D.Double rst = new Point2D.Double();
		try {
			Projection to = ProjectionFactory
					.fromPROJ4Specification((String[]) this.projectionMap.get(GisRestController.gisProjection));
			to.inverseTransform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		} catch (ProjectionException localProjectionException) {
		}
		return rst;
	}

	public Point2D.Double convertWGS842UTMK(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.5E", "+ellps=GRS80", "+units=m",
				"+x_0=1000000", "+y_0=2000000", "+k=0.9996" };
		if (lon.indexOf(":") >= 0) {
			String[] slon = lon.split(":");
			String[] slat = lat.split(":");
			String[] sslon = slon[1].split("\\.");
			String[] sslat = slat[1].split("\\.");
			lon = Double.toString(Double.parseDouble(slon[0])
					+ (Integer.parseInt(sslon[0]) / 60.0D + Integer.parseInt(sslon[1]) / 3600.0D));
			lat = Double.toString(Double.parseDouble(slat[0])
					+ (Integer.parseInt(sslat[0]) / 60.0D + Integer.parseInt(sslat[1]) / 3600.0D));
		}
		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.transform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertWGS842WTMK(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.0E", "+ellps=GRS80", "+units=m", "+x_0=200000",
				"+y_0=600000", "+k=1" };
		if (lon.indexOf(":") >= 0) {
			String[] slon = lon.split(":");
			String[] slat = lat.split(":");
			String[] sslon = slon[1].split("\\.");
			String[] sslat = slat[1].split("\\.");
			lon = Double.toString(Double.parseDouble(slon[0])
					+ (Integer.parseInt(sslon[0]) / 60.0D + Integer.parseInt(sslon[1]) / 3600.0D));
			lat = Double.toString(Double.parseDouble(slat[0])
					+ (Integer.parseInt(sslat[0]) / 60.0D + Integer.parseInt(sslat[1]) / 3600.0D));
		}
		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.transform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertWGS842ETM(String lon, String lat) {
//		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.0E", "+ellps=GRS80", "+units=m", "+x_0=200000",
//				"+y_0=500000", "+k=1" };
		String[] proj4Param = this.projectionMap.get("EPSG:5181");
		if (lon.indexOf(":") >= 0) {
			String[] slon = lon.split(":");
			String[] slat = lat.split(":");
			String[] sslon = slon[1].split("\\.");
			String[] sslat = slat[1].split("\\.");
			lon = Double.toString(Double.parseDouble(slon[0])
					+ (Integer.parseInt(sslon[0]) / 60.0D + Integer.parseInt(sslon[1]) / 3600.0D));
			lat = Double.toString(Double.parseDouble(slat[0])
					+ (Integer.parseInt(sslat[0]) / 60.0D + Integer.parseInt(sslat[1]) / 3600.0D));
		}
		Point2D.Double rst = new Point2D.Double();
		Projection toETM = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toETM.transform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertWGS842UTMK(double lon, double lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.5E", "+ellps=GRS80", "+units=m",
				"+x_0=1000000", "+y_0=2000000", "+k=0.9996" };

		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.transform(new Point2D.Double(lon, lat), rst);
		return rst;
	}

	public Point2D.Double convertWGS842GoogleMercator(String lon, String lat) {
		String[] proj4Param = { "+proj=merc", "+a=6378137", "+b=6378137", "+lat_ts=0.0", "+lon_0=0.0", "+x_0=0.0",
				"+y_0=0", "+k=1.0", "+units=m", "+nadgrids=@null", "+no_defs" };
		if (lon.indexOf(":") >= 0) {
			String[] slon = lon.split(":");
			String[] slat = lat.split(":");
			String[] sslon = slon[1].split("\\.");
			String[] sslat = slat[1].split("\\.");
			lon = Double.toString(Double.parseDouble(slon[0])
					+ (Integer.parseInt(sslon[0]) / 60.0D + Integer.parseInt(sslon[1]) / 3600.0D));
			lat = Double.toString(Double.parseDouble(slat[0])
					+ (Integer.parseInt(sslat[0]) / 60.0D + Integer.parseInt(sslat[1]) / 3600.0D));
		}
		Point2D.Double rst = new Point2D.Double();
		Projection toGoogleMercator = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toGoogleMercator.transform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertBessel2KATEC(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=128E", "+ellps=bessel", "+units=m", "+x_0=400000",
				"+y_0=600000", "+k=0.9999" };

		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.transform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertUTMK2WGS84(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.5E", "+ellps=GRS80", "+units=m",
				"+x_0=1000000", "+y_0=2000000", "+k=0.9996" };

		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.inverseTransform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertWTMK2WGS84(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.0E", "+ellps=GRS80", "+units=m", "+x_0=200000",
				"+y_0=600000", "+k=1" };

		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.inverseTransform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertETM2WGS84(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=127.0E", "+ellps=GRS80", "+units=m", "+x_0=200000",
				"+y_0=500000", "+k=1" };

		Point2D.Double rst = new Point2D.Double();
		Projection toETM = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toETM.inverseTransform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertGoogleMercator2WGS84(String lon, String lat) {
		String[] proj4Param = { "+proj=merc", "+a=6378137", "+b=6378137", "+lat_ts=0.0", "+lon_0=0.0", "+x_0=0.0",
				"+y_0=0", "+k=1.0", "+units=m", "+nadgrids=@null", "+no_defs" };

		Point2D.Double rst = new Point2D.Double();
		Projection toGoogleMercator = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toGoogleMercator.inverseTransform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertKATEC2Bessel(String lon, String lat) {
		String[] proj4Param = { "+proj=tmerc", "+lat_0=38N", "+lon_0=128E", "+ellps=bessel", "+units=m", "+x_0=400000",
				"+y_0=600000", "+k=0.9999" };

		Point2D.Double rst = new Point2D.Double();
		Projection toUTMK = ProjectionFactory.fromPROJ4Specification(proj4Param);
		toUTMK.inverseTransform(new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat)), rst);
		return rst;
	}

	public Point2D.Double convertUTMK2KATEC(String lon, String lat) {
		Point2D.Double r = convertUTMK2WGS84(lon, lat);
		Point2D.Double r1 = convertEllipWGS842Bessel(r);
		Point2D.Double rst = convertBessel2KATEC(Double.toString(r1.getX()), Double.toString(r1.getY()));
		return rst;
	}

	public Point2D.Double convertKATEC2UTMK(String lon, String lat) {
		Point2D.Double r = convertKATEC2Bessel(lon, lat);
		Point2D.Double r1 = convertEllipBessel2WGS84(r);
		Point2D.Double rst = convertWGS842UTMK(Double.toString(r1.getX()), Double.toString(r1.getY()));
		return rst;
	}

	public Point2D.Double convertEllipBessel2WGS84(Point2D.Double in) {
		Ellipsoid bessel1841 = new Ellipsoid(6377397.155D, 0.0033427731799399794D);
		Ellipsoid wgs1984 = new Ellipsoid(6378137.0D, 0.0033528106647474805D);
		GisParameters params = new GisParameters(-115.8D, 474.99D, 674.11D, -1.16D, 2.31D, 1.63D, 6.43D);

		Ellip2Ellipsoid tr = new Ellip2Ellipsoid(bessel1841, wgs1984, params);
		Values3 src = new Values3(in.getY(), in.getX(), 0.0D);
		Values3 dst = new Values3();
		tr.transfom(src, dst);
		Point2D.Double rst = new Point2D.Double(dst.V2, dst.V1);
		return rst;
	}

	public Point2D.Double convertEllipWGS842Bessel(Point2D.Double in) {
		Ellipsoid bessel1841 = new Ellipsoid(6377397.155D, 0.0033427731799399794D);
		Ellipsoid wgs1984 = new Ellipsoid(6378137.0D, 0.0033528106647474805D);
		GisParameters params = new GisParameters(-115.8D, 474.99D, 674.11D, -1.16D, 2.31D, 1.63D, 6.43D);

		Ellip2Ellipsoid tr = new Ellip2Ellipsoid(bessel1841, wgs1984, params);
		Values3 src = new Values3(in.getY(), in.getX(), 0.0D);
		Values3 dst = new Values3();
		tr.reverseTransform(src, dst);
		Point2D.Double rst = new Point2D.Double(dst.V2, dst.V1);
		return rst;
	}

	public String wktTransform(Object owkt, String transform) throws IOException, SQLException {
		String wkt = (String) owkt;

		Point2D.Double tp = new Point2D.Double();
		int sp = wkt.indexOf("((");

		int ep = wkt.indexOf("))");

		String sb = wkt.substring(sp + 2, ep);
		String[] ssb = sb.split(transform.equals("wu") ? ", " : ",");

		String[] xy = new String[0];
		String a = new String();
		ArrayList<String> r = new ArrayList();
		if ((transform.equals("wu")) || (transform.equals("uw"))) {
			DecimalFormat df = new DecimalFormat(transform.equals("wu") ? ".##" : ".########");
			for (int i = 0; i < ssb.length; i++) {
				xy = ssb[i].split(" ");
				if (transform.equals("wu")) {
					tp = convertWGS842UTMK(xy[0], xy[1]);
				} else {
					if (!transform.equals("uw")) {
						break;
					}
					tp = convertUTMK2WGS84(xy[0], xy[1]);
				}
				a = df.format(tp.getX()) + " " + df.format(tp.getY());
				r.add(a);
			}
			String joined = CommonUtil.join(r, ",");

			return wkt.substring(0, sp) + "((" + joined + "))";
		}
		return wkt;
	}

	public String wktLineTransform(Object owkt, String transform) throws IOException, SQLException {
		String wkt = (String) owkt;

		Point2D.Double tp = new Point2D.Double();
		int sp = wkt.indexOf("(");

		int ep = wkt.indexOf(")");

		String sb = wkt.substring(sp + 1, ep);
		String[] ssb = sb.split(transform.equals("wu") ? ", " : ",");

		String[] xy = new String[0];
		String a = new String();
		ArrayList<String> r = new ArrayList();
		if ((transform.equals("wu")) || (transform.equals("uw"))) {
			DecimalFormat df = new DecimalFormat(transform.equals("wu") ? ".##" : ".########");
			for (int i = 0; i < ssb.length; i++) {
				xy = ssb[i].split(" ");
				if (transform.equals("wu")) {
					tp = convertWGS842UTMK(xy[0], xy[1]);
				} else {
					if (!transform.equals("uw")) {
						break;
					}
					tp = convertUTMK2WGS84(xy[0], xy[1]);
				}
				a = df.format(tp.getX()) + " " + df.format(tp.getY());
				r.add(a);
			}
			String joined = CommonUtil.join(r, ",");

			return wkt.substring(0, sp) + "(" + joined + ")";
		}
		return wkt;
	}

	public String geoJsonTransform(Object owkt, String transform) throws IOException, SQLException {
		String wkt = (String) owkt;

		Point2D.Double tp = new Point2D.Double();
		int sp = wkt.indexOf("((");

		int ep = wkt.indexOf("))");

		String sb = wkt.substring(sp + 2, ep);
		String[] ssb = sb.split(transform.equals("wu") ? ", " : ",");

		String[] xy = new String[0];
		String a = new String();
		ArrayList<String> r = new ArrayList();
		if ((transform.equals("wu")) || (transform.equals("uw"))) {
			DecimalFormat df = new DecimalFormat(transform.equals("wu") ? ".##" : ".########");
			for (int i = 0; i < ssb.length; i++) {
				xy = ssb[i].split(" ");
				if (transform.equals("wu")) {
					tp = convertWGS842UTMK(xy[0], xy[1]);
				} else {
					if (!transform.equals("uw")) {
						break;
					}
					tp = convertUTMK2WGS84(xy[0], xy[1]);
				}
				a = "[" + df.format(tp.getX()) + "," + df.format(tp.getY()) + "]";
				r.add(a);
			}
			String joined = CommonUtil.join(r, ",");

			return joined;
		}
		return wkt;
	}

	public String geoJsonTransform(double[] dd, String transform) throws IOException, SQLException {
		Point2D.Double tp = new Point2D.Double();
		String a = new String();
		ArrayList<String> r = new ArrayList();
		if ((transform.equals("wu")) || (transform.equals("uw"))) {
			DecimalFormat df = new DecimalFormat(transform.equals("wu") ? ".##" : ".########");
			for (int i = 0; i < dd.length; i += 2) {
				if (!transform.equals("wu")) {
					break;
				}
				tp = convertWGS842UTMK(dd[i], dd[(i + 1)]);

				a = "[" + df.format(tp.getX()) + "," + df.format(tp.getY()) + "]";
				r.add(a);
			}
			String joined = CommonUtil.join(r, ",");
			return joined;
		}
		return Arrays.toString(dd);
	}

	public String wktTransformPoint(Object owkt, String transform) throws IOException, SQLException {
		String wkt = (String) owkt;
		Point2D.Double tp = new Point2D.Double();
		int sp = wkt.indexOf("(");
		int ep = wkt.indexOf(")");
		String sb = wkt.substring(sp + 1, ep);

		String[] xy = new String[0];
		String a = new String();
		ArrayList<String> r = new ArrayList();
		if ((transform.equals("wu")) || (transform.equals("uw"))) {
			DecimalFormat df = new DecimalFormat(transform.equals("wu") ? ".##" : ".########");

			xy = sb.split(" ");
			if (transform.equals("wu")) {
				tp = convertWGS842UTMK(xy[0], xy[1]);
			} else if (transform.equals("uw")) {
				tp = convertUTMK2WGS84(xy[0], xy[1]);
			}
			a = df.format(tp.getX()) + " " + df.format(tp.getY());
			r.add(a);
			String joined = CommonUtil.join(r, ",");

			return wkt.substring(0, sp) + "(" + joined + ")";
		}
		return wkt;
	}

	public List<EgovMap> truncateTempNm(List<EgovMap> in, String fieldNm) {
		List<EgovMap> out = in;
		for (EgovMap itr : out) {
			itr.put(fieldNm, ((String) itr.get(fieldNm)).substring(0, 7) + "...");
		}
		return out;
	}

	public byte[] generateImage(byte[] imageContent, int maxWidth, double xyRatio) throws IOException {
		return null;
	}
}
