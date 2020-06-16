import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";
import UserUtils from "../User/UserUtils"

class CustomerDetails extends Component {
    constructor(props) {
        super(props);

        this.state = {
            customer: [],
            customerId: props.match.params.id,
            unauthorizedError: false
        }
    }

    fetchCustomerData() {
        let url = "http://localhost:9000/api/customer/" + this.state.customerId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let customerDetails = (
                <div>
                    <div className="col-md-7">
                        <div>Id: {data.customer.id}</div>
                        <div>Name: {data.customer.firstname}</div>
                        <div>Surname: {data.customer.surname}</div>
                        <div>Address: {data.customer.address}</div>
                    </div>
                    <hr></hr>
                    <div className="btn-group wishlist">
                        <Link to={"/customer/edit/" + this.state.customerId}
                              className="btn btn-sm btn-success">Edit</Link>
                    </div>
                </div>
            )
            this.setState({customer: customerDetails})
        })
    }

    componentDidMount() {
        if (typeof this.state.customerId === "undefined") {
            UserUtils.getUserId().then(userId => {
                if (userId === null) {
                    this.setState({unauthorizedError: true})
                } else {
                    this.setState({customerId: userId})
                    this.fetchCustomerData()
                }
            })
        } else {
            this.fetchCustomerData(this.state.customerId)
        }
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

        return (
            <div className="item-container">
                <div className="container">
                    {this.state.customer}
                </div>
            </div>

        )
    }
}

export default CustomerDetails;