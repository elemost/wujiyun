export default {
    install(app) {
        app.config.globalProperties.$test = function (params) {
            console.log(params);
        };
    }
}