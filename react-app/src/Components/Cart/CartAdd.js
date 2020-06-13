import React, {Component} from "react";

class CartAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            productId: props.productId,
            productIds: [],
            cartId: null,
            customerId: 1
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
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
            },
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
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
            },
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

    componentDidMount() {
        let url = "http://localhost:9000/api/cart/customer/" + this.state.customerId;

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
            })
            .then(data => {
                this.setState({
                    productIds: data.products,
                    cartId: data.cart.id
                })
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