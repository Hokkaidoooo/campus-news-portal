// 页面与 API 都以此脚本所属应用为基准，避免在具名部署时跳出上下文。
const portalBaseUrl = new URL('../', document.currentScript.src);
window.PortalConfig = Object.freeze({
    apiUrl: new URL('api/data', portalBaseUrl).href,
    articleUrl(type, id) {
        const url = new URL('article', portalBaseUrl);
        url.searchParams.set('type', type);
        url.searchParams.set('id', id);
        return url.href;
    },
    reportError(error) {
        console.error('门户内容加载失败', error);
        let message = document.getElementById('portal-error');
        if (!message) {
            message = document.createElement('p');
            message.id = 'portal-error';
            message.setAttribute('role', 'alert');
            message.style.cssText = 'padding:16px;margin:16px;background:#fff1ef;color:#962d22';
            document.body.prepend(message);
        }
        message.textContent = '内容加载失败，请稍后重试。';
    },
});
