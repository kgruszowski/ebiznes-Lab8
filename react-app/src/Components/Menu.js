import React, {Component} from "react";
import {
    BrowserRouter as Router,
    Route,
    Link,
    Switch
} from 'react-router-dom';
import {CategoryList, CategoryDetails, CategoryAdd, CategoryEdit} from "./Category";
import {DiscountList, DiscountDetails, DiscountAdd, DiscountEdit} from './Discount'
import {ProductList, ProductDetails, ProductAdd, ProductEdit} from './Product'
import {ShippingList, ShippingDetails, ShippingAdd, ShippingEdit} from './Shipping'
import {InventoryAdd, InventoryEdit} from './Inventory'
import {CustomerList, CustomerDetails, CustomerAdd, CustomerEdit} from './Customer'
import {ReviewAdd, ReviewEdit} from "./Review";
import {WishlistSwitcher, WishlistList} from "./Wishlist";
import {CartDetails, CartIcon} from "./Cart";
import {OrderDetails, OrderList} from "./Order";

class Menu extends Component {
    linksMarkup = this.props.links.map((link, index) =>
        <li className="nav-item" key={link.label}>
            <Link className="nav-link" to={link.link}>{link.label}</Link>
        </li>
    );

    render() {
        return <Router>
            <nav className="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
                <div className="container">
                    <a className="navbar-brand" href="/">Home</a>
                    <button className="navbar-toggler" type="button" data-toggle="collapse"
                            data-target="#navbarsExampleDefault"
                            aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"/>
                    </button>

                    <div className="collapse navbar-collapse" id="navbarsExampleDefault">
                        <ul className="navbar-nav mr-auto">
                            {this.linksMarkup}
                        </ul>
                        <ul className="navbar-nav ml-auto">
                            <li className="nav-item pill-right" key='cart'>
                                <CartIcon />
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
            <div className="container">
                <h3>Sign in </h3>
                    <a className="btn btn-md btn-danger" href="http://localhost:9000/authenticate/google">Google</a>
                    <a className="btn btn-md btn-info" href="http://localhost:9000/authenticate/github">Github</a>
            </div>
            <Switch>
                <Route path="/products" component={ProductList}/>
                <Route path="/product/add" component={ProductAdd}/>
                <Route path="/product/edit/:id" component={ProductEdit}/>
                <Route path="/product/:id" component={ProductDetails}/>

                <Route path="/categories" component={CategoryList}/>
                <Route path="/category/add" component={CategoryAdd}/>
                <Route path="/category/edit/:id" component={CategoryEdit}/>
                <Route path="/category/:id" component={CategoryDetails}/>

                <Route path="/shippings" component={ShippingList}/>
                <Route path="/shipping/add" component={ShippingAdd}/>
                <Route path="/shipping/edit/:id" component={ShippingEdit}/>
                <Route path="/shipping/:id" component={ShippingDetails}/>

                <Route path="/discounts" component={DiscountList}/>
                <Route path="/discount/add" component={DiscountAdd}/>
                <Route path="/discount/edit/:id" component={DiscountEdit}/>
                <Route path="/discount/:id" component={DiscountDetails}/>

                <Route path="/inventory/add/product/:id" component={InventoryAdd}/>
                <Route path="/inventory/edit/product/:id" component={InventoryEdit}/>

                <Route path="/customers" component={CustomerList}/>
                <Route path="/customer/add" component={CustomerAdd}/>
                <Route path="/customer/edit/:id" component={CustomerEdit}/>
                <Route path="/customer/:id" component={CustomerDetails}/>

                <Route path="/review/add/product/:productId" component={ReviewAdd}/>
                <Route path="/review/edit/:id" component={ReviewEdit}/>

                <Route path="/wishlists" component={WishlistList}/>
                <Route path="/wishlist/add/product/:productId" component={WishlistSwitcher}/>

                <Route path="/cart" component={CartDetails}/>

                <Route path="/orders" component={OrderList} />
                <Route path="/order/:id" component={OrderDetails} />
            </Switch>
        </Router>
    }
}

export default Menu;