'use strict'
document.addEventListener("DOMContentLoaded",function () {
    const lnb = document.querySelector(".lnb .listScroll ul li");
    lnb.addEventListener("click", function(e) {
        missionDetail(e.currentTarget);
    })
})

const createMisnRouteDetail = function() {

    // 상단부분 고정 total cnt 만 변경
    const missionRoute = common.crtEl("article");
    missionRoute.className = "missionRoute";
    const titleDl = common.crtEl("dl");
    title.className = "title";
    const titleDt = common.crtEl("dt");
    const titleDtI = common.crtEl("i");
    const titleDtImg = common.crtEl("img");
    titleDtImg.src = "images/default/arrowBottom.svg";
    titleDtI.append(titleDtImg);
    titleDt.append(titleDtI);

    const titleDd = common.crtEl("dd");
    const titleDdSpan = common.crtEl("span");

    titleDd.createTextNode("Total");
    titleDdSpan.createTextNode("555");
    titleDd.append(titleDdSpan);

    titleDl.append(titleDt,titleDd);
    missionRoute.appendChild(titleDl);


    const missionStart = common.crtEl("p");
    missionStart.className = "missionStart";
    const missionStartI = common.crtEl("i");
    const missionStartImg = common.crtEl("img");
    missionStartImg.src = "images/default/arrowBottom.svg";
    missionStartI.append(missionStartImg);
    missionStart.append(missionStartI);
    // 내용 부분
    const routeContents = common.crtEl("div");
}


const missionDetail = async function() {
    missionDetail.prototype.missionList = await axios.post("/drone/api/getTestData", {
        "url":"data/missionList.json"
    })
    const testData = await axios.post("/drone/api/getTestData",{
        "url":"data/mission.json"
    })
    let key = Object.keys(testData.data);
    const dataArr = testData.data[key];
    for(let i of dataArr) {
        if(i.name =="takeOff") {
            createTakeOff(i,this.missionList.data);
        }
        else if(i.name == "wayPoint") {
            createWayPoint(i,this.missionList.data)
        }
    }
}

const createTakeOff = async function(data,selectBox) {
    const contents = common.getQs(".routeContents");
    const routeSet = common.crtEl("div");
    routeSet.className = "routeSet";

}

const createWayPoint = async function(data,selectBox) {

    //import contents
    const contents = common.getQs(".routeContents");
    //routeSet
    const routeSet = common.crtEl("div");
    routeSet.className = "routeSet";

    const routeDl = common.crtEl("dl");
    routeDl.className = "routeTitle";
z
    const routeDt = common.crtEl("dt");
    const dtSpan = common.crtEl("span")
    dtSpan.className = "num";

    //test
    customSelectBox.createSelectBox(data,selectBox);

    //routeDt.append(dtSpan,selectBox);
    //routeDl.append(routeDt);

    const routeDd = common.crtEl("dd");
    const ddSpan = common.crtEl("span");

    dtSpan.className = "button";
    dtSpan.innerHtml = "삭제";
    const i = common.crtEl("i");
    const iImg = common.crtEl("img");
    iImg.src = "images/default/arrowBottom.svg";
    i.append(iImg);
    routeDd.append(dtSpan, i);

    routeSet.append(routeDl,routeDd);
    contents.append(routeSet);
}

const returnDrone = function() {

}

const clickSelect = function() {
}

const customSelectBox = {
    createSelectBox: function (data,selectBox) {
        let key = Object.keys(selectBox);
        const selectList = selectBox[key];
        const selectBoxType = common.crtEl("div");
        selectBoxType.className = "selectBox"
        selectBoxType.classList.add(`type${data.order}`)

    }
}
