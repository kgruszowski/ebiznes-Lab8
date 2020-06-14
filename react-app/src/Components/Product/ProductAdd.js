import React, {Component} from "react";
import {Redirect} from 'react-router-dom'
import InputComponent from "../Common/InputComponent";

class ProductAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            name: "",
            description: "",
            photo: "",
            price: "",
            category: "",
            createdProductId: null
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleCategoryChange = this.handleCategoryChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": 0,
            "name": this.state.name,
            "description": this.state.description,
            "photo": this.state.photo,
            "price": parseFloat(this.state.price),
            "category": parseInt(this.state.category)
        };
        var url = 'http://localhost:9000/api/product';

        fetch(url, {
            method: 'POST',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({createdProductId: responseData.product.id})
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    handleCategoryChange(event) {
        this.setState({
            category: event.target.value
        });
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/categories"

        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let categories = data.map((cat) => {
                return (
                    <option value={cat.id} name="category">{cat.name}</option>
                )
            })
            this.setState({categories: categories})
        })
    }

    render() {
        if (this.state.createdProductId !== null) {
            return <Redirect to={"/product/" + this.state.createdProductId}/>
        }

        return (
            <div>
                <InputComponent label="Product name" name="name" value={this.state.name}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Description" name="description" value={this.state.description}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Photo url" name="photo" value={this.state.photo}
                                onChangeCallback={this.handleInputChange}/>

                <InputComponent label="Price" name="price" value={this.state.price}
                                onChangeCallback={this.handleInputChange}/>

                <div className="form-group">
                    <label htmlFor="category">Category</label>
                    <select className="form-control" name="category" id="category" onChange={this.handleCategoryChange}>
                        <option value="" name="category">---Choose category---</option>
                        {this.state.categories}
                    </select>
                </div>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Add product</button>
            </div>
        );
    }
}

export default ProductAdd;