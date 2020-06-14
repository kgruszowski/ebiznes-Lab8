import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class InventoryAdd extends Component {
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

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "product": parseInt(this.state.productId),
            "quantity": parseInt(this.state.quantity),
            "available": parseInt(this.state.available)
        };
        var url = 'http://localhost:9000/api/inventory';

        fetch(url, {
            method: 'POST',
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

    render() {
        if (this.state.inventoryCreated) {
            return <Redirect to={"/product/" + this.state.productId}/>
        }
        return (
            <div>
                <InputComponent label="Quantity" name="quantity" value={this.state.quantity}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Available" name="available" value={this.state.available}
                                onChangeCallback={this.handleInputChange}/>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Create inventory</button>
            </div>
        );
    }
}

export default InventoryAdd;