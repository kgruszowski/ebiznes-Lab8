import React, {Component} from "react";
import {Link} from "react-router-dom";

class ShippingList extends Component {

    constructor() {
        super();
        this.state = {
            shipping: [],
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/shipping-methods"

        fetch(url, {
            mode: 'cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
            },
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
        })
    }

    render() {
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