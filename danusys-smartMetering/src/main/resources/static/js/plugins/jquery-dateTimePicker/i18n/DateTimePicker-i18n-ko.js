/* ----------------------------------------------------------------------------- 

  jQuery DateTimePicker - Responsive flat design jQuery DateTime Picker plugin for Web & Mobile
  Version 0.1.37
  Copyright (c)2016 Curious Solutions LLP and Neha Kadam
  http://curioussolutions.github.io/DateTimePicker
  https://github.com/CuriousSolutions/DateTimePicker

 ----------------------------------------------------------------------------- */

/*

	language: Korean
	file: DateTimePicker-i18n-ko

*/

(function ($) {
    $.DateTimePicker.i18n["ko"] = $.extend($.DateTimePicker.i18n["ko"], {
        
    	language: "ko",

    	dateTimeFormat: "yyyy-mm-dd HH:mm",
		dateFormat: "yyyy-mm-dd",
		timeFormat: "HH:mm",

		shortDayNames: ["일", "월", "화", "수", "목", "금", "토"],
		fullDayNames: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
		shortMonthNames: ["01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"],
		fullMonthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],

		titleContentDate: "날짜 설정",
		titleContentTime: "시간 설정",
		titleContentDateTime: "날짜 & 시간 설정",
	
		setButtonContent: "확인",
		clearButtonContent: "지우기"
        
    });
})(jQuery);