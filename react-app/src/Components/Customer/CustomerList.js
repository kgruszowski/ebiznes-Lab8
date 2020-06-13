import React, {Component} from "react";
import {Link} from "react-router-dom";

class CustomerList extends Component {

    constructor() {
        super();
        this.state = {
            customers: [],
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/customers"

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
            let customers = data.map((customer) => {
                return (
                    <li key={customer.id}>
                        <Link to={"/customer/" + customer.id}>{customer.firstname} {customer.surname}</Link>
                    </li>
                )
            })
            this.setState({customers: customers})
        })
    }

    render() {
        return (
            <div className="container">
                <div className="customers">
                    <h3>List of all customers</h3>
                    <ul>
                        {this.state.customers}
                    </ul>
                </div>
                <Link to="/customer/add" className="btn btn-md btn-success">Add new customer</Link>
            </div>
        )
    }
}

export default CustomerList;