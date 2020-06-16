//utils
export default {
    getUserId: () => {
        let url = "http://localhost:9000/api/get-user";
        let userId = null

        return fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            // console.log("user id after fetch " + data.userId)
            // userId = data.userId
                return data.userId
        }).catch(error => null)

        // fetchedUser.then()
        // console.log("return " + userId)
        // return userId;
    }
}