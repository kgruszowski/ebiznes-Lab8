import React, {Component} from "react";
import {Link} from "react-router-dom";

class WishlistList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customer: 1,
            wishlistedProducts: ""
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/wishlists/customer/" + this.state.customer;

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
            let wishlistedProducts = data.map((wishlistedProduct) => {
                let product = wishlistedProduct[1];
                return (
                    <li key={product.id}>
                        <Link to={"/product/" + product.id}>{product.name}</Link>
                    </li>
                )
            })
            this.setState({wishlistedProducts: wishlistedProducts})
        })
    }

    render() {
        return (
            <div className="item-container">
                <div className="container">
                    <h3>Products on my wishlist</h3>
                    <ul>
                        {this.state.wishlistedProducts}
                    </ul>
                </div>
            </div>

        )
    }
}

export default WishlistList;
