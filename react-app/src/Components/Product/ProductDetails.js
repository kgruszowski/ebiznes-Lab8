import React, {Component} from "react";
import {Redirect, Link} from "react-router-dom";
import ReviewList from "../Review/ReviewList";
import {WishlistSwitcher} from "../Wishlist";
import {CartAdd} from "../Cart";

class ProductDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            product: null,
            productId: props.match.params.id,
            productDeleted: false,
            inventoryDetails: ""
        }
        this.removeProduct = this.removeProduct.bind(this);
    }

    removeProduct(event) {
        let url = "http://localhost:9000/api/product/" + this.state.productId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'DELETE',
        }).then(result => {
            this.setState({productDeleted: true})
        })
    }

    getProduct() {
        let url = "http://localhost:9000/api/product/" + this.state.productId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            this.setState({product: data.product})
        })
    }

    getInventoryForProduct() {
        let url = "http://localhost:9000/api/inventory/product/" + this.state.productId;

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let availability;
            if (data.inventory.available > 0) {
                availability = "In stock"
            } else {
                availability = "Out of stock"
            }
            let inventoryDetails = (
                <div>
                    <div className="product-stock">{availability} - available {data.inventory.quantity} out
                        of {data.inventory.available}</div>
                    <Link to={"/inventory/edit/product/" + this.state.productId} className="btn btn-sm btn-success">Edit
                        inventory</Link>
                </div>
            )
            this.setState({inventoryDetails: inventoryDetails})
        }).catch(err => {
            let inventoryDetails = (
                <div>
                    <div>No inventory data</div>
                    <Link to={"/inventory/add/product/" + this.state.productId} className="btn btn-sm btn-success">Add
                        inventory</Link>
                </div>
            )
            this.setState({inventoryDetails: inventoryDetails})
        })
    }

    componentDidMount() {
        this.getProduct();
        this.getInventoryForProduct()
    }

    render() {
        if (this.state.productDeleted) {
            return <Redirect to="/products"/>
        }

        if (this.state.product !== null) {
            return (
                <div className="item-container">
                    <div className="container">
                        <div>
                            <div className="col-md-12">
                                <div className="product col-md-3 service-image-left">
                                    <img height="350" id="item-display" src={this.state.product.photo} alt=""></img>
                                </div>
                            </div>

                            <div className="col-md-7">
                                <div className="product-title">{this.state.product.name}</div>
                                <div className="product-desc">{this.state.product.description}</div>
                                <div className="product-rating"><i className="fa fa-star gold"></i> <i
                                    className="fa fa-star gold"></i> <i className="fa fa-star gold"></i> <i
                                    className="fa fa-star gold"></i> <i className="fa fa-star-o"></i></div>
                                <hr></hr>
                                <div className="product-price">$ {parseFloat(this.state.product.price.toFixed(2))}</div>
                                {this.state.inventoryDetails}
                                <hr/>
                                <h3>Reviews</h3>
                                <ReviewList productId={this.state.product.id}/>
                                <div>
                                    <WishlistSwitcher productId={this.state.product.id}/>
                                    <CartAdd productId={this.state.product.id}/>
                                    <Link to={"/review/add/product/" + this.state.product.id}
                                          className="btn btn-md btn-warning mr-1">
                                        Add review
                                    </Link>
                                    <button type="button" className="btn btn-danger mr-1" onClick={this.removeProduct}>
                                        Remove
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            )
        } else {
            return (
                <div>Product details</div>
            )
        }
    }
}

export default ProductDetails;