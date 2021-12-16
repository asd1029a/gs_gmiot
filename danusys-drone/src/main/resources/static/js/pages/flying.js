
$(function() {
    $(".lnbToggle").on("click", function() {
        $(".lnb").toggleClass("toggle");
    });

    $(".selectboxCheck_title").click(function(){
        $(".selectCheckList").show();
    })

    $('.lnb .tabButton li').click(function(){
        $(this).addClass('on');
    });

    $(".infoTitle i").click(function(){
        $(".infoArea").hide("2000", function() {
            // mapManager.map.updateSize();
        });
    });

    $(".listScroll dl").click(function(){
        $(".infoArea").toggle("2000", function() {
            // mapManager.map.updateSize();
        });
    });

    $(".gnm li").click(function() {
        $(this).addClass('on');
    })

    // 각각의 셀렉트 박스 인스턴스 생성
    var selectType01 = new CustomSelectBox('.selectBox.type01');
})

//    selectbox custom
function CustomSelectBox(selector){
    this.$selectBox = null,
        this.$select = null,
        this.$list = null,
        this.$listLi = null;
    CustomSelectBox.prototype.init = function(selector){
        this.$selectBox = $(selector);
        this.$select = this.$selectBox.find('.box .select');
        this.$list = this.$selectBox.find('.box .list');
        this.$listLi = this.$list.children('li');
    }
    CustomSelectBox.prototype.initEvent = function(e){
        var that = this;
        this.$select.on('click', function(e){
            that.listOn();
        });
        this.$listLi.on('click', function(e){
            that.listSelect($(this));
        });
        $(document).on('click', function(e){
            that.listOff($(e.target));
        });
    }
    CustomSelectBox.prototype.listOn = function(){
        this.$selectBox.toggleClass('on');
        if(this.$selectBox.hasClass('on')){
            this.$list.css('display', 'block');
        }else{
            this.$list.css('display', 'none');
        };
    }
    CustomSelectBox.prototype.listSelect = function($target){
        $target.addClass('selected').siblings('li').removeClass('selected');
        this.$selectBox.removeClass('on');
        this.$select.text($target.text());
        this.$list.css('display', 'none');
    }
    CustomSelectBox.prototype.listOff = function($target){
        if(!$target.is(this.$select) && this.$selectBox.hasClass('on')){
            this.$selectBox.removeClass('on');
            this.$list.css('display', 'none');
        };
    }
    this.init(selector);
    this.initEvent();
}

const setDroneDraw = function(data) {
    let source = createSource(data);
    let style = droneLayerStyle;
    //mapManager.createVectorLayer("fclt",style,source);
    mapManager.createVectorLayer("drone", style, source);

}

const droneLayerStyle = function(feature) {
    const imgSrc = createFcltMarker();
    const style = new ol.style.Style({
        image: new ol.style.Icon({ scale: 1, src: imgSrc }),
        zIndex: 0
    });
    return style;
}

const createSource = function(data) {
    let dronePosition = data.GlobalPositionInt;
    let coordinates =  new ol.proj.transform([dronePosition.lon / 10000000,dronePosition.lat / 10000000],
        mapManager.properties.projection, mapManager.properties.pro4j[mapManager.properties.type]);
    const source = new ol.source.Vector({
        features: [
            new ol.Feature({
                geometry : new ol.geom.Point(coordinates)
            })
        ]
    });

    return source;
}

const createFcltMarker = function() {
    let imageSrc = '';
    imageSrc = "/images/test/drone.png";
    return imageSrc;
}