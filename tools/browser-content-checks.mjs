import assert from 'node:assert/strict';

const script = '<script>document.documentElement.dataset.portalXss="executed"</script>';
const title = '引号"与 & 符号 <b>按原文保存</b>';
const content = `第一行：${script}\n第二行："引号" & </textarea><img src=x onerror="document.documentElement.dataset.portalXss='executed'">`;

export async function verifySafeEditing(page, base) {
    const results = [];
    for (const kind of ['news', 'notice', 'topmenu', 'leftmenu']) {
        const listUrl = `${base}/servlets/${kind}/list.htm`;
        const response = await page.request.post(`${base}/servlets/${kind}/modify.htm`, {
            form: { [`${kind}Name`]: title, [`${kind}Content`]: content, [kind === 'topmenu' ? 'id' : `${kind}ID`]: '' },
        });
        assert.equal(response.status(), 200, `${kind} 保存失败`);
        await page.goto(listUrl);
        assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null, `${kind} 列表执行了正文中的脚本`);
        const row = page.locator('tbody tr').filter({ has: page.getByRole('cell', { name: title, exact: true }) }).first();
        await row.waitFor();
        assert.ok((await row.textContent()).includes(content), `${kind} 列表未保留原文`);
        await row.getByRole('link', { name: '修改', exact: true }).click();
        await page.waitForURL(url => url.pathname.endsWith('/load.htm'));
        assert.equal(await page.locator(`[name="${kind}Name"]`).inputValue(), title);
        assert.equal(await page.locator(`[name="${kind}Content"]`).inputValue(), content);
        assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null, `${kind} 编辑页执行了正文中的脚本`);
        const id = await page.locator(`[name="${kind === 'topmenu' ? 'id' : `${kind}ID`}"]`).inputValue();
        await Promise.all([page.waitForURL('**/modify.htm'), page.getByRole('button', { name: '提交', exact: true }).click()]);
        assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null);
        if (kind === 'news' || kind === 'notice') {
            const detail = await page.goto(`${base}/article?type=${kind}&id=${encodeURIComponent(id)}`);
            assert.equal(detail.status(), 200);
            assert.equal(await page.locator('h1').textContent(), title);
            assert.equal(await page.locator('.article-content').textContent(), content);
            assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null);
        }
        await page.request.get(`${base}/servlets/${kind}/drop.htm?id=${encodeURIComponent(id)}`);
        results.push(kind);
    }
    const search = '" onfocus="document.documentElement.dataset.portalXss=1" & <svg>';
    await page.goto(`${base}/servlets/news/list.htm?x_name=${encodeURIComponent(search)}`);
    await page.locator('#fy_name').focus();
    assert.equal(await page.locator('#fy_name').inputValue(), search);
    assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null);
    await Promise.all([page.waitForURL(url => url.searchParams.get('x_name') === search), page.getByRole('button', { name: '搜索', exact: true }).click()]);
    assert.equal(new URL(page.url()).searchParams.get('x_name'), search);
    await verifyUserText(page, base);
    results.push('user');
    return results;
}

async function verifyUserText(page, base) {
    const name = '账号"><img src=x onerror="document.documentElement.dataset.portalXss=1">';
    const password = 'browser-text-password';
    const add = await page.request.post(`${base}/servlets/user/modify.htm`, {
        form: { id: '0', name, uname: title, password, role: '用户', valid: '有效' },
    });
    assert.equal(add.status(), 200);
    await page.goto(`${base}/servlets/user/list.htm`);
    assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null);
    const row = page.locator('tbody tr').filter({ has: page.getByRole('cell', { name, exact: true }) });
    await Promise.all([page.waitForURL('**/load.htm?**'), row.getByRole('link', { name: '修改', exact: true }).click()]);
    assert.equal(await page.locator('[name="name"]').inputValue(), name);
    assert.equal(await page.locator('[name="uname"]').inputValue(), title);
    assert.equal(await page.locator('[name="password"]').inputValue(), '');
    assert.equal(await page.locator('html').getAttribute('data-portal-xss'), null);
    const id = await page.locator('[name="id"]').inputValue();
    const context = await page.context().browser().newContext();
    try {
        const accountPage = await context.newPage();
        await accountPage.goto(`${base}/login.jsp`);
        await accountPage.locator('#uname').fill(name);
        await accountPage.locator('#upwd').fill(password);
        await Promise.all([accountPage.waitForURL('**/servlets/index.htm'), accountPage.getByRole('button', { name: '登录', exact: true }).click()]);
        assert.ok((await accountPage.locator('header').textContent()).includes(name));
        assert.equal(await accountPage.getByRole('link', { name: '后台账号', exact: false }).count(), 0);
        await accountPage.goto(`${base}/servlets/news/list.htm`);
        assert.ok((await accountPage.locator('.user-info').textContent()).includes(name));
        assert.equal(await accountPage.locator('html').getAttribute('data-portal-xss'), null);
        assert.equal(await accountPage.getByRole('link', { name: '用户管理', exact: true }).count(), 0);
    } finally {
        await context.close();
    }
    assert.equal((await page.request.get(`${base}/servlets/user/drop.htm?id=${id}`, { maxRedirects: 0 })).status(), 302);
}

export async function verifyReading(page, base, output) {
    const checks = [];
    for (const kind of ['news', 'notice']) {
        const titleText = kind === 'news' ? '已验证新闻' : '已验证通知';
        const bodyText = kind === 'news' ? '真实测试库新闻内容' : '真实测试库通知内容';
        const detailPath = `/article?type=${kind}&id=verified-${kind}`;
        for (const list of ['index.html', `${kind}list.html`]) {
            await page.goto(`${base}/${list}`);
            const link = page.getByRole('link', { name: titleText, exact: true });
            await link.waitFor();
            assert.equal(await link.count(), 1, `${list} 内容重复`);
            assert.equal(new URL(await link.getAttribute('href'), page.url()).href, base + detailPath);
            await Promise.all([page.waitForURL(base + detailPath), link.click()]);
            assert.equal(await page.locator('h1').textContent(), titleText);
            assert.equal(await page.locator('.article-content').textContent(), bodyText);
            assert.match((await page.locator('.published').textContent()).trim(), /^发布时间：\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/);
        }
        assert.equal((await page.goto(base + detailPath)).status(), 200);
        assert.equal((await page.reload()).status(), 200);
        await page.setViewportSize({ width: 390, height: 844 });
        assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true);
        if (kind === 'news') await page.screenshot({ path: `${output}-article-mobile.png`, fullPage: true });
        await page.setViewportSize({ width: 1280, height: 850 });
        if (kind === 'news') await page.screenshot({ path: `${output}-article.png`, fullPage: true });
        await page.getByRole('link', { name: kind === 'news' ? '返回动态列表' : '返回公告列表', exact: true }).click();
        assert.equal(page.url(), `${base}/${kind}list.html`);
        checks.push(kind);
    }
    for (const [query, status, message] of [
        ['type=news&id=missing-record', 404, '内容不存在'],
        ['type=news', 400, '阅读地址不正确'],
        ['type=user&id=1', 400, '阅读地址不正确'],
    ]) {
        assert.equal((await page.goto(`${base}/article?${query}`)).status(), status);
        assert.equal(await page.locator('h1').textContent(), message);
    }
    return checks;
}
