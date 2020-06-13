import React, {Component} from "react";
import ProductCard from "./ProductCard";
import {Link} from "react-router-dom";

class ProductList extends Component {

    constructor() {
        super();
        this.state = {
            products: [],
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/products"

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
            }).then(data => {
            let products = data.map((prod) => {
                return (
                    <ProductCard key={prod[0].id} category={prod[1]} product={prod[0]}/>
                )
            })
            this.setState({products: products})
        })
    }

    render() {
        return (
            <div className="container">
                <div className="row">
                    <Link to={"/product/add"} type="button" className="btn btn-sm btn-success">Add product</Link>
                </div>
                <div className="py-5 bg-light row">
                    <div className="container">
                        <div className="row">
                            {this.state.products}
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default ProductList;