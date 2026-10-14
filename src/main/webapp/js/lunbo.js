$(document).ready(function () {
    $('.Yl-container').YlSlide({
        wrapper: '.Yl-wrapper',
        slideClass: '.Yl-slide',
        stylePrefix: '.Yl-',
        slideLength: 4,
        pages: true,
        pagination: '.Yl-pagination',
        pagingSelect: '.Yl-pagination-bullet-active',
        autoplay: 30000,
        imgTemplate: {
            0: ['<div class="banner" title="2022年江苏省研究生工商管理案例大赛开幕式暨晋级赛" onclick="window.open(\'None\',\'_blank\')" style="background: url(../images/001.jpg)no-repeat top center/100% 100%;"></div>'],
            1: ['<div class="banner" title="河海大学工程管理硕士（MEM）PMI-GAC国际再认证" onclick="window.open(\'None\',\'_blank\')" style="background: url(../images/002.png)no-repeat top center/100% 100%;"></div>'],
            2: ['<div class="banner" title="毕业照" onclick="window.open(\'None\',\'_blank\')" style="background: url(../images/003.jpg)no-repeat top center/100% 100%;"></div>'],
            3: ['<div class="banner" title="AMBA&amp;BGA认证" onclick="window.open(\'None\',\'_blank\')" style="background: url(../images/004.jpg)no-repeat top center/100% 100%;"></div>'],
        },
        fontTemplate: {
            0: ['<div class="Yl-font0" style="color:"></div>', '<div class="Yl-font1" style="color:"></div>', '<div class="Yl-font2" style="color:"></div>'],
            1: ['<div class="Yl-font0" style="color:"></div>', '<div class="Yl-font1" style="color:"></div>', '<div class="Yl-font2" style="color:"></div>'],
            2: ['<div class="Yl-font0" style="color:"></div>', '<div class="Yl-font1" style="color:"></div>', '<div class="Yl-font2" style="color:"></div>'],
            3: ['<div class="Yl-font0" style="color:"></div>', '<div class="Yl-font1" style="color:"></div>', '<div class="Yl-font2" style="color:"></div>'],
        },
        fontAnimationMode: {
            0: ['fadeInUp', 'fadeInUp', 'fadeInUp'],
            1: ['fadeInUp', 'fadeInUp', 'fadeInUp'],
            2: ['fadeInUp', 'fadeInUp', 'fadeInUp'],
            3: ['fadeInUp', 'fadeInUp', 'fadeInUp'],
        },
        callback: function (e) {
            // 在轮播图初始化后的回调
        },
        before: function (e) {
            // 在轮播切换之前的回调
        },
        after: function (e) {
            // 在轮播切换之后的回调
        }
    });
});
