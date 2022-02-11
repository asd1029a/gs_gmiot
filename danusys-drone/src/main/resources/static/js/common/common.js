"use strict"
const common = {
    arr: Array.prototype.slice,
    crtEl: function(el) {
        const rtnElement = document.createElement(el);
        return rtnElement;
    },
    getId: function (el) {
        const rtnElement = document.getElementById(el);
        return rtnElement;
    },
    getClass: function (el) {
        const rtnElement = document.getElementsByClassName(el);
        return rtnElement;
    },
    getQs: function (el) {
        const rtnElement = document.querySelector(el);
        return rtnElement;
    },
    getQsAll: function (el) {
        const rtnElement = document.querySelectorAll(el);
        return rtnElement;
    },
    elementSelected: (elements, name, className) => {
        //let selected = common.getQs(`.${className}}``).getAttribute("id");
        for (let arr of common.arr.call(elements)) {
            arr.addEventListener(name, e => {
                elements.map(value=> {
                    return value.classList.remove(className);
                });
                e.currentTarget.classList.add(className);
            });
        }
    },
    /*postAxios: (data) => {
        let key = Object.keys(data.data);
        const dataArr = data.data[key];
        return dataArr;
    },*/
}
const commonPopup = {
    openPopup: function(page,list,popupFnc,option) {
        const createPopup =
            axios.get("/view/action/page.do?page="+page,{

            }).then(response => {
                if(response.data) {
                    const popupArea = common.getId("popupArea");
                    popupArea.classList.add("open");
                    popupArea.innerHTML = response.data;
                    popupFnc.init();
                    setTimeout(function() {
                        if(list != null && list != undefined) {
                            common.setDetail(list,option);
                        }
                    },100)
                }
            }).catch(error => {
                console.log(error);
            })

    },
    closePopup: function() {
        const popupArea = document.getElementById("popupArea");
        popupArea.innerHTML = "";
        popupArea.classList.remove("open")
    },

    
    // 수정 필요
    createSelectBox: function(id,dataList) {
        const select = document.getElementById(id);
        const option = document.createElement("option");
        option.value = "";
        option.text = "-선택-";
        select.appendChild(option);
        for(let i in dataList) {
            const option = document.createElement("option");
            option.value = dataList[i].value;
            option.text = dataList[i].text;
            select.appendChild(option);
        }
    },


    setDetail: function(list,option) {
        const keys = Object.keys(list);
        for (let i=0;i<keys.length;i++) {
            const id = common.getId(keys[i])
            if(id != undefined && id != null) {
                id.value = list[keys[i]];
            }
        }
        for(let i=0;i<option.length;i++) {
            common.getId(option[i]).setAttribute("disabled",true)
        }
    }
}


/*

let elementSelected = function(elements,event,className) {
    let el = common.getClass(elements);
    let arr = common.arr.call(el);
    arr.map(a => {
        a.addEventListener(event, mouseEvent => {
            removeSelected(arr,className);
            mouseEvent.currentTarget.classList.add(className);
        })
    })
}

let removeSelected = function(list,className) {
    list.map(a => {
        a.classList.remove(className);
    })
}

let removeElements = function(el) {
    let arr = common.arr.call(el)
    arr.map(a => {
        a.remove();
    })
}*/
