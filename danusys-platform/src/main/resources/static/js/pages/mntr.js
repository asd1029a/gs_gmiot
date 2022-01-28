/**
 * 관제
 */

const mntr = {
    init : () => {
        //지도 생성
        let map = new mapCreater('map',0);
        map.createMousePosition('mousePosition');
        map.scaleLine();
        window.map = map;
        //레이어 도구
        let lyControl = new layerControl('map', 'title');
        window.lc = lyControl;
        //측정 도구
        let measure = new measureTool('map');
        window.measure = measure;

    },
    eventHandler : () => {
        //LNM FOLD
        $('.mntr_container .lnb_fold').on("click", function(e){
            $('.mntr_container .menu_fold').hide();
            window.map.updateSize();
        });
        //LNM SWITCH
        $('.mntr_container .lnb ul li').on("click", function(e){
            const theme = $(e.currentTarget).attr('data-value');
            $('.mntr_container .menu_fold').hide();
            $('.mntr_container .menu_fold#'+theme).show();
            window.map.updateSize();
        });
        //LNM TAB SWITCH
        $('.mntr_container .menu_fold .tab li').on("click", function(e){
            const tab = $(e.currentTarget).attr('data-value');
            $('.menu_fold .lnb_tab_section').hide();
            $('div#'+tab).show();
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");

        });
        //LNM TAB SEARCH DETAIL
        $('.detail_btn').on("click", function(e){
            const form = $(e.currentTarget).parents('.lnb_tab_section').find('.search_fold');
            if(form.is(':visible')) {
                form.hide();
            } else {
                form.show();
            }
        });
        //RNM CLOSER
        $('.rnm_closer').on("click", function(e){
            $('.area_info').hide();
            window.map.updateSize();
        });
        //MAP TOOL
        $('.map_options li').on("click", function(e){
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
        //MAP BASE SWITCH
        $('.map_type li').on("click", function(e){
            const type = $(e.currentTarget).attr('data-value');
            window.map.switchTileMap(type);
            //ACTIVE STYLE
            $(e.currentTarget).parent().children("li").removeClass("active");
            $(e.currentTarget).addClass("active");
        });

    }

}
