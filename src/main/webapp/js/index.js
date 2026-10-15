new Vue({
    el: '#ibody',
    data: {
        menuItems: [],
        newsList: [],
        noticeList: [],
    },
    async mounted() {
        try {
            this.menuItems = await this.fetchData('topmenu');
            this.newsList = await this.fetchData('news');
            this.noticeList = await this.fetchData('notice');

            this.formatDateList(this.menuItems);
            this.formatDateList(this.newsList);
            this.formatDateList(this.noticeList);

        } catch (error) {
            PortalConfig.reportError(error);
        }
    },
    methods: {
        async fetchData(dataType) {
            const response = await axios.get(PortalConfig.apiUrl, {
                params: { dataType },
            });
            return response.data;
        },
        formatDateList(list) {
            list.forEach(item => {
                item.createTime = this.formatDate(item.createTime);
            });
        },
        formatDate(dateString) {
            const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
            return new Date(dateString).toLocaleDateString(undefined, options).replace(/\//g, '-');
        },
        handleMenuClick(item) {
            const fatherId = item.Id;
            const newPageUrl = `index2.html?dataType=leftmenu&fatherId=${fatherId}`;
            window.open(newPageUrl, '_blank');
        }
    },
});
