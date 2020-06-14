import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class CustomerAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            firstname: "",
            surname: "",
            address: "",
            customerCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "firstname": this.state.firstname,
            "surname": this.state.surname,
            "address": this.state.address,
        };
        var url = 'http://localhost:9000/api/customer';

        fetch(url, {
            method: 'POST',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                customerCreated: true,
                id: responseData.customer.id
            })
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    render() {
        if (this.state.customerCreated) {
            return <Redirect to={"/customer/" + this.state.id}/>
        }
        return (
            <div>
                <InputComponent label="Firstname" name="firstname" value={this.state.firstname}
                                onChangeCallback={this.handleInputChange}/>
                <InputComponent label="Surname" name="surname" value={this.state.surname}
                                onChangeCallback={this.handleInputChange}/>
                <InputComponent label="Address" name="address" value={this.state.address}
                                onChangeCallback={this.handleInputChange}/>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Add customer</button>
            </div>
        );
    }
}

export default CustomerAdd;