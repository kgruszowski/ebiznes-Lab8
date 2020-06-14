import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class ProductEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            name: "",
            description: "",
            photo: "",
            price: "",
            category: "",
            productEdited: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleCategoryChange = this.handleCategoryChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "name": this.state.name,
            "description": this.state.description,
            "photo": this.state.photo,
            "price": parseFloat(this.state.price),
            "category": parseInt(this.state.category)
        };
        var url = 'http://localhost:9000/api/product/' + this.state.id;

        fetch(url, {
            method: 'PUT',
            headers: {},
            body: JSON.stringify(data),
        }).then(result => {
            this.setState({productEdited: true})
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

    async getCategories() {
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
                let categoryId = cat.id
                if (categoryId === this.state.category) {
                    return (
                        <option value={cat.id} name="category" key={cat.id} selected>{cat.name}</option>
                    )
                } else {
                    return (
                        <option value={cat.id} name="category" key={cat.id}>{cat.name}</option>
                    )
                }
            })
            this.setState({categories: categories})
        })
    }

    async getProduct() {
        let url = "http://localhost:9000/api/product/" + this.state.id;
        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            this.setState({
                id: data.product.id,
                name: data.product.name,
                description: data.product.description,
                photo: data.product.photo,
                price: data.product.price,
                category: data.product.category
            })
            let inputs = (
                <div>
                    <InputComponent label="Product name" name="name" value={this.state.name}
                                    onChangeCallback={this.handleInputChange}/>

                    <InputComponent label="Description" name="description" value={this.state.description}
                                    onChangeCallback={this.handleInputChange}/>

                    <InputComponent label="Photo url" name="photo" value={this.state.photo}
                                    onChangeCallback={this.handleInputChange}/>

                    <InputComponent label="Price" name="price" value={this.state.price}
                                    onChangeCallback={this.handleInputChange}/>
                </div>
            )
            this.setState({inputs: inputs})
        });
    }

    componentDidMount() {
        this.getProduct().then(this.getCategories());
    }

    render() {
        if (this.state.productEdited) {
            return <Redirect to={"/product/" + this.state.id}/>
        }
        return (
            <div>
                {this.state.inputs}
                <div className="form-group">
                    <label htmlFor="category">Category</label>
                    <select className="form-control" name="category" id="category" onChange={this.handleCategoryChange}>
                        <option value="" name="category">---Choose category---</option>
                        {this.state.categories}
                    </select>
                </div>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit product</button>
            </div>
        );
    }
}

export default ProductEdit;