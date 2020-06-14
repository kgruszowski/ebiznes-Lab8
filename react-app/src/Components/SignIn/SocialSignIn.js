import React, {Component} from "react";

class SocialSignIn extends Component{
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }

    render() {
        return (
            <div className="container">
                <h3>Sign in </h3>
                <div>
                    <a className="btn btn-md btn-danger" href="http://localhost:9000/authenticate/google">Google</a>
                </div>
                <hr/>
                <div>
                    <a className="btn btn-md btn-info" href="http://localhost:9000/authenticate/github">Github</a>
                </div>
            </div>
        )
    }
}

export default SocialSignIn;