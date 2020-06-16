import React, {Component} from "react";
import {Redirect} from "react-router-dom";

class SocialSignOut extends Component{
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        localStorage.removeItem("accessToken")
    }

    render() {
        return <Redirect to={"/sign-in"}/>
    }
}

export default SocialSignOut