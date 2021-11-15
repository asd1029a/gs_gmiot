package com.danusys.web.platform.common.util;

public class Values3 {
	public double V1;
	public double V2;
	public double V3;

	public Values3() {
		this.V1 = 0.0D;
		this.V2 = 0.0D;
		this.V3 = 0.0D;
	}

	public Values3(Values3 v) {
		this.V1 = v.V1;
		this.V2 = v.V2;
		this.V3 = v.V3;
	}

	public Values3(double V1, double V2, double V3) {
		this.V1 = V1;
		this.V2 = V2;
		this.V3 = V3;
	}

	public String toString() {
		return "(" + this.V1 + ", " + this.V2 + ", " + this.V3 + ")";
	}
}
