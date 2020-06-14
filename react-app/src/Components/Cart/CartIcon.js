import React, {Component} from "react";
import {Link} from "react-router-dom";

class CartIcon extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customerId: 1,
            icon: ""
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/cart/customer/" + this.state.customerId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            })
            .then(data => {
                let products = data.products;
                let icon = (
                    <li className="nav-item pill-right" key='cart'>
                        <Link className="nav-link" to="/cart">Cart ({products.length}) </Link>
                    </li>
                )
                this.setState({icon: icon})
            })
            .catch(error => {
                let icon = (
                    <li className="nav-item pill-right" key='cart'>
                        <Link className="nav-link" to="/cart">Cart (0)</Link>
                    </li>
                )
                this.setState({icon: icon})
            })
    }

    render() {
        return (
            this.state.icon
        )
    }
}

export default CartIcon;