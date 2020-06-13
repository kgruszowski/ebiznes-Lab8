import React, {Component} from "react";
import {Link} from "react-router-dom";

class OrderList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customerId: 1,
            orders: []
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/orders/customer/" + this.state.customerId

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
            let orders = data.map((order) => {
                return {
                    id: order[0].id
                }
            })
            this.setState({orders: orders})
        })
    }

    render() {
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