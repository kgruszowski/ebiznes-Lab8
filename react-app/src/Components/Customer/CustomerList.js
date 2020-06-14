import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";

class CustomerList extends Component {

    constructor() {
        super();
        this.state = {
            customers: [],
            unauthorizedError: false
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/customers"

        fetch(url, {
            mode: 'cors',
            headers: {},
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
        }).catch(error => this.setState({unauthorizedError: true}))
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

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