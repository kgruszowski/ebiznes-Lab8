import React, {Component} from "react";
import {Link} from 'react-router-dom';

class ProductCard extends Component{
    render() {
        return (
            <div className="col-md-4">
                <div className="card mb-4 shadow-sm">
                    <img width="100%" height="250" src={this.props.product.photo} alt={this.props.product.name} />
                    <div className="card-body">
                        <h3>{this.props.product.name}</h3>
                        <span>{this.props.category.name}</span>
                        <p className="card-text">{this.props.product.description}</p>
                        <div className="d-flex justify-content-between align-items-center">
                            <div className="btn-group">
                                <Link to={"/product/" + this.props.product.id} type="button" className="btn btn-sm btn-outline-secondary">View</Link>
                                <Link to={"/product/edit/" + this.props.product.id} type="button" className="btn btn-sm btn-outline-secondary">Edit</Link>
                            </div>
                            <small className="text-muted">$ {parseFloat(this.props.product.price.toFixed(2))}</small>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default ProductCard