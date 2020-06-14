import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class ShippingAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            name: "",
            deliveryTime: "",
            price: "",
            shippingCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "name": this.state.name,
            "deliveryTime": this.state.deliveryTime,
            "price": parseFloat(this.state.price)
        };
        var url = 'http://localhost:9000/api/shipping-method';

        fetch(url, {
            method: 'POST',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                shippingCreated: true,
                id: responseData.shippingMethod.id
            })
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    render() {
        if (this.state.shippingCreated) {
            return <Redirect to={"/shipping/" + this.state.id}/>
        }
        return (
            <div>
                <InputComponent label="Shipping name" name="name" value={this.state.name}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Delivery Time" name="deliveryTime" value={this.state.deliveryTime}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Price" name="price" value={this.state.price}
                                onChangeCallback={this.handleInputChange}/>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Add shipping</button>
            </div>
        );
    }
}

export default ShippingAdd;