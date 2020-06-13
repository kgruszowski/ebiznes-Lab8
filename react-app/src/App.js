import React, {Component} from 'react';
import './App.css';
import Menu from "./Components/Menu";

class App extends Component {
    render() {
        let links = [
            {label: "Product", link: "/products"},
            {label: "Category", link: "/categories"},
            {label: "Shipping", link: "/shippings"},
            {label: "Discount", link: "/discounts"},
            {label: "Customer", link: "/customers"},
            {label: "My wishlist", link: "/wishlists"},
            {label: "My Orders", link: "/orders"}
        ]
        return (
            <div className="container">
                <Menu links={links}/>
            </div>
        );
    }
}

export default App;
