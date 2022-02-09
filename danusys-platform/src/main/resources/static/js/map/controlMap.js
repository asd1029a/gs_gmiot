/**
 * 플랫폼 당 스타일 지정
 * */
const layerStyle = {
    //개소
    station : () => {
        return new ol.style.Style({
            image: new ol.style.Circle({
                radius:13,
                stroke: new ol.style.Stroke({
                    color:'rgba(255,255,255,1)',
                    width: 2,
                }),
                fill: new ol.style.Fill({
                    color: 'red',
                })
            }),
            text: new ol.style.Text({
                text: '10',
                fill: new ol.style.Fill({
                    color:'white',
                    font: '13px'
                })
            })
        });
    },
    //이벤트
    event : () => {
        return null;
    }
}
