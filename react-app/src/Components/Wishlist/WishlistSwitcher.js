import React, {Component} from "react";

class WishlistSwitcher extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            productId: props.productId,
            customerId: 1,
            wishlistBtn: "",
            productOnWishlist: false
        }
        this.handleInputChange = this.handleInputChange.bind(this);
        this.switchWishlistState = this.switchWishlistState.bind(this);
        this.deleteWishlist = this.deleteWishlist.bind(this);
        this.addToWishlist = this.addToWishlist.bind(this);
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    deleteWishlist() {
        let url = "http://localhost:9000/api/wishlist/" + this.state.id;
        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'DELETE',
        }).then(result => {
            this.setState({
                productOnWishlist: false
            })
            this.forceUpdate()
        })
    }

    addToWishlist() {
        var data = {
            "id": this.state.id,
            "product": parseInt(this.state.productId),
            "customer": parseInt(this.state.customerId),
        };
        var url = 'http://localhost:9000/api/wishlist';

        fetch(url, {
            method: 'POST',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                productOnWishlist: true,
                id: responseData.wishlist.id
            })
            this.forceUpdate()
        });
    }

    switchWishlistState(event) {
        if (this.state.productOnWishlist) {
            this.deleteWishlist();
        } else {
            this.addToWishlist();
        }
    }

    getBtnText() {
        if (this.state.productOnWishlist) {
            return "Remove from wishlist"
        } else {
            return "Add to wishlist"
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/wishlists/customer/" + this.state.customerId;
        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            data.map(wishlistedProduct => {
                let product = wishlistedProduct[1];
                if (product.id == this.state.productId) {
                    this.setState({
                        id: wishlistedProduct[0].id,
                        productId: product.id,
                        productOnWishlist: true
                    })
                }
            })
        });
    }

    render() {
        return (
            <button type="button" className="btn btn-md btn-success mr-1" onClick={this.switchWishlistState}>
                {this.state.productOnWishlist ? "Remove from wishlist" : "Add to wishlist"}
            </button>
        )
    }
}

export default WishlistSwitcher;