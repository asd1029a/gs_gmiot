/* slideMenu plugin */

/**
 * slideMenu 생성용 class
 * @author - 
 * @version 0.0.1
 * @class slideMenu
 * */
var slideMenu = {
	/**
	 * slideMenu 생성 함수.
	 * @function slideMenu.init
	 * @returns {slide} slide object
	 * */
	init: function() {
		/**
		 * slide menu
		 * @extends slideMenu
		 * @class slide
		 * @property {element} target - slide element
		 * @property {string} type - 슬라이드 방향 (h : 가로, v : 세로)
		 * @property {Array} data - slide 데이터
		 * @property {number} viewSize - 한번에 보여질 개수
		 * @property {number} itemSize - 메뉴 하나의 크기
		 * */
		const slide = {
			target: undefined,
			type: 'h', // h : 좌우, v : 세로
			data: [],
			viewSize: 5,
			itemSize: 0,
			/**
			 * slide init 함수
			 * @function slide.init
			 * @param {element} target - slide element
			 * @param {viewSize} viewSize - 한번에 보여질 개수
			 * @param {string} type - slide type
			 * @see {@link slide.type}
			 * */
			init: function(target, viewSize, type) {
				this.target = $(target);
				this.data = this.target.children();
				this.viewSize = viewSize;
				this.type = type;
				this.itemSize = type == 'h' ? $(this.data[0]).outerWidth(true) : $(this.data[0]).outerHeight(true);
				if (type == 'h') this.target.parent().width(viewSize * this.itemSize);
				this.move.init(this);
			},
			/**
			 * slide move object 생성자
			 * @function slide.move
			 * @returns {object} - move object
			 * */
			move: (function() {
				var count = 0;
				var menu = undefined;
				
				return {
					min: 0,
					max: 0,
					init: function(target) {
						menu = target;
						this.max = menu.type == 'h' ? (menu.data.length * menu.itemSize) - (menu.viewSize * menu.itemSize) : menu.target.outerHeight(true) - menu.target.parent().height();
					},
					left: function() {
						if(count == 0) menu.target.css('left', -(this.min));
						else if(count > 0) menu.target.css('left', -(--count * menu.itemSize));
					},
					right: function() {
						const max = menu.data.length - menu.viewSize;
						if(count == max) menu.target.css('left', -(this.max));
						else if(count < max) menu.target.css('left', -(++count * menu.itemSize));
					},
					top: function() {
						if(count == 0) menu.target.css('top', -(this.min));
						else if(count > 0) menu.target.css('top', -(--count * menu.itemSize));
					},
					bottom: function() {
						const max = menu.data.length - menu.viewSize;
						if(count == max) menu.target.css('top', -(this.max));
						else if(count < max) menu.target.css('top', -(++count * menu.itemSize));
					}
				}
			}()),
		}
		
		return slide;
	}
}