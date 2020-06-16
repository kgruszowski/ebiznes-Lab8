import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";
import UserUtils from "../User/UserUtils";

class WishlistList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customer: 1,
            wishlistedProducts: "",
            unauthorizedError: false
        }
    }

    fetchCustomerWishlists(id) {
        let url = "http://localhost:9000/api/wishlists/customer/" + id;

        fetch(url, {
            mode: 'cors',
            headers: {},
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
        }).catch(error => this.setState({unauthorizedError: true}))
    }

    componentDidMount() {
        UserUtils.getUserId().then(userId => {
            if (userId === null) {
                this.setState({unauthorizedError: true})
            } else {
                this.setState({customerId: userId})
                this.fetchCustomerWishlists(userId)
            }
        })
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

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
