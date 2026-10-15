import { chromium } from 'playwright';
import assert from 'node:assert/strict';

const [base, output] = process.argv.slice(2);
if (!base || !output) throw new Error('需要真实部署 URL 和截图路径');
const browser = await chromium.launch({ headless: true, channel: process.env.PLAYWRIGHT_CHANNEL });
try {
    const page = await browser.newPage({ viewport: { width: 1280, height: 850 } });
    const errors = [];
    const apiRequests = [];
    page.on('pageerror', error => errors.push(error.message));
    page.on('request', request => { if (request.url().includes('/api/data')) apiRequests.push(request.url()); });
    await page.goto(`${base}/index.html`);
    assert.equal(await page.title(), '河海大学商学院');
    await page.getByText('最新动态', { exact: true }).waitFor();
    await page.getByText('通知公告', { exact: true }).waitFor();
    await page.getByText('已验证新闻', { exact: false }).first().waitFor();
    await page.getByText('已验证通知', { exact: false }).first().waitFor();
    await page.getByText('已验证栏目', { exact: false }).first().waitFor();
    assert.ok(apiRequests.length >= 3);
    assert.ok(apiRequests.every(url => url.startsWith(`${base}/api/data?`)));
    const banners = await page.locator('.Yl-slide .banner').evaluateAll(elements =>
        elements.map(element => getComputedStyle(element).backgroundImage.match(/^url\(["']?(.*?)["']?\)$/)?.[1]));
    assert.equal(banners.length, 4);
    for (const url of banners) {
        assert.ok(url?.startsWith(`${base}/images/`), `轮播图片越过应用上下文：${url}`);
        await page.evaluate(async imageUrl => {
            const image = new Image();
            image.src = imageUrl;
            await image.decode();
        }, url);
    }
    await page.screenshot({ path: `${output}-portal.png`, fullPage: true });
    await page.goto(`${base}/login.jsp`);
    await page.locator('#uname').fill('verification_admin');
    await page.locator('#upwd').fill('verification-password-20261015');
    await Promise.all([page.waitForURL('**/servlets/index.htm'), page.getByRole('button', { name: '登录', exact: true }).click()]);
    assert.ok((await page.content()).includes('verification_admin'));
    await page.screenshot({ path: `${output}-admin.png`, fullPage: true });
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto(`${base}/login.jsp`);
    assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
    await page.screenshot({ path: `${output}-login-mobile.png`, fullPage: true });
    assert.deepEqual(errors, []);
    console.log(JSON.stringify({ base, apiRequests: apiRequests.length, staticChinese: 'passed', banners: banners.length, login: 'passed', mobileLogin: 'passed' }));
} finally { await browser.close(); }
