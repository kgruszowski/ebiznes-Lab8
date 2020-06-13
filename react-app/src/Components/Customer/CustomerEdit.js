import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class CustomerEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            firstname: "",
            surname: "",
            address: "",
            customerEdited: false
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
        var url = 'http://localhost:9000/api/customer/' + this.state.id;

        fetch(url, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(result => {
            this.setState({productEdited: true})
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    async getcustomer() {
        let url = "http://localhost:9000/api/customer/" + this.state.id;
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
                id: data.customer.id,
                firstname: data.customer.firstname,
                surname: data.customer.surname,
                address: data.customer.address,
            })
            let inputs = (
                <div>
                    <InputComponent label="Firstname" name="firstname" value={this.state.firstname}
                                     onChangeCallback={this.handleInputChange}/>
                    <InputComponent label="Surname" name="surname" value={this.state.surname}
                                    onChangeCallback={this.handleInputChange}/>
                    <InputComponent label="Address" name="address" value={this.state.address}
                                    onChangeCallback={this.handleInputChange}/>
                </div>
            )
            this.setState({inputs: inputs})
        });
    }

    componentDidMount() {
        this.getcustomer();
    }

    render() {
        if (this.state.productEdited) {
            return <Redirect to={"/customer/" + this.state.id}/>
        }
        return (
            <div>
                {this.state.inputs}
                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit customer</button>
            </div>
        );
    }
}

export default CustomerEdit;