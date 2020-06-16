import React, {Component} from 'react';
import './App.css';
import Menu from "./Components/Menu";
import unregister from "./interceptor"
import {ProductAdd, ProductDetails, ProductEdit, ProductList} from "./Components/Product";
import {CategoryAdd, CategoryDetails, CategoryEdit, CategoryList} from "./Components/Category";
import {ShippingAdd, ShippingDetails, ShippingEdit, ShippingList} from "./Components/Shipping";
import {DiscountAdd, DiscountDetails, DiscountEdit, DiscountList} from "./Components/Discount";
import {InventoryAdd, InventoryEdit} from "./Components/Inventory";
import {CustomerAdd, CustomerDetails, CustomerEdit, CustomerList} from "./Components/Customer";
import {ReviewAdd, ReviewEdit} from "./Components/Review";
import {WishlistList, WishlistSwitcher} from "./Components/Wishlist";
import {CartDetails} from "./Components/Cart";
import {OrderDetails, OrderList} from "./Components/Order";
import {SocialSignIn, SocialSignOut} from "./Components/User";
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import Home from "./Components/Home";

class App extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        let links = [
            {label: "Product", link: "/products"},
            {label: "Category", link: "/categories"},
            {label: "Shipping", link: "/shippings"},
            {label: "Discount", link: "/discounts"},
            {label: "Customer", link: "/customer"},
            {label: "My wishlist", link: "/wishlists"},
            {label: "My Orders", link: "/orders"}
        ]
        return <Router>
            <div className="container">
                <Menu links={links}/>
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
                    <Route path="/customer/:id?" component={CustomerDetails}/>

                    <Route path="/review/add/product/:productId" component={ReviewAdd}/>
                    <Route path="/review/edit/:id" component={ReviewEdit}/>

                    <Route path="/wishlists" component={WishlistList}/>
                    <Route path="/wishlist/add/product/:productId" component={WishlistSwitcher}/>

                    <Route path="/cart" component={CartDetails}/>

                    <Route path="/orders" component={OrderList}/>
                    <Route path="/order/:id" component={OrderDetails}/>

                    <Route path="/sign-in" component={SocialSignIn}/>
                    <Route path="/sign-out" component={SocialSignOut}/>

                    <Route path="/" component={Home} />
                </Switch>

            </div>
        </Router>
    }
}

export default App;
