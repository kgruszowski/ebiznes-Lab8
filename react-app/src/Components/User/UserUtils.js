export default {
    getUserId: () => {
        let url = "http://localhost:9000/api/get-user";

        return fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
                return data.userId
        }).catch(error => null)
    }
}