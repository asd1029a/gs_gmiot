/**
 * 관제
 */

const mntr = {
    init : () => {
        //mapManager.map.updateSize();
        let map = new mapCreater('map',0);
        map.createMousePosition('mouse-position');
        map.scaleLine();
        window.map = map;

        let lc = new layerControl('map', 'title');
        window.lc = lc;

        let measure = new measureTool('map');
        window.measure = measure;
        //msT.initDraw('Line');

    },
    eventHandler : () => {
        //LNM FOLD
        $('.mntrContainer .lnbFold').on("click", function(e){
            $('.mntrContainer .menuFold').hide();
            window.map.updateSize();
        });
        //LNM SWITCH
        $('.mntrContainer .lnb ul li').on("click", function(e){
            const theme = $(e.currentTarget).attr('data-value');
            $('.mntrContainer .menuFold').hide();
            $('.mntrContainer .menuFold#'+theme).show();
            window.map.updateSize();
        });
        //LNM TAB SWITCH
        $('.mntrContainer .menuFold .tab li').on("click", function(e){
            const tab = $(e.currentTarget).attr('data-value');
            $('.menuFold .lnbTabSection').hide();
            $('div#'+tab).show();
        });
        //LNM TAB SEARCH DETAIL
        $('.detailBtn').on("click", function(e){
            const form = $(e.currentTarget).parents('.lnbTabSection').find('.searchFold');
            if(form.is(':visible')) {
                form.hide();
            } else {
                form.show();
            }
        });
        //RNM CLOSER
        $('.rnmCloser').on("click", function(e){
            $('.areaInfo').hide();
            window.map.updateSize();
        });
        //MAP TOOL
        $('.mapOptions li').on("click", function(e){
            const type = $(e.currentTarget).attr('data-value');
            switch(type) {
                case "plus" : window.map.zoomInOut('plus'); break;
                case "minus" : window.map.zoomInOut('minus'); break;
                case "distance" : window.measure.initDraw('LineString'); break;
                case "measure" : window.measure.initDraw('Polygon'); break;
                case "radius" : window.measure.initDraw('Circle'); break;
                case "eraser" : window.measure.removeMeasureTool(); break;
                /////////////////////////////
                default:
            }
        });
        //TOOL TIP
        //console.log("aaaaaaa");
        //$('.mapOptions li').tooltip();


    }

}
