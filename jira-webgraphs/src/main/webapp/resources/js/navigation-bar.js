/*
	RequestAnimationFrame Polyfill

	http://paulirish.com/2011/requestanimationframe-for-smart-animating/
	http://my.opera.com/emoller/blog/2011/12/20/requestanimationframe-for-smart-er-animating
	by Erik Möller, fixes from Paul Irish and Tino Zijdel

	MIT license
 */

(function() {
    var lastTime = 0;
    var vendors = ['ms', 'moz', 'webkit', 'o'];
    for (var x = 0; x < vendors.length && !window.requestAnimationFrame; ++x) {
        window.requestAnimationFrame = window[vendors[x] + 'RequestAnimationFrame'];
        window.cancelAnimationFrame = window[vendors[x] + 'CancelAnimationFrame'] || window[vendors[x] + 'CancelRequestAnimationFrame'];
    }

    if (!window.requestAnimationFrame) {
        window.requestAnimationFrame = function(callback, element) {
            var currTime = new Date().getTime();
            var timeToCall = Math.max(0, 16 - (currTime - lastTime));
            var id = window.setTimeout(function() {
                    callback(currTime + timeToCall);
                },
                timeToCall);
            lastTime = currTime + timeToCall;
            return id;
        };
    }

    if (!window.cancelAnimationFrame) {
        window.cancelAnimationFrame = function(id) {
            clearTimeout(id);
        };
    }
}());


/*
	Sticky menu script
 */

(function(w, d, undefined) {
    var el_html = d.documentElement,
        el_body = d.getElementsByTagName('body')[0],
        header = d.getElementById('header'),
        lastScroll = w.pageYOffset || el_body.scrollTop,

        menuIsStuck = function(triggerElement, wScrollTop, lastScroll) {
            var regexp = /(nav\-is\-stuck)/i,
                classFound = el_html.className.match(regexp),
                navHeight = header.offsetHeight,
                bodyRect = el_body.getBoundingClientRect(),
                scrollValue = triggerElement ? triggerElement.getBoundingClientRect().top - bodyRect.top - navHeight : 800,
                scrollValFix = classFound ? scrollValue : scrollValue + navHeight;

            // if scroll down is 700 or more AND nav-is-stuck class doesn't exist AND we are going up
            if (wScrollTop > scrollValFix && !classFound && wScrollTop < lastScroll) {
                el_html.className = el_html.className + ' nav-is-stuck';
                el_body.style.paddingTop = navHeight + 'px';
            }

            // if we are to high in the page AND nav-is-stuck class exists
            if (classFound && wScrollTop > lastScroll) {
                el_html.className = el_html.className.replace(regexp, '');
                el_body.style.paddingTop = '0px';
            }
        },

        onScrolling = function() {
            var wScrollTop = w.pageYOffset || el_body.scrollTop;

            // use scroll datas as parameters…
            menuIsStuck(d.getElementById('main'), wScrollTop, lastScroll);

            // save current scroll as last scroll position
            lastScroll = wScrollTop;

        };


    el_html.className = el_html.className + ' js';

    // when you scroll, fire onScrolling() function
    w.addEventListener('scroll', function() {
        w.requestAnimationFrame(onScrolling);
    });


}(window, document));