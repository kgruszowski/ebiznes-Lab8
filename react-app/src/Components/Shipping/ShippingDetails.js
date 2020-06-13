import React, {Component} from "react";
import {Link} from "react-router-dom";

class ShippingDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            shipping: [],
            shippingId: props.match.params.id,
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/shipping-method/" + this.state.shippingId;

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
            let shippingDetails = (
                <div>
                    <div className="col-md-7">
                        <div>Id: {data.shippingMethod.id}</div>
                        <div>Name: {data.shippingMethod.name}</div>
                        <div>Delivery Time: {data.shippingMethod.deliveryTime}</div>
                        <div>Price: {parseFloat(data.shippingMethod.price.toFixed(2))}</div>
                    </div>
                    <hr></hr>
                    <div className="btn-group wishlist">
                        <Link to={"/shipping/edit/" + this.state.shippingId}
                              className="btn btn-sm btn-success">Edit</Link>
                    </div>
                </div>
            )
            this.setState({shipping: shippingDetails})
        })
    }

    render() {
        return (
            <div className="item-container">
                <div className="container">
                    {this.state.shipping}
                </div>
            </div>

        )
    }
}

export default ShippingDetails;