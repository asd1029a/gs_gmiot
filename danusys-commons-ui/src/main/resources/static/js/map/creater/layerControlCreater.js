
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
        this.mapName = target;
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
     * @summary 해당 레이어들 모두 보이기
     * @param layerList : 레이어명 리스트 (***Layer제외)
     * */
    onList(layerList) {
        if(layerList.length > 0){
            for(let name of layerList){
                this.on(name+'Layer');
            }
        }
    }

    /**
     * @summary 해당 레이어 숨기기
     * @param layerName : 레이어명
     * */
    off(layerName) {
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                layer.setVisible(false);
                //레이어의 팝업끄기
                let popup = new mapPopup(this.mapName);
                popup.remove('mouseClickPopup');
            }
        });
    }

    /**
     * @summary 해당 레이어들 모두 숨기기
     * @param layerList : 레이어명 리스트 (***Layer제외)
     * */
    offList(layerList) {
        for(let name of layerList){
            this.off(name+'Layer');
        }
    }

    /**
     * @summary 해당 레이어 on off 토글
     * 레이어가 켜져 있으면 끄기 꺼져 있으면 켜기
     * @param layerName : 레이어명
     * */
    toggle(layerName) {
        let flag = this.find(layerName).getVisible();
        if(flag){
            this.off(layerName);
        } else {
            this.on(layerName);
        }
    }

    /**
     * @summary 해당 레이어 제거하기
     * @param layerName : 레이어명
     * */
    remove(layerName) {
        this.map.getLayers().forEach( layer => {
            if(layer.get(this.check)==layerName){
                this.map.removeLayer(layer);
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
                this.map.removeLayer(layer);
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
     * @summary 클러스터 레이어 배열 거리 기준부여
     * @param layerNames : 레이어 이름들
     * @param distance : 거리
     */
    setDistances(layerNames, distance) {
        layerNames.forEach(name => {
            if(this.exist(name+ 'Layer')){
                this.find(name + 'Layer').getSource().setDistance(distance);
            }
        });
    }

    /**
     * @summary 레이어 순서 바꾸기
     * @param 레이어명
     * @param 순서
     * */
    // order(layerName, order) {
    //     this.map.getLayers().forEach( layer => {
    //         if(layer.get(this.check)==layerName){
    //             layer.setZIndex(order);
    //         }
    //     });
    //     this.map.renderSync();
    // }

    /**
     * @summary 해당 그룹안 모든 레이어 보여지기
     * @param groupName : 그룹명
     */
    groupAllOn(groupName) {
        const layerGroup = this.find(groupName);
        layerGroup.getLayers().forEach( layer => layer.setVisible(true) );
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 모든 레이어 숨겨지기
     * @param groupName : 그룹명
     */
    groupAllOff(groupName) {
        const layerGroup = this.find(groupName);
        layerGroup.getLayers().forEach(layer => layer.setVisible(false) );
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 모든 레이어 제거하기
     * @param groupName : 그룹명
     */
    groupAllRemove(groupName) {
        this.map.removeLayer(this.find(groupName));
        this.map.renderSync();
    }

    /**
     * @summary 해당 그룹안 해당 레이어 보여지기
     * @param groupName 그룹레이어 이름
     * @param layerName 보여질 그룹안 레이어 이름
     */
    groupOn(groupName, layerName) {
        const layerGroup = this.find(groupName);
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
        const layerGroup = this.find(groupName);
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
        const ary = this.find(groupName).getLayers().getArray();
        ary.push(layer);
        this.map.renderSync();
    }

    /**
     * 그룹레이어 안에 레이어 제거하기
     * @param groupName 그룹레이어 이름
     * @param layerName 제거할 레이어 이름
     */
    groupRemove(groupName,layerName) {
        const ary = this.find(groupName).getLayers().getArray();
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

