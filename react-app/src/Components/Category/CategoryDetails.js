import React, {Component} from "react";
import {Link} from "react-router-dom";

class CategoryDetails extends Component {
    constructor(props) {
        super(props);
        this.state = {
            category: [],
            categoryId: props.match.params.id,
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/category/" + this.state.categoryId;

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
            let categoryDetails = (
                <div>
                    <div className="col-md-7">
                        <div>Id: {data.category.id}</div>
                        <div>Name: {data.category.name}</div>
                    </div>
                    <hr></hr>
                    <div className="btn-group wishlist">
                        <Link to={"/category/edit/" + this.state.categoryId}
                              className="btn btn-sm btn-success">Edit</Link>
                    </div>
                </div>
            )
            this.setState({category: categoryDetails})
        })
    }

    render() {
        return (
            <div className="item-container">
                <div className="container">
                    {this.state.category}
                </div>
            </div>

        )
    }
}

export default CategoryDetails;