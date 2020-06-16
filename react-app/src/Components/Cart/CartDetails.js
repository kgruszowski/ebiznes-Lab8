import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";
import UserUtils from "../User/UserUtils";

class CartDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customerId: null,
            cartId: null,
            products: [],
            productIds: [],
            discounts: [],
            discountId: null,
            discountValue: 0,
            shipping: [],
            shippingId: null,
            shippingPrice: 0,
            orderId: null,
            unauthorizedError: false
        }

        this.removeFromCart = this.removeFromCart.bind(this);
        this.handleDiscountChange = this.handleDiscountChange.bind(this);
        this.handleShippingChange = this.handleShippingChange.bind(this);
        this.placeOrder = this.placeOrder.bind(this);
    }

    placeOrder(event) {
        event.preventDefault();

        var data = {
            "id": 0,
            "cart": parseInt(this.state.cartId),
            "shippingMethod": parseInt(this.state.shippingId),
            "discount": parseInt(this.state.discountId)
        };
        var url = 'http://localhost:9000/api/order';

        fetch(url, {
            method: 'POST',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                orderId: responseData.order.id
            });
        });
    }

    removeFromCart(index) {
        let products = [...this.state.products]; // make a separate copy of the array
        let productIds = [...this.state.productIds]; // make a separate copy of the array
        products.splice(index, 1);
        productIds.splice(index, 1);
        this.setState({
            productIds: productIds,
            products: products
        }, () => {
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
        });
    }

    fetchDiscounts() {
        let url = "http://localhost:9000/api/discounts"

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let discounts = data.map((discount) => {
                return {
                    id: discount.id,
                    code: discount.code,
                    value: discount.value
                }
            })
            this.setState({discounts: discounts})
        })
    }

    fetchShippingMethods() {
        let url = "http://localhost:9000/api/shipping-methods"

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let shipping = data.map((method) => {
                return {
                    id: method.id,
                    name: method.name,
                    price: method.price
                }
            })
            this.setState({shipping: shipping})
        })
    }

    handleDiscountChange(event) {
        let chosenDiscount = this.state.discounts.find((discount) => {
            return discount.id == event.target.value;
        })

        this.setState({
            discountId: chosenDiscount.id,
            discountValue: chosenDiscount.value
        });
    }

    handleShippingChange(event) {
        let chosenMethod = this.state.shipping.find((method) => {
            return method.id == event.target.value;
        })

        if (chosenMethod !== undefined) {
            this.setState({
                shippingId: chosenMethod.id,
                shippingPrice: parseFloat(chosenMethod.price.toFixed(2))
            });
        }
    }

    fetchCartContent(){
        let url = "http://localhost:9000/api/cart/customer/" + this.state.customerId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                if (results.status !== 404) {
                    return results.json();
                }

                return null
            })
            .then(data => {
                if (data == null) {
                    return
                }

                this.setState({cartId: data.cart.id})
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
                            productIds: this.state.productIds.concat(product.id)
                        })
                    });
                })
            }).catch(error => this.setState({unauthorizedError: true}))
    }

    componentDidMount() {
        UserUtils.getUserId().then(userId => {
            if (userId === null) {
                this.setState({unauthorizedError: true})
            } else {
                this.setState({customerId: userId})
                this.fetchCartContent()
                this.fetchDiscounts()
                this.fetchShippingMethods()
            }
        })
    }

    calculateCartValue() {
        return parseFloat(this.state.products.reduce((totalPrice, product) => totalPrice + product.price, 0).toFixed(2))
    }

    calculateTotal() {
        let cartValue = this.calculateCartValue();

        return (cartValue - cartValue * parseFloat(this.state.discountValue) / 100 + parseFloat(this.state.shippingPrice)).toFixed(2)
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

        if (this.state.orderId !== null) {
            return <Redirect to={"/order/" + this.state.orderId}/>
        }

        return (
            <div>
                <h3>Cart content</h3>
                <ul>
                    {this.state.products.map((product, i) => {
                        return (
                            <li key={product.id}>
                                <Link to={"/product/" + product.id}>{product.name} -
                                    $ {parseFloat(product.price.toFixed(2))}</Link>
                                <span className="oi oi-x ml-3" onClick={this.removeFromCart.bind(null, i)}/>
                            </li>
                        )
                    })}
                </ul>
                <hr/>
                <h5>Cart value:
                    $ {this.calculateCartValue()}</h5>
                <hr/>
                <h5>Select discount</h5>
                <select className="form-control" name="discount" id="discount" onChange={this.handleDiscountChange}>
                    <option value="" name="discount">---Choose discount---</option>
                    {this.state.discounts.map((discount) => {
                        return <option value={discount.id} name="discount">{discount.code} - {discount.value}%</option>
                    })}
                </select>
                <hr/>
                <h5>Select shipping method</h5>
                <select className="form-control" name="shipping" id="shipping" onChange={this.handleShippingChange}>
                    <option value="" name="shipping">---Choose shipping method---</option>
                    {this.state.shipping.map((method) => {
                        return <option value={method.id} name="discount">{method.name} - ${method.price.toFixed(2)}</option>
                    })}
                </select>
                <hr/>
                <h5>Total:
                    $ {this.calculateTotal()}</h5>
                <hr/>
                <button className="btn btn-lg btn-success" onClick={this.placeOrder}>Buy!</button>
            </div>
        )
    }
}

export default CartDetails;