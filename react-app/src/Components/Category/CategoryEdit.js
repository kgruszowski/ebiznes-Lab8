import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class CategoryEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            name: "",
            categoryEdited: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleSubmit(event) {
        var data = {
            "id": this.state.id,
            "name": this.state.name,
        };
        var url = 'http://localhost:9000/api/category/' + this.state.id;

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

    async getCategory()
    {
        let url = "http://localhost:9000/api/category/" + this.state.id;
        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
                this.setState({
                    id : data.category.id,
                    name : data.category.name,
                })
                let inputs = <InputComponent label="Category name" name="name" value={this.state.name}
                                             onChangeCallback={this.handleInputChange} />
                this.setState({inputs: inputs})
        });
    }

    componentDidMount() {
        this.getCategory();
    }

    render() {
        if (this.state.productEdited) {
            return <Redirect to={"/category/" + this.state.id}/>
        }
        return (
            <div>
                {this.state.inputs}
                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit category</button>
            </div>
        );
    }
}

export default CategoryEdit;