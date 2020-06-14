import React, {Component} from "react";
import {Link} from "react-router-dom";

class CustomerDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customer: [],
            customerId: props.match.params.id,
        }
    }

    componentDidMount() {
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

    render() {
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