
/**
 * 레이어 조작
 */
class layerControl {
    /**
     * @param target : 레이어 적용할 맵명
     * @param checkProp : 레이어 구분 속성명 (name, title)
     * */
    constructor(target, checkProp) {
        this.map = window[target].map;
        this.check = checkProp;
    }

    /**
     * @summary 해당 레이어 보이기
     * @param layerName : 레이어명
     * */
    on(layerName) {
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                layer.setVisible(true);
            }
        });
    }

    /**
     * @summary 해당 레이어 숨기기
     * @param layerName : 레이어명
     * */
    off(layerName) {
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                layer.setVisible(false);
            }
        });
    }

    /**
     * @summary 해당 레이어 제거하기
     * @param layerName : 레이어명
     * */
    remove(layerName) {
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                map.removeLayer(layer);
            }
        });
    }

    /**
     * @summary 해당단어가 포함된 레이어 제거하기
     * @param word : 레이어명에 포함될 키워드
     */
    removeList(word) {
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check).indexOf(word)>-1) {
                map.removeLayer(layer);
            }
        });
    }

    /**
     * @summary 레이어 존재 여부 판단
     * @param 레이어 이름
     * @return boolean(존재(true)/미존재(false))
     */
    exist(layerName) {
        let flag = false;
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                flag = true;
            }
        });
        return flag;
    }

    /**
     * @summary 레이어 객체 찾기
     * @param 레이어 이름
     * @return 해당 레이어 객체
     */
    find(layerName) {
        let foundLayer;
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                foundLayer =  layer;
            }
        });
        return foundLayer;
    }

    /**
     * @summary 해당 그룹안 모든 레이어 보여지기
     * @param groupName : 그룹명
     */
    groupAllOn(groupName) {
        const layerGroup = layerControl.find(groupName);
        layerGroup.getLayers().forEach( layer => layer.setVisible(true) );
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 모든 레이어 숨겨지기
     * @param groupName : 그룹명
     */
    groupAllOff(groupName) {
        const layerGroup = layerControl.find(groupName);
        layerGroup.getLayers().forEach(layer => layer.setVisible(false) );
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 모든 레이어 제거하기
     * @param groupName : 그룹명
     */
    groupAllRemove(groupName) {
        this.map.removeLayer(layerControl.find(groupName));
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 해당 레이어 보여지기
     * @param groupName 그룹레이어 이름
     * @param layerName 보여질 그룹안 레이어 이름
     */
    groupOn(groupName, layerName) {
        const layerGroup = layerControl.find(groupName);
        layerGroup.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                layer.setVisible(true);
            }
        });
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 해당 레이어 숨겨지기
     * @param groupName 그룹레이어 이름
     * @param layerName 숨겨질 그룹안 레이어 이름
     */
    groupOff(groupName, layerName) {
        const layerGroup = layerControl.find(groupName);
        layerGroup.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                layer.setVisible(false);
            }
        });
        this.map.renderSync();
    }

    /**
     * 그룹레이어 안에 레이어 추가하기
     * @param groupName 그룹레이어 이름
     * @param layer 추가할 레이어 객체
     */
    groupAdd(groupName,layer) {
        const ary = layerControl.find(groupName).getLayers().getArray();
        ary.push(layer);
        this.map.renderSync();
    }

    /**
     * 그룹레이어 안에 레이어 제거하기
     * @param groupName 그룹레이어 이름
     * @param layerName 제거할 레이어 이름
     */
    groupRemove(groupName,layerName) {
        const ary = layerControl.find(groupName).getLayers().getArray();
        if(ary.length>0){
            for(let i=0; i<ary.length; i++){
                if(ary[i].get(this.check)==layerName){
                    ary.splice(i,1);
                }
            }
        }
        this.map.renderSync();
    }



}

