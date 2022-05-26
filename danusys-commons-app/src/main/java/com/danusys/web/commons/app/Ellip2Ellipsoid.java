package com.danusys.web.commons.app;

public class Ellip2Ellipsoid {
	private static double degrad = Math.atan(1.0D) / 45.0D;
	private Ellipsoid srcEllipse;
	private Ellipsoid dstEllipse;
	private GisParameters params;

	public Ellip2Ellipsoid(Ellipsoid srcEllipse, Ellipsoid dstEllipse, GisParameters params) {
		this.srcEllipse = srcEllipse;
		this.dstEllipse = dstEllipse;
		this.params = params;
	}

	public void transfom(Values3 src, Values3 dst) {
		Values3 in = new Values3(src);

		LatLong2Geocentric(in, dst, this.srcEllipse);
		this.params.transform(dst, in);
		Geocentric2LatLong(in, dst, this.dstEllipse);
	}

	public void reverseTransform(Values3 src, Values3 dst) {
		Values3 in = new Values3(src);

		LatLong2Geocentric(in, dst, this.dstEllipse);
		this.params.reverseTransfom(dst, in);
		Geocentric2LatLong(in, dst, this.srcEllipse);
	}

	private void LatLong2Geocentric(Values3 src, Values3 dst, Ellipsoid ellipse) {
		double f = ellipse.f;
		double sphi = src.V1 * degrad;
		double slam = src.V2 * degrad;
		double recf = 1.0D / f;
		double b = ellipse.a * (recf - 1.0D) / recf;
		double es = (ellipse.a * ellipse.a - b * b) / (ellipse.a * ellipse.a);

		double n = ellipse.a / Math.sqrt(1.0D - es * Math.sin(sphi) * Math.sin(sphi));

		dst.V1 = ((n + src.V3) * Math.cos(sphi) * Math.cos(slam));
		dst.V2 = ((n + src.V3) * Math.cos(sphi) * Math.sin(slam));
		dst.V3 = ((b * b / (ellipse.a * ellipse.a) * n + src.V3) * Math.sin(sphi));
	}

	private void Geocentric2LatLong(Values3 src, Values3 dst, Ellipsoid ellipse) {
		double f = ellipse.f;
		double recf = 1.0D / f;
		double b = ellipse.a * (recf - 1.0D) / recf;
		double es = (ellipse.a * ellipse.a - b * b) / (ellipse.a * ellipse.a);

		double slam = Math.atan(src.V2 / src.V1);
		double p = Math.sqrt(src.V1 * src.V1 + src.V2 * src.V2);

		double n = ellipse.a;
		dst.V3 = 0.0D;
		double sphiold = 0.0D;
		int icount = 0;
		double sphinew;
		do {
			icount++;
			double t1 = Math.pow(b * b / (ellipse.a * ellipse.a) * n + dst.V3, 2.0D) - src.V3 * src.V3;
			t1 = src.V3 / Math.sqrt(t1);
			sphinew = Math.atan(t1);
			if (Math.abs(sphinew - sphiold) < 1.0E-18D) {
				break;
			}
			n = ellipse.a / Math.sqrt(1.0D - es * Math.pow(Math.sin(sphinew), 2.0D));
			dst.V3 = (p / Math.cos(sphinew) - n);
			sphiold = sphinew;
		} while (

		icount < 300);
		dst.V1 = (sphinew / degrad);
		dst.V2 = (slam / degrad);
		if (src.V1 < 0.0D) {
			dst.V2 = (180.0D + dst.V2);
		}
		if (dst.V2 < 0.0D) {
			dst.V2 = (360.0D + dst.V2);
		}
	}
}
