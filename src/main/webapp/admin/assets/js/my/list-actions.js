// 服务端只输出已转义的数据属性；数据不进入可执行的 JavaScript 字符串。
const listSettings = document.getElementById('page-settings');
const listKind = listSettings.getAttribute('data-kind');
pubSelected('fy_pagesize', listSettings.getAttribute('data-size'));
pubSelected('fy_order', listSettings.getAttribute('data-order'));
pubSelected('fy_sort', listSettings.getAttribute('data-sort'));
fysz(listSettings.getAttribute('data-now'), `servlets/${listKind}/drop.htm`,
    `servlets/${listKind}/load.htm`, `servlets/${listKind}/list.htm`);
document.addEventListener('click', function (event) {
    const link = event.target.closest('[data-edit-id], [data-delete-id]');
    if (!link) return;
    event.preventDefault();
    if (link.hasAttribute('data-edit-id')) fybj(link.getAttribute('data-edit-id'));
    else fysc(link.getAttribute('data-delete-id'));
});
