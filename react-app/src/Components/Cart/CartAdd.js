import React, {Component} from "react";
import UserUtils from "../User/UserUtils";

class CartAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            productId: props.productId,
            productIds: [],
            cartId: null,
            customerId: null
        }

        this.addToCart = this.addToCart.bind(this);
    }

    updateCart() {
        let data = {
            products: this.state.productIds
        }

        let url = "http://localhost:9000/api/cart/" + this.state.cartId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'PUT',
            body: JSON.stringify(data),
        })
            .then(results => {
                return results.json();
            })
            .then(responseData => {

            })
    }

    createCart() {
        let data = {
            cart: {id: 0, customer: this.state.customerId, enabled: true},
            products: this.state.productIds
        }

        let url = "http://localhost:9000/api/cart";

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'POST',
            body: JSON.stringify(data),
        })
            .then(results => {
                return results.json();
            })
            .then(responseData => {
                this.setState({cartId: responseData.cart.id})
            })
    }

    addToCart(event) {
        event.preventDefault();

        this.setState({
            productIds: this.state.productIds.concat(this.state.productId)
        }, () => {
            if (this.state.cartId !== null) {
                this.updateCart()
            } else {
                this.createCart()
            }
        })
    }

    fetchCustomerCartContent() {
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
                this.setState({
                    productIds: data.products,
                    cartId: data.cart.id
                })
            })
    }

    componentDidMount() {
        UserUtils.getUserId().then(userId => {
            if (userId === null) {
                this.setState({unauthorizedError: true})
            } else {
                this.setState({customerId: userId})
                this.fetchCustomerCartContent()
            }
        })
    }

    render() {
        return (
            <button type="button" className="btn btn-md btn-info mr-1" onClick={this.addToCart}>
                Add to Cart
            </button>
        )
    }

}

export default CartAdd;