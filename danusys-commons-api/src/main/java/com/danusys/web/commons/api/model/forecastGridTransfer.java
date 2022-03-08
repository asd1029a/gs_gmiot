package com.danusys.web.commons.api.model;

import lombok.Getter;
import lombok.Setter;

public class forecastGridTransfer {
    @Getter
    @Setter
    private double lat; //받은 위도
    @Getter
    @Setter
    private double lon; //받은 경도
    @Getter
    @Setter
    private double xLat; //변환 위도
    @Getter
    @Setter
    private double yLon; //변환 경도

    public forecastGridTransfer(){ }

    public forecastGridTransfer(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public void transfer(forecastGridTransfer fgt, int mode){
        double RE = 6371.00877; //지구반경
        double GRID = 5.0; //격자간격(km)
        double SLAT1 = 30.0; //투영 위도 (degree)
        double SLAT2 = 60.0; //투영 위도
        double OLON = 126.0; //기준점 경도
        double OLAT = 38.0; //기준점 위도
        double XO = 43; //기준점 X좌표
        double YO = 126; //기준점 Y좌표
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )

        double DEGRAD = Math.PI/ 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI* 0.25 + slat2 * 0.5) / Math.tan(Math.PI* 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI* 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI* 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        if(mode==0){
            double ra = Math.tan(Math.PI * 0.25 + (fgt.getLat()) * DEGRAD * 0.5);
            ra = re * sf/ Math.pow(ra, sn);
            double theta = fgt.getLon() * DEGRAD - olon;
            if(theta > Math.PI) theta -=2.0 * Math.PI;
            if(theta < -Math.PI) theta +=2.0 * Math.PI;
            theta *= sn;
            double x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
            double y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

            fgt.setLat(x);
            fgt.setLon(y);

        } else {
            double xlat = fgt.getXLat();
            double ylon = fgt.getYLon();
            double xn = xlat - XO;
            double yn = ro - ylon + YO;
            double ra = Math.sqrt(xn + xn + yn + yn);
            if(sn < 0.0){
                ra = -ra;
            }
            double alat = Math.pow((re * sf / ra), (1.0 / sn));
            alat = 2.0 * Math.atan(alat) - Math.PI* 0.5;

            double theta = 0.0;
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0;
            }
            else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI* 0.5;
                    if (xn < 0.0) {
                        theta = -theta;
                    }
                }
                else theta = Math.atan2(xn, yn);
            }
            double alon = theta / sn + olon;
            fgt.setLat(alat + RADDEG);
            fgt.setLon(alon + RADDEG);
        }
    }
    @Override
    public String toString() {
        return lat + " , " + lon + " , " + xLat + " , " + yLon;
    }


}
