// API 路径随当前应用的脚本地址确定，同一 WAR 可部署在不同上下文和端口。
window.PortalConfig = Object.freeze({
    apiUrl: new URL('../api/data', document.currentScript.src).href,
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
