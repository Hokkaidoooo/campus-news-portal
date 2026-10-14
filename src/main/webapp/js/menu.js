nav_menu('.nav_menu1');

window.curView = '/'
if (curView.substr(-1) == '/') {
    curView = curView.substr(0, curView.length - 1)
}

window.navData = []
window.breadcrumb = []
$('.nav_one').each(function (i, nav_one) {
    var $one = $(nav_one);
    var twoList = [];
    $one.children('.nav_two').children('li').each(function (j, nav_two) {
        var $two = $(nav_two);
        var twoHref = $two.children('a').attr('href')
        var threeList = [];
        if (curView == twoHref) {
            breadcrumb.push({
                name: $one.children('a').text()
            })
            breadcrumb.push({
                name: $two.children('a').text(),
                href: twoHref,
            })
        }
        $two.children('.nav_three').children('li').each(function (k, nav_three) {
            var $three = $(nav_three);
            var threeHref = $three.children('a').attr('href')
            if (curView == threeHref) {
                breadcrumb.push({
                    name: $one.children('a').text()
                })
                breadcrumb.push({
                    name: $two.children('a').text()
                })
                breadcrumb.push({
                    name: $three.children('a').text(),
                    href: threeHref
                })
            }
            threeList.push({
                name: $three.children('a').text(),
                href: threeHref
            })
        })
        twoList.push({
            name: $two.children('a').text(),
            href: twoHref,
            threeList: threeList
        })
    })
    window.navData.push({
        name: $one.children('a').text(),
        href: $one.children('a').attr('href'),
        twoList: twoList
    })
})

function renderBreadcrumb() {




    if (!breadcrumb.length) {
        $('.Breadcrumb_box').hide();
        topBanners.forEach(function (top) {
            if (location.pathname.indexOf('article/detail') > 0 && top.fields.category == '文章顶部') {
                window.bannerUrl = top.fields.thumbnail;
                $('.banner_n img').attr('src', bannerUrl)
            }
        });
    } else {
        breadcrumb.forEach(function (item, index) {
            $('.Breadcrumb_r').append('<span> > </span><a target="_blank"  ' + (item.href ? ('href="' + item.href + '"') : '') + '>' + item.name + '</a>')
        });
        var banner_category = breadcrumb[breadcrumb.length - 1].name
        topBanners.forEach(function (top) {
            if (top.fields.category == banner_category) {
                window.bannerUrl = top.fields.thumbnail;
                $('.banner_n img').attr('src', bannerUrl)
            }
        });
    }

}

window.topBanners = [];
renderBreadcrumb()
