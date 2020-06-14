import React, {Component} from "react";
import {Link, Redirect} from "react-router-dom";


class CategoryList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            categories: [],
            unauthorizedError: false
        }
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
                    <li key={cat.id}>
                        <Link to={"/category/" + cat.id}>{cat.name}</Link>
                    </li>
                )
            })
            this.setState({categories: categories})
        }).catch(error => this.setState({unauthorizedError: true}))
    }

    render() {
        if (this.state.unauthorizedError) {
            return <Redirect to={"/sign-in"}/>
        }

        return (
            <div className="container">
                <div className="categories">
                    <h3>List of all categories</h3>
                    <ul>
                        {this.state.categories}
                    </ul>
                </div>
                <Link to="/category/add" className="btn btn-md btn-success">Add new category</Link>
            </div>
        )
    }
}

export default CategoryList;