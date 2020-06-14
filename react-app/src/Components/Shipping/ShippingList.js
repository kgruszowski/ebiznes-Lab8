import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";

class ShippingList extends Component {

    constructor() {
        super();
        this.state = {
            shipping: [],
            unauthorizedError: false
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/shipping-methods"

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let methods = data.map((method) => {
                return (
                    <li key={method.id}>
                        <Link to={"/shipping/" + method.id}>{method.name}</Link>
                    </li>
                )
            })
            this.setState({shipping: methods})
        }).catch(error => this.setState({unauthorizedError: true}))
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

        return (
            <div className="container">
                <div className="shippings">
                    <h3>List of all available shipping methods</h3>
                    <ul>
                        {this.state.shipping}
                    </ul>
                </div>
                <Link to="/shipping/add" className="btn btn-md btn-success">Add new shipping method</Link>
            </div>
        )
    }
}

export default ShippingList;