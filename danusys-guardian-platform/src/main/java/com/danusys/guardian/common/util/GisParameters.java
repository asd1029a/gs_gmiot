package com.danusys.guardian.common.util;

public class GisParameters {

	public double tX;
	public double tY;
	public double tZ;
	public double tOmega;
	public double tPhi;
	public double tKappa;
	public double tScale;

	public GisParameters(double tX, double tY, double tZ, double tOmega, double tPhi, double tKappa, double tScale)
	  {
	    setParameters(tX, tY, tZ, tOmega, tPhi, tKappa, tScale);
	  }

	public void setParameters(double tX, double tY, double tZ, double tOmega, double tPhi, double tKappa,
			double tScale) {
		double degrad = Math.atan(1.0D) / 45.0D;

		this.tX = tX;
		this.tY = tY;
		this.tZ = tZ;
		this.tOmega = (tOmega / 3600.0D * degrad);
		this.tPhi = (tPhi / 3600.0D * degrad);
		this.tKappa = (tKappa / 3600.0D * degrad);
		this.tScale = (tScale * 1.0E-6D);
	}

	public void transform(Values3 src, Values3 dst) {
		double scale = 1.0D + this.tScale;

		dst.V1 = (src.V1 + scale * (this.tKappa * src.V2 - this.tPhi * src.V3) + this.tX);
		dst.V2 = (src.V2 + scale * (-this.tKappa * src.V1 + this.tOmega * src.V3) + this.tY);
		dst.V3 = (src.V3 + scale * (this.tPhi * src.V1 - this.tOmega * src.V2) + this.tZ);
	}

	public void reverseTransfom(Values3 src, Values3 dst) {
		double xt = (src.V1 - this.tX) * (1.0D + this.tScale);
		double yt = (src.V2 - this.tY) * (1.0D + this.tScale);
		double zt = (src.V3 - this.tZ) * (1.0D + this.tScale);

		dst.V1 = (1.0D / (1.0D + this.tScale) * (xt - this.tKappa * yt + this.tPhi * zt));
		dst.V2 = (1.0D / (1.0D + this.tScale) * (this.tKappa * xt + yt - this.tOmega * zt));
		dst.V3 = (1.0D / (1.0D + this.tScale) * (-this.tPhi * xt + this.tOmega * yt + zt));
	}
}
