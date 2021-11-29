/* log.js */
/**
 * 이벤트 및 경고 관련 로그를 제공한다.
 * @author - 
 * @version 0.0.1
 * @class commonLog
 * @property {obejct} prop - log 관련 property
 * @property {element} prop.wrap - 로그를 출력할 element
 * @property {Array} prop.list - 현재 출력 되어있는 log 데이터 배열
 * @property {number} prop.timeout - 로그 출력 시간
 * @property {number} prop.max - 로그 최대 개수
 * @example
 * // commonLog 생성
 * commonLog.init('map');
 * */
var commonLog = {
	prop : {
		wrap : undefined,
		list : [],
		timeout : 3000,
		max : 10
	},
	/**
	 * info log 출력 함수.
	 * @param {string} message - 출력 할 메시지
	 * @param {object} option - log 관련 option
	 * @param {object} option.isAutoClose - 로그 자동 닫기 기능(true : 자동 닫기, false : 수동 닫기)
	 * @param {function} option.callback - 로그 클릭시 실행 될 callback function
	 * @param {object} option.callbackProp - callback function에서 사용 될 option
	 * */
	info : function(message, option) {
		this.createLog(message, 'info', option);
	},
	/**
	 * debug log 출력 함수.
	 * @param {string} message - 출력 할 메시지
	 * @param {object} option - log 관련 option
	 * @param {object} option.isAutoClose - 로그 자동 닫기 기능(true : 자동 닫기, false : 수동 닫기)
	 * @param {function} option.callback - 로그 클릭시 실행 될 callback function
	 * @param {object} option.callbackProp - callback function에서 사용 될 option
	 * */
	debug : function(message, option) {
		this.createLog(message, 'debug', option);
	},
	/**
	 * error log 출력 함수.
	 * @param {string} message - 출력 할 메시지
	 * @param {object} option - log 관련 option
	 * @param {object} option.isAutoClose - 로그 자동 닫기 기능(true : 자동 닫기, false : 수동 닫기)
	 * @param {function} option.callback - 로그 클릭시 실행 될 callback function
	 * @param {object} option.callbackProp - callback function에서 사용 될 option
	 * */
	error : function(message, option) {
		this.createLog(message, 'error', option);
	},
	/**
	 * 로그 자동 종료 함수.
	 * @param {element} el - log element
	 * */
	setAutoClose : function(el) {
		setTimeout(function() {
			commonLog.prop.list.splice(commonLog.prop.list.indexOf(el), 1);
			el.remove();
		}, commonLog.prop.timeout)
	},
	/**
	 * log element 별 option 지정 함수.
	 * @param {element} el - log element
	 * @param {object} option - log 관련 option
	 * @param {object} option.isAutoClose - 로그 자동 닫기 기능(true : 자동 닫기, false : 수동 닫기)
	 * @param {function} option.callback - 로그 클릭시 실행 될 callback function
	 * */
	setElementOption : function(el, option) {
		if(option.isAutoClose) {
			this.setAutoClose(el);
		}
		
		if(typeof option.callback == 'function') {
			el.classList.add('twinkle');
		}
	},
	/**
	 * log element 생성 함수.
	 * @param {string} message - 출력 할 메시지
	 * @param {string} level - log level('info' : info log, 'debug' : debug log, 'error' : error log)
	 * @param {object} option - log 관련 option
	 * @param {object} option.isAutoClose - 로그 자동 닫기 기능(true : 자동 닫기, false : 수동 닫기)
	 * @param {function} option.callback - 로그 클릭시 실행 될 callback function
	 * @param {object} option.callbackProp - callback function에서 사용 될 option
	 * */
	createLog : function(message, level, option) {
		const length = this.prop.list.length;
		const max = this.prop.max;
		
		if(length >= max) {
			const temp = this.prop.list.shift();
			temp.classList.add('remove');
			temp.remove();
		}
		
		const el = document.createElement('div');
		el.classList.add('log');
		el.classList.add(level);
		
		const text = document.createElement('span');
		text.innerHTML = message;
		
		el.appendChild(text);
		
		el.addEventListener('click', function(e) {
			el.classList.add('remove');
			commonLog.prop.list.splice(commonLog.prop.list.indexOf(el), 1);
			el.remove();

			if(typeof option.callback == 'function') {
				option.callback(option.callbackProp);
			}
		});
		
		this.prop.wrap.appendChild(el);

		this.prop.list.push(el);
		
		if(typeof option == 'object') this.setElementOption(el, option);
	},
	/**
	 * commonLog class init function.
	 * @param {string} parentId - 부모 element id
	 * */
	init : function(parent) {
		const wrap = document.createElement('div');
		wrap.classList.add('log-area');
		
		this.prop.wrap = wrap;
		
		parent.append(wrap);
	}
}