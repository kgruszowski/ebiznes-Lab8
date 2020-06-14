import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";

class OrderList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customerId: 1,
            orders: [],
            unauthorizedError: false
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/orders/customer/" + this.state.customerId

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let orders = data.map((order) => {
                return {
                    id: order[0].id
                }
            })
            this.setState({orders: orders})
        }).catch(error => this.setState({unauthorizedError: true}))
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

        return (
            <div>
                <h3>Cart content</h3>
                <ul>
                    {this.state.orders.map((order) => {
                        return <li><Link to={"/order/" + order.id}>Order #{order.id}</Link></li>
                    })}
                </ul>
            </div>
        );
    }
}

export default OrderList;