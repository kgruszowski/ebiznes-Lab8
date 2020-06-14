import React, {Component} from "react";

class OrderDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            products: [],
            cartId: null,
            shipping: null,
            discount: null,
        }
    }

    fetchOrderDetails() {
        let url = "http://localhost:9000/api/order/" + this.state.id

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
                this.setState({
                    shipping: data.order[1],
                    discount: data.order[2],
                })
            this.fetchProductsList(data.order[0].cart)
        })
    }

    fetchProductsList(cartId) {
        let url = "http://localhost:9000/api/cart/" + cartId

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            data.products.map((id) => {
                let productUrl = "http://localhost:9000/api/product/" + id;

                fetch(productUrl, {
                    mode: 'cors',
                    headers: {},
                    method: 'GET',
                })
                    .then(results => {
                        return results.json();
                    }).then(responseData => {
                    let product = responseData.product
                    this.setState({
                        products: this.state.products.concat(product),
                    })
                });
            })
        })
    }

    calculateCartValue() {
        return parseFloat(this.state.products.reduce((totalPrice, product) => totalPrice + product.price, 0).toFixed(2))
    }

    calculateTotal() {
        let cartValue = this.calculateCartValue();
        let discount = this.state.discount !== null ? this.state.discount.value : 0;
        let shippingPrice = this.state.shipping !== null ? this.state.shipping.price : 0;

        return (cartValue - cartValue * parseFloat(discount) / 100 + parseFloat(shippingPrice)).toFixed(2)
    }

    componentDidMount() {
        this.fetchOrderDetails()
    }

    render() {
        return (
            <div>
                <h5>Order details</h5>
                <ol>
                    {this.state.products.map((product) => {
                        return (
                            <li>
                                {product.name} - ${product.price.toFixed(2)}
                            </li>
                        )
                    })}
                </ol>
                <hr/>
                <p>Discount applied <b>{this.state.discount !== null ? this.state.discount.code + " " + this.state.discount.value + "%" : "---"}</b></p>
                <p>Shipping method <b>{this.state.shipping !== null ? this.state.shipping.name + " $" + this.state.shipping.price.toFixed(2) : ""}</b></p>
                <hr/>
                <h5>Total:</h5>
                ${this.calculateTotal()}
            </div>
        )

    }
}

export default OrderDetails;