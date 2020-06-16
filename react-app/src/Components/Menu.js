import React, {Component} from "react";
import {
    Link,
} from 'react-router-dom';
import {CartIcon} from "./Cart";

class Menu extends Component {
    linksMarkup = this.props.links.map((link, index) =>
        <li className="nav-item" key={link.label}>
            <Link className="nav-link" to={link.link}>{link.label}</Link>
        </li>
    );

    render() {
        return (
            <nav className="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
                <div className="container">
                    <div className="collapse navbar-collapse" id="navbarsExampleDefault">
                        <ul className="navbar-nav mr-auto">
                            {this.linksMarkup}
                        </ul>
                        <ul className="navbar-nav ml-auto">
                            <li className="nav-item" key='cart'>
                                <CartIcon/>
                            </li>
                            <li className="nav-item" key="Sign Out">
                                <Link className="nav-link" to="/sign-out">Sign Out</Link>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        )
    }
}

export default Menu;