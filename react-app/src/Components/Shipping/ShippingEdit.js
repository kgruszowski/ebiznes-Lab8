import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class ShippingEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            name: "",
            deliveryTime: "",
            price: "",
            shippingEdited: false
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
        var url = 'http://localhost:9000/api/shipping-method/' + this.state.id;

        fetch(url, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(result => {
            this.setState({shippingEdited: true})
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    async getshipping() {
        let url = "http://localhost:9000/api/shipping-method/" + this.state.id;
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
            this.setState({
                id: data.shippingMethod.id,
                name: data.shippingMethod.name,
                deliveryTime: data.shippingMethod.deliveryTime,
                price: data.shippingMethod.price,
            })
            let inputs = (
                <div>
                    <InputComponent label="Shipping name" name="name" value={this.state.name}
                                    onChangeCallback={this.handleInputChange}/>

                    <InputComponent label="Delivery Time" name="deliveryTime" value={this.state.deliveryTime}
                                    onChangeCallback={this.handleInputChange}/>

                    <InputComponent label="Price" name="price" value={this.state.price}
                                    onChangeCallback={this.handleInputChange}/>
                </div>
            )
            this.setState({inputs: inputs})
        });
    }

    componentDidMount() {
        this.getshipping();
    }

    render() {
        if (this.state.shippingEdited) {
            return <Redirect to={"/shipping/" + this.state.id}/>
        }
        return (
            <div>
                {this.state.inputs}
                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit shipping</button>
            </div>
        );
    }
}

export default ShippingEdit;