package com.danusys.web.commons.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class ForecastGridTransfer {
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

    private int mode; //위경도 <-> 기상청 격자

    //결과 반환용
    class LatXLonY {
        public double originLat;
        public double originLon;
        public int gridX;
        public int gridY;
    }

    /**
     * 기상청 격자 transfer 생성자
     * @param paramX : 위도 혹은 격자x
     * @param paramY : 경도 혹은 격자y
     * @param mode : 0(to_grid) 혹은 1(to_lonlat)
     */
    public ForecastGridTransfer(double paramX, double paramY, int mode){
        if(mode==0){
            this.lat = paramX;
            this.lon = paramY;
        } else {
            this.xLat = paramX;
            this.yLon = paramY;
        }
        this.mode = mode;
    }

    /**
     * 위경도 <-> 기상청 격자
     */
    public Map<String, Object> transfer(){
        double RE = 6371.00877; //지구반경
        double GRID = 5.0; //격자간격(km)
        double SLAT1 = 30.0; //투영 위도 (degree)
        double SLAT2 = 60.0; //투영 위도
        double OLON = 126.0; //기준점 경도
        double OLAT = 38.0; //기준점 위도
        double XO = 43; //기준점 X좌표
        double YO = 136; //기준점 Y좌표

        double DEGRAD = Math.PI/ 180.0; //각a -> 라디안
        double RADDEG = 180.0 / Math.PI; //라디안a -> 각

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

        LatXLonY latXLonY = new LatXLonY();

        if(this.mode==0){ //to_grid
            latXLonY.originLat = this.lat;
            latXLonY.originLon = this.lon;

            double ra = Math.tan(Math.PI * 0.25 + (this.lat) * DEGRAD * 0.5);
                   ra = re * sf/ Math.pow(ra, sn);
            double theta = this.lon * DEGRAD - olon;
                   if(theta > Math.PI) theta -=2.0 * Math.PI;
                   if(theta < -Math.PI) theta +=2.0 * Math.PI;
                   theta *= sn;
            int x = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
            int y = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

            latXLonY.gridX = x;
            latXLonY.gridY = y;
        } else { //to_lonlat
            latXLonY.gridX = (int) this.xLat;
            latXLonY.gridY = (int) this.yLon;

            double xlat =  this.xLat;
            double ylon = this.yLon;
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
            latXLonY.originLat = alat * RADDEG;
            latXLonY.originLon = alon * RADDEG;

        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("lon", Double.toString(latXLonY.originLon));
        resultMap.put("lat", Double.toString(latXLonY.originLat));
        resultMap.put("nx", Integer.toString(latXLonY.gridX));
        resultMap.put("ny", Integer.toString(latXLonY.gridY));
        return resultMap;
    }

}
