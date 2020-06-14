import fetchIntercept from 'fetch-intercept';

export const unregister = fetchIntercept.register({
    request: function (url, config) {
        // Modify the url or config here
        config.headers['X-Auth-Token'] = localStorage.getItem('accessToken')
        config.headers['Accept'] = 'application/json; charset=utf-8'
        config.headers['Content-Type'] = 'application/json; charset=utf-8'
        config.headers['Access-Control-Allow-Origin'] = 'http://localhost:3000'
        return [url, config];
    },

    requestError: function (error) {
        // Called when an error occured during another 'request' interceptor call
        console.log("eteeqtqt")
        return Promise.reject(error);
    },

    response: function (response) {
        // Modify the reponse object
        return response;
    },

    responseError: function (error) {
        return Promise.reject(error);
    }
});