import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class InventoryEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            productId: props.match.params.id,
            quantity: 0,
            available: 0,
            inventoryCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/inventory/product/" + this.state.productId;
        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            this.setState({
                id: data.inventory.id,
                quantity: data.inventory.quantity,
                available: data.inventory.available,
            })
            let inputs = (
                <div>
                    <InputComponent label="Quantity" name="quantity" value={this.state.quantity}
                                    onChangeCallback={this.handleInputChange}/>
                    <InputComponent label="available" name="available" value={this.state.available}
                                    onChangeCallback={this.handleInputChange}/>
                </div>
            )
            this.setState({inputs: inputs})
        });
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "product": parseInt(this.state.productId),
            "quantity": parseInt(this.state.quantity),
            "available": parseInt(this.state.available)
        };
        var url = 'http://localhost:9000/api/inventory/' + this.state.id;

        fetch(url, {
            method: 'PUT',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                inventoryCreated: true,
                id: responseData.inventory.id
            })
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    render() {
        if (this.state.inventoryCreated) {
            return <Redirect to={"/product/" + this.state.productId}/>
        }
        return (
            <div>
                {this.state.inputs}
                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit inventory</button>
            </div>
        );
    }
}

export default InventoryEdit;