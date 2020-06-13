import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class CategoryAdd extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: 0,
            name: "",
            categoryCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "name": this.state.name,
        };
        var url = 'http://localhost:9000/api/category';

        fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                categoryCreated: true,
                id: responseData.category.id
            })
        });
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    render() {
        if (this.state.categoryCreated) {
            return <Redirect to={"/category/" + this.state.id}/>
        }
        return (
            <div>
                <InputComponent label="Category name" name="name" value={this.state.name}
                                onChangeCallback={this.handleInputChange}/>

                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Add category</button>
            </div>
        );
    }
}

export default CategoryAdd;