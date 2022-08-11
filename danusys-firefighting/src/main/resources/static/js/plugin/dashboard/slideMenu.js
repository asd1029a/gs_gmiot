/* slideMenu plugin */

var slideMenu = {
    init: function() {
        const slide = {
            target: undefined,
            type: 'h', // h : 좌우, v : 세로
            data: [],
            viewSize: 5,
            itemSize: 0,
            init: function(target, viewSize, type) {
                this.target = $(target);
                this.data = this.target.children();
                this.viewSize = viewSize;
                this.type = type;
                this.itemSize = type == 'h' ? $(this.data[0]).outerWidth(true) : $(this.data[0]).outerHeight(true);
                if (type == 'h') this.target.parent().width(viewSize * this.itemSize);
                this.move.init(this);
            },
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