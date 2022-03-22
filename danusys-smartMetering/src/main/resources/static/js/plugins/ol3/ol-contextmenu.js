/*!
 * ol-contextmenu - v4.0.0
 * https://github.com/jonataswalker/ol-contextmenu
 * Built: Wed Nov 20 2019 13:29:37 GMT-0300 (Brasilia Standard Time)
 */
! function(t, e) {
    "object" == typeof exports && "undefined" != typeof module ? module.exports = e(require("ol/control/Control")) : "function" == typeof define && define.amd ? define(["ol/control/Control"], e) : (t = t || self).ContextMenu = e(t.ol.control.Control)
}(this, (function(t) {
    "use strict";
    t = t && t.hasOwnProperty("default") ? t.default : t;
    var e = "ol-ctx-menu",
        n = {
            namespace: e,
            container: e + "-container",
            separator: e + "-separator",
            submenu: e + "-submenu",
            hidden: e + "-hidden",
            icon: e + "-icon",
            zoomIn: e + "-zoom-in",
            zoomOut: e + "-zoom-out",
            unselectable: "ol-unselectable"
        },
        i = n,
        o = "beforeopen",
        s = "open",
        r = "close",
        a = "contextmenu",
        l = {
            width: 150,
            scrollAt: 4,
            eventType: a,
            defaultItems: !0
        },
        c = [{
            text: "Zoom In",
            classname: n.zoomIn + " " + n.icon,
            callback: function(t, e) {
                var n = e.getView();
                n.animate({
                    zoom: +n.getZoom() + 1,
                    duration: 700,
                    center: t.coordinate
                })
            }
        }, {
            text: "Zoom Out",
            classname: n.zoomOut + " " + n.icon,
            callback: function(t, e) {
                var n = e.getView();
                n.animate({
                    zoom: +n.getZoom() - 1,
                    duration: 700,
                    center: t.coordinate
                })
            }
        }];

    function h(t, e) {
        if (void 0 === e && (e = "Assertion failed"), !t) {
            if ("undefined" != typeof Error) throw new Error(e);
            throw e
        }
    }

    function p(t) {
        return /^\d+$/.test(t)
    }

    function u(t, e) {
        return t.classList ? t.classList.contains(e) : y(e).test(t.className)
    }

    function d(t, e, n) {
        void 0 === e && (e = window.document);
        var i = Array.prototype.slice,
            o = [];
        if (/^(#?[\w-]+|\.[\w-.]+)$/.test(t)) switch (t[0]) {
            case "#":
                o = [m(t.substr(1))];
                break;
            case ".":
                o = i.call(e.getElementsByClassName(t.substr(1).replace(/\./g, " ")));
                break;
            default:
                o = i.call(e.getElementsByTagName(t))
        } else o = i.call(e.querySelectorAll(t));
        return n ? o : o[0]
    }

    function m(t) {
        return t = "#" === t[0] ? t.substr(1, t.length) : t, document.getElementById(t)
    }

    function f(t) {
        var e = document.createDocumentFragment(),
            n = document.createElement("div");
        for (n.innerHTML = t; n.firstChild;) e.appendChild(n.firstChild);
        return e
    }

    function y(t) {
        return new RegExp("(^|\\s+) " + t + " (\\s+|$)")
    }

    function v(t, e, n) {
        t.classList ? t.classList.add(e) : t.className = (t.className + " " + e).trim(), n && p(n) && window.setTimeout((function() {
            return g(t, e)
        }), n)
    }

    function g(t, e, n) {
        t.classList ? t.classList.remove(e) : t.className = t.className.replace(y(e), " ").trim(), n && p(n) && window.setTimeout((function() {
            return v(t, e)
        }), n)
    }
    var b = function(t) {
        return this.Base = t, this.map = void 0, this.viewport = void 0, this.coordinateClicked = void 0, this.pixelClicked = void 0, this.lineHeight = 0, this.items = {}, this.opened = !1, this.submenu = {
            left: t.options.width - 15 + "px",
            lastLeft: ""
        }, this.eventHandler = this.handleEvent.bind(this), this
    };
    b.prototype.init = function(t) {
        this.map = t, this.viewport = t.getViewport(), this.setListeners(), this.Base.Html.createMenu(), this.lineHeight = this.getItemsLength() > 0 ? this.Base.container.offsetHeight / this.getItemsLength() : this.Base.Html.cloneAndGetLineHeight()
    }, b.prototype.getItemsLength = function() {
        var t = this,
            e = 0;
        return Object.keys(this.items).forEach((function(n) {
            t.items[n].submenu || t.items[n].separator || e++
        })), e
    }, b.prototype.getPixelClicked = function() {
        return this.pixelClicked
    }, b.prototype.getCoordinateClicked = function() {
        return this.coordinateClicked
    }, b.prototype.positionContainer = function(t) {
        var e = this,
            n = this.Base.container,
            o = this.map.getSize(),
            s = o[1] - t[1],
            r = o[0] - t[0],
            a = n.offsetWidth,
            l = Math.round(this.lineHeight * this.getItemsLength()),
            c = d("li." + i.submenu + ">div", n, !0);
        r >= a ? (n.style.right = "auto", n.style.left = t[0] + 5 + "px") : (n.style.left = "auto", n.style.right = "15px"), s >= l ? (n.style.bottom = "auto", n.style.top = t[1] - 10 + "px") : (n.style.top = "auto", n.style.bottom = 0),
            function t(e, n, i) {
                if (Array.isArray(e)) e.forEach((function(e) {
                    return t(e, n, i)
                }));
                else
                    for (var o = Array.isArray(n) ? n : n.split(/\s+/), s = o.length; s--;) u(e, o[s]) && g(e, o[s], i)
            }(n, i.hidden), c.length && (this.submenu.lastLeft = r < 2 * a ? "-" + a + "px" : this.submenu.left, c.forEach((function(t) {
                var n, i, o, r = {
                        w: window.innerWidth || document.documentElement.clientWidth,
                        h: window.innerHeight || document.documentElement.clientHeight
                    },
                    a = (i = (n = t).getBoundingClientRect(), o = document.documentElement, {
                        left: i.left + window.pageXOffset - o.clientLeft,
                        top: i.top + window.pageYOffset - o.clientTop,
                        width: n.offsetWidth,
                        height: n.offsetHeight
                    }),
                    l = a.height,
                    c = s - l;
                c < 0 && (c = l - (r.h - a.top), t.style.top = "-" + c + "px"), t.style.left = e.submenu.lastLeft
            })))
    }, b.prototype.openMenu = function(t, e) {
        this.Base.dispatchEvent({
            type: s,
            pixel: t,
            coordinate: e
        }), this.opened = !0, this.positionContainer(t)
    }, b.prototype.closeMenu = function() {
        this.opened = !1,
            function t(e, n, i) {
                if (Array.isArray(e)) e.forEach((function(e) {
                    return t(e, n)
                }));
                else
                    for (var o = Array.isArray(n) ? n : n.split(/\s+/), s = o.length; s--;) u(e, o[s]) || v(e, o[s], i)
            }(this.Base.container, i.hidden), this.Base.dispatchEvent({
                type: r
            })
    }, b.prototype.setListeners = function() {
        this.viewport.addEventListener(this.Base.options.eventType, this.eventHandler, !1)
    }, b.prototype.removeListeners = function() {
        this.viewport.removeEventListener(this.Base.options.eventType, this.eventHandler, !1)
    }, b.prototype.handleEvent = function(t) {
        var e = this;
        this.coordinateClicked = this.map.getEventCoordinate(t), this.pixelClicked = this.map.getEventPixel(t), this.Base.dispatchEvent({
            type: o,
            pixel: this.pixelClicked,
            coordinate: this.coordinateClicked
        }), this.Base.disabled || (this.Base.options.eventType === a && (t.stopPropagation(), t.preventDefault()), this.openMenu(this.pixelClicked, this.coordinateClicked), t.target.addEventListener("click", {
            handleEvent: function(n) {
                e.closeMenu(), t.target.removeEventListener(n.type, this, !1)
            }
        }, !1))
    }, b.prototype.setItemListener = function(t, e) {
        var n, i = this;
        t && "function" == typeof this.items[e].callback && (n = this.items[e].callback, t.addEventListener("click", (function(t) {
            t.preventDefault();
            var o = {
                coordinate: i.getCoordinateClicked(),
                data: i.items[e].data || null
            };
            i.closeMenu(), n(o, i.map)
        }), !1))
    };
    var C = function(t) {
        return this.Base = t, this.Base.container = this.container = this.createContainer(!0), this
    };
    return C.prototype.createContainer = function(t) {
            var e = document.createElement("div"),
                n = document.createElement("ul"),
                o = [i.container, i.unselectable];
            return t && o.push(i.hidden), e.className = o.join(" "), e.style.width = parseInt(this.Base.options.width, 10) + "px", e.appendChild(n), e
        }, C.prototype.createMenu = function() {
            var t = [];
            if ("items" in this.Base.options ? t = this.Base.options.defaultItems ? this.Base.options.items.concat(c) : this.Base.options.items : this.Base.options.defaultItems && (t = c), 0 === t.length) return !1;
            t.forEach(this.addMenuEntry, this)
        }, C.prototype.addMenuEntry = function(t) {
            var e, n = this;
            if (t.items && Array.isArray(t.items)) {
                t.classname = t.classname || "", e = i.submenu, ~t.classname.indexOf(e) || (t.classname = t.classname.length ? " " + i.submenu : i.submenu);
                var o = this.generateHtmlAndPublish(this.container, t),
                    s = this.createContainer();
                s.style.left = this.Base.Internal.submenu.lastLeft || this.Base.Internal.submenu.left, o.appendChild(s), t.items.forEach((function(t) {
                    n.generateHtmlAndPublish(s, t, !0)
                }))
            } else this.generateHtmlAndPublish(this.container, t)
        }, C.prototype.generateHtmlAndPublish = function(t, e, n) {
            var o, s, r = "_" + Math.random().toString(36).substr(2, 9),
                a = !1;
            return "string" == typeof e && "-" === e.trim() ? (o = f('<li id="' + r + '" class="' + i.separator + '"><hr></li>'), s = [].slice.call(o.childNodes, 0)[0], t.firstChild.appendChild(o), a = !0) : (e.classname = e.classname || "", o = f("<span>" + e.text + "</span>"), s = document.createElement("li"), e.icon && ("" === e.classname ? e.classname = i.icon : -1 === e.classname.indexOf(i.icon) && (e.classname += " " + i.icon), s.setAttribute("style", "background-image:url(" + e.icon + ")")), s.id = r, s.className = e.classname, s.appendChild(o), t.firstChild.appendChild(s)), this.Base.Internal.items[r] = {
                id: r,
                submenu: n || 0,
                separator: a,
                callback: e.callback,
                data: e.data || null
            }, this.Base.Internal.setItemListener(s, r), s
        }, C.prototype.removeMenuEntry = function(t) {
            var e = d("#" + t, this.container.firstChild);
            e && this.container.firstChild.removeChild(e), delete this.Base.Internal.items[t]
        }, C.prototype.cloneAndGetLineHeight = function() {
            var t = this.container.cloneNode(),
                e = f("<span>Foo</span>"),
                n = f("<span>Foo</span>"),
                i = document.createElement("li"),
                o = document.createElement("li");
            i.appendChild(e), o.appendChild(n), t.appendChild(i), t.appendChild(o), this.container.parentNode.appendChild(t);
            var s = t.offsetHeight / 2;
            return this.container.parentNode.removeChild(t), s
        },
        function(t) {
            function e(e) {
                void 0 === e && (e = {}), h("object" == typeof e, "@param `opt_options` should be object type!"), this.options = function(t, e) {
                    var n = {};
                    for (var i in t) n[i] = t[i];
                    for (var o in e) n[o] = e[o];
                    return n
                }(l, e), this.disabled = !1, this.Internal = new b(this), this.Html = new C(this), t.call(this, {
                    element: this.container
                })
            }
            return t && (e.__proto__ = t), e.prototype = Object.create(t && t.prototype), e.prototype.constructor = e, e.prototype.clear = function() {
                Object.keys(this.Internal.items).forEach(this.Html.removeMenuEntry, this.Html)
            }, e.prototype.close = function() {
                this.Internal.closeMenu()
            }, e.prototype.enable = function() {
                this.disabled = !1
            }, e.prototype.disable = function() {
                this.disabled = !0
            }, e.prototype.getDefaultItems = function() {
                return c
            }, e.prototype.countItems = function() {
                return Object.keys(this.Internal.items).length
            }, e.prototype.extend = function(t) {
                h(Array.isArray(t), "@param `arr` should be an Array."), t.forEach(this.push, this)
            }, e.prototype.isOpen = function() {
                return this.Internal.opened
            }, e.prototype.updatePosition = function(t) {
                h(Array.isArray(t), "@param `pixel` should be an Array."), this.isOpen() && this.Internal.positionContainer(t)
            }, e.prototype.pop = function() {
                var t = Object.keys(this.Internal.items);
                this.Html.removeMenuEntry(t[t.length - 1])
            }, e.prototype.push = function(t) {
                h(null != t, "@param `item` must be informed."), this.Html.addMenuEntry(t)
            }, e.prototype.shift = function() {
                this.Html.removeMenuEntry(Object.keys(this.Internal.items)[0])
            }, e.prototype.setMap = function(e) {
                t.prototype.setMap.call(this, e), e ? this.Internal.init(e, this) : this.Internal.removeListeners()
            }, e
        }(t)
}));