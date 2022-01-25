/**
 * 관제
 */

const mntr = {
    eventHandler : () => {
        $('.mntrContainer .lnbFold').on("click", function(e){
            console.log(e);////////////////////////////////////////////////////
            $('.mntrContainer .menuFold').css('display','none');
            window.map.updateSize();
        });
    }


}



$(function() {


    //mapManager.map.updateSize();
    let map = new mapCreater('map',0);
    map.createMousePosition('mouse-position');
    map.scaleLine();
    window.map = map;

    let lc = new layerControl('map', 'title');
    window.lc = lc;
    console.log(lc.exist('cctvLayer'));

    let msT = new measureTool('map');
    window.msT = msT;
    //msT.initDraw('Line');


    //
    //let map2 = new mapCreater('map2',2);
    //map2.createMousePosition('mouse-position');
    //map2.scaleLine();
    //window.map2 = map2;

    //console.log(map);
});

